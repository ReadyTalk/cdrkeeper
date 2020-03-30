package com.ecovate.cdrkeeper;

import java.io.IOException;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Collections;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.threadly.concurrent.PriorityScheduler;
import org.threadly.concurrent.TaskPriority;
import org.threadly.concurrent.ThreadReferencingThreadFactory;
import org.threadly.db.aurora.Driver;
import org.threadly.litesockets.ThreadedSocketExecuter;
import org.threadly.litesockets.buffers.ReuseableMergedByteBuffers;
import org.threadly.litesockets.protocols.http.request.HTTPRequest;
import org.threadly.litesockets.protocols.http.response.HTTPResponse;
import org.threadly.litesockets.protocols.http.response.HTTPResponseBuilder;
import org.threadly.litesockets.protocols.http.shared.HTTPConstants;
import org.threadly.litesockets.protocols.http.shared.HTTPRequestMethod;
import org.threadly.litesockets.protocols.http.shared.HTTPResponseCode;
import org.threadly.litesockets.protocols.websocket.WSFrame;
import org.threadly.litesockets.server.http.HTTPServer;
import org.threadly.litesockets.server.http.HTTPServer.BodyFuture;
import org.threadly.litesockets.server.http.HTTPServer.BodyListener;
import org.threadly.litesockets.server.http.HTTPServer.ResponseWriter;
import org.threadly.litesockets.utils.IOUtils;
import org.threadly.util.AbstractService;
import org.threadly.util.ExceptionUtils;

import com.ecovate.cdrkeeper.cdr.CDRObject;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import io.prometheus.client.Histogram.Timer;
import io.prometheus.client.exporter.common.TextFormat;
import io.prometheus.client.hotspot.DefaultExports;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class CDRKeeper extends AbstractService {

  private static final HTTPResponse BadRequestResponse = new HTTPResponseBuilder()
      .setResponseCode(HTTPResponseCode.BadRequest)
      .setHeader(HTTPConstants.HTTP_KEY_CONNECTION, "close")
      .build();
  private static final HTTPResponse GoodRequestResponse = new HTTPResponseBuilder()
      .setResponseCode(HTTPResponseCode.OK)
      .setHeader(HTTPConstants.HTTP_KEY_CONNECTION, "close")
      .build();

  private static final Logger log = LoggerFactory.getLogger(CDRKeeper.class);
  private final ThreadReferencingThreadFactory trf = new ThreadReferencingThreadFactory("TP", true, true,Thread.currentThread().getPriority(), Constants.UEH , ExceptionUtils.getExceptionHandler());
  private final PriorityScheduler PS = new PriorityScheduler(2, TaskPriority.High, 500, trf);
  private final ThreadedSocketExecuter TSE = new ThreadedSocketExecuter(PS);
  private final HTTPServer httpServer;
  private final HikariDataSource ds;
  private final Jdbi jdbi;
  private final String user;
  private final String password;
  private final String base64;
  
  private final Histogram httpRequestLatency = Histogram.build()
      .name("http_requests_latency_seconds")
      .help("HTTP Request latency in seconds.")
      .register();
  
  private final Counter requestCounter = Counter.build()
      .name("http_requests_total")
      .help("http request counter")
      .labelNames("type")
      .register();
  private final Counter responseCounter = Counter.build()
      .name("http_response_total")
      .help("http response counter")
      .labelNames("type")
      .register();
  
  private final Counter cdrsProcessed = Counter.build()
      .name("cdrs_processed_total")
      .help("cdrs_processed_total")
      .register();
  
  private final Counter cdrsBytes = Counter.build()
      .name("cdrs_processed_bytes")
      .help("cdrs_processed_bytes")
      .register();
  
  private final Histogram cdrsProcessedTime = Histogram.build()
      .name("cdrs_process_time_seconds")
      .help("cdrs_process_time_seconds")
      .register();
  
  private final Histogram callTime = Histogram.build()
      .buckets(60, 300, 600, 1200, 1800, 3600, 7200, 14400, 28800, 43200, 86400)
      .name("call_time_seconds")
      .help("call_time_seconds")
      .labelNames("cluster")
      .register();
  
  private final Counter codecs = Counter.build()
      .name("call_codecs")
      .help("call_codecs")
      .labelNames("cluster", "codec")
      .register();
  
  private final Histogram callMOS = Histogram.build()
      .name("call_mos")
      .help("call_mos")
      .labelNames("cluster", "direction")
      .register();
  
  CDRKeeper(InetSocketAddress listener, HikariConfig hc, String user, String password) throws IOException {
    TSE.startIfNotStarted();
    this.user = user;
    this.password = password;
    this.base64 = Base64.getEncoder().encodeToString((this.user+":"+this.password).getBytes());
    httpServer = new HTTPServer(TSE, listener.getAddress().getHostAddress(), listener.getPort());
    httpServer.setHandler((h,r,b)->handle(h,r,b));
    ds = new HikariDataSource(hc);
    jdbi = Jdbi.create(ds);
    try (Handle h = jdbi.open()) {
      int count = h.execute(Constants.CREATE_CDR_RAW_TABLE);
      if(count == 1) {
        log.info("Created CDR_RAW Table");
      }
      count = h.execute(Constants.CREATE_CDR_CALL_TABLE);
      if(count == 1) {
        log.info("Created CDR_CALL Table");
      }
    }
  }
  
  private void handle(HTTPRequest hr, ResponseWriter rw, BodyFuture bl) {
    rw.closeOnDone();
    Timer t = httpRequestLatency.startTimer();
    HTTPRequestMethod rm = HTTPRequestMethod.valueOf(hr.getHTTPRequestHeader().getRequestMethod());
    requestCounter.labels(rm.toString()).inc();
    String auth = hr.getHTTPHeaders().getHeader(HTTPConstants.HTTP_KEY_AUTHORIZATION);
    String b64 = "";
    if(auth != null && auth.startsWith("Basic ")) {
      b64 = auth.substring(6);
    }
    String path = hr.getHTTPRequestHeader().getRequestPath();
    if(rm == HTTPRequestMethod.POST && path.startsWith("/cdr") && b64.equals(this.base64)) {
      bl.setBodyListener(new BodyListener() {
        ReuseableMergedByteBuffers mbb = new ReuseableMergedByteBuffers(false);
        @Override
        public void onBody(HTTPRequest httpRequest, ByteBuffer bb, ResponseWriter responseWriter) {
          mbb.add(bb);
        }

        @Override
        public void onWebsocketFrame(HTTPRequest httpRequest, WSFrame wsf, ByteBuffer bb,
            ResponseWriter responseWriter) {
          responseCounter.labels(Integer.toString(BadRequestResponse.getResponseCode().getId())).inc();
          rw.sendHTTPResponse(BadRequestResponse);
          rw.done();
        }

        @Override
        public void bodyComplete(HTTPRequest httpRequest, ResponseWriter responseWriter) {
          Timer t = cdrsProcessedTime.startTimer();
          String cluster = httpRequest.getHTTPRequestHeader().getRequestQueryValue("cluster");
          if(cluster == null || cluster.equals("")) {
            cluster = "unknown";
          }
          try {
            int size = mbb.remaining();
            String json = mbb.getAsString(mbb.remaining());
            CDRObject cdr = CDRObject.GSON.fromJson(json, CDRObject.class);
            callTime.labels(cluster).observe(cdr.getCallDuration());
            codecs.labels(cluster, cdr.getVariables().getWrite_codec()).inc();
            callMOS.labels(cluster, cdr.getVariables().getDirection()).observe(cdr.getMOS());
            if(cdr.getVariables() == null || cdr.getVariables().getCall_uuid() == null) {
              log.error("Bad request json:{}", json);
              responseCounter.labels(Integer.toString(BadRequestResponse.getResponseCode().getId())).inc();
              rw.sendHTTPResponse(BadRequestResponse);
              return;
            }
            log.info("Got uuid:{}, callid:{} for server:{}", cdr.getVariables().getUuid(), cdr.getVariables().getCall_uuid(), cdr.getCoreuuid());
            DBUtil.writeRawJson(jdbi, cdr, json);
            DBUtil.writeParsedJson(jdbi, cdr, json);
            cdrsProcessed.inc();
            cdrsBytes.inc(size);
            responseCounter.labels(Integer.toString(GoodRequestResponse.getResponseCode().getId())).inc();
            rw.sendHTTPResponse(GoodRequestResponse);
          } catch(Exception e) {
            log.error("Error parsing request",e);
            responseCounter.labels(Integer.toString(BadRequestResponse.getResponseCode().getId())).inc();
            rw.sendHTTPResponse(BadRequestResponse);
          } finally {
            rw.done();  
            t.close();
          }
        }
       });
    } else if(rm == HTTPRequestMethod.GET && path.startsWith("/metrics")) {
      metricsResponse(hr,rw,bl);
      rw.done();
    } else {
      log.info("Got unknown request:{}\n", hr.toString());
      responseCounter.labels(Integer.toString(BadRequestResponse.getResponseCode().getId())).inc();
      rw.sendHTTPResponse(BadRequestResponse);
      rw.done();
    }
    t.close();
  }

  @Override
  protected void startupService() {
    httpServer.start();
  }

  @Override
  protected void shutdownService() {
    httpServer.stop();
    TSE.stop();
    PS.shutdownNow();
  }
    
  public void metricsResponse(HTTPRequest httpRequest, ResponseWriter rw, BodyFuture bodyListener) {
    rw.closeOnDone();
    String metrics = "";
    StringWriter writer = new StringWriter();
    try {
      TextFormat.write004(writer, CollectorRegistry.defaultRegistry.filteredMetricFamilySamples(Collections.emptySet()));
      metrics = writer.toString();
      HTTPResponse hr = new HTTPResponseBuilder()
          .setResponseCode(HTTPResponseCode.OK)
          .setHeader(HTTPConstants.HTTP_KEY_CONTENT_LENGTH, Integer.toString(metrics.length()))
          .setHeader(HTTPConstants.HTTP_KEY_CONTENT_TYPE, "text/plain")
          .setHeader(HTTPConstants.HTTP_KEY_CONNECTION, "close")
          .build();
      rw.sendHTTPResponse(hr);
      responseCounter.labels(Integer.toString(hr.getResponseCode().getId())).inc();
      rw.writeBody(ByteBuffer.wrap(metrics.getBytes()));
      rw.done();
    } catch (Exception e) {
      log.error("Error parsing metrics!", e);
      HTTPResponse hr = new HTTPResponseBuilder()
          .setResponseCode(HTTPResponseCode.InternalServerError)
          .setHeader(HTTPConstants.HTTP_KEY_CONTENT_LENGTH, "0")
          .setHeader(HTTPConstants.HTTP_KEY_CONNECTION, "close")
          .build();
      rw.sendHTTPResponse(hr);
      responseCounter.labels(Integer.toString(hr.getResponseCode().getId())).inc();
      rw.done();
    } finally {
      IOUtils.closeQuietly(writer);
    }
  }


  public static void main(String[] args) throws Exception {
    System.setProperty(org.slf4j.impl.SimpleLogger.SHOW_DATE_TIME_KEY, "true");
    System.setProperty(org.slf4j.impl.SimpleLogger.DATE_TIME_FORMAT_KEY, "yyyy-MM-dd HH:mm:ss.SSSZ");
    LoggingConfig.configureLogging();
    Logger log = LoggerFactory.getLogger("Main");
    log.info("logging configured!");
    Driver.registerDriver();
    DefaultExports.initialize();

    
    Integer env_listenport = null;
    if(System.getenv("CDR_LISTEN_PORT") != null) {
      try {
        env_listenport = Integer.parseInt(System.getenv("CDR_LISTEN_PORT"));
      }catch(Exception e) {}
    }

    String env_dbserver = System.getenv("CDR_DB_SERVER");
    String env_dbname = System.getenv("CDR_DB_NAME");
    String env_dbuser = System.getenv("CDR_DB_USER");
    String env_dbpassword = System.getenv("CDR_DB_PASSWORD");
    String env_http_user = System.getenv("CDR_HTTP_USER");
    String env_http_password = System.getenv("CDR_HTTP_PASSWORD");
    Integer env_dbport = null;
    if(System.getenv("CDR_DB_PORT") != null) {
      try {
        env_dbport = Integer.parseInt(System.getenv("CDR_DB_PORT"));
      }catch(Exception e) {}
    }
    
    ArgumentParser parser = ArgumentParsers.newFor("CDRKeeper").build()
        .defaultHelp(true)
        .description("Takes CDR reports from freeswitch and logs them into a database.");
    Argument arg_listenport = parser.addArgument("--listen_port")
        .type(Integer.class)
        .setDefault(8080)
        .help("Default port to listen on (Default 8080)");
    Argument arg_dbserver = parser.addArgument("--dbserver")
        .required(true)
        .help("Database Server to connect to");
    Argument arg_dbport = parser.addArgument("--dbport")
        .type(Integer.class)
        .setDefault(3306)
        .help("Database port (default 3306)");
    Argument arg_dbname = parser.addArgument("--dbname")
        .required(true)
        .help("Database Server to connect to");
    Argument arg_dbuser = parser.addArgument("--dbuser")
        .required(true)
        .help("Database user");
    Argument arg_dbpassword = parser.addArgument("--dbpassword")
        .required(true)
        .help("Database password");
    Argument arg_http_user = parser.addArgument("--http_user")
        .required(true)
        .help("HTTP user required for posting json cdr data");
    Argument arg_http_password = parser.addArgument("--http_password")
        .required(true)
        .help("HTTP users password");
    
    if(env_listenport != null) {
      arg_listenport.setDefault(env_listenport);
    }
    if(env_dbserver != null) {
      arg_dbserver.required(false);
      arg_dbserver.setDefault(env_dbserver);
    }
    if(env_dbport != null) {
      arg_dbport.setDefault(env_dbport);
    }
    if(env_dbname != null) {
      arg_dbname.required(false);
      arg_dbname.setDefault(env_dbname);
    }
    if(env_dbuser != null) {
      arg_dbuser.required(false);
      arg_dbuser.setDefault(env_dbuser);
    }
    if(env_dbpassword != null) {
      arg_dbpassword.required(false);
      arg_dbpassword.setDefault(env_dbpassword);
    }
    if(env_http_user != null) {
      arg_http_user.required(false);
      arg_http_user.setDefault(env_http_user);
    }
    if(env_http_password != null) {
      arg_http_password.required(false);
      arg_http_password.setDefault(env_http_password);
    }
    
    Namespace res = null;
    try {
      res = parser.parseArgs(args);
    } catch (ArgumentParserException e) {
      parser.handleError(e);
      System.exit(1);
    }
    
    final String dbServer = res.getString("dbserver");
    final int dbPort = res.getInt("dbport");
    final String dbName = res.getString("dbname");
    final String dbUser = res.getString("dbuser");
    final String dbPassword = res.getString("dbpassword");
    final int serverPort = res.getInt("listen_port");

    final String httpUser = res.getString("http_user");
    final String httpPassword = res.getString("http_password");
    
    HikariConfig hc = new HikariConfig();
    hc.setJdbcUrl("jdbc:mysql:aurora://"+dbServer+":"+dbPort+"/"+dbName+"?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&verifyServerCertificate=false&useSSL=true");
    hc.setMaximumPoolSize(3);
    hc.setUsername(dbUser);
    hc.setPassword(dbPassword);

    CDRKeeper cdrk = new CDRKeeper(new InetSocketAddress("0.0.0.0", serverPort), hc, httpUser, httpPassword);
    
    cdrk.start();
    while(cdrk.isRunning()) {
      Thread.sleep(10000);
    }
  }
}


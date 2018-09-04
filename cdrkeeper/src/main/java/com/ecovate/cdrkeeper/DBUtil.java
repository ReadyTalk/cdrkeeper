package com.ecovate.cdrkeeper;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;

import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.jdbi.v3.core.statement.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ecovate.cdrkeeper.cdr.CDRObject;

public class DBUtil {
  private static final Logger log = LoggerFactory.getLogger(DBUtil.class);

  public static void writeParsedJson(Jdbi db, CDRObject cdr, String json) {
    try (Handle h = db.open()) {
      int entry = 0;
      Update u = h.createUpdate("insert into "+
          "cdr_call(core_uuid, call_uuid, uuid, start_time, end_time, direction, sip_call_id, write_codec, read_codec,"+
          "hangup_cause, remote_media_ip, local_media_ip,"+
          "audio_inbound_media_packet_count, audio_inbound_skip_packet_count, audio_inbound_quality_percentage, audio_inbound_mos,"+
          "audio_outbound_media_packet_count, audio_outbound_skip_packet_count)"+
          " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)")
          .bind(entry++, cdr.getCoreuuid())
          .bind(entry++, cdr.getVariables().getCall_uuid())
          .bind(entry++, cdr.getVariables().getUuid())
          .bind(entry++, new Date(Long.parseLong(cdr.getVariables().getStart_epoch())*1000))
          .bind(entry++, new Date(Long.parseLong(cdr.getVariables().getEnd_epoch())*1000))

          .bind(entry++, cdr.getVariables().getDirection())
          .bind(entry++, cdr.getVariables().getSip_call_id())
          .bind(entry++, cdr.getVariables().getWrite_codec())
          .bind(entry++, cdr.getVariables().getRead_codec())

          .bind(entry++, cdr.getVariables().getHangup_cause())
          .bind(entry++, cdr.getVariables().getRemote_media_ip())
          .bind(entry++, cdr.getVariables().getAdvertised_media_ip());
      if(cdr.getCallStats().getAudio() != null && cdr.getCallStats().getAudio().getInbound() != null) {
        u.bind(entry++, cdr.getCallStats().getAudio().getInbound().getMedia_packet_count());
        u.bind(entry++, cdr.getCallStats().getAudio().getInbound().getSkip_packet_count());
        u.bind(entry++, cdr.getCallStats().getAudio().getInbound().getQuality_percentage());
        u.bind(entry++, cdr.getCallStats().getAudio().getInbound().getMos());
      } else {
        u.bind(entry++, 0);
        u.bind(entry++, 0);
        u.bind(entry++, 0.0);
        u.bind(entry++, 0.0);            
      }
      if(cdr.getCallStats().getAudio() != null && cdr.getCallStats().getAudio().getOutbound() != null) {
        u.bind(entry++, cdr.getCallStats().getAudio().getOutbound().getMedia_packet_count());
        u.bind(entry++, cdr.getCallStats().getAudio().getOutbound().getSkip_packet_count());
      } else {
        u.bind(entry++, 0);
        u.bind(entry++, 0);            
      }

      u.execute();
    } catch(UnableToExecuteStatementException e) {
      if(e.getCause() instanceof SQLIntegrityConstraintViolationException) {
        log.error("Duplicate key, can not insert into CDR_CALLS!");
        log.error("core_uuid:{}, call_uuid:{}, uuid:{}", cdr.getCoreuuid(), cdr.getVariables().getCall_uuid(), cdr.getVariables().getUuid());
        log.error("",e);
      } else {
        log.error("",e);
      }
      log.error("{}", json);
    }
  }

  public static void writeRawJson(Jdbi db, CDRObject cdr, String json) {
    try (Handle h = db.open()) {
      int entry = 0;
      Update u = h.createUpdate("insert into cdr_raw(core_uuid, call_uuid, uuid, rawdata) VALUES(?,?,?,?)")
          .bind(entry++, cdr.getCoreuuid())
          .bind(entry++, cdr.getVariables().getCall_uuid())
          .bind(entry++, cdr.getVariables().getUuid())
          .bind(entry++, json);
      u.execute();
    } catch(UnableToExecuteStatementException e) {
      if(e.getCause() instanceof SQLIntegrityConstraintViolationException) {
        log.error("Duplicate key, can not insert into CDR_RAW");
        log.error("core_uuid:{}, call_uuid:{}, uuid:{}", cdr.getCoreuuid(), cdr.getVariables().getCall_uuid(), cdr.getVariables().getUuid());
      } else {
        log.error("",e);
      }
      log.error("{}", json);
    }
  }
}

package com.ecovate.cdrkeeper.cdr;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;


public class CDRObject {
  public static final Gson GSON = new GsonBuilder().create();
  public static final Gson GSON_PRETTY = new GsonBuilder().setPrettyPrinting().create();

  @SerializedName("core-uuid")
  private final String coreuuid;
  private final String switchname;
  private final ChannelData channel_data;
  private final CallStats callStats;
  private final Variables variables;
  private final AppLog app_log;
  private final List<CallFlow> callflow;

  private transient String json = null;

  public CDRObject(String coreuuid, String switchname, ChannelData channel_data, CallStats callStats,
      Variables variables, AppLog app_log, List<CallFlow> callflow) {
    this.coreuuid = coreuuid;
    this.switchname = switchname;
    this.channel_data = channel_data;
    this.callStats = callStats;
    this.variables = variables;
    this.app_log = app_log;
    this.callflow = new ArrayList<>(callflow);
  }

  public String getCoreuuid() {
    return coreuuid;
  }

  public String getSwitchname() {
    return switchname;
  }

  public ChannelData getChannel_data() {
    return channel_data;
  }

  public CallStats getCallStats() {
    return callStats;
  }

  public Variables getVariables() {
    return variables;
  }

  public AppLog getApp_log() {
    return app_log;
  }

  public List<CallFlow> getCallflow() {
    return Collections.unmodifiableList(callflow);
  }

  public String getJson() {
    return toString();
  }

  public String toString() {
    if(json == null) {
      json = GSON.toJson(this);
    }
    return json;
  }

  public int hashCode() {
    return toString().hashCode();
  }

  public boolean equals(Object o) {
    if(o == this) {
      return true;
    }
    if(o instanceof CDRObject) {
      if(o.hashCode() == this.hashCode()) {
        if(o.toString().equals(this.toString())) {
          return true;
        }
      }
    }
    return false;
  }

  public static void main(String[] args) throws Exception {
    RandomAccessFile raf = new RandomAccessFile("/tmp/test.json", "r");
    byte[] ba = new byte[(int)raf.length()];
    raf.read(ba);
    raf.close();
    String s = new String(ba);
    Gson g = new GsonBuilder().setPrettyPrinting().create();
    CDRObject c = g.fromJson(s, CDRObject.class);
    System.out.println(g.toJson(c));
    System.out.println(c.getCallStats().audio.getInbound().toString());
    System.out.println(c.getCallStats().audio.getOutbound().toString());
  }
}

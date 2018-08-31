package com.ecovate.cdrkeeper.cdr;

public class CallFlowCallerProfile {
  private final String username;
  private final String dialplan;
  private final String caller_id_name;
  private final String ani;
  private final String aniii;
  private final String caller_id_number;
  private final String network_addr;
  private final String rdnis;
  private final String destination_number;
  private final String uuid;
  private final String source;
  private final String context;
  private final String chan_name;
  
  private transient String json = null;
  
  public CallFlowCallerProfile(String username, String dialplan, String caller_id_name, String ani, String aniii,
      String caller_id_number, String network_addr, String rdnis, String destination_number, String uuid, String source,
      String context, String chan_name) {
    super();
    this.username = username;
    this.dialplan = dialplan;
    this.caller_id_name = caller_id_name;
    this.ani = ani;
    this.aniii = aniii;
    this.caller_id_number = caller_id_number;
    this.network_addr = network_addr;
    this.rdnis = rdnis;
    this.destination_number = destination_number;
    this.uuid = uuid;
    this.source = source;
    this.context = context;
    this.chan_name = chan_name;
  }
  
  public String getUsername() {
    return username;
  }

  public String getDialplan() {
    return dialplan;
  }

  public String getCaller_id_name() {
    return caller_id_name;
  }

  public String getAni() {
    return ani;
  }

  public String getAniii() {
    return aniii;
  }

  public String getCaller_id_number() {
    return caller_id_number;
  }

  public String getNetwork_addr() {
    return network_addr;
  }

  public String getRdnis() {
    return rdnis;
  }

  public String getDestination_number() {
    return destination_number;
  }

  public String getUuid() {
    return uuid;
  }

  public String getSource() {
    return source;
  }

  public String getContext() {
    return context;
  }

  public String getChan_name() {
    return chan_name;
  }

  public String getJson() {
    return toString();
  }

  public String toString() {
    if(json == null) {
      json = CDRObject.GSON.toJson(this);
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
    if(o instanceof CallFlowCallerProfile) {
      if(o.hashCode() == this.hashCode()) {
        if(o.toString().equals(this.toString())) {
          return true;
        }
      }
    }
    return false;
  }
}
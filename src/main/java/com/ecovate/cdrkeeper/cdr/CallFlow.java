package com.ecovate.cdrkeeper.cdr;

public class CallFlow {
  private final String dialplan;
  private final String profile_index;
  private final CallFlowExtension extension;
  private final CallFlowCallerProfile caller_profile;
  private final CallFlowTimes times;
  
  private transient String json = null;
  
  public CallFlow(String dialplan, String profile_index, CallFlowExtension extension,
      CallFlowCallerProfile caller_profile, CallFlowTimes times) {
    this.dialplan = dialplan;
    this.profile_index = profile_index;
    this.extension = extension;
    this.caller_profile = caller_profile;
    this.times = times;
  }

  public String getDialplan() {
    return dialplan;
  }

  public String getProfile_index() {
    return profile_index;
  }

  public CallFlowExtension getExtension() {
    return extension;
  }

  public CallFlowCallerProfile getCaller_profile() {
    return caller_profile;
  }

  public CallFlowTimes getTimes() {
    return times;
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
    if(o instanceof CallFlow) {
      if(o.hashCode() == this.hashCode()) {
        if(o.toString().equals(this.toString())) {
          return true;
        }
      }
    }
    return false;
  }
}
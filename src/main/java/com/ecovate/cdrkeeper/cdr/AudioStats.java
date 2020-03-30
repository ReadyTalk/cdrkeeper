package com.ecovate.cdrkeeper.cdr;

public class AudioStats {
  private final AudioStatsInbound inbound;
  private final AudioStatsOutbound outbound;
  
  private transient String json = null;
  
  public AudioStats(AudioStatsInbound inbound, AudioStatsOutbound outbound) {
    this.inbound = inbound;
    this.outbound = outbound;
  }

  public AudioStatsInbound getInbound() {
    return inbound;
  }

  public AudioStatsOutbound getOutbound() {
    return outbound;
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
    if(o instanceof AudioStats) {
      if(o.hashCode() == this.hashCode()) {
        if(o.toString().equals(this.toString())) {
          return true;
        }
      }
    }
    return false;
  }
}
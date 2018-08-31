package com.ecovate.cdrkeeper.cdr;

public class InboundAudioErrorLog {
  private final Long start;
  private final Long stop;
  private final Long flaws;
  private final Long consecutiveFlaws;
  private final Long durationMS;

  private transient String json = null;

  public InboundAudioErrorLog(Long start, Long stop, Long flaws, Long consecutiveFlaws, Long durationMS) {
    this.start = start;
    this.stop = stop;
    this.flaws = flaws;
    this.consecutiveFlaws = consecutiveFlaws;
    this.durationMS = durationMS;
  }
  public Long getStart() {
    return start;
  }
  public Long getStop() {
    return stop;
  }
  public Long getFlaws() {
    return flaws;
  }
  public Long getConsecutiveFlaws() {
    return consecutiveFlaws;
  }
  public Long getDurationMS() {
    return durationMS;
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
    if(o instanceof InboundAudioErrorLog) {
      if(o.hashCode() == this.hashCode()) {
        if(o.toString().equals(this.toString())) {
          return true;
        }
      }
    }
    return false;
  }
}
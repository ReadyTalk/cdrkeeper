package com.ecovate.cdrkeeper.cdr;

public class CallStats {
  AudioStats audio;
  
  private transient String json = null;
  
  public CallStats(AudioStats audio) {
    this.audio = audio;
  }
  
  public AudioStats getAudio() {
    return audio;
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
    if(o instanceof CallStats) {
      if(o.hashCode() == this.hashCode()) {
        if(o.toString().equals(this.toString())) {
          return true;
        }
      }
    }
    return false;
  }
}
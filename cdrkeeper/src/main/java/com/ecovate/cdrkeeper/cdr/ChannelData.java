package com.ecovate.cdrkeeper.cdr;

public class ChannelData {
  private final String state;
  private final String direction;
  private final String state_number;
  private final String flags;
  private final String caps;
  
  private transient String json = null;
  
  public ChannelData(String state, String direction, String state_number, String flags, String caps) {
    this.state = state;
    this.direction = direction;
    this.state_number = state_number;
    this.flags = flags;
    this.caps = caps;
  }
  public String getState() {
    return state;
  }
  public String getDirection() {
    return direction;
  }
  public String getState_number() {
    return state_number;
  }
  public String getFlags() {
    return flags;
  }
  public String getCaps() {
    return caps;
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
    if(o instanceof ChannelData) {
      if(o.hashCode() == this.hashCode()) {
        if(o.toString().equals(this.toString())) {
          return true;
        }
      }
    }
    return false;
  }
}
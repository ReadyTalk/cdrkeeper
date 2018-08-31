package com.ecovate.cdrkeeper.cdr;

public class Applications {
  private final String app_name;
  private final String app_data;
  private final String app_stamp;
  
  private transient String json = null;
  
  public Applications(String app_name, String app_data, String app_stamp) {
    this.app_name = app_name;
    this.app_data = app_data;
    this.app_stamp = app_stamp;
  }

  public String getApp_name() {
    return app_name;
  }

  public String getApp_data() {
    return app_data;
  }

  public String getApp_stamp() {
    return app_stamp;
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
    if(o instanceof Applications) {
      if(o.hashCode() == this.hashCode()) {
        if(o.toString().equals(this.toString())) {
          return true;
        }
      }
    }
    return false;
  }
}
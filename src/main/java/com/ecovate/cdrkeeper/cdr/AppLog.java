package com.ecovate.cdrkeeper.cdr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppLog {
  private final List<Applications> applications;
  
  private transient String json = null;
  
  public AppLog(List<Applications> applications) {
    this.applications = new ArrayList<>(applications);
  }

  public List<Applications> getApplications() {
    return Collections.unmodifiableList(applications);
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
    if(o instanceof AppLog) {
      if(o.hashCode() == this.hashCode()) {
        if(o.toString().equals(this.toString())) {
          return true;
        }
      }
    }
    return false;
  }
}
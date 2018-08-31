package com.ecovate.cdrkeeper.cdr;

import java.util.List;

public class CallFlowExtension {
  private final String name;
  private final String number;
  private final List<CallFlowExtensionApplication> applications;
  
  private transient String json = null;
  
  public CallFlowExtension(String name, String number, List<CallFlowExtensionApplication> applications) {
    this.name = name;
    this.number = number;
    this.applications = applications;
  }
  
  public String getName() {
    return name;
  }

  public String getNumber() {
    return number;
  }

  public List<CallFlowExtensionApplication> getApplications() {
    return applications;
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
    if(o instanceof CallFlowExtension) {
      if(o.hashCode() == this.hashCode()) {
        if(o.toString().equals(this.toString())) {
          return true;
        }
      }
    }
    return false;
  }
}
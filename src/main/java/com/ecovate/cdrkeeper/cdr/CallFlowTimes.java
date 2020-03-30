package com.ecovate.cdrkeeper.cdr;

public class CallFlowTimes {
  private final String created_time;
  private final String profile_created_time;
  private final String progress_time;
  private final String progress_media_time;
  private final String answered_time;
  private final String bridged_time;
  private final String last_hold_time;
  private final String hold_accum_time;
  private final String hangup_time;
  private final String resurrect_time;
  private final String transfer_time;
  
  private transient String json = null;
  
  public CallFlowTimes(String created_time, String profile_created_time, String progress_time,
      String progress_media_time, String answered_time, String bridged_time, String last_hold_time,
      String hold_accum_time, String hangup_time, String resurrect_time, String transfer_time) {
    this.created_time = created_time;
    this.profile_created_time = profile_created_time;
    this.progress_time = progress_time;
    this.progress_media_time = progress_media_time;
    this.answered_time = answered_time;
    this.bridged_time = bridged_time;
    this.last_hold_time = last_hold_time;
    this.hold_accum_time = hold_accum_time;
    this.hangup_time = hangup_time;
    this.resurrect_time = resurrect_time;
    this.transfer_time = transfer_time;
  }
  public String getCreated_time() {
    return created_time;
  }
  public String getProfile_created_time() {
    return profile_created_time;
  }
  public String getProgress_time() {
    return progress_time;
  }
  public String getProgress_media_time() {
    return progress_media_time;
  }
  public String getAnswered_time() {
    return answered_time;
  }
  public String getBridged_time() {
    return bridged_time;
  }
  public String getLast_hold_time() {
    return last_hold_time;
  }
  public String getHold_accum_time() {
    return hold_accum_time;
  }
  public String getHangup_time() {
    return hangup_time;
  }
  public String getResurrect_time() {
    return resurrect_time;
  }
  public String getTransfer_time() {
    return transfer_time;
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
    if(o instanceof CallFlowTimes) {
      if(o.hashCode() == this.hashCode()) {
        if(o.toString().equals(this.toString())) {
          return true;
        }
      }
    }
    return false;
  }
}
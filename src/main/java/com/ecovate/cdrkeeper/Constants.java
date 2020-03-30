package com.ecovate.cdrkeeper;

import java.lang.Thread.UncaughtExceptionHandler;

import org.threadly.util.ExceptionUtils;

public class Constants {
  
  public static final UncaughtExceptionHandler UEH = new UncaughtExceptionHandler() {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
      ExceptionUtils.getExceptionHandler().accept(e);
    }
  };

  public static final String CREATE_CDR_RAW_TABLE = 
      "CREATE TABLE IF NOT EXISTS cdr_raw("+
          "core_uuid varchar(36) NOT NULL,"+
          "call_uuid varchar(36) NOT NULL,"+
          "uuid varchar(36) NOT NULL,"+
          "created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"+
          "rawdata MEDIUMTEXT, "+
          "PRIMARY KEY (core_uuid, call_uuid, uuid)"+
          ")";

  public static final String CREATE_CDR_CALL_TABLE = 
      "CREATE TABLE IF NOT EXISTS cdr_call("+
          "core_uuid varchar(36) NOT NULL,"+
          "call_uuid varchar(36) NOT NULL,"+
          "uuid varchar(36) NOT NULL,"+
          "created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"+
          "start_time TIMESTAMP NULL DEFAULT NULL,"+
          "end_time TIMESTAMP NULL DEFAULT NULL,"+
          "direction varchar(36) NOT NULL," +
          "sip_call_id varchar(255),"+
          
          "audio_inbound_media_packet_count BIGINT NOT NULL DEFAULT 0,"+
          "audio_inbound_skip_packet_count BIGINT NOT NULL DEFAULT 0,"+
          "audio_inbound_quality_percentage DOUBLE NOT NULL DEFAULT 0,"+
          "audio_inbound_mos DOUBLE NOT NULL DEFAULT 0,"+

          "audio_outbound_media_packet_count BIGINT NOT NULL DEFAULT 0,"+
          "audio_outbound_skip_packet_count BIGINT NOT NULL DEFAULT 0,"+
          "write_codec varchar(36)," +
          
          "read_codec varchar(36)," +
          "hangup_cause varchar(36)," +
          "remote_media_ip varchar(36)," +
          "local_media_ip varchar(36)," +

          "PRIMARY KEY (core_uuid, call_uuid, uuid)"+
        ")";
}

package com.ecovate.cdrkeeper;

public class Constants {

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
          "sip_call_id varchar(255) NOT NULL,"+
          
          "audio_inbound_media_packet_count BIGINT,"+
          "audio_inbound_skip_packet_count BIGINT,"+
          "audio_inbound_quality_percentage DOUBLE,"+
          "audio_inbound_mos DOUBLE,"+

          "audio_outbound_media_packet_count BIGINT,"+
          "audio_outbound_skip_packet_count BIGINT,"+
          "write_codec varchar(36) NOT NULL," +
          
          "read_codec varchar(36) NOT NULL," +
          "hangup_cause varchar(36) NOT NULL," +
          "remote_media_ip varchar(36) NOT NULL," +
          "local_media_ip varchar(36) NOT NULL," +

          "PRIMARY KEY (core_uuid, call_uuid, uuid)"+
        ")";
}

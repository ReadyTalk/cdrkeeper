package com.ecovate.cdrkeeper.cdr;

public class AudioStatsOutbound {
  private final Long raw_bytes;
  private final Long media_bytes;
  private final Long packet_count;
  private final Long media_packet_count;
  private final Long skip_packet_count;
  private final Long dtmf_packet_count;
  private final Long cng_packet_count;
  private final Long rtcp_packet_count;
  private final Long rtcp_octet_count;
  
  private transient String json = null;
  
  public AudioStatsOutbound(Long raw_bytes, Long media_bytes, Long packet_count, Long media_packet_count,
      Long skip_packet_count, Long dtmf_packet_count, Long cng_packet_count, Long rtcp_packet_count,
      Long rtcp_octet_count) {
    this.raw_bytes = raw_bytes;
    this.media_bytes = media_bytes;
    this.packet_count = packet_count;
    this.media_packet_count = media_packet_count;
    this.skip_packet_count = skip_packet_count;
    this.dtmf_packet_count = dtmf_packet_count;
    this.cng_packet_count = cng_packet_count;
    this.rtcp_packet_count = rtcp_packet_count;
    this.rtcp_octet_count = rtcp_octet_count;
  }

  public Long getRaw_bytes() {
    return raw_bytes;
  }

  public Long getMedia_bytes() {
    return media_bytes;
  }

  public Long getPacket_count() {
    return packet_count;
  }

  public Long getMedia_packet_count() {
    return media_packet_count;
  }

  public Long getSkip_packet_count() {
    return skip_packet_count;
  }

  public Long getDtmf_packet_count() {
    return dtmf_packet_count;
  }

  public Long getCng_packet_count() {
    return cng_packet_count;
  }

  public Long getRtcp_packet_count() {
    return rtcp_packet_count;
  }

  public Long getRtcp_octet_count() {
    return rtcp_octet_count;
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
    if(o instanceof AudioStatsOutbound) {
      if(o.hashCode() == this.hashCode()) {
        if(o.toString().equals(this.toString())) {
          return true;
        }
      }
    }
    return false;
  }
}
package com.ecovate.cdrkeeper.cdr;

import java.util.ArrayList;
import java.util.List;

public class AudioStatsInbound {
  private final Long raw_bytes;
  private final Long media_bytes;
  private final Long packet_count;
  private final Long media_packet_count;
  private final Long skip_packet_count;
  private final Long jitter_packet_count;
  private final Long dtmf_packet_count;
  private final Long cng_packet_count;
  private final Long flush_packet_count;
  private final Long largest_jb_size;
  private final Double jitter_min_variance;
  private final Double jitter_max_variance;
  private final Double jitter_loss_rate;
  private final Double jitter_burst_rate;
  private final Double mean_interval;
  private final Long flaw_total;
  private final Double quality_percentage;
  private final Double mos;
  private final List<InboundAudioErrorLog> errorLog;
  
  private transient String json = null;
  
  public AudioStatsInbound(Long raw_bytes, Long media_bytes, Long packet_count, Long media_packet_count,
      Long skip_packet_count, Long jitter_packet_count, Long dtmf_packet_count, Long cng_packet_count,
      Long flush_packet_count, Long largest_jb_size, Double jitter_min_variance, Double jitter_max_variance,
      Double jitter_loss_rate, Double jitter_burst_rate, Double mean_interval, Long flaw_total, Double quality_percentage,
      Double mos, List<InboundAudioErrorLog> errorLog) {
    this.raw_bytes = raw_bytes;
    this.media_bytes = media_bytes;
    this.packet_count = packet_count;
    this.media_packet_count = media_packet_count;
    this.skip_packet_count = skip_packet_count;
    this.jitter_packet_count = jitter_packet_count;
    this.dtmf_packet_count = dtmf_packet_count;
    this.cng_packet_count = cng_packet_count;
    this.flush_packet_count = flush_packet_count;
    this.largest_jb_size = largest_jb_size;
    this.jitter_min_variance = jitter_min_variance;
    this.jitter_max_variance = jitter_max_variance;
    this.jitter_loss_rate = jitter_loss_rate;
    this.jitter_burst_rate = jitter_burst_rate;
    this.mean_interval = mean_interval;
    this.flaw_total = flaw_total;
    this.quality_percentage = quality_percentage;
    this.mos = mos;
    this.errorLog = new ArrayList<>(errorLog);
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

  public Long getJitter_packet_count() {
    return jitter_packet_count;
  }

  public Long getDtmf_packet_count() {
    return dtmf_packet_count;
  }

  public Long getCng_packet_count() {
    return cng_packet_count;
  }

  public Long getFlush_packet_count() {
    return flush_packet_count;
  }

  public Long getLargest_jb_size() {
    return largest_jb_size;
  }

  public Double getJitter_min_variance() {
    return jitter_min_variance;
  }

  public Double getJitter_max_variance() {
    return jitter_max_variance;
  }

  public Double getJitter_loss_rate() {
    return jitter_loss_rate;
  }

  public Double getJitter_burst_rate() {
    return jitter_burst_rate;
  }

  public Double getMean_interval() {
    return mean_interval;
  }

  public Long getFlaw_total() {
    return flaw_total;
  }

  public Double getQuality_percentage() {
    return quality_percentage;
  }

  public Double getMos() {
    return mos;
  }

  public List<InboundAudioErrorLog> getErrorLog() {
    return errorLog;
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
    if(o instanceof AudioStatsInbound) {
      if(o.hashCode() == this.hashCode()) {
        if(o.toString().equals(this.toString())) {
          return true;
        }
      }
    }
    return false;
  }
}
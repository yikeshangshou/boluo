package com.boluo.model;

import java.io.Serializable;

/**
 * @author mixueqiang
 * @since Jun 13, 2016
 */
public class Source extends BaseModel implements Serializable {
  private static final long serialVersionUID = 7063461869120769769L;

  private String source;
  private String url;
  private long topicId;
  private long lastUpdate;

  public long getLastUpdate() {
    return lastUpdate;
  }

  public String getSource() {
    return source;
  }

  public long getTopicId() {
    return topicId;
  }

  public String getUrl() {
    return url;
  }

  public void setLastUpdate(long lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public void setTopicId(long topicId) {
    this.topicId = topicId;
  }

  public void setUrl(String url) {
    this.url = url;
  }

}

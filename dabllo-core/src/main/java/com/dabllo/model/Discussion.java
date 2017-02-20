package com.dabllo.model;

import java.io.Serializable;

/**
 * @author mixueqiang
 * @since Jul 26, 2016
 */
public class Discussion extends BaseModel implements Serializable {
  private static final long serialVersionUID = -5377536052266875065L;

  private String description;
  private long itemId;
  private long lastReplyTime;
  private String title;
  private long topicId;
  private long userId;

  public String getDescription() {
    return description;
  }

  public long getItemId() {
    return itemId;
  }

  public long getLastReplyTime() {
    return lastReplyTime;
  }

  public String getTitle() {
    return title;
  }

  public long getTopicId() {
    return topicId;
  }

  public long getUserId() {
    return userId;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setItemId(long itemId) {
    this.itemId = itemId;
  }

  public void setLastReplyTime(long lastReplyTime) {
    this.lastReplyTime = lastReplyTime;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setTopicId(long topicId) {
    this.topicId = topicId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

}

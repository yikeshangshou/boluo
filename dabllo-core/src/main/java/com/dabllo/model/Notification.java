package com.dabllo.model;

import java.io.Serializable;

/**
 * @author mixueqiang
 * @since Jun 29, 2016
 */
public class Notification extends BaseModel implements Serializable {
  public static final String MODEL_COMMENT = "comment";
  public static final String MODEL_FEED = "feed";
  public static final String MODEL_MESSAGE = "message";
  public static final String MODEL_SYSTEM = "system";
  private static final long serialVersionUID = 7990249230398887819L;

  private String content;
  private long dataId; // itemId, discussionId or replyId.
  private long discussionId;
  private long fromUserId;
  private long itemId;
  private long lastReadTime;
  private long lastUpTime;
  private long lastVerifyTime;
  private long replyId;
  private long toUserId;
  private int type;
  private long value;

  public String getContent() {
    return content;
  }

  public long getDataId() {
    return dataId;
  }

  public long getDiscussionId() {
    return discussionId;
  }

  public long getFromUserId() {
    return fromUserId;
  }

  public long getItemId() {
    return itemId;
  }

  public long getLastReadTime() {
    return lastReadTime;
  }

  public long getLastUpTime() {
    return lastUpTime;
  }

  public long getReplyId() {
    return replyId;
  }

  public long getToUserId() {
    return toUserId;
  }

  public int getType() {
    return type;
  }

  public long getValue() {
    return value;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setDataId(long dataId) {
    this.dataId = dataId;
  }

  public void setDiscussionId(long discussionId) {
    this.discussionId = discussionId;
  }

  public void setFromUserId(long fromUserId) {
    this.fromUserId = fromUserId;
  }

  public void setItemId(long itemId) {
    this.itemId = itemId;
  }

  public void setLastReadTime(long lastReadTime) {
    this.lastReadTime = lastReadTime;
  }

  public void setLastUpTime(long lastUpTime) {
    this.lastUpTime = lastUpTime;
  }

  public void setReplyId(long replyId) {
    this.replyId = replyId;
  }

  public void setToUserId(long toUserId) {
    this.toUserId = toUserId;
  }

  public void setType(int type) {
    this.type = type;
  }

  public void setValue(long value) {
    this.value = value;
  }

public long getLastVerifyTime() {
	return lastVerifyTime;
}

public void setLastVerifyTime(long lastVerifyTime) {
	this.lastVerifyTime = lastVerifyTime;
}

}

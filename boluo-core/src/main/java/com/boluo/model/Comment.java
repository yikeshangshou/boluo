package com.boluo.model;

import java.io.Serializable;

/**
 * @author mixueqiang
 * @since May 26, 2016
 */
public class Comment extends BaseModel implements Serializable {
  private static final long serialVersionUID = 2388971118333453806L;

  private String content;
  private long discussionId;
  private long feedId;
  private long itemId;
  private long replyId;
  private long replyUserId;
  private long userId;

  public String getContent() {
    return content;
  }

  public long getDiscussionId() {
    return discussionId;
  }

  public long getFeedId() {
    return feedId;
  }

  public long getItemId() {
    return itemId;
  }

  public long getReplyId() {
    return replyId;
  }

  public long getReplyUserId() {
    return replyUserId;
  }

  public long getUserId() {
    return userId;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setDiscussionId(long discussionId) {
    this.discussionId = discussionId;
  }

  public void setFeedId(long feedId) {
    this.feedId = feedId;
  }

  public void setItemId(long itemId) {
    this.itemId = itemId;
  }

  public void setReplyId(long replyId) {
    this.replyId = replyId;
  }

  public void setReplyUserId(long replyUserId) {
    this.replyUserId = replyUserId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

}

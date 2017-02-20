package com.dabllo.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

/**
 * @author mixueqiang
 * @since Jul 26, 2016
 */
public class Reply extends BaseModel implements Serializable {
  private static final long serialVersionUID = -2670805643230889085L;

  private String content;
  private long discussionId;
  private long userId;

  public String getContent() {
    return content;
  }

  public String getContentPreview() {
    if (StringUtils.length(content) > 60) {
      return StringUtils.substring(content, 0, 60);
    }

    return content;
  }

  public long getDiscussionId() {
    return discussionId;
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

  public void setUserId(long userId) {
    this.userId = userId;
  }

}

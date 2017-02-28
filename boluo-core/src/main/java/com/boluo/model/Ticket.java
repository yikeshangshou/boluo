package com.boluo.model;

import java.io.Serializable;

/**
 * @author mixueqiang
 * @since Jul 23, 2016
 */
public class Ticket extends BaseModel implements Serializable {
  private static final long serialVersionUID = -8062639519857723008L;

  private String contact;
  private String content;
  private long userId;

  public String getContact() {
    return contact;
  }

  public String getContent() {
    return content;
  }

  public long getUserId() {
    return userId;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

}

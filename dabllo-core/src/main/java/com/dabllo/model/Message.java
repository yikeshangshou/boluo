package com.dabllo.model;

import java.io.Serializable;

/**
 * @author mixueqiang
 * @since Mar 8, 2014
 */
public class Message extends BaseModel implements Serializable {
  private static final long serialVersionUID = 2517566138485434373L;

  private long from;
  private long to;
  private String title;
  private String content;
  private int status;

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public long getFrom() {
    return from;
  }

  public void setFrom(long from) {
    this.from = from;
  }

  public long getTo() {
    return to;
  }

  public void setTo(long to) {
    this.to = to;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

}
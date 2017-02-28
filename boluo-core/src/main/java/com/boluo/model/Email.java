package com.boluo.model;

import java.io.Serializable;

/**
 * @author mixueqiang
 * @since May 5, 2014
 */
public class Email implements Serializable {
  private static final long serialVersionUID = -6917039219468825559L;
  public static final int TYPE_USER_ACTIVATE = 1;
  public static final int TYPE_WORKER_ACTIVATE = 2;
  public static final int TYPE_COMPANY_ACTIVATE = 3;
  public static final int TYPE_PASSWORD_RESET = 4;

  private long id;
  private int type;
  private long userId;
  private String email;
  private String fromEmail;
  private String fromName;
  private String toEmail;
  private String toName;
  private String locale;
  private String subject;
  private String message;
  private int status;
  private long createTime;
  private long sendTime;

  public long getCreateTime() {
    return createTime;
  }

  public String getEmail() {
    return email;
  }

  public String getFromEmail() {
    return fromEmail;
  }

  public String getFromName() {
    return fromName;
  }

  public long getId() {
    return id;
  }

  public String getLocale() {
    return locale;
  }

  public String getMessage() {
    return message;
  }

  public long getSendTime() {
    return sendTime;
  }

  public int getStatus() {
    return status;
  }

  public String getSubject() {
    return subject;
  }

  public String getToEmail() {
    return toEmail;
  }

  public String getToName() {
    return toName;
  }

  public int getType() {
    return type;
  }

  public long getUserId() {
    return userId;
  }

  public void setCreateTime(long createTime) {
    this.createTime = createTime;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setFromEmail(String fromEmail) {
    this.fromEmail = fromEmail;
  }

  public void setFromName(String fromName) {
    this.fromName = fromName;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setSendTime(long sendTime) {
    this.sendTime = sendTime;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public void setToEmail(String toEmail) {
    this.toEmail = toEmail;
  }

  public void setToName(String toName) {
    this.toName = toName;
  }

  public void setType(int type) {
    this.type = type;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

}

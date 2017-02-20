package com.dabllo.model;

import java.io.Serializable;

/**
 * @author mixueqiang
 * @since Jan 4, 2016
 */
public class Media extends BaseModel implements Serializable {
  private static final long serialVersionUID = -1858611441685146985L;

  private String date;
  private String path;
  private String title;
  private int type;
  private String url;
  private long userId;

  public String getDate() {
    return date;
  }

  public String getPath() {
    return path;
  }

  public String getTitle() {
    return title;
  }

  public int getType() {
    return type;
  }

  public String getUrl() {
    return url;
  }

  public long getUserId() {
    return userId;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setType(int type) {
    this.type = type;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

  @Override
  public String toString() {
    return "Media [userId=" + userId + ", type=" + type + ", path=" + path + ", url=" + url + "]";
  }

}

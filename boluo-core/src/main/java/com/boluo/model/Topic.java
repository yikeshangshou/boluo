package com.boluo.model;

import java.io.Serializable;

/**
 * @author mixueqiang
 * @since Feb 22, 2016
 */
public class Topic extends BaseModel implements Serializable {
  private static final long serialVersionUID = 5391872281132305200L;

  private String description;
  private String image;
  private int itemCount;
  private long lastUpdateTime;
  private String link;
  private int rank;
  private int selected;
  private String title;
  private int userCount;
  private long userId;

  public String getDescription() {
    return description;
  }

  public String getImage() {
    return image;
  }

  public int getItemCount() {
    return itemCount;
  }

  public long getLastUpdateTime() {
    return lastUpdateTime;
  }

  public String getLink() {
    return link;
  }

  public int getRank() {
    return rank;
  }

  public int getSelected() {
    return selected;
  }

  public String getTitle() {
    return title;
  }

  public int getUserCount() {
    return userCount;
  }

  public long getUserId() {
    return userId;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public void setItemCount(int itemCount) {
    this.itemCount = itemCount;
  }

  public void setLastUpdateTime(long lastUpdateTime) {
    this.lastUpdateTime = lastUpdateTime;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public void setRank(int rank) {
    this.rank = rank;
  }

  public void setSelected(int selected) {
    this.selected = selected;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setUserCount(int userCount) {
    this.userCount = userCount;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

}

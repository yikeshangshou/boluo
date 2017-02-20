package com.dabllo.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mixueqiang
 * @since Dec 24, 2015
 */
public class Feed extends BaseModel implements Serializable {
  private static final long serialVersionUID = -8062639519857723008L;

  private int commentCount;
  private String description;
  private int hidden;
  private String image;
  private List<String> images;
  private BigDecimal latitude;
  private int likeCount;
  private int likeStatus; // 用户是否赞了这条动态
  private String link;
  private BigDecimal longitude;
  private String place;
  private String source;
  private String title;
  private long topicId;
  private long userId;
  private String username;

  public int getCommentCount() {
    return commentCount;
  }

  public String getDescription() {
    return description;
  }

  public int getHidden() {
    return hidden;
  }

  public String getImage() {
    return image;
  }

  public List<String> getImages() {
    if (images == null) {
      images = new ArrayList<String>();
    }

    return images;
  }

  public BigDecimal getLatitude() {
    return latitude;
  }

  public int getLikeCount() {
    return likeCount;
  }

  public int getLikeStatus() {
    return likeStatus;
  }

  public String getLink() {
    return link;
  }

  public BigDecimal getLongitude() {
    return longitude;
  }

  public String getPlace() {
    return place;
  }

  public String getSource() {
    return source;
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

  public String getUsername() {
    return username;
  }

  public void setCommentCount(int commentCount) {
    this.commentCount = commentCount;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setHidden(int hidden) {
    this.hidden = hidden;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public void setImages(List<String> images) {
    this.images = images;
  }

  public void setLatitude(BigDecimal latitude) {
    this.latitude = latitude;
  }

  public void setLikeCount(int likeCount) {
    this.likeCount = likeCount;
  }

  public void setLikeStatus(int likeStatus) {
    this.likeStatus = likeStatus;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public void setLongitude(BigDecimal longitude) {
    this.longitude = longitude;
  }

  public void setPlace(String place) {
    this.place = place;
  }

  public void setSource(String source) {
    this.source = source;
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

  public void setUsername(String username) {
    this.username = username;
  }

}

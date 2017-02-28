package com.boluo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mixueqiang
 * @since Jul 6, 2016
 */
public class Article extends BaseModel implements Serializable {
  private static final long serialVersionUID = 3287780480837954059L;

  private String description;
  private int hot;
  private String image;
  private List<String> images;
  private String link;
  private long publishTime;
  private int rank;
  private long relatedId;
  private String source;
  private String title;
  private long userId;

  public String getDescription() {
    return description;
  }

  public int getHot() {
    return hot;
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

  public String getLink() {
    return link;
  }

  public long getPublishTime() {
    return publishTime;
  }

  public int getRank() {
    return rank;
  }

  public long getRelatedId() {
    return relatedId;
  }

  public String getSource() {
    return source;
  }

  public String getTitle() {
    return title;
  }

  public long getUserId() {
    return userId;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setHot(int hot) {
    this.hot = hot;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public void setImages(List<String> images) {
    this.images = images;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public void setPublishTime(long publishTime) {
    this.publishTime = publishTime;
  }

  public void setRank(int rank) {
    this.rank = rank;
  }

  public void setRelatedId(long relatedId) {
    this.relatedId = relatedId;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

}

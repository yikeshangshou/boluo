package com.dabllo.model;

import java.io.Serializable;

/**
 * @author mixueqiang
 * @since Apr 29, 2016
 */
public class Category extends BaseModel implements Serializable {
  private static final long serialVersionUID = 5472992695418748843L;

  private String image;
  private long parentId;
  private String slug;

  public String getImage() {
    return image;
  }

  public long getParentId() {
    return parentId;
  }

  public String getSlug() {
    return slug;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public void setParentId(long parentId) {
    this.parentId = parentId;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

}

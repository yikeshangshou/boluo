package com.boluo.model;

import java.io.Serializable;

import com.boluo.model.BaseModel;

/**
 * @author xueqiangmi
 * @since Jun 11, 2015
 */
public class FeedImage extends BaseModel implements Serializable {
  private static final long serialVersionUID = 6909300125059553668L;
  private static final String URL_REPO = "http://wfenxiang.b0.upaiyun.com";

  private long feedId;
  private String path;
  private int sort;

  public long getFeedId() {
    return feedId;
  }

  public String getImage() {
    return URL_REPO + path;
  }

  public String getLargeImage() {
    return getImage() + "!L";
  }

  public String getMediumImage() {
    return getImage() + "!M";
  }

  public String getPath() {
    return path;
  }

  public String getSmallImage() {
    return getImage() + "!S";
  }

  public int getSort() {
    return sort;
  }

  public void setFeedId(long feedId) {
    this.feedId = feedId;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public void setSort(int sort) {
    this.sort = sort;
  }

}

package com.boluo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mixueqiang
 * @since Jul 6, 2016
 */
public class Section extends BaseModel implements Serializable {
  private static final long serialVersionUID = 5391872281132305200L;

  private List<Discussion> discussions;
  private List<Item> items;
  private long refreshTime;
  private String title;
  private List<Topic> topics;

  public Section() {
    super();
  }

  public Section(String title, long refreshTime) {
    super();
    this.title = title;
    this.refreshTime = refreshTime;
  }

  public List<Discussion> getDiscussions() {
    if (discussions == null) {
      discussions = new ArrayList<Discussion>();
    }

    return discussions;
  }

  public List<Item> getItems() {
    if (items == null) {
      items = new ArrayList<Item>();
    }

    return items;
  }

  public long getRefreshTime() {
    return refreshTime;
  }

  public String getTitle() {
    return title;
  }

  public List<Topic> getTopics() {
    if (topics == null) {
      topics = new ArrayList<Topic>();
    }

    return topics;
  }

  public void setDiscussions(List<Discussion> discussions) {
    this.discussions = discussions;
  }

  public void setItems(List<Item> items) {
    this.items = items;
  }

  public void setRefreshTime(long refreshTime) {
    this.refreshTime = refreshTime;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setTopics(List<Topic> topics) {
    this.topics = topics;
  }

}

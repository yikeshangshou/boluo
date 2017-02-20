package com.dabllo.crawler.tech;

import java.io.Serializable;

/**
 * @author mixueqiang
 * @since Jul 5, 2016
 */
public class Response implements Serializable {
  class Data implements Serializable {
    private static final long serialVersionUID = -4215115514851277635L;

    private int before;
    private int current;
    private int first;
    private Item[] items;
    private int last;
    private int limit;
    private int next;
    private int total_items;
    private int total_pages;

    public int getBefore() {
      return before;
    }

    public int getCurrent() {
      return current;
    }

    public int getFirst() {
      return first;
    }

    public Item[] getItems() {
      return items;
    }

    public int getLast() {
      return last;
    }

    public int getLimit() {
      return limit;
    }

    public int getNext() {
      return next;
    }

    public int getTotal_items() {
      return total_items;
    }

    public int getTotal_pages() {
      return total_pages;
    }

    public void setBefore(int before) {
      this.before = before;
    }

    public void setCurrent(int current) {
      this.current = current;
    }

    public void setFirst(int first) {
      this.first = first;
    }

    public void setItems(Item[] items) {
      this.items = items;
    }

    public void setLast(int last) {
      this.last = last;
    }

    public void setLimit(int limit) {
      this.limit = limit;
    }

    public void setNext(int next) {
      this.next = next;
    }

    public void setTotal_items(int total_items) {
      this.total_items = total_items;
    }

    public void setTotal_pages(int total_pages) {
      this.total_pages = total_pages;
    }
  }

  class Item implements Serializable {
    private static final long serialVersionUID = 9139007742986969670L;

    private String catch_title;
    private int close_comment;
    private int column_id;
    private String cover;
    private String created_at;
    private String description;
    private long id;
    private String news_url;
    private String published_at;
    private int related_company_id;
    private String related_company_name;
    private String related_company_type;
    private String summary;
    private String title;
    private String updated_at;
    private long user_id;

    public String getCatch_title() {
      return catch_title;
    }

    public int getClose_comment() {
      return close_comment;
    }

    public int getColumn_id() {
      return column_id;
    }

    public String getCover() {
      return cover;
    }

    public String getCreated_at() {
      return created_at;
    }

    public String getDescription() {
      return description;
    }

    public long getId() {
      return id;
    }

    public String getNews_url() {
      return news_url;
    }

    public String getPublished_at() {
      return published_at;
    }

    public int getRelated_company_id() {
      return related_company_id;
    }

    public String getRelated_company_name() {
      return related_company_name;
    }

    public String getRelated_company_type() {
      return related_company_type;
    }

    public String getSummary() {
      return summary;
    }

    public String getTitle() {
      return title;
    }

    public String getUpdated_at() {
      return updated_at;
    }

    public long getUser_id() {
      return user_id;
    }

    public void setCatch_title(String catch_title) {
      this.catch_title = catch_title;
    }

    public void setClose_comment(int close_comment) {
      this.close_comment = close_comment;
    }

    public void setColumn_id(int column_id) {
      this.column_id = column_id;
    }

    public void setCover(String cover) {
      this.cover = cover;
    }

    public void setCreated_at(String created_at) {
      this.created_at = created_at;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public void setId(long id) {
      this.id = id;
    }

    public void setNews_url(String news_url) {
      this.news_url = news_url;
    }

    public void setPublished_at(String published_at) {
      this.published_at = published_at;
    }

    public void setRelated_company_id(int related_company_id) {
      this.related_company_id = related_company_id;
    }

    public void setRelated_company_name(String related_company_name) {
      this.related_company_name = related_company_name;
    }

    public void setRelated_company_type(String related_company_type) {
      this.related_company_type = related_company_type;
    }

    public void setSummary(String summary) {
      this.summary = summary;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public void setUpdated_at(String updated_at) {
      this.updated_at = updated_at;
    }

    public void setUser_id(long user_id) {
      this.user_id = user_id;
    }
  }

  private static final long serialVersionUID = -6191149799369539052L;
  private long code;
  private Data data;

  public long getCode() {
    return code;
  }

  public Data getData() {
    return data;
  }

  public void setCode(long code) {
    this.code = code;
  }

  public void setData(Data data) {
    this.data = data;
  }

}

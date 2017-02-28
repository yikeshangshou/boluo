package com.boluo.model;

import java.io.Serializable;

/**
 * @author mixueqiang
 * @since Aug 5, 2016
 */
public class Menu implements Serializable {
  private static final long serialVersionUID = 7999934644926014613L;
  private static final String URL_PREFIX = "/api/v1/item?section=";

  private String name;
  private String title;
  private String url;
  private int visible;

  public Menu() {
  }

  public Menu(String name, String title, int visible) {
    this.name = name;
    this.title = title;
    this.visible = visible;
    this.url = URL_PREFIX + name;
  }

  public String getName() {
    return name;
  }

  public String getTitle() {
    return title;
  }

  public String getUrl() {
    return url;
  }

  public int getVisible() {
    return visible;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setVisible(int visible) {
    this.visible = visible;
  }

}

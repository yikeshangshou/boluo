package com.boluo.web.util;

import java.util.ArrayList;
import java.util.List;

import com.boluo.model.Menu;

/**
 * @author mixueqiang
 * @since Aug 5, 2016
 */
public final class Menus {
  private static final List<Menu> MENUS = new ArrayList<Menu>();

  static {
    Menu menu = new Menu("1d", "昨日回顾", 1);
    menu.setUrl("/api/v1/item?time=1d");
    MENUS.add(menu);

    menu = new Menu("new", "最新", 1);
    menu.setUrl("/api/v1/item?time=8h:new");
    MENUS.add(menu);

    menu = new Menu("hot", "热度上升最快", 1);
    menu.setUrl("/api/v1/item?time=16h");
    MENUS.add(menu);

    menu = new Menu("24h", "24小时最热", 1);
    menu.setUrl("/api/v1/item?time=24h");
    MENUS.add(menu);

    menu = new Menu("3d", "3天最热", 1);
    menu.setUrl("/api/v1/item?time=3d");
    MENUS.add(menu);

    menu = new Menu("7d", "7天最热", 1);
    menu.setUrl("/api/v1/item?time=7d");
    MENUS.add(menu);
  }

  public static List<Menu> get() {
    return MENUS;
  }

}

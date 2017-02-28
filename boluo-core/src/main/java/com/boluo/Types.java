package com.boluo;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * @author mixueqiang
 * @since Apr 29, 2014
 */
public class Types {
  private static final Map<Integer, String> map = new HashMap<Integer, String>();

  static {
    map.put(0, "不限");
    map.put(1, "全职");
    map.put(2, "兼职");
    map.put(3, "实习生");
  }

  public static String get(int typeId) {
    String type = map.get(typeId);
    if (StringUtils.isEmpty(type)) {
      return map.get(0);
    }

    return type;
  }

}

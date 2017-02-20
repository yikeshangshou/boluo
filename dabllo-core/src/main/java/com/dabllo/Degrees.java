package com.dabllo;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * @author mixueqiang
 * @since Jul 6, 2014
 */
public class Degrees {
  private static final Map<Integer, String> map = new HashMap<Integer, String>();

  static {
    map.put(0, "未知学历");
    map.put(1, "高中/中专");
    map.put(2, "大学专科");
    map.put(3, "大学本科");
    map.put(4, "研究生");
    map.put(5, "博士生");
    map.put(100, "其他");
  }

  public static String get(int degreeId) {
    String degree = map.get(degreeId);
    if (StringUtils.isEmpty(degree)) {
      return map.get(100);
    }

    return degree;
  }

}

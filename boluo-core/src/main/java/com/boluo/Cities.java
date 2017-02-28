package com.boluo;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * @author mixueqiang
 * @since Apr 29, 2014
 */
@Deprecated
public class Cities {
  private static final Map<Integer, String> map = new HashMap<Integer, String>();

  static {
    map.put(0, "不限");
    map.put(1, "北京");
    map.put(2, "上海");
    map.put(3, "广州");
    map.put(4, "深圳");
    map.put(5, "天津");
    map.put(6, "杭州");
    map.put(7, "厦门");
    map.put(8, "西安");
    map.put(9, "珠海");
    map.put(10, "宁波");
    map.put(11, "福州");
    map.put(12, "济南");
    map.put(13, "青岛");
    map.put(14, "成都");
    map.put(15, "沈阳");
    map.put(16, "哈尔滨");
    map.put(100, "其他");
  }

  public static String get(int cityId) {
    String city = map.get(cityId);
    if (StringUtils.isEmpty(city)) {
      return map.get(100);
    }

    return city;
  }

}

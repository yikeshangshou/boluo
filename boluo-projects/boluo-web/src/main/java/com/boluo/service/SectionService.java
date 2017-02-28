package com.boluo.service;

import com.boluo.model.Section;

/**
 * @author mixueqiang
 * @since Jul 7, 2016
 */
public interface SectionService {
  public static final String TIME_16H = "16h";
  public static final String TIME_1D = "1d";
  public static final String TIME_24H = "24h";
  public static final String TIME_3D = "3d";
  public static final String TIME_3H = "3h";
  public static final String TIME_7D = "7d";
  public static final String TIME_8H = "8h";
  public static final String TYPE_HOT = ":hot";
  public static final String TYPE_NEW = ":new";
  public static final String SECTION_LATEST = "latest";
  public static final String SECTION_HOT = "hot";
  

  Section getSection1(long userId);

  Section getSection2(long userId);

  Section getSection3(long userId);

  Section getSection4(long userId);

  Section getSectionT16h(long userId);

  Section getSectionT1d(long userId);

  Section getSectionT24h(long userId);

  Section getSectionT3d(long userId);

  Section getSectionT3h(long userId);

  Section getSectionT7d(long userId);

  Section getSectionT8hHot(long userId);

  Section getSectionT8hNew(long userId);

  boolean resetCache(String time);

}

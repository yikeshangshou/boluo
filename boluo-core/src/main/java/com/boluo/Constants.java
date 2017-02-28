package com.boluo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mixueqiang
 * @since Jan 4, 2016
 */
public class Constants {

  public static final String APP_DOMAIN = "http://www.dabllo.com/";
  public static final String APP_NAME = "dabllo";
  public static final String IMAGE_REPO = "/data/" + APP_NAME + "/";

  public static final int IMAGE_TYPE_COVER = 1;
  public static final int IMAGE_TYPE_IMAGE = 0;

  public static final int READ_CLOSE = 0;
  public static final int READ_OPEN = 1;

  public static final int SOURCE_APP = 1;
  public static final int SOURCE_OTHERS = 0;
  public static final int SOURCE_WEB = 2;

  public static final int STATUS_DELETED_BY_ADMIN = -2;
  public static final int STATUS_DELETED_BY_REVIEW = -3;
  public static final int STATUS_DELETED_BY_SYSTEM = -100;
  public static final int STATUS_DELETED_BY_USER = -1;
  public static final int STATUS_DISABLED = 0;
  public static final int STATUS_ENABLED = 1;
  public static final int STATUS_NO = 0;
  public static final int STATUS_NOT_READY = 0;
  public static final int STATUS_OK = 1;
  public static final int STATUS_YES = 1;
  public static final int STATUS_READY = 1;

  public static final long MINUTE = 60 * 1000L;
  public static final long HOUR = 60 * MINUTE;
  public static final long DAY = 24 * HOUR;

  public static final Map<Integer, String> REVIEW_REASONS = new HashMap<Integer, String>();

  static {
    REVIEW_REASONS.put(0, "已过时或者已存在的资讯。感谢你的投稿。");
    REVIEW_REASONS.put(1, "已有类似的资讯。感谢你的投稿。");
    REVIEW_REASONS.put(2, "不是资讯。大菠萝专注于资讯信息，分析、心得等文章暂不在大菠萝的投稿范围。感谢你的投稿。");
    REVIEW_REASONS.put(3, "已过时或者已存在的资讯。感谢你的投稿。");
    REVIEW_REASONS.put(4, "不是资讯。大菠萝是一个严肃的资讯平台，请投稿有价值的资讯帮助我们一起成长。");
    REVIEW_REASONS.put(10, "广告、垃圾内容等。大菠萝是一个严肃的资讯平台，请投稿有价值的资讯帮助我们一起成长。");
  }

}

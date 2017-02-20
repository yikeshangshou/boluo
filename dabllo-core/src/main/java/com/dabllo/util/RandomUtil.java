package com.dabllo.util;

import org.apache.commons.lang.math.RandomUtils;

/**
 * @author mixueqiang
 * @since Oct 12, 2013
 */
public class RandomUtil {

  public static final String UPPER_CASE_CHARCHTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
  public static final String WORD_CHARCHTERS = "abcdefghijklmnopqrstuvwxyz" + UPPER_CASE_CHARCHTERS;
  public static final String ANY_CHARACTERS = "~!@#$%^&*()" + WORD_CHARCHTERS;

  public static String randomAnyChars(int length) {
    StringBuilder sb = new StringBuilder();
    while (sb.length() < length) {
      int index = RandomUtils.nextInt(ANY_CHARACTERS.length());
      sb.append(ANY_CHARACTERS.charAt(index));
    }

    return sb.toString();
  }

  public static String randomUpperCaseString(int length) {
    StringBuilder sb = new StringBuilder();
    while (sb.length() < length) {
      int index = RandomUtils.nextInt(UPPER_CASE_CHARCHTERS.length());
      sb.append(UPPER_CASE_CHARCHTERS.charAt(index));
    }

    return sb.toString();
  }

  public static String randomString(int length) {
    StringBuilder sb = new StringBuilder();
    while (sb.length() < length) {
      int index = RandomUtils.nextInt(WORD_CHARCHTERS.length());
      sb.append(WORD_CHARCHTERS.charAt(index));
    }

    return sb.toString();
  }

  public static String randomNumber(int length) {
    StringBuilder sb = new StringBuilder();
    while (sb.length() < length) {
      sb.append(RandomUtils.nextInt(10));
    }

    return sb.toString();
  }

}

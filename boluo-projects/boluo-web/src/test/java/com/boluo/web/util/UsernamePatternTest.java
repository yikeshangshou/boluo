package com.boluo.web.util;

import java.util.regex.Pattern;

import org.junit.Test;

/**
 * @author mixueqiang
 * @since Aug 1, 2016
 */
public class UsernamePatternTest {

  private static final String[] NAMES = new String[] { //
      "宓学强", //
      "Dabllo", //
      "宓学强的测试", //
      "宓学强的测试十个字符", //
      "宓学强的测试十一个字符", //
      "宓学强Dabllo", //
      "宓学强Dabllo11", //
      "Dabllo123", //
      "Dabllo1234", //
      "Dabllo12345", //
      "Da", //
      "", //
  };

  @Test
  public void test() {
    String regex = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{3,10}$";
    Pattern pattern = Pattern.compile(regex);
    for (String name : NAMES)
      System.out.println(name + ": " + pattern.matcher(name).matches());
  }

}

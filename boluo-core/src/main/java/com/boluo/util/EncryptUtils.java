package com.boluo.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author xueqiangmi
 * @since May 6, 2013
 */
public final class EncryptUtils {

  public static String encryptPlainPassword(String password) {
    return encryptMd5Password(DigestUtils.md5Hex(password));
  }

  public static String encryptMd5Password(String password) {
    if (StringUtils.length(password) != 32) {
      throw new IllegalArgumentException("MD5 string required!");
    }

    String salt = StringUtils.substring(password, 27);
    return DigestUtils.md5Hex(password + salt);
  }

  public static String encryptshalHex(String sha1) {
    return DigestUtils.sha1Hex(sha1);
  }

  public static String Md5(String password, int count) {
    String md5 = password;
    for (int i = 0; i < count; i++) {
      md5 = DigestUtils.md5Hex(md5);
    }

    return md5;
  }

  private EncryptUtils() {
  }

}

package com.dabllo.util;

import org.junit.Test;

import com.dabllo.util.EncryptUtils;

/**
 * @author xueqiangmi
 * @since May 6, 2013
 */
public final class EncryptUtilsTest {

  @Test
  public void testEncrypt() {
    System.out.println(EncryptUtils.encryptPlainPassword("Tb034780"));
  }

}

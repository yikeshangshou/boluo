package com.boluo.web.util;

import org.junit.Test;

import com.boluo.web.util.SmsUtilsYunpian;

/**
 * @author mixueqiang
 * @since Jul 30, 2016
 */
public class SmsYunpianTest {

  @Test
  public void testSend() throws Throwable {
    SmsUtilsYunpian.send("18668090654", "register", new String[] { "123456" });
  }

}

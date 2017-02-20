package com.dabllo.web.util;

import org.junit.Test;

/**
 * @author mixueqiang
 * @since Jul 30, 2016
 */
public class SmsYuntongxunTest {

  @Test
  public void testSend() throws Throwable {
    SmsUtilsYuntongxun.send("18668090654", 1, new String[] { "123456", "1分钟" });
  }

}

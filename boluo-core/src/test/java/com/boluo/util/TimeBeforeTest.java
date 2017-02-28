package com.boluo.util;

import org.junit.Test;

/**
 * @author mixueqiang
 * @since Jul 19, 2016
 */
public class TimeBeforeTest {

  @Test
  public void testTimeBefore() {
    System.out.println((System.currentTimeMillis() - 1468897622494L) / (60 * 60 * 1000L));
  }

}

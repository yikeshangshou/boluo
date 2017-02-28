package com.boluo.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

/**
 * @author mixueqiang
 * @since Jul 6, 2016
 */
public class DateFormatTest {

  @Test
  public void testFormat() {
    System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
  }

}

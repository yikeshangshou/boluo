package com.boluo.web.util;

import java.util.Calendar;

import org.junit.Test;

/**
 * @author mixueqiang
 * @since Jul 9, 2016
 */
public class CalendarTest {

  @Test
  public void testCalendar() throws Throwable {
    Calendar calendar = Calendar.getInstance();
    System.out.println(calendar.get(Calendar.HOUR));
    System.out.println(calendar.get(Calendar.HOUR_OF_DAY));
    System.out.println(calendar.get(Calendar.MINUTE));
    System.out.println(calendar.get(Calendar.SECOND));
    Thread.sleep(2001);
    calendar = Calendar.getInstance();
    System.out.println(calendar.get(Calendar.SECOND));
  }

}

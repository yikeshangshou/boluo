package com.boluo.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author mixueqiang
 * @since Mar 9, 2014
 */
public final class CalendarUtils {

  public static Calendar getCalendar() {
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
    return calendar;
  }

  public static Calendar getCalendar(long timeInMillis) {
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
    calendar.setTimeInMillis(timeInMillis);
    return calendar;
  }

  public static Date getDate(long timeInMillis) {
    return getCalendar(timeInMillis).getTime();
  }

  public static long now() {
    return getCalendar().getTimeInMillis();
  }

  public static long todayZero() {
    Calendar calendar = getCalendar();
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    return calendar.getTimeInMillis();
  }

  public static long theDayStart(long someTime) {
    Calendar calendar = getCalendar();
    calendar.setTimeInMillis(someTime);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    return calendar.getTimeInMillis();
  }

  public static long theDayEnd(long someTime) {
    Calendar calendar = getCalendar();
    calendar.setTimeInMillis(someTime);
    calendar.add(Calendar.DAY_OF_YEAR, 1);
    calendar.set(Calendar.HOUR_OF_DAY, 0);
    calendar.set(Calendar.SECOND, 0);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.MILLISECOND, 0);

    return calendar.getTimeInMillis();
  }

}

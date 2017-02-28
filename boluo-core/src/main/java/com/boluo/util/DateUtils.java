package com.boluo.util;

/**
 * @author mixueqiang
 * @since Jul 19, 2016
 */
public final class DateUtils {

  public static String convertToReadableDate(long time) {
    if (time <= 0) {
      return null;
    }

    long nMinutes = (System.currentTimeMillis() - time) / (60 * 1000L);
    if (nMinutes >= (30 * 24 * 60)) {
      return nMinutes / (30 * 24 * 60) + "M";
    }

    if (nMinutes >= (7 * 24 * 60)) {
      return nMinutes / (7 * 24 * 60) + "W";
    }

    if (nMinutes >= (24 * 60)) {
      return nMinutes / (24 * 60) + "D";
    }

    if (nMinutes >= 60) {
      return nMinutes / 60 + "H";
    }

    return nMinutes + "m";
  }

}
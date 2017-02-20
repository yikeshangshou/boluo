package com.dabllo.util;

import java.util.Arrays;

/**
 * @author xueqiangmi
 * @since Aug 11, 2013
 */
public final class ImageTypeUtils {

  public static final String HEADER_JPEG = "FFD8FF";
  public static final String HEADER_PNG = "89504E47";
  public static final String HEADER_GIF = "47494638";

  public static boolean isImage(byte[] bytes) {
    if (bytes.length < 28) {
      return false;
    }

    byte[] header = Arrays.copyOf(bytes, 28);
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < header.length; i++) {
      int v = header[i] & 0xFF;
      String hv = Integer.toHexString(v);
      if (hv.length() < 2) {
        sb.append(0);
      }
      sb.append(hv);
    }

    String type = sb.toString().toUpperCase();
    if (type.startsWith(HEADER_JPEG) || type.startsWith(HEADER_PNG) || type.startsWith(HEADER_GIF)) {
      return true;
    }

    return false;
  }

}

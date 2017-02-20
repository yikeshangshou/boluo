package com.dabllo.util;

import org.junit.Test;

/**
 * @author mixueqiang
 * @since Jun 14, 2016
 */
public class ImageUtilsTest {

  @Test
  public void testGetAndSaveIamge() {
    Pair<String, String> pair = ImageUtils
        .getAndSaveImage("https://igcdn-photos-c-a.akamaihd.net/hphotos-ak-xat1/t51.2885-15/e35/13398876_292399477762634_1510186741_n.jpg?ig_cache_key=MTI3MDk5MzE2MTExNTEzMzM2MQ%3D%3D.2");
    System.out.println(ImageUtils.getImageUrl(pair.left + "/" + pair.right));
  }

}

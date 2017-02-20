package com.upyun;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import com.dabllo.model.Media;

/**
 * @author mixueqiang
 * @since Oct 17, 2014
 */
public class UpYunUtilsTest {

  @Test
  public void testListMedias() {
    Collection<Media> medias = UpYunUtils.listMedias("20141004", 1, 20);
    Assert.assertNotNull(medias);
    for (Media media : medias) {
      System.out.println(media.getPath());
    }
  }

}

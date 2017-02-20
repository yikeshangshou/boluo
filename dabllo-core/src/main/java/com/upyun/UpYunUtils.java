package com.upyun;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.dabllo.model.Media;
import com.upyun.UpYun.FolderItem;

/**
 * @author mixueqiang
 * @since Oct 17, 2014
 */
public final class UpYunUtils {
  private static final String BUCKET_NAME = "weifenxiang";
  private static final String USER_NAME = "mxq";
  private static final String USER_PWD = "SJTU1070379103";
  private static UpYun upyun = null;

  public static Collection<FolderItem> listItems(String folder) {
    upyun = new UpYun(BUCKET_NAME, USER_NAME, USER_PWD);
    List<FolderItem> items = upyun.readDir(folder);

    Set<FolderItem> result = new TreeSet<FolderItem>(new Comparator<FolderItem>() {
      @Override
      public int compare(FolderItem o1, FolderItem o2) {
        return o1.name.compareTo(o2.name);
      }
    });
    result.addAll(items);
    return result;
  }

  public static Collection<Media> listMedias(String folder, int page, int size) {
    int start = (page - 1) * size;
    int end = start + size;
    Collection<FolderItem> items = listItems(folder);

    // Paged.
    List<Media> result = new ArrayList<Media>();
    int i = 0;
    for (FolderItem item : items) {
      if (i >= start) {
        Media media = new Media();
        media.setDate(folder);
        media.setName(item.name);
        media.setTitle(item.name);
        result.add(media);
      }

      i++;
      if (i >= end) {
        break;
      }
    }

    return result;
  }

}

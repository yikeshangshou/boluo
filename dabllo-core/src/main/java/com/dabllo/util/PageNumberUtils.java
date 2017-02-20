package com.dabllo.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Page number generator.
 * 
 * @author xueqiangmi
 * @since Jun 29, 2013
 */
public final class PageNumberUtils {
  public static final int PAGE_SIZE_XXLARGE = 200;
  public static final int PAGE_SIZE_XLARGE = 80;
  public static final int PAGE_SIZE_LARGE = 40;
  public static final int PAGE_SIZE_XMEDIUM = 30;
  public static final int PAGE_SIZE_MEDIUM = 20;
  public static final int PAGE_SIZE_SMALL = 10;
  public static final int PAGE_SIZE_XSMALL = 5;

  /**
   * @param currentPageNumber
   *          current page number.
   * @param count
   *          the amount of the data item.
   * @return page number and the last page number.
   */
  public static Pair<List<Integer>, Integer> generate(int currentPageNumber, long count) {
    return generate(currentPageNumber, count, PAGE_SIZE_SMALL);
  }

  /**
   * @param currentPageNumber
   *          current page number.
   * @param count
   *          the amount of the data item.
   * @param size
   *          page size
   * @return page number and the last page number.
   */
  public static Pair<List<Integer>, Integer> generate(int currentPageNumber, long count, int size) {
    int last = (int) (count / size + (count % size > 0 ? 1 : 0));
    int start = currentPageNumber > 3 ? currentPageNumber - 3 : 1;
    int end = currentPageNumber + 3 < last ? currentPageNumber + 3 : last;

    List<Integer> pageNumbers = new ArrayList<Integer>();
    for (int i = start; i <= end; i++) {
      pageNumbers.add(i);
    }
    return new Pair<List<Integer>, Integer>(pageNumbers, last);
  }

  public static List<Integer> generates(int pageNumber, int last) {
    int start = pageNumber > 3 ? pageNumber - 3 : 1;
    int end = pageNumber + 3 < last ? pageNumber + 3 : last;

    List<Integer> pageNumbers = new ArrayList<Integer>();
    for (int i = start; i <= end; i++) {
      pageNumbers.add(i);
    }
    return pageNumbers;
  }
}

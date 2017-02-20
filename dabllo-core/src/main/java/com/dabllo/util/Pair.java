package com.dabllo.util;

import java.io.Serializable;

/**
 * @author xueqiangmi
 * @since Jul 6, 2013
 */
public class Pair<L, R> implements Serializable {
  private static final long serialVersionUID = 4508388586133291182L;

  public final L left;
  public final R right;

  public Pair(L left, R right) {
    if (left == null || right == null) {
      throw new NullPointerException("L: " + left + ", R:" + right);
    }

    this.left = left;
    this.right = right;
  }

  public L getLeft() {
    return left;
  }

  public R getRight() {
    return right;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((left == null) ? 0 : left.hashCode());
    result = prime * result + ((right == null) ? 0 : right.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Pair<?, ?> other = (Pair<?, ?>) obj;
    if (left == null) {
      if (other.left != null)
        return false;
    } else if (!left.equals(other.left))
      return false;
    if (right == null) {
      if (other.right != null)
        return false;
    } else if (!right.equals(other.right))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Pair [left=" + left + ", right=" + right + "]";
  }

}

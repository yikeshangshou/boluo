package com.boluo.model;

import java.io.Serializable;

/**
 * @author mixueqiang
 * @since Jun 24, 2016
 */
public class Response implements Serializable {
  private static final long serialVersionUID = 3855662876377149445L;

  private int e;
  private Object r;
  private long o;
  private String m;

  public int getE() {
    return e;
  }

  public void setE(int e) {
    this.e = e;
  }

  public Object getR() {
    return r;
  }

  public void setR(Object r) {
    this.r = r;
  }

  public long getO() {
    return o;
  }

  public void setO(long o) {
    this.o = o;
  }

  public String getM() {
    return m;
  }

  public void setM(String m) {
    this.m = m;
  }

}

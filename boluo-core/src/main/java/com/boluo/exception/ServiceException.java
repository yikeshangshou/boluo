package com.boluo.exception;

/**
 * @author mixueqiang
 * @since Apr 25, 2014
 */
public class ServiceException extends RuntimeException {
  private static final long serialVersionUID = 1470699093026331644L;

  private int errorCode = 0;

  public ServiceException(int errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public ServiceException(String message) {
    super(message);
  }

  public int getErrorCode() {
    return errorCode;
  }

}

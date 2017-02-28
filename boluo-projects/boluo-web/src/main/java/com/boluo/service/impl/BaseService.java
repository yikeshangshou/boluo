package com.boluo.service.impl;

import javax.annotation.Resource;

import com.boluo.dao.EntityDao;

/**
 * @author mixueqiang
 * @since Apr 24, 2014
 */
public abstract class BaseService {

  public static final long MINUTE = 60 * 1000L;
  public static final long HOUR = 60 * MINUTE;
  public static final long DAY = 24 * HOUR;

  @Resource
  protected EntityDao entityDao;

}

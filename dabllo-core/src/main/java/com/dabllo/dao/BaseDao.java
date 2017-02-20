package com.dabllo.dao;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 * @author xueqiangmi
 * @since May 1, 2013
 */
public abstract class BaseDao {
  private static final Log LOG = LogFactory.getLog(BaseDao.class);

  public static String ORDER_BY_ID = "id";
  public static String ORDER_OPTION_ASC = "ASC";
  public static String ORDER_OPTION_DESC = "DESC";
  public static int PAGE_SIZE_LARGE = 50;
  public static int PAGE_SIZE_MEDIUM = 20;
  public static int PAGE_SIZE_SMALL = 10;

  @Resource
  protected JdbcTemplate jdbcTemplate;

  public JdbcTemplate getJdbcTemplate() {
    return jdbcTemplate;
  }

  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  protected <T> T queryForNullable(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
    try {
      return jdbcTemplate.queryForObject(sql, args, rowMapper);

    } catch (EmptyResultDataAccessException e) {
      LOG.warn("Get 0 items on: " + sql + ", " + args);
    }

    return null;
  }

}

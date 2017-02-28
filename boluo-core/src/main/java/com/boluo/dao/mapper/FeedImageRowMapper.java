package com.boluo.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.boluo.model.FeedImage;

/**
 * @author xueqiangmi
 * @since Jun 11, 2015
 */
public class FeedImageRowMapper implements RowMapper<FeedImage> {
  private static FeedImageRowMapper instance;

  public static FeedImageRowMapper getInstance() {
    if (instance == null) {
      instance = new FeedImageRowMapper();
    }

    return instance;
  }

  @Override
  public FeedImage mapRow(ResultSet rs, int rowNum) throws SQLException {
    FeedImage entity = new FeedImage();
    entity.setId(rs.getLong("id"));
    entity.setFeedId(rs.getLong("feedId"));
    entity.setName(rs.getString("name"));
    entity.setPath(rs.getString("path"));
    entity.setSort(rs.getInt("sort"));
    entity.setStatus(rs.getInt("status"));
    entity.setCreateTime(rs.getLong("createTime"));
    entity.setUpdateTime(rs.getLong("updateTime"));

    return entity;
  }

}

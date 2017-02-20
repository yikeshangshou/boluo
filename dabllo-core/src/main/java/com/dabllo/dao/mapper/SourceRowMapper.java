package com.dabllo.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.dabllo.model.Source;

/**
 * @author mixueqiang
 * @since Jun 13, 2016
 */
public class SourceRowMapper implements RowMapper<Source> {
  private static SourceRowMapper instance;

  public static SourceRowMapper getInstance() {
    if (instance == null) {
      instance = new SourceRowMapper();
    }

    return instance;
  }

  @Override
  public Source mapRow(ResultSet rs, int rowNum) throws SQLException {
    Source entity = new Source();
    entity.setId(rs.getLong("id"));
    entity.setSource(rs.getString("source"));
    entity.setUrl(rs.getString("url"));
    entity.setTopicId(rs.getLong("topicId"));
    entity.setLastUpdate(rs.getLong("lastUpdate"));
    entity.setStatus(rs.getInt("status"));
    entity.setCreateTime(rs.getLong("createTime"));
    entity.setUpdateTime(rs.getLong("updateTime"));

    return entity;
  }

}

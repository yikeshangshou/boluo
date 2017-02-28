package com.boluo.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.boluo.model.Discussion;

/**
 * @author mixueqiang
 * @since Jul 6, 2016
 */
public class DiscussionRowMapper implements RowMapper<Discussion> {
  private static DiscussionRowMapper instance;

  public static DiscussionRowMapper getInstance() {
    if (instance == null) {
      instance = new DiscussionRowMapper();
    }

    return instance;
  }

  @Override
  public Discussion mapRow(ResultSet rs, int rowNum) throws SQLException {
    Discussion entity = new Discussion();
    entity.setId(rs.getLong("id"));
    entity.setUserId(rs.getLong("userId"));
    entity.setTopicId(rs.getLong("topicId"));
    entity.setItemId(rs.getLong("itemId"));
    entity.setTitle(rs.getString("title"));
    entity.setDescription(rs.getString("description"));
    entity.setLastReplyTime(rs.getLong("lastReplyTime"));
    entity.setStatus(rs.getInt("status"));
    entity.setCreateTime(rs.getLong("createTime"));
    entity.setUpdateTime(rs.getLong("updateTime"));

    return entity;
  }

}

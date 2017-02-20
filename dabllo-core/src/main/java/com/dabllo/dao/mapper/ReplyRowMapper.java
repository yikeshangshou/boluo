package com.dabllo.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.dabllo.model.Reply;

/**
 * @author mixueqiang
 * @since Jul 6, 2016
 */
public class ReplyRowMapper implements RowMapper<Reply> {
  private static ReplyRowMapper instance;

  public static ReplyRowMapper getInstance() {
    if (instance == null) {
      instance = new ReplyRowMapper();
    }

    return instance;
  }

  @Override
  public Reply mapRow(ResultSet rs, int rowNum) throws SQLException {
    Reply entity = new Reply();
    entity.setId(rs.getLong("id"));
    entity.setUserId(rs.getLong("userId"));
    entity.setDiscussionId(rs.getLong("discussionId"));
    entity.setContent(rs.getString("content"));
    entity.setStatus(rs.getInt("status"));
    entity.setCreateTime(rs.getLong("createTime"));
    entity.setUpdateTime(rs.getLong("updateTime"));

    return entity;
  }

}

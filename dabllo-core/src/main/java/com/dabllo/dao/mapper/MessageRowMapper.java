package com.dabllo.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.dabllo.model.Message;

/**
 * @author mixueqiang
 * @since Mar 8, 2014
 */
public class MessageRowMapper implements RowMapper<Message> {
  private static MessageRowMapper instance;

  public static MessageRowMapper getInstance() {
    if (instance == null) {
      instance = new MessageRowMapper();
    }
    return instance;
  }

  @Override
  public Message mapRow(ResultSet rs, int row) throws SQLException {
    Message entity = new Message();
    entity.setId(rs.getLong("id"));
    entity.setFrom(rs.getLong("from"));
    entity.setTo(rs.getLong("to"));
    entity.setTitle(rs.getString("title"));
    entity.setContent(rs.getString("content"));
    entity.setStatus(rs.getInt("status"));
    entity.setCreateTime(rs.getLong("createTime"));
    entity.setUpdateTime(rs.getLong("updateTime"));

    return entity;
  }

}

package com.boluo.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.boluo.model.Comment;

/**
 * @author mixueqiang
 * @since May 26, 2016
 */
public class CommentRowMapper implements RowMapper<Comment> {
  private static CommentRowMapper instance;

  public static CommentRowMapper getInstance() {
    if (instance == null) {
      instance = new CommentRowMapper();
    }

    return instance;
  }

  @Override
  public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
    Comment entity = new Comment();
    entity.setId(rs.getLong("id"));
    entity.setUserId(rs.getLong("userId"));
    entity.setFeedId(rs.getLong("feedId"));
    entity.setReplyId(rs.getLong("replyId"));
    entity.setReplyUserId(rs.getLong("replyUserId"));
    entity.setContent(rs.getString("content"));
    entity.setStatus(rs.getInt("status"));
    entity.setCreateTime(rs.getLong("createTime"));
    entity.setUpdateTime(rs.getLong("updateTime"));

    return entity;
  }

}

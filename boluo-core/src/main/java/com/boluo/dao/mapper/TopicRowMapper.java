package com.boluo.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.boluo.model.Topic;

/**
 * @author mixueqiang
 * @since Feb 22, 2016
 */
public class TopicRowMapper implements RowMapper<Topic> {
  private static TopicRowMapper instance;

  public static TopicRowMapper getInstance() {
    if (instance == null) {
      instance = new TopicRowMapper();
    }

    return instance;
  }

  @Override
  public Topic mapRow(ResultSet rs, int rowNum) throws SQLException {
    Topic entity = new Topic();
    entity.setId(rs.getLong("id"));
    entity.setUserId(rs.getLong("userId"));
    entity.setName(rs.getString("name"));
    entity.setTitle(rs.getString("title"));
    entity.setDescription(rs.getString("description"));
    entity.setImage(rs.getString("image"));
    entity.setLink(rs.getString("link"));
    entity.setLastUpdateTime(rs.getLong("lastUpdateTime"));
    entity.setSelected(rs.getInt("selected"));
    entity.setUserCount(rs.getInt("userCount"));
    entity.setItemCount(rs.getInt("itemCount"));
    entity.setRank(rs.getInt("rank"));
    entity.setStatus(rs.getInt("status"));
    entity.setCreateTime(rs.getLong("createTime"));
    entity.setUpdateTime(rs.getLong("updateTime"));

    return entity;
  }

}

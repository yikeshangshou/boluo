package com.boluo.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.boluo.model.Feed;
import com.boluo.util.DateUtils;

/**
 * @author xueqiangmi
 * @since Jun 11, 2015
 */
public class FeedRowMapper implements RowMapper<Feed> {
  private static FeedRowMapper instance;

  public static FeedRowMapper getInstance() {
    if (instance == null) {
      instance = new FeedRowMapper();
    }

    return instance;
  }

  @Override
  public Feed mapRow(ResultSet rs, int rowNum) throws SQLException {
    Feed entity = new Feed();
    entity.setId(rs.getLong("id"));
    entity.setUserId(rs.getLong("userId"));
    entity.setTopicId(rs.getLong("topicId"));
    // entity.setUsername(rs.getString("username"));
    entity.setTitle(rs.getString("title"));
    entity.setImage(rs.getString("image"));
    entity.setPlace(rs.getString("place"));
    entity.setLink(rs.getString("link"));
    entity.setDescription(rs.getString("description"));
    entity.setLongitude(rs.getBigDecimal("longitude"));
    entity.setLatitude(rs.getBigDecimal("latitude"));
    entity.setSource(rs.getString("source"));
    entity.setHidden(rs.getInt("hidden"));
    entity.setCommentCount(rs.getInt("commentCount"));
    entity.setLikeCount(rs.getInt("likeCount"));
    entity.setStatus(rs.getInt("status"));
    entity.setCreateTime(rs.getLong("createTime"));
    entity.setUpdateTime(rs.getLong("updateTime"));

    long time = rs.getLong("createTime");
    entity.getProperties().put("timeBefore", DateUtils.convertToReadableDate(time));

    return entity;
  }

}

package com.dabllo.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.dabllo.model.Article;
import com.dabllo.util.DateUtils;

/**
 * @author mixueqiang
 * @since Jul 6, 2016
 */
public class ArticleRowMapper implements RowMapper<Article> {
  private static ArticleRowMapper instance;

  public static ArticleRowMapper getInstance() {
    if (instance == null) {
      instance = new ArticleRowMapper();
    }

    return instance;
  }

  @Override
  public Article mapRow(ResultSet rs, int rowNum) throws SQLException {
    Article entity = new Article();
    entity.setId(rs.getLong("id"));
    entity.setUserId(rs.getLong("userId"));
    entity.setSource(rs.getString("source"));
    entity.setTitle(rs.getString("title"));
    entity.setImage(rs.getString("image"));
    entity.setDescription(rs.getString("description"));
    entity.setLink(rs.getString("link"));
    entity.setRank(rs.getInt("rank"));
    entity.setRelatedId(rs.getLong("relatedId"));
    entity.setPublishTime(rs.getLong("publishTime"));
    entity.setHot(rs.getInt("hot"));
    entity.setStatus(rs.getInt("status"));
    entity.setCreateTime(rs.getLong("createTime"));
    entity.setUpdateTime(rs.getLong("updateTime"));

    long time = rs.getLong("publishTime");
    entity.getProperties().put("timeBefore", DateUtils.convertToReadableDate(time));

    return entity;
  }

}

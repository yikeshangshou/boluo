package com.boluo.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.boluo.model.Item;
import com.boluo.util.DateUtils;

/**
 * @author mixueqiang
 * @since Jul 6, 2016
 */
public class ItemRowMapper implements RowMapper<Item> {
  private static ItemRowMapper instance;

  public static ItemRowMapper getInstance() {
    if (instance == null) {
      instance = new ItemRowMapper();
    }

    return instance;
  }

  @Override
  public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
    Item entity = new Item();
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

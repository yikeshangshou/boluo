package com.boluo.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.boluo.model.Category;

/**
 * @author mixueqiang
 * @since Apr 29, 2016
 */
public class CategoryRowMapper implements RowMapper<Category> {
  private static CategoryRowMapper instance;

  public static CategoryRowMapper getInstance() {
    if (instance == null) {
      instance = new CategoryRowMapper();
    }

    return instance;
  }

  @Override
  public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
    Category entity = new Category();
    entity.setId(rs.getLong("id"));
    entity.setParentId(rs.getLong("parentId"));
    entity.setSlug(rs.getString("slug"));
    entity.setName(rs.getString("name"));
    entity.setImage(rs.getString("image"));
    entity.setStatus(rs.getInt("status"));
    entity.setCreateTime(rs.getLong("createTime"));
    entity.setUpdateTime(rs.getLong("updateTime"));

    return entity;
  }

}

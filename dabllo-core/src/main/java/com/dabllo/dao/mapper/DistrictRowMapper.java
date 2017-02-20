package com.dabllo.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.dabllo.model.District;

/**
 * @author mixueqiang
 * @since Jan 12, 2015
 */
public class DistrictRowMapper implements RowMapper<District> {
  private static DistrictRowMapper instance;

  public static DistrictRowMapper getInstance() {
    if (instance == null) {
      instance = new DistrictRowMapper();
    }
    return instance;
  }

  @Override
  public District mapRow(ResultSet rs, int rowNum) throws SQLException {
    District entity = new District();
    entity.setId(rs.getLong("id"));
    entity.setDistrictId(rs.getLong("districtId"));
    entity.setParentId(rs.getLong("parentId"));
    entity.setName(rs.getString("name"));
    entity.setNameEn(rs.getString("nameEn"));
    entity.setShortName(rs.getString("shortName"));
    entity.setLevel(rs.getInt("level"));
    entity.setOrderNumber(rs.getInt("orderNumber"));
    entity.setStatus(rs.getInt("status"));
    entity.setCreateTime(rs.getLong("createTime"));
    entity.setUpdateTime(rs.getLong("updateTime"));

    return entity;
  }

}

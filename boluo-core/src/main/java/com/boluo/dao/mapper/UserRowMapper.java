package com.boluo.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.boluo.model.User;

/**
 * @author mixueqiang
 * @since Mar 11, 2014
 */
public class UserRowMapper implements RowMapper<User> {
  private static UserRowMapper instance;

  public static UserRowMapper getInstance() {
    if (instance == null) {
      instance = new UserRowMapper();
    }
    return instance;
  }

  @Override
  public User mapRow(ResultSet rs, int rowNum) throws SQLException {
    User entity = new User();
    entity.setId(rs.getLong("id"));
    entity.setPhone(rs.getString("phone"));
    entity.setEmail(rs.getString("email"));
    entity.setUsername(rs.getString("username"));
    entity.setPassword(rs.getString("password"));
    entity.setFirstName(rs.getString("firstName"));
    entity.setLastName(rs.getString("lastName"));
    entity.setAvatar(rs.getString("avatar"));
    entity.setGender(rs.getString("gender"));
    entity.setBirthday(rs.getString("birthday"));
    entity.setCity(rs.getString("city"));
    entity.setProfile(rs.getString("profile"));
    entity.setLocale(rs.getString("locale"));
    entity.setRoles(rs.getString("roles"));
    entity.setSecurityCode(rs.getString("securityCode"));
    entity.setBindStatus(rs.getInt("bindStatus"));
    entity.setStatus(rs.getInt("status"));
    entity.setCreateTime(rs.getLong("createTime"));
    entity.setUpdateTime(rs.getLong("updateTime"));

    return entity;
  }
}

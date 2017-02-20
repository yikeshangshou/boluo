package com.dabllo.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.dabllo.model.Ticket;

/**
 * @author mixueqiang
 * @since Jul 23, 2016
 */
public class TicketRowMapper implements RowMapper<Ticket> {
  private static TicketRowMapper instance;

  public static TicketRowMapper getInstance() {
    if (instance == null) {
      instance = new TicketRowMapper();
    }

    return instance;
  }

  @Override
  public Ticket mapRow(ResultSet rs, int rowNum) throws SQLException {
    Ticket entity = new Ticket();
    entity.setId(rs.getLong("id"));
    entity.setUserId(rs.getLong("userId"));
    entity.setContact(rs.getString("contact"));
    entity.setContent(rs.getString("content"));
    entity.setStatus(rs.getInt("status"));
    entity.setCreateTime(rs.getLong("createTime"));
    entity.setUpdateTime(rs.getLong("updateTime"));

    return entity;
  }

}

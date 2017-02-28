package com.boluo.dao.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import org.springframework.jdbc.core.RowMapper;

import com.boluo.model.Entity;

/**
 * @author mixueqiang
 * @since Mar 1, 2014
 */
public class EntityRowMapper implements RowMapper<Entity> {
  private static EntityRowMapper instance;

  public static EntityRowMapper getInstance() {
    if (instance == null) {
      instance = new EntityRowMapper();
    }

    return instance;
  }

  @Override
  public Entity mapRow(ResultSet rs, int rowNum) throws SQLException {
    Entity entity = new Entity();

    // Read all column values.
    ResultSetMetaData metaData = rs.getMetaData();
    for (int i = 1; i < metaData.getColumnCount() + 1; i++) {
      String columnName = metaData.getColumnName(i);
      int columnType = metaData.getColumnType(i);

      if (Types.CHAR == columnType || Types.VARCHAR == columnType || Types.LONGVARCHAR == columnType) {
        entity.put(columnName, rs.getString(i));

      } else if (Types.SMALLINT == columnType || Types.TINYINT == columnType) {
        entity.put(columnName, rs.getInt(i));

      } else if (Types.INTEGER == columnType || Types.BIGINT == columnType) {
        entity.put(columnName, rs.getLong(i));

      } else if (Types.FLOAT == columnType) {
        entity.put(columnName, rs.getFloat(i));

      } else {
        // TODO:
      }
    }

    return entity;
  }

}

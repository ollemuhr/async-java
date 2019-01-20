package com.github.ollemuhr.user;

import com.github.ollemuhr.user.JdbcDao.Mapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.jdbi.v3.sqlobject.config.RegisterRowMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.transaction.Transaction;

@RegisterRowMapper(Mapper.class)
public interface JdbcDao {

  @Transaction
  @SqlQuery(
      "INSERT INTO users(username, firstname, lastname, email, supervisor_id) values(:username,:firstName,:lastName,:email,:supervisorId) RETURNING *;")
  Optional<UserEntity> insert(@BindBean UserEntity user);

  @SqlQuery("SELECT * FROM users WHERE username=:username;")
  Optional<UserEntity> find(@Bind("username") String username);

  final class Mapper implements RowMapper<UserEntity> {

    @Override
    public UserEntity map(ResultSet rs, StatementContext ctx) throws SQLException {
      return UserEntity.valid(
          rs.getLong("id"),
          rs.getLong("supervisor_id"),
          rs.getString("firstname"),
          rs.getString("lastname"),
          rs.getString("email"),
          rs.getString("username"));
    }
  }
}

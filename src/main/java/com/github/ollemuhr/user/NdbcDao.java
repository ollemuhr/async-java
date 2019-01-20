package com.github.ollemuhr.user;

import com.github.ollemuhr.ValidationError;
import io.trane.future.Future;
import io.trane.ndbc.DataSource;
import io.trane.ndbc.NdbcException;
import io.trane.ndbc.PreparedStatement;
import io.trane.ndbc.Row;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface NdbcDao {

  interface PS {
    PreparedStatement userById = PreparedStatement.create("SELECT * FROM users WHERE id=?;");
    PreparedStatement userByUsername =
        PreparedStatement.create("SELECT * FROM users WHERE username=?;");
    PreparedStatement allUsers = PreparedStatement.create("SELECT * FROM users;");
    PreparedStatement insertUser =
        PreparedStatement.create(
            "INSERT INTO users(username, firstname, lastname, email, supervisor_id) values(?,?,?,?,?) RETURNING *;");
    PreparedStatement insertUserWithBug =
        PreparedStatement.create(
            "INSERT INTO users(boom, firstname, lastname, email, supervisor_id) values(?,?,?,?,?);");
    PreparedStatement updateUser =
        PreparedStatement.create(
            "UPDATE users SET username=?, firstname=?, lastname=?, email=?, supervisor_id=? WHERE id=?;");
  }

  static Future<Optional<UserEntity>> insertUser(
      final DataSource<PreparedStatement, Row> ds, final UserEntity u) {
    return ds.query(params(PS.insertUser, u, false)).map(NdbcDao::mapUser);
  }

  static Future<Optional<UserEntity>> findByUserName(
      final DataSource<PreparedStatement, Row> ds, final String username) {
    return ds.query(PS.userByUsername.setString(username)).map(NdbcDao::mapUser);
  }

  static PreparedStatement updateUser(final UserEntity u) {
    return params(PS.updateUser, u, true);
  }

  private static Optional<UserEntity> mapUser(final List<Row> rows) {
    return mapUsers(rows).stream().findFirst();
  }

  private static List<UserEntity> mapUsers(final List<Row> rows) {
    return rows.stream()
        .map(
            row ->
                UserEntity.valid(
                    row.getLong("id"),
                    row.isNull("supervisor_id") ? null : row.getLong("supervisor_id"),
                    row.getString("firstname"),
                    row.getString("lastname"),
                    row.getString("email"),
                    row.getString("username")))
        .collect(Collectors.toList());
  }

  private static PreparedStatement params(
      final PreparedStatement ps, final UserEntity u, final boolean addId) {
    final var ret =
        ps.setString(0, u.getUsername())
            .setString(1, u.getFirstName())
            .setString(2, u.getLastName())
            .setString(3, u.getEmail())
            .setLong(4, u.getSupervisorId());
    return addId ? ret.setLong(5, u.getId()) : ret;
  }

  private static Function<Throwable, Future<UserEntity>> handleUserExists() {
    return e -> {
      if (e instanceof NdbcException && e.getMessage().contains("users_username_key")) {
        return Future.exception(ValidationError.single("user.unique.constraint"));
      } else {
        return Future.exception(e);
      }
    };
  }
}

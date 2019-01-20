package com.github.ollemuhr.dw;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.ollemuhr.pool.HikariDataSourceFactory;
import io.dropwizard.Configuration;
import io.dropwizard.client.JerseyClientConfiguration;
import io.trane.future.CheckedFutureException;
import io.trane.ndbc.DataSource;
import io.trane.ndbc.PreparedStatement;
import io.trane.ndbc.Row;
import java.time.Duration;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class Config extends Configuration {
  @Valid
  @NotNull
  @JsonProperty("jersey-client")
  private JerseyClientConfiguration jerseyClient = new JerseyClientConfiguration();

  @JsonProperty("database")
  @Valid
  @NotNull
  private HikariDataSourceFactory database = new HikariDataSourceFactory();

  public HikariDataSourceFactory getDatabase() {
    return database;
  }

  public DataSource<PreparedStatement, Row> embedded() {
    final var config =
        io.trane.ndbc.Config.create(
                "io.trane.ndbc.postgres.netty4.DataSourceSupplier", "localhost", 0, "user")
            .database("test_schema")
            .password("test")
            .port(5432)
            .poolMaxSize(1)
            .embedded("io.trane.ndbc.postgres.embedded.EmbeddedSupplier");

    // Create a DataSource
    final var ds = DataSource.fromConfig(config);
    // Define a timeout
    final var timeout = Duration.ofSeconds(1);

    try {
      ds.execute(
              "CREATE TABLE users(\n"
                  + " id serial PRIMARY KEY,\n"
                  + " username VARCHAR (50) UNIQUE NOT NULL,\n"
                  + " firstname VARCHAR (50) NOT NULL,\n"
                  + " lastname VARCHAR (50) NOT NULL,\n"
                  + " email VARCHAR (355) UNIQUE NOT NULL,\n"
                  + " supervisor_id int4 references users(id)"
                  + ");")
          .get(timeout);
    } catch (CheckedFutureException e) {
      throw new RuntimeException(e);
    }
    return ds;
  }

  public JerseyClientConfiguration getJerseyClient() {
    return jerseyClient;
  }
}

package com.github.ollemuhr.pool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.dropwizard.db.ManagedDataSource;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

/**
 * Copied from https://github.com/nickbabcock/dropwizard-hikaricp-benchmark
 *
 * @author Olle Muhr | olle.muhr@fareoffice.com
 */
public class HikariManagedPooledDataSource extends HikariDataSource implements ManagedDataSource {
  /**
   * Instantiates a new Hikari managed pooled data source.
   *
   * @param config the config
   */
  public HikariManagedPooledDataSource(final HikariConfig config) {
    super(config);
  }

  /**
   * Returns the parent logger.
   *
   * @return the parent logger.
   * @throws SQLFeatureNotSupportedException the sql feature not supported exception
   */
  // JDK6 has JDBC 4.0 which doesn't have this -- don't add @Override
  @SuppressWarnings("override")
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    throw new SQLFeatureNotSupportedException("Doesn't use java.util.logging");
  }

  /**
   * Start.
   *
   * @throws Exception the exception
   */
  @Override
  public void start() throws Exception {}

  /**
   * Stop.
   *
   * @throws Exception the exception
   */
  @Override
  public void stop() throws Exception {
    this.close();
  }
}

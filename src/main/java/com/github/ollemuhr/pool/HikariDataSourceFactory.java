package com.github.ollemuhr.pool;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;
import com.zaxxer.hikari.HikariConfig;
import io.dropwizard.db.ManagedDataSource;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.util.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Properties;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Copied from https://github.com/nickbabcock/dropwizard-hikaricp-benchmark
 *
 * @author Olle Muhr | olle.muhr@fareoffice.com
 */
public class HikariDataSourceFactory implements PooledDataSourceFactory {
  private String datasourceClassName = null;

  private String driverClass = null;

  private String user = null;

  private String password = null;

  private boolean autoCommit = true;

  @NotNull private Map<String, String> properties = Maps.newLinkedHashMap();

  @Min(1)
  @JsonProperty
  private OptionalInt minSize = OptionalInt.empty();

  @Min(1)
  @JsonProperty
  private int maxSize = 16;

  @NotNull private String validationQuery = "/* Health Check */ SELECT 1";

  private boolean autoCommentsEnabled = true;

  private HealthCheckRegistry healthCheckRegistry;

  /**
   * Is auto comments enabled boolean.
   *
   * @return the boolean.
   */
  @JsonProperty
  @Override
  public boolean isAutoCommentsEnabled() {
    return this.autoCommentsEnabled;
  }

  /**
   * Sets the auto comments enabled.
   *
   * @param autoCommentsEnabled the auto comments enabled.
   */
  @JsonProperty
  public void setAutoCommentsEnabled(final boolean autoCommentsEnabled) {
    this.autoCommentsEnabled = autoCommentsEnabled;
  }

  /**
   * Returns the driver class.
   *
   * @return the driver class.
   */
  @JsonProperty
  @Override
  public String getDriverClass() {
    return this.driverClass;
  }

  /**
   * Returns the url.
   *
   * @return the url.
   */
  @Override
  public String getUrl() {
    return "I'm not needed";
  }

  /**
   * Sets the datasource class name.
   *
   * @param datasourceClassName the datasource class name.
   */
  @JsonProperty
  public void setDatasourceClassName(final String datasourceClassName) {
    this.datasourceClassName = datasourceClassName;
  }

  /**
   * Returns the user.
   *
   * @return the user.
   */
  @JsonProperty
  public String getUser() {
    return this.user;
  }

  /**
   * Sets the user.
   *
   * @param user the user.
   */
  @JsonProperty
  public void setUser(final String user) {
    this.user = user;
  }

  /**
   * Returns the password.
   *
   * @return the password.
   */
  @JsonProperty
  public String getPassword() {
    return this.password;
  }

  /**
   * Sets the password.
   *
   * @param password the password.
   */
  @JsonProperty
  public void setPassword(final String password) {
    this.password = password;
  }

  /**
   * Returns the properties.
   *
   * @return the properties.
   */
  @JsonProperty
  @Override
  public Map<String, String> getProperties() {
    return this.properties;
  }

  /**
   * Sets the properties.
   *
   * @param properties the properties.
   */
  @JsonProperty
  public void setProperties(final Map<String, String> properties) {
    this.properties = properties;
  }

  /**
   * Returns the validation query.
   *
   * @return the validation query.
   */
  @Override
  @JsonProperty
  public String getValidationQuery() {
    return this.validationQuery;
  }

  /**
   * Returns the health check validation query.
   *
   * @return the health check validation query.
   */
  @Override
  @Deprecated
  @JsonIgnore
  public String getHealthCheckValidationQuery() {
    return this.getValidationQuery();
  }

  /**
   * Returns the health check validation timeout.
   *
   * @return the health check validation timeout.
   */
  @Override
  @Deprecated
  @JsonIgnore
  public Optional<Duration> getHealthCheckValidationTimeout() {
    return this.getValidationQueryTimeout();
  }

  /**
   * Is auto commit boolean.
   *
   * @return the boolean.
   */
  @JsonProperty
  public boolean isAutoCommit() {
    return autoCommit;
  }

  /**
   * Sets the auto commit.
   *
   * @param autoCommit the auto commit.
   */
  @JsonProperty
  public void setAutoCommit(boolean autoCommit) {
    this.autoCommit = autoCommit;
  }

  /**
   * Returns the min size.
   *
   * @return the min size.
   */
  @JsonProperty
  public OptionalInt getMinSize() {
    return minSize;
  }

  /**
   * Returns the datasource class name.
   *
   * @return the datasource class name.
   */
  @JsonProperty
  public String getDatasourceClassName() {
    return datasourceClassName;
  }

  /**
   * Sets the min size.
   *
   * @param minSize the min size.
   */
  @JsonProperty
  public void setMinSize(OptionalInt minSize) {
    this.minSize = minSize;
  }

  /**
   * Returns the max size.
   *
   * @return the max size.
   */
  @JsonProperty
  public int getMaxSize() {
    return maxSize;
  }

  /**
   * Sets the max size.
   *
   * @param maxSize the max size.
   */
  @JsonProperty
  public void setMaxSize(int maxSize) {
    this.maxSize = maxSize;
  }

  /** As single connection pool. */
  @Override
  public void asSingleConnectionPool() {
    this.minSize = OptionalInt.empty();
    this.maxSize = 1;
  }

  /**
   * Returns the health check registry.
   *
   * @return the health check registry.
   */
  public HealthCheckRegistry getHealthCheckRegistry() {
    return healthCheckRegistry;
  }

  /**
   * Sets the health check registry.
   *
   * @param healthCheckRegistry the health check registry.
   */
  public void setHealthCheckRegistry(HealthCheckRegistry healthCheckRegistry) {
    this.healthCheckRegistry = healthCheckRegistry;
  }

  /**
   * Sets the driver class.
   *
   * @param driverClass the driver class.
   */
  public void setDriverClass(String driverClass) {
    this.driverClass = driverClass;
  }

  /**
   * Build managed data source.
   *
   * @param metricRegistry the metric registry.
   * @param name the name.
   * @return the managed data source.
   */
  @Override
  public ManagedDataSource build(final MetricRegistry metricRegistry, final String name) {
    final Properties properties = new Properties();
    for (final Map.Entry<String, String> property : this.properties.entrySet()) {
      properties.setProperty(property.getKey(), property.getValue());
    }

    final HikariConfig config = new HikariConfig();
    config.setMetricRegistry(metricRegistry);
    if (healthCheckRegistry != null) {
      config.setHealthCheckRegistry(healthCheckRegistry);
    }

    config.setAutoCommit(autoCommit);
    config.setDataSourceProperties(properties);
    if (datasourceClassName != null) {
      config.setDataSourceClassName(datasourceClassName);
    } else {
      config.setDriverClassName(driverClass);
    }

    config.setMaximumPoolSize(maxSize);
    minSize.ifPresent(config::setMinimumIdle);
    config.setPoolName(name);
    config.setUsername(user);
    config.setPassword(user != null && password == null ? "" : password);
    return new HikariManagedPooledDataSource(config);
  }

  /**
   * Returns the validation query timeout.
   *
   * @return the validation query timeout.
   */
  @Override
  @JsonProperty
  public Optional<Duration> getValidationQueryTimeout() {
    return Optional.of(Duration.minutes(1));
  }
}

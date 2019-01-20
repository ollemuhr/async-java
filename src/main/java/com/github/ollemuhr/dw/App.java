package com.github.ollemuhr.dw;

import com.github.ollemuhr.user.JdbcDao;
import com.github.ollemuhr.user.UserResource;
import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.configuration.UrlConfigurationSourceProvider;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.trane.ndbc.DataSource;
import io.trane.ndbc.PreparedStatement;
import io.trane.ndbc.Row;
import org.jdbi.v3.core.Jdbi;

public class App extends Application<Config> {

  /**
   * The entry point of application.
   *
   * @param args the input arguments
   * @throws Exception the exception
   */
  public static void main(String[] args) throws Exception {
    new App().run("server", resourceFilePath("/config.yaml"));
  }

  @Override
  public void run(final Config configuration, final Environment environment) {
    final DataSource<PreparedStatement, Row> embedded = configuration.embedded();
    final JdbiFactory factory = new JdbiFactory();
    final Jdbi jdbi = factory.build(environment, configuration.getDatabase(), "postgresql");
    final JdbcDao userDao = jdbi.onDemand(JdbcDao.class);
    environment.jersey().register(new UserResource(embedded, userDao));
  }

  /**
   * Initialize.
   *
   * @param bootstrap the bootstrap.
   */
  @Override
  public void initialize(final Bootstrap<Config> bootstrap) {
    bootstrap.setConfigurationSourceProvider(
        new SubstitutingSourceProvider(
            new UrlConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(false)));
  }

  /**
   * Resource file path string.
   *
   * @param resourceClassPathLocation the resource class path location.
   * @return the string.
   */
  public static String resourceFilePath(final String resourceClassPathLocation) {
    try {
      return App.class.getResource(resourceClassPathLocation).toString();
    } catch (final Exception e) {
      throw new RuntimeException(e);
    }
  }
}

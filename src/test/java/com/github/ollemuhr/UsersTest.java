package com.github.ollemuhr;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.ollemuhr.dw.App;
import com.github.ollemuhr.dw.Config;
import com.github.ollemuhr.user.User;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.testing.junit5.DropwizardAppExtension;
import io.dropwizard.testing.junit5.DropwizardExtensionsSupport;
import io.trane.future.CheckedFutureException;
import io.trane.future.Future;
import io.trane.future.Promise;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.glassfish.jersey.client.rx.RxClient;
import org.glassfish.jersey.client.rx.RxWebTarget;
import org.glassfish.jersey.client.rx.java8.RxCompletionStageInvoker;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ExtendWith({DropwizardExtensionsSupport.class})
class UsersTest {
  private static final Logger LOGGER = LoggerFactory.getLogger("UsersTest");
  private static final DropwizardAppExtension<Config> app =
      new DropwizardAppExtension<>(App.class, App.resourceFilePath("/config.yaml"));

  private static RxWebTarget<RxCompletionStageInvoker> target;
  private static RxClient<RxCompletionStageInvoker> client;

  @BeforeAll
  static void beforeAll() {

    var timeout = io.dropwizard.util.Duration.seconds(10);
    var jerseyClientConfiguration = new JerseyClientConfiguration();

    jerseyClientConfiguration.setWorkQueueSize(1024);
    jerseyClientConfiguration.setMaxThreads(Runtime.getRuntime().availableProcessors() + 1);
    jerseyClientConfiguration.setTimeout(timeout);
    jerseyClientConfiguration.setConnectionRequestTimeout(timeout);
    jerseyClientConfiguration.setConnectionTimeout(timeout);

    client =
        new JerseyClientBuilder(app.getEnvironment())
            .using(jerseyClientConfiguration)
            .buildRx("client", RxCompletionStageInvoker.class);

    target = client.target("http://localhost:" + app.getLocalPort());
  }

  @AfterAll
  static void afterAll() {
    client.close();
  }


  @Test
  void ndbc() throws CheckedFutureException, JsonProcessingException {
    doIt("ndbc");
  }

  private void doIt(final String path) throws CheckedFutureException {
    var timeout = Duration.ofSeconds(30);
    post(user(0, path), path).get(timeout);
    var n = 1000;
    var start = System.currentTimeMillis();
    var posted =
        IntStream.range(1, n).mapToObj(i -> post(user(i, path), path)).collect(Collectors.toList());

    var responses = Future.collect(posted).get(timeout);

    responses.forEach(r -> Assert.assertEquals(201, r.getStatus()));

    final var users =
        IntStream.range(0, n).mapToObj(i -> get(i, path)).collect(Collectors.toList());

    final var stored = Future.collect(users).get(timeout);

    System.out.println("time: " + (System.currentTimeMillis() - start));
    //    stored.forEach(r -> System.out.println(r.readEntity(User.class)));
  }

  private Future<Response> post(final User user, final String path) {
    var completionStage =
        target
            .path("users")
            .path(path)
            .request()
            .header("X-Request-Id", UUID.randomUUID().toString())
            .rx()
            .post(Entity.entity(user, MediaType.APPLICATION_JSON_TYPE));
    return fromCompletable(completionStage);
  }

  private Future<Response> get(final int i, final String path) {
    var completionStage =
        target
            .path("users")
            .path(path)
            .path(Integer.toString(i))
            .request(MediaType.APPLICATION_JSON_TYPE)
            .header("X-Request-Id", UUID.randomUUID().toString())
            .rx()
            .get();
    return fromCompletable(completionStage);
  }

  private User user(final int i, final String path) {
    return new User(
        null, i > 1 ? 1L : null, "first", "last", path + i + "a@b.se", path + "username" + i);
  }

  static <T> Future<T> fromCompletable(final CompletionStage<T> completable) {
    final Promise<T> future = Promise.apply();
    completable
        .thenAccept(future::setValue)
        .exceptionally(
            t -> {
              future.setException(t);
              return null;
            });
    return future;
  }
}

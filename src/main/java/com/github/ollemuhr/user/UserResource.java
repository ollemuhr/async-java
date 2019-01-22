package com.github.ollemuhr.user;

import com.github.ollemuhr.log.EventLogger;
import com.github.ollemuhr.log.EventType;
import io.trane.future.Future;
import io.trane.ndbc.DataSource;
import io.trane.ndbc.PreparedStatement;
import io.trane.ndbc.Row;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import org.slf4j.MDC;

@Path("users")
public class UserResource {

  private final Map<String, User> m = new HashMap<>();
  private final DataSource<PreparedStatement, Row> dataSource;
  private final JdbcDao userDao;

  public UserResource(final DataSource<PreparedStatement, Row> dataSource, final JdbcDao userDao) {
    this.userDao = userDao;
    this.dataSource = dataSource;
  }

  @Path("inmem/{id}")
  @GET
  public Response inmemGet(@PathParam("id") final Long id) {
    return Response.ok(m.get("inmemusername" + id)).build();
  }

  @Path("inmem")
  @POST
  public Response inmemAdd(final User user) {
    m.put(user.getUsername(), user);
    return Response.created(UriBuilder.fromResource(UserResource.class).build()).build();
  }

  @Path("jdbc/{id}")
  @GET
  public Response jdbcGet(@PathParam("id") final Long id) {
    return ok(userDao.find("jdbcusername" + id));
  }

  @Path("ndbc/{id}")
  @GET
  public void ndbcGet(
      @Suspended final AsyncResponse asyncResponse, @PathParam("id") final Long id) {
    asyncResponse.setTimeout(10, TimeUnit.SECONDS);
    NdbcDao.findByUserName(dataSource, "ndbcusername" + id)
        .map(UserResource::ok)
        .onFailure(asyncResponse::resume)
        .onSuccess(asyncResponse::resume);
  }

  @Path("jdbc")
  @POST
  public Response jdbcAdd(final User user) {
    return created(userDao.insert(UserEntity.valid(user)));
  }

  @Path("ndbc")
  @POST
  public void ndbcAdd(@Suspended final AsyncResponse asyncResponse, final User user) {
    asyncResponse.setTimeout(10, TimeUnit.SECONDS);
    NdbcDao.insertUser(dataSource, UserEntity.valid(user))
        .map(UserResource::created)
        .onFailure(asyncResponse::resume)
        .onSuccess(asyncResponse::resume);
  }

  private static Response ok(final Optional<UserEntity> user) {
    return user.map(User::new)
        .map(u -> Response.ok(u).build())
        .orElseGet(() -> Response.status(Status.NOT_FOUND).build());
  }

  private static Response created(final Optional<UserEntity> u) {
    return u.map(User::new)
        .map(
            user -> {
              EventLogger.log(EventType.USER_ADDED, user);
              return user;
            })
        .map(user -> Response.created(location(user).build()))
        .map(ResponseBuilder::build)
        .orElseGet(() -> Response.serverError().build());
  }

  private static UriBuilder location(final User u) {
    return UriBuilder.fromResource(UserResource.class).path(Long.toString(u.getId()));
  }

  static <T> CompletableFuture<T> toCompletable(final Future<T> future) {
    final CompletableFuture<T> completable = new CompletableFuture<>();
    future.onSuccess(completable::complete).onFailure(completable::completeExceptionally);
    return completable;
  }
}

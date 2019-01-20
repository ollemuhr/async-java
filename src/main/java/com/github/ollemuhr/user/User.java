package com.github.ollemuhr.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

/** A user that can only be created using static 'valid' method. */
public class User {

  @JsonProperty private final Long id;
  @JsonProperty private final String firstName;
  @JsonProperty private final String lastName;
  @JsonProperty private final String email;
  @JsonProperty private final Long supervisorId;
  @JsonProperty private final String username;

  public User() {
    this(null, null, null, null, null, null);
  }

  public User(
      final Long id,
      final Long supervisorId,
      final String firstName,
      final String lastName,
      final String email,
      final String username) {
    this.id = id;
    this.supervisorId = supervisorId;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.username = username;
  }

  public User(final UserEntity u) {
    this(
        u.getId(),
        u.getSupervisorId(),
        u.getFirstName(),
        u.getLastName(),
        u.getEmail(),
        u.getUsername());
  }

  /**
   * A user with id.
   *
   * @param id the id.
   * @return the copied user with an id.
   */
  public User withId(final Long id) {
    return new User(
        id,
        this.getSupervisorId(),
        this.getFirstName(),
        this.getLastName(),
        this.getEmail(),
        this.getUsername());
  }

  public String getEmail() {
    return email;
  }

  public Long getSupervisorId() {
    return supervisorId;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getUsername() {
    return username;
  }

  public Long getId() {
    return id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, firstName, lastName, email, supervisorId, username);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final User other = (User) obj;
    return Objects.equals(this.id, other.id)
        && Objects.equals(this.firstName, other.firstName)
        && Objects.equals(this.lastName, other.lastName)
        && Objects.equals(this.email, other.email)
        && Objects.equals(this.supervisorId, other.supervisorId)
        && Objects.equals(this.username, other.username);
  }

  @Override
  public String toString() {
    return "User{"
        + "id="
        + id
        + ", firstName='"
        + firstName
        + '\''
        + ", lastName='"
        + lastName
        + '\''
        + ", email='"
        + email
        + '\''
        + ", supervisorId="
        + supervisorId
        + ", username='"
        + username
        + '\''
        + '}';
  }
}

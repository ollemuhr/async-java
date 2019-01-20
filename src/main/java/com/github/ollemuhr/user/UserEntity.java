package com.github.ollemuhr.user;

import com.github.ollemuhr.ValidationError;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/** A user that can only be created using static 'valid' method. */
public class UserEntity {

  private final Long id;
  private final String firstName;
  private final String lastName;
  private final String email;
  private final Long supervisorId;
  private final String username;

  public static UserEntity valid(
      final Long id,
      final Long supervisorId,
      final String firstName,
      final String lastName,
      final String email,
      final String username) {
    return Valid.user(id, supervisorId, firstName, lastName, email, username);
  }

  public static UserEntity valid(final com.github.ollemuhr.user.User u) {
    return Valid.user(
        u.getId(),
        u.getSupervisorId(),
        u.getFirstName(),
        u.getLastName(),
        u.getEmail(),
        u.getUsername());
  }

  private UserEntity(
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

  /**
   * A user with id.
   *
   * @param id the id.
   * @return the copied user with an id.
   */
  public UserEntity withId(final Long id) {
    return new UserEntity(
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
    final UserEntity other = (UserEntity) obj;
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

  /** Some silly validation for a User. */
  private interface Valid {

    Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    Pattern namePattern = Pattern.compile("^[A-Za-z0-9+_.-]+$");

    static UserEntity user(
        final Long id,
        final Long supervisorId,
        final String firstName,
        final String lastName,
        final String email,
        final String username) {
      final var errors = new ArrayList<String>();
      validFirstName(firstName, errors);
      validLastName(lastName, errors);
      validEmail(email, errors);
      validUsername(username, errors);
      if (!errors.isEmpty()) {
        throw new ValidationError(errors);
      }
      return new UserEntity(id, supervisorId, firstName, lastName, email, username);
    }

    static void validFirstName(final String s, final List<String> errors) {
      if (!namePattern.matcher(s).matches()) {
        errors.add("user.firstName.invalid");
      }
    }

    static void validLastName(final String s, final List<String> errors) {
      if (!namePattern.matcher(s).matches()) {
        errors.add("user.lastName.invalid");
      }
    }

    static void validEmail(final String s, final List<String> errors) {
      if (!emailPattern.matcher(s).matches()) {
        errors.add("user.email.invalid");
      }
    }

    static void validUsername(final String s, final List<String> errors) {
      if (s == null || s.length() < 4) {
        errors.add("user.username.minlen");
      }
    }
  }
}

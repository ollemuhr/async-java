package com.github.ollemuhr;

import java.util.Collections;
import java.util.List;

public class ValidationError extends RuntimeException {

  private final List<String> errors;

  public ValidationError(final List<String> errors) {
    super(String.join(",", errors));
    this.errors = errors;
  }

  public static ValidationError single(final String msg) {
    return new ValidationError(Collections.singletonList(msg));
  }

  public List<String> errors() {
    return errors;
  }
}

package com.github.ollemuhr.log;

public enum EventType {
  USER_ADDED("user-added");
  private final String type;

  EventType(final String s) {
    this.type = s;
  }

  public String type() {
    return type;
  }
}

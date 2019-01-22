package com.github.ollemuhr.log;

public class Event {

  private final String type;
  private final Object object;

  Event(String type, Object object) {
    this.type = type;
    this.object = object;
  }

  String getType() {
    return type;
  }

  Object getObject() {
    return object;
  }
}

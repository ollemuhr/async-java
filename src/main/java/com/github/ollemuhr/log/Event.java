package com.github.ollemuhr.log;

public class Event {

  private final String type;
  private final Object object;

  public Event(String type, Object object) {
    this.type = type;
    this.object = object;
  }

  public String getType() {
    return type;
  }

  public Object getObject() {
    return object;
  }
}

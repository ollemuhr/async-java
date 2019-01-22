package com.github.ollemuhr.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventLogger {

  private static final Logger LOGGER = LoggerFactory.getLogger("EventLogger");

  public static void log(final EventType eventType, final Object o) {
    LOGGER.info("", new Event(eventType.type(), o));
  }
}

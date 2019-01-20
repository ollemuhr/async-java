package com.github.ollemuhr.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventLogger {

  private static final Logger LOGGER = LoggerFactory.getLogger("EventLogger");

  public static void log(final Event event) {
    LOGGER.info("", event);
  }
}

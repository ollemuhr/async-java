package com.github.ollemuhr.log;

import ch.qos.logback.classic.pattern.ThrowableHandlingConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.logging.json.EventAttribute;
import io.dropwizard.logging.json.layout.EventJsonLayout;
import io.dropwizard.logging.json.layout.JsonFormatter;
import io.dropwizard.logging.json.layout.TimestampFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

class AppEventJsonLayout extends EventJsonLayout {

  private static final ObjectMapper objectMapper = Jackson.newObjectMapper();

  AppEventJsonLayout(
      final JsonFormatter jsonFormatter,
      final TimestampFormatter timestampFormatter,
      final ThrowableHandlingConverter throwableProxyConverter,
      final Set<EventAttribute> includes,
      final Map<String, String> customFieldNames,
      final Map<String, Object> additionalFields,
      final Set<String> includesMdcKeys,
      final boolean flattenMdc) {
    super(
        jsonFormatter,
        timestampFormatter,
        throwableProxyConverter,
        includes,
        customFieldNames,
        additionalFields,
        includesMdcKeys,
        flattenMdc);
  }

  @Override
  protected Map<String, Object> toJsonMap(final ILoggingEvent event) {
    final Map<String, Object> map = super.toJsonMap(event);
    if (event.getArgumentArray() != null) {
      Stream.of(event.getArgumentArray())
          .filter(o -> (o instanceof Event))
          .findFirst()
          .map(o -> (Event) o)
          .ifPresent(e -> map.put(e.getType(), toMap(e)));
    }
    return map;
  }

  private Map<String, Object> toMap(final Event e) {
    try {
      return objectMapper.convertValue(e.getObject(), new TypeReference<Map<String, Object>>() {});
    } catch (final RuntimeException ex) {
      var m = new HashMap<String, Object>();
      m.put("error", ex.getMessage());
      return m;
    }
  }
}

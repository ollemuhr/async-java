package com.github.ollemuhr.log;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.dropwizard.logging.json.EventJsonLayoutBaseFactory;
import io.dropwizard.logging.json.layout.EventJsonLayout;
import java.util.TimeZone;

@JsonTypeName("app-json")
public class AppEventJsonLayoutBaseFactory extends EventJsonLayoutBaseFactory {
  @Override
  public LayoutBase<ILoggingEvent> build(final LoggerContext context, final TimeZone timeZone) {
    final EventJsonLayout jsonLayout =
        new AppEventJsonLayout(
            createDropwizardJsonFormatter(),
            createTimestampFormatter(timeZone),
            createThrowableProxyConverter(),
            getIncludes(),
            getCustomFieldNames(),
            getAdditionalFields(),
            getIncludesMdcKeys(),
            isFlattenMdc());
    jsonLayout.setContext(context);
    return jsonLayout;
  }
}

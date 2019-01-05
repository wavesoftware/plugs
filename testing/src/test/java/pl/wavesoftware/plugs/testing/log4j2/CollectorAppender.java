/*
 * Copyright (c) 2019 Wave Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.wavesoftware.plugs.testing.log4j2;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/**
 * A collector appender
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@Plugin(
  name = "Collector",
  category = "Core",
  elementType = "appender",
  printObject = true
)
public final class CollectorAppender extends AbstractAppender {
  private final CollectorManager manager;

  private CollectorAppender(
    String name,
    Layout<? extends Serializable> layout,
    @Nullable Filter filter,
    CollectorManager manager,
    boolean ignoreExceptions
  ) {
    super(name, filter, layout, ignoreExceptions);
    this.manager = manager;
  }

  /**
   * Factory method for Log4j2
   *
   * @param name             a name
   * @param ignoreExceptions do ignore exceptions
   * @param layout           a Log4j2 layout like pattern layout
   * @param filter           a Log4j2 filter
   * @return a created instance
   */
  @PluginFactory
  public static CollectorAppender createAppender(
    @Nullable @PluginAttribute("name") String name,
    @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
    @Nullable @PluginElement("Layout") Layout<? extends Serializable> layout,
    @Nullable @PluginElement("Filters") Filter filter
  ) {

    if (name == null) {
      LOGGER.error("No name provided for CollectorAppender");
      return null;
    }

    CollectorManager manager = CollectorManager.getCollectorManager(name);
    if (layout == null) {
      layout = PatternLayout.createDefaultLayout();
    }
    return new CollectorAppender(name, layout, filter, manager, ignoreExceptions);
  }

  @Override
  public void append(LogEvent event) {
    byte[] bytes = getLayout().toByteArray(event);
    String formattedMessage = new String(bytes, StandardCharsets.UTF_8);
    manager.collect(event, formattedMessage);
  }
}

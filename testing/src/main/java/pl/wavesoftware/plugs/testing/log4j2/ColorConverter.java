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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;
import pl.wavesoftware.plugs.testing.ansi.AnsiBackground;
import pl.wavesoftware.plugs.testing.ansi.AnsiColor;
import pl.wavesoftware.plugs.testing.ansi.AnsiComposite;
import pl.wavesoftware.plugs.testing.ansi.AnsiElement;
import pl.wavesoftware.plugs.testing.ansi.AnsiOutput;
import pl.wavesoftware.plugs.testing.ansi.AnsiStyle;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Log4j2 {@link LogEventPatternConverter} colors output using the {@link AnsiOutput}
 * class. A single option 'styling' can be provided to the converter, or if not specified
 * color styling will be picked based on the logging level.
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@Plugin(name = "color", category = PatternConverter.CATEGORY)
@ConverterKeys({"clr", "color"})
public final class ColorConverter extends LogEventPatternConverter {

  private static final Map<String, AnsiElement> ELEMENTS;

  static {
    Map<String, AnsiElement> ansiElements = new HashMap<>();
    ansiElements.put("faint", AnsiColor.WHITE);
    ansiElements.put("red", AnsiColor.RED);
    ansiElements.put("green", AnsiColor.GREEN);
    ansiElements.put("yellow", AnsiColor.YELLOW);
    ansiElements.put("blue", AnsiColor.BLUE);
    ansiElements.put("magenta", AnsiColor.MAGENTA);
    ansiElements.put("cyan", AnsiColor.CYAN);
    ELEMENTS = Collections.unmodifiableMap(ansiElements);
  }

  private static final Map<Integer, AnsiElement> LEVELS;

  static {
    Map<Integer, AnsiElement> ansiLevels = new HashMap<>();
    ansiLevels.put(Level.FATAL.intLevel(), AnsiComposite.of(
      AnsiColor.BLACK, AnsiBackground.RED
    ));
    ansiLevels.put(Level.ERROR.intLevel(), AnsiComposite.of(
      AnsiColor.RED, AnsiStyle.BOLD
    ));
    ansiLevels.put(Level.WARN.intLevel(), AnsiColor.YELLOW);
    ansiLevels.put(Level.INFO.intLevel(), AnsiColor.GREEN);
    ansiLevels.put(Level.DEBUG.intLevel(), AnsiColor.BLUE);
    ansiLevels.put(Level.TRACE.intLevel(), AnsiColor.CYAN);
    LEVELS = Collections.unmodifiableMap(ansiLevels);
  }

  private final List<PatternFormatter> formatters;

  private final AnsiElement styling;

  private ColorConverter(List<PatternFormatter> formatters, AnsiElement styling) {
    super("style", "style");
    this.formatters = formatters;
    this.styling = styling;
  }

  /**
   * Creates a new instance of the class. Required by Log4J2.
   *
   * @param config  the configuration
   * @param options the options
   * @return a new instance, or {@code null} if the options are invalid
   */
  public static ColorConverter newInstance(Configuration config, String[] options) {
    if (options.length < 1) {
      LOGGER.error("Incorrect number of options on style. "
        + "Expected at least 1, received {}", options.length);
      return null;
    }
    if (options[0] == null) {
      LOGGER.error("No pattern supplied on style");
      return null;
    }
    PatternParser parser = PatternLayout.createPatternParser(config);
    List<PatternFormatter> formatters = parser.parse(options[0]);
    AnsiElement element = (options.length != 1) ? ELEMENTS.get(options[1]) : null;
    return new ColorConverter(formatters, element);
  }

  @Override
  public boolean handlesThrowable() {
    for (PatternFormatter formatter : this.formatters) {
      if (formatter.handlesThrowable()) {
        return true;
      }
    }
    return super.handlesThrowable();
  }

  @Override
  public void format(LogEvent event, StringBuilder toAppendTo) {
    StringBuilder buf = new StringBuilder();
    for (PatternFormatter formatter : this.formatters) {
      formatter.format(event, buf);
    }
    if (buf.length() > 0) {
      AnsiElement element = this.styling;
      if (element == null) {
        // Assume highlighting
        element = LEVELS.get(event.getLevel().intLevel());
        element = (element != null) ? element : AnsiColor.GREEN;
      }
      appendAnsiString(toAppendTo, buf.toString(), element);
    }
  }

  private void appendAnsiString(
    StringBuilder toAppendTo,
    String input,
    AnsiElement element
  ) {
    toAppendTo.append(AnsiOutput.toString(element, input));
  }

}

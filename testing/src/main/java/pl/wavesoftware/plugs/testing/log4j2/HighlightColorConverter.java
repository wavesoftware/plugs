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

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
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
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import pl.wavesoftware.plugs.testing.ansi.AnsiBackground;
import pl.wavesoftware.plugs.testing.ansi.AnsiColor;
import pl.wavesoftware.plugs.testing.ansi.AnsiComposite;
import pl.wavesoftware.plugs.testing.ansi.AnsiElement;
import pl.wavesoftware.plugs.testing.ansi.AnsiOutput;
import pl.wavesoftware.plugs.testing.ansi.AnsiStyle;

import javax.annotation.Nullable;
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
@Plugin(name = "hightlight-color", category = PatternConverter.CATEGORY)
@ConverterKeys("hl")
public final class HighlightColorConverter extends LogEventPatternConverter {

  private static final Map<String, AnsiElement> ELEMENTS;
  private static final int NO_ARGS_WITHOUT_STYLE = 1;
  private static final int NO_ARGS_WITH_STYLE = 2;
  private static final Set<Integer> VALID_NUMBER_OF_ARGS = HashSet.of(
    NO_ARGS_WITHOUT_STYLE,
    NO_ARGS_WITH_STYLE
  );

  static {
    Map<String, AnsiElement> ansiElements = new HashMap<>();
    ansiElements.put("faint", AnsiColor.WHITE);
    ansiElements.put("black", AnsiColor.BLACK);
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

  @Nullable
  private final AnsiElement styling;

  private HighlightColorConverter(
    List<PatternFormatter> formatters,
    @Nullable AnsiElement styling
  ) {
    super("style", "style");
    this.formatters = Collections.unmodifiableList(formatters);
    this.styling = styling;
  }

  /**
   * Creates a new instance of the class. Required by Log4j.
   *
   * @param config  the configuration
   * @param options the options
   * @return a new instance, or {@code null} if the options are invalid
   */
  @SuppressWarnings("WeakerAccess")
  @API(status = Status.MAINTAINED)
  public static HighlightColorConverter newInstance(
    Configuration config,
    String[] options
  ) {
    if (!VALID_NUMBER_OF_ARGS.contains(options.length)) {
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
    AnsiElement element = null;
    if (options.length != 1) {
      element = ELEMENTS.get(options[1]);
    }
    return new HighlightColorConverter(formatters, element);
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
  public void format(LogEvent event, StringBuilder stringBuilder) {
    StringBuilder buf = new StringBuilder();
    for (PatternFormatter formatter : this.formatters) {
      formatter.format(event, buf);
    }
    if (buf.length() > 0) {
      AnsiElement ansiElement = this.styling;
      if (ansiElement == null) {
        // Assume highlighting
        ansiElement = LEVELS.getOrDefault(
          event.getLevel().intLevel(), AnsiColor.BLUE
        );
      }
      appendAnsiString(stringBuilder, buf.toString(), ansiElement);
    }
  }

  private static void appendAnsiString(
    StringBuilder toAppendTo,
    String text,
    AnsiElement ansiElement
  ) {
    toAppendTo.append(AnsiOutput.encode(ansiElement, text));
  }

}

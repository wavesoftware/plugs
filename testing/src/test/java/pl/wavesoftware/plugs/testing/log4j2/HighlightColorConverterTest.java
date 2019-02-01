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
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.status.StatusLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.wavesoftware.plugs.testing.log4j2.CollectorManager.CollectedEvent;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
class HighlightColorConverterTest {

  @Nested
  class ActualLoggingWithSlf4j {
    private static final int EXPECTED_SIZE = 2;
    private final CollectorManager manager =
      CollectorManager.getCollectorManager("PseudoConsole");

    @AfterEach
    void after() {
      manager.clear();
    }

    @Test
    void format() {
      // given
      Logger logger = LoggerFactory.getLogger(HighlightColorConverterTest.class);

      // when
      logger.info("info");
      logger.error("error");

      // then
      assertThat(manager.getCollected())
        .hasSize(EXPECTED_SIZE)
        .extracting(this::formattedMessageOf)
        .containsExactly(
          "\u001B[32m INFO\u001B[0;39m \u001B[33m[      main]\u001B[0;39m " +
            "\u001B[36mp.w.p.t.l.HighlightColorConverterTest   " +
            "\u001B[0;39m \u001B[37;2m:\u001B[0;39m info\n",
          "\u001B[31;1mERROR\u001B[0;39m \u001B[33m[      main]\u001B[0;39m " +
            "\u001B[36mp.w.p.t.l.HighlightColorConverterTest   " +
            "\u001B[0;39m \u001B[37;2m:\u001B[0;39m error\n"
        );
    }

    private String formattedMessageOf(CollectedEvent event) {
      return event.getFormattedMessage().replace("\r\n", "\n");
    }
  }

  @ExtendWith(MockitoExtension.class)
  @Nested
  class NewInstance {

    @Mock
    private Configuration configuration;
    private Level level;

    @BeforeEach
    void before() {
      level = StatusLogger.getLogger().getLevel();
      StatusLogger.getLogger().setLevel(Level.OFF);
    }

    @AfterEach
    void after() {
      StatusLogger.getLogger().setLevel(level);
      Mockito.validateMockitoUsage();
      Mockito.verifyNoMoreInteractions(configuration);
    }

    @ParameterizedTest
    @ArgumentsSource(NewInstanceArgumentSource.class)
    void newInstance(String[] options) {
      // when
      HighlightColorConverter instance = HighlightColorConverter.newInstance(
        configuration, options
      );

      // then
      assertThat(instance).isNull();
    }
  }

  private static final class NewInstanceArgumentSource implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
      return Stream.of(
        new Options(new String[0]),
        new Options(new String[] { null, "red" }),
        new Options(new String[] { "color me", "black", "red"})
      );
    }
  }

  private static final class Options implements Arguments {

    private final Object[] value;

    private Options(String[] value) {
      this.value = new Object[] { value.clone() };
    }

    @Override
    public Object[] get() {
      return value.clone();
    }
  }

}

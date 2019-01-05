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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.wavesoftware.plugs.testing.log4j2.CollectorManager.CollectedEvent;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
class HighlightColorConverterTest {

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
      .extracting(CollectedEvent::getFormattedMessage)
      .containsExactly(
        "\u001B[32m INFO\u001B[0;39m \u001B[33m[      main]" +
          "\u001B[0;39m \u001B[36mp.w.p.t.l.HighlightColorConverterTest" +
          "   \u001B[0;39m \u001B[37m:\u001B[0;39m info\n",
        "\u001B[31;1mERROR\u001B[0;39m \u001B[33m[      main]" +
          "\u001B[0;39m \u001B[36mp.w.p.t.l.HighlightColorConverterTest" +
          "   \u001B[0;39m \u001B[37m:\u001B[0;39m error\n"
      );
  }

}

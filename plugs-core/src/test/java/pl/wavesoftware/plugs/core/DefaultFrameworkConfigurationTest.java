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

package pl.wavesoftware.plugs.core;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
class DefaultFrameworkConfigurationTest {

  @Test
  void fromDefault() {
    // when
    FrameworkConfiguration configuration = new DefaultFrameworkConfiguration();

    // then
    assertThat(configuration.asMap()).isEmpty();
  }

  @Test
  void fromProperties() {
    // given
    Properties properties = new Properties();
    properties.setProperty("Alice", "Bob");

    // when
    FrameworkConfiguration configuration = new DefaultFrameworkConfiguration(
      properties
    );

    // then
    assertThat(
      configuration.asMap()
        .toList()
        .map(objects -> objects.toSeq()
          .map(Object::toString)
          .intersperse("-")
          .foldLeft(new StringBuilder(), StringBuilder::append)
          .toString()
        )
    ).containsExactly("Alice-Bob");
  }

}

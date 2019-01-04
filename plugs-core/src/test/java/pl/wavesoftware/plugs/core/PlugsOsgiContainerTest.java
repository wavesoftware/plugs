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

import io.vavr.collection.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import slf4jtest.LogLevel;
import slf4jtest.LogMessage;
import slf4jtest.Settings;
import slf4jtest.TestLoggerFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PlugsOsgiContainerTest {

  private static final String OSGI_NAME = "Mock OSGi";

  @Mock
  private Framework framework;

  private final TestLoggerFactory loggerFactory = new TestLoggerFactory(
    new Settings().printingEnabled(false)
  );

  @AfterEach
  void after() {
    Mockito.verifyNoMoreInteractions(framework);
    Mockito.validateMockitoUsage();
  }

  @Test
  void getFramework() {
    // given
    OsgiContainer container = createContainer();

    // when
    Framework result = container.getFramework();

    // then
    assertThat(result)
      .isNotNull()
      .isSameAs(framework);
    assertThat(result)
      .extracting(Framework::getSymbolicName)
      .isEqualTo(OSGI_NAME);
    verify(framework, atLeastOnce()).getSymbolicName();
  }

  @Test
  void dispose() throws BundleException, InterruptedException {
    // given
    OsgiContainer container = createContainer();

    // when
    container.dispose();

    // then
    verify(framework).stop();
    verify(framework).waitForStop(120000L);
  }

  @Test
  void disposeWithoutEnoughTime() throws InterruptedException, BundleException {
    // given
    OsgiContainer container = createContainer();
    when(framework.waitForStop(anyLong())).thenThrow(
      new InterruptedException("20190104:202726")
    );

    // when
    container.dispose();

    // then
    verify(framework).stop();
    assertThat(loggerFactory.lines())
      .extracting(log -> log.level, PlugsOsgiContainerTest::takeFirst2Lines)
      .contains(
        tuple(
          LogLevel.ErrorLevel,
          "Not enough time to stop OSGi container, timeout was: PT2M\n" +
            "java.lang.InterruptedException: 20190104:202726"
        )
      );
  }

  private static String takeFirst2Lines(LogMessage message) {
    return List.of(message.text.split(System.lineSeparator()))
      .take(1)
      .intersperse("\n")
      .foldLeft(new StringBuilder(), StringBuilder::append)
      .toString();
  }

  private OsgiContainer createContainer() {
    Supplier<Framework> frameworkSupplier = () -> framework;
    when(framework.getSymbolicName()).thenReturn(OSGI_NAME);
    Duration stopTimeout = Duration.of(2, ChronoUnit.MINUTES);
    return new PlugsOsgiContainer(
      loggerFactory,
      frameworkSupplier,
      stopTimeout
    );
  }
}

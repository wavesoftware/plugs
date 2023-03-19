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

package pl.wavesoftware.plugs.tools.maven.plugin.io;

import org.apache.maven.plugin.logging.Log;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 2.0.0
 */
@ExtendWith(MockitoExtension.class)
class MavenLoggerTest {

  private static final String MESSAGE = "msg";
  private static final String FORMAT_ONE = "arg1: {}";
  private static final String FORMAT_TWO = "arg1: {}, arg2: {}";
  private static final String FORMAT_THREE = "arg1: {}, arg2: {}, arg3: {}";
  private static final Object ARG_ONE = 42L;
  private static final Object ARG_TWO = 43L;
  private static final Object ARG_THREE = 44L;
  private static final Throwable THROW = new IOException();

  @Mock
  private Log log;
  private final Logger logger = new MavenLogger(() -> log, MavenLoggerTest.class);

  @AfterEach
  void after() {
    Mockito.validateMockitoUsage();
    Mockito.verifyNoMoreInteractions(log);
  }

  @Nested
  class LevelEnabled extends Cases {
    @Override
    boolean isEnabled() {
      return true;
    }
  }

  @Nested
  class LevelDisabled extends Cases {
    @Override
    boolean isEnabled() {
      return false;
    }
  }

  abstract class Cases {
    abstract boolean isEnabled();

    @Test
    void isTraceEnabled() {
      // given
      when(log.isDebugEnabled()).thenReturn(isEnabled());

      // when
      boolean result = logger.isTraceEnabled();

      // then
      assertThat(result).isEqualTo(isEnabled());
      verify(log).isDebugEnabled();
    }

    @Test
    void trace() {
      // given
      when(log.isDebugEnabled()).thenReturn(isEnabled());

      // when
      ThrowingCallable throwingCallable = () -> logger.trace(MESSAGE);

      // then
      assertThatCode(throwingCallable).doesNotThrowAnyException();
      verify(log).isDebugEnabled();
      if (isEnabled()) {
        verify(log).debug(anyString());
      }
    }

    @Test
    void testTrace() {
      // given
      when(log.isDebugEnabled()).thenReturn(isEnabled());

      // when
      ThrowingCallable throwingCallable = () -> logger.trace(FORMAT_ONE, ARG_ONE);

      // then
      assertThatCode(throwingCallable).doesNotThrowAnyException();
      verify(log).isDebugEnabled();
      if (isEnabled()) {
        verify(log).debug(anyString());
      }
    }

    @Test
    void testTrace1() {
      // given
      when(log.isDebugEnabled()).thenReturn(isEnabled());

      // when
      ThrowingCallable throwingCallable = () -> logger.trace(FORMAT_TWO, ARG_ONE, ARG_TWO);

      // then
      assertThatCode(throwingCallable).doesNotThrowAnyException();
      verify(log).isDebugEnabled();
      if (isEnabled()) {
        verify(log).debug(anyString());
      }
    }

    @Test
    void testTrace2() {
      // given
      when(log.isDebugEnabled()).thenReturn(isEnabled());

      // when
      ThrowingCallable throwingCallable = () -> logger.trace(
        FORMAT_THREE, ARG_ONE, ARG_TWO, ARG_THREE
      );

      // then
      assertThatCode(throwingCallable).doesNotThrowAnyException();
      verify(log).isDebugEnabled();
      if (isEnabled()) {
        verify(log).debug(anyString());
      }
    }

    @Test
    void testTrace3() {
      // given
      when(log.isDebugEnabled()).thenReturn(isEnabled());

      // when
      ThrowingCallable throwingCallable = () -> logger.trace(MESSAGE, THROW);

      // then
      assertThatCode(throwingCallable).doesNotThrowAnyException();
      verify(log).isDebugEnabled();
      if (isEnabled()) {
        verify(log).debug(anyString(), any());
      }
    }

    @Test
    void isDebugEnabled() {
      // given
      when(log.isDebugEnabled()).thenReturn(isEnabled());

      // when
      boolean result = logger.isDebugEnabled();

      // then
      assertThat(result).isEqualTo(isEnabled());
      verify(log).isDebugEnabled();
    }

    @Test
    void debug() {
      // given
      when(log.isDebugEnabled()).thenReturn(isEnabled());

      // when
      ThrowingCallable throwingCallable = () -> logger.debug(MESSAGE);

      // then
      assertThatCode(throwingCallable).doesNotThrowAnyException();
      verify(log).isDebugEnabled();
      if (isEnabled()) {
        verify(log).debug(anyString());
      }
    }

    @Test
    void testDebug() {
      // given
      when(log.isDebugEnabled()).thenReturn(isEnabled());

      // when
      ThrowingCallable throwingCallable = () -> logger.debug(FORMAT_ONE, ARG_ONE);

      // then
      assertThatCode(throwingCallable).doesNotThrowAnyException();
      verify(log).isDebugEnabled();
      if (isEnabled()) {
        verify(log).debug(anyString());
      }
    }

    @Test
    void testDebug1() {
      // given
      when(log.isDebugEnabled()).thenReturn(isEnabled());

      // when
      ThrowingCallable throwingCallable = () -> logger.debug(FORMAT_TWO, ARG_ONE, ARG_TWO);

      // then
      assertThatCode(throwingCallable).doesNotThrowAnyException();
      verify(log).isDebugEnabled();
      if (isEnabled()) {
        verify(log).debug(anyString());
      }
    }

    @Test
    void testDebug2() {
      // given
      when(log.isDebugEnabled()).thenReturn(isEnabled());

      // when
      ThrowingCallable throwingCallable = () -> logger.debug(
        FORMAT_THREE, ARG_ONE, ARG_TWO, ARG_THREE
      );

      // then
      assertThatCode(throwingCallable).doesNotThrowAnyException();
      verify(log).isDebugEnabled();
      if (isEnabled()) {
        verify(log).debug(anyString());
      }
    }

    @Test
    void testDebug3() {
      // given
      when(log.isDebugEnabled()).thenReturn(isEnabled());

      // when
      ThrowingCallable throwingCallable = () -> logger.debug(MESSAGE, THROW);

      // then
      assertThatCode(throwingCallable).doesNotThrowAnyException();
      verify(log).isDebugEnabled();
      if (isEnabled()) {
        verify(log).debug(anyString(), any());
      }
    }

    @Test
    void isInfoEnabled() {
      // given
      when(log.isInfoEnabled()).thenReturn(isEnabled());

      // when
      boolean result = logger.isInfoEnabled();

      // then
      assertThat(result).isEqualTo(isEnabled());
      verify(log).isInfoEnabled();
    }

    @Test
    void info() {
      // given
      when(log.isInfoEnabled()).thenReturn(isEnabled());

      // when
      ThrowingCallable throwingCallable = () -> logger.info(MESSAGE);

      // then
      assertThatCode(throwingCallable).doesNotThrowAnyException();
      verify(log).isInfoEnabled();
      if (isEnabled()) {
        verify(log).info(anyString());
      }
    }

    @Test
    void testInfo() {
      // given
      when(log.isInfoEnabled()).thenReturn(isEnabled());

      // when
      ThrowingCallable throwingCallable = () -> logger.info(FORMAT_ONE, ARG_ONE);

      // then
      assertThatCode(throwingCallable).doesNotThrowAnyException();
      verify(log).isInfoEnabled();
      if (isEnabled()) {
        verify(log).info(anyString());
      }
    }

    @Test
    void testInfo1() {
      // given
      when(log.isInfoEnabled()).thenReturn(isEnabled());

      // when
      ThrowingCallable throwingCallable = () -> logger.info(FORMAT_TWO, ARG_ONE, ARG_TWO);

      // then
      assertThatCode(throwingCallable).doesNotThrowAnyException();
      verify(log).isInfoEnabled();
      if (isEnabled()) {
        verify(log).info(anyString());
      }
    }

    @Test
    void testInfo2() {
      // given
      when(log.isInfoEnabled()).thenReturn(isEnabled());

      // when
      ThrowingCallable throwingCallable = () -> logger.info(
        FORMAT_THREE, ARG_ONE, ARG_TWO, ARG_THREE
      );

      // then
      assertThatCode(throwingCallable).doesNotThrowAnyException();
      verify(log).isInfoEnabled();
      if (isEnabled()) {
        verify(log).info(anyString());
      }
    }

    @Test
    void testInfo3() {
      // given
      when(log.isInfoEnabled()).thenReturn(isEnabled());

      // when
      ThrowingCallable throwingCallable = () -> logger.info(MESSAGE, THROW);

      // then
      assertThatCode(throwingCallable).doesNotThrowAnyException();
      verify(log).isInfoEnabled();
      if (isEnabled()) {
        verify(log).info(anyString(), any());
      }
    }

    @Test
    void isWarnEnabled() {
      // given
      when(log.isWarnEnabled()).thenReturn(isEnabled());

      // when
      boolean result = logger.isWarnEnabled();

      // then
      assertThat(result).isEqualTo(isEnabled());
      verify(log).isWarnEnabled();
    }

    @Test
    void warn() {
      // given
      when(log.isWarnEnabled()).thenReturn(isEnabled());

      // when
      ThrowingCallable throwingCallable = () -> logger.warn(MESSAGE);

      // then
      assertThatCode(throwingCallable).doesNotThrowAnyException();
      verify(log).isWarnEnabled();
      if (isEnabled()) {
        verify(log).warn(anyString());
      }
    }

    @Test
    void testWarn() {
      // given
      when(log.isWarnEnabled()).thenReturn(isEnabled());

      // when
      ThrowingCallable throwingCallable = () -> logger.warn(FORMAT_ONE, ARG_ONE);

      // then
      assertThatCode(throwingCallable).doesNotThrowAnyException();
      verify(log).isWarnEnabled();
      if (isEnabled()) {
        verify(log).warn(anyString());
      }
    }

    @Test
    void testWarn1() {
      // given
      when(log.isWarnEnabled()).thenReturn(isEnabled());

      // when
      ThrowingCallable throwingCallable = () -> logger.warn(FORMAT_TWO, ARG_ONE, ARG_TWO);

      // then
      assertThatCode(throwingCallable).doesNotThrowAnyException();
      verify(log).isWarnEnabled();
      if (isEnabled()) {
        verify(log).warn(anyString());
      }
    }

    @Test
    void testWarn2() {
      // given
      when(log.isWarnEnabled()).thenReturn(isEnabled());

      // when
      ThrowingCallable throwingCallable = () -> logger.warn(
        FORMAT_THREE, ARG_ONE, ARG_TWO, ARG_THREE
      );

      // then
      assertThatCode(throwingCallable).doesNotThrowAnyException();
      verify(log).isWarnEnabled();
      if (isEnabled()) {
        verify(log).warn(anyString());
      }
    }

    @Test
    void testWarn3() {
      // given
      when(log.isWarnEnabled()).thenReturn(isEnabled());

      // when
      ThrowingCallable throwingCallable = () -> logger.warn(MESSAGE, THROW);

      // then
      assertThatCode(throwingCallable).doesNotThrowAnyException();
      verify(log).isWarnEnabled();
      if (isEnabled()) {
        verify(log).warn(anyString(), any());
      }
    }

    @Test
    void isErrorEnabled() {
      // given
      when(log.isErrorEnabled()).thenReturn(isEnabled());

      // when
      boolean result = logger.isErrorEnabled();

      // then
      assertThat(result).isEqualTo(isEnabled());
      verify(log).isErrorEnabled();
    }

    @Test
    void error() {
      // given
      when(log.isErrorEnabled()).thenReturn(isEnabled());

      // when
      ThrowingCallable throwingCallable = () -> logger.error(MESSAGE);

      // then
      assertThatCode(throwingCallable).doesNotThrowAnyException();
      verify(log).isErrorEnabled();
      if (isEnabled()) {
        verify(log).error(anyString());
      }
    }

    @Test
    void testError() {
      // given
      when(log.isErrorEnabled()).thenReturn(isEnabled());

      // when
      ThrowingCallable throwingCallable = () -> logger.error(FORMAT_ONE, ARG_ONE);

      // then
      assertThatCode(throwingCallable).doesNotThrowAnyException();
      verify(log).isErrorEnabled();
      if (isEnabled()) {
        verify(log).error(anyString());
      }
    }

    @Test
    void testError1() {
      // given
      when(log.isErrorEnabled()).thenReturn(isEnabled());

      // when
      ThrowingCallable throwingCallable = () -> logger.error(FORMAT_TWO, ARG_ONE, ARG_TWO);

      // then
      assertThatCode(throwingCallable).doesNotThrowAnyException();
      verify(log).isErrorEnabled();
      if (isEnabled()) {
        verify(log).error(anyString());
      }
    }

    @Test
    void testError2() {
      // given
      when(log.isErrorEnabled()).thenReturn(isEnabled());

      // when
      ThrowingCallable throwingCallable = () -> logger.error(FORMAT_THREE, ARG_ONE, ARG_TWO, ARG_THREE);

      // then
      assertThatCode(throwingCallable).doesNotThrowAnyException();
      verify(log).isErrorEnabled();
      if (isEnabled()) {
        verify(log).error(anyString());
      }
    }

    @Test
    void testError3() {
      // given
      when(log.isErrorEnabled()).thenReturn(isEnabled());

      // when
      ThrowingCallable throwingCallable = () -> logger.error(MESSAGE, THROW);

      // then
      assertThatCode(throwingCallable).doesNotThrowAnyException();
      verify(log).isErrorEnabled();
      if (isEnabled()) {
        verify(log).error(anyString(), any());
      }
    }
  }

}

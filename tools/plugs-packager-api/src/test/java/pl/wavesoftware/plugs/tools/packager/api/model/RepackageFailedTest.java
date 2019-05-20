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

package pl.wavesoftware.plugs.tools.packager.api.model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
class RepackageFailedTest {

  @Nested
  class Positive {
    @Test
    void check() {
      // when
      assertThatCode(() ->
        RepackageFailed.check(true).or("polite err")
      ).doesNotThrowAnyException();
    }

    @Test
    void tring() {
      // when
      assertThatCode(() ->
        RepackageFailed.tring(() -> assertThat(this).isNotNull())
          .or("polite {}", "err")
      ).doesNotThrowAnyException();
    }

    @Test
    void tringToGet() throws RepackageFailed {
      // when
      String result = RepackageFailed.tring(() -> "result")
        .or(() -> "err");

      // then
      assertThat(result).isEqualTo("result");
    }
  }

  @Nested
  class Negative {
    @Test
    void check() {
      // when
      assertThatCode(() ->
        RepackageFailed.check(false).or("err {}", 1)
      ).hasMessage("err 1");
    }

    @Test
    void tring() {
      // when
      assertThatCode(() ->
        RepackageFailed.tring((IoPossibleBlock) () -> {
          throw new IOException("io err");
        }).or(() -> "err 2")
      ).hasMessage("err 2");
    }

    @Test
    void tringToGet() {
      // when
      assertThatCode(() -> {
        RepackageFailed.tring((IoPossibleSupplier<String>) () -> {
          throw new IOException("err io");
        }).or("err 3");
        failBecauseExceptionWasNotThrown(RepackageFailed.class);
      }).hasMessage("err 3");
    }
  }
}

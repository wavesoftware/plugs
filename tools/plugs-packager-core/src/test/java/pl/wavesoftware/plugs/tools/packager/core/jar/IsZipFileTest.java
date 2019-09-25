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

package pl.wavesoftware.plugs.tools.packager.core.jar;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IsZipFileTest {

  @Nested
  @ExtendWith(MockitoExtension.class)
  class NonExistingFile {
    @Mock
    private Logger logger;

    private Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"))
      .resolve("file-utils-test");
    private Path path = tempDir.resolve(UUID.randomUUID().toString());

    @AfterEach
    void after() {
      Mockito.validateMockitoUsage();
      Mockito.verifyNoMoreInteractions(logger);
    }

    @Test
    void withDefaultConstructor() {
      // given
      IsZipFile isZipFile = new IsZipFile(path);

      // when
      boolean result = isZipFile.getAsBoolean();

      // then
      assertThat(result).isFalse();
    }

    @Test
    void withPassingLogger() {
      // given
      when(logger.isTraceEnabled()).thenReturn(true);
      IsZipFile isZipFile = new IsZipFile(path, logger);

      // when
      boolean result = isZipFile.getAsBoolean();

      // then
      assertThat(result).isFalse();
      verify(logger).isTraceEnabled();
      verify(logger).trace(matches("^Can't read file.*"),
        any(IOException.class));
    }
  }

  @Nested
  class NonZipFile {
    @Test
    void checkTestFileForBeingZip() {
      // given
      Path path = Paths.get(
        getClass()
          .getResource(IsZipFileTest.class.getSimpleName() + ".class")
          .getFile()
          .replaceFirst("/([a-zA-Z]):/", "$1:/")
      );
      IsZipFile isZipFile = new IsZipFile(path);

      // when
      boolean result = isZipFile.getAsBoolean();

      // then
      assertThat(result).isFalse();
    }
  }
}

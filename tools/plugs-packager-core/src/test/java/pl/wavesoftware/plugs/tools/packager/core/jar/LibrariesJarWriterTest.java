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

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import pl.wavesoftware.plugs.tools.packager.api.model.Library;
import pl.wavesoftware.plugs.tools.packager.api.model.LibraryScope;

import java.io.IOException;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
class LibrariesJarWriterTest {

  private final Listeners listeners = new Listeners();
  @Mock
  private Logger logger;
  @Mock
  private JarEntryWriter entryWriter;

  @AfterEach
  void after() {
    Mockito.validateMockitoUsage();
    Mockito.verifyNoMoreInteractions(logger, entryWriter);
  }

  @Test
  void writeLibrary() throws IOException {
    // given
    when(logger.isTraceEnabled()).thenReturn(true);
    LibrariesJarWriter jarWriter = new LibrariesJarWriter(
      logger, () -> listeners, entryWriter
    );
    Library library = new Library(Paths.get("/path/to/guava.jar"), LibraryScope.RUNTIME);
    String destination = "PLUGS-INF";

    // when
    ThrowingCallable throwingCallable = () -> jarWriter.writeLibrary(destination, library);

    // then
    assertThatCode(throwingCallable)
      .hasCauseInstanceOf(IOException.class)
      .hasMessageContaining("/path/to/guava.jar")
      .hasMessageContaining("20190924:230826");
    verify(logger).isTraceEnabled();
    verify(logger).trace(
      matches("^Can't read a supposed JAR file.+"), any(IOException.class)
    );
  }
}

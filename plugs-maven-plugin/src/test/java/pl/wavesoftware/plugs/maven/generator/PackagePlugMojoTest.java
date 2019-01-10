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

package pl.wavesoftware.plugs.maven.generator;

import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@ExtendWith({
  MojoRuleExtension.class,
  MockitoExtension.class
})
class PackagePlugMojoTest {

  @Mock
  private Log log;

  @AfterEach
  void after() {
    Mockito.verifyNoMoreInteractions(log);
    Mockito.validateMockitoUsage();
  }

  @Test
  void execute(MojoBuilderFactory factory) {
    // given
    Path pomDirectory = Paths.get("simpliest");
    PackagePlugMojo mojo = factory
      .builder(PackagePlugMojo.class)
      .withPomDirectory(pomDirectory)
      .build(PackagePlugMojo.GOAL);
    mojo.setLog(log);

    // when & then
    assertThatCode(mojo::execute).doesNotThrowAnyException();
    verify(log).debug(eq("Worker is: default"));
  }
}

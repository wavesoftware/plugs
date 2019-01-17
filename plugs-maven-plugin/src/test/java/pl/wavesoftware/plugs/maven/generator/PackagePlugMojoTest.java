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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.wavesoftware.maven.junit5.MojoBuilderFactory;
import pl.wavesoftware.maven.junit5.MojoRuleExtension;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.verify;
import static pl.wavesoftware.eid.utils.EidExecutions.tryToExecute;
import static pl.wavesoftware.eid.utils.EidPreconditions.checkNotNull;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@ExtendWith({
  MojoRuleExtension.class,
  MockitoExtension.class
})
class PackagePlugMojoTest {

  private final Path pomDirectory = Paths.get("simpliest");

  @Mock
  private Log log;

  @BeforeEach
  void before() throws URISyntaxException, IOException {
    Path testClasses =
      new File(checkNotNull(
        getClass().getClassLoader().getResource("."),
        "20190117:004601"
      ).toURI()).toPath();
    Path target = testClasses.resolve(pomDirectory).resolve("target");
    if (target.toFile().exists()) {
      Files.walk(target)
        .sorted(Comparator.reverseOrder())
        .forEach(path -> tryToExecute(() ->
          Files.deleteIfExists(path), "20190117:005048")
        );
    }
  }

  @AfterEach
  void after() {
    Mockito.verifyNoMoreInteractions(log);
    Mockito.validateMockitoUsage();
  }

  @Test
  void execute(MojoBuilderFactory factory) {
    // given
    PackagePlugMojo mojo = factory
      .builder(PackagePlugMojo.class)
      .withPomDirectory(pomDirectory)
      .build(PackagePlugMojo.GOAL);
    mojo.setLog(log);

    // when & then
    assertThatCode(mojo::execute).doesNotThrowAnyException();
    verify(log).info(contains("simpliest/target/simpliest-0.1.0-plug.jar was successful."));
    verify(log).info(contains("Artifact attached to the build"));
  }
}

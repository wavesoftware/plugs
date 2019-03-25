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

package pl.wavesoftware.plugs.tools.maven.plugin;

import org.apache.maven.cli.logging.Slf4jLogger;
import org.apache.maven.monitor.logging.DefaultLog;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import pl.wavesoftware.maven.testing.junit5.MojoFactory;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
class PackagePlugMojoIT {

  private static final Log DEFAULT_LOG = new DefaultLog(new Slf4jLogger(
    LoggerFactory.getLogger(PackagePlugMojoIT.class)
  ));

  @Nested
  @ExtendWith(MockitoExtension.class)
  class Simpliest extends BaseMavenTestCase {
    @Spy
    private Log log = DEFAULT_LOG;

    Simpliest() {
      super(Paths.get("simpliest"));
    }

    @AfterEach
    void after() {
      Mockito.verifyNoMoreInteractions(log);
      Mockito.validateMockitoUsage();
    }

    @Test
    @DisplayName("Execute mojo on simpliest project")
    void execute(MojoFactory factory) {
      // given
      PackagePlugMojo mojo = factory
        .customizer(getCustomizer())
        .builder(PackagePlugMojo.class)
        .withPomDirectory(getPomDirectory())
        .build(PackagePlugMojo.GOAL);
      mojo.setLog(log);

      // when & then
      assertThatCode(mojo::execute).doesNotThrowAnyException();
      verify(log, atLeastOnce()).isDebugEnabled();
      verify(log).isInfoEnabled();
      verify(log).info(contains("Building plug: "));
      verify(log).debug(contains(
        "simpliest-0.1.0-plg.jar was successful."
      ));
      verify(log).debug(contains("Artifact attached to the build"));
      Resource resource = new ClassPathResource(
        "/simpliest/target/simpliest-0.1.0-plg.jar"
      );
      assertThat(resource.exists()).isTrue();
    }
  }

  @Nested
  class Pom extends BaseMavenTestCase {

    Pom() {
      super(Paths.get("pom"));
    }

    @Test
    @DisplayName("Execute mojo on pom project")
    void execute(MojoFactory factory) {
      // given
      PackagePlugMojo mojo = factory
        .customizer(getCustomizer())
        .builder(PackagePlugMojo.class)
        .withPomDirectory(getPomDirectory())
        .build(PackagePlugMojo.GOAL);
      mojo.setLog(DEFAULT_LOG);

      // when & then
      assertThatCode(mojo::execute).doesNotThrowAnyException();
      Resource resource = new ClassPathResource(
        "/pom/target/a-pom-0.1.0-plug.jar"
      );
      assertThat(resource.exists()).isFalse();
    }
  }

  @Nested
  class Skipped extends BaseMavenTestCase {

    Skipped() {
      super(Paths.get("skipped"));
    }

    @Test
    @DisplayName("Execute mojo on skipped project")
    void execute(MojoFactory factory) {
      // given
      PackagePlugMojo mojo = factory
        .customizer(getCustomizer())
        .builder(PackagePlugMojo.class)
        .withPomDirectory(getPomDirectory())
        .build(PackagePlugMojo.GOAL);
      mojo.setLog(DEFAULT_LOG);

      // when & then
      assertThatCode(mojo::execute).doesNotThrowAnyException();
      Resource resource = new ClassPathResource(
        "/skipped/target/skipped-0.1.0-plug.jar"
      );
      assertThat(resource.exists()).isFalse();
    }
  }

  @Nested
  class CodeWithDependencies extends BaseMavenTestCase {
    CodeWithDependencies() {
      super(Paths.get("code-with-deps"));
    }

    @Test
    @DisplayName("Execute mojo on code-with-deps project")
    void execute(MojoFactory factory) {
      // given
      PackagePlugMojo mojo = factory
        .customizer(getCustomizer())
        .builder(PackagePlugMojo.class)
        .withPomDirectory(getPomDirectory())
        .build(PackagePlugMojo.GOAL);
      mojo.setLog(DEFAULT_LOG);

      // when & then
      assertThatCode(mojo::execute).doesNotThrowAnyException();
      Resource resource = new ClassPathResource(
        "/code-with-deps/target/code-with-deps-0.1.0-plug.jar"
      );
      assertThat(resource.exists()).isTrue();
    }
  }

}

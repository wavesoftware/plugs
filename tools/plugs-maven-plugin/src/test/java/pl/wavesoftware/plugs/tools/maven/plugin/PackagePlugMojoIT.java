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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import pl.wavesoftware.maven.testing.junit5.MavenInvoker;
import pl.wavesoftware.maven.testing.junit5.MavenInvokerExtension;
import pl.wavesoftware.maven.testing.junit5.MavenProjectCustomizer;
import pl.wavesoftware.maven.testing.junit5.MojoExtension;
import pl.wavesoftware.maven.testing.junit5.MojoFactory;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static pl.wavesoftware.eid.utils.EidExecutions.tryToExecute;
import static pl.wavesoftware.eid.utils.EidPreconditions.checkNotNull;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@ExtendWith({
  MojoExtension.class,
  MavenInvokerExtension.class,
  MockitoExtension.class
})
class PackagePlugMojoIT {

  private final Path pomDirectory = Paths.get("simpliest");
  private final MavenProjectCustomizer customizer = project -> {
    File destination = fromTargetClasses(pomDirectory)
      .resolve("target")
      .resolve(project.getBuild().getFinalName() + "." + project.getPackaging())
      .toFile();
    project.getArtifact().setFile(destination);
  };

  @Spy
  private Log log = new DefaultLog(new Slf4jLogger(
    LoggerFactory.getLogger(PackagePlugMojoIT.class)
  ));

  @BeforeEach
  void before(MavenInvoker invoker) {
    invoker
      .forDirectory(fromTargetClasses(pomDirectory))
      .execute("clean", "compile", "jar:jar");
  }

  @AfterEach
  void after() {
    Mockito.verifyNoMoreInteractions(log);
    Mockito.validateMockitoUsage();
  }

  @Test
  void execute(MojoFactory factory) {
    // given
    PackagePlugMojo mojo = factory
      .customizer(customizer)
      .builder(PackagePlugMojo.class)
      .withPomDirectory(pomDirectory)
      .build(PackagePlugMojo.GOAL);
    mojo.setLog(log);

    // when & then
    assertThatCode(mojo::execute).doesNotThrowAnyException();
    verify(log, atLeastOnce()).isDebugEnabled();
    verify(log).isInfoEnabled();
    verify(log).info(contains("Building plug: "));
    verify(log).debug(contains(
      "simpliest-0.1.0-plug.jar was successful."
    ));
    verify(log).debug(contains("Artifact attached to the build"));
  }

  private static Path fromTargetClasses(Path pomDirectory) {
    Path testClasses = tryToExecute(() ->
      new File(checkNotNull(
        PackagePlugMojoIT.class.getClassLoader().getResource("."),
        "20190117:004601"
      ).toURI()).toPath(),
      "20190201:003826"
    );
    return testClasses.resolve(pomDirectory);
  }
}

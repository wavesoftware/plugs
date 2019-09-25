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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.wavesoftware.maven.testing.junit5.MavenInvoker;
import pl.wavesoftware.maven.testing.junit5.MavenInvokerExtension;
import pl.wavesoftware.maven.testing.junit5.MavenProjectCustomizer;
import pl.wavesoftware.maven.testing.junit5.MojoExtension;

import java.io.File;
import java.nio.file.Path;

import static pl.wavesoftware.eid.utils.EidExecutions.tryToExecute;
import static pl.wavesoftware.eid.utils.EidPreconditions.checkNotNull;

@ExtendWith({
  MojoExtension.class,
  MavenInvokerExtension.class
})
abstract class BaseMavenTestCase {
  private final Path pomDirectory;
  private final MavenProjectCustomizer customizer;

  BaseMavenTestCase(Path pomDirectory) {
    this.pomDirectory = pomDirectory;
    customizer = project -> {
      File destination = fromTargetClasses(pomDirectory)
        .resolve("target")
        .resolve(project.getBuild().getFinalName() + "." + project.getPackaging())
        .toFile();
      project.getArtifact().setFile(destination);
    };
  }

  @BeforeEach
  void before(MavenInvoker invoker) {
    invoker
      .forDirectory(fromTargetClasses(pomDirectory))
      .execute("clean", "package");
  }

  MavenProjectCustomizer getCustomizer() {
    return customizer;
  }

  Path getPomDirectory() {
    return pomDirectory;
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

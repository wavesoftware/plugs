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

package pl.wavesoftware.plugs.tools.maven.plugin.mapper;

import io.vavr.Lazy;
import pl.wavesoftware.plugs.tools.packager.api.model.Artifact;
import pl.wavesoftware.plugs.tools.packager.api.model.PackagerCoordinates;
import pl.wavesoftware.plugs.tools.packager.api.model.Project;

import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class MavenPackagerCoordinates implements PackagerCoordinates {
  private final Supplier<Project> projectSupplier;
  private final Lazy<Path> lazyTargetPath;
  private final Lazy<Artifact> lazySourceArtifact;

  MavenPackagerCoordinates(Supplier<Project> project) {
    this.projectSupplier = project;
    lazyTargetPath = Lazy.of(this::calculateTargetPath);
    lazySourceArtifact = Lazy.of(this::calculateSourceArtifact);
  }

  @Override
  public Artifact sourceArtifact() {
    return lazySourceArtifact.get();
  }

  @Override
  public Path targetPath() {
    return lazyTargetPath.get();
  }

  private Artifact calculateSourceArtifact() {
    return projectSupplier.get().mainArtifact();
  }

  private Path calculateTargetPath() {
    Project project = this.projectSupplier.get();
    String classifier = project.classifier().trim();
    if (!classifier.isEmpty() && !classifier.startsWith("-")) {
      classifier = "-" + classifier;
    }
    //noinspection RedundantIfStatement
    if (!project.outputPath().toFile().exists()) {
      assert project.outputPath().toFile().mkdirs();
    }
    return project.outputPath().resolve(plugFileNameFor(project, classifier));
  }

  private static String plugFileNameFor(Project project, String classifier) {
    return project.finalName()
      + classifier
      + "."
      + project.mainArtifact().type().extension();
  }
}

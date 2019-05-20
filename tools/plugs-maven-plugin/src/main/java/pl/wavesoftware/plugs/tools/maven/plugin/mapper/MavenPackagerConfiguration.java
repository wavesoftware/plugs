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
import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import pl.wavesoftware.plugs.tools.packager.api.model.PackagerConfiguration;
import pl.wavesoftware.plugs.tools.packager.api.model.PackagerCoordinates;
import pl.wavesoftware.plugs.tools.packager.api.model.Project;

import java.io.File;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class MavenPackagerConfiguration implements PackagerConfiguration {

  private final ArtifactMapper artifactMapper;
  private final Logger logger;
  private final MavenProject mavenProject;
  private final MavenSession mavenSession;
  private final Supplier<Project> project;
  private final String classifier;
  private final boolean attach;
  private final File outputDirectory;
  private final String finalName;
  private final PackagerCoordinates coordinates;

  MavenPackagerConfiguration(
    ArtifactMapper artifactMapper,
    Logger logger,
    MavenProject project,
    MavenSession session,
    String classifier,
    boolean attach,
    File outputDirectory,
    String finalName
  ) {
    this.artifactMapper = artifactMapper;
    this.logger = logger;
    this.mavenProject = project;
    this.mavenSession = session;
    this.classifier = classifier;
    this.attach = attach;
    this.outputDirectory = outputDirectory;
    this.finalName = finalName;
    this.project = Lazy.of(this::calculateProject);
    this.coordinates = new MavenPackagerCoordinates(this.project);
  }

  @Override
  public Logger logger() {
    return logger;
  }

  @Override
  public boolean shouldAttach() {
    return attach;
  }

  @Override
  public PackagerCoordinates coordinates() {
    return coordinates;
  }

  @Override
  public Project project() {
    return project.get();
  }

  private Project calculateProject() {
    return new MavenBackedProject(
      artifactMapper,
      mavenProject,
      mavenSession,
      outputDirectory.toPath(),
      finalName,
      classifier
    );
  }
}

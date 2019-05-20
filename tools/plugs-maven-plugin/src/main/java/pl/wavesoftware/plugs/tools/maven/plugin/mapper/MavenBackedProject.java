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
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import pl.wavesoftware.plugs.tools.maven.plugin.model.ResolvableDependency;
import pl.wavesoftware.plugs.tools.packager.api.model.Artifact;
import pl.wavesoftware.plugs.tools.packager.api.model.Project;

import java.nio.file.Path;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class MavenBackedProject implements Project {
  private final ArtifactMapper artifactMapper;
  private final MavenProject mavenProject;
  private final MavenSession mavenSession;
  private final Path path;
  private final String finalName;
  private final String classifier;

  private final Lazy<Set<Artifact>> dependenciesLazy;
  private final Lazy<Set<Artifact>> importsLazy;

  MavenBackedProject(
    ArtifactMapper artifactMapper,
    MavenProject mavenProject,
    MavenSession mavenSession,
    Path path,
    String finalName,
    String classifier
  ) {
    this.artifactMapper = artifactMapper;
    this.mavenProject = mavenProject;
    this.mavenSession = mavenSession;
    this.path = path;
    this.finalName = finalName;
    this.classifier = classifier;

    dependenciesLazy = Lazy.of(this::calculateDependencies);
    importsLazy = Lazy.of(this::calculateImports);
  }

  @Override
  public Path buildFilePath() {
    return mavenProject.getBasedir().toPath().resolve("pom.xml");
  }

  @Override
  public Artifact mainArtifact() {
    return artifactMapper.generalize(mavenProject.getArtifact());
  }

  @Override
  public Path outputPath() {
    return path;
  }

  @Override
  public String finalName() {
    return finalName;
  }

  @Override
  public String classifier() {
    return classifier;
  }

  @Override
  public Set<Artifact> dependencies() {
    return dependenciesLazy.get();
  }

  @Override
  public Set<Artifact> imports() {
    return importsLazy.get();
  }

  private Set<Artifact> calculateDependencies() {
    return HashSet
      .ofAll(mavenProject.getDependencies())
      .reject(MavenBackedProject::hasProvidedScope)
      .map(this::asResolvable)
      .flatMap(artifactMapper::map);
  }

  private Set<Artifact> calculateImports() {
    return HashSet
      .ofAll(mavenProject.getDependencies())
      .filter(MavenBackedProject::hasProvidedScope)
      .map(this::asResolvable)
      .flatMap(artifactMapper::map);
  }

  private ResolvableDependency asResolvable(Dependency dependency) {
    return new ResolvableDependency(dependency, mavenProject, mavenSession);
  }

  private static boolean hasProvidedScope(Dependency dependency) {
    return dependency.getScope().equals(org.apache.maven.artifact.Artifact.SCOPE_PROVIDED);
  }
}

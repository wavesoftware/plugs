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

import com.github.zafarkhaja.semver.Version;
import io.vavr.Lazy;
import org.apache.maven.model.Dependency;
import org.apache.maven.repository.RepositorySystem;
import pl.wavesoftware.plugs.tools.packager.core.model.Artifact;
import pl.wavesoftware.plugs.tools.packager.core.model.ArtifactType;

import java.nio.file.Path;

final class MavenDependency implements Artifact {
  private final RepositorySystem repositorySystem;
  private final Dependency delegate;
  private final Lazy<org.apache.maven.artifact.Artifact> artifactLazy = Lazy.of(
    this::mapAsArtifact
  );

  MavenDependency(RepositorySystem repositorySystem, Dependency dependency) {
    this.repositorySystem = repositorySystem;
    this.delegate = dependency;
  }

  @Override
  public String name() {
    return delegate.getArtifactId();
  }

  @Override
  public Version version() {
    return Version.valueOf(delegate.getVersion());
  }

  @Override
  public Path path() {
    org.apache.maven.artifact.Artifact artifact = asArtifact();
    return artifact.getFile().toPath();
  }

  @Override
  public ArtifactType type() {
    return ArtifactType.fromPackging(delegate.getType());
  }

  org.apache.maven.artifact.Artifact asArtifact() {
    return artifactLazy.get();
  }

  private org.apache.maven.artifact.Artifact mapAsArtifact() {
    return repositorySystem.createDependencyArtifact(delegate);
  }
}

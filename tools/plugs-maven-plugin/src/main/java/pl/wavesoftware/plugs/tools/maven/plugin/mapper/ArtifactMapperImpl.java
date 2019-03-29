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

import io.vavr.collection.HashSet;
import io.vavr.collection.Traversable;
import org.apache.maven.artifact.repository.DefaultRepositoryRequest;
import org.apache.maven.artifact.repository.RepositoryRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.repository.RepositorySystem;
import pl.wavesoftware.plugs.tools.maven.plugin.model.ResolvableDependency;
import pl.wavesoftware.plugs.tools.packager.core.model.Artifact;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@Named
final class ArtifactMapperImpl implements ArtifactMapper {

  private final RepositorySystem repositorySystem;
  private final ArtifactResolver artifactResolver;

  @Inject
  ArtifactMapperImpl(
    RepositorySystem repositorySystem,
    ArtifactResolver artifactResolver
  ) {
    this.repositorySystem = repositorySystem;
    this.artifactResolver = artifactResolver;
  }

  @Override
  public Artifact generalize(org.apache.maven.artifact.Artifact artifact) {
    return new MavenArtifact(artifact);
  }

  @Override
  public org.apache.maven.artifact.Artifact mavenize(Artifact artifact) {
    if (artifact instanceof MavenArtifact) {
      return ((MavenArtifact) artifact).getDelegate();
    }
    throw new UnsupportedOperationException(
      "Not supported artifact type: " + artifact.getClass()
    );
  }

  @Override
  public Traversable<Artifact> map(ResolvableDependency resolvable) {
    return resolve(resolvable).map(this::generalize);
  }

  private Traversable<org.apache.maven.artifact.Artifact> resolve(ResolvableDependency resolvable) {
    RepositoryRequest repositoryRequest = DefaultRepositoryRequest.getRepositoryRequest(
      resolvable.mavenSession(), resolvable.mavenProject()
    );
    ArtifactResolutionRequest request = new ArtifactResolutionRequest(repositoryRequest);
    request.setResolveTransitively(true);
    org.apache.maven.artifact.Artifact artifact =
      repositorySystem.createDependencyArtifact(resolvable.dependency());
    request.setArtifact(artifact);
    ArtifactResolutionResult response = artifactResolver.resolve(request);
    return HashSet.ofAll(response.getArtifacts());
  }
}

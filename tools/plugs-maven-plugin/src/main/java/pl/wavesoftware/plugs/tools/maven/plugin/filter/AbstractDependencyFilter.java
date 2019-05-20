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

package pl.wavesoftware.plugs.tools.maven.plugin.filter;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.artifact.filter.collection.AbstractArtifactsFilter;
import org.apache.maven.shared.artifact.filter.collection.ArtifactFilterException;
import org.apache.maven.shared.artifact.filter.collection.ArtifactsFilter;
import pl.wavesoftware.plugs.tools.packager.api.model.FilterableDependency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Base class for {@link ArtifactsFilter} based on a {@link FilterableDependency} list.
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @author Stephane Nicoll (Spring Boot project)
 * @author David Turanski (Spring Boot project)
 * @since 0.1.0
 */
abstract class AbstractDependencyFilter extends AbstractArtifactsFilter {
  private final List<FilterableDependency> filters;

  /**
   * Create a new instance with the list of {@link FilterableDependency} instance(s) to
   * use.
   *
   * @param dependencies the source dependencies
   */
  AbstractDependencyFilter(List<? extends FilterableDependency> dependencies) {
    this.filters = new ArrayList<>(dependencies);
  }

  @Override
  public Set<Artifact> filter(Set<Artifact> artifacts) throws ArtifactFilterException {
    Set<Artifact> result = new HashSet<>();
    for (Artifact artifact : artifacts) {
      if (!filter(artifact)) {
        result.add(artifact);
      }
    }
    return Collections.unmodifiableSet(result);
  }

  protected abstract boolean filter(Artifact artifact) throws ArtifactFilterException;

  /**
   * Check if the specified {@link org.apache.maven.artifact.Artifact} matches the
   * specified {@link FilterableDependency}. Returns
   * {@code true} if it should be excluded
   *
   * @param artifact   the Maven {@link Artifact}
   * @param dependency the {@link FilterableDependency}
   * @return {@code true} if the artifact matches the dependency
   */
  final boolean equals(
    Artifact artifact,
    FilterableDependency dependency
  ) {
    if (!dependency.groupId().equals(artifact.getGroupId())) {
      return false;
    }
    if (!dependency.artifactId().equals(artifact.getArtifactId())) {
      return false;
    }
    return (dependency.classifier() == null
      || (
      artifact.getClassifier() != null
        && dependency.classifier().equals(artifact.getClassifier())
    )
    );
  }

  final List<FilterableDependency> getFilters() {
    return Collections.unmodifiableList(filters);
  }
}

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

package pl.wavesoftware.plugs.maven.generator.filter;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.artifact.filter.collection.ArtifactFilterException;
import org.apache.maven.shared.artifact.filter.collection.FilterArtifacts;
import pl.wavesoftware.plugs.maven.generator.model.PlugsMojoException;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class DefaultFilter implements Filter {
  private final FilterArtifacts filters;

  DefaultFilter(FilterArtifacts filters) {
    this.filters = filters;
  }

  @Override
  public Set<Artifact> filterDependencies(Set<Artifact> dependencies)
    throws PlugsMojoException {
    try {
      Set<Artifact> filtered = new LinkedHashSet<>(dependencies);
      filtered.retainAll(filters.filter(dependencies));
      return filtered;
    } catch (ArtifactFilterException ex) {
      throw new PlugsMojoException(ex.getMessage(), ex);
    }
  }
}

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

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import org.apache.maven.shared.artifact.filter.collection.ArtifactFilterException;
import org.apache.maven.shared.artifact.filter.collection.FilterArtifacts;
import pl.wavesoftware.plugs.tools.maven.plugin.mapper.ArtifactMapper;
import pl.wavesoftware.plugs.tools.packager.api.model.Artifact;
import pl.wavesoftware.plugs.tools.packager.api.model.Filter;
import pl.wavesoftware.plugs.tools.packager.api.model.RepackageFailed;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class MavenFilter implements Filter {
  private final FilterArtifacts filters;
  private final ArtifactMapper artifactMapper;

  MavenFilter(FilterArtifacts filters, ArtifactMapper artifactMapper) {
    this.filters = filters;
    this.artifactMapper = artifactMapper;
  }

  @Override
  public Set<Artifact> filterDependencies(Set<Artifact> dependencies) {
    try {
      return HashSet.ofAll(
        filters.filter(dependencies.map(artifactMapper::mavenize).toJavaSet())
      ).map(artifactMapper::generalize);
    } catch (ArtifactFilterException ex) {
      throw new RepackageFailed(
        "Can't filter Maven dependencies using provided filters",
        ex
      );
    }
  }
}

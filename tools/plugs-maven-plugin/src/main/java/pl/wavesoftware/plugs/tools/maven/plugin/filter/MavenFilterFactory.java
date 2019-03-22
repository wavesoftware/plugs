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

import org.apache.maven.shared.artifact.filter.collection.FilterArtifacts;
import pl.wavesoftware.plugs.tools.maven.plugin.mapper.ArtifactMapper;
import pl.wavesoftware.plugs.tools.maven.plugin.model.Exclude;
import pl.wavesoftware.plugs.tools.maven.plugin.model.Include;
import pl.wavesoftware.plugs.tools.packager.core.model.Filter;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@Named
final class MavenFilterFactory implements FilterFactory {

  private final ArtifactMapper artifactMapper;

  @Inject
  MavenFilterFactory(ArtifactMapper artifactMapper) {
    this.artifactMapper = artifactMapper;
  }

  @Override
  public Filter create(
    @Nullable List<Include> includes,
    @Nullable List<Exclude> excludes
  ) {
    FilterArtifacts filters = getFilters(includes, excludes);
    return new MavenFilter(filters, artifactMapper);
  }

  private static FilterArtifacts getFilters(
    @Nullable List<Include> includes,
    @Nullable List<Exclude> excludes
  ) {
    FilterArtifacts filters = new FilterArtifacts();
    if (includes != null && !includes.isEmpty()) {
      filters.addFilter(new IncludeFilter(includes));
    }
    if (excludes != null && !excludes.isEmpty()) {
      filters.addFilter(new ExcludeFilter(excludes));
    }

    return filters;
  }
}
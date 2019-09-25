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

import io.vavr.collection.Set;
import org.slf4j.Logger;
import pl.wavesoftware.plugs.tools.maven.plugin.model.MavenLibraries;
import pl.wavesoftware.plugs.tools.packager.api.model.Artifact;
import pl.wavesoftware.plugs.tools.packager.api.model.Libraries;
import pl.wavesoftware.plugs.tools.packager.api.spi.LibrariesFactory;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@Named
final class MavenLibrariesFactory implements LibrariesFactory {

  private final ArtifactMapper artifactMapper;

  @Inject
  MavenLibrariesFactory(ArtifactMapper artifactMapper) {
    this.artifactMapper = artifactMapper;
  }

  @Override
  public Libraries create(Set<Artifact> artifacts, Logger logger) {
    return new MavenLibraries(
      artifacts.map(artifactMapper::mavenize),
      logger
    );
  }
}

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

import pl.wavesoftware.plugs.tools.packager.core.model.Artifact;

import javax.inject.Named;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@Named
final class ArtifactMapperImpl implements ArtifactMapper {
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
}

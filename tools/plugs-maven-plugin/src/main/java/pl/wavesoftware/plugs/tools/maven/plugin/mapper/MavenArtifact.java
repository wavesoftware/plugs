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

import com.vdurmont.semver4j.Semver;
import pl.wavesoftware.plugs.tools.packager.api.model.Artifact;
import pl.wavesoftware.plugs.tools.packager.api.model.ArtifactType;

import java.nio.file.Path;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class MavenArtifact implements Artifact {
  private final org.apache.maven.artifact.Artifact delegate;

  MavenArtifact(org.apache.maven.artifact.Artifact delegate) {
    this.delegate = delegate;
  }

  org.apache.maven.artifact.Artifact getDelegate() {
    return delegate;
  }

  @Override
  public String name() {
    return getDelegate().getArtifactId();
  }

  @Override
  public String group() {
    return getDelegate().getGroupId();
  }

  @Override
  public Semver version() {
    return new Semver(getDelegate().getVersion());
  }

  @Override
  public Path path() {
    return getDelegate().getFile().toPath();
  }

  @Override
  public ArtifactType type() {
    return ArtifactType.fromPackaging(getDelegate().getType());
  }

  @Override
  public String toString() {
    return "MavenArtifact{" +
      name() + "@" + version() +
      '}';
  }
}

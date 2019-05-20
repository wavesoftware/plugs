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

package pl.wavesoftware.plugs.tools.packager.sample.artifact.impl;

import com.vdurmont.semver4j.Semver;
import pl.wavesoftware.plugs.tools.packager.api.model.Artifact;
import pl.wavesoftware.plugs.tools.packager.api.model.ArtifactType;
import pl.wavesoftware.plugs.tools.packager.sample.fs.VirtualRoot;
import pl.wavesoftware.sampler.api.SamplerContext;

import java.nio.file.Path;

final class MavenlikeArtifact implements Artifact {

  private final SamplerContext context;
  private final String name;
  private final String group;
  private final Semver version;

  MavenlikeArtifact(
    SamplerContext context,
    String name,
    String group,
    Semver version
  ) {
    this.context = context;
    this.name = name;
    this.group = group;
    this.version = version;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Semver version() {
    return version;
  }

  @Override
  public Path path() {
    Path root = context.get(VirtualRoot.class);
    return root.resolve("home")
      .resolve("jenkins")
      .resolve(".m2")
      .resolve("repository")
      .resolve(groupAsPath())
      .resolve(name())
      .resolve(version().toString())
      .resolve(finalName());
  }

  @Override
  public ArtifactType type() {
    return ArtifactType.JAR;
  }

  private String groupAsPath() {
    return group.replace('.', '/');
  }

  private String finalName() {
    return String.format(
      "%s-%s.%s",
      name(), version(), type().extension()
    );
  }
}

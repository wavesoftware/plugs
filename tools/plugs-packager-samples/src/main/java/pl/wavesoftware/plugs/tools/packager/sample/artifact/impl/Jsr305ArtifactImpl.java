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
import pl.wavesoftware.plugs.tools.packager.sample.artifact.Jsr305Artifact;
import pl.wavesoftware.sampler.api.SamplerContext;
import pl.wavesoftware.sampler.spring.Sample;

@Sample
final class Jsr305ArtifactImpl implements Jsr305Artifact {
  private final SamplerContext context;

  Jsr305ArtifactImpl(SamplerContext context) {
    this.context = context;
  }

  @Override
  public Artifact create() {
    return new MavenlikeArtifact(
      context,
      "jsr305",
      "com.google.code.findbugs",
      new Semver("3.0.2")
    );
  }
}

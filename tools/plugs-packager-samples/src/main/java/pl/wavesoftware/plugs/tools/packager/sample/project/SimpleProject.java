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

package pl.wavesoftware.plugs.tools.packager.sample.project;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import pl.wavesoftware.plugs.tools.packager.api.model.Artifact;
import pl.wavesoftware.plugs.tools.packager.api.model.Project;
import pl.wavesoftware.plugs.tools.packager.sample.artifact.HibernateArtifact;
import pl.wavesoftware.plugs.tools.packager.sample.artifact.Jsr305Artifact;
import pl.wavesoftware.plugs.tools.packager.sample.artifact.OsgiCoreArtifact;
import pl.wavesoftware.plugs.tools.packager.sample.artifact.SimpleProjectBuildArtifact;
import pl.wavesoftware.plugs.tools.packager.sample.fs.VirtualRoot;
import pl.wavesoftware.sampler.api.Sampler;
import pl.wavesoftware.sampler.api.SamplerContext;
import pl.wavesoftware.sampler.spring.Sample;

import java.nio.file.Path;

@Sample
public final class SimpleProject implements Sampler<Project> {

  private final SamplerContext context;

  SimpleProject(SamplerContext context) {
    this.context = context;
  }

  @Override
  public Project create() {
    Path root = context.get(VirtualRoot.class);
    return new AbstractProject(root, "simple") {
      @Override
      public Artifact mainArtifact() {
        return context.get(SimpleProjectBuildArtifact.class);
      }

      @Override
      public Set<Artifact> dependencies() {
        return HashSet
          .of(HibernateArtifact.class, Jsr305Artifact.class)
          .map(context::get);
      }

      @Override
      public Set<Artifact> imports() {
        return HashSet
          .of(OsgiCoreArtifact.class)
          .map(context::get);
      }
    };
  }
}

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

package pl.wavesoftware.plugs.tools.packager.sample.artifact;

import com.vdurmont.semver4j.Semver;
import io.vavr.Lazy;
import pl.wavesoftware.plugs.tools.packager.api.model.Artifact;
import pl.wavesoftware.plugs.tools.packager.api.model.ArtifactType;
import pl.wavesoftware.plugs.tools.packager.api.model.Project;
import pl.wavesoftware.plugs.tools.packager.sample.project.SimpleProject;
import pl.wavesoftware.sampler.api.SamplerContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static pl.wavesoftware.eid.utils.EidExecutions.tryToExecute;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 2.0.0
 */
final class SimpleArtifact implements Artifact {
  private static final byte[] BYTES = {0x56, 0x45};
  private static final Instant FILE_MOD_TIME = LocalDateTime.of(
    2019, 5, 23, 20, 39
  ).toInstant(ZoneOffset.ofHours(2));

  private final Lazy<Path> artifactPath;

  SimpleArtifact(SamplerContext context) {
    artifactPath = Lazy.of(() -> {
      Project project = context.get(SimpleProject.class);
      Path path = project.outputPath().resolve(project.finalName());
      tryToExecute(() -> {
        Files.createDirectories(path.getParent());
        Files.createFile(path);
        Files.write(path, BYTES);
        Files.setLastModifiedTime(path, FileTime.from(FILE_MOD_TIME));
      }, "20190523:203722");
      return path;
    });
  }

  @Override
  public String name() {
    return "simple";
  }

  @Override
  public String group() {
    return "org.example";
  }

  @Override
  public Semver version() {
    return new Semver("1.0.0");
  }

  @Override
  public Path path() {
    return artifactPath.get();
  }

  @Override
  public ArtifactType type() {
    return ArtifactType.JAR;
  }
}

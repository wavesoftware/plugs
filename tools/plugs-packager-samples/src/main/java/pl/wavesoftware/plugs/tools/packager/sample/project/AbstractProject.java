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

import io.vavr.Lazy;
import pl.wavesoftware.plugs.tools.packager.api.model.Project;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static pl.wavesoftware.eid.utils.EidExecutions.tryToExecute;

abstract class AbstractProject implements Project {
  private static final Instant POM_MOD_TIME = LocalDateTime.of(
    2019, 5, 23, 20, 54
  ).toInstant(ZoneOffset.ofHours(2));

  private final Path projectRoot;
  private final Lazy<Path> buildFilePath;

  AbstractProject(Path root) {
    this(root, "project");
  }

  AbstractProject(Path root, String projectDir) {
    projectRoot = root.resolve("opt").resolve(projectDir);
    buildFilePath = Lazy.of(() -> {
      Path path = projectRoot.resolve("pom.xml");
      tryToExecute(() -> {
        Files.createDirectories(path.getParent());
        Files.createFile(path);
        Files.write(path, "<project />".getBytes(StandardCharsets.UTF_8));
        Files.setLastModifiedTime(path, FileTime.from(POM_MOD_TIME));
      }, "20190523:205548");
      return path;
    });
  }

  @Override
  public Path buildFilePath() {
    return buildFilePath.get();
  }

  @Override
  public Path outputPath() {
    Path path = projectRoot.resolve("target");
    tryToExecute(() -> Files.createDirectories(path), "20190523:205605");
    return path;
  }

  @Override
  public String finalName() {
    return mainArtifact().name()
      + '-' + mainArtifact().version()
      + '-' + classifier()
      + '.' + mainArtifact().type().extension();
  }

  @Override
  public String classifier() {
    return "plug";
  }

}

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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.wavesoftware.plugs.tools.packager.api.model.Artifact;
import pl.wavesoftware.plugs.tools.packager.api.model.ArtifactType;
import pl.wavesoftware.plugs.tools.packager.api.model.Project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MavenPackagerCoordinatesTest {

  @Mock
  private Project project;
  @Mock
  private Artifact artifact;

  @AfterEach
  void after() {
    Mockito.validateMockitoUsage();
    Mockito.verifyNoMoreInteractions(project, artifact);
  }

  @Test
  void targetPath() throws IOException {
    // given
    stub("plx");
    MavenPackagerCoordinates coordinates =
      new MavenPackagerCoordinates(() -> project);

    // when
    Path result1 = coordinates.targetPath();
    Path result2 = coordinates.targetPath();

    // then
    assertThat(result1.toString()).endsWith("acmeapp-plx.jar");
    assertThat(result1).isEqualTo(result2);
  }

  @Test
  void withHypenOnClassifier() throws IOException {
    // given
    stub("-plx");
    MavenPackagerCoordinates coordinates =
      new MavenPackagerCoordinates(() -> project);

    // when
    Path result = coordinates.targetPath();

    // then
    assertThat(result.toString()).endsWith("acmeapp-plx.jar");
  }

  @Test
  void withEmptyClassifier() throws IOException {
    // given
    stub("");
    MavenPackagerCoordinates coordinates =
      new MavenPackagerCoordinates(() -> project);

    // when
    Path result = coordinates.targetPath();

    // then
    assertThat(result.toString()).endsWith("acmeapp.jar");
  }

  private void stub(String classifier) throws IOException {
    when(project.classifier()).thenReturn(classifier);
    Path dir = Paths.get(
      System.getProperty("java.io.tmpdir"), "depth", "path", "inside"
    );
    Files.deleteIfExists(dir);
    when(project.outputPath()).thenReturn(dir);
    when(project.finalName()).thenReturn("acmeapp");
    when(project.mainArtifact()).thenReturn(artifact);
    when(artifact.type()).thenReturn(ArtifactType.JAR);
  }
}

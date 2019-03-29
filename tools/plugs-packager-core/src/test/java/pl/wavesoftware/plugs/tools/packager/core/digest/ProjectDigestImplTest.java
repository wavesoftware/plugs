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

package pl.wavesoftware.plugs.tools.packager.core.digest;

import com.github.zafarkhaja.semver.Version;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.wavesoftware.plugs.tools.packager.core.model.Artifact;
import pl.wavesoftware.plugs.tools.packager.core.model.ArtifactType;
import pl.wavesoftware.plugs.tools.packager.core.model.Project;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
class ProjectDigestImplTest {

  @Mock
  Project project;

  @Test
  void digest() throws IOException, URISyntaxException {
    // given
    ProjectDigest digester = new ProjectDigestImpl();
    Path buildFilePath = Paths.get(getClass().getResource("pom.xml").toURI());
    Artifact artifact = new Artifact() {
      @Override
      public String name() {
        throw new UnsupportedOperationException("Not yet implemented");
      }

      @Override
      public Version version() {
        throw new UnsupportedOperationException("Not yet implemented");
      }

      @Override
      public Path path() {
        throw new UnsupportedOperationException("Not yet implemented");
      }

      @Override
      public ArtifactType type() {
        throw new UnsupportedOperationException("Not yet implemented");
      }
    };
    when(project.buildFilePath()).thenReturn(buildFilePath);
    when(project.mainArtifact()).thenReturn(artifact);

    // when
    CharSequence digest = digester.digest(project);

    // then
    assertThat(digest).isEqualTo("22ssdd33");
  }
}

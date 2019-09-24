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

import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.repository.RepositorySystem;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.wavesoftware.plugs.tools.packager.api.model.Artifact;

import static org.assertj.core.api.Assertions.assertThatCode;

@ExtendWith(MockitoExtension.class)
class ArtifactMapperImplTest {
  @Mock
  private RepositorySystem repositorySystem;
  @Mock
  private ArtifactResolver artifactResolver;
  @Mock
  private Artifact artifact;

  @AfterEach
  void after() {
    Mockito.validateMockitoUsage();
    Mockito.verifyNoMoreInteractions(repositorySystem, artifactResolver);
  }

  @Test
  void mavenize() {
    // given
    ArtifactMapper mapper = new ArtifactMapperImpl(repositorySystem, artifactResolver);

    // when
    ThrowingCallable throwingCallable = () -> mapper.mavenize(artifact);

    // then
    assertThatCode(throwingCallable)
      .isInstanceOf(UnsupportedOperationException.class)
      .hasMessageContaining("Not supported artifact type");
  }
}

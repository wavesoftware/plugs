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

package pl.wavesoftware.plugs.tools.packager.core.manifest;

import com.vdurmont.semver4j.Semver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.wavesoftware.plugs.tools.packager.api.digest.ProjectDigester;
import pl.wavesoftware.plugs.tools.packager.api.manifest.ManifestBuilder;
import pl.wavesoftware.plugs.tools.packager.api.model.Project;
import pl.wavesoftware.plugs.tools.packager.api.model.RepackageFailed;
import pl.wavesoftware.plugs.tools.packager.sample.PackagerSamplerContext;
import pl.wavesoftware.plugs.tools.packager.sample.project.SimpleProject;
import pl.wavesoftware.sampler.api.SamplerContext;

import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static pl.wavesoftware.plugs.tools.packager.api.Constants.PLUGS_DIGEST_ATTRIBUTE;
import static pl.wavesoftware.plugs.tools.packager.api.Constants.PLUGS_VERSION_ATTRIBUTE;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PackagerSamplerContext.class)
class ManifestBuilderImplTest {

  @Mock
  private ProjectDigester digester;

  @Mock
  private JarFile sourceJar;

  @Autowired
  private SamplerContext samplerContext;

  @AfterEach
  void after() {
    Mockito.validateMockitoUsage();
    Mockito.verifyNoMoreInteractions(digester, sourceJar);
  }

  @Test
  void buildManifest() throws RepackageFailed, IOException {
    // given
    Manifest input = new Manifest();
    String hash = "1q2w3e";
    when(digester.digest(any())).thenReturn(hash);
    when(sourceJar.getManifest()).thenReturn(input, (Manifest[]) null);
    ManifestBuilder builder = new ManifestBuilderImpl(digester);
    Project project = samplerContext.get(SimpleProject.class);

    // when
    Manifest manifest1 = builder.buildManifest(project, sourceJar);
    Manifest manifest2 = builder.buildManifest(project, sourceJar);

    // then
    Attributes attributes = manifest1.getMainAttributes();
    assertThat(attributes).isNotNull();
    assertThat(new Semver(attributes.getValue(PLUGS_VERSION_ATTRIBUTE)))
      .isGreaterThan(new Semver("0.0.0"));
    assertThat(attributes.getValue(PLUGS_DIGEST_ATTRIBUTE))
      .isEqualTo(hash);

    attributes = manifest2.getMainAttributes();
    assertThat(attributes).isNotNull();
    assertThat(new Semver(attributes.getValue(PLUGS_VERSION_ATTRIBUTE)))
      .isGreaterThan(new Semver("0.0.0"));
    assertThat(attributes.getValue(PLUGS_DIGEST_ATTRIBUTE))
      .isEqualTo(hash);
  }
}

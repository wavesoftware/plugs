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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.wavesoftware.plugs.tools.packager.api.digest.ProjectDigester;
import pl.wavesoftware.plugs.tools.packager.api.model.Project;
import pl.wavesoftware.plugs.tools.packager.sample.PackagerSamplerContext;
import pl.wavesoftware.plugs.tools.packager.sample.project.SimpleProject;
import pl.wavesoftware.sampler.api.SamplerContext;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PackagerSamplerContext.class)
class ProjectDigesterImplTest {

  @Autowired
  private SamplerContext context;

  @Test
  void digest() throws IOException {
    // given
    ProjectDigester digester = new ProjectDigesterImpl();
    Project project = context.get(SimpleProject.class);

    // when
    CharSequence digest = digester.digest(project);

    // then
    assertThat(digest).isEqualTo("1is34vi");
  }
}

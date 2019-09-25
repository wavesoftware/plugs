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

package pl.wavesoftware.plugs.tools.packager.sample;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.wavesoftware.plugs.tools.packager.api.model.Project;
import pl.wavesoftware.plugs.tools.packager.sample.project.SimpleProject;
import pl.wavesoftware.sampler.api.SamplerContext;

import java.nio.file.Path;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PackagerSamplerContext.class)
class PackagerSamplerContextIT {

  @Autowired
  private SamplerContext samplerContext;

  @Test
  void get() {
    // when
    Project project = samplerContext.get(SimpleProject.class);
    Path dependency = project.dependencies()
      .filter(art -> "hibernate-core".equals(art.name()))
      .head()
      .path();

    // then
    assertThat(project).isNotNull();
    assertThat(dependency.toString()).isEqualTo(
      "/home/jenkins/.m2/repository/org/hibernate/hibernate-core/" +
        "5.4.2.Final/hibernate-core-5.4.2.Final.jar"
    );
  }

  @Test
  void differentScopes() {
    // when
    Project project1 = samplerContext.get(SimpleProject.class);
    Project project2 = samplerContext.get(SimpleProject.class);
    UUID firstId = samplerContext.controller().actualId();
    samplerContext.controller().newId();
    Project project3 = samplerContext.get(SimpleProject.class);
    Project project4 = samplerContext.get(SimpleProject.class);
    UUID secondId = samplerContext.controller().actualId();
    samplerContext.controller().setId(firstId);
    Project project5 = samplerContext.get(SimpleProject.class);
    Project project6 = samplerContext.get(SimpleProject.class);
    project6.dependencies();

    // then
    assertThat(project1).isNotNull().isSameAs(project2);
    assertThat(project3).isNotNull().isSameAs(project4);
    assertThat(project3).isNotSameAs(project1);
    assertThat(project5).isNotNull().isSameAs(project6).isSameAs(project1);
    assertThat(firstId).isNotNull().isNotEqualTo(secondId);
    assertThat(secondId).isNotNull();
  }

}

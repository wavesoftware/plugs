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

package pl.wavesoftware.plugs.maven.generator.model;

import org.apache.maven.plugins.annotations.Parameter;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @author Stephane Nicoll (Spring Boot project)
 * @author David Turanski (Spring Boot project)
 * @since 0.1.0
 */
abstract class AbstractFilterableDependency
  implements FilterableDependency {

  /**
   * The groupId of the artifact to exclude.
   *
   * @since 0.1.0
   */
  @Parameter(required = true)
  private String groupId;

  /**
   * The artifactId of the artifact to exclude.
   *
   * @since 0.1.0
   */
  @Parameter(required = true)
  private String artifactId;

  /**
   * The classifier of the artifact to exclude.
   *
   * @since 0.1.0
   */
  @Parameter
  private String classifier;

  @Override
  public String getGroupId() {
    return groupId;
  }

  @Override
  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  @Override
  public String getArtifactId() {
    return artifactId;
  }

  @Override
  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }

  @Override
  public String getClassifier() {
    return classifier;
  }

  @Override
  public void setClassifier(String classifier) {
    this.classifier = classifier;
  }
}

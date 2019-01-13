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

package pl.wavesoftware.plugs.maven.generator;

import org.apache.maven.project.MavenProject;
import pl.wavesoftware.plugs.maven.generator.model.ExecutionConfiguration;

import java.io.File;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class DefaultExecutionConfiguration
  implements ExecutionConfiguration {

  private final MavenProject project;
  private final String classifier;
  private final boolean attach;
  private final File outputDirectory;
  private final String finalName;

  DefaultExecutionConfiguration(
    MavenProject project,
    String classifier,
    boolean attach,
    File outputDirectory,
    String finalName
  ) {
    this.project = project;
    this.classifier = classifier;
    this.attach = attach;
    this.outputDirectory = outputDirectory;
    this.finalName = finalName;
  }

  @Override
  public String getFinalName() {
    return finalName;
  }

  @Override
  public String getClassifier() {
    return classifier;
  }

  @Override
  public boolean shouldAttach() {
    return attach;
  }

  @Override
  public File getOutputDirectory() {
    return outputDirectory;
  }

  @Override
  public MavenProject getMavenProject() {
    return project;
  }
}

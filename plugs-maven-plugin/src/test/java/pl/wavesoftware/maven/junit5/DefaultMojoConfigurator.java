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

package pl.wavesoftware.maven.junit5;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.project.MavenProject;

import java.nio.file.Path;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class DefaultMojoConfigurator implements MojoConfigurator {
  @Override
  public MavenSession getMavenSession(MojoRule rule, Path pomDirectory) throws Exception {
    // setup with pom
    MavenProject project = rule.readMavenProject(pomDirectory.toFile());

    // Generate session
    return rule.newMavenSession(project);
  }

  @Override
  public MojoExecution getMojoExecution(MojoRule rule, String goal) {
    // Generate Execution and Mojo for testing
    return rule.newMojoExecution(goal);
  }
}

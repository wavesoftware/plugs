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

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.testing.MojoRule;

import java.nio.file.Path;

/**
 * A configurator for a mojo to be tested
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
public interface MojoConfigurator {

  /**
   * Creates a new Maven session
   *
   * @param rule         a mojo rule
   * @param pomDirectory a directory of a pom file
   * @return a new Maven session
   */
  MavenSession getMavenSession(MojoRule rule, Path pomDirectory) throws Exception;

  /**
   * Creates a new Mojo execution
   *
   * @param rule a mojo rule
   * @param goal a goal to load mojo for
   * @return a new Mojo execution
   */
  MojoExecution getMojoExecution(MojoRule rule, String goal);
}

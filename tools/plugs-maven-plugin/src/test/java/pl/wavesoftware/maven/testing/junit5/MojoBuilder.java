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

package pl.wavesoftware.maven.testing.junit5;

import org.apache.maven.plugin.AbstractMojo;

import java.nio.file.Path;

/**
 * Builds mojo while configuring it with builder like interface
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
public interface MojoBuilder<T extends AbstractMojo> {
  /**
   * Sets a POM directory to be used
   *
   * @param pomDirectory a pom directory
   * @return self reference
   */
  MojoBuilder<T> withPomDirectory(Path pomDirectory);

  /**
   * Sets wherever to use Java resources of file system path
   *
   * @param setting a setting of resources location
   * @return self reference
   */
  MojoBuilder<T> withUsingResources(boolean setting);

  /**
   * Builds a Mojo for a given goal
   *
   * @param goal a goal to retrieve mojo for
   * @return a mojo
   */
  T build(String goal);
}

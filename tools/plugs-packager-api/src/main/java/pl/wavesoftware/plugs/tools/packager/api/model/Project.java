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

package pl.wavesoftware.plugs.tools.packager.api.model;

import io.vavr.collection.Set;

import java.nio.file.Path;

/**
 * Represents a project to be build
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
public interface Project {

  /**
   * A build file path. For ex.: pom.xml
   *
   * @return a build file path
   */
  Path buildFilePath();

  /**
   * A main artifact of a project
   *
   * @return a main artifact
   */
  Artifact mainArtifact();

  /**
   * A project output path
   *
   * @return a path to output directory
   */
  Path outputPath();

  /**
   * A final name of the artifact
   *
   * @return a final name of the artifact
   */
  String finalName();

  /**
   * A Maven classifier, by default it's {@code "plug"}
   *
   * @return a classifier
   */
  String classifier();

  /**
   * A set of dependencies of project
   *
   * @return a set of dependencies
   */
  Set<Artifact> dependencies();

  /**
   * A set of imports of project
   *
   * @return a set of imports
   */
  Set<Artifact> imports();
}

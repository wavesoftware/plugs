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

import com.vdurmont.semver4j.Semver;

import java.nio.file.Path;

/**
 * Represents a artifact of a project
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
public interface Artifact {

  /**
   * A name
   *
   * @return a name
   */
  String name();

  /**
   * A group of a artifact
   *
   * @return a group
   */
  String group();

  /**
   * A version
   *
   * @return a version
   */
  Semver version();

  /**
   * A path to a file that this artifact represents
   *
   * @return a path to a file of the artifact
   */
  Path path();

  /**
   * A type of this artifact
   * @return a type of this artifact
   */
  ArtifactType type();
}

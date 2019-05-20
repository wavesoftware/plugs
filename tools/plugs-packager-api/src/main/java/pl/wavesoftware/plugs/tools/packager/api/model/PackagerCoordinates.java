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

import java.nio.file.Path;

/**
 * A coordinates for packager operation
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
public interface PackagerCoordinates {
  /**
   * A source artifact that will be a base for re-packaging as a Plug module.
   *
   * @return a source artifact
   */
  Artifact sourceArtifact();

  /**
   * A target file to be created during packaging process.
   *
   * @return a target file to be repackaged to
   */
  Path targetPath();
}

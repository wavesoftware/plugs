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

package pl.wavesoftware.plugs.tools.packager.core;

import pl.wavesoftware.plugs.tools.packager.core.model.PackagerCoordinates;
import pl.wavesoftware.plugs.tools.packager.core.model.Project;
import pl.wavesoftware.plugs.tools.packager.core.model.RepackagingIsRequired;

/**
 * A factory of a packaging required condition
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
public interface RepackagingIsRequiredFactory {
  /**
   * Create a condition that is true if packaging is required.
   *
   * @param coordinates a coordinates of operation
   * @param project     a project
   * @return a condition
   */
  RepackagingIsRequired create(PackagerCoordinates coordinates, Project project);
}

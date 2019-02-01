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

package pl.wavesoftware.plugs.tools.packager.core.model;

import org.slf4j.Logger;

/**
 * Represents a configuration of packager
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
public interface PackagerConfiguration {
  /**
   * A logger to log messages to
   *
   * @return a logger
   */
  Logger logger();

  /**
   * Should created artifact be attached to the build
   *
   * @return true, if created artifact be attached to the build
   */
  boolean shouldAttach();

  /**
   * The coordinates, like source and target paths, to be used
   *
   * @return a coordinates
   */
  PackagerCoordinates coordinates();

  /**
   * A project representation
   *
   * @return a project representation
   */
  Project project();
}

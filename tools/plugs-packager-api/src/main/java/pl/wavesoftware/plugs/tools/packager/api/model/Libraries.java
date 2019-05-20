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

import java.io.IOException;

/**
 * Encapsulates information about libraries that may be packed into the archive.
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @author Phillip Webb (Spring Boot project)
 * @since 0.1.0
 */
@FunctionalInterface
public interface Libraries {

  /**
   * Iterate all relevant libraries.
   * @param callback a callback for each relevant library.
   * @throws IOException if the operation fails
   */
  void doWithLibraries(LibraryCallback callback) throws IOException;
}

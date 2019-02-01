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

import pl.wavesoftware.plugs.tools.packager.core.model.Or;

/**
 * <p>
 * A Plugs packager that package jar as a plug module.
 *
 * <p>
 * Code will be embedded in standard jar location. All dependencies will be
 * embedded as a internal dependencies. Dependencies in provided scope will
 * become imports in bundle manifest.
 * <p>
 * All that above means that Plug module is usable as a standard Jar, but
 * have extra parts that makes it usable as a OSGi bundle.
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
public interface Packager {
  /**
   * Repackages a artifact as a Plug module, if needed. If not needed
   * {@link Or} interface will be used to performs an alternative operation.
   *
   * @return the interface that can be used to execute commands if
   */
  Or repackage();
}

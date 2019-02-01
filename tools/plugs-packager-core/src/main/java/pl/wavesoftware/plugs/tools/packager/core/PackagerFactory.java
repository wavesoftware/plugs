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

import pl.wavesoftware.plugs.tools.packager.core.model.Filter;
import pl.wavesoftware.plugs.tools.packager.core.model.PackagerConfiguration;

/**
 * A packager factory to produce instances of {@link Packager}.
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @see Packager
 * @since 0.1.0
 */
public interface PackagerFactory {

  /**
   * Creates a packages from execution configuration and filter of dependencies.
   *
   * @param configuration a configuration of execution
   * @param filter        a filter for dependencies
   * @return a created packager instance
   */
  Packager create(PackagerConfiguration configuration, Filter filter);
}

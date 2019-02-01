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

import pl.wavesoftware.plugs.tools.packager.core.manifest.ManifestBuilder;
import pl.wavesoftware.plugs.tools.packager.core.model.Filter;
import pl.wavesoftware.plugs.tools.packager.core.model.PackagerConfiguration;
import pl.wavesoftware.plugs.tools.packager.core.model.RepackagingIsRequired;
import pl.wavesoftware.plugs.tools.packager.core.spi.LibrariesFactory;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@Named
final class DefaultPackagerFactory implements PackagerFactory {

  private final ManifestBuilder manifestBuilder;
  private final RepackagingIsRequiredFactory conditionFactory;
  private final LibrariesFactory librariesFactory;

  @Inject
  DefaultPackagerFactory(
    ManifestBuilder manifestBuilder,
    RepackagingIsRequiredFactory conditionFactory,
    LibrariesFactory librariesFactory
  ) {
    this.manifestBuilder = manifestBuilder;
    this.conditionFactory = conditionFactory;
    this.librariesFactory = librariesFactory;
  }

  @Override
  public Packager create(
    PackagerConfiguration configuration,
    Filter filter
  ) {
    RepackagingIsRequired condition = conditionFactory.create(
      configuration.coordinates()
    );
    return new DefaultPackager(
      configuration,
      condition,
      librariesFactory,
      filter,
      manifestBuilder
    );
  }
}

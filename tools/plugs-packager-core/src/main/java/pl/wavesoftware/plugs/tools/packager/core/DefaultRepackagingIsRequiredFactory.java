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

import pl.wavesoftware.plugs.tools.packager.api.RepackagingIsRequiredFactory;
import pl.wavesoftware.plugs.tools.packager.api.digest.ProjectDigester;
import pl.wavesoftware.plugs.tools.packager.api.model.PackagerCoordinates;
import pl.wavesoftware.plugs.tools.packager.api.model.Project;
import pl.wavesoftware.plugs.tools.packager.api.model.RepackagingIsRequired;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@Named
final class DefaultRepackagingIsRequiredFactory
  implements RepackagingIsRequiredFactory {

  private final ProjectDigester digest;

  @Inject
  DefaultRepackagingIsRequiredFactory(ProjectDigester digest) {
    this.digest = digest;
  }

  @Override
  public RepackagingIsRequired create(
    PackagerCoordinates coordinates, Project project
  ) {
    return new DefaultRepackagingIsRequired(coordinates, project, digest);
  }

}

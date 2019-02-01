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

package pl.wavesoftware.plugs.tools.packager.core.manifest;

import pl.wavesoftware.plugs.tools.packager.core.model.Project;
import pl.wavesoftware.plugs.tools.packager.core.model.RepackageFailed;

import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * A builder that builds a OSGi manifest for a Plug
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
public interface ManifestBuilder {

  /**
   * Builds a OSGi manifest of a Jar based on artifact information
   *
   * @param project   a project to which manifest should be created
   * @param sourceJar a source artifact jar
   * @return a OSGi manifest for a Plug
   * @throws RepackageFailed if source jar can't be reed
   */
  Manifest buildManifest(Project project, JarFile sourceJar) throws RepackageFailed;
}

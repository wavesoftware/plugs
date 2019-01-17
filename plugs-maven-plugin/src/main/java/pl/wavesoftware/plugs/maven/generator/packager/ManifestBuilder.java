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

package pl.wavesoftware.plugs.maven.generator.packager;

import io.vavr.collection.Set;
import org.apache.maven.artifact.Artifact;
import pl.wavesoftware.plugs.maven.generator.model.Library;

import java.util.jar.Manifest;

/**
 * A builder that builds a OSGi manifest for a Plug
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
interface ManifestBuilder {

  /**
   * Builds a OSGi manifest of a Jar based on artifact information
   *
   * @param artifact     an artifact
   * @param dependencies dependencies of an artifact that ware embedded in Jar
   * @param imports      a imports to be declared
   * @return a OSGi manifest for a Plug
   */
  Manifest buildManifest(
    Artifact artifact,
    Set<Library> dependencies,
    Set<Library> imports
  );
}

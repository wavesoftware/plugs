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

import org.apache.maven.artifact.Artifact;
import pl.wavesoftware.plugs.maven.generator.model.PlugsMojoException;

import java.io.File;

/**
 * A Plugs packager that repackage jar as a plug module. All dependencies will
 * be embedded as a internal dependencies.
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
public interface PlugPackager {
  /**
   * Checks where repackage action is required
   *
   * @return true, if repackage is needed
   */
  boolean needsRepackaging();

  /**
   * Repackages a artifact as a Plug module
   *
   * @throws PlugsMojoException if something goes wrong in repackage process
   */
  void repackageAsPlug() throws PlugsMojoException;

  /**
   * Gets a source artifact that will be a base for re-packaging as a Plug
   * module.
   *
   * @return a source artifact
   */
  Artifact getSourceArtifact();

  /**
   * Gets a target file to be created during packaging process.
   *
   * @return a target file to be repackaged to
   */
  File getTargetFile();
}

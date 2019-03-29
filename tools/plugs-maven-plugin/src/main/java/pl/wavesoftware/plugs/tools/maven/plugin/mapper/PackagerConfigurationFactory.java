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

package pl.wavesoftware.plugs.tools.maven.plugin.mapper;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import pl.wavesoftware.plugs.tools.packager.core.model.PackagerConfiguration;

import java.io.File;

/**
 * Creates a packager configuration based on a Maven parameters
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
public interface PackagerConfigurationFactory {
  /**
   * Creates a configuration
   *
   * @param project         a maven project
   * @param session         a maven session
   * @param logger          a logger
   * @param classifier      classifier of plug to create
   * @param attach          should created plug be attached to reactor
   * @param outputDirectory an output directory
   * @param finalName       a final name of the source artifact
   * @return a configuration
   */
  PackagerConfiguration create(
    MavenProject project,
    MavenSession session,
    Logger logger,
    String classifier,
    boolean attach,
    File outputDirectory,
    String finalName
  );
}

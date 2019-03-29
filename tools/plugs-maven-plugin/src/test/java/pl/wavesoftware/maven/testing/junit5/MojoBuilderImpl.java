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

package pl.wavesoftware.maven.testing.junit5;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.LegacySupport;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.testing.MojoRule;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static pl.wavesoftware.eid.utils.EidExecutions.tryToExecute;
import static pl.wavesoftware.eid.utils.EidPreconditions.checkNotNull;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class MojoBuilderImpl<T extends AbstractMojo>
  implements MojoBuilder<T> {

  private final MojoRule mojoRule;
  private final Class<T> type;
  private final MojoConfigurator configurator;

  private Path pomDirectory = Paths.get(".");
  private boolean usingResources = true;

  MojoBuilderImpl(MojoRule mojoRule, Class<T> type, MojoConfigurator configurator) {
    this.mojoRule = mojoRule;
    this.type = type;
    this.configurator = configurator;
  }

  @Override
  public MojoBuilder<T> withPomDirectory(Path pomDirectory) {
    this.pomDirectory = pomDirectory;
    return this;
  }

  @Override
  public MojoBuilder<T> withUsingResources(boolean setting) {
    this.usingResources = setting;
    return this;
  }

  @Override
  public T build(String goal) {
    return tryToExecute(() -> {
      MavenSession session = configurator.getMavenSession(
        mojoRule, getPomDirectory()
      );
      MojoExecution execution = configurator.getMojoExecution(
        mojoRule,
        goal
      );
      LegacySupport legacySupport = mojoRule.lookup(LegacySupport.class);
      legacySupport.setSession(session);
      org.apache.maven.plugin.Mojo mojo =
        mojoRule.lookupConfiguredMojo(session, execution);
      return type.cast(mojo);
    }, "20190111:223034");
  }

  private Path getPomDirectory() throws URISyntaxException {
    if (usingResources) {
      URL url = checkNotNull(
         type.getClassLoader().getResource(pomDirectory.toString()),
        "20190111:224609"
      );
      File file = new File(url.toURI());
      return file.toPath();
    }
    return pomDirectory;
  }
}

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

import org.apache.maven.cli.MavenCli;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
public final class MavenInvokerExtension implements ParameterResolver {

  @Override
  public boolean supportsParameter(
    ParameterContext parameterContext,
    ExtensionContext extensionContext
  ) throws ParameterResolutionException {
    return MavenInvoker.class.isAssignableFrom(
      parameterContext.getParameter().getType()
    );
  }

  @Override
  public Object resolveParameter(
    ParameterContext parameterContext,
    ExtensionContext extensionContext
  ) throws ParameterResolutionException {
    return (MavenInvoker) pomDirectory -> goals -> {
      MavenCli cli = new MavenCli();
      System.setProperty(
        MavenCli.MULTIMODULE_PROJECT_DIRECTORY, pomDirectory.toString()
      );
      int retcode = cli.doMain(
        goals, pomDirectory.toString(), System.out, System.err
      );
      System.clearProperty(MavenCli.MULTIMODULE_PROJECT_DIRECTORY);
      if (retcode != 0) {
        throw new IllegalStateException("Maven CLI failed with retcode: " + retcode);
      }
    };
  }
}

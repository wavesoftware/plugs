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

package pl.wavesoftware.plugs.maven.generator.model;

import org.apache.maven.plugin.MojoExecutionException;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A standard Plugs mojo exception
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
public final class PlugsMojoException extends MojoExecutionException {
  private static final long serialVersionUID = 20190115004728L;

  @Nullable
  private final ArrayList<BuildFailure> failures;

  public PlugsMojoException(List<BuildFailure> failures) {
    super("Multiple reasons of failure");
    this.failures = new ArrayList<>(failures);
  }

  public PlugsMojoException(String message, Throwable cause) {
    super(message, cause);
    failures = null;
  }

  public Optional<Iterable<BuildFailure>> getFailures() {
    return Optional.ofNullable(failures);
  }
}

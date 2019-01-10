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

package pl.wavesoftware.plugs.maven.generator;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;

import javax.inject.Inject;

/**
 * A main mojo to generate plug modules
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@Mojo(
  name = PackagePlugMojo.GOAL,
  defaultPhase = LifecyclePhase.PACKAGE,
  requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
  requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME
)
public final class PackagePlugMojo extends AbstractMojo {

  @SuppressWarnings("WeakerAccess")
  @API(status = Status.STABLE)
  public static final String GOAL = "package-plug";

  private final Worker worker;

  @Inject
  PackagePlugMojo(Worker worker) {
    this.worker = worker;
  }

  @Override
  public void execute() {
    getLog().debug("Worker is: " + worker);
  }
}

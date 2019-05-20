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

package pl.wavesoftware.plugs.tools.maven.plugin.model;

import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import org.apache.maven.artifact.Artifact;
import org.slf4j.Logger;
import pl.wavesoftware.plugs.tools.packager.api.model.Libraries;
import pl.wavesoftware.plugs.tools.packager.api.model.Library;
import pl.wavesoftware.plugs.tools.packager.api.model.LibraryCallback;
import pl.wavesoftware.plugs.tools.packager.api.model.LibraryScope;

import java.io.IOException;

/**
 * {@link Libraries} backed by Maven {@link Artifact}s.
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @author Phillip Webb (Spring Boot project)
 * @author Andy Wilkinson (Spring Boot project)
 * @author Stephane Nicoll (Spring Boot project)
 * @since 0.1.0
 */
public final class MavenLibraries implements Libraries {
  private static final Map<String, LibraryScope> SCOPES =
    HashMap.<String, LibraryScope>empty()
      .put(Artifact.SCOPE_COMPILE, LibraryScope.COMPILE)
      .put(Artifact.SCOPE_RUNTIME, LibraryScope.RUNTIME)
      .put(Artifact.SCOPE_PROVIDED, LibraryScope.PROVIDED)
      .put(Artifact.SCOPE_SYSTEM, LibraryScope.PROVIDED);

  private final Set<Artifact> artifacts;
  private final Logger logger;

  public MavenLibraries(
    Set<Artifact> artifacts,
    Logger logger
  ) {
    this.artifacts = artifacts;
    this.logger = logger;
  }

  @Override
  public void doWithLibraries(LibraryCallback callback) throws IOException {
    Set<String> duplicates = getDuplicates(this.artifacts);
    for (Artifact artifact : this.artifacts) {
      Option<LibraryScope> scopeOption = SCOPES.get(artifact.getScope());
      if (scopeOption.isDefined() && artifact.getFile() != null) {
        String fileName = getFileName(artifact);
        StringBuilder name = new StringBuilder(fileName);
        if (duplicates.contains(fileName)) {
          logger.debug("Duplicate found: {}", name);
          name.delete(0, name.length());
          name.append(artifact.getGroupId()).append("-").append(name);
          logger.debug("Renamed to: {}", name);
        }
        callback.library(new Library(
          name.toString(),
          artifact.getFile(),
          scopeOption.get()
        ));
      }
    }
  }

  private static Set<String> getDuplicates(Set<Artifact> artifacts) {
    java.util.Set<String> duplicates = new java.util.HashSet<>();
    java.util.Set<String> seen = new java.util.HashSet<>();
    for (Artifact artifact : artifacts) {
      String fileName = getFileName(artifact);
      if (artifact.getFile() != null && !seen.add(fileName)) {
        duplicates.add(fileName);
      }
    }
    return HashSet.ofAll(duplicates);
  }

  private static String getFileName(Artifact artifact) {
    StringBuilder sb = new StringBuilder();
    sb.append(artifact.getArtifactId()).append("-").append(artifact.getBaseVersion());
    String classifier = artifact.getClassifier();
    if (classifier != null) {
      sb.append("-").append(classifier);
    }
    sb.append(".").append(artifact.getArtifactHandler().getExtension());
    return sb.toString();
  }
}

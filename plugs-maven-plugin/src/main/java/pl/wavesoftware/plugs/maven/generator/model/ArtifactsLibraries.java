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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.logging.Log;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@link Libraries} backed by Maven {@link Artifact}s.
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @author Phillip Webb (Spring Boot project)
 * @author Andy Wilkinson (Spring Boot project)
 * @author Stephane Nicoll (Spring Boot project)
 * @since 0.1.0
 */
public final class ArtifactsLibraries implements Libraries {
  private static final Map<String, LibraryScope> SCOPES;

  static {
    Map<String, LibraryScope> libraryScopes = new HashMap<>();
    libraryScopes.put(Artifact.SCOPE_COMPILE, LibraryScope.COMPILE);
    libraryScopes.put(Artifact.SCOPE_RUNTIME, LibraryScope.RUNTIME);
    libraryScopes.put(Artifact.SCOPE_PROVIDED, LibraryScope.PROVIDED);
    libraryScopes.put(Artifact.SCOPE_SYSTEM, LibraryScope.PROVIDED);
    SCOPES = Collections.unmodifiableMap(libraryScopes);
  }

  private final Set<Artifact> artifacts;
  private final Collection<Dependency> unpacks;
  private final Log log;

  public ArtifactsLibraries(
    Set<Artifact> artifacts,
    Collection<Dependency> unpacks,
    Log log
  ) {
    this.artifacts = Collections.unmodifiableSet(artifacts);
    this.unpacks = Collections.unmodifiableCollection(unpacks);
    this.log = log;
  }

  @Override
  public void doWithLibraries(LibraryCallback callback) throws IOException {
    Set<String> duplicates = getDuplicates(this.artifacts);
    for (Artifact artifact : this.artifacts) {
      LibraryScope scope = SCOPES.get(artifact.getScope());
      if (scope != null && artifact.getFile() != null) {
        String fileName = getFileName(artifact);
        StringBuilder name = new StringBuilder(fileName);
        if (duplicates.contains(fileName)) {
          this.log.debug("Duplicate found: " + name);
          name.delete(0, name.length());
          name.append(artifact.getGroupId()).append("-").append(name);
          this.log.debug("Renamed to: " + name);
        }
        callback.library(new Library(
          name.toString(),
          artifact.getFile(),
          scope,
          isUnpackRequired(artifact)
        ));
      }
    }
  }

  private boolean isUnpackRequired(Artifact artifact) {
    if (this.unpacks != null) {
      for (Dependency unpack : this.unpacks) {
        if (artifact.getGroupId().equals(unpack.getGroupId())
          && artifact.getArtifactId().equals(unpack.getArtifactId())) {
          return true;
        }
      }
    }
    return false;
  }

  private static Set<String> getDuplicates(Set<Artifact> artifacts) {
    Set<String> duplicates = new HashSet<>();
    Set<String> seen = new HashSet<>();
    for (Artifact artifact : artifacts) {
      String fileName = getFileName(artifact);
      if (artifact.getFile() != null && !seen.add(fileName)) {
        duplicates.add(fileName);
      }
    }
    return duplicates;
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

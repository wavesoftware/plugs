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

import io.vavr.Lazy;
import org.apache.maven.artifact.Artifact;
import pl.wavesoftware.plugs.maven.generator.filter.Filter;
import pl.wavesoftware.plugs.maven.generator.model.ExecutionConfiguration;
import pl.wavesoftware.plugs.maven.generator.model.PlugsMojoException;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Set;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class PlugPackagerImpl implements PlugPackager {

  private final ExecutionConfiguration configuration;
  private final Filter filter;
  private final Lazy<File> lazyTargetFile;
  private final Lazy<Artifact> lazySourceArtifact;

  PlugPackagerImpl(ExecutionConfiguration configuration, Filter filter) {
    this.configuration = configuration;
    this.filter = filter;
    lazyTargetFile = Lazy.of(this::calculateTargetFile);
    lazySourceArtifact = Lazy.of(this::calculateSourceArtifact);
  }

  @Override
  public boolean needsRepackaging() {
    return true;
  }

  @Override
  public void repackageAsPlug() throws PlugsMojoException {
    Artifact source = getSourceArtifact();
    File target = getTargetFile();
    Set<Artifact> artifacts = filter.filterDependencies(
      configuration.getMavenProject().getArtifacts()
    );

    repackage(source, target, artifacts);
  }

  @Override
  public File getTargetFile() {
    return lazyTargetFile.get();
  }

  @Override
  public Artifact getSourceArtifact() {
    return lazySourceArtifact.get();
  }

  private void repackage(
    Artifact source,
    File target,
    Set<Artifact> artifacts
  ) {
    throw new UnsupportedOperationException("Not yet implemented");
  }

  private Artifact calculateSourceArtifact() {
    @Nullable
    Artifact sourceArtifact = getArtifact(configuration.getClassifier());
    return (sourceArtifact != null)
      ? sourceArtifact
      : configuration.getMavenProject().getArtifact();
  }

  private File calculateTargetFile() {
    String classifier = configuration.getClassifier().trim();
    if (!classifier.isEmpty() && !classifier.startsWith("-")) {
      classifier = "-" + classifier;
    }
    assert configuration.getOutputDirectory().exists()
      || configuration.getOutputDirectory().mkdirs();
    return new File(
      configuration.getOutputDirectory(),
      configuration.getFinalName()
        + classifier + "."
        + configuration.getMavenProject()
        .getArtifact()
        .getArtifactHandler()
        .getExtension()
    );
  }

  @Nullable
  private Artifact getArtifact(String classifier) {
    for (Artifact attachedArtifact : configuration.getMavenProject().getAttachedArtifacts()) {
      if (classifier.equals(attachedArtifact.getClassifier())
        && attachedArtifact.getFile() != null
        && attachedArtifact.getFile().isFile()) {
        return attachedArtifact;
      }
    }
    return null;
  }
}

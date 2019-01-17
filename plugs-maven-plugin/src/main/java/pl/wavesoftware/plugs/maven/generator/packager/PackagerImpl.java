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
import io.vavr.collection.HashSet;
import org.apache.maven.artifact.Artifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.wavesoftware.plugs.maven.generator.filter.Filter;
import pl.wavesoftware.plugs.maven.generator.model.ArtifactsLibraries;
import pl.wavesoftware.plugs.maven.generator.model.ExecutionConfiguration;
import pl.wavesoftware.plugs.maven.generator.model.Libraries;
import pl.wavesoftware.plugs.maven.generator.model.Library;
import pl.wavesoftware.plugs.maven.generator.model.PlugsMojoException;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static pl.wavesoftware.eid.utils.EidExecutions.tryToExecute;
import static pl.wavesoftware.plugs.maven.generator.packager.Constants.PLUGS_VERSION_ATTRIBUTE;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class PackagerImpl implements Packager {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(PackagerImpl.class);

  private final ExecutionConfiguration configuration;
  private final Filter filter;
  private final ManifestBuilder manifestBuilder;
  private final Lazy<File> lazyTargetFile;
  private final Lazy<Artifact> lazySourceArtifact;

  PackagerImpl(
    ExecutionConfiguration configuration,
    Filter filter,
    ManifestBuilder manifestBuilder
  ) {
    this.configuration = configuration;
    this.filter = filter;
    this.manifestBuilder = manifestBuilder;
    lazyTargetFile = Lazy.of(this::calculateTargetFile);
    lazySourceArtifact = Lazy.of(this::calculateSourceArtifact);
  }

  @Override
  public boolean needsRepackaging() {
    return tryToExecute(() -> !alreadyRepackaged(), "20190116:000119");
  }

  @Override
  public void repackage() throws PlugsMojoException {
    Artifact source = getSourceArtifact();
    File target = getTargetFile();
    Set<Artifact> artifacts = filter.filterDependencies(
      configuration.getMavenProject().getArtifacts()
    );
    Libraries libraries = new ArtifactsLibraries(
      artifacts,
      new ArrayList<>(),
      configuration.getLog()
    );

    try {
      repackage(source, target, libraries);
    } catch (IOException ex) {
      throw new PlugsMojoException(ex.getMessage(), ex);
    }
  }

  @Override
  public File getTargetFile() {
    return lazyTargetFile.get();
  }

  @Override
  public Artifact getSourceArtifact() {
    return lazySourceArtifact.get();
  }

  private boolean alreadyRepackaged() throws IOException {
    File targetFile = getTargetFile();
    if (!targetFile.isFile()) {
      return false;
    }
    try (JarFile jarFile = new JarFile(targetFile)) {
      Manifest manifest = jarFile.getManifest();
      LOGGER.warn("Better decision of noop is needed, based on source hash.");
      return manifest != null
        && manifest.getMainAttributes()
        .getValue(PLUGS_VERSION_ATTRIBUTE) != null;
    }
  }

  private void repackage(
    Artifact source,
    File destination,
    Libraries libraries
  ) throws IOException {
    destination = destination.getAbsoluteFile();
    Files.deleteIfExists(destination.toPath());
    WritableLibraries writeableLibraries = new WritableLibraries(libraries);
    LibrariesCollector collector = new LibrariesCollector();
    try (JarWriter writer = new JarWriter(destination)) {
      writer.addListener(
        LibraryHasBeenWritten.class,
        event -> collector.collect(event.getLibrary())
      );
      writeableLibraries.write(writer);
      LOGGER.warn("Write source artifact compiled sources");

      LOGGER.warn("Create real imports, from provided scope");
      HashSet<Library> imports = HashSet.empty();
      Manifest manifest = manifestBuilder.buildManifest(
        source,
        collector.getCollected(),
        imports
      );
      writer.writeManifest(manifest);
    }
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
    //noinspection RedundantIfStatement
    if (!configuration.getOutputDirectory().exists()) {
      assert configuration.getOutputDirectory().mkdirs();
    }
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

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

package pl.wavesoftware.plugs.tools.packager.core;

import io.vavr.collection.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.wavesoftware.plugs.tools.packager.core.jar.JarWriter;
import pl.wavesoftware.plugs.tools.packager.core.jar.LibrariesCollector;
import pl.wavesoftware.plugs.tools.packager.core.jar.LibraryHasBeenWritten;
import pl.wavesoftware.plugs.tools.packager.core.jar.WritableLibraries;
import pl.wavesoftware.plugs.tools.packager.core.manifest.ManifestBuilder;
import pl.wavesoftware.plugs.tools.packager.core.model.Artifact;
import pl.wavesoftware.plugs.tools.packager.core.model.ArtifactType;
import pl.wavesoftware.plugs.tools.packager.core.model.Filter;
import pl.wavesoftware.plugs.tools.packager.core.model.Libraries;
import pl.wavesoftware.plugs.tools.packager.core.model.Or;
import pl.wavesoftware.plugs.tools.packager.core.model.PackagerConfiguration;
import pl.wavesoftware.plugs.tools.packager.core.model.PackagerCoordinates;
import pl.wavesoftware.plugs.tools.packager.core.model.Project;
import pl.wavesoftware.plugs.tools.packager.core.model.RepackageFailed;
import pl.wavesoftware.plugs.tools.packager.core.model.RepackagingIsRequired;
import pl.wavesoftware.plugs.tools.packager.core.spi.LibrariesFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static pl.wavesoftware.plugs.tools.packager.core.model.RepackageFailed.check;
import static pl.wavesoftware.plugs.tools.packager.core.model.RepackageFailed.tring;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class DefaultPackager implements Packager {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(DefaultPackager.class);

  private final PackagerConfiguration configuration;
  private final RepackagingIsRequired condition;
  private final LibrariesFactory librariesFactory;
  private final Filter filter;
  private final ManifestBuilder manifestBuilder;

  DefaultPackager(
    PackagerConfiguration configuration,
    RepackagingIsRequired condition,
    LibrariesFactory librariesFactory,
    Filter filter,
    ManifestBuilder manifestBuilder
  ) {
    this.configuration = configuration;
    this.condition = condition;
    this.librariesFactory = librariesFactory;
    this.filter = filter;
    this.manifestBuilder = manifestBuilder;
  }

  @Override
  public Or repackage() {
    return block -> {
      if (condition.isTrue()) {
        doRepackage();
      } else {
        block.execute();
      }
    };
  }

  private void doRepackage() throws RepackageFailed {
    PackagerCoordinates coordinates = configuration.coordinates();
    Artifact source = validateSource(coordinates.sourceArtifact());
    Path destination = validateDestination(coordinates.targetPath());
    Set<Artifact> artifacts = filter.filterDependencies(
      configuration.project().dependencies()
    );
    Libraries libraries = librariesFactory.create(
      artifacts, configuration.logger()
    );

    try (JarFile sourceJar = newJarFile(source)) {
      repackage(
        configuration.project(),
        sourceJar,
        destination.toAbsolutePath(),
        libraries
      );
    } catch (IOException ex) {
      throw new RepackageFailed(ex.getMessage(), ex);
    }
  }

  private static JarFile newJarFile(Artifact source) throws IOException {
    return new JarFile(source.path().toAbsolutePath().toFile());
  }

  private static Path validateDestination(Path destination)
    throws RepackageFailed {
    File asFile = destination.toFile();
    if (asFile.exists()) {
      check(asFile.isFile()).or(
        "Invalid destination: {}",
        destination
      );
    }
    return destination;
  }

  private static Artifact validateSource(Artifact source) throws RepackageFailed {
    check(source.type() == ArtifactType.JAR).or(
      "Only jar artifacts are supported at this time"
    );
    Path sourcePath = source.path();
    File sourceFile = sourcePath.toFile();
    check(sourceFile.isFile()).or(
      "Source must refer to an existing file, got: {}",
      sourcePath.toAbsolutePath()
    );
    return source;
  }

  private void repackage(
    Project project,
    JarFile sourceJar,
    Path destination,
    Libraries libraries
  ) throws RepackageFailed {
    tring(() -> Files.deleteIfExists(destination)).or(
      "Can't remove previously created target file: {}",
      destination
    );
    Path parentPath = destination.getParent();
    tring(() -> Files.createDirectories(parentPath)).or(
      "Can't create directory for destination: {}",
      parentPath
    );
    WritableLibraries writeableLibraries =
      tring(() -> new WritableLibraries(libraries)).or(
        "Can't read all provided libraries"
      );
    LibrariesCollector collector = new LibrariesCollector();
    try (JarWriter writer = newJarWriter(destination)) {
      writer.addListener(
        LibraryHasBeenWritten.class,
        event -> collector.collect(event.getLibrary())
      );
      tring(() -> writeableLibraries.write(writer)).or(
        "Can't write libraries to a destination jar, {}",
        destination
      );
      tring(() -> writer.writeEntries(sourceJar, writeableLibraries)).or(
        "Can't rewrite source jar into destination jar: {}",
        destination
      );
      LOGGER.warn("TODO: Create real imports, from provided scope");
      Manifest manifest = manifestBuilder.buildManifest(
        project,
        sourceJar
      );
      tring(() -> writer.writeManifest(manifest)).or(
        "Can't write MANIFEST.MF file int destination jar: {}",
        destination
      );
    } catch (IOException ex) {
      throw new RepackageFailed(
        "Can't save destination jar: " + destination,
        ex
      );
    }
  }

  private JarWriter newJarWriter(Path destination) throws RepackageFailed {
    return tring(() -> new JarWriter(destination)).or(
      "Can't create a jar at destination: {}",
      destination
    );
  }

}

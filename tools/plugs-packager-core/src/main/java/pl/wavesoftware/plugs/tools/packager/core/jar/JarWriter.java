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

package pl.wavesoftware.plugs.tools.packager.core.jar;

import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import pl.wavesoftware.plugs.tools.packager.api.model.Library;
import pl.wavesoftware.plugs.tools.packager.core.jar.JarArchiveEntryWriter.Context;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Writes JAR content, ensuring valid directory entries are always created and
 * duplicate items are ignored.
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @author Phillip Webb (Spring Boot project)
 * @author Andy Wilkinson (Spring Boot project)
 * @since 0.1.0
 */
public final class JarWriter
  extends ArchiveWriterEventAware
  implements ArchiveWriter, AutoCloseable {

  private final JarArchiveOutputStream jarOutput;
  private final Set<String> writtenEntries = new HashSet<>();

  /**
   * Create a new {@link JarWriter} instance.
   *
   * @param path the path to write
   * @throws IOException           if the path cannot be opened
   * @throws FileNotFoundException if the path cannot be found
   */
  public JarWriter(Path path) throws IOException {
    FileOutputStream fileOutputStream = new FileOutputStream(path.toFile());
    this.jarOutput = new JarArchiveOutputStream(fileOutputStream);
    this.jarOutput.setEncoding(StandardCharsets.UTF_8.displayName());
  }

  @Override
  public void writeManifest(Manifest manifest) throws IOException {
    JarArchiveEntry entry = new JarArchiveEntry("META-INF/MANIFEST.MF");
    writeEntry(entry, manifest::write);
  }

  @Override
  public void writeLibrary(
    String destination, Library library
  ) throws IOException {
    LibrariesJarWriter librariesJarWriter = new LibrariesJarWriter(
      this::getListeners, this::writeEntry
    );
    librariesJarWriter.writeLibrary(destination, library);
  }

  @Override
  public void writeEntries(JarFile jarFile) throws IOException {
    this.writeEntries(jarFile, new SkipManifestMfTransformer());
  }

  /**
   * Close the writer.
   *
   * @throws IOException if the file cannot be closed
   */
  @Override
  public void close() throws IOException {
    this.jarOutput.close();
  }

  private void writeEntries(
    JarFile jarFile, EntryTransformer entryTransformer
  ) throws IOException {
    Context context = new Context(
      () -> writtenEntries, () -> jarOutput
    );
    JarArchiveEntryWriter jarArchiveEntryWriter = new JarArchiveEntryWriter(context);
    JarFileEntriesWriter jarFileEntriesWriter = new JarFileEntriesWriter(
      jarArchiveEntryWriter
    );
    jarFileEntriesWriter.writeEntries(jarFile, entryTransformer);
  }

  /**
   * Perform the actual write of a {@link JarEntry}. All other write methods delegate to
   * this one.
   *
   * @param entry         the entry to write
   * @param entryWriter   the entry writer or {@code null} if there is no content
   * @throws IOException in case of I/O errors
   */
  private void writeEntry(
    JarArchiveEntry entry, @Nullable EntryWriter entryWriter
  ) throws IOException {
    Context context = new Context(
      () -> writtenEntries, () -> jarOutput
    );
    JarArchiveEntryWriter jarArchiveEntryWriter = new JarArchiveEntryWriter(context);
    jarArchiveEntryWriter.writeEntry(entry, entryWriter);
  }

}

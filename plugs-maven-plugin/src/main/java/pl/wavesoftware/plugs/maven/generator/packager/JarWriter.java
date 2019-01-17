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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.UnixStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import pl.wavesoftware.plugs.maven.generator.model.Library;

import javax.annotation.Nullable;
import javax.annotation.WillClose;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

/**
 * Writes JAR content, ensuring valid directory entries are always created and
 * duplicate items are ignored.
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @author Phillip Webb (Spring Boot project)
 * @author Andy Wilkinson (Spring Boot project)
 * @since 0.1.0
 */
final class JarWriter implements ArchiveWriter, AutoCloseable {
  private static final Logger LOGGER =
    LoggerFactory.getLogger(JarWriter.class);
  private static final UnpackHandler NEVER_UNPACK = new NeverUnpackHandler();

  private final JarArchiveOutputStream jarOutput;
  private final Set<String> writtenEntries = new HashSet<>();
  private final Multimap<
    Class<? extends ArchiveWriterEvent>,
    ArchiveWriterListener<?>
    > listeners = HashMultimap.create();

  /**
   * Create a new {@link JarWriter} instance.
   *
   * @param file the file to write
   * @throws IOException           if the file cannot be opened
   * @throws FileNotFoundException if the file cannot be found
   */
  JarWriter(File file) throws IOException {
    FileOutputStream fileOutputStream = new FileOutputStream(file);
    this.jarOutput = new JarArchiveOutputStream(fileOutputStream);
    this.jarOutput.setEncoding(StandardCharsets.UTF_8.displayName());
  }

  @Override
  public void writeManifest(Manifest manifest) throws IOException {
    JarArchiveEntry entry = new JarArchiveEntry("META-INF/MANIFEST.MF");
    writeEntry(entry, manifest::write);
  }

  @Override
  public void writeEntry(
    String entryName,
    @WillClose InputStream inputStream
  ) throws IOException {
    JarArchiveEntry entry = new JarArchiveEntry(entryName);
    writeEntry(entry, new InputStreamEntryWriter(inputStream, true));
  }

  @Override
  public void writeNestedLibrary(
    String destination,
    Library library
  ) throws IOException {
    File file = library.getFile();
    JarArchiveEntry entry = new JarArchiveEntry(destination + library.getName());
    entry.setTime(getNestedLibraryTime(file));
    new CrcAndSize(file).setupStoredEntry(entry);
    writeEntry(
      entry,
      new InputStreamEntryWriter(new FileInputStream(file), true),
      new LibraryUnpackHandler(library)
    );
    LibraryHasBeenWritten event = new LibraryHasBeenWritten(library);
    for (ArchiveWriterListener<?> listener : listeners.get(LibraryHasBeenWritten.class)) {
      @SuppressWarnings("unchecked")
      ArchiveWriterListener<LibraryHasBeenWritten> libraryListener =
        (ArchiveWriterListener<LibraryHasBeenWritten>) listener;
      libraryListener.handle(event);
    }
  }

  @Override
  public void writeEntries(JarFile jarFile) throws IOException {
    this.writeEntries(jarFile, new IdentityEntryTransformer(), NEVER_UNPACK);
  }

  @Override
  public void writeEntries(JarFile jarFile, UnpackHandler unpackHandler) throws IOException {
    this.writeEntries(jarFile, new IdentityEntryTransformer(), unpackHandler);
  }

  @Override
  public void writeEntries(
    JarFile jarFile,
    EntryTransformer entryTransformer,
    UnpackHandler unpackHandler
  ) throws IOException {
    Enumeration<JarEntry> entries = jarFile.entries();
    while (entries.hasMoreElements()) {
      JarArchiveEntry entry = new JarArchiveEntry(entries.nextElement());
      setUpEntry(jarFile, entry);
      try (ZipHeaderPeekInputStream inputStream = new ZipHeaderPeekInputStream(
        jarFile.getInputStream(entry))) {
        EntryWriter entryWriter = new InputStreamEntryWriter(inputStream, true);
        JarArchiveEntry transformedEntry = entryTransformer.transform(entry);
        if (transformedEntry != null) {
          writeEntry(transformedEntry, entryWriter, unpackHandler);
        }
      }
    }
  }

  @Override
  public <E extends ArchiveWriterEvent> void addListener(
    Class<E> eventType,
    ArchiveWriterListener<E> listener
  ) {
    listeners.put(eventType, listener);
  }

  private static void setUpEntry(JarFile jarFile, JarArchiveEntry entry)
    throws IOException {
    try (ZipHeaderPeekInputStream inputStream = new ZipHeaderPeekInputStream(
      jarFile.getInputStream(entry))) {
      if (inputStream.hasZipHeader() && entry.getMethod() != ZipEntry.STORED) {
        new CrcAndSize(inputStream).setupStoredEntry(entry);
      } else {
        entry.setCompressedSize(-1);
      }
    }
  }

  private static long getNestedLibraryTime(File file) {
    try {
      try (JarFile jarFile = new JarFile(file)) {
        Enumeration<JarEntry> entries = jarFile.entries();
        Long entry = findTimeOfJarEntries(entries);
        if (entry != null) {
          return entry;
        }
      }
    } catch (IOException ex) {
      // Ignore and just use the source file timestamp
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(
          MessageFormatter.format(
            "Can't read a supposed JAR file: {}", file
          ).toString(),
          ex
        );
      }
    }
    return file.lastModified();
  }

  @Nullable
  private static Long findTimeOfJarEntries(Enumeration<JarEntry> entries) {
    while (entries.hasMoreElements()) {
      JarEntry entry = entries.nextElement();
      if (!entry.isDirectory()) {
        return entry.getTime();
      }
    }
    return null;
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

  private void writeEntry(
    JarArchiveEntry entry,
    EntryWriter entryWriter
  ) throws IOException {
    writeEntry(entry, entryWriter, NEVER_UNPACK);
  }

  /**
   * Perform the actual write of a {@link JarEntry}. All other write methods delegate to
   * this one.
   *
   * @param entry         the entry to write
   * @param entryWriter   the entry writer or {@code null} if there is no content
   * @param unpackHandler handles possible unpacking for the entry
   * @throws IOException in case of I/O errors
   */
  private void writeEntry(
    JarArchiveEntry entry,
    @Nullable EntryWriter entryWriter,
    UnpackHandler unpackHandler
  ) throws IOException {
    String parent = entry.getName();
    if (parent.endsWith("/")) {
      parent = parent.substring(0, parent.length() - 1);
      entry.setUnixMode(UnixStat.DIR_FLAG | UnixStat.DEFAULT_DIR_PERM);
    } else {
      entry.setUnixMode(UnixStat.FILE_FLAG | UnixStat.DEFAULT_FILE_PERM);
    }
    if (parent.lastIndexOf('/') != -1) {
      parent = parent.substring(0, parent.lastIndexOf('/') + 1);
      if (!parent.isEmpty()) {
        writeEntry(new JarArchiveEntry(parent), null, unpackHandler);
      }
    }

    if (this.writtenEntries.add(entry.getName())) {
      entryWriter = addUnpackCommentIfNecessary(entry, entryWriter, unpackHandler);
      this.jarOutput.putArchiveEntry(entry);
      if (entryWriter != null) {
        entryWriter.write(this.jarOutput);
      }
      this.jarOutput.closeArchiveEntry();
    }
  }

  private static EntryWriter addUnpackCommentIfNecessary(
    JarArchiveEntry entry,
    @Nullable EntryWriter entryWriter,
    UnpackHandler unpackHandler
  ) throws IOException {
    if (entryWriter == null || !unpackHandler.requiresUnpack(entry.getName())) {
      return entryWriter;
    }
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    entryWriter.write(output);
    entry.setComment("UNPACK:" + unpackHandler.sha256Hash(entry.getName()));
    return new InputStreamEntryWriter(
      new ByteArrayInputStream(output.toByteArray()),
      true
    );
  }

}

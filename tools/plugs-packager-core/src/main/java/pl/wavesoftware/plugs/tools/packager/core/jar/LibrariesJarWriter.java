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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;
import pl.wavesoftware.plugs.tools.packager.api.model.Library;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.function.Supplier;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class LibrariesJarWriter {
  private static final Logger LOGGER =
    LoggerFactory.getLogger(LibrariesJarWriter.class);

  private final Supplier<Listeners> listenersSupplier;
  private final JarEntryWriter entryWriter;

  LibrariesJarWriter(
    Supplier<Listeners> listenersSupplier,
    JarEntryWriter entryWriter
  ) {
    this.listenersSupplier = listenersSupplier;
    this.entryWriter = entryWriter;
  }

  void writeLibrary(String destination, Library library) throws IOException {
    File file = library.getFile();
    JarArchiveEntry entry = new JarArchiveEntry(destination + library.getName());
    entry.setTime(getNestedLibraryTime(file));
    new CrcAndSize(file).setupStoredEntry(entry);
    entryWriter.writeEntry(
      entry,
      new InputStreamEntryWriter(new FileInputStream(file), true)
    );
    LibraryHasBeenWritten event = new LibraryHasBeenWritten(library);
    Listeners listeners = listenersSupplier.get();
    listeners.get(LibraryHasBeenWritten.class).forEach(
      listener -> listener.handle(event)
    );
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
}

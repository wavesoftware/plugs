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

import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class JarFileEntriesWriter {
  private final JarArchiveEntryWriter jarArchiveEntryWriter;

  JarFileEntriesWriter(JarArchiveEntryWriter jarArchiveEntryWriter) {
    this.jarArchiveEntryWriter = jarArchiveEntryWriter;
  }

  void writeEntries(
    JarFile jarFile, EntryTransformer entryTransformer
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
          jarArchiveEntryWriter.writeEntry(transformedEntry, entryWriter);
        }
      }
    }
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
}

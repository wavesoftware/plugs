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
import org.apache.commons.compress.archivers.zip.UnixStat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class JarArchiveEntryWriter {
  private static final Logger LOGGER =
    LoggerFactory.getLogger(JarArchiveEntryWriter.class);

  private final Context context;

  JarArchiveEntryWriter(Context context) {
    this.context = context;
  }

  void writeEntry(
    JarArchiveEntry entry, @Nullable EntryWriter entryWriter
  ) throws IOException {
    String parent = entry.getName();
    boolean isDirectory = false;
    if (parent.endsWith("/")) {
      parent = parent.substring(0, parent.length() - 1);
      entry.setUnixMode(UnixStat.DIR_FLAG | UnixStat.DEFAULT_DIR_PERM);
      isDirectory = true;
    } else {
      entry.setUnixMode(UnixStat.FILE_FLAG | UnixStat.DEFAULT_FILE_PERM);
    }
    if (parent.lastIndexOf('/') != -1) {
      parent = parent.substring(0, parent.lastIndexOf('/') + 1);
      if (!parent.isEmpty()) {
        writeEntry(new JarArchiveEntry(parent), null);
      }
    }

    if (context.register(entry)) {
      context.writeHeaderOfEntry(entry);
      if (entryWriter != null) {
        context.writeEntry(entryWriter);
      }
      context.finalizeEntry();
    } else {
      if (!isDirectory) {
        LOGGER.warn(
          "Skipping resource, as it was already written: {}",
          entry.getName()
        );
      }
    }
  }

  static final class Context {
    private final Supplier<Collection<String>> registeredEntries;
    private final Supplier<JarArchiveOutputStream> outputStreamSupplier;

    Context(
      Supplier<Collection<String>> registeredEntries,
      Supplier<JarArchiveOutputStream> outputStreamSupplier
    ) {
      this.registeredEntries = registeredEntries;
      this.outputStreamSupplier = outputStreamSupplier;
    }

    private boolean register(JarArchiveEntry entry) {
      return registeredEntries.get().add(entry.getName());
    }

    private void writeHeaderOfEntry(JarArchiveEntry entry) throws IOException {
      outputStreamSupplier.get().putArchiveEntry(entry);
    }

    private void writeEntry(EntryWriter entryWriter) throws IOException {
      entryWriter.write(outputStreamSupplier.get());
    }

    private void finalizeEntry() throws IOException {
      outputStreamSupplier.get().closeArchiveEntry();
    }
  }
}

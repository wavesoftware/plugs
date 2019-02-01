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

import pl.wavesoftware.plugs.tools.packager.core.model.Library;

import javax.annotation.WillClose;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Writer used to write classes into a repackaged JAR.
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
interface ArchiveWriter {
  /**
   * Write a single entry to the JAR.
   *
   * @param name        the name of the entry
   * @param inputStream the input stream content
   * @throws IOException if the entry cannot be written
   */
  void writeEntry(
    String name,
    @WillClose InputStream inputStream
  ) throws IOException;

  /**
   * Write the specified manifest.
   *
   * @param manifest the manifest to write
   * @throws IOException of the manifest cannot be written
   */
  void writeManifest(Manifest manifest) throws IOException;

  /**
   * Write a nested library.
   *
   * @param destination the destination of the library
   * @param library     the library
   * @throws IOException if the write fails
   */
  void writeNestedLibrary(
    String destination,
    Library library
  ) throws IOException;

  /**
   * Write all entries from the specified jar file.
   *
   * @param jarFile the source jar file
   * @throws IOException if the entries cannot be written
   */
  void writeEntries(JarFile jarFile) throws IOException;

  void writeEntries(
    JarFile jarFile,
    UnpackHandler unpackHandler
  ) throws IOException;

  void writeEntries(
    JarFile jarFile,
    EntryTransformer entryTransformer,
    UnpackHandler unpackHandler
  ) throws IOException;

  <E extends ArchiveWriterEvent> void addListener(
    Class<E> eventType,
    ArchiveWriterListener<E> listener
  );
}

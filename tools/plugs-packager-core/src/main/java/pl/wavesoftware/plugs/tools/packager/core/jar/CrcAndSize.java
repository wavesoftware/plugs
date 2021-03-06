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

import javax.annotation.WillNotClose;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;

/**
 * Data holder for CRC and Size.
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class CrcAndSize {

  private static final int BUFFER_SIZE = 32 * 1024;
  private final CRC32 crc = new CRC32();

  private long size;

  CrcAndSize(Path path) throws IOException {
    try (InputStream inputStream = Files.newInputStream(path)) {
      load(inputStream);
    }
  }

  CrcAndSize(@WillNotClose InputStream inputStream) throws IOException {
    load(inputStream);
  }

  void setupStoredEntry(JarArchiveEntry entry) {
    entry.setSize(this.size);
    entry.setCompressedSize(this.size);
    entry.setCrc(this.crc.getValue());
    entry.setMethod(ZipEntry.STORED);
  }

  private void load(@WillNotClose InputStream inputStream) throws IOException {
    byte[] buffer = new byte[BUFFER_SIZE];
    int bytesRead;
    while ((bytesRead = inputStream.read(buffer)) != -1) {
      this.crc.update(buffer, 0, bytesRead);
      this.size += bytesRead;
    }
  }

}

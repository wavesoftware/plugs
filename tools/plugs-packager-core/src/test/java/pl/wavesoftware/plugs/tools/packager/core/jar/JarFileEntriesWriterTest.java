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

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.junit.jupiter.api.Test;
import pl.wavesoftware.plugs.tools.packager.core.jar.JarArchiveEntryWriter.Context;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.jar.JarFile;

import static pl.wavesoftware.eid.utils.EidPreconditions.checkNotNull;

class JarFileEntriesWriterTest {

  @Test
  void writeEntries() throws IOException, URISyntaxException {
    // given
    try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
      Path path = fs.getPath("JarFileEntriesWriterTest", "target.jar");
      Files.createDirectories(path.getParent());
      Collection<String> registeredEntries = new ArrayList<>();
      try (OutputStream out = Files.newOutputStream(path);
           JarArchiveOutputStream jarOutputStream = new JarArchiveOutputStream(out)) {
        Context context = new Context(() -> registeredEntries, () -> jarOutputStream);
        JarArchiveEntryWriter jarArchiveEntryWriter =
          new JarArchiveEntryWriter(context);
        JarFileEntriesWriter jarFileEntriesWriter =
          new JarFileEntriesWriter(jarArchiveEntryWriter);
        URL url = checkNotNull(
          getClass().getClassLoader().getResource("composite.zip"),
          "20190925:124819"
        );
        JarFile jarFile = new JarFile(Paths.get(url.toURI()).toFile());

        // when
        jarFileEntriesWriter.writeEntries(jarFile, new IdentityEntryTransformer());
      }
    }
  }
}

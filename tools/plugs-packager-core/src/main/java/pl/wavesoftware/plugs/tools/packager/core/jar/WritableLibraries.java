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

import pl.wavesoftware.plugs.tools.packager.core.model.Libraries;
import pl.wavesoftware.plugs.tools.packager.core.model.Library;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static pl.wavesoftware.plugs.tools.packager.core.Constants.LIBRARY_DESTINATION;
import static pl.wavesoftware.plugs.tools.packager.core.jar.FileUtils.isZip;

/**
 * A libraries that can be written
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
public final class WritableLibraries implements UnpackHandler {

  private final Map<String, Library> libraryEntryNames = new LinkedHashMap<>();

  public WritableLibraries(Libraries libraries) throws IOException {
    libraries.doWithLibraries(library -> {
      if (isZip(library.getFile())) {
        Library existing = this.libraryEntryNames.putIfAbsent(
          LIBRARY_DESTINATION + library.getName(),
          library
        );
        if (existing != null) {
          throw new IllegalStateException(
            "Duplicate library " + library.getName()
          );
        }
      }
    });
  }

  @Override
  public boolean requiresUnpack(String name) {
    Library library = this.libraryEntryNames.get(name);
    return library != null && library.isUnpackRequired();
  }

  @Override
  public String sha256Hash(String name) throws IOException {
    Library library = this.libraryEntryNames.get(name);
    if (library == null) {
      throw new IllegalArgumentException(
        "No library found for entry name '" + name + "'");
    }
    return FileUtils.sha256Hash(library.getFile());
  }

  public void write(JarWriter writer) throws IOException {
    for (Map.Entry<String, Library> entry : this.libraryEntryNames.entrySet()) {
      writer.writeNestedLibrary(
        entry.getKey().substring(0, entry.getKey().lastIndexOf('/') + 1),
        entry.getValue()
      );
    }
  }

}

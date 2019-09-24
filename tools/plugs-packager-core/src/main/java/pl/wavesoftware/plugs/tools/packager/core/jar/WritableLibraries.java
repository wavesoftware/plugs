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

import pl.wavesoftware.plugs.tools.packager.api.model.Libraries;
import pl.wavesoftware.plugs.tools.packager.api.model.Library;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static pl.wavesoftware.plugs.tools.packager.api.Constants.LIBRARY_DESTINATION;

/**
 * A libraries that can be written
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
public final class WritableLibraries {

  private final Map<String, Library> libraryEntryNames = new LinkedHashMap<>();

  public WritableLibraries(Libraries libraries) throws IOException {
    libraries.doWithLibraries(library -> {
      IsZipFile isZipFile = new IsZipFile(library.getPath());
      if (isZipFile.getAsBoolean()) {
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

  public void write(JarWriter writer) throws IOException {
    for (Map.Entry<String, Library> entry : this.libraryEntryNames.entrySet()) {
      writer.writeLibrary(
        entry.getKey().substring(0, entry.getKey().lastIndexOf('/') + 1),
        entry.getValue()
      );
    }
  }

}

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

package pl.wavesoftware.plugs.tools.packager.api.model;

import javax.annotation.Nullable;
import java.nio.file.Path;

/**
 * Encapsulates information about a single library that may be packed into
 * the archive.
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @author Phillip Webb (Spring Boot project)
 * @since 0.1.0
 * @see Libraries
 */
public final class Library {
  private final String name;
  private final Path path;
  private final LibraryScope scope;

  /**
   * Create a new {@link Library}.
   * @param path the source file
   * @param scope the scope of the library
   */
  public Library(Path path, LibraryScope scope) {
    this(null, path, scope);
  }

  /**
   * Create a new {@link Library}.
   * @param name the name of the library as it should be written or {@code null} to use
   * the file name
   * @param path the source file
   * @param scope the scope of the library
   */
  public Library(
    @Nullable String name,
    Path path,
    LibraryScope scope
  ) {
    this.name = (name != null) ? name : path.getFileName().toString();
    this.path = path;
    this.scope = scope;
  }

  /**
   * Return the name of file as it should be written.
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Return the library file.
   * @return the file
   */
  public Path getPath() {
    return this.path;
  }

  /**
   * Return the scope of the library.
   * @return the scope
   */
  public LibraryScope getScope() {
    return this.scope;
  }

}

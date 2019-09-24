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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BooleanSupplier;

/**
 * Utilities for manipulating files and directories in Spring Boot tooling.
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class IsZipFile implements BooleanSupplier {

  private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(IsZipFile.class);
  private static final byte[] ZIP_FILE_HEADER = new byte[] { 'P', 'K', 3, 4 };

  private final Path path;
  private final Logger logger;

  IsZipFile(Path path) {
    this(path, DEFAULT_LOGGER);
  }

  IsZipFile(Path path, Logger logger) {
    this.path = path;
    this.logger = logger;
  }

  @Override
  public boolean getAsBoolean() {
    try {
      try (InputStream inputStream = Files.newInputStream(path)) {
        return isZip(inputStream);
      }
    } catch (IOException ex) {
      if (logger.isTraceEnabled()) {
        logger.trace("Can't read file: " + path, ex);
      }
      return false;
    }
  }

  private static boolean isZip(InputStream inputStream) throws IOException {
    for (byte magicByte : ZIP_FILE_HEADER) {
      if (inputStream.read() != magicByte) {
        return false;
      }
    }
    return true;
  }
}

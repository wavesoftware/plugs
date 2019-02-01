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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * {@link EntryWriter} that writes content from an {@link InputStream}.
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class InputStreamEntryWriter implements EntryWriter {

  private static final int BUFFER_SIZE = 32 * 1024;
  private final InputStream inputStream;
  private final boolean close;

  InputStreamEntryWriter(InputStream inputStream, boolean close) {
    this.inputStream = inputStream;
    this.close = close;
  }

  @Override
  public void write(OutputStream outputStream) throws IOException {
    byte[] buffer = new byte[BUFFER_SIZE];
    int bytesRead;
    while ((bytesRead = this.inputStream.read(buffer)) != -1) {
      outputStream.write(buffer, 0, bytesRead);
    }
    outputStream.flush();
    if (this.close) {
      this.inputStream.close();
    }
  }

}

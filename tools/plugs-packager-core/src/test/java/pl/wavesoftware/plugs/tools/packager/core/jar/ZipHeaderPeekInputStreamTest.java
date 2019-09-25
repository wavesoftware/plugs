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

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

import static org.assertj.core.api.Assertions.assertThat;

class ZipHeaderPeekInputStreamTest {

  @Test
  void read() throws IOException {
    // given
    CRC32 crc32 = new CRC32();
    try (InputStream inputStream = getClass().getClassLoader()
      .getResourceAsStream("example.zip");
         ZipHeaderPeekInputStream zipHeaderPeekInputStream =
           new ZipHeaderPeekInputStream(inputStream)
    ) {
      // when
      int next;
      while ((next = zipHeaderPeekInputStream.read()) != -1) {
        crc32.update(next);
      }
    }

    // then
    assertThat(crc32.getValue()).isEqualTo(3245340136L);
  }
}

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

import javax.inject.Named;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.CRC32;

import static java.nio.charset.StandardCharsets.UTF_8;
import static pl.wavesoftware.eid.utils.EidPreconditions.checkArgument;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@Named
final class FileDigestImpl implements FileDigest {
  @Override
  public CharSequence digest(Path path) throws IOException {
    checkArgument(path.toFile().isFile(), "20190131:221929");
    CRC32 digest = new CRC32();
    digest.update(path.toAbsolutePath().toString().getBytes(UTF_8));
    digest.update(Long.toHexString(Files.size(path)).getBytes(UTF_8));
    digest.update(Files.getLastModifiedTime(path).toString().getBytes(UTF_8));
    return Long.toHexString(Math.abs(digest.getValue()));
  }
}

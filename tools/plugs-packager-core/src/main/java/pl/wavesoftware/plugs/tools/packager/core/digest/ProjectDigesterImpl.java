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

package pl.wavesoftware.plugs.tools.packager.core.digest;

import pl.wavesoftware.plugs.tools.packager.api.digest.ProjectDigester;
import pl.wavesoftware.plugs.tools.packager.api.model.Project;

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
final class ProjectDigesterImpl implements ProjectDigester {

  private static final int BASE36 = 36;

  @Override
  public CharSequence digest(Project project) throws IOException {
    Path sourcePath = project.mainArtifact().path();
    Path buildFilePath = project.buildFilePath();
    checkArgument(Files.isRegularFile(sourcePath), "20190131:221929");
    return encode(digest(
      sourcePath.toAbsolutePath().toString(),
      Long.toHexString(Files.size(sourcePath)),
      Long.toHexString(Files.getLastModifiedTime(sourcePath).toMillis()),
      Long.toHexString(Files.size(buildFilePath)),
      Long.toHexString(Files.getLastModifiedTime(buildFilePath).toMillis())
    ));
  }

  private static CharSequence encode(long digest) {
    return Long.toUnsignedString(digest, BASE36);
  }

  private static long digest(String... strings) {
    CRC32 digester = new CRC32();
    for (String s : strings) {
      byte[] bytes = s.getBytes(UTF_8);
      digester.update(bytes, 0, bytes.length);
    }
    return digester.getValue();
  }

}

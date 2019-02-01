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

package pl.wavesoftware.plugs.tools.packager.core;

import pl.wavesoftware.plugs.tools.packager.core.jar.FileDigest;
import pl.wavesoftware.plugs.tools.packager.core.model.PackagerCoordinates;
import pl.wavesoftware.plugs.tools.packager.core.model.RepackagingIsRequired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static pl.wavesoftware.eid.utils.EidExecutions.tryToExecute;
import static pl.wavesoftware.plugs.tools.packager.core.Constants.PLUGS_DIGEST_ATTRIBUTE;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class DefaultRepackagingIsRequired
  implements RepackagingIsRequired {

  private final PackagerCoordinates coordinates;
  private final FileDigest fileDigest;

  DefaultRepackagingIsRequired(
    PackagerCoordinates coordinates,
    FileDigest fileDigest
  ) {
    this.coordinates = coordinates;
    this.fileDigest = fileDigest;
  }

  @Override
  public boolean isTrue() {
    return tryToExecute(() -> !alreadyRepackaged(), "20190116:000119");
  }

  private boolean alreadyRepackaged() throws IOException {
    File targetFile = coordinates.targetPath().toFile();
    Path sourcePath = coordinates.sourceArtifact().path();
    if (!targetFile.isFile() || !sourcePath.toFile().isFile()) {
      return false;
    }
    try (JarFile jarFile = new JarFile(targetFile)) {
      Manifest manifest = jarFile.getManifest();
      CharSequence digest = fileDigest.digest(sourcePath);
      return manifest != null
        && digest.equals(manifest.getMainAttributes().getValue(PLUGS_DIGEST_ATTRIBUTE));
    }
  }

}

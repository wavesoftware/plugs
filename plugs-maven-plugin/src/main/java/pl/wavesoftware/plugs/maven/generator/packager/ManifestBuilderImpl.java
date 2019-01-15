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

package pl.wavesoftware.plugs.maven.generator.packager;

import javax.inject.Named;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static pl.wavesoftware.plugs.maven.generator.packager.Constants.PLUGS_VERSION_ATTRIBUTE;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@Named
final class ManifestBuilderImpl implements ManifestBuilder {
  @Override
  public Manifest buildManifest(JarFile source) throws IOException {
    Manifest manifest = source.getManifest();
    if (manifest == null) {
      manifest = new Manifest();
      manifest.getMainAttributes()
        .putValue("Manifest-Version", "1.0");
    }
    manifest = new Manifest(manifest);
    String plugsVersion = getClass().getPackage().getImplementationVersion();
    manifest.getMainAttributes().putValue(
      PLUGS_VERSION_ATTRIBUTE,
      plugsVersion
    );
    return manifest;
  }
}

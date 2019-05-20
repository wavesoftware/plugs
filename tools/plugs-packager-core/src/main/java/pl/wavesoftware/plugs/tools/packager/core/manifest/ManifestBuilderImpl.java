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

package pl.wavesoftware.plugs.tools.packager.core.manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.wavesoftware.plugs.api.PlugsVersion;
import pl.wavesoftware.plugs.tools.packager.api.digest.ProjectDigester;
import pl.wavesoftware.plugs.tools.packager.api.manifest.ManifestBuilder;
import pl.wavesoftware.plugs.tools.packager.api.model.Project;
import pl.wavesoftware.plugs.tools.packager.api.model.RepackageFailed;

import javax.inject.Inject;
import javax.inject.Named;
import java.nio.file.Path;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static pl.wavesoftware.plugs.tools.packager.api.model.RepackageFailed.tring;
import static pl.wavesoftware.plugs.tools.packager.api.Constants.PLUGS_DIGEST_ATTRIBUTE;
import static pl.wavesoftware.plugs.tools.packager.api.Constants.PLUGS_VERSION_ATTRIBUTE;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@Named
final class ManifestBuilderImpl implements ManifestBuilder {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(ManifestBuilderImpl.class);

  private final ProjectDigester digest;

  @Inject
  ManifestBuilderImpl(ProjectDigester digest) {
    this.digest = digest;
  }

  @Override
  public Manifest buildManifest(Project project, JarFile sourceJar)
    throws RepackageFailed {

    Path sourcePath = project.mainArtifact().path();
    Manifest manifest = tring(sourceJar::getManifest).or(
      "Can't read MANIFEST.MF file from source jar: {}",
      sourcePath
    );
    if (manifest == null) {
      manifest = new Manifest();
      manifest.getMainAttributes()
        .putValue("Manifest-Version", "1.0");
    }
    manifest = new Manifest(manifest);
    Attributes attributes = manifest.getMainAttributes();
    String plugsVersion = PlugsVersion.getVersion();
    CharSequence hash = tring(() -> digest.digest(project)).or(
      "Can't calculate digest from source jar: {}",
      sourcePath
    );

    attributes.putValue(PLUGS_VERSION_ATTRIBUTE, plugsVersion);
    attributes.putValue(PLUGS_DIGEST_ATTRIBUTE, hash.toString());

    LOGGER.warn("TODO: Define dependencies");
    LOGGER.warn("TODO: Define imports");
    LOGGER.warn("TODO: Define sources");
    LOGGER.warn("TODO: Define name & version");

    return manifest;
  }
}

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

import io.vavr.collection.Set;
import org.apache.maven.artifact.Artifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.wavesoftware.plugs.maven.generator.model.Library;

import javax.inject.Named;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static pl.wavesoftware.plugs.maven.generator.packager.Constants.PLUGS_VERSION_ATTRIBUTE;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@Named
final class ManifestBuilderImpl implements ManifestBuilder {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(ManifestBuilderImpl.class);

  @Override
  public Manifest buildManifest(
    Artifact artifact,
    Set<Library> dependencies,
    Set<Library> imports
  ) {
    Manifest manifest = new Manifest();
    Attributes attributes = manifest.getMainAttributes();
    String plugsVersion = getClass().getPackage().getImplementationVersion();

    attributes.putValue("Manifest-Version", "1.0");
    attributes.putValue(PLUGS_VERSION_ATTRIBUTE, plugsVersion);

    LOGGER.warn("Define dependencies");
    LOGGER.warn("Define imports");
    LOGGER.warn("Define sources");
    LOGGER.warn("Define name & version");

    return manifest;
  }
}

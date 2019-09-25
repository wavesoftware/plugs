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

package pl.wavesoftware.plugs.api;

import pl.wavesoftware.eid.exceptions.EidIllegalArgumentException;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.jar.Manifest;

import static pl.wavesoftware.eid.utils.EidExecutions.tryToExecute;
import static pl.wavesoftware.eid.utils.EidPreconditions.checkNotNull;

public final class PlugsVersion {

  private final ClassLoader classLoader;

  private PlugsVersion() {
    this(Thread.currentThread().getContextClassLoader());
  }

  PlugsVersion(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  public static PlugsVersion get() {
    return new PlugsVersion();
  }

  public String getVersion() {
    Optional<String> maybeVersion =
      Optional.ofNullable(
        PlugsVersion.class.getPackage().getImplementationVersion()
      );
    return checkNotNull(
      maybeVersion.orElseGet(this::manuallyRead),
      "20190325:202509"
    );
  }

  String manuallyRead() {
    List<URL> urls = Collections.list(tryToExecute(
      () -> classLoader
        .getResources("META-INF/MANIFEST.MF"),
      "20190325:205203"
    ));
    URL location = PlugsVersion.class.getProtectionDomain().getCodeSource().getLocation();
    URL resource = urls.stream()
      .filter(url -> url.toString().contains(location.toString()))
      .findFirst()
      .orElseThrow(() -> new EidIllegalArgumentException("20190325:205648"));
    InputStream inputStream = checkNotNull(
      tryToExecute(resource::openStream, "20190325:205720"),
      "20190325:204807"
    );
    Manifest manifest = tryToExecute(
      () -> new Manifest(inputStream),
      "20190325:204433"
    );
    return manifest.getMainAttributes()
      .getValue("Implementation-Version");
  }
}

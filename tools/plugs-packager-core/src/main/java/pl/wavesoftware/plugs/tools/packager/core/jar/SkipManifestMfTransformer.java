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

import org.apache.commons.compress.archivers.jar.JarArchiveEntry;

import javax.annotation.Nullable;

final class SkipManifestMfTransformer implements EntryTransformer {

  private final EntryTransformer delegate = new IdentityEntryTransformer();

  @Override
  @Nullable
  public JarArchiveEntry transform(JarArchiveEntry jarEntry) {
    if (jarEntry.getName().equals("META-INF/MANIFEST.MF")) {
      return null;
    }
    return delegate.transform(jarEntry);
  }
}

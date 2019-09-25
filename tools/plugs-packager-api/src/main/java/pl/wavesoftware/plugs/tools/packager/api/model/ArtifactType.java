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

package pl.wavesoftware.plugs.tools.packager.api.model;

import pl.wavesoftware.eid.exceptions.EidIllegalArgumentException;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
public enum ArtifactType {
  JAR("jar"),
  WAR("war"),
  EAR("ear"),
  PAR("par"),
  RAR("rar"),
  ZIP("zip");

  private final String extension;

  ArtifactType(String extension) {
    this.extension = extension;
  }

  public String extension() {
    return extension;
  }

  public static ArtifactType fromPackaging(String packaging) {
    for (ArtifactType value : ArtifactType.values()) {
      if (value.extension().equals(packaging)) {
        return value;
      }
    }
    throw new EidIllegalArgumentException("20190209:000032");
  }
}

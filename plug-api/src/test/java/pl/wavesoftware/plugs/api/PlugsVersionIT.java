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

import com.vdurmont.semver4j.Semver;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlugsVersionIT {

  private static final Semver BASE = new Semver("0.0.0-0");

  @Test
  void getVersion() {
    String v = PlugsVersion.get().getVersion();
    Semver sv = new Semver(v);

    assertThat(sv.isGreaterThanOrEqualTo(BASE)).isTrue();
  }

  @Test
  void manually() {
    String v = PlugsVersion.get().manuallyRead();
    Semver sv = new Semver(v);

    assertThat(sv.isGreaterThanOrEqualTo(BASE)).isTrue();
  }
}

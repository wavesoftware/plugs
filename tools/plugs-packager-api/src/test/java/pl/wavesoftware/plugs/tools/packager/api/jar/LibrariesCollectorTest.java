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

package pl.wavesoftware.plugs.tools.packager.api.jar;

import org.junit.jupiter.api.Test;
import pl.wavesoftware.plugs.tools.packager.api.model.Library;
import pl.wavesoftware.plugs.tools.packager.api.model.LibraryScope;

import java.io.File;

import static org.assertj.core.api.Assertions.*;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
class LibrariesCollectorTest {

  @Test
  void getCollected() {
    // given
    LibrariesCollector collector = new LibrariesCollector();
    File acmeFile = new File("acme");
    LibraryScope scope = LibraryScope.PROVIDED;
    Library library = new Library(acmeFile, scope);

    // when
    collector.collect(library);

    // then
    assertThat(collector.getCollected()).hasSize(1);
    assertThat(collector.getCollected().head()).extracting(
      Library::getFile, Library::getScope, Library::getName
    ).containsOnly(
      acmeFile, scope, "acme"
    );
    assertThat(scope.toString()).isEqualTo("provided");
  }
}
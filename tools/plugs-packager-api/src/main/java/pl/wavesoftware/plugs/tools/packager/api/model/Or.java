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

import java.util.function.Consumer;

/**
 * This interface represent a block of code that should be execute as a
 * alternative to the basic operation.
 */
public interface Or extends Consumer<CodeBlock> {
  /**
   * Consumes a block of code
   *
   * @param block a block of code
   * @throws RepackageFailed if repackaging has failed for a some kind of reason
   */
  default void or(CodeBlock block) {
    accept(block);
  }
}

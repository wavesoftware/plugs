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

package pl.wavesoftware.plugs.testing.ansi;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An ANSI composite color code, that consists of multiple {@link AnsiElement}s
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
public final class AnsiComposite implements AnsiElement {
  private static final CharSequence ENCODE_JOIN = ";";

  private final List<AnsiElement> elements;

  private AnsiComposite(AnsiElement... elements) {
    this.elements = Arrays.asList(elements);
  }

  /**
   * A static builder method
   *
   * @param elements elements from which create this composite
   * @return an instance
   */
  public static AnsiComposite of(AnsiElement... elements) {
    return new AnsiComposite(elements);
  }

  @Override
  public String toString() {
    return elements.stream()
      .map(AnsiElement::toString)
      .collect(Collectors.joining(ENCODE_JOIN));
  }
}

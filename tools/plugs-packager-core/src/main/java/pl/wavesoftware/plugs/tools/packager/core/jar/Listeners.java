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

import pl.wavesoftware.plugs.tools.packager.api.jar.ArchiveWriterEvent;
import pl.wavesoftware.plugs.tools.packager.api.jar.ArchiveWriterListener;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class Listeners {
  private final Map<Class<? extends ArchiveWriterEvent>,
    Set<ArchiveWriterListener<?>>> map = new LinkedHashMap<>();

  <E extends ArchiveWriterEvent> void addListener(
    Class<E> eventType, ArchiveWriterListener<E> listener
  ) {
    Set<ArchiveWriterListener<?>> values;
    if (!map.containsKey(eventType)) {
      values = new LinkedHashSet<>();
      map.put(eventType, values);
    } else {
      values = map.get(eventType);
    }
    values.add(listener);
  }

  @SuppressWarnings("unchecked")
  public <T extends ArchiveWriterEvent> Stream<ArchiveWriterListener<T>> get(
    Class<T> eventType
  ) {
    Set<ArchiveWriterListener<?>> values = map.get(eventType);
    Set<ArchiveWriterListener<T>> listeners = new LinkedHashSet<>(values.size());
    for (ArchiveWriterListener<?> value : values) {
      listeners.add((ArchiveWriterListener<T>) value);
    }
    return listeners.stream();
  }
}

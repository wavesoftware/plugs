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

package pl.wavesoftware.plugs.core;

import io.vavr.Tuple;
import io.vavr.collection.HashMap;
import io.vavr.collection.LinkedHashSet;
import io.vavr.collection.Map;

import java.util.AbstractMap;
import java.util.Properties;

/**
 * A default implementation fo framework configuration.
 * <p>
 * It can be created wither from Properties or Map
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
public final class DefaultFrameworkConfiguration implements FrameworkConfiguration {

  private final Map<String, Object> map;

  /**
   * Create default, empty, configuration
   */
  public DefaultFrameworkConfiguration() {
    this(HashMap.empty());
  }

  /**
   * Create configuration based on a properties
   *
   * @param properties a properties to be used as configuration
   */
  public DefaultFrameworkConfiguration(Properties properties) {
    this(
      LinkedHashSet.ofAll(properties.entrySet())
      .map(entry -> new AbstractMap.SimpleImmutableEntry<>(
        entry.getKey().toString(),
        entry.getValue()
      )).toLinkedMap(entry -> Tuple.of(entry.getKey(), entry.getValue()))
    );
  }

  /**
   * Create configuration based on a map values
   *
   * @param map a map to be used as a configuration
   */
  public DefaultFrameworkConfiguration(Map<String, Object> map) {
    this.map = map;
  }

  @Override
  public Map<String, Object> asMap() {
    return map;
  }
}

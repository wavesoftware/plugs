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

import io.vavr.collection.Traversable;
import org.osgi.framework.FrameworkListener;

import java.time.Duration;

/**
 * A default implementation of a framework operation interface
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
public final class DefaultFrameworkOperation implements FrameworkOperation {

  private final FrameworkProducer producer;
  private final FrameworkConfiguration configuration;
  private final Traversable<FrameworkListener> listeners;
  private final Duration stopTimeout;

  /**
   * A default constructor
   *
   * @param producer      a producer of OSGi framework
   * @param configuration a configuration
   * @param listeners     listeners to use on initialization
   * @param stopTimeout   a stop timeout for a framework
   */
  public DefaultFrameworkOperation(
    FrameworkProducer producer,
    FrameworkConfiguration configuration,
    Traversable<FrameworkListener> listeners,
    Duration stopTimeout
  ) {
    this.producer = producer;
    this.configuration = configuration;
    this.listeners = listeners;
    this.stopTimeout = stopTimeout;
  }

  @Override
  public FrameworkProducer getProducer() {
    return producer;
  }

  @Override
  public FrameworkConfiguration getConfiguration() {
    return configuration;
  }

  @Override
  public Traversable<FrameworkListener> getListeners() {
    return listeners;
  }

  @Override
  public Duration getStopTimeout() {
    return stopTimeout;
  }
}

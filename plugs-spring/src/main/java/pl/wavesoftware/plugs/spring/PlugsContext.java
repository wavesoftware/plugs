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

package pl.wavesoftware.plugs.spring;

import io.vavr.collection.LinkedHashSet;
import org.osgi.framework.FrameworkListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import pl.wavesoftware.plugs.core.DefaultFrameworkConfiguration;
import pl.wavesoftware.plugs.core.DefaultFrameworkOperation;
import pl.wavesoftware.plugs.core.FrameworkConfiguration;
import pl.wavesoftware.plugs.core.FrameworkOperation;
import pl.wavesoftware.plugs.core.FrameworkProducer;
import pl.wavesoftware.plugs.core.OsgiContainer;
import pl.wavesoftware.plugs.core.Plugs;
import pl.wavesoftware.plugs.felix.FelixFrameworkProducer;
import pl.wavesoftware.plugs.spring.annotation.Typed;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

/**
 * A Spring configuration context to be used in users configuration with
 * {@link Import} or with {@link EnablePlugs} annotation.
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 * @see EnablePlugs
 * @see Import
 */
@Configuration
public class PlugsContext {

  private static final int DEFAULT_STOP_TIMOUT_IN_MINUTES = 10;

  @Bean
  @ConditionalOnClass(FelixFrameworkProducer.class)
  FrameworkProducer felixFrameworkFactory() {
    return new FelixFrameworkProducer();
  }

  @Bean
  @ConditionalOnMissingBean
  FrameworkConfiguration defaultConfiguration() {
    return new DefaultFrameworkConfiguration();
  }

  @Bean
  @Typed(Plugs.class)
  @ConditionalOnMissingBean
  Duration stopTimeout() {
    return Duration.of(DEFAULT_STOP_TIMOUT_IN_MINUTES, ChronoUnit.MINUTES);
  }

  @Bean
  @ConditionalOnMissingBean
  FrameworkOperation frameworkOperation(
    FrameworkProducer frameworkProducer,
    FrameworkConfiguration configuration,
    @Typed(Plugs.class) Duration stopTimeout,
    @Typed(Plugs.class) Collection<FrameworkListener> listeners
  ) {
    return new DefaultFrameworkOperation(
      frameworkProducer,
      configuration,
      LinkedHashSet.ofAll(listeners),
      stopTimeout
    );
  }

  @Bean
  OsgiContainer osgiContainer(
    FrameworkOperation operation
  ) {
    return new SpringOsgiContainer(operation);
  }
}

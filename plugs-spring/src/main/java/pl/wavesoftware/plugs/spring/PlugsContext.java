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

import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.wavesoftware.plugs.core.OsgiContainer;
import pl.wavesoftware.plugs.core.Plugs;
import pl.wavesoftware.plugs.spring.annotation.Typed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@Configuration
public class PlugsContext {

  @Bean
  @ConditionalOnClass(org.apache.felix.framework.FrameworkFactory.class)
  @Typed(Plugs.class)
  FrameworkFactory felixFrameworkFactory() {
    return new org.apache.felix.framework.FrameworkFactory();
  }

  @Bean
  @Typed(Plugs.class)
  @ConditionalOnMissingBean
  Map<String, String> defaultConfiguration() {
    return new HashMap<>();
  }

  @Bean
  @Typed(Plugs.class)
  @ConditionalOnMissingBean
  long stopTimeout() {
    // 120 sec
    return 1_000L * 120;
  }

  @Bean
  OsgiContainer osgiContainer(
    @Typed(Plugs.class) FrameworkFactory factory,
    @Typed(Plugs.class) Map<String, String> configuration,
    @Typed(Plugs.class) long stopTimeout,
    @Typed(Plugs.class) List<FrameworkListener> listeners
  ) {
    return new SpringOsgiContainer(
      () -> getFramework(factory, configuration, listeners),
      stopTimeout
    );
  }

  private Framework getFramework(
    FrameworkFactory factory,
    Map<String, String> configuration,
    List<FrameworkListener> listeners
  ) {
    Framework framework = factory.newFramework(configuration);
    try {
      framework.init(listeners.toArray(new FrameworkListener[0]));
    } catch (BundleException ex) {
      throw new IllegalStateException(ex);
    }
    return framework;
  }
}

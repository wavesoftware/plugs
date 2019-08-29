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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.launch.Framework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.wavesoftware.plugs.core.OsgiContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@ExtendWith(SpringExtension.class)
@ContextHierarchy({
  @ContextConfiguration(classes = FrameworkEventCollectingContext.class),
  @ContextConfiguration(classes = PlugsTestContext.class)
})
class PlugsContextIT {

  @Autowired
  private OsgiContainer container;

  @Autowired
  private Collector<FrameworkEvent> eventCollector;

  @Autowired
  private ApplicationEventPublisher publisher;

  @Autowired
  private ApplicationContext applicationContext;

  @Test
  void osgiContainer() {
    // when
    Framework framework = container.getFramework();

    // then
    String name = framework.getSymbolicName();
    assertThat(name).isEqualTo("org.apache.felix.framework");
    assertThat(framework.getState()).isEqualTo(Bundle.ACTIVE);
    assertThat(eventCollector.getCollected()).isEmpty();
  }

  @Test
  @DirtiesContext
  void refreshContext() {
    // when
    assertThatCode(() ->
      publisher.publishEvent(new ContextClosedEvent(applicationContext))
    ).doesNotThrowAnyException();
  }
}

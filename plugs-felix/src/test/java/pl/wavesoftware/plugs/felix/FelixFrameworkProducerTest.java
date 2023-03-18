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

package pl.wavesoftware.plugs.felix;

import io.vavr.collection.List;
import io.vavr.collection.Traversable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.launch.Framework;
import pl.wavesoftware.plugs.core.DefaultFrameworkConfiguration;
import pl.wavesoftware.plugs.core.FrameworkConfiguration;
import pl.wavesoftware.plugs.core.FrameworkProducer;

import javax.annotation.Nullable;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
class FelixFrameworkProducerTest {

  private static final Duration STOP_TIMEOUT = Duration.ofMinutes(5);
  @Mock
  private FrameworkListener listener;

  @Nullable
  private Framework framework;

  @AfterEach
  void after() throws BundleException, InterruptedException {
    Mockito.verifyNoMoreInteractions(listener);
    Mockito.validateMockitoUsage();

    if (framework != null) {
      framework.stop();
      framework.waitForStop(STOP_TIMEOUT.toMillis());
    }
  }

  @Test
  void provide() {
    // given
    FrameworkProducer producer = new FelixFrameworkProducer();

    // when
    FrameworkConfiguration configuration = new DefaultFrameworkConfiguration();
    Traversable<FrameworkListener> listeners = List.of(listener);
    framework = producer.provide(configuration, listeners);

    // then
    assertThat(framework).isNotNull();
    await().atMost(Duration.ofMinutes(5))
      .until(() -> framework.getState() == Bundle.ACTIVE);
    assertThat(framework.getState()).isEqualTo(Bundle.ACTIVE);
  }
}

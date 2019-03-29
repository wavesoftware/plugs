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

package pl.wavesoftware.example.code;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@ExtendWith(MockitoExtension.class)
class ActivatorTest {

  private Activator activator = new Activator();

  @Mock
  private BundleContext bundleContext;

  @Mock
  private ServiceRegistration<HelloService> serviceRegistration;

  @Captor
  private ArgumentCaptor<HelloService> helloServiceArgumentCaptor;

  @Test
  void startAndStop() {
    // then
    verify(bundleContext).registerService(
      eq(HelloService.class),
      helloServiceArgumentCaptor.capture(),
      isNull()
    );
    HelloService service = helloServiceArgumentCaptor.getValue();
    assertThat(service.hello()).isEqualTo("Hello from Plug!");
  }

  @BeforeEach
  void before() {
    when(bundleContext.registerService(
      eq(HelloService.class), any(HelloService.class), any()
    )).thenReturn(serviceRegistration);
    activator.start(bundleContext);
  }

  @AfterEach
  void after() {
    activator.stop(bundleContext);
    verify(serviceRegistration).unregister();

    Mockito.validateMockitoUsage();
    Mockito.verifyNoMoreInteractions(bundleContext, serviceRegistration);
  }

}

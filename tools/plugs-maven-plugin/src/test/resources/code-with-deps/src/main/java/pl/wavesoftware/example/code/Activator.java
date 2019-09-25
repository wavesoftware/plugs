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

import org.apiguardian.api.API;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.annotation.Nullable;
import java.util.Optional;

import static org.apiguardian.api.API.Status.STABLE;

@API(status = STABLE)
final class Activator implements BundleActivator {

  @Nullable
  private ConfigurableApplicationContext applicationContext;
  @Nullable
  private ServiceRegistration<HelloService> registration;

  public void start(BundleContext context) {
    applicationContext = new AnnotationConfigApplicationContext(Context.class);
    registration = context.registerService(
      HelloService.class,
      applicationContext.getBean(HelloService.class),
      null
    );
  }

  public void stop(BundleContext context) {
    Optional.ofNullable(registration)
      .ifPresent(ServiceRegistration::unregister);
    Optional.ofNullable(applicationContext)
      .ifPresent(ConfigurableApplicationContext::close);
  }
}

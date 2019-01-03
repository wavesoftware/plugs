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

import io.vavr.Lazy;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
public final class PlugsOsgiContainer implements OsgiContainer {

  private static final Logger LOGGER =
    LoggerFactory.getLogger(PlugsOsgiContainer.class);

  private final Lazy<Framework> frameworkLazy;
  private final long stopTimeout;

  public PlugsOsgiContainer(Supplier<Framework> frameworkLazy, long stopTimeout) {
    this.frameworkLazy = Lazy.of(() -> {
      LOGGER.info("Starting Plugs OSGi container...");
      Framework framework = frameworkLazy.get();
      LOGGER.info(
        "Plugs OSGi container stated and initialized: {}",
        framework.getSymbolicName()
      );
      return framework;
    });
    this.stopTimeout = stopTimeout;
  }

  @Override
  public Framework getFramework() {
    return frameworkLazy.get();
  }

  @Override
  public void dispose() {
    try {
      Framework framework = getFramework();
      LOGGER.info("Stopping Plugs OSGi container...");
      framework.stop();
      framework.waitForStop(stopTimeout);
      LOGGER.info("Plugs OSGi container stopped.");
    } catch (InterruptedException ex) {
      LOGGER.error("Not enough time to stop OSGi container", ex);
      Thread.currentThread().interrupt();
    } catch (BundleException ex) {
      throw new IllegalStateException(ex);
    }
  }
}

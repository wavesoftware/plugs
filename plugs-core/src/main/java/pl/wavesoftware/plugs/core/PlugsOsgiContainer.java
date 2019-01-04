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
import org.osgi.framework.launch.Framework;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.helpers.MessageFormatter;
import pl.wavesoftware.eid.utils.UnsafeProcedure;

import java.time.Duration;
import java.util.function.Supplier;

import static pl.wavesoftware.eid.utils.EidExecutions.tryToExecute;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
public final class PlugsOsgiContainer implements OsgiContainer {

  private final Logger logger;
  private final Supplier<Framework> frameworkLazy;
  private final Duration stopTimeout;

  public PlugsOsgiContainer(
    ILoggerFactory loggerFactory,
    Supplier<Framework> frameworkSupplier,
    Duration stopTimeout
  ) {
    logger = loggerFactory.getLogger(PlugsOsgiContainer.class.getName());
    this.frameworkLazy = Lazy.of(() -> {
      logger.info("Starting Plugs OSGi container...");
      Framework framework = frameworkSupplier.get();
      logger.info(
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
      logger.info("Stopping Plugs OSGi container...");
      tryToExecute((UnsafeProcedure) framework::stop, "20190104:202359");
      framework.waitForStop(getStopTimeoutAsMilliseconds());
      logger.info("Plugs OSGi container stopped.");
    } catch (InterruptedException ex) {
      logger.error(
        MessageFormatter.format(
          "Not enough time to stop OSGi container, timeout was: {}",
          stopTimeout
        ).getMessage(),
        ex
      );
      Thread.currentThread().interrupt();
    }
  }

  private long getStopTimeoutAsMilliseconds() {
    return stopTimeout.toMillis();
  }
}

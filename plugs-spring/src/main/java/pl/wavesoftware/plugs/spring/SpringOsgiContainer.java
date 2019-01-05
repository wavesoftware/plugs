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

import org.osgi.framework.launch.Framework;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import pl.wavesoftware.plugs.core.FrameworkOperation;
import pl.wavesoftware.plugs.core.OsgiContainer;
import pl.wavesoftware.plugs.core.PlugsOsgiContainer;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class SpringOsgiContainer implements OsgiContainer {

  private final OsgiContainer delegate;

  SpringOsgiContainer(FrameworkOperation operation) {
    delegate = new PlugsOsgiContainer(operation);
  }

  @Override
  public Framework getFramework() {
    return delegate.getFramework();
  }

  @Override
  @EventListener(ContextClosedEvent.class)
  public void dispose() {
    delegate.dispose();
  }
}

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

package pl.wavesoftware.maven.testing.junit5;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.testing.MojoRule;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import javax.annotation.Nullable;
import java.util.Optional;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class MojoFactoryImpl implements MojoFactory {

  private final MojoRule rule;

  @Nullable
  private MavenProjectCustomizer customizer;
  private MojoConfigurator configurator = new DefaultMojoConfigurator(
    () -> Optional.ofNullable(customizer)
  );

  MojoFactoryImpl(MojoRule rule) {
    this.rule = rule;
  }

  @Override
  public MojoFactory configurator(MojoConfigurator configurator) {
    this.configurator = configurator;
    return this;
  }

  @Override
  public MojoFactory customizer(MavenProjectCustomizer customizer) {
    this.customizer = customizer;
    return this;
  }

  @Override
  public <T extends AbstractMojo> MojoBuilder<T> builder(Class<T> mojoType) {
    return new MojoBuilderImpl<>(rule, mojoType, configurator);
  }

  @Override
  public <T> T lookup(Class<T> role) throws ComponentLookupException {
    return rule.lookup(role);
  }
}

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

package pl.wavesoftware.plugs.maven.generator;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugins.annotations.Mojo;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.reflect.Parameter;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class MojoRuleExtension implements BeforeAllCallback, ParameterResolver {

  private static final Namespace MOJO_NS = Namespace.create(Mojo.class);

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    MojoRule rule = new MojoRule(new MojoDelegate());
    context.getStore(MOJO_NS).put(MojoRule.class, rule);
  }

  @Override
  public boolean supportsParameter(
    ParameterContext parameterContext,
    ExtensionContext extensionContext
  ) throws ParameterResolutionException {
    Parameter parameter = parameterContext.getParameter();
    return parameter
      .getType()
      .isAssignableFrom(MojoBuilderFactory.class);
  }

  @Override
  public Object resolveParameter(
    ParameterContext parameterContext,
    ExtensionContext extensionContext
  ) throws ParameterResolutionException {
    MojoRule mojoRule = extensionContext
      .getStore(MOJO_NS)
      .get(MojoRule.class, MojoRule.class);
    return new MojoBuilderFactoryImpl(mojoRule);
  }

  private static final class MojoDelegate extends AbstractMojoTestCase {
    MojoDelegate() throws Exception {
      setUp();
    }
  }
}

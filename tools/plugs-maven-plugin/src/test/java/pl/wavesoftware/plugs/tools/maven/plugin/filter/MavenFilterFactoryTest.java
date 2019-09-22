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

package pl.wavesoftware.plugs.tools.maven.plugin.filter;

import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.plugin.testing.stubs.DefaultArtifactHandlerStub;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pl.wavesoftware.maven.testing.junit5.MojoExtension;
import pl.wavesoftware.maven.testing.junit5.MojoFactory;
import pl.wavesoftware.plugs.tools.maven.plugin.mapper.ArtifactMapper;
import pl.wavesoftware.plugs.tools.maven.plugin.model.Exclude;
import pl.wavesoftware.plugs.tools.maven.plugin.model.Include;
import pl.wavesoftware.plugs.tools.packager.api.model.Artifact;
import pl.wavesoftware.plugs.tools.packager.api.model.Filter;
import pl.wavesoftware.plugs.tools.packager.sample.PackagerSamplerContext;
import pl.wavesoftware.plugs.tools.packager.sample.artifact.AnsiTestingArtifact;
import pl.wavesoftware.plugs.tools.packager.sample.artifact.EidArtifact;
import pl.wavesoftware.plugs.tools.packager.sample.artifact.HibernateArtifact;
import pl.wavesoftware.plugs.tools.packager.sample.artifact.Jsr305Artifact;
import pl.wavesoftware.plugs.tools.packager.sample.artifact.Log4j2TestingArtifact;
import pl.wavesoftware.plugs.tools.packager.sample.artifact.OsgiCoreArtifact;
import pl.wavesoftware.plugs.tools.packager.sample.artifact.SpringContextArtifact;
import pl.wavesoftware.plugs.tools.packager.sample.artifact.SpringCoreArtifact;
import pl.wavesoftware.plugs.tools.packager.sample.artifact.SpringJclArtifact;
import pl.wavesoftware.sampler.api.SamplerContext;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@ExtendWith({
  MojoExtension.class,
  SpringExtension.class
})
@ContextConfiguration(classes = PackagerSamplerContext.class)
class MavenFilterFactoryTest {

  @Autowired
  private SamplerContext samplerContext;
  private ArtifactMapper artifactMapper;

  @Test
  void create(MojoFactory mojoFactory) throws ComponentLookupException {
    // given
    artifactMapper = mojoFactory.lookup(ArtifactMapper.class);
    MavenFilterFactory filterFactory = new MavenFilterFactory(artifactMapper);
    List<Include> includes = Arrays.asList(
      newInclude("pl.wavesoftware"),
      newInclude("pl.wavesoftware.testing"),
      newInclude("org.springframework")
    );
    List<Exclude> excludes = Arrays.asList(
      newExclude("pl.wavesoftware.testing", "ansi"),
      newExclude("org.springframework", "spring-jcl")
    );
    Set<Artifact> artifacts = HashSet.of(
      EidArtifact.class,
      Log4j2TestingArtifact.class,
      AnsiTestingArtifact.class,
      SpringCoreArtifact.class,
      SpringContextArtifact.class,
      SpringJclArtifact.class,
      HibernateArtifact.class,
      Jsr305Artifact.class,
      OsgiCoreArtifact.class
    ).map(samplerContext::get).map(this::mavenize);
    Set<Artifact> expected = HashSet.of(
      EidArtifact.class,
      Log4j2TestingArtifact.class,
      SpringCoreArtifact.class,
      SpringContextArtifact.class
    ).map(samplerContext::get);

    // when
    Filter filter = filterFactory.create(includes, excludes);
    Set<Artifact> filtered = filter.filterDependencies(artifacts);

    // then
    assertThat(filtered).hasSize(4);
    assertThat(filtered)
      .usingElementComparator(new ArtifactComparator())
      .containsOnlyElementsOf(expected);
  }

  private Artifact mavenize(Artifact artifact) {
    ArtifactHandler handler = new DefaultArtifactHandlerStub(
      artifact.type().extension(), ""
    );
    DefaultArtifact maven = new DefaultArtifact(
      artifact.group(), artifact.name(), artifact.version().getValue(),
      "compile", artifact.type().extension(), null, handler
    );
    return artifactMapper.generalize(maven);
  }

  private Exclude newExclude(String groupId, String artifactId) {
    Exclude exclude = new Exclude();
    exclude.setGroupId(groupId);
    exclude.setArtifactId(artifactId);
    return exclude;
  }

  private Include newInclude(String groupId) {
    Include inc = new Include();
    inc.setGroupId(groupId);
    return inc;
  }

  private static final class ArtifactComparator implements Comparator<Artifact> {
    @Override
    public int compare(Artifact a1, Artifact a2) {
      return a1.group().compareTo(a2.group())
        + a1.name().compareTo(a2.name())
        + a1.version().compareTo(a2.version());
    }
  }
}

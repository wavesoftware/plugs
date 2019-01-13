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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import pl.wavesoftware.plugs.maven.generator.filter.Filter;
import pl.wavesoftware.plugs.maven.generator.filter.FilterFactory;
import pl.wavesoftware.plugs.maven.generator.model.BuildFailure;
import pl.wavesoftware.plugs.maven.generator.model.Exclude;
import pl.wavesoftware.plugs.maven.generator.model.ExecutionConfiguration;
import pl.wavesoftware.plugs.maven.generator.model.Include;
import pl.wavesoftware.plugs.maven.generator.model.PlugsMojoException;
import pl.wavesoftware.plugs.maven.generator.packager.PlugPackager;
import pl.wavesoftware.plugs.maven.generator.packager.PlugPackagerFactory;

import javax.inject.Inject;
import java.io.File;
import java.util.List;

/**
 * A main mojo to generate plug modules
 *
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
@Mojo(
  name = PackagePlugMojo.GOAL,
  defaultPhase = LifecyclePhase.PACKAGE,
  requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
  requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME
)
public final class PackagePlugMojo extends AbstractMojo {

  @SuppressWarnings("WeakerAccess")
  @API(status = Status.STABLE)
  public static final String GOAL = "package";

  /**
   * The package factory
   */
  private final PlugPackagerFactory packagerFactory;

  /**
   * The filter factory
   */
  private final FilterFactory filterFactory;

  /**
   * Maven's project helper
   */
  private final MavenProjectHelper projectHelper;

  /**
   * The Maven project.
   *
   * @since 0.1.0
   */
  @Parameter(
    defaultValue = "${project}",
    required = true,
    readonly = true
  )
  private MavenProject project;

  /**
   * Classifier to add to the repackaged archive. If not given, classifier
   * "plug" will be used. If given, the classifier will also be used
   * to determine the source archive to repackage: if an artifact with that
   * classifier already exists, it will be used as source and replaced. If no
   * such artifact exists, the main artifact will be used as source and the
   * repackaged archive will be attached as a supplemental artifact with that
   * classifier. Attaching the artifact allows to deploy it alongside to the
   * original one, see <a href=
   * "http://maven.apache.org/plugins/maven-deploy-plugin/examples/deploying-with-classifiers.html"
   * > the maven documentation for more details</a>.
   *
   * @since 0.1.0
   */
  @Parameter(property = "plugs.classifier", defaultValue = "plug")
  private String classifier = "plug";

  /**
   * Attach the repackaged archive to be installed and deployed.
   *
   * @since 0.1.0
   */
  @Parameter(property = "plugs.attach", defaultValue = "true")
  private boolean attach = true;

  /**
   * Directory containing the generated archive.
   *
   * @since 0.1.0
   */
  @Parameter(defaultValue = "${project.build.directory}", required = true)
  private File outputDirectory;

  /**
   * Name of the generated archive.
   *
   * @since 0.1.0
   */
  @Parameter(defaultValue = "${project.build.finalName}", readonly = true)
  private String finalName;

  /**
   * Skip the execution.
   *
   * @since 0.1.0
   */
  @Parameter(property = "plugs.skip", defaultValue = "false")
  private boolean skip;

  /**
   * Collection of artifact definitions to include. The {@link Include} element
   * defines a {@code groupId} and {@code artifactId} mandatory properties
   * and an optional {@code classifier} property.
   *
   * @since 0.1.0
   */
  @Parameter(property = "plugs.includes")
  private List<Include> includes;

  /**
   * Collection of artifact definitions to exclude. The {@link Exclude} element
   * defines a {@code groupId} and {@code artifactId} mandatory properties
   * and an optional {@code classifier} property.
   *
   * @since 0.1.0
   */
  @Parameter(property = "plugs.excludes")
  private List<Exclude> excludes;

  @Inject
  PackagePlugMojo(
    PlugPackagerFactory packagerFactory,
    FilterFactory filterFactory,
    MavenProjectHelper projectHelper
  ) {
    this.packagerFactory = packagerFactory;
    this.filterFactory = filterFactory;
    this.projectHelper = projectHelper;
  }

  @Override
  public void execute() throws PlugsMojoException {
    if ("pom".equals(project.getPackaging())) {
      getLog().debug(GOAL + " goal could not be applied to pom project.");
      return;
    }
    if (this.skip) {
      getLog().debug("skipping packaging as per configuration.");
      return;
    }
    build();
  }

  private void build() throws PlugsMojoException {
    Filter filter = filterFactory.create(includes, excludes);
    ExecutionConfiguration configuration = getConfiguration();
    PlugPackager packager = packagerFactory.create(configuration, filter);
    if (packager.needsRepackaging()) {
      buildAndReport(packager, configuration);
    } else {
      getLog().info("Plug package " + finalName + " is up-to-date.");
    }
  }

  private void buildAndReport(
    PlugPackager packager,
    ExecutionConfiguration configuration
  ) throws PlugsMojoException {
    try {
      packager.repackageAsPlug();
      getLog().info("Building of " + finalName + " was successful.");
      attachIfNeeded(packager, configuration);
    } catch (PlugsMojoException ex) {
      getLog().error("Building of " + finalName + " has failed.");
      ex.getFailures().ifPresent(failures -> {
        getLog().error("Reasons of failure:");
        for (BuildFailure failure : failures) {
          getLog().error(" * " + failure.describe());
        }
      });
      throw ex;
    }
  }

  private void attachIfNeeded(
    PlugPackager packager,
    ExecutionConfiguration configuration
  ) {
    if (configuration.shouldAttach()) {
      projectHelper.attachArtifact(
        project,
        packager.getTargetFile(),
        configuration.getClassifier()
      );
      getLog().info("Artifact attached to the build: " + packager.getTargetFile());
    } else {
      getLog().debug("Skipping attach of artifact: " + packager.getTargetFile());
    }
  }

  private ExecutionConfiguration getConfiguration() {
    return new DefaultExecutionConfiguration(
      project,
      classifier,
      attach,
      outputDirectory,
      finalName
    );
  }
}

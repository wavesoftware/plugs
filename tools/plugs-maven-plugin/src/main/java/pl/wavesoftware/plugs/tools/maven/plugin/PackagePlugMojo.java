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

package pl.wavesoftware.plugs.tools.maven.plugin;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apiguardian.api.API;
import org.apiguardian.api.API.Status;
import org.slf4j.Logger;
import pl.wavesoftware.plugs.tools.maven.plugin.model.Exclude;
import pl.wavesoftware.plugs.tools.maven.plugin.model.Include;
import pl.wavesoftware.plugs.tools.packager.core.Packager;
import pl.wavesoftware.plugs.tools.packager.core.model.CodeBlock;
import pl.wavesoftware.plugs.tools.packager.core.model.Filter;
import pl.wavesoftware.plugs.tools.packager.core.model.PackagerConfiguration;
import pl.wavesoftware.plugs.tools.packager.core.model.PackagerCoordinates;
import pl.wavesoftware.plugs.tools.packager.core.model.RepackageFailed;

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
final class PackagePlugMojo extends AbstractMojo {

  @API(status = Status.STABLE)
  public static final String GOAL = "package";

  /**
   * The package factories
   */
  private final PackagerFactories factories;

  /**
   * The logger
   */
  private final Logger logger;

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
   * The Maven session.
   *
   * @since 0.1.0
   */
  @Parameter(
    defaultValue = "${session}",
    required = true,
    readonly = true
  )
  private MavenSession session;

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
    PackagerFactories factories,
    MavenProjectHelper projectHelper
  ) {
    this.factories = factories;
    this.logger = factories.logger().create(this::getLog);
    this.projectHelper = projectHelper;
  }

  @Override
  public void execute() throws MojoExecutionException {
    if ("pom".equals(project.getPackaging())) {
      logger.info(
        "Skipping, {} goal could not be applied to pom project.",
        GOAL
      );
      return;
    }
    if (this.skip) {
      logger.info("Skipping packaging as per configuration.");
      return;
    }
    maybePackage();
  }

  private void maybePackage() throws MojoExecutionException {
    Filter filter = factories.filter().create(includes, excludes);
    PackagerConfiguration configuration = createConfiguration();
    Packager packager = factories.packager().create(
      configuration, filter
    );
    PackagerCoordinates coordinates = configuration.coordinates();
    logger.info("Building plug: {}", coordinates.targetPath());
    repackageAndReport(packager, configuration).or(() -> logger.info(
      "Plug package {} is up-to-date.",
      coordinates.targetPath().getFileName()
    ));
    attachArtifactIfNeeded(configuration);
  }

  @SuppressWarnings("squid:S1162")
  private MojoOr repackageAndReport(
    Packager packager,
    PackagerConfiguration configuration
  ) {
    PackagerCoordinates coordinates = configuration.coordinates();
    return codeBlock -> {
      try {
        packager.repackage().or(codeBlock);
        logger.debug("Building of {} was successful.", coordinates.targetPath());
      } catch (RepackageFailed ex) {
        logger.error(
          "Building of {} has failed: {}",
          coordinates.targetPath(),
          ex.getMessage()
        );
        throw new MojoExecutionException("Repackage FAILURE!", ex);
      }
    };
  }

  private void attachArtifactIfNeeded(PackagerConfiguration configuration) {
    PackagerCoordinates coordinates = configuration.coordinates();
    if (configuration.shouldAttach()) {
      projectHelper.attachArtifact(
        project,
        coordinates.targetPath().toFile(),
        configuration.project().classifier()
      );
      logger.debug("Artifact attached to the build: {}", coordinates.targetPath());
    } else {
      logger.info("Skipping attaching of artifact: {}",
        coordinates.targetPath());
    }
  }

  private PackagerConfiguration createConfiguration() {
    return factories.configuration().create(
      project,
      session,
      logger,
      classifier,
      attach,
      outputDirectory,
      finalName
    );
  }

  private interface MojoOr {
    void or(CodeBlock codeBlock) throws MojoExecutionException;
  }
}

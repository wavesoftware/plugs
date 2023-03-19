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

import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulationException;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.wavesoftware.eid.utils.EidPreconditions;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

import static pl.wavesoftware.eid.utils.EidPreconditions.checkNotNull;

/**
 * @author <a href="mailto:krzysztof.suszynski@wavesoftware.pl">Krzysztof Suszynski</a>
 * @since 0.1.0
 */
final class DefaultMojoConfigurator implements MojoConfigurator {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMojoConfigurator.class);

  private final Supplier<Optional<MavenProjectCustomizer>> customizer;

  DefaultMojoConfigurator(Supplier<Optional<MavenProjectCustomizer>> customizer) {
    this.customizer = customizer;
  }

  @Override
  public MavenSession getMavenSession(MojoRule rule, Path pomDirectory) throws Exception {
    // create execution request
    MavenExecutionRequest request = createMavenExecutionRequest(rule);

    // setup with pom
    MavenProject project = readMavenProject(
      rule, request, pomDirectory.toFile()
    );

    customizer.get().ifPresent(customizer -> customizer.customize(project));

    // Generate session
    return newMavenSession(rule, request, project);
  }

  @Override
  public MojoExecution getMojoExecution(MojoRule rule, String goal) {
    // Generate PackagerConfiguration and Mojo for testing
    return rule.newMojoExecution(goal);
  }

  private MavenExecutionRequest createMavenExecutionRequest(MojoRule rule)
    throws ComponentLookupException, MavenExecutionRequestPopulationException,
    SettingsBuildingException {

    PlexusContainer container = rule.getContainer();
    MavenExecutionRequest request = new DefaultMavenExecutionRequest();
    MavenExecutionRequestPopulator requestPopulator =
      container.lookup(MavenExecutionRequestPopulator.class);
    SettingsBuilder settingsBuilder = container.lookup(SettingsBuilder.class);
    SettingsBuildingResult sbr = settingsBuilder.build(
      new DefaultSettingsBuildingRequest()
    );
    sbr.getProblems().forEach(problem ->
      LOGGER.error(problem.getMessage(), problem.getException())
    );
    EidPreconditions.checkState(sbr.getProblems().isEmpty(), "20230320:004212");
    Settings settings = sbr.getEffectiveSettings();
    requestPopulator.populateFromSettings(request, settings);
    requestPopulator.populateDefaults(request);
    return request;
  }

  private MavenProject readMavenProject(
    MojoRule rule, MavenExecutionRequest request, File basedir
  ) throws Exception {
    File pom = new File(basedir, "pom.xml");
    request.setBaseDirectory(basedir);
    ProjectBuildingRequest configuration = request.getProjectBuildingRequest();
    configuration.setRepositorySession(new DefaultRepositorySystemSession());
    MavenProject project = rule.lookup(ProjectBuilder.class)
      .build(pom, configuration)
      .getProject();
    checkNotNull(project, "20190331:130819");
    return project;
  }

  private MavenSession newMavenSession(
    MojoRule rule, MavenExecutionRequest request, MavenProject project
  ) {
    MavenExecutionResult result = new DefaultMavenExecutionResult();

    MavenSession session = new MavenSession(
      rule.getContainer(),
      MavenRepositorySystemUtils.newSession(),
      request,
      result
    );
    session.setCurrentProject(project);
    session.setProjects(Collections.singletonList(project));
    return session;
  }
}

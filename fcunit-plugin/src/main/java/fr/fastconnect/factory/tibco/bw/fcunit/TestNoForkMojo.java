/**
 * (C) Copyright 2011-2015 FastConnect SAS
 * (http://www.fastconnect.fr/) and others.
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
package fr.fastconnect.factory.tibco.bw.fcunit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import fr.fastconnect.factory.tibco.bw.fcunit.jaxws.Settings;
import fr.fastconnect.factory.tibco.bw.maven.bwengine.AbstractServiceEngineMojo;

/**
 * <p>
 * This goal runs the unit tests of a FCUnit-enabled TIBCO BusinessWorks
 * project.
 * </p>
 *
 * @author Mathieu Debove
 *
 */
@Mojo( name="test-no-fork", defaultPhase=LifecyclePhase.TEST, requiresDependencyResolution = ResolutionScope.COMPILE)
public class TestNoForkMojo extends AbstractServiceEngineMojo {

    /**
     * Name of the generated artifact (without file extension).
     */
    @Parameter(property = "project.build.finalName", required = true)
    protected String finalName;

    /**
     * Optional href link to use in HTML output on the top-left caption
     */
    @Parameter(property = "fcunit.homePage")
    protected String homePage;

	@Parameter (property = "fcunit.timeout", defaultValue = "100")
	private int timeOut; // in seconds

	@Parameter (property = "fcunit.retryInterval", defaultValue = "1")
	private int retryInterval; // interval between FCUnit WebService call, in seconds

	protected final static String FCUNIT_SERVICE_NAME = "FCUnit";
	protected final static String FCUNIT_FAILURE = "The execution of the tests failed";
	protected final static String FCUNIT_TIMEOUT = "The execution of the tests timed out";
	protected final static String FCUNIT_SUCCESSFUL = "All the tests were executed successfully";
	protected final static String FCUNIT_RESULTS = "The results are found in : ";

	protected static final String JUNIT_JAR_ALIAS = "junit:junit:jar:4.8.2:compile";
	protected static final String XMLUNIT_JAR_ALIAS = "xmlunit:xmlunit:jar:1.3:compile";

	/**
	 * Whether the FCUnit tests on the project should be skipped or not.
	 */
	@Parameter (property = "bw.tests.skip")
	private boolean skipTests;

	private String bwEnginePort;

	public static boolean skip(MavenProject mavenProject) {
		return !BWEAR_TYPE.equals(mavenProject.getPackaging()) &&
			   !PROJLIB_TYPE.equals(mavenProject.getPackaging());
	}

	public boolean skip() {
		return skip(this.getProject()) ||
			   skipTests;
	}

	private void checkWorkingDirectory() throws MojoExecutionException {
		if (!directory.exists()) {
			directory.mkdir();
		}

		if (!directory.isDirectory()) {
			throw new MojoExecutionException("Working directory for Maven build can't be found or created", new FileNotFoundException());
		}
	}

	private void checkOutputDirectory() throws MojoExecutionException {
		if (!testDirectory.exists()) {
			testDirectory.mkdir();
		}

		if (!testDirectory.isDirectory()) {
			throw new MojoExecutionException("Output directory for tests can't be found or created", new FileNotFoundException());
		}
	}

	private boolean foundFCUnitDependency() {
		try {
			List<Dependency> dependencies = getDependencies(PROJLIB_TYPE, false);

			for (Dependency dependency : dependencies) {
				if ("fcunit".equals(dependency.getArtifactId())) {
					return true;
				}
			}
		} catch (IOException e) {
			return false;
		}
		return false;
	}

	public void execute() throws MojoExecutionException {
		if (skip()) {
			getLog().info(SKIPPING);
			return;
		}

		enableTestScope();

		// this try-block can be removed when BWMaven > 2.4.1 is released
		try {
			File systemDesigner5Prefs = new File(System.getProperty("user.home") + "/" + TIBCO_HOME_DIR + "/" + DESIGNER5_PREFS);
			getLog().info(systemDesigner5Prefs.getAbsolutePath());
			if (!systemDesigner5Prefs.exists()) {
				systemDesigner5Prefs.getParentFile().mkdirs();
				systemDesigner5Prefs.createNewFile(); // touch the system file to avoid crash
			}

		} catch (IOException e) {
			throw new MojoExecutionException(e.getLocalizedMessage(), e);
		}

		if (!foundFCUnitDependency()) {
			getLog().info("FCUnit tests require the \"fcunit:projlib\" dependency.");
			return;
		}

		checkWorkingDirectory();
		checkOutputDirectory();

		super.execute();
	}

	@Override
	public void initServiceAgent() throws MojoExecutionException {
		bwEnginePort = getFreePort().toString();

		getLog().debug("engine port : " + bwEnginePort);

		try {
			serviceAgent = new FCUnitService(bwEnginePort);
		} catch (Exception e) {
			throw new MojoExecutionException(FCUNIT_FAILURE, e);
		}
	}

	@Override
	public void executeServiceMethods() throws MojoExecutionException {
		Settings settings = new Settings();
		settings.setScope(null);
		settings.setSuitePattern(null);
		settings.setCasePattern(null);
		settings.setExportToFiles(true);
		settings.setExportDirectory(testDirectory.getAbsolutePath());
		settings.setOutputFileName(finalName);

		// exécution des tests en appelant la méthode "runAllTests" du Service Agent de FCUnit
		if (((FCUnitService) serviceAgent).runAllTests(settings)) {
			getLog().info(FCUNIT_SUCCESSFUL);
			getLog().info(FCUNIT_RESULTS + "\"" + testDirectory.getAbsolutePath() + "\"");
		}
	}

	@Override
	public String getBWEnginePort() {
		return bwEnginePort;
	}

	@Override
	public int getRetryInterval() {
		return retryInterval;
	}

	@Override
	public int getTimeOut() {
		return timeOut;
	}

	@Override
	public String getServiceName() {
		return FCUNIT_SERVICE_NAME;
	}

	@Override
	public String getServiceFailureMessage() {
		return FCUNIT_FAILURE;
	}

	@Override
	public String getServiceTimeoutMessage() {
		return FCUNIT_TIMEOUT;
	}

	@Override
	protected File getProjectToRunPath() {
		return testSrcDirectory;
	}

}

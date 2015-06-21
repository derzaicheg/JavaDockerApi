package com.skozlov.breed.common.install.remote;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.github.dockerjava.api.model.Info;
import com.jcraft.jsch.JSchException;
import com.skozlov.breed.common.config.TestSettings;
import com.skozlov.breed.common.config.TestSettings.PropertyNotExistsException;
import com.skozlov.breed.common.helpers.docker.DockerHelper;
import com.skozlov.breed.common.helpers.docker.DockerHelperImpl;
import com.skozlov.breed.common.helpers.ssh.SshHelper;
import com.skozlov.breed.common.helpers.ssh.SshHelperImpl;
import com.skozlov.breed.labrador.util.LabradorTestProperties;

/**
 * Makes the machine ready to run the tests remotely
 */
@Test(groups = "install-remote")
public class RemoteTestSetupTest {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * Test to install docker on remote host
	 * 
	 * @author skozlov
	 * @throws Exception
	 */
	@Test
	public void installDocker() throws Exception {
		DockerInstaller dockerInstaller = new DockerInstaller(this.logger);
		boolean isInstalled = dockerInstaller.isInstalled();
		if (!isInstalled) {
			dockerInstaller.install();
		}
	}

	@Test
	public void disableFirewall() throws JSchException, IOException, PropertyNotExistsException, InterruptedException {
		TestSettings testSettings = new TestSettings(logger);
		SshHelper sshHelper = new SshHelperImpl(
				testSettings.getProperty(LabradorTestProperties.DOCKER_SERVER_HOST),
				testSettings.getProperty(LabradorTestProperties.DOCKER_SERVER_USR),
				testSettings.getProperty(LabradorTestProperties.DOCKER_SERVER_PWD),
				logger);
		sshHelper.execSudo("sudo ufw disable");
	}
	
	@Test(dependsOnMethods = {"disableFirewall", "installDocker"})
	public void startDockerAgent() throws PropertyNotExistsException, IOException, JSchException, InterruptedException{
		//start or restart docker agent
		DockerInstaller dockerInstaller = new DockerInstaller(logger);
		dockerInstaller.startDockerAgent();
		//check that dockerangent works
		DockerHelper dockerHelper = new DockerHelperImpl(logger);
		Info info = dockerHelper.getDockerInfo();
		System.out.println(info.toString());
		
	}
}

package com.skozlov.breed.common.install.remote;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.JSchException;
import com.skozlov.breed.common.config.TestSettings;
import com.skozlov.breed.common.config.TestSettings.PropertyNotExistsException;
import com.skozlov.breed.common.helpers.ssh.SshHelper;
import com.skozlov.breed.common.helpers.ssh.SshHelperImpl;
import com.skozlov.breed.common.install.backend.BreedProductInstaller;
import com.skozlov.breed.labrador.util.LabradorTestProperties;

public class DockerInstaller extends BreedProductInstaller {

	private String host;
	private String user;
	private String pwd;

	public DockerInstaller(Logger logger) throws PropertyNotExistsException,
			IOException {
		super(logger);
		TestSettings testSettings = new TestSettings(logger);
		this.host = testSettings
				.getProperty(LabradorTestProperties.DOCKER_SERVER_HOST);
		this.user = testSettings
				.getProperty(LabradorTestProperties.DOCKER_SERVER_USR);
		this.pwd = testSettings
				.getProperty(LabradorTestProperties.DOCKER_SERVER_PWD);
	}

	@Override
	protected String getProductName() {
		return "lxc-docker";
	}

	public boolean isInstalled() throws JSchException, InterruptedException,
			IOException {
		final SshHelper sshHelper = new SshHelperImpl(host, user, pwd, logger);
		String result = sshHelper.exec("docker info");
		if (result.toLowerCase().contains("docker: command not found")
				|| result.toLowerCase().contains("permission denied")
				|| result.toLowerCase().contains(
						"the program 'docker' is currently not installed")) {
			logger.info("Docker is not installed");
			return false;
		}
		logger.info("Docker is installed");
		return true;
	}

	/**
	 * Function to Start DockerAgent on remote host. If Agent is already started
	 * - it kills all the agent instances and starts it again to ensure clean system.
	 * 
	 * @author skozlov
	 * @throws JSchException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public void startDockerAgent() throws JSchException, InterruptedException,
			IOException {
		// kill before all the instances
		final SshHelper sshHelper = new SshHelperImpl(host, user, pwd, logger);
		sshHelper
				.execSudo("sudo kill -9 `ps -aux | grep -i 0.0.0.0:2375 | awk {'print $2'}`");
		sshHelper
//				.execSudo("sudo nohup docker -H tcp://0.0.0.0:2375 -H unix:///var/run/docker.sock -d >> 1.txt 2>&1 &");
		.execSudo("sudo screen -d -m docker -H tcp://0.0.0.0:2375 -H unix:///var/run/docker.sock -d");
		
	}

	public void install() throws IOException, JSchException,
			InterruptedException {
		final SshHelper sshHelper = new SshHelperImpl(host, user, pwd, logger);
		// install docker
		sshHelper.execSudo("sudo wget -qO- https://get.docker.com/ | sh");
		// execute docker without sudo
		sshHelper.execSudo("sudo groupadd docker");
		sshHelper.execSudo("sudo gpasswd -a " + user + " docker");
		sshHelper.execSudo("sudo service docker restart");
	}
	
	public static void main(String[] args) throws JSchException, InterruptedException, IOException, PropertyNotExistsException {
		Logger logger = LoggerFactory.getLogger(DockerInstaller.class);
		DockerInstaller d = new DockerInstaller(logger);
		d.startDockerAgent();
		
	}


}

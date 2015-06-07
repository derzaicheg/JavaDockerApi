package com.skozlov.breed.common.install.remote;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;

import com.jcraft.jsch.JSchException;
import com.skozlov.breed.common.config.TestSettings;
import com.skozlov.breed.common.config.TestSettings.PropertyNotExistsException;
import com.skozlov.breed.common.helpers.SshHelper;
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
		this.pwd = testSettings.getProperty(LabradorTestProperties.DOCKER_SERVER_PWD);
	}

	@Override
	protected String getProductName() {
		return "lxc-docker";
	}

	public boolean isInstalled() throws JSchException, InterruptedException,
			IOException {
		final SshHelper sshHelper = new SshHelper(host, user, pwd, logger);
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

	public void install() throws IOException, JSchException,
			InterruptedException {
		final SshHelper sshHelper = new SshHelper(host, user, pwd, logger);
		// install docker
		sshHelper.execSudo("sudo wget -qO- https://get.docker.com/ | sh");
		// execute docker without sudo
		sshHelper.execSudo("sudo groupadd docker");
		sshHelper.execSudo("sudo gpasswd -a " + user + " docker");
		sshHelper.execSudo("sudo service docker restart");
	}

}

package com.skozlov.control.common.install.remote;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;

import com.jcraft.jsch.JSchException;
import com.skozlov.control.common.config.JCTestProperties;
import com.skozlov.control.common.config.TestSettings;
import com.skozlov.control.common.config.TestSettings.PropertyNotExistsException;
import com.skozlov.control.common.helpers.SshHelper;
import com.skozlov.control.common.install.backend.IrdetoProductInstaller;

public class DockerInstaller extends IrdetoProductInstaller {

	private String host;
	private String user;
	private String pwd;

	public DockerInstaller(Logger logger) throws PropertyNotExistsException,
			IOException {
		super(logger);
		TestSettings testSettings = new TestSettings(logger);
		this.host = testSettings
				.getProperty(JCTestProperties.DOCKER_SERVER_HOST);
		this.user = testSettings
				.getProperty(JCTestProperties.DOCKER_SERVER_USR);
		this.pwd = testSettings.getProperty(JCTestProperties.DOCKER_SERVER_PWD);
	}

	@Override
	protected String getProductName() {
		return "docker";
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

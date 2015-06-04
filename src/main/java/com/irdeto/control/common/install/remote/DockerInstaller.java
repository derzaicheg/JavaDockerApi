package com.irdeto.control.common.install.remote;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;

import com.irdeto.control.common.helpers.SshHelper;
import com.irdeto.control.common.install.backend.IrdetoProductInstaller;
import com.jcraft.jsch.JSchException;

public class DockerInstaller extends IrdetoProductInstaller {

	public DockerInstaller(Logger logger) {
		super(logger);
	}

	@Override
	protected String getProductName() {
		return "docker";
	}
	
	public boolean isInstalled() throws JSchException, InterruptedException, IOException{
		final SshHelper sshHelper = new SshHelper(logger);
		String result = sshHelper.exec("docker info");
		if (!result.toLowerCase().contains("docker: command not found") || !result.toLowerCase().contains("permission denied")){
			logger.info("Docker is installed");
			return true;
		}
		logger.info("Docker is not installed");
		return false;
	}
	
	public void install() throws IOException, JSchException, InterruptedException{
		final SshHelper sshHelper = new SshHelper(logger);
		//install docker
		sshHelper.execSudo("wget -qO- https://get.docker.com/ | sh");
		//execute docker without sudo
		sshHelper.execSudo("sudo groupadd docker");
		sshHelper.execSudo("sudo gpasswd -a " + sshHelper.user + " docker");
		sshHelper.execSudo("sudo service docker restart");
	}


	

}

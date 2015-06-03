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
	
	public void install() throws IOException, JSchException, InterruptedException{
		final SshHelper sshHelper = new SshHelper(logger);
		//install docker
		sshHelper.execSudo("sudo apt-get install -y docker");
		//execute docker without sudo
		
	}


	

}

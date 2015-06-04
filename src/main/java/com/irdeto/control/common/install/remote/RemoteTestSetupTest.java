package com.irdeto.control.common.install.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

/**
 * Makes the machine ready to run the tests remotely
 */
@Test(groups = "install-remote")
public class RemoteTestSetupTest {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Test
	public void installDocker() throws Exception{
		DockerInstaller dockerInstaller = new DockerInstaller(this.logger);
		boolean isInstalled = dockerInstaller.isInstalled();
		if (!isInstalled){
			dockerInstaller.install();
		}
	}
}

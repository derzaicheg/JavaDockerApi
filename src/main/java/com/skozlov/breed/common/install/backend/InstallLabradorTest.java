package com.skozlov.breed.common.install.backend;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.skozlov.breed.common.config.TestSettings.PropertyNotExistsException;

@Test(groups = "install-labrador")
public class InstallLabradorTest {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Test
	public void uninstallLabrador() throws PropertyNotExistsException, IOException{
		LabradorInstaller labradorInstaller = new LabradorInstaller(logger);
		labradorInstaller.uninstall();
	}
	
	@Test(dependsOnMethods = {"uninstallLabrador"})
	public void installLabrador() throws PropertyNotExistsException, IOException{
		LabradorInstaller labradorInstaller = new LabradorInstaller(logger);
		labradorInstaller.install();
	}
	
	@Test(dependsOnMethods = {"installLabrador"})
	public void validateLabradorInstallation() throws PropertyNotExistsException, IOException {
		LabradorInstaller labradorInstaller = new LabradorInstaller(logger);
		labradorInstaller.validateInstall();
	}
	
	
}

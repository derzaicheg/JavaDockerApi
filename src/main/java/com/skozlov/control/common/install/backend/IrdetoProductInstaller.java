package com.skozlov.control.common.install.backend;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;

import com.jcraft.jsch.JSchException;
import com.skozlov.control.common.config.JCTestProperties;
import com.skozlov.control.common.config.TestSettings;
import com.skozlov.control.common.config.TestSettings.PropertyNotExistsException;
import com.skozlov.control.common.helpers.SshHelper;


/**
 * Abstract Irdeto data product installer. Implemented installation steps: - download artifact from the repository - cleanup
 * product folder - download required components - execute installation script
 */
public abstract class IrdetoProductInstaller {
	
	protected final Logger logger;
	private String user;
	private String host;
	private String pwd;

	public IrdetoProductInstaller(final Logger logger) throws PropertyNotExistsException, IOException{
		this.logger = logger;
		TestSettings testSettings = new TestSettings(logger);
		this.host = testSettings.getProperty(JCTestProperties.DOCKER_SERVER_HOST);
		this.user = testSettings.getProperty(JCTestProperties.DOCKER_SERVER_USR);
		this.pwd = testSettings.getProperty(JCTestProperties.DOCKER_SERVER_PWD);
	}
	
    /**
     * Returns the name of the product. This name will be used in the properties describing the version / location of the
     * product.
     *
     * @return the name of the product
     */
    protected abstract String getProductName();
    
    protected void doInstall(File packageFile) {
    	// TODO Auto-generated method stub
    }
    
    /**
     * Returns true or false according to the result of package installed or not. Supports ubuntu only
     * 
     * @param packageName
     * @return boolean
     * @throws JSchException
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean isPackageInstalled(String packageName) throws JSchException, IOException, InterruptedException{
    	SshHelper sshHelper = new SshHelper(host, user, pwd, logger);
    	String result = sshHelper.exec("dpkg -s " + packageName + " | grep Status");
    	if (result.toLowerCase().contains("status: install okdocker")){
    		return true;
    	}
    	return false;
    }
    
    
    public void install(File packageFile) throws Exception {
    	doInstall(packageFile);
    }
    
    
	
}

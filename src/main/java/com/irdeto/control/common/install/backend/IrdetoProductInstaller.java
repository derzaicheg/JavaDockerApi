package com.irdeto.control.common.install.backend;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;

import com.irdeto.control.common.helpers.SshHelper;
import com.jcraft.jsch.JSchException;


/**
 * Abstract Irdeto data product installer. Implemented installation steps: - download artifact from the repository - cleanup
 * product folder - download required components - execute installation script
 */
public abstract class IrdetoProductInstaller {
	
	protected final Logger logger;

	public IrdetoProductInstaller(final Logger logger){
		this.logger = logger;
	}
	
    /**
     * Returns the name of the product. This name will be used in the properties describing the version / location of the
     * product.
     *
     * @return the name of the product
     */
    protected abstract String getProductName();
    
    public void doInstall(File packageFile) {
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
    	SshHelper sshHelper = new SshHelper(logger);
    	String result = sshHelper.exec("dpkg -s " + packageName);
    	if (!result.toLowerCase().contains("package '" + packageName + "' is not installed")){
    		return true;
    	}
    	return false;
    }
    
    
    public void install(File packageFile) throws Exception {
    	doInstall(packageFile);
    }
    
    
	
}

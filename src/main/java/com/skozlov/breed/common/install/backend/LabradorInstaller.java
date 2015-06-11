package com.skozlov.breed.common.install.backend;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.github.dockerjava.api.command.CreateContainerResponse;
import com.skozlov.breed.common.config.TestSettings;
import com.skozlov.breed.common.config.TestSettings.PropertyNotExistsException;
import com.skozlov.breed.common.helpers.DockerHelper;
import com.skozlov.breed.labrador.util.LabradorTestProperties;

public class LabradorInstaller extends BreedProductInstaller {

	
	
	private DockerHelper dockerHelper;
	private String dockerFilePath;
	private String jettyContPort;
	private String jettyHostPort;
	private String postgresContPort;
	private String postgresHostPort;


	public LabradorInstaller(Logger logger) throws PropertyNotExistsException,
			IOException {
		super(logger);
		this.dockerHelper = new DockerHelper(logger);
		TestSettings testSettings = new TestSettings(logger);
		this.dockerFilePath = testSettings.getProperty(LabradorTestProperties.DOCKER_FILE_LABRADOR_CENTOS7_PATH);
		this.jettyContPort = testSettings.getProperty(LabradorTestProperties.DOCKER_LABRADOR_JETTY_CONTAINER_PORT);
		this.jettyHostPort = testSettings.getProperty(LabradorTestProperties.DOCKER_LABRADOR_JETTY_HOST_PORT);
		this.postgresContPort = testSettings.getProperty(LabradorTestProperties.DOCKER_LABRADOR_POSTGRESQL_CONTAINER_PORT);
		this.postgresHostPort = testSettings.getProperty(LabradorTestProperties.DOCKER_LABRADOR_POSTGRESQL_HOST_PORT);
	}


	@Override
	protected String getProductName() {
		return "labrador";
	}

	
	public void install() throws IOException{
		String imageResult = this.dockerHelper.buildFromDockerfile(this.dockerFilePath, getProductName() + "_image");
		this.dockerHelper.validateImage(imageResult);
		
		CreateContainerResponse res = this.dockerHelper.createContainerByImageId(getProductName() + "_image", getProductName());
		Map<String, String> ports = new HashMap<String, String>();
		ports.put(this.jettyContPort, this.jettyHostPort);
		ports.put(this.postgresContPort, this.postgresHostPort);
		this.dockerHelper.startContainer(getProductName(), ports);
		
		
	}
}

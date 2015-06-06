package com.skozlov.labrador.common.helpers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.core.DockerClientBuilder;
import com.skozlov.labrador.common.config.LTestProperties;
import com.skozlov.labrador.common.config.TestSettings;
import com.skozlov.labrador.common.config.TestSettings.PropertyNotExistsException;


public class DockerHelper {

	protected final Logger logger;
	private DockerClient dockerClient;
	private String host;

	public DockerHelper(Logger logger) throws PropertyNotExistsException,
			IOException {
		this.logger = logger;
		TestSettings testSettings = new TestSettings(logger);
		this.host = testSettings
				.getProperty(LTestProperties.DOCKER_SERVER_HOST);
		this.dockerClient = DockerClientBuilder.getInstance(
				"http://" + this.host + ":2375").build();
	}

	/**
	 * Function to test docker installation
	 * 
	 * @author skozlov
	 * @return info
	 */
	public Info getDockerInfo() {
		Info info = this.dockerClient.infoCmd().exec();
		return info;
	}

	/**
	 * Function builds an image from Dockerfile
	 * 
	 * @author skozlov
	 * @param dockerfilePath - path to Dockerfile folder or to the file itself
	 * @return string docker image id
	 * @throws IOException
	 */
	public String buildFromDockerfile(String dockerfilePath) throws IOException {
		InputStream response = dockerClient.buildImageCmd(
				new File(dockerfilePath))
				.exec();
		String result = "";
		try {
			LineIterator itr = IOUtils.lineIterator(response, "UTF-8");
			while (itr.hasNext()) {
				String line = itr.next();
				logger.info(line);
				result = result + line;
			}
		} finally {
			IOUtils.closeQuietly(response);
		}
		String imageId = getImageIdFromBuildDockerfile(result);
		return imageId;
	}
	
	/**
	 * Function gets as a parameter response of Docker build command from Dockerfile and return an ImageId 
	 * 
	 * @author skozlov
	 * @param response
	 * @return string docker image id
	 */
	private String getImageIdFromBuildDockerfile(String response){
		Assert.assertTrue(response.contains("Successfully built"));
		String imageId = StringUtils.substringBetween(response,
				"Successfully built ", "\\n\"}").trim();
		return imageId;
	}
	
	/**
	 * Function returns InspectImageRespinse
	 * 
	 * @author skozlov
	 * @param imageId
	 * @return InspectImageResponse obj
	 */
	public InspectImageResponse inspectImage(String imageId){
		InspectImageResponse imageResponse = dockerClient.inspectImageCmd(imageId).exec();
		return imageResponse;
	}
	
	/**
	 * Function validates InspectImage response
	 * 
	 * @author skozlov
	 * @param imageId
	 */
	public void validateImage(String imageId){
		InspectImageResponse imageResponse = inspectImage(imageId);
		Assert.assertNotNull(imageResponse);
		Assert.assertNotNull(imageResponse.getId());
		logger.info("Image inspect: {}", imageResponse.toString());
	}
	
	//implement start container
	//hosts entries: example
	//search for with dns
//	https://github.com/docker-java/docker-java/blob/fb6d50b32f9a21188fea945dcf29a07a591cf4b8/src/test/java/com/github/dockerjava/core/command/StartContainerCmdImplTest.java
	
	

	public static void main(String[] args) throws PropertyNotExistsException,
			IOException {
		final Logger logger = LoggerFactory.getLogger(DockerHelper.class);
		DockerHelper d = new DockerHelper(logger);
		// Info info = d.getDockerInfo();
		// System.out.println(info);
//		String res = d.buildFromDockerfile("src/main/resources/dockerfiles/centos/Dockerfile");
//		System.out.println(res);
		InspectImageResponse obj = d.inspectImage("77059a45608e");
		d.validateImage("77059a45608e");
		
		

	}
}

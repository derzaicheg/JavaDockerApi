package com.skozlov.breed.common.helpers.docker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.InternalServerErrorException;
import com.github.dockerjava.api.NotFoundException;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DockerClientBuilder;
import com.skozlov.breed.common.config.TestSettings;
import com.skozlov.breed.common.config.TestSettings.PropertyNotExistsException;
import com.skozlov.breed.labrador.util.LabradorTestProperties;

public class DockerHelperImpl implements DockerHelper {

	protected final Logger logger;
	private DockerClient dockerClient;
	private String host;

	public DockerHelperImpl(Logger logger) throws PropertyNotExistsException,
			IOException {
		this.logger = logger;
		TestSettings testSettings = new TestSettings(logger);
		this.host = testSettings
				.getProperty(LabradorTestProperties.DOCKER_SERVER_HOST);
		this.dockerClient = DockerClientBuilder.getInstance(
				"http://" + this.host + ":2375").build();
	}

	/* (non-Javadoc)
	 * @see com.skozlov.breed.common.helpers.DockerHelper#getDockerInfo()
	 */
	@Override
	public Info getDockerInfo() {
		Info info = this.dockerClient.infoCmd().exec();
		return info;
	}

	/* (non-Javadoc)
	 * @see com.skozlov.breed.common.helpers.DockerHelper#buildFromDockerfile(java.lang.String, java.lang.String)
	 */
	@Override
	public String buildFromDockerfile(String dockerfilePath, String imageTag)
			throws IOException {
		if (imageTag == null) {
			imageTag = "labrador_image" + RandomStringUtils.randomNumeric(5);
		}
		InputStream response = dockerClient
				.buildImageCmd(new File(dockerfilePath)).withTag(imageTag)
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
	 * Function gets as a parameter response of Docker build command from
	 * Dockerfile and return an ImageId
	 * 
	 * @author skozlov
	 * @param response
	 * @return string docker image id
	 */
	private String getImageIdFromBuildDockerfile(String response) {
		Assert.assertTrue(response.contains("Successfully built"));
		String imageId = StringUtils.substringBetween(response,
				"Successfully built ", "\\n\"}").trim();
		return imageId;
	}

	/* (non-Javadoc)
	 * @see com.skozlov.breed.common.helpers.DockerHelper#inspectImage(java.lang.String)
	 */
	@Override
	public InspectImageResponse inspectImage(String imageId) {
		InspectImageResponse imageResponse = dockerClient.inspectImageCmd(
				imageId).exec();
		return imageResponse;
	}

	/* (non-Javadoc)
	 * @see com.skozlov.breed.common.helpers.DockerHelper#validateImage(java.lang.String)
	 */
	@Override
	public void validateImage(String imageId) {
		InspectImageResponse imageResponse = inspectImage(imageId);
		Assert.assertNotNull(imageResponse);
		Assert.assertNotNull(imageResponse.getId());
		logger.info("Image inspect: {}", imageResponse.toString());
	}

	/* (non-Javadoc)
	 * @see com.skozlov.breed.common.helpers.DockerHelper#createContainerByImageId(java.lang.String, java.lang.String)
	 */
	@Override
	public CreateContainerResponse createContainerByImageId(String imageId,
			String containerName) {
		if (containerName == null) {
			containerName = "labrador" + RandomStringUtils.randomNumeric(5);
		}
		CreateContainerResponse container = dockerClient
				.createContainerCmd(imageId).withName(containerName).exec();
		return container;
	}

	/* (non-Javadoc)
	 * @see com.skozlov.breed.common.helpers.DockerHelper#startContainer(java.lang.String, java.util.Map)
	 */
	@Override
	@SuppressWarnings("deprecation")
	public void startContainer(String containerId,
			Map<String, String> portBindingsMap) {
		Ports portBindings = new Ports();
		for (Map.Entry<String, String> entry : portBindingsMap.entrySet()) {
			portBindings.bind(
					ExposedPort.tcp(Integer.parseInt(entry.getKey())),
					Ports.Binding(Integer.parseInt(entry.getValue())));
			dockerClient.startContainerCmd(containerId)
					.withPortBindings(portBindings).exec();
		}
	}

	/* (non-Javadoc)
	 * @see com.skozlov.breed.common.helpers.DockerHelper#inspectContainer(java.lang.String)
	 */
	@Override
	public InspectContainerResponse inspectContainer(String containerId) {
		return dockerClient.inspectContainerCmd(containerId).exec();
	}

	/* (non-Javadoc)
	 * @see com.skozlov.breed.common.helpers.DockerHelper#removeWithForceContainer(java.lang.String)
	 */
	@Override
	public void removeWithForceContainer(String containerId) {
		logger.info("Force removing container {}", containerId);
		try {
			dockerClient.removeContainerCmd(containerId).withForce() // stop too
					.exec();
		} catch (NotFoundException | InternalServerErrorException ignored) {
			logger.info("Container not found or ignoring to remove with force {}",
					ignored.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see com.skozlov.breed.common.helpers.DockerHelper#stopContainer(java.lang.String)
	 */
	@Override
	public void stopContainer(String containerId) {
		logger.info("Stopping container {}", containerId);
		try {
			dockerClient.stopContainerCmd(containerId).exec();
		} catch (NotFoundException | InternalServerErrorException ignored) {
			logger.info("Container not found or ignoring to stop {}", ignored.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see com.skozlov.breed.common.helpers.DockerHelper#removeContainer(java.lang.String)
	 */
	@Override
	public void removeContainer(String containerId) {
		logger.info("Removing container {}", containerId);
		try {
			dockerClient.removeContainerCmd(containerId).exec();
		} catch (NotFoundException | InternalServerErrorException ignored) {
			logger.info("ignoring to remove {}", ignored.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see com.skozlov.breed.common.helpers.DockerHelper#removeWithForceImage(java.lang.String)
	 */
	@Override
	public void removeWithForceImage(String imageId) {
		logger.info("Removing image {}", imageId);
		try {
			dockerClient.removeImageCmd(imageId).withForce().exec();
		} catch (NotFoundException | InternalServerErrorException e) {
			logger.info("ignoring {}", e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see com.skozlov.breed.common.helpers.DockerHelper#listContainers()
	 */
	@Override
	public List<Container> listContainers() {
		List<Container> containersList = dockerClient.listContainersCmd()
				.withShowAll(true).exec();
		System.out.println(containersList.toString());
		return containersList;
	}

	public static void main(String[] args) throws PropertyNotExistsException,
			IOException {
		final Logger logger = LoggerFactory.getLogger(DockerHelperImpl.class);
		DockerHelper d = new DockerHelperImpl(logger);
		// Info info = d.getDockerInfo();
		// System.out.println(info);
		 String res = d.buildFromDockerfile("src/main/resources/dockerfiles/labrador/centos7/Dockerfile", "postgresql");
		 System.out.println(res);
		// InspectImageResponse obj = d.inspectImage("77059a45608e");
		// d.validateImage("77059a45608e");
		// CreateContainerResponse res = d.createContainerByImageId(
		// "77059a45608e", "labrador");
		// Map<String, String> ports = new HashMap<String, String>();
		// ports.put("8080", "8080");
		// d.startContainer(res.getId(), ports);
		// InspectContainerResponse inspectRes =
		// d.inspectContainer(res.getId());
		// // System.out.println(inspectRes.toString());
		// d.removeWithForceContainer("labrador");
//		d.listContainers();
	}
}

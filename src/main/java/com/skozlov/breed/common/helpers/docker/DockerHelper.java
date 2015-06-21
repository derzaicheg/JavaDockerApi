package com.skozlov.breed.common.helpers.docker;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Info;

public interface DockerHelper {

	/**
	 * Function to test docker installation
	 * 
	 * @author skozlov
	 * @return info
	 */
	public abstract Info getDockerInfo();

	/**
	 * Function builds an image from Dockerfile
	 * 
	 * @author skozlov
	 * @param dockerfilePath
	 *            - path to Dockerfile folder or to the file itself
	 * @return string docker image id
	 * @throws IOException
	 */
	public abstract String buildFromDockerfile(String dockerfilePath,
			String imageTag) throws IOException;

	/**
	 * Function returns InspectImageRespinse
	 * 
	 * @author skozlov
	 * @param imageId
	 * @return InspectImageResponse obj
	 */
	public abstract InspectImageResponse inspectImage(String imageId);

	/**
	 * Function validates InspectImage response
	 * 
	 * @author skozlov
	 * @param imageId
	 */
	public abstract void validateImage(String imageId);

	public abstract CreateContainerResponse createContainerByImageId(
			String imageId, String containerName);

	/**
	 * Method to start up a container
	 * 
	 * @author skozlov
	 * @param containerId
	 * @param portBindingsMap
	 *            - map of strings where key is container port and value is host
	 *            port. If value is null - no ports binding will be made
	 */
	public abstract void startContainer(String containerId,
			Map<String, String> portBindingsMap);

	public abstract InspectContainerResponse inspectContainer(String containerId);

	/**
	 * Method to remove container with -f (force) flag
	 * 
	 * @author skozlov
	 * @param containerId
	 *            - can be used containerId or containerName
	 */
	public abstract void removeWithForceContainer(String containerId);

	/**
	 * Method to stop container
	 * 
	 * @author skozlov
	 * @param containerId
	 *            - can be used containerId or containerName
	 */
	public abstract void stopContainer(String containerId);

	/**
	 * Method to remove container
	 * 
	 * @author skozlov
	 * @param containerId
	 *            - can be used containerId or containerName
	 */
	public abstract void removeContainer(String containerId);

	/**
	 * Method to remove image with force (-f flag)
	 * 
	 * @author skozlov
	 * @param containerId
	 *            - can be used containerId or containerName
	 */
	public abstract void removeWithForceImage(String imageId);

	public abstract List<Container> listContainers();

}
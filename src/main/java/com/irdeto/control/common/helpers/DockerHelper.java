package com.irdeto.control.common.helpers;

import org.slf4j.Logger;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.core.DockerClientBuilder;

public class DockerHelper {
	
	protected final Logger logger;
	private DockerClient dockerClient;
	
	public DockerHelper(Logger logger){
		this.logger = logger;
		this.dockerClient = DockerClientBuilder.getInstance("http://192.168.56.101:2375").build();
	}
	
	public Info getDockerInfo(){
		Info info = this.dockerClient.infoCmd().exec();
		return info;
	}
	
	
	
	public static void main(String[] args) {
		Logger logger = null;
		DockerHelper d = new DockerHelper(logger);
		Info info = d.getDockerInfo();
		System.out.println(info);
	}
}

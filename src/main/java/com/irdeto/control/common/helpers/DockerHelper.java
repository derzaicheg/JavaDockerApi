package com.irdeto.control.common.helpers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.logging.impl.Log4JLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.core.DockerClientBuilder;
import com.irdeto.control.common.config.JCTestProperties;
import com.irdeto.control.common.config.TestSettings;
import com.irdeto.control.common.config.TestSettings.PropertyNotExistsException;

public class DockerHelper {
	
	protected final Logger logger;
	private DockerClient dockerClient;
	private String host;
	
	public DockerHelper(Logger logger) throws PropertyNotExistsException, IOException{
		this.logger = logger;
		TestSettings testSettings = new TestSettings(logger);
		this.host = testSettings
				.getProperty(JCTestProperties.DOCKER_SERVER_HOST);
		this.dockerClient = DockerClientBuilder.getInstance("http://" + this.host + ":2375").build();
	}
	
	public Info getDockerInfo(){
		Info info = this.dockerClient.infoCmd().exec();
		return info;
	}
	
	public void buildFromDockerfile() throws IOException{
		final InputStream input = getClass().getClassLoader().getResourceAsStream("dockerfiles/centos/Dockerfile.tar");
		System.out.println(input);
		//File baseDir = new File("D:\\projects\\JavaDockerApi\\src\\main\\resources\\dockerfiles\\centos\\Dockerfile");
		//OutputStream outputStream = new FileOutputStream(tempFile);
		//IOUtils.copy(input, outputStream);
//		outputStream.close();
		//InputStream response = dockerClient.buildImageCmd(baseDir).exec();
		InputStream response = dockerClient.buildImageCmd(input).exec();
		StringWriter logwriter = new StringWriter();
		
		try {
		    LineIterator itr = IOUtils.lineIterator(response, "UTF-8");
		    while (itr.hasNext()) {
		        String line = itr.next();
		        logwriter.write(line);
		        logger.info(line);
		    }
		} finally {
		    IOUtils.closeQuietly(response);
		}
	}
	
	
	
	public static void main(String[] args) throws PropertyNotExistsException, IOException {
		final Logger logger = LoggerFactory.getLogger(DockerHelper.class);
		DockerHelper d = new DockerHelper(logger);
		//Info info = d.getDockerInfo();
		//System.out.println(info);
		d.buildFromDockerfile();
		
	}
}

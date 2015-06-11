package com.skozlov.breed.labrador.util;

public class LabradorTestProperties {
	
	/** Property holds ip for the docker server */
	public static final String DOCKER_SERVER_HOST = "docker.server.host";
	
	/** Property holds username for the docker server to connect  */
	public static final String DOCKER_SERVER_USR = "docker.server.user";
	
	/** Property holds pwd for the docker server to connect  */
	public static final String DOCKER_SERVER_PWD = "docker.server.pwd";
	
	/** Property holds path to labrador centos7 dockerfile */
	public static final String DOCKER_FILE_LABRADOR_CENTOS7_PATH = "docker.file.labrador.centos7.path";
	
	/** Property holds docker labrador jetty container port */
	public static final String DOCKER_LABRADOR_JETTY_CONTAINER_PORT = "docker.labrador.jetty.container.port";
	
	/** Property holds docker labrador jetty host port */
	public static final String DOCKER_LABRADOR_JETTY_HOST_PORT = "docker.labrador.jetty.host.port";
	
	/** Property holds docker labrador postgresql container port */
	public static final String DOCKER_LABRADOR_POSTGRESQL_CONTAINER_PORT = "docker.labrador.postgresql.container.port";
	
	/** Property holds docker labrador postgresql host port */
	public static final String DOCKER_LABRADOR_POSTGRESQL_HOST_PORT = "docker.labrador.postgresql.host.port";
}


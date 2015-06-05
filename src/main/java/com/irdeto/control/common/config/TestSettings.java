package com.irdeto.control.common.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;

public class TestSettings {

	private static final String DEFAULT_FILE_PREFIX = "local";
	private String fileFullPath;
	private Properties properties;
	private Logger logger;
	
	public TestSettings(Logger logger) throws IOException{
		this.logger = logger;
		String fileSuffix = System.getenv("env");
		if (fileSuffix == null){
			fileSuffix = DEFAULT_FILE_PREFIX;
		}
		properties = new PropertyFileNameResolver(logger).resolveFileName(fileSuffix);
	}
	
	public String getProperty(String key) throws PropertyNotExistsException{
		String value = properties.getProperty(key);
		if (value == null) throw new PropertyNotExistsException("Property null or key does not exists");
		return value;
	}
	
	public String getProperty(String key, String defaultValue){
		String value = properties.getProperty(key, defaultValue);
		return value;
	}
	
	

	
	public class PropertyNotExistsException extends Exception {
		public PropertyNotExistsException(String message){
			super(message);
		}
	}
	
//	public static void main(String[] args) throws IOException {
//		Logger logger = null;
//		TestSettings t = new TestSettings(logger);

//	}
	
}

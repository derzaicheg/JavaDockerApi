package com.irdeto.control.common.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;

public class PropertyFileNameResolver {
	
	private Logger logger;

	public PropertyFileNameResolver(Logger logger){
		this.logger = logger;
	}

	public Properties resolveFileName(String fileSuffix) throws IOException{
		String fileFullPath = String.format("configs/env_%s.properties", fileSuffix);
		Properties properties = loadPropertiesFromFile(fileFullPath);
		return properties;
	}
	
	private Properties loadPropertiesFromFile(String fileFullPath) throws IOException{
		Properties properties = new Properties();
		try (final InputStream input = getClass().getClassLoader()
				.getResourceAsStream(fileFullPath)) {
			try{
				properties.load(input);
			} catch (NullPointerException e){
				logger.error("Properties file not found with error");
			}
			return properties;
		}
		
	}
}

package com.graly.framework.runtime;

import java.io.File;
import java.util.Properties;

public interface RuntimeService {
	
	void start() throws Exception;
	
	void stop() throws Exception;
	
	boolean isStarted();
	
	File getHome();
	
	Properties getProperties();
	
	String getProperty(String name);
	
	String getProperty(String name, String defaultValue);
	
	
}

package com.graly.framework.runtime.service;

import java.io.Serializable;
import java.util.Properties;

public interface ServiceLocator extends Serializable {
	
	void initialize(String host, int port, Properties properties) throws Exception;
	
	Object lookup(ServiceDescriptor descriptor) throws Exception;
	
	Object lookup(String serviceId) throws Exception;
	
	void dispose();
}

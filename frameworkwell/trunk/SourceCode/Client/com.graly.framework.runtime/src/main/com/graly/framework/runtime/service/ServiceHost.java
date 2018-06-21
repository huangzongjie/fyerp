package com.graly.framework.runtime.service;

import java.io.Serializable;
import java.util.Properties;
import java.util.Map;

public class ServiceHost implements Serializable {
	
	private String hostName;
	private String host;
	private int port;
	private Properties properties;
	private static final ServiceHost instance = new ServiceHost();
	
	private Class<? extends ServiceLocator> serviceLocatorClass;
	private transient ServiceLocator serviceLocator;
	
	public ServiceHost() {
    }
	
    public ServiceHost(Class<? extends ServiceLocator> serverClass) {
        serviceLocatorClass = serverClass;
    }
    
    public ServiceLocator getServiceLocator() throws Exception {
        if (serviceLocator == null) {
            serviceLocator = createServiceLocator();
        }
        return serviceLocator;
    }
    
	protected ServiceLocator createServiceLocator() throws Exception {
        ServiceLocator serviceLocator = serviceLocatorClass.newInstance();
        serviceLocator.initialize(host, port, properties);
        return serviceLocator;
    }
	
	/**
     * @return the host.
     */
    public String getHost() {
        return host;
    }

    /**
     * @return the port.
     */
    public int getPort() {
        return port;
    }

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getHostName() {
		return hostName;
	}
	
    public void setAddress(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
    
    public Properties getProperties() {
        return properties;
    }
    
    public void setProperty(String key, String value) {
    	if (properties == null){
    		properties = new Properties();
    	} 
    	properties.setProperty(key, value);
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    public String getProperty(String key, String defValue) {
        return properties.getProperty(key, defValue);
    }
    
    public void dispose() {
        if (serviceLocator != null) {
            serviceLocator.dispose();
            serviceLocator = null;
        }
        serviceLocatorClass = null;
        properties = null;
    }

}

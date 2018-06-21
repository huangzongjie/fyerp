package com.graly.promisone.runtime.service;

import java.io.Serializable;
import java.util.Properties;

public class ServiceHost implements Serializable {
	
	private String host;
	private int port;
	private Properties properties;
	private static final ServiceHost instance = new ServiceHost();
	
	private Class<? extends ServiceLocator> serviceLocatorClass;
	private transient ServiceLocator serviceLocator;
	
	public ServiceHost() {
    }
	
	public static ServiceHost getInstance() {
    	return instance;
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

    public void setAddress(String host, int port) {
        this.host = host;
        this.port = port;
    }
}

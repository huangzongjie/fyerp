package com.graly.framework.runtime.service;

import java.io.Serializable;

public class ServiceDescriptor implements Serializable {
	
	private static final long serialVersionUID = 5490362136607217161L;
	
	private String name;
	private String hostName;
	private String serviceClassName;
	private transient Class<?> serviceClass;
	private String locatorPattern;
	private transient String locator;
	
	private ServiceHost server;
	
	public ServiceDescriptor() {
    }
	
	public ServiceDescriptor(String serviceClassName) {
        this.serviceClassName = serviceClassName;
    }
	
	public ServiceDescriptor(Class<?> serviceClass) {
        setServiceClass(serviceClass);
    }
	
	void setServiceClass(Class<?> serviceClass) {
        this.serviceClass = serviceClass;
        serviceClassName = serviceClass.getName();
    }
	
	/**
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the className.
     */
    public String getServiceClassName() {
        return serviceClassName;
    }

    public String getServiceClassSimpleName() {
        int p = serviceClassName.lastIndexOf('.');
        if (p == -1) {
            return serviceClassName;
        }
        return serviceClassName.substring(p + 1);
    }
    
    public String getLocator() {
        return locator == null ? locatorPattern : locator;
    }

    public void setLocator(String locator) {
        this.locator = locator;
    }
    
    public String getInstanceName() {
        return name != null ? serviceClassName + '#' + name
                : serviceClassName;
    }
    
    Class<?> getServiceClass() throws ClassNotFoundException {
        if (serviceClass == null) {
            serviceClass = Thread.currentThread().getContextClassLoader().loadClass(serviceClassName);
        }
        return serviceClass;
    }
    
    public ServiceHost getServer() {
    	return server;
    }
    
    public void setServer(ServiceHost server) {
        this.server = server;
    }

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getHostName() {
		return hostName;
	}
}	


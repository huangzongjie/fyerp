package com.graly.promisone.runtime.service;

import java.io.Serializable;

public class ServiceDescriptor implements Serializable {
	
	private static final long serialVersionUID = 5490362136607217161L;
	
	private String name;
	private String serviceClassName;
	private String locatorPattern;
	private transient String locator;
	
	public ServiceDescriptor() {
    }
	
	public ServiceDescriptor(String serviceClassName) {
        this.serviceClassName = serviceClassName;
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
}	


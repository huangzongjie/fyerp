package com.graly.promisone.runtime.service;

import com.graly.promisone.runtime.model.ComponentInstance;
import com.graly.promisone.runtime.model.ComponentContext;
import com.graly.promisone.runtime.model.DefaultComponent;

public class ServiceManagement extends DefaultComponent {
	
	private ServiceManager manager;
	
    @Override
    public void activate(ComponentContext context) throws Exception {
        manager = ServiceManager.getInstance();
    }
    
    @Override
    public void deactivate(ComponentContext context) throws Exception {
        manager = null;
    }
    
    @Override
    public void registerContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor) {
    	if (extensionPoint.equals("services")) {
            manager.registerService((ServiceDescriptor) contribution);
        }
    }
    
    @Override
    public void unregisterContribution(Object contribution,
            String extensionPoint, ComponentInstance contributor) {
    	if (extensionPoint.equals("services")) {
            manager.unregisterService((ServiceDescriptor) contribution);
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAdapter(Class<T> adapter) {
        if (ServiceManager.class.isAssignableFrom(adapter)) {
            return (T) manager;
        }
        return null;
    }
}

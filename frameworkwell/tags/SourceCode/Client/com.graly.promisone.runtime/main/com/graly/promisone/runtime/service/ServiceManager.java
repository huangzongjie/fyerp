package com.graly.promisone.runtime.service;

import java.util.HashMap;
import java.util.Map;

public final class ServiceManager {
	
	private static final ServiceManager instance = new ServiceManager();
	protected final Map<String, ServiceDescriptor> services = new HashMap<String, ServiceDescriptor>();
	
	private ServiceManager() {
    }

    public static ServiceManager getInstance() {
    	return instance;
    }
    
    public ServiceDescriptor[] getServiceDescriptors() {
        return services.values().toArray(new ServiceDescriptor[services.size()]);
    }

    public ServiceDescriptor getServiceDescriptor(Class<?> serviceClass) {
        return getServiceDescriptor(serviceClass.getName());
    }

    public ServiceDescriptor getServiceDescriptor(String serviceClass) {
        return services.get(serviceClass);
    }

    public ServiceDescriptor getServiceDescriptor(Class<?> serviceClass,
            String name) {
        return getServiceDescriptor(serviceClass.getName(), name);
    }

    public ServiceDescriptor getServiceDescriptor(String serviceClass,
            String name) {
        return services.get(serviceClass + '#' + name);
    }
    
    public <T> T getService(Class<T> serviceClass) throws Exception {
        ServiceDescriptor descriptor = services.get(serviceClass.getName());
        if (descriptor == null) {
            //return Framework.getLocalService(serviceClass);
        }
        return (T) lookup(descriptor);
    }
    
    public <T> T getService(Class<T> serviceClass, String name)
	    throws Exception {
		ServiceDescriptor descriptor = services.get(serviceClass.getName() + '#' + name);
		if (descriptor == null) {
		    //return Framework.getLocalService(serviceClass);
		}
		return (T) lookup(descriptor);
	}
    
    public Object lookup(ServiceDescriptor descriptor) throws Exception {
        Object service = ServiceHost.getInstance().getServiceLocator().lookup(descriptor);
        if (service == null) {
            return null;
        }
        return service;
    }
    
    public void registerService(ServiceDescriptor descriptor) {
        String key = descriptor.getInstanceName();
        synchronized (services) {
            if (services.containsKey(key)) {
                return;
            }
            services.put(key, descriptor);
        }
    }
    
    public void unregisterService(ServiceDescriptor descriptor) {
        String key = descriptor.getInstanceName();
        synchronized (services) {
        	descriptor = services.remove(key);
        }
    }
    
    public void removeServices() {
        services.clear();
    }
    
}

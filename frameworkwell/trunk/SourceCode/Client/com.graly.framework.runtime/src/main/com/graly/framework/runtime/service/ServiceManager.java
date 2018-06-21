package com.graly.framework.runtime.service;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
public final class ServiceManager {
	
	private static final ServiceManager instance = new ServiceManager();
	protected final Map<String, ServiceDescriptor> services = new HashMap<String, ServiceDescriptor>();
	protected final Map<String, ServiceHost> servers = new HashMap<String, ServiceHost>();
	
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
        //ClassLoader oldLoader = Thread.currentThread().getContextClassLoader();
        //Thread.currentThread().setContextClassLoader(serviceClass.getClassLoader());
        //ClassLoader newLoader = Thread.currentThread().getContextClassLoader();
        try{
        	return (T) lookup(descriptor);
        } 
        finally {
        	//Thread.currentThread().setContextClassLoader(oldLoader);
        }
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
    	ServiceHost host = getServer(descriptor.getHostName());
    	if (host != null){
    		Object service = host.getServiceLocator().lookup(descriptor);
            if (service == null) {
                return null;
            }
            return service;
    	}
        return null;
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
    
    public void registerServer(ServiceHost server) {
        servers.put(server.getHostName(), server);
    }

    public void unregisterServer(ServiceHost server) {
        servers.remove(server);
        server.dispose();
    }
    
    public void removeServers() {
        for (ServiceHost server : servers.values()) {
            server.dispose();
        }
        servers.clear();
    }
    
    public void reset() {
        removeServices();
        removeServers();
    }
    
    public ServiceHost getServer(String hostName) {
    	if (servers.containsKey(hostName)) {
    		return servers.get(hostName);
    	}
    	return null;
    }
    
}

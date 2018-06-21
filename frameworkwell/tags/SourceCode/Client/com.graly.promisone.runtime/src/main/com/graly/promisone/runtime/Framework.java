package com.graly.promisone.runtime;

import java.util.Properties;

import com.graly.promisone.runtime.service.ServiceHost;
import com.graly.promisone.runtime.service.ServiceManager;

public final class Framework {
	
	private static final long serialVersionUID = 1L;
	
	private static RuntimeService runtime;
	
	private Framework() { }
	
	public static void initialize(RuntimeService runtimeService) throws Exception {
        if (runtime != null) {
            throw new Exception("Framework was already initialized");
        }
        Framework.runtime = runtimeService;
        runtime.start();
    }
	
    public static void shutdown() throws Exception {
        if (runtime != null) {
            runtime.stop();
            runtime = null;
        }
    }
    
	public static <T> T getService(Class<T> serviceClass) throws Exception {
        return ServiceManager.getInstance().getService(serviceClass);
    }
	
	public static <T> T getService(Class<T> serviceClass, String name)
	    throws Exception {
		return ServiceManager.getInstance().getService(serviceClass, name);
	}
	
	public static ServiceHost[] getServer() throws Exception {
		return ServiceManager.getInstance().getServers();
	}
	public static String getProperty(String key) {
        return getProperty(key, null);
    }
	
	public static String getProperty(String key, String defValue) {
        return runtime.getProperty(key, defValue);
    }
	
	public static Properties getProperties() {
        return runtime.getProperties();
    }
	 
    public static RuntimeService getRuntime() {
        return runtime;
    }
}

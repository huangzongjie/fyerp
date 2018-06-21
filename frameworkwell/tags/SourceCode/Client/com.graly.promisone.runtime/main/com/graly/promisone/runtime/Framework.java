package com.graly.promisone.runtime;

import com.graly.promisone.runtime.service.ServiceManager;

public final class Framework {
	
	private static final long serialVersionUID = 1L;
	
	public static <T> T getService(Class<T> serviceClass) throws Exception {
        return ServiceManager.getInstance().getService(serviceClass);
    }
	
	public static <T> T getService(Class<T> serviceClass, String name)
	    throws Exception {
		return ServiceManager.getInstance().getService(serviceClass, name);
	}
	
	public static <T> T getLocalService(Class<T> serviceClass) {
        ServiceProvider provider = DefaultServiceProvider.getProvider();
        if (provider != null) {
            return provider.getService(serviceClass);
        }
        return runtime.getService(serviceClass);
    }
}

package com.graly.framework.runtime.service;

import java.util.Properties;
import javax.naming.InitialContext;

public abstract class JndiServiceLocator implements ServiceLocator {
	
	protected transient InitialContext context;
	
	@Override
	public void dispose() {
		context = null;

	}

	@Override
	public void initialize(String host, int port, Properties properties)
			throws Exception {
		context = new InitialContext();

	}
	
	@Override
	public Object lookup(ServiceDescriptor descriptor) throws Exception {
		String locator = descriptor.getLocator();
        if (locator == null) {
            locator = createLocator(descriptor);
            descriptor.setLocator(locator);
        }
        return lookup(locator);
	}

	@Override
	public Object lookup(String serviceId) throws Exception {
		return context.lookup(serviceId);
	}
	
	public InitialContext getContext() {
        return context;
    }
	
	protected static String createLocator(ServiceDescriptor descriptor) {
        return descriptor.getServiceClassName();
    }
}

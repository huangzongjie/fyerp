package com.graly.promisone.runtime.service;

import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import java.rmi.RMISecurityManager;

public class JBossServiceLocator extends JndiServiceLocator {
	
	private static final long serialVersionUID = 5490362136607217161L;
	
	private String prefix = "";
    private String suffix = "";
    private int defaultPort = 1099;
    
	@Override
    public void initialize(String host, int port, Properties properties)
            throws Exception {
		
//		System.setProperty( "java.security.policy ", "client.policy "); 
//		if(System.getSecurityManager() == null){
//			System.setSecurityManager(new RMISecurityManager());
//		}

        if (port == 0) {
        	port = defaultPort;
        }
        if (properties != null) {
            prefix = properties.getProperty("prefix", getDefaultPrefix());
            suffix = properties.getProperty("suffix", getDefaultSuffix());
            
            String value = properties.getProperty(Context.PROVIDER_URL);
            if (value != null) {
                value = String.format(value, host, port);
                properties.put(Context.PROVIDER_URL, value);
            }
        }
        context = new InitialContext(properties);
    }
	
	@Override
    public Object lookup(ServiceDescriptor descriptor) throws Exception {
        String locator = descriptor.getLocator();
        if (locator == null) {
            locator = prefix + descriptor.getServiceClassSimpleName() + suffix;
            descriptor.setLocator(locator);
        } else if (locator.startsWith("%")) {
            locator = prefix + locator.substring(1) + suffix;
            descriptor.setLocator(locator);
        }
        return lookup(locator);
    }
	
	public String getDefaultPrefix() {
    	return "promisone";
    }
	
    public String getDefaultSuffix() {
    	return System.getProperty("jboss.home.dir") == null ? "/remote" : "/local";
    }
}

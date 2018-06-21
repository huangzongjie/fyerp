package com.graly.promisone.runtime.extensionpoints;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.graly.promisone.runtime.service.ServiceLocator;
import com.graly.promisone.runtime.service.ServiceHost;
import com.graly.promisone.runtime.service.ServiceManager;

public class ServerExtensionPoint {
	
	private static final Logger logger = Logger.getLogger(ServerExtensionPoint.class);
	
	public final static String X_POINT = "com.graly.promisone.runtime.server";
    public final static String E_SERVER = "server";
    public final static String E_PROPERTY = "property";
    public final static String A_CLASS = "class";
    public final static String A_HOSTNAME = "hostname";
    public final static String A_NAME = "name";
    public final static String A_VALUE = "value";
    private ServiceManager manager;
    
    public void initialize() {
    	manager = ServiceManager.getInstance();
        loadExtensionPoints();
    }
    
    public void loadExtensionPoints() {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint point = registry.getExtensionPoint(X_POINT);
        if (point == null) {
            return;
        }
        IExtension[] extensions = point.getExtensions();
        for (IExtension element : extensions) {
            loadExtension(element);
        }
    }
    
    private void loadExtension(IExtension extension) {
    	try{
	    	IConfigurationElement[] elements = extension.getConfigurationElements();
	        for (IConfigurationElement elem : elements) {
	            if (elem.getName().equals(E_SERVER)) {
	            	ServiceHost host = loadServiceHost(elem);
	            	manager.registerServer(host);
	            } else {
	            	throw new IllegalArgumentException("Element "+elem.getName()+" is not supported by extension-point " + X_POINT);
	            }
	        }
    	} catch (Exception e){
        	logger.error("Error processing extension element. The element is located in an extension in bundle: " + extension.getNamespaceIdentifier(), e); 
    	}
    }
    
    @SuppressWarnings("unchecked")
    private ServiceHost loadServiceHost(IConfigurationElement element) throws Exception{
    	String locator = element.getAttribute(A_CLASS); 
    	try{
	    	Class<? extends ServiceLocator> klass = (Class<? extends ServiceLocator>) Thread.currentThread()
	            .getContextClassLoader().loadClass(locator);
	    	ServiceHost host = new ServiceHost(klass);
	    	host.setHostName(System.getProperty(A_HOSTNAME));
	    	IConfigurationElement[] properties = element.getChildren(E_PROPERTY);
	    	for (IConfigurationElement elem : properties) {
	    		String name = elem.getAttribute(A_NAME);
	    		if (System.getProperty(name) != null && !"".equalsIgnoreCase(System.getProperty(name))) {
	    			host.setProperty(name, System.getProperty(name));
	    		} else {
	    			host.setProperty(elem.getAttribute(A_NAME), elem.getAttribute(A_VALUE));
	    		}
	    	}
    		return host;
    	} catch (Exception e){
    		throw e;
    	}
    }
}

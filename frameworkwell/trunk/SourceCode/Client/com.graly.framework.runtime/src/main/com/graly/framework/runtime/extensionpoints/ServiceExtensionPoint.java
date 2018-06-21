package com.graly.framework.runtime.extensionpoints;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.graly.framework.runtime.service.ServiceDescriptor;
import com.graly.framework.runtime.service.ServiceHost;
import com.graly.framework.runtime.service.ServiceLocator;
import com.graly.framework.runtime.service.ServiceManager;

public class ServiceExtensionPoint {
	
	private static final Logger logger = Logger.getLogger(ServiceExtensionPoint.class);
	
	public final static String X_POINT = "com.graly.framework.runtime.service";
	public final static String E_SERVICE = "service";
	public final static String A_HOSTNAME = "hostname";
	public final static String A_CLASS = "class";
	public final static String A_LOCATOR = "locator";
	
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
	            if (elem.getName().equals(E_SERVICE)) {
	            	ServiceDescriptor service = loadService(elem);
	            	manager.registerService(service);
	            } else {
	            	throw new IllegalArgumentException("Element "+elem.getName()+" is not supported by extension-point " + X_POINT);
	            }
	        }
		} catch (Exception e){
	    	logger.error("Error processing extension element. The element is located in an extension in bundle: " + extension.getNamespaceIdentifier(), e); 
		}
    }
	
	@SuppressWarnings("unchecked")
    private ServiceDescriptor loadService(IConfigurationElement element) throws Exception{
    	try{
    		String hostName = element.getAttribute(A_HOSTNAME); 
    		String className = element.getAttribute(A_CLASS); 
    		String locator = element.getAttribute(A_LOCATOR); 
    		ServiceDescriptor service = new ServiceDescriptor(className);
    		service.setLocator(locator);
    		service.setHostName(hostName);
    		return service;
    	} catch (Exception e){
    		throw e;
    	}
    }
}

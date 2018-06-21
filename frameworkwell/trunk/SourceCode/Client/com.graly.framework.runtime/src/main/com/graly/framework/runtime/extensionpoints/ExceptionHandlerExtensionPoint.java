package com.graly.framework.runtime.extensionpoints;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.runtime.exceptionhandler.IExceptionHandler;

public class ExceptionHandlerExtensionPoint {
	
	private static final Logger logger = Logger.getLogger(ExceptionHandlerExtensionPoint.class);
	
	public final static String X_POINT = "com.graly.framework.runtime.exceptionhandler";
	public final static String E_EXCEPTIONHANDLER = "exceptionHandler";
	public final static String A_CLASS = "class";
	public final static String A_TARGETTYPE = "targetType";
	
	private ExceptionHandlerManager manager;
	
	public void initialize() {
    	manager = ExceptionHandlerManager.getInstance();
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
	
	private void loadExtension(IExtension extension){
		try{
			IConfigurationElement[] elements = extension.getConfigurationElements();
	        for (IConfigurationElement elem : elements) {
	            if (elem.getName().equals(E_EXCEPTIONHANDLER)) {
	            	String targetType = elem.getAttribute("targetType");
	            	IExceptionHandler handler = (IExceptionHandler) elem.createExecutableExtension("class");
					if (!IExceptionHandler.class.isAssignableFrom(handler.getClass()))
						throw new IllegalArgumentException("Specified class for element exceptionHandler must implement "+IExceptionHandler.class.getName()+". "+handler.getClass().getName()+" does not."); 
					manager.addExceptionHandler(targetType, handler);

	            } else {
	            	throw new IllegalArgumentException("Element "+elem.getName()+" is not supported by extension-point " + X_POINT); 
	            }
	        }
        } catch (Exception e){
        	logger.error("Error processing extension element. The element is located in an extension in bundle: " + extension.getNamespaceIdentifier(), e); 
    	}
    }
}

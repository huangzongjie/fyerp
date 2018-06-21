package com.graly.framework.base.ui;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

import com.graly.framework.base.entitymanager.forms.EntityProperties;


public class ManagerExtensionPoint {
	
	private final static Logger logger = Logger.getLogger(ManagerExtensionPoint.class);
	
	private static final ManagerExtensionPoint instance = new ManagerExtensionPoint();
	private static HashMap<String, EntityProperties> managerRegistry = new HashMap<String, EntityProperties>();
	
    public final static String X_POINT = "com.graly.framework.base.managers";
    public final static String E_MANAGER = "manager";
    public final static String A_OBJECT = "object";
    public final static String A_CLASS = "class";
    
    public static ManagerExtensionPoint getInstance() {
    	return instance;
    }

	public static HashMap<String, EntityProperties> getManagerRegistry() {
		return managerRegistry;
	}

	static {
		try{
			IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(X_POINT);
			IExtension[] extensions = extensionPoint.getExtensions();
			for (int i = 0; i < extensions.length; i++) {
				IConfigurationElement[] configElements = extensions[i].getConfigurationElements();
				for (int j = 0; j < configElements.length; j++) {
					String object = configElements[j].getAttribute(A_OBJECT);
					EntityProperties manager = (EntityProperties)configElements[j].createExecutableExtension(A_CLASS);
					getManagerRegistry().put(object, manager);
				}
			}			
		} catch (Exception e){
			logger.error("ManagerExtensionPoint : init ", e);
		}
	}
	
	
   
}

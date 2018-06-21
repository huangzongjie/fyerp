package com.graly.framework.base.ui;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

import com.graly.framework.base.entitymanager.forms.EntityProperties;


public class ManagerDialogExtensionPoint {
	
	private final static Logger logger = Logger.getLogger(ManagerDialogExtensionPoint.class);
	
	private static final ManagerDialogExtensionPoint instance = new ManagerDialogExtensionPoint();
	
    public final static String X_POINT = "com.graly.framework.base.managerdialogs";
    public final static String E_MANAGER = "manager";
    public final static String A_OBJECT = "object";
    public final static String A_CLASS = "class";
    
    public static ManagerDialogExtensionPoint getInstance() {
    	return instance;
    }
	
	public static EntityProperties getManager(String object) {
		try{
			IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(X_POINT);
			IExtension[] extensions = extensionPoint.getExtensions();
			for (int i = 0; i < extensions.length; i++) {
				IConfigurationElement[] configElements = extensions[i].getConfigurationElements();
				for (int j = 0; j < configElements.length; j++) {
					if (object.equals(configElements[j].getAttribute(A_OBJECT))) {
						EntityProperties manger = (EntityProperties)configElements[j].createExecutableExtension(A_CLASS);
						return manger;
					}
				}
			}			
		} catch (Exception e){
			logger.error("ManagerDialogExtensionPoint : getManager ", e);
		}
		return null;
	}
	
}

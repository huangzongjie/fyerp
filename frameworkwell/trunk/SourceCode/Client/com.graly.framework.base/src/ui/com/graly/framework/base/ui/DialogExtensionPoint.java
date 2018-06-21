package com.graly.framework.base.ui;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

import com.graly.framework.base.entitymanager.dialog.ExtendDialog;


public class DialogExtensionPoint {
	
	private final static Logger logger = Logger.getLogger(DialogExtensionPoint.class);
	
	private static final DialogExtensionPoint instance = new DialogExtensionPoint();
	
    public final static String X_POINT = "com.graly.framework.base.dialogs";
    public final static String E_DIALOG = "dialog";
    public final static String A_OBJECT = "object";
    public final static String A_CLASS = "class";
    
    public static DialogExtensionPoint getInstance() {
    	return instance;
    }
	
	public static ExtendDialog getDialog(String object) {
		try{
			IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(X_POINT);
			IExtension[] extensions = extensionPoint.getExtensions();
			for (int i = 0; i < extensions.length; i++) {
				IConfigurationElement[] configElements = extensions[i].getConfigurationElements();
				for (int j = 0; j < configElements.length; j++) {
					if (object.equals(configElements[j].getAttribute(A_OBJECT))) {
						ExtendDialog dialog = (ExtendDialog)configElements[j].createExecutableExtension(A_CLASS);
						return dialog;
					}
				}
			}			
		} catch (Exception e){
			logger.error("DialogExtensionPoint : getDialog ", e);
		}
		return null;
	}
}

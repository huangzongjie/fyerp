package com.graly.promisone.base.ui;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

import com.graly.promisone.base.entitymanager.forms.EntityProperties;
import com.graly.promisone.base.ui.wizard.FlowWizardPage;


public class WizardPageExtensionPoint {
	
	private final static Logger logger = Logger.getLogger(WizardPageExtensionPoint.class);
	
	private static final WizardPageExtensionPoint instance = new WizardPageExtensionPoint();
	
	private static Map<String, Map> pageCategoryRegistry = new HashMap<String, Map>();
	
    public final static String X_POINT = "com.graly.promisone.base.wizard";
    public final static String E_PAGE = "page";
    public final static String A_CATEGORY = "category";
    public final static String A_NAME = "name";
    public final static String A_CLASS = "class";
    public final static String A_DIRECT = "defaultDirect";
    public final static String A_ISSTART = "isStart";
    
	static {
		try{
			IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(X_POINT);
			IExtension[] extensions = extensionPoint.getExtensions();
			for (int i = 0; i < extensions.length; i++) {
				IConfigurationElement[] configElements = extensions[i].getConfigurationElements();
				for (int j = 0; j < configElements.length; j++) {
					String category = configElements[j].getAttribute(A_CATEGORY);
					if (pageCategoryRegistry.get(category) == null) {
						pageCategoryRegistry.put(category, new HashMap<String, IConfigurationElement>());
					}
					Map pageMap = pageCategoryRegistry.get(category);
					String name = configElements[j].getAttribute(A_NAME);
					pageMap.put(name, configElements[j]);
				}
			}			
		} catch (Exception e){
			logger.error("WizardPageExtensionPoint : init ", e);
		}
	}
    
    public static WizardPageExtensionPoint getInstance() {
    	return instance;
    }

	public static Map<String, Map> getPageCategoryRegistry() {
		return pageCategoryRegistry;
	}   
}

package com.graly.framework.base.ui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

public class PanelExtensionPoint {
	
	private final static Logger logger = Logger.getLogger(PanelExtensionPoint.class);
	
	private static final PanelExtensionPoint instance = new PanelExtensionPoint();
	
	private static Map<String, IConfigurationElement> panelRegistry = new HashMap<String, IConfigurationElement>();
	
    public final static String X_POINT = "com.graly.framework.base.panels";
    public final static String E_PANEL = "panel";
    public final static String A_ID = "id";
    public final static String A_PERSPECTIVE = "perspective";
    public final static String A_NAME = "name";
    public final static String A_CLOSEABLE = "closeable";
    
    public static PanelExtensionPoint getInstance() {
    	return instance;
    }

	static {
		try{
			IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(X_POINT);
			IExtension[] extensions = extensionPoint.getExtensions();
			for (int i = 0; i < extensions.length; i++) {
				IConfigurationElement[] configElements = extensions[i].getConfigurationElements();
				for (int j = 0; j < configElements.length; j++) {
					String id = configElements[j].getAttribute(A_ID);
					panelRegistry.put(id, configElements[j]);
				}
			}			
		} catch (Exception e){
			logger.error("PanelExtensionPoint : init ", e);
		}
	}
	
	public static IConfigurationElement getPanel(String id) {
		return panelRegistry.get(id);
	}
	
	public static Collection<IConfigurationElement> getPanels() {
		return panelRegistry.values();
	}
   
}

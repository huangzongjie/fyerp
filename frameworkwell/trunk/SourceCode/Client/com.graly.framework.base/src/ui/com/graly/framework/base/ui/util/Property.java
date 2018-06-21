package com.graly.framework.base.ui.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADListProperty;
import com.graly.framework.activeentity.model.ADSingleProperty;
import com.graly.framework.runtime.Framework;

public class Property {
	private final static Logger logger = Logger.getLogger(Property.class);
	private final static Map<String, String> singleProperties = new HashMap<String, String>();
	private final static Map<String, List<String>> listProperties = new HashMap<String, List<String>>();
	
	public Property() {
	}
	
	public static void load(){
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			synchronized (singleProperties) {
				try {
					List<ADSingleProperty> props = entityManager.getSingleProperty();
					for (ADSingleProperty prop : props){
						singleProperties.put(prop.getKey(), prop.getValue());
					}
		        } catch (Exception e) {
					logger.error("Property : Load error ", e);
		        }
			}
			
			synchronized (listProperties) {
				try {
					List<ADListProperty> props = entityManager.getListProperty();
					for (ADListProperty prop : props){
						listProperties.put(prop.getKey(), prop.getValues());
					}
		        } catch (Exception e) {
					logger.error("Property : Load error ", e);
		        }
			}
		} catch (Exception e) {
			logger.error("Property : Load error ", e);
		}
	}
	
	public static String getSingleProperty(String key){
		return singleProperties.get(key) == null ? "" : singleProperties.get(key);
	}
	
	public static List<String> getListProperty(String key){
		return listProperties.get(key) == null ? new ArrayList<String>() : listProperties.get(key);
	}
}

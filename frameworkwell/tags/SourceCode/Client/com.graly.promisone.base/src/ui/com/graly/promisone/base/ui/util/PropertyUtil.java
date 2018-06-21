package com.graly.promisone.base.ui.util;

import java.util.LinkedHashMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import com.graly.promisone.base.entitymanager.forms.EntityForm;
import com.graly.promisone.base.ui.forms.field.IField;

public class PropertyUtil {
	
	private static final Logger logger = Logger.getLogger(PropertyUtil.class);

	public static void setProperty(Object bean, String name, Object value) {
		try {
			if(name != null && !"".equals(name))
				PropertyUtils.setSimpleProperty(bean, name, value);
		} catch (IllegalArgumentException ie) {
			try {
				if ((value instanceof String) && value != null && 
						"".equals(((String)value).trim())){
					PropertyUtils.setSimpleProperty(bean, name, null);
					return;
				} 
				Class klass = PropertyUtils.getPropertyType(bean, name);
				if (value instanceof Boolean ) {
					if ("java.lang.String".equalsIgnoreCase(klass.getName())) {
						if ((Boolean)value) {
							PropertyUtils.setSimpleProperty(bean, name, "Y");
						} else {
							PropertyUtils.setSimpleProperty(bean, name, "N");
						}
					}
				}
				if ("java.util.Date".equalsIgnoreCase(klass.getName())) {
					if (value instanceof String) {
						try {
							PropertyUtils.setSimpleProperty(bean, name,
									I18nUtil.parseDate((String) value));
						} catch (Exception e1) {
							logger.error("PropertyUtil : setProperty ", e1);
						}
					}
				} else if ("java.lang.Long".equalsIgnoreCase(klass.getName())) {
					if (value instanceof String) {
						try {
							PropertyUtils.setSimpleProperty(bean, name, Long
									.parseLong((String) value));
						} catch (Exception e1) {
							logger.error("PropertyUtil : setProperty ", e1);
						}
					}
				} else if ("java.lang.Double".equalsIgnoreCase(klass.getName())) {
					if (value instanceof String) {
						try {
							PropertyUtils.setSimpleProperty(bean, name, Double
									.parseDouble((String) value));
						} catch (Exception e1) {
							logger.error("PropertyUtil : setProperty ", e1);
						}
					}
				}
			}  catch (Exception ex) {
				logger.error("PropertyUtil : setProperty ", ex);
			}
		} catch (java.lang.NoSuchMethodException ne){
			logger.error("PropertyUtil : setProperty " + "name=" + name , ne);
		}
		catch (Exception e) {
			logger.error("PropertyUtil : setProperty ", e);
		}
	}

	public static Object getPropertyForIField(Object bean, String name) {
		Object value = null;
		try {
			if(name != null && !"".equals(name)){
				value = PropertyUtils.getSimpleProperty(bean, name);
			}
			if (value != null)
				if (value instanceof java.sql.Timestamp) {
					value = new java.util.Date(((java.sql.Timestamp)value).getTime());
				} else if (value instanceof Long) {
					value = ((Long)value).toString();
				} else if (value instanceof Double) {
					value = ((Double)value).toString();
				}
		} catch (Exception e) {
			logger.error("PropertyUtil : getPropertyForIField ", e);
		}
		return value;
	}
	
	public static Object getPropertyForString(Object bean, String name) {
		Object value = null;
		try {
			if(name != null && !"".equals(name)){
				value = PropertyUtils.getSimpleProperty(bean, name);
			}
			
			if (value != null)
				if (value instanceof java.sql.Timestamp) {
					value = new java.util.Date(((java.sql.Timestamp)value).getTime());
					value = I18nUtil.formatDateTime((java.util.Date)value, false);
				} else if (value instanceof Long) {
					value = ((Long)value).toString();
				} else if (value instanceof Double) {
					value = ((Double)value).toString();
				} else if (value instanceof Boolean) {
					value = (Boolean)value == true ? "Y" : "N";
				}
		} catch (Exception e) {
			logger.error("PropertyUtil : getPropertyForString ", e);
		}
		return value;
	}
	
	public static void copyProperties(Object destBean, Object sourceBean, LinkedHashMap<String, IField> fields){
		for (String name : fields.keySet()){
			try {
				if(name != null && !"".equals(name)){
					Object obj = PropertyUtils.getProperty(sourceBean, name);
					PropertyUtils.setProperty(destBean, name, obj);
				}
				
			} catch (Exception e) {
				logger.error("PropertyUtil : copyProperties ", e);
			}
		}
	}
	
	public static String getValueString(Object value){
		if (value == null) {
			return "";
		}
		if (value instanceof java.util.Date) {
			value = I18nUtil.formatDate((java.util.Date)value);
		} else if (value instanceof Long) {
			value = ((Long)value).toString();
		} else if (value instanceof Double) {
			value = ((Double)value).toString();
		} else if (value instanceof Boolean) {
			value = (Boolean)value == true ? "Y" : "N";
		} else if (value instanceof String) {
			
		}
		return (String)value;
	}
}

package com.graly.framework.activeentity.util;


import java.math.BigDecimal;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;


public class DataFmtUtil {
	private static final Logger logger = Logger.getLogger(DataFmtUtil.class);
	
	public static String bean2String(Object bean, String[] properties){
		StringBuffer formatSb = new StringBuffer();
		int i=0;
		for(String property : properties){
			if((i++)>0){
				formatSb.append(",");
			}
			Object propertyVal = getPropertyForString(bean, property);
			formatSb.append(property+"="+propertyVal);
		}
		return formatSb.toString();
	}
	
	public static String array2String(Object[] objs, String[] keys){
		StringBuffer formatSb = new StringBuffer();
		for(int i=0; i< keys.length; i++){
			if(i>0){
				formatSb.append(",");
			}
			
			if(i < objs.length){
				formatSb.append(keys[i]+"="+convertObjectToString(objs[i]));
			}else{
				formatSb.append(keys[i]+"= ");
			}
		}
		return formatSb.toString();
	}
	
	/**
	 * copy from com.graly.framework.base.ui.util.PropertyUtil.getPropertyForString(Object bean, String name)
	 * @param bean
	 * @param name
	 * @return
	 */
	private static Object getPropertyForString(Object bean, String name) {
		Object value = null;
		try {
			if(name != null && !"".equals(name)){
				value = PropertyUtils.getSimpleProperty(bean, name);
			}
			
			value = convertObjectToString(value);
		} catch (Exception e) {
			logger.error("PropertyUtil : getPropertyForString ", e);
		}
		return value;
	}
	
	public static String convertObjectToString(Object obj){
		String value = "";
		if (obj != null){
			if (obj instanceof java.sql.Timestamp) {
				obj = new java.util.Date(((java.sql.Timestamp)obj).getTime());
				value = I18nUtil.formatDateTime((java.util.Date)obj, false);
			}else if(obj instanceof java.sql.Date){
				obj = new java.util.Date(((java.sql.Date)obj).getTime());
				value = I18nUtil.formatDateTime((java.util.Date)obj, false);
			} else if (obj instanceof Long) {
				value = ((Long)obj).toString();
			} else if (obj instanceof Double) {
				value = ((Double)obj).toString();
			} else if (obj instanceof Boolean) {
				value = (Boolean)obj == true ? "Y" : "N";
			} else if (obj instanceof BigDecimal) {
				value = ((BigDecimal)obj).toString();
			} else if (obj instanceof java.util.Date) {
				value = I18nUtil.formatDateTime((java.util.Date)obj, false);
			} else if (obj instanceof String){
				value = (String) obj;
			}
		}
		return value;
	}
}

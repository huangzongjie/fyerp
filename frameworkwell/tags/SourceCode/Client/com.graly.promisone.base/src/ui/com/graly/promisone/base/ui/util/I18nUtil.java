package com.graly.promisone.base.ui.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.beanutils.PropertyUtils;

public class I18nUtil {
	
	public static String getI18nMessage(Object bean, String property){
		String message = "";
		try{
			String name = property + "_" + Locale.getDefault().getLanguage();
			message = (String)PropertyUtils.getProperty(bean, name);
		} catch (Exception e){
			try{
				message = (String)PropertyUtils.getProperty(bean, property);
			} catch (Exception ex){
				
			}
		}
		return message == null ? "" : message;
	}
	
	public static String getDefaultDatePattern(){
		return "yyyy-MM-dd";
	}
	
	public static String getDefaultTimePattern(){
		return "HH:mm:ss";
	}
	
	public static String getDefaultDateTimePattern(){
		return "yyyy-MM-dd HH:mm:ss";
	}
	
	public static Date parseDate(String dateString){
		if (dateString == null) {
            return null;
        }
		SimpleDateFormat formatter = new SimpleDateFormat(getDefaultDatePattern());
        formatter.setLenient(false);
        try {
            return formatter.parse(dateString);
        } catch(ParseException e) {
            return null;
        }
	}
	
	public static String formatDate(Date date){
		if (date == null) {
            return null;
        }
		SimpleDateFormat formatter = new SimpleDateFormat(getDefaultDatePattern());
        formatter.setLenient(false);
        try {
            return formatter.format(date);
        } catch(Exception e) {
            return null;
        }
	}
	
	public static String formatDateTime(Date date, boolean force){
		if (date == null) {
            return null;
        }
		SimpleDateFormat formatter = null;
		if (force) {
			formatter = new SimpleDateFormat(getDefaultDateTimePattern());
		} else {
			if (date.getHours() == 0 && date.getHours() == 0 && date.getSeconds() == 0) {
				formatter = new SimpleDateFormat(getDefaultDatePattern());
			} else {
				formatter = new SimpleDateFormat(getDefaultDateTimePattern());
			}
		}
        formatter.setLenient(false);
        try {
            return formatter.format(date);
        } catch(Exception e) {
            return null;
        }
	}
	
	public static String formatDateTime(Date date){
		return formatDateTime(date, true);
	}
}

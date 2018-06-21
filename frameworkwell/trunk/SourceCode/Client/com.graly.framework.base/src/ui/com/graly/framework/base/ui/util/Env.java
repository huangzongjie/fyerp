package com.graly.framework.base.ui.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.runtime.Framework;
import com.graly.framework.security.model.ADUser;
import com.graly.framework.security.model.WorkCenter;

public final class Env {
	
	private static final Logger logger = Logger.getLogger(Env.class);
	private static Properties sctx = new Properties();
	private static List<String> authority;
	private static ADUser user;
	private static List<WorkCenter> workCenters;
	private static ADManager adManager;
	
	static {
		try {
			String count = Message.getString("sys.counts_show_perpage");
			setContext(getCtx(), "#Max_Result", Integer.valueOf(count));
		} catch (Exception e) {
			logger.error(e);
			setContext(getCtx(), "#Max_Result", 1000);
		}
	}
	
	public static final Properties getCtx() {
		return sctx;
	}  

	public static void setCtx (Properties ctx) {
		if (ctx == null)
			throw new IllegalArgumentException ("Require Context");
		sctx.clear();
		sctx = ctx;
	}
	
	public static void setContext (Properties ctx, String context, String value) {
		if (ctx == null || context == null)
			return;
		logger.info("Context " + context + "==" + value);
		if (value == null || value.length() == 0)
			ctx.remove(context);
		else
			ctx.setProperty(context, value);
	}
	
	public static void setContext (Properties ctx, String context, Timestamp value) {
		if (ctx == null || context == null)
			return;
		if (value == null) {
			ctx.remove(context);
			logger.info("Context " + context + "==" + value);
		} else {	//	
			String stringValue = value.toString();
			//	Chop off .0
			stringValue = stringValue.substring(0, stringValue.length()-2);		
			ctx.setProperty(context, stringValue);
			logger.info("Context " + context + "==" + stringValue);
		}
	}
	
	public static void setContext (Properties ctx, String context, int value) {
		if (ctx == null || context == null)
			return;
		logger.info("Context " + context + "==" + value);
		//
		ctx.setProperty(context, String.valueOf(value));
	}
	
	public static void setContext (Properties ctx, String context, long value) {
		if (ctx == null || context == null)
			return;
		logger.info("Context " + context + "==" + value);
		//
		ctx.setProperty(context, String.valueOf(value));
	}
	
	public static void setContext (Properties ctx, String context, boolean value) {
		setContext (ctx, context, value ? "Y" : "N");
	}
	
	public static String getContext (Properties ctx, String context) {
		if (ctx == null || context == null)
			throw new IllegalArgumentException ("Require Context");
		return ctx.getProperty(context, "");
	}
	
	public static int getContextAsInt(Properties ctx, String context) {
		if (ctx == null || context == null)
			throw new IllegalArgumentException ("Require Context");
		String s = getContext(ctx, context);
		if (s.length() == 0)
			return 0;
		//
		try {
			return Integer.parseInt(s);
		}
		catch (NumberFormatException e)
		{
			logger.error("(" + context + ") = " + s, e);
		}
		return 0;
	}
	
	public static long getContextAsLong(Properties ctx, String context) {
		if (ctx == null || context == null)
			throw new IllegalArgumentException ("Require Context");
		String s = getContext(ctx, context);
		if (s.length() == 0)
			return 0;
		//
		try {
			return Long.parseLong(s);
		}
		catch (NumberFormatException e)
		{
			logger.error("(" + context + ") = " + s, e);
		}
		return 0;
	}
	
	public static void setUserName (String userName) {
		Env.setContext(Env.getCtx(), "#AD_User_Name", userName);
	}
	
	public static String getUserName () {
		return Env.getContext(Env.getCtx(), "#AD_User_Name");
	}
	
	public static void setUserRrn (long userRrn) {
		Env.setContext(Env.getCtx(), "#AD_User_Rrn", userRrn);
	}
	
	public static long getUserRrn () {
		return Env.getContextAsLong(Env.getCtx(), "#AD_User_Rrn");
	}
	
	public static void setOrgRrn (long orgRrn) {
		Env.setContext(Env.getCtx(), "#AD_Org_Rrn", orgRrn);
	}
	
	public static long getOrgRrn () {
		return Env.getContextAsLong(Env.getCtx(), "#AD_Org_Rrn");
	}
	
	public static void setOrgName (String orgName) {
		Env.setContext(Env.getCtx(), "#AD_Org_Name", orgName);
	}
	
	public static String getOrgName () {
		return Env.getContext(Env.getCtx(), "#AD_Org_Name");
	}
	
	public static List<String> getAuthority () {
		return authority;
	}
	
	public static void setAuthority (List<String> authority) {
		Env.authority = authority;
	}
	
	public static int getMaxResult () {
		return Env.getContextAsInt(getCtx(), "#Max_Result");
	}

	public static List<WorkCenter> getWorkCenters() {
		return workCenters;
	}

	public static void setWorkCenters(List<WorkCenter> workCenters) {
		Env.workCenters = workCenters;
	}

	public static ADUser getUser() {
		return user;
	}

	public static void setUser(ADUser user) {
		Env.user = user;
	}
	
	public static Date getSysDate(){
		try {
			if(adManager == null){
				adManager = Framework.getService(ADManager.class);
			}
			
		} catch (Exception e) {
			logger.error("Env : getSysDate", e);
		}
		return adManager.getSysDate();
	}
}

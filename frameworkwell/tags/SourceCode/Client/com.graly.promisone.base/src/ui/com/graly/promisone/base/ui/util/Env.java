package com.graly.promisone.base.ui.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.sql.Timestamp;
import org.apache.log4j.Logger;

public final class Env {
	
	private static final Logger logger = Logger.getLogger(Env.class);
	private static Properties s_ctx = new Properties();
	private static List<String> authority;
	
	static {
		setContext(getCtx(), "#Max_Result", 9999);
	}
	
	public static final Properties getCtx()
	{
		return s_ctx;
	}  

	public static void setCtx (Properties ctx) {
		if (ctx == null)
			throw new IllegalArgumentException ("Require Context");
		s_ctx.clear();
		s_ctx = ctx;
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
		if (value == null)
		{
			ctx.remove(context);
			logger.info("Context " + context + "==" + value);
		}
		else
		{	//	JDBC Format	2005-05-09 00:00:00.0
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
	
	public static String getUserName () {
		return Env.getContext(Env.getCtx(), "#AD_User_Name");
	}
	
	public static long getUserId () {
		return Env.getContextAsLong(Env.getCtx(), "#AD_User_Id");
	}
	
	public static long getOrgId () {
		return Env.getContextAsLong(Env.getCtx(), "#AD_Org_Id");
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
}

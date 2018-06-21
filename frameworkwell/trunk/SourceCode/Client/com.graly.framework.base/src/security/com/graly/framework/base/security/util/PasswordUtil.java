package com.graly.framework.base.security.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.base.ui.util.Property;
import com.graly.framework.runtime.Framework;
import com.graly.framework.security.model.ADUser;

public class PasswordUtil {
	private final static Logger logger = Logger.getLogger(PasswordUtil.class);
	private final static String PROP_PWD_LEN = "PWD.Len";
	private final static String PROP_PWD_EXCLUDE = "PWD.Exclude";
	private final static String PROP_PWD_LIFE = "PWD.Life";
	private final static String PROP_PWD_BEFORE_EXPIRED = "PWD.BeforeExpired";
	
	public static boolean checkPWD(String pwd){
		return checkPWD(pwd, null);
	}
	
	public static boolean checkPWD(String pwd, StringBuffer message){
		try {
			//1.判断pwd是否是空(是否null, 是否长度为0,是否全空格)
			if(pwd == null || pwd.trim().length() == 0){
				if(message != null){
					message.append("密码不能为空!");
				}
				return false;
			}
			
			//2.判断pwd中是否含有空格
			if(pwd.contains(" ")){
				if(message != null){
					message.append("密码不能包含空格!");
				}
				return false;
			}
			
			//3.判断字符串长度是否达到系统要求的位数
			String lenStr = Property.getSingleProperty(PROP_PWD_LEN);
			if(lenStr != null && lenStr.trim().length() > 0){
				int len = Integer.valueOf(lenStr);
				if(pwd.length() < len){
					if(message != null){
						message.append("密码长度不能小于" + len + "位!");
					}
					return false;
				}
			}
			
			//4.判断密码是否单一字符组成
			char c = pwd.charAt(0);
			String regex = "^"+c+"{2,}$";
			Pattern pat = Pattern.compile(regex);
			Matcher ma = pat.matcher(pwd);
			if(ma.matches()){
				if(message != null){
					message.append("密码不能为单一字符!");
				}
				return false;
			}
			
			//5.判断密码是否是排除密码中的
			List<String> pwdExcludes = Property.getListProperty(PROP_PWD_EXCLUDE);
			for(String pwdEx : pwdExcludes){
				if(pwd.equals(pwdEx)){
					if(message != null){
						message.append("密码不能为\"" + pwdEx + "\"!");
					}
					return false;
				}
			}
		} catch (Exception e) {
			logger.error("PasswordUtil : checkPWD", e);
		}
		
		return true;
	}
	
	/**
	 * 判断用户密码是否将过期
	 * @param user
	 * @return true将过期，false未将过期
	 */
	public static boolean willPwdExpired(ADUser user){
		return willPwdExpired(user);
	}
	
	public static boolean willPwdExpired(ADUser user, StringBuffer msg){
		try {
			String lifeStr = Property.getSingleProperty(PROP_PWD_LIFE);
			String adviceDaysStr = Property.getSingleProperty(PROP_PWD_BEFORE_EXPIRED);
			int userPwdLife = ( user.getPwdLife() == null ? 0 : user.getPwdLife().intValue() );
			int adviceDays = 0;
			int life = 0;
			
			//如果为用户设定了密码有效期，则以为用户设定的为准
			if(userPwdLife > 0){
				life = userPwdLife;
			}
			if(lifeStr != null && lifeStr.trim().length() > 0 && life == 0){
				life = Integer.valueOf(lifeStr);
			}
			
			if(adviceDaysStr != null && adviceDaysStr.trim().length() > 0){
				adviceDays = Integer.valueOf(adviceDaysStr);
			}
			
			if(life > 0){
				java.util.GregorianCalendar calender = new java.util.GregorianCalendar();
				calender.setTime(user.getPwdChanged());
				calender.add(java.util.Calendar.DAY_OF_YEAR, life);
				ADManager manager = Framework.getService(ADManager.class);
				Date now = manager.getSysDate();
				long days = (calender.getTime().getTime() - now.getTime()) /(24*60*60*1000);
				if(days <= adviceDays){
					if(msg != null){
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
						String exprieDate = sdf.format(calender.getTime());
						msg.append("您的密码即将于"+exprieDate+"过期，建议您立即修改密码!");
					}
					return true;
				}
			}
		} catch (Exception e) {
			logger.error("PasswordUtil : willPwdExpired", e);
		}
		return false;
	}
	
	/**
	 * 判断用户密码是否已经过期
	 * @param user
	 * @return true将过期，false未将过期
	 */
	public static boolean isPwdExpired(ADUser user){
		try {
			String lifeStr = Property.getSingleProperty(PROP_PWD_LIFE);
			String adviceDaysStr = Property.getSingleProperty(PROP_PWD_BEFORE_EXPIRED);
			int userPwdLife = ( user.getPwdLife() == null ? 0 : user.getPwdLife().intValue() );
			int adviceDays = 0;
			int life = 0;
			
			//如果为用户设定了密码有效期，则以为用户设定的为准
			if(userPwdLife > 0){
				life = userPwdLife;
			}
			if(lifeStr != null && lifeStr.trim().length() > 0 && life == 0){
				life = Integer.valueOf(lifeStr);
			}
			
			if(adviceDaysStr != null && adviceDaysStr.trim().length() > 0){
				adviceDays = Integer.valueOf(adviceDaysStr);
			}
			
			if(life > 0){
				java.util.GregorianCalendar calender = new java.util.GregorianCalendar();
				calender.setTime(user.getPwdChanged());
				calender.add(java.util.Calendar.DAY_OF_YEAR, life);
				ADManager manager = Framework.getService(ADManager.class);
				Date now = manager.getSysDate();
				if(now.compareTo(calender.getTime()) >= 0){
					return true;
				}
			}
		} catch (Exception e) {
			logger.error("PasswordUtil : willPwdExpired", e);
		}
		return false;
	}
}

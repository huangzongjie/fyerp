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
			//1.�ж�pwd�Ƿ��ǿ�(�Ƿ�null, �Ƿ񳤶�Ϊ0,�Ƿ�ȫ�ո�)
			if(pwd == null || pwd.trim().length() == 0){
				if(message != null){
					message.append("���벻��Ϊ��!");
				}
				return false;
			}
			
			//2.�ж�pwd���Ƿ��пո�
			if(pwd.contains(" ")){
				if(message != null){
					message.append("���벻�ܰ����ո�!");
				}
				return false;
			}
			
			//3.�ж��ַ��������Ƿ�ﵽϵͳҪ���λ��
			String lenStr = Property.getSingleProperty(PROP_PWD_LEN);
			if(lenStr != null && lenStr.trim().length() > 0){
				int len = Integer.valueOf(lenStr);
				if(pwd.length() < len){
					if(message != null){
						message.append("���볤�Ȳ���С��" + len + "λ!");
					}
					return false;
				}
			}
			
			//4.�ж������Ƿ�һ�ַ����
			char c = pwd.charAt(0);
			String regex = "^"+c+"{2,}$";
			Pattern pat = Pattern.compile(regex);
			Matcher ma = pat.matcher(pwd);
			if(ma.matches()){
				if(message != null){
					message.append("���벻��Ϊ��һ�ַ�!");
				}
				return false;
			}
			
			//5.�ж������Ƿ����ų������е�
			List<String> pwdExcludes = Property.getListProperty(PROP_PWD_EXCLUDE);
			for(String pwdEx : pwdExcludes){
				if(pwd.equals(pwdEx)){
					if(message != null){
						message.append("���벻��Ϊ\"" + pwdEx + "\"!");
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
	 * �ж��û������Ƿ񽫹���
	 * @param user
	 * @return true�����ڣ�falseδ������
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
			
			//���Ϊ�û��趨��������Ч�ڣ�����Ϊ�û��趨��Ϊ׼
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
						msg.append("�������뼴����"+exprieDate+"���ڣ������������޸�����!");
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
	 * �ж��û������Ƿ��Ѿ�����
	 * @param user
	 * @return true�����ڣ�falseδ������
	 */
	public static boolean isPwdExpired(ADUser user){
		try {
			String lifeStr = Property.getSingleProperty(PROP_PWD_LIFE);
			String adviceDaysStr = Property.getSingleProperty(PROP_PWD_BEFORE_EXPIRED);
			int userPwdLife = ( user.getPwdLife() == null ? 0 : user.getPwdLife().intValue() );
			int adviceDays = 0;
			int life = 0;
			
			//���Ϊ�û��趨��������Ч�ڣ�����Ϊ�û��趨��Ϊ׼
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

package com.graly.erp.inv.alarm.query;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {

	public static void main(String[] args){
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d1 =null;
		Date d2 =null;
		try {
			d1 = df.parse("2013-06-23 13:31:40");
			d2 = df.parse("2013-06-24 13:31:40");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		long diff = d2.getTime() - d1.getTime(); 
		float days = diff / (1000 * 60 * 60 * 24);
		System.out.println(days);
		
		double  between=(d2.getTime()-d1.getTime())/1000;//除以1000是为了转换成秒   
		double  day=between/(24*3600);
		System.out.println(day);
		
//		BigDecimal bigDecimal = new BigDecimal(day, BigDecimal.ROUND_CEILING);
		
	}
	
	
}

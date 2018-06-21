package com.graly.erp.base.client;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import com.graly.erp.base.calendar.BusinessCalendar;
import com.graly.erp.base.calendar.model.CalendarDay;
import com.graly.erp.base.calendar.model.CalendarHour;
import com.graly.framework.core.exception.ClientException;

public interface BASManager {
	
	List<CalendarDay> selectCalendarDay(int year,int month,String calendarType, long userRrn,long orgRrn)throws ClientException;
	void saveCalendarDay(long orgRrn, List<CalendarDay> calendarDays, String calendarType, long userRrn)throws ClientException ;
	void saveCalendarHour(long orgRrn, List<CalendarHour> calendarHours, long userRrn)throws ClientException ;
	BusinessCalendar getCalendarByDay(long orgRrn, String calendarType) throws ClientException;
	BusinessCalendar getCalendarByDayHours(long orgRrn, String calendarType, String weekType) throws ClientException;
	List<String> getWeekType(long orgRrn) throws ClientException;
	
	long getHisSequence();
	String getDocCode(long orgRrn, String docType); 
	String getYearCode(long orgRrn, String year);
//	String getOrgCode(String org);
//	String getDocCode(long orgRrn, String docType);
	String generateCodePrefix(long orgRrn, String docType);
	String generateCodeSuffix(long orgRrn, String docType, Date docDate);
	
	String getCurrentDateCode(long orgRrn);
	public EntityManager getEntityManager();
	
}

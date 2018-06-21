package com.graly.erp.base.ejb;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.graly.erp.base.calendar.BusinessCalendar;
import com.graly.erp.base.calendar.model.CalendarDay;
import com.graly.erp.base.calendar.model.CalendarHour;
import com.graly.erp.base.client.BASManager;
import com.graly.erp.base.model.CodeDay;
import com.graly.erp.base.model.CodeDoc;
import com.graly.erp.base.model.CodeMonth;
import com.graly.erp.base.model.CodeYear;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.security.model.ADOrg;


@Stateless
@Remote(BASManager.class)
@Local(BASManager.class)
public class BASManagerBean implements BASManager {
	private static final Logger logger = Logger.getLogger(BASManagerBean.class);

	@PersistenceContext
	private EntityManager em;

	@EJB
	private ADManager adManager;
	
	public void saveCalendarDay(long orgRrn, List<CalendarDay> calendarDays, String calendarType, long userRrn)throws ClientException {
		try {
			for (int i=0; i<calendarDays.size(); i++) {
				CalendarDay calendarDay = calendarDays.get(i);
				if (calendarDay != null && calendarDay.getObjectRrn() != null) {
					calendarDay.setUpdated(new Date());
					calendarDay.setUpdatedBy(userRrn);
					em.merge(calendarDay);
					
				} else {
					calendarDay.setOrgRrn(orgRrn);
					calendarDay.setIsActive(true);
					calendarDay.setCreatedBy(userRrn);
					calendarDay.setCreated(new Date());
					calendarDay.setUpdated(new Date());
					calendarDay.setUpdatedBy(userRrn);
					calendarDay.setCalendarType(calendarType);
					em.persist(calendarDay);
				}
			}
		} catch (OptimisticLockException e) {
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	public List<CalendarDay> selectCalendarDay(int year, int month, String calendarType, long userRrn,long orgRrn)
			throws ClientException {
		List<CalendarDay> calendarDays = new ArrayList<CalendarDay>();
		
		try {
			if (year != 0 && month != 0) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				/*获得某月的第一天*/
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, year);
				calendar.set(Calendar.MONTH, month - 1);
				calendar.set(Calendar.DATE, 1);
				Date first=calendar.getTime();
				Date firstDate = dateFormat.parse(dateFormat.format(first));
				/*获得下月的第一天*/
				calendar.set(Calendar.YEAR, year);
				calendar.set(Calendar.MONTH, month);
				calendar.set(Calendar.DATE, 1);
				Date last=calendar.getTime();
				Date lastDate = dateFormat.parse(dateFormat.format(last));

				StringBuffer sql1 = new StringBuffer();
				sql1.append("SELECT CalendarDay FROM CalendarDay as CalendarDay ");
				sql1.append(" WHERE (day < ? AND day >= ?)  AND calendarType = ?  and orgRrn=?");
				Query query = em.createQuery(sql1.toString());
				query.setParameter(1, lastDate);
				query.setParameter(2, firstDate);
				query.setParameter(3, calendarType);
				query.setParameter(4, orgRrn);
				calendarDays = query.getResultList();
				if (calendarDays.size() == 0) {
					CalendarDay calendarFirstDay = new CalendarDay();
					
					java.sql.Timestamp sqlFirstDate = new java.sql.Timestamp(firstDate.getTime());
					calendarFirstDay.setDay(sqlFirstDate);
					calendarDays.add(calendarFirstDay);
					/*获得某月的天数*/
					calendar.set(Calendar.YEAR, year);
					calendar.set(Calendar.MONTH, month);
					calendar.set(Calendar.DATE, 0);
					int dayNumbers = calendar.get(Calendar.DATE);
					int day;
					Date nextDate,next;
					for (int i = 1; i < dayNumbers; i++) {
						CalendarDay calendarDay = new CalendarDay();
						calendar.setTime(firstDate);
						day = calendar.get(Calendar.DATE);
						calendar.set(Calendar.DATE, day + i);
						next=calendar.getTime();
						nextDate = dateFormat.parse(dateFormat.format(next));
						java.sql.Timestamp sqlNextDate = new java.sql.Timestamp(nextDate.getTime());
						calendarDay.setDay(sqlNextDate);
						calendarDay.setOrgRrn(orgRrn);
						calendarDays.add(calendarDay);
					}
				}
			}
		} catch (OptimisticLockException e) {
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}

		return calendarDays;
	}
	
	public void saveCalendarHour(long orgRrn, List<CalendarHour> calendarHours, long userRrn)throws ClientException {
		try {
			for (int i = 0; i<calendarHours.size(); i++) {
				CalendarHour hour = calendarHours.get(i);
				
				if (hour != null && hour.getObjectRrn() != null) {
					hour.setUpdated(new Date());
					hour.setUpdatedBy(userRrn);
					em.merge(hour);
				}else{
					hour.setOrgRrn(orgRrn);
					hour.setIsActive(true);
					hour.setCreated(new Date());
					hour.setCreatedBy(userRrn);
					hour.setUpdated(new Date());
					hour.setUpdatedBy(userRrn);
					em.persist(hour);
				}
			}
		} catch (OptimisticLockException e) {
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public BusinessCalendar getCalendarByDay(long orgRrn, String calendarType) throws ClientException {
		try {
			String whereClause = " isHoliday = 'Y' AND calendarType = '" + calendarType + "'";
			List<CalendarDay> holidays = adManager.getEntityList(orgRrn, CalendarDay.class, Integer.MAX_VALUE, whereClause, "");
			
			List<CalendarHour> hours = getDefaultHours();
			
			BusinessCalendar businessCalendar = new BusinessCalendar(holidays, hours);
			return businessCalendar;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public BusinessCalendar getCalendarByDayHours(long orgRrn, String calendarType, String weekType) throws ClientException {
		try {
			String whereClause = " isHoliday = 'Y' AND calendarType = '" + calendarType + "'";
			List<CalendarDay> holidays = adManager.getEntityList(orgRrn, CalendarDay.class, Integer.MAX_VALUE, whereClause, "");
			
			if (weekType == null || weekType.length() == 0) {
				whereClause = " weekType = '" + BusinessCalendar.WEEKTYPE_DEFAULT + "'";
			} else {
				whereClause = " weekType = '" + weekType + "'";
			}
			List<CalendarHour> hours = adManager.getEntityList(orgRrn, CalendarHour.class, Integer.MAX_VALUE, whereClause, "");
			if (hours == null || hours.size() == 0) {
				hours = getDefaultHours();
			}
			BusinessCalendar businessCalendar = new BusinessCalendar(holidays, hours);
			return businessCalendar;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<String> getWeekType(long orgRrn) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DISTINCT CalendarHour.weekType FROM CalendarHour as CalendarHour ");
		sql.append("WHERE");
		sql.append(ADBase.BASE_CONDITION);
		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgRrn);
			return (List<String>)query.getResultList();
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	private List<CalendarHour> getDefaultHours() {
		List<CalendarHour> defaultHours = new ArrayList<CalendarHour>();
		CalendarHour hour1 = new CalendarHour();
		hour1.setWeekDay(CalendarHour.WEEK_MONDAY);
		hour1.setPart1("0:00-24:00");
		defaultHours.add(hour1);
		
		CalendarHour hour2 = new CalendarHour();
		hour2.setWeekDay(CalendarHour.WEEK_TUESDAY);
		hour1.setPart1("0:00-24:00");
		defaultHours.add(hour2);
		
		CalendarHour hour3 = new CalendarHour();
		hour3.setWeekDay(CalendarHour.WEEK_WEDENSDAY);
		hour1.setPart1("0:00-24:00");
		defaultHours.add(hour3);
		
		CalendarHour hour4 = new CalendarHour();
		hour4.setWeekDay(CalendarHour.WEEK_THURDAY);
		hour1.setPart1("0:00-24:00");
		defaultHours.add(hour4);
		
		CalendarHour hour5 = new CalendarHour();
		hour5.setWeekDay(CalendarHour.WEEK_FRIDAY);
		hour1.setPart1("0:00-24:00");
		defaultHours.add(hour5);
		
		CalendarHour hour6 = new CalendarHour();
		hour6.setWeekDay(CalendarHour.WEEK_SATURDAY);
		hour1.setPart1("0:00-24:00");
		defaultHours.add(hour6);
		
		CalendarHour hour7 = new CalendarHour();
		hour7.setWeekDay(CalendarHour.WEEK_SUNDAY);
		hour1.setPart1("0:00-24:00");
		defaultHours.add(hour7);
		
		return defaultHours;
	}
	
	public long getHisSequence() {
		StringBuffer sql = new StringBuffer(" SELECT HIS_SEQ.NEXTVAL FROM DUAL ");
		Query query = em.createNativeQuery(sql.toString());

		return ((BigDecimal) query.getSingleResult()).longValue();
	}
	
	public String getYearCode(long orgRrn, String year) {
		try {
			if (CodeYear.yearMap == null) {
				List<CodeYear> yearList = adManager.getEntityList(orgRrn, CodeYear.class, Integer.MAX_VALUE, "", "");
				Map<String, String> yearMap = new HashMap<String, String>();
				for (CodeYear codeYear : yearList) {
					yearMap.put(codeYear.getYear(), codeYear.getYearCode());
				}
				CodeYear.yearMap = yearMap;
			}
			if (CodeYear.yearMap.containsKey(year)) {
				return (String)CodeYear.yearMap.get(year);
			}
		} catch (Exception e) {
			logger.error("getYearCode " + e.getMessage(), e);
		}
		return "_";
	}
	
	public String getMonthCode(long orgRrn, String month) {
		try {
			if (CodeMonth.monthMap == null) {
				List<CodeMonth> monthList = adManager.getEntityList(orgRrn, CodeMonth.class, Integer.MAX_VALUE, "", "");
				Map<String, String> monthMap = new HashMap<String, String>();
				for (CodeMonth codeMonth : monthList) {
					monthMap.put(codeMonth.getMonth(), codeMonth.getMonthCode());
				}
				CodeMonth.monthMap  = monthMap;
			}
			if (CodeMonth.monthMap.containsKey(month)) {
				return (String)CodeMonth.monthMap.get(month);
			}
		} catch (Exception e) {
			logger.error("getMonthCode " + e.getMessage(), e);
		}
		return "_";
	}
	
	public String getDayCode(long orgRrn, String day) {
		try {
			if (CodeDay.dayMap == null) {
				List<CodeDay> dayList = adManager.getEntityList(orgRrn, CodeDay.class, Integer.MAX_VALUE, "", "");
				Map<String, String> dayMap = new HashMap<String, String>();
				for (CodeDay codeDay : dayList) {
					dayMap.put(codeDay.getDay(), codeDay.getDayCode());
				}
				CodeDay.dayMap = dayMap;
			}
			if (CodeDay.dayMap.containsKey(day)) {
				return (String)CodeDay.dayMap.get(day);
			}
		} catch (Exception e) {
			logger.error("getDayCode " + e.getMessage(), e);
		}
		return "_";
	}
	
	public String getCurrentDateCode(long orgRrn) {
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		String yearSuffix = String.format("%02d", year - 2000);
		yearSuffix = getYearCode(orgRrn, yearSuffix);
		
		int month = now.get(Calendar.MONTH) + 1;
		String monthSuffix = String.format("%02d", month);
		monthSuffix = getMonthCode(orgRrn, monthSuffix);
		
		int day = now.get(Calendar.DAY_OF_MONTH);
		String daySuffix = String.format("%02d", day);
		daySuffix = getDayCode(orgRrn, daySuffix);
		
		return yearSuffix + monthSuffix + daySuffix;
	}
	
	public String getDocCode(long orgRrn, String docType) {
		try {
			if (CodeDoc.docMap == null) {
				List<CodeDoc> docList = adManager.getEntityList(orgRrn, CodeDoc.class, Integer.MAX_VALUE, "", "");
				Map<String, String> docMap = new HashMap<String, String>();
				for (CodeDoc codeDoc : docList) {
					docMap.put(codeDoc.getDoc(), codeDoc.getDocCode());
				}
				CodeDoc.docMap = docMap;
			}
			if (CodeDoc.docMap.containsKey(docType)) {
				return (String)CodeDoc.docMap.get(docType);
			}
		} catch (Exception e) {
			logger.error("getDocCode " + e.getMessage(), e);
		}
		return "_";
	}
	
	private String getDocCategory(long orgRrn, String docType) {
		try {
			if (CodeDoc.docCategoryMap == null) {
				List<CodeDoc> docList = adManager.getEntityList(orgRrn, CodeDoc.class, Integer.MAX_VALUE, "", "");
				Map<String, String> docMap = new HashMap<String, String>();
				for (CodeDoc codeDoc : docList) {
					docMap.put(codeDoc.getDoc(), codeDoc.getDocCategory());
				}
				CodeDoc.docCategoryMap = docMap;
			}
			if (CodeDoc.docCategoryMap.containsKey(docType)) {
				return (String)CodeDoc.docCategoryMap.get(docType);
			}
		} catch (Exception e) {
			logger.error("getDocCode " + e.getMessage(), e);
		}
		return "_";
	}
	
//	public String getOrgCode(String org) {
//		try {
//			if (CodeOrg.orgMap == null) {
//				List<CodeOrg> orgList = adManager.getEntityList(0, CodeOrg.class, Integer.MAX_VALUE, "", "");
//				Map<String, String> orgMap = new HashMap<String, String>();
//				for (CodeOrg codeOrg : orgList) {
//					orgMap.put(codeOrg.getOrg(), codeOrg.getOrgCode());
//				}
//				CodeOrg.orgMap = orgMap;
//			}
//			if (CodeOrg.orgMap.containsKey(org)) {
//				return CodeOrg.orgMap.get(org);
//			}
//		} catch (Exception e) {
//			logger.error("getDocCode " + e.getMessage(), e);
//		}
//		return "_";
//	}
	
	public String generateCodeSuffix(long orgRrn, String docType, Date docDate) {
		try {
			Calendar now = new GregorianCalendar();
			now.setTime(docDate);
			int year = now.get(Calendar.YEAR);
			String yearSuffix = String.format("%02d", year - 2000);
			yearSuffix = getYearCode(orgRrn, yearSuffix);
			int month = now.get(Calendar.MONTH) + 1;
			String monthSuffix = String.format("%02d", month);
			String docCategory = getDocCategory(orgRrn, docType);
			String seqSuffix = String.format("%04d", adManager.getNextSequence(orgRrn, docCategory, year, month, 0));
			return yearSuffix + monthSuffix + seqSuffix;
		} catch (Exception e) {
			logger.error("generateCodeSuffix " + e.getMessage(), e);
		}
		return "_";
	}
	
	public String generateCodePrefix(long orgRrn, String docType) {
		try {
			String docPrefix = getDocCode(orgRrn, docType);
			ADOrg org = em.find(ADOrg.class, orgRrn);
			return docPrefix + org.getCode();
		} catch (Exception e) {
			logger.error("generateCodePrefix " + e.getMessage(), e);
		}
		return "_";
	}

	public EntityManager getEntityManager() {
		return em;
	}
}

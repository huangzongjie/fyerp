package com.graly.erp.base.calendar;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.graly.erp.base.calendar.model.CalendarDay;
import com.graly.erp.base.calendar.model.CalendarHour;

/**
 * a calendar that knows about business hours.
 */
public class BusinessCalendar implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BusinessCalendar.class);
	
	public static final String CALENDAR_MANUFACTURE = "MANUFACTURE";
	public static final String CALENDAR_PURCHASE = "PURCHASE";
	
	public static final String WEEKTYPE_DEFAULT = "DEFAULT";
	
	private Day[] weekDays = null;
	private List<Holiday> holidays = null;

	public BusinessCalendar(List<CalendarDay> days, List<CalendarHour> hours)  {
		try {
			weekDays = Day.parseWeekDays(hours, this);
			holidays = Holiday.parseHolidays(days, this);
		} catch (Exception e) {
			logger.error("couldn't create business calendar ", e);
			throw new CalendarException(e);
		}
	}

	public Day[] getWeekDays() {
		if (weekDays == null)
			throw new CalendarException("couldn't create business calendar");
		return weekDays;
	}

	public List<Holiday> getHolidays() {
		if (holidays == null)
			throw new CalendarException("couldn't create business calendar");
		return holidays;
	}

	public Date add(Date date, Duration duration) {
		Date end = null;
		if (duration.isBusinessTime()) {
			DayPart dayPart = findDayPart(date);
			boolean isInbusinessHours = (dayPart != null);
			if (!isInbusinessHours) {
				Object[] result = new Object[2];
				findDay(date).findNextDayPartStart(0, date, result);
				date = (Date) result[0];
				dayPart = (DayPart) result[1];
			}
			end = dayPart.add(date, duration);
		} else {
			end = duration.addTo(date);
		}
		return end;
	}
	
	public Date addDay(Date date, int day) {
		Date addDate = date;
		for (int i = 0; i < day; i++) {
			addDate = findStartOfNextDay(addDate);
		}
		return addDate;
	}
	
	public Date findStartOfNextDay(Date date) {
		Calendar calendar = getCalendar();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		date = calendar.getTime();
		while (isHoliday(date)) {
			calendar.setTime(date);
			calendar.add(Calendar.DATE, 1);
			date = calendar.getTime();
		}
		return date;
	}

	public Day findDay(Date date) {
		Calendar calendar = getCalendar();
		calendar.setTime(date);
		return getWeekDays()[calendar.get(Calendar.DAY_OF_WEEK)];
	}

	public boolean isHoliday(Date date) {
		Iterator<Holiday> iter = getHolidays().iterator();
		while (iter.hasNext()) {
			Holiday holiday = (Holiday) iter.next();
			if (holiday.includes(date)) {
				return true;
			}
		}
		return false;
	}

	DayPart findDayPart(Date date) {
		DayPart dayPart = null;
		if (!isHoliday(date)) {
			Day day = findDay(date);
			for (int i = 0; ((i < day.dayParts.length) && (dayPart == null)); i++) {
				DayPart candidate = day.dayParts[i];
				if (candidate.includes(date)) {
					dayPart = candidate;
				}
			}
		}
		return dayPart;
	}

	public DayPart findNextDayPart(Date date) {
		DayPart nextDayPart = null;
		while (nextDayPart == null) {
			nextDayPart = findDayPart(date);
			if (nextDayPart == null) {
				date = findStartOfNextDay(date);
				Object result[] = new Object[2];
				Day day = findDay(date);
				day.findNextDayPartStart(0, date, result);
				nextDayPart = (DayPart) result[1];
			}
		}
		return nextDayPart;
	}

	public boolean isInBusinessHours(Date date) {
		return (findDayPart(date) != null);
	}

	public static Calendar getCalendar() {
		return new GregorianCalendar();
	}
}

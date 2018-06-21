package com.graly.erp.base.calendar;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.graly.erp.base.calendar.model.CalendarDay;


/**
 * identifies a continuous set of days.
 */
public class Holiday implements Serializable {

	private static final long serialVersionUID = 1L;

	Date fromDay = null;
	Date toDay = null;
	BusinessCalendar businessCalendar = null;

	public static List<Holiday> parseHolidays(List<CalendarDay> days, BusinessCalendar businessCalendar) {
		List<Holiday> holidays = new ArrayList<Holiday>();

		for (CalendarDay day : days) {
			if (day.getIsHoliday()) {
				Holiday holiday = new Holiday(day.getDay(), businessCalendar);
				holidays.add(holiday);
			}
		}

		return holidays;
	}

	public Holiday(Date date, BusinessCalendar businessCalendar) {
		this.businessCalendar = businessCalendar;
		try {
			fromDay = date;
			toDay = fromDay;
			// now we are going to set the toDay to the end of the day, rather then the beginning.
			// we take the start of the next day as the end of the toDay.
			Calendar calendar = BusinessCalendar.getCalendar();
			calendar.setTime(toDay);
			calendar.add(Calendar.DATE, 1);
			toDay = calendar.getTime();

		} catch (Exception e) {
			throw new CalendarException("couldn't parse holiday ", e);
		}
	}

	public boolean includes(Date date) {
		return ((fromDay.getTime() <= date.getTime()) && (date.getTime() < toDay
				.getTime()));
	}
}

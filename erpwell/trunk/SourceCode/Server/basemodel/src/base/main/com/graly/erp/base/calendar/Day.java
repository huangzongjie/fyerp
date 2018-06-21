package com.graly.erp.base.calendar;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.graly.erp.base.calendar.model.CalendarHour;

/**
 * is a day on a business calendar.
 */
public class Day implements Serializable {

	private static final long serialVersionUID = 1L;

	DayPart[] dayParts = null;
	BusinessCalendar businessCalendar = null;

	public static Day[] parseWeekDays(List<CalendarHour> hours, BusinessCalendar businessCalendar) {
		DateFormat dateFormat = new SimpleDateFormat(CalendarHour.HOUR_FORMAT);
		Day[] weekDays = new Day[8];
		weekDays[Calendar.MONDAY] = new Day(getCalendarHour(hours, CalendarHour.WEEK_MONDAY), dateFormat, businessCalendar);
		weekDays[Calendar.TUESDAY] = new Day(getCalendarHour(hours, CalendarHour.WEEK_TUESDAY), dateFormat, businessCalendar);
		weekDays[Calendar.WEDNESDAY] = new Day(getCalendarHour(hours, CalendarHour.WEEK_WEDENSDAY), dateFormat, businessCalendar);
		weekDays[Calendar.THURSDAY] = new Day(getCalendarHour(hours, CalendarHour.WEEK_THURDAY), dateFormat, businessCalendar);
		weekDays[Calendar.FRIDAY] = new Day(getCalendarHour(hours, CalendarHour.WEEK_FRIDAY), dateFormat, businessCalendar);
		weekDays[Calendar.SATURDAY] = new Day(getCalendarHour(hours, CalendarHour.WEEK_SATURDAY), dateFormat, businessCalendar);
		weekDays[Calendar.SUNDAY] = new Day(getCalendarHour(hours, CalendarHour.WEEK_SUNDAY), dateFormat, businessCalendar);
		return weekDays;
	}

	public Day(String[] dayPartsText, DateFormat dateFormat, BusinessCalendar businessCalendar) {
		this.businessCalendar = businessCalendar;

		List<DayPart> dayPartsList = new ArrayList<DayPart>();
		for (String dayPart : dayPartsText) {
			dayPartsList.add(new DayPart(dayPart, dateFormat, this, dayPartsList.size()));
		}

		dayParts = (DayPart[]) dayPartsList.toArray(new DayPart[dayPartsList.size()]);
	}

	public void findNextDayPartStart(int dayPartIndex, Date date,
			Object[] result) {
		// if there is a day part in this day that starts after the given date
		if (dayPartIndex < dayParts.length) {
			if (dayParts[dayPartIndex].isStartAfter(date)) {
				result[0] = dayParts[dayPartIndex].getStartTime(date);
				result[1] = dayParts[dayPartIndex];
			} else {
				findNextDayPartStart(dayPartIndex + 1, date, result);
			}
		} else {
			// descend recursively
			date = businessCalendar.findStartOfNextDay(date);
			Day nextDay = businessCalendar.findDay(date);
			nextDay.findNextDayPartStart(0, date, result);
		}
	}
	
	private static String[] getCalendarHour(List<CalendarHour> hours, String weekDay) {
		for (CalendarHour hour : hours) {
			if (weekDay.equals(hour.getWeekDay())) {
				List<String> partList = new ArrayList<String>();
				if (hour.getPart1() != null && hour.getPart1().length() > 0) {
					partList.add(hour.getPart1());
				}
				if (hour.getPart2() != null && hour.getPart2().length() > 0) {
					partList.add(hour.getPart2());
				}
				if (hour.getPart3() != null && hour.getPart3().length() > 0) {
					partList.add(hour.getPart3());
				}
				return partList.toArray(new String[partList.size()]);
			}
		}
		return new String[0];
	}
}

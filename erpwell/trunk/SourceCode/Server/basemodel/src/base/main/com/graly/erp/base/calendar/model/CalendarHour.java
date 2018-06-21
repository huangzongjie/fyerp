package com.graly.erp.base.calendar.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="BAS_CALENDAR_HOUR")
public class CalendarHour extends ADUpdatable {
	
	private static final long serialVersionUID = 1L;
	public static final String WEEK_MONDAY = "weekday.monday";
	public static final String WEEK_TUESDAY = "weekday.tuesday";
	public static final String WEEK_WEDENSDAY = "weekday.wednesday";
	public static final String WEEK_THURDAY = "weekday.thursday";
	public static final String WEEK_FRIDAY = "weekday.friday";
	public static final String WEEK_SATURDAY = "weekday.saturday";
	public static final String WEEK_SUNDAY = "weekday.sunday";
	
	public static final String HOUR_FORMAT = "HH:mm";
	
	@Column(name="WEEK_DAY")
	private String weekDay;
	
	@Column(name="WEEK_TYPE")
	private String weekType;
	
	@Column(name="PART1")
	private String part1;

	@Column(name="PART2")
	private String part2;
	
	@Column(name="PART3")
	private String part3;

	public void setWeekDay(String weekDay) {
		this.weekDay = weekDay;
	}

	public String getWeekDay() {
		return weekDay;
	}

	public void setPart1(String part1) {
		this.part1 = part1;
	}

	public String getPart1() {
		return part1;
	}

	public void setPart2(String part2) {
		this.part2 = part2;
	}

	public String getPart2() {
		return part2;
	}

	public void setPart3(String part3) {
		this.part3 = part3;
	}

	public String getPart3() {
		return part3;
	}
	
	public void setWeekType(String weekType) {
		this.weekType = weekType;
	}

	public String getWeekType() {
		return weekType;
	}
}

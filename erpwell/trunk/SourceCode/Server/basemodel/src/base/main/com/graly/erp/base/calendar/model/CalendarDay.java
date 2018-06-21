package com.graly.erp.base.calendar.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="BAS_CALENDAR_DAY")
public class CalendarDay extends ADUpdatable {
	
	private static final long serialVersionUID = 1L;
	public static final String HOUR_FORMAT = "yyyy/MM/dd";
	
	@Column(name="DAY")
	private Date day;

	@Column(name="IS_HOLIDAY")
	private String isHoliday;
	
	@Column(name="CALENDAR_TYPE")
	private String calendarType;
	
	@Transient
	private Long year;
	
	@Transient
	private Long month;

	public void setDay(Date day) {
		this.day = day;
	}

	public Date getDay() {
		return day;
	}
	
	public void setIsHoliday(Boolean isHoliday) {
		this.isHoliday = isHoliday ? "Y" : "N";
	}
	
	public Boolean getIsHoliday(){
		return "Y".equalsIgnoreCase(this.isHoliday) ? true : false; 
	}

	public Long getYear() {
		return year;
	}

	public void setYear(Long year) {
		this.year = year;
	}

	public Long getMonth() {
		return month;
	}

	public void setMonth(Long month) {
		this.month = month;
	}

	public void setCalendarType(String calendarType) {
		this.calendarType = calendarType;
	}

	public String getCalendarType() {
		return calendarType;
	}	
	
}

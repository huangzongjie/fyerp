package com.graly.erp.base.calendar.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="V_CALENDARHOUR_WEEKTYPE")
public class VCalendarHourWeekType extends ADBase {
	private static final long serialVersionUID = 1L;
	@Column(name="WEEK_TYPE")
	private String weekType;
	
	public String getWeekType() {
		return weekType;
	}

	public void setWeekType(String weekType) {
		this.weekType = weekType;
	}
	
}

package com.graly.erp.base.model;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="BAS_CODE_DAY")
public class CodeDay extends ADBase {
	
	private static final long serialVersionUID = 1L;
	public static Map<String, String> dayMap = null;
	
	@Column(name="DAY")
	private String day;
	
	@Column(name = "DAY_CODE")
	private String dayCode;

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getDayCode() {
		return dayCode;
	}

	public void setDayCode(String dayCode) {
		this.dayCode = dayCode;
	}


}

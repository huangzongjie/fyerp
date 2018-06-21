package com.graly.erp.base.model;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="BAS_CODE_MONTH")
public class CodeMonth extends ADBase {
	
	private static final long serialVersionUID = 1L;
	public static Map<String, String> monthMap = null;
	
	@Column(name="MONTH")
	private String month;
	
	@Column(name = "MONTH_CODE")
	private String monthCode;

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getMonthCode() {
		return monthCode;
	}

	public void setMonthCode(String monthCode) {
		this.monthCode = monthCode;
	}


}

package com.graly.erp.base.model;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name="BAS_CODE_YEAR")
public class CodeYear extends ADBase {
	
	private static final long serialVersionUID = 1L;
	public static Map<String, String> yearMap = null;
	
	@Column(name="YEAR")
	private String year;
	
	@Column(name = "YEAR_CODE")
	private String yearCode;

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getYearCode() {
		return yearCode;
	}

	public void setYearCode(String yearCode) {
		this.yearCode = yearCode;
	}


}

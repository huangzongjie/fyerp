package com.graly.promisone.activeentity.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "AD_SEQUENCE")
public class ADSequence extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="YEAR")
	private Long year;
	
	@Column(name="MONTH")
	private Long month;
	
	@Column(name="DAY")
	private Long day;
	
	@Column(name="NEXT_SEQ")
	private Long nextSeq;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setYear(Long year) {
		this.year = year;
	}

	public Long getYear() {
		return year;
	}

	public void setMonth(Long month) {
		this.month = month;
	}

	public Long getMonth() {
		return month;
	}

	public void setDay(Long day) {
		this.day = day;
	}

	public Long getDay() {
		return day;
	}

	public void setNextSeq(Long nextSeq) {
		this.nextSeq = nextSeq;
	}

	public Long getNextSeq() {
		return nextSeq;
	}

}

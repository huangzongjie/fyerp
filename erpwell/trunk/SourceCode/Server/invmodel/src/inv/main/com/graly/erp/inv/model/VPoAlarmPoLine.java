package com.graly.erp.inv.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="V_PO_ALARM_POLINE")
public class VPoAlarmPoLine implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="OBJECT_RRN")
	private Long objectRrn;
	
	@Column(name="CREATED_BY")
	protected Long createdBy;
	
	@Column(name="PURCHASER")
	protected String purchaser;

	public Long getObjectRrn() {
		return objectRrn;
	}
	public void setObjectRrn(Long objectRrn) {
		this.objectRrn = objectRrn;
	}
	public Long getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}
	public String getPurchaser() {
		return purchaser;
	}
	public void setPurchaser(String purchaser) {
		this.purchaser = purchaser;
	}
	
}

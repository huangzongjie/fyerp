package com.graly.erp.inv.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;
@Entity
@Table(name="INV_ERROR_LOG")
public class InvErrorLog extends ADBase {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="lot_id")
	private String lotId;
	
	@Column(name="salesorderid")
	private String salesOrderId;
	
	@Column(name="err_message")
	private String errorMessage;
	
	@Column(name="err_date")
	private Date errorDate;

	public String getLotId() {
		return lotId;
	}

	public void setLotId(String lotId) {
		this.lotId = lotId;
	}

	public String getSalesOrderId() {
		return salesOrderId;
	}

	public void setSalesOrderId(String salesOrderId) {
		this.salesOrderId = salesOrderId;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Date getErrorDate() {
		return errorDate;
	}

	public void setErrorDate(Date errorDate) {
		this.errorDate = errorDate;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}
	
	
	
}

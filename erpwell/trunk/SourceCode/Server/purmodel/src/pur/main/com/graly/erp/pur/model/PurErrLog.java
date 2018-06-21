package com.graly.erp.pur.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;

@Entity
@Table(name = "PUR_ERR_LOG")
public class PurErrLog extends ADBase {
	private static final long serialVersionUID = 1L;

	@Column(name="ERR_MESSAGE")
	private String errMessage;
	
	@Column(name="ERR_DATE")
	private Date errDate;

	public String getErrMessage() {
		return errMessage;
	}

	public void setErrMessage(String errMessage) {
		this.errMessage = errMessage;
	}

	public Date getErrDate() {
		return errDate;
	}

	public void setErrDate(Date errDate) {
		this.errDate = errDate;
	}
}

package com.graly.erp.ppm.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name = "PPM_MPS")
public class Mps extends ADUpdatable {

	private static final long serialVersionUID = 1L;
	@Column(name = "MPS_ID")
	private String mpsId;

	@Column(name = "DATE_START")
	private Date dateStart;
	
	@Column(name = "DATE_END")
	private Date dateEnd;
	
	@Column(name = "DATE_RESERVED")
	private Date dateReserved;
	
	@Column(name = "COMMENTS")
	private String comments;

	@Column(name = "IS_PROCESSING_MPS")
	private String isProcessingMps = "N";
	
	@Column(name = "IS_PROCESSING_PP")
	private String isProcessingPp = "N";
	
	@Transient
	private boolean isFrozen = false;
	
	public String getMpsId() {
		return mpsId;
	}

	public void setMpsId(String mpsId) {
		this.mpsId = mpsId;
	}

	public Date getDateStart() {
		return dateStart;
	}

	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
	}

	public Date getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}

	public Date getDateReserved() {
		return dateReserved;
	}

	public void setDateReserved(Date dateReserved) {
		this.dateReserved = dateReserved;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public void setFrozen(boolean isFrozen) {
		this.isFrozen = isFrozen;
	}

	public boolean isFrozen() {
		return isFrozen;
	}
	
	public Boolean getIsProcessingMps() {
		return "Y".equalsIgnoreCase(this.isProcessingMps) ? true : false; 
	}
	
	public void setIsProcessingMps(Boolean isProcessingMps) {
		this.isProcessingMps = isProcessingMps ? "Y" : "N";
	}
	
	public Boolean getIsProcessingPp() {
		return "Y".equalsIgnoreCase(this.isProcessingPp) ? true : false; 
	}
	
	public void setIsProcessingPp(Boolean isProcessingPp) {
		this.isProcessingPp = isProcessingPp ? "Y" : "N";
	}

}

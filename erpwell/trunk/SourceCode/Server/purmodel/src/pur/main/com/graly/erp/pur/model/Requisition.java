package com.graly.erp.pur.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.graly.erp.base.model.Documentation;

@Entity
@Table(name="PUR_REQUISITION")
public class Requisition extends Documentation {
	
	@Column(name="MPS_RRN")
	private Long mpsRrn;
	
	@Column(name="MPS_ID")
	private String mpsId;
	
	@Column(name="MO_RRN")
	private Long moRrn;
	
	@Column(name="MO_ID")
	private String moId;
	
	@Column(name="DESCRIPTION")
	private String description;

	@Column(name="DATE_REQUIRED")
	private Date dateRquired;
		
	@Column(name="TOTAL")
	private BigDecimal total = BigDecimal.ZERO;
	
	@Column(name="TOTAL_LINES")
	private Long totalLines = 0L;

	@Column(name="WAREHOUSE_RRN")
	private Long warehouseRrn;
	
	@Column(name="WAREHOUSE_ID")
	private String warehouseId;
	
	@Column(name="REQUISITION_USER_ID")
	private String requisitionUserId;
	
	@Column(name="USER_CREATED")
	private String userCreated;
	
	@Column(name="USER_APPROVED")
	private String userApproved;
	
	@Column(name="DATE_CREATED")
	private Date dateCreated;
	
	@Column(name="DATE_APPROVED")
	private Date dateApproved;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.REFRESH)
	@OrderBy(value = "lineNo ASC")
	@JoinColumn(name = "REQUISITION_RRN", referencedColumnName = "OBJECT_RRN", insertable = false, updatable = false)
	private List<RequisitionLine> prLines;
	
	public Long getMpsRrn() {
		return mpsRrn;
	}

	public void setMpsRrn(Long mpsRrn) {
		this.mpsRrn = mpsRrn;
	}

	public String getMpsId() {
		return mpsId;
	}

	public void setMpsId(String mpsId) {
		this.mpsId = mpsId;
	}


	public void setMoRrn(Long moRrn) {
		this.moRrn = moRrn;
	}

	public Long getMoRrn() {
		return moRrn;
	}

	public void setMoId(String moId) {
		this.moId = moId;
	}

	public String getMoId() {
		return moId;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getDateRquired() {
		return dateRquired;
	}

	public void setDateRquired(Date dateRquired) {
		this.dateRquired = dateRquired;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public Long getTotalLines() {
		return totalLines;
	}

	public void setTotalLines(Long totalLines) {
		this.totalLines = totalLines;
	}

	public Long getWarehouseRrn() {
		return warehouseRrn;
	}

	public void setWarehouseRrn(Long warehouseRrn) {
		this.warehouseRrn = warehouseRrn;
	}

	public String getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(String warehouseId) {
		this.warehouseId = warehouseId;
	}

	public String getRequisitionUserId() {
		return requisitionUserId;
	}

	public void setRequisitionUserId(String requisitionUserId) {
		this.requisitionUserId = requisitionUserId;
	}

	public String getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(String userCreated) {
		this.userCreated = userCreated;
	}

	public String getUserApproved() {
		return userApproved;
	}

	public void setUserApproved(String userApproved) {
		this.userApproved = userApproved;
	}


	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateApproved(Date dateApproved) {
		this.dateApproved = dateApproved;
	}

	public Date getDateApproved() {
		return dateApproved;
	}
	
	public void setPrLines(List<RequisitionLine> prLines) {
		this.prLines = prLines;
	}

	public List<RequisitionLine> getPrLines() {
		return prLines;
	}

	
}

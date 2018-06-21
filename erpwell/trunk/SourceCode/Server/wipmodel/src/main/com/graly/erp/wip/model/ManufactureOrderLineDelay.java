package com.graly.erp.wip.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="WIP_MO_LINE_DELAY")
public class ManufactureOrderLineDelay extends ADUpdatable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="MASTER_MO_RRN")
	private Long masterMoRrn;
	
	@Column(name="MASTER_MO_ID")
	private String masterMoId;
	
	
	@Column(name="MATERIAL_RRN")
	private Long materialRrn;
	
	@Column(name="MATERIAL_ID")
	private String materialId;
	
	@Column(name="MATERIAL_NAME")
	private String materialName;
	
	@Column(name="UOM_ID")
	private String uomId;
	
	@Column(name="QTY_RECEIVE")
	private BigDecimal qtyReceive = BigDecimal.ZERO;
	  
	@Column(name="DATE_END")
	private Date dateEnd;
	
	@Column(name="DATE_RECEIVE")
	private Date dateReceive;
	
	@Column(name="DATE_START_ACTUAL")
	private Date dateStatActual;
	
	@Column(name="DATE_END_ACTUAL")
	private Date dateEndActual;
	
	@Column(name="WORKCENTER_RRN")
	private Long workcenterRrn;
	
	@Column(name="WORKCENTER_ID")
	private String workcenterId;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="DELAY_REASON")
	private String delayReason;

	@Column(name="DELAY_REASON_DETAIL")
	private String delayReasonDetail;
	
	@Column(name="COMMENTS")
	private String comments;
	
	@Column(name="DELAY_DEPT")
	private String delayDept;

	
	public Long getMasterMoRrn() {
		return masterMoRrn;
	}

	public void setMasterMoRrn(Long masterMoRrn) {
		this.masterMoRrn = masterMoRrn;
	}

	public String getMasterMoId() {
		return masterMoId;
	}

	public void setMasterMoId(String masterMoId) {
		this.masterMoId = masterMoId;
	}

	public Long getMaterialRrn() {
		return materialRrn;
	}

	public void setMaterialRrn(Long materialRrn) {
		this.materialRrn = materialRrn;
	}

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getMaterialName() {
		return materialName;
	}

	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}

	public String getUomId() {
		return uomId;
	}

	public void setUomId(String uomId) {
		this.uomId = uomId;
	}

	public BigDecimal getQtyReceive() {
		return qtyReceive;
	}

	public void setQtyReceive(BigDecimal qtyReceive) {
		this.qtyReceive = qtyReceive;
	}

	public Date getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}

	public Date getDateReceive() {
		return dateReceive;
	}

	public void setDateReceive(Date dateReceive) {
		this.dateReceive = dateReceive;
	}

	public Date getDateStatActual() {
		return dateStatActual;
	}

	public void setDateStatActual(Date dateStatActual) {
		this.dateStatActual = dateStatActual;
	}

	public Date getDateEndActual() {
		return dateEndActual;
	}

	public void setDateEndActual(Date dateEndActual) {
		this.dateEndActual = dateEndActual;
	}

	public Long getWorkcenterRrn() {
		return workcenterRrn;
	}

	public void setWorkcenterRrn(Long workcenterRrn) {
		this.workcenterRrn = workcenterRrn;
	}

	public String getWorkcenterId() {
		return workcenterId;
	}

	public void setWorkcenterId(String workcenterId) {
		this.workcenterId = workcenterId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDelayReason() {
		return delayReason;
	}

	public void setDelayReason(String delayReason) {
		this.delayReason = delayReason;
	}

	public String getDelayReasonDetail() {
		return delayReasonDetail;
	}

	public void setDelayReasonDetail(String delayReasonDetail) {
		this.delayReasonDetail = delayReasonDetail;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getDelayDept() {
		return delayDept;
	}

	public void setDelayDept(String delayDept) {
		this.delayDept = delayDept;
	}
}

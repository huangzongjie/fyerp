package com.graly.mes.wiphis.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import com.graly.erp.wiphis.model.InvLotHis;
import com.graly.mes.wip.model.Lot;

@Entity
@Table(name="WIPHIS_LOT")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="TRANS_TYPE", discriminatorType = DiscriminatorType.STRING, length = 32)
public class LotHis extends InvLotHis {
	private static final long serialVersionUID = 1L;

	@Column(name="TRANS_TYPE", insertable = false, updatable = false)
	private String transType;
	
	@Column(name="MOLD_ID")
	private String moldId;
	
	@Column(name="LOT_RRN")
	private Long lotRrn;
	
	@Column(name="LOT_ID")
	private String lotId;
	
	@Column(name="DESCRIPTION")
	private String description;
	
	@Column(name="LOT_TYPE")
	private String lotType;
	
	@Column(name="PART_RRN")
	private Long partRrn;
	
	@Column(name="PART_NAME")
	private String partName;
	
	@Column(name="PART_VERSION")
	private Long partVersion;

	@Column(name="PART_TYPE")
	private String partType;
	
	@Column(name="LOCATION")
	private String location;
	
	@Column(name="ENGINEER")
	private Long engineer;

	@Column(name="CUSTOMER_NAME")
	private String customerName;
	
	@Column(name="CUSTOMER_ORDER")
	private String customerOrder;
	
	@Column(name="CUSTOMER_PART_ID")
	private String customerPartId;
	
	@Column(name="CUSTOMER_LOT_ID")
	private String customerLotId;
	
	@Column(name="PRIORITY")
	private Integer priority;
	
	@Column(name="REQUITED_DATE")
	private Date requiredDate;

	@Column(name="DUE_DATE")
	private Date dueDate;
	
	@Column(name="PLAN_START_DATE")
	private Date planStartDate;
	
	@Column(name="CREATE_TIME")
	private Date createTime;
	
	@Column(name="LOT_COMMENT")
	private String lotComment;

	@Column(name="START_MAIN_QTY")
	private Double startMainQty;
	
	@Column(name="START_SUB_QTY")
	private Double startSubQty;
	
	@Column(name="END_MAIN_QTY")
	private Double endMainQty;
	
	@Column(name="END_SUB_QTY")
	private Double endSubQty;
	
	@Column(name="START_TIME")
	private Date startTime;
	
	@Column(name="END_TIME")
	private Date endTime;

	@Column(name="TRACK_IN_TIME")
	private Date trackInTime;
	
	@Column(name="TRACK_OUT_TIME")
	private Date trackOutTime;

	@Column(name="PARENT_LOT_RRN")
	private Long parentLotRrn;

	@Column(name="COM_CLASS")
	private String comClass;

	@Column(name="STATE")
	private String state;
	
	@Column(name="SUB_STATE")
	private String subState;

	@Column(name="STATE_ENTRY_TIME")
	private Date stateEntryTime;
	
	@Column(name="PRE_TRANS_TYPE")
	private String preTransType;
	
	@Column(name="PRE_COM_CLASS")
	private String preComClass;
	
	@Column(name="PRE_STATE")
	private String preState;
	
	@Column(name="PRE_SUB_STATE")
	private String preSubState;
	
	@Column(name="PRE_STATE_ENTRY_TIME")
	private Date preStateEntryTime;
	
	@Column(name="PROCEDURE_RRN")
	private Long procedureRrn;
	
	@Column(name="PROCEDURE_NAME")
	private String procedureName;
	
	@Column(name="PROCEDURE_VERSION")
	private Long procedureVersion;
	
	@Column(name="STEP_Rrn")
	private Long stepRrn;
	
	@Column(name="STEP_NAME")
	private String stepName;
	
	@Column(name="STEP_VERSION")
	private Long stepVersion;
	
	@Column(name="ACTION_CODE")
	private String actionCode;
	
	@Column(name="ACTION_REASON")
	private String actionReason;
	
	@Column(name="ACTION_COMMENT")
	private String actionComment;

	
	public LotHis(){
	}
	
	public LotHis(Lot lot){
		this.setOrgRrn(lot.getOrgRrn());
		this.setIsActive(lot.getIsActive());
		this.setUpdatedBy(lot.getUpdatedBy());
		this.setHisSeq(lot.getCurSeq());
		this.setLotRrn(lot.getObjectRrn());
		this.setLotId(lot.getLotId());
		this.setDescription(lot.getDescription());
		this.setLotType(lot.getLotType());
		this.setPartRrn(lot.getPartRrn());
		this.setPartName(lot.getPartName());
		this.setPartVersion(lot.getPartVersion());
		this.setPartType(lot.getPartType());
		this.setMainQty(lot.getMainQty()); 
		this.setSubQty(lot.getSubQty());
		this.setLocation(lot.getLocation());
		this.setEngineer(lot.getEngineer());
		this.setCustomerName(lot.getCustomerName());
		this.setCustomerOrder(lot.getCustomerOrder());
		this.setCustomerPartId(lot.getCustomerPartId());
		this.setCustomerLotId(lot.getCustomerLotId());
		this.setPriority(lot.getPriority());
		this.setRequiredDate(lot.getRequiredDate());
		this.setDueDate(lot.getDueDate());
		this.setPlanStartDate(lot.getPlanStartDate());
		this.setCreateTime(lot.getCreateTime());
		this.setLotComment(lot.getLotComment());
		this.setStartMainQty(lot.getStartMainQty());
		this.setStartSubQty(lot.getStartSubQty());
		this.setEndMainQty(lot.getEndMainQty());
		this.setEndSubQty(lot.getEndSubQty());
		this.setStartTime(lot.getStartTime());
		this.setEndTime(lot.getEndTime());
		this.setTrackInTime(lot.getTrackInTime());
		this.setTrackOutTime(lot.getTrackOutTime());
		this.setEquipmentRrn(lot.getEquipmentRrn());
		this.setEquipmentId(lot.getEquipmentId());
		this.setParentLotRrn(lot.getParentLotRrn());
		this.setComClass(lot.getComClass());
		this.setState(lot.getState());
		this.setSubState(lot.getSubState());
		this.setStateEntryTime(lot.getStateEntryTime());
		this.setPreTransType(lot.getPreTransType());
		this.setPreComClass(lot.getComClass());
		this.setPreState(lot.getPreState());
		this.setPreSubState(lot.getPreSubState());
		this.setPreStateEntryTime(lot.getPreStateEntryTime());
		this.setProcedureRrn(lot.getProcedureRrn());
		this.setProcedureName(lot.getProcedureName());
		this.setProcedureVersion(lot.getProcedureVersion());
		this.setStepRrn(lot.getStepRrn());
		this.setStepName(lot.getStepName());
		this.setStepVersion(lot.getStepVersion());
		this.setOperatorRrn(lot.getOperatorRrn());
		this.setOperatorName(lot.getOperatorName());
		this.setParentUnitRrn(lot.getParentUnitRrn());
		this.setSubUnitType(lot.getSubUnitType());		
		
		this.setMaterialRrn(lot.getMaterialRrn());
		this.setMaterialId(lot.getMaterialId());
		this.setMaterialName(lot.getMaterialName());
		this.setWarehouseRrn(lot.getWarehouseRrn());
		this.setWarehouseId(lot.getWarehouseId());
		this.setLocatorRrn(lot.getLocatorRrn());
		this.setLocatorId(lot.getLocatorId());
		this.setUsedLotRrn(lot.getUsedLotRrn());
		this.setReceiptRrn(lot.getReceiptRrn());
		this.setReceiptId(lot.getReceiptId());
		this.setIqcRrn(lot.getIqcRrn());
		this.setIqcId(lot.getIqcId());
		this.setIqcLineRrn(lot.getIqcLineRrn());
		this.setPoRrn(lot.getPoRrn());
		this.setPoId(lot.getPoId());
		this.setPoLineRrn(lot.getPoLineRrn());
		this.setInRrn(lot.getInRrn());
		this.setInId(lot.getInId());
		this.setInLineRrn(lot.getInLineRrn());
		this.setOutRrn(lot.getOutRrn());
		this.setOutId(lot.getOutId());
		this.setOutLineRrn(lot.getOutLineRrn());
		this.setMoRrn(lot.getMoRrn());
		this.setMoId(lot.getMoId());
		this.setMoLineRrn(lot.getMoLineRrn());
		this.setQtyCurrent(lot.getQtyCurrent());
		this.setQtyInitial(lot.getQtyInitial());
		this.setIsUsed(lot.getIsUsed());
		this.setUserQc(lot.getUserQc());
		this.setDateIn(lot.getDateIn());
		this.setDateOut(lot.getDateOut());
		this.setDateProduct(lot.getDateProduct());
		this.setWorkCenterRrn(lot.getWorkCenterRrn());
		this.setWorkCenterId(lot.getWorkCenterId());
		this.setPosition(lot.getPosition());
		this.setQtyTransaction(lot.getQtyTransaction());
		this.setReverseField1(lot.getReverseField1());
		this.setReverseField2(lot.getReverseField2());
		this.setReverseField3(lot.getReverseField3());
		this.setReverseField4(lot.getReverseField4());
		this.setReverseField5(lot.getReverseField5());
		this.setReverseField6(lot.getReverseField6());
		this.setReverseField7(lot.getReverseField7());
		this.setReverseField8(lot.getReverseField8());
		this.setReverseField9(lot.getReverseField9());
		this.setReverseField10(lot.getReverseField10());
		
		this.setMoldId(lot.getMoldId());
	}

	public void setTransType(String transType) {
		this.transType = transType;
	}

	public String getTransType() {
		return transType;
	}

	public void setLotRrn(Long lotRrn) {
		this.lotRrn = lotRrn;
	}

	public Long getLotRrn() {
		return lotRrn;
	}

	public void setLotId(String lotId) {
		this.lotId = lotId;
	}

	public String getLotId() {
		return lotId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLotType() {
		return lotType;
	}

	public void setLotType(String lotType) {
		this.lotType = lotType;
	}

	public Long getPartRrn() {
		return partRrn;
	}

	public void setPartRrn(Long partRrn) {
		this.partRrn = partRrn;
	}

	public String getPartName() {
		return partName;
	}

	public void setPartName(String partName) {
		this.partName = partName;
	}

	public Long getPartVersion() {
		return partVersion;
	}

	public void setPartVersion(Long partVersion) {
		this.partVersion = partVersion;
	}

	public String getPartType() {
		return partType;
	}

	public void setPartType(String partType) {
		this.partType = partType;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Long getEngineer() {
		return engineer;
	}

	public void setEngineer(Long engineer) {
		this.engineer = engineer;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(String customerOrder) {
		this.customerOrder = customerOrder;
	}

	public String getCustomerPartId() {
		return customerPartId;
	}

	public void setCustomerPartId(String customerPartId) {
		this.customerPartId = customerPartId;
	}

	public String getCustomerLotId() {
		return customerLotId;
	}

	public void setCustomerLotId(String customerLotId) {
		this.customerLotId = customerLotId;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Date getRequiredDate() {
		return requiredDate;
	}

	public void setRequiredDate(Date requiredDate) {
		this.requiredDate = requiredDate;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Date getPlanStartDate() {
		return planStartDate;
	}

	public void setPlanStartDate(Date planStartDate) {
		this.planStartDate = planStartDate;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getLotComment() {
		return lotComment;
	}

	public void setLotComment(String lotComment) {
		this.lotComment = lotComment;
	}

	public Double getStartMainQty() {
		return startMainQty;
	}

	public void setStartMainQty(Double startMainQty) {
		this.startMainQty = startMainQty;
	}

	public Double getStartSubQty() {
		return startSubQty;
	}

	public void setStartSubQty(Double startSubQty) {
		this.startSubQty = startSubQty;
	}

	public Double getEndMainQty() {
		return endMainQty;
	}

	public void setEndMainQty(Double endMainQty) {
		this.endMainQty = endMainQty;
	}

	public Double getEndSubQty() {
		return endSubQty;
	}

	public void setEndSubQty(Double endSubQty) {
		this.endSubQty = endSubQty;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getTrackInTime() {
		return trackInTime;
	}

	public void setTrackInTime(Date trackInTime) {
		this.trackInTime = trackInTime;
	}

	public Date getTrackOutTime() {
		return trackOutTime;
	}

	public void setTrackOutTime(Date trackOutTime) {
		this.trackOutTime = trackOutTime;
	}

	public Long getParentLotRrn() {
		return parentLotRrn;
	}

	public void setParentLotRrn(Long parentLotRrn) {
		this.parentLotRrn = parentLotRrn;
	}

	public String getComClass() {
		return comClass;
	}

	public void setComClass(String comClass) {
		this.comClass = comClass;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getSubState() {
		return subState;
	}

	public void setSubState(String subState) {
		this.subState = subState;
	}

	public Date getStateEntryTime() {
		return stateEntryTime;
	}

	public void setStateEntryTime(Date stateEntryTime) {
		this.stateEntryTime = stateEntryTime;
	}

	public void setPreTransType(String preTransType) {
		this.preTransType = preTransType;
	}

	public String getPreTransType() {
		return preTransType;
	}

	public String getPreComClass() {
		return preComClass;
	}

	public void setPreComClass(String preComClass) {
		this.preComClass = preComClass;
	}

	public String getPreState() {
		return preState;
	}

	public void setPreState(String preState) {
		this.preState = preState;
	}

	public String getPreSubState() {
		return preSubState;
	}

	public void setPreSubState(String preSubState) {
		this.preSubState = preSubState;
	}

	public Date getPreStateEntryTime() {
		return preStateEntryTime;
	}

	public void setPreStateEntryTime(Date preStateEntryTime) {
		this.preStateEntryTime = preStateEntryTime;
	}

	public Long getProcedureRrn() {
		return procedureRrn;
	}

	public void setProcedureRrn(Long procedureRrn) {
		this.procedureRrn = procedureRrn;
	}

	public String getProcedureName() {
		return procedureName;
	}

	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

	public Long getProcedureVersion() {
		return procedureVersion;
	}

	public void setProcedureVersion(Long procedureVersion) {
		this.procedureVersion = procedureVersion;
	}

	public Long getStepRrn() {
		return stepRrn;
	}

	public void setStepRrn(Long stepRrn) {
		this.stepRrn = stepRrn;
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public Long getStepVersion() {
		return stepVersion;
	}

	public void setStepVersion(Long stepVersion) {
		this.stepVersion = stepVersion;
	}

	public String getActionCode() {
		return actionCode;
	}

	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}

	public String getActionReason() {
		return actionReason;
	}

	public void setActionReason(String actionReason) {
		this.actionReason = actionReason;
	}

	public String getActionComment() {
		return actionComment;
	}

	public void setActionComment(String actionComment) {
		this.actionComment = actionComment;
	}

	public String getStepFullName() {
		return stepName == null ? "" : stepName + "." + (stepVersion == null ? "" : stepVersion);
	}
	
	public String getPartFullName() {
		return partName == null ? "" : partName + "." + (partVersion == null ? "" : partVersion);
	}
	
	public String getProcedureFullName() {
		return procedureName == null ? "" : procedureName + "." + (procedureVersion == null ? "" : procedureVersion);
	}

	public String getMoldId() {
		return moldId;
	}

	public void setMoldId(String moldId) {
		this.moldId = moldId;
	}
}

package com.graly.mes.wip.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.erp.wip.model.ManufactureOrderLineLot;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.core.statemachine.State;

@Entity
@Table(name="WIP_LOT")
public class Lot extends InvLot{
	private static final long serialVersionUID = 1L;
	
	public static String UNIT_TYPE = "Lot";
	public static String ID_PATTERN = "[A-Z]\\w{1,10}(.\\d{2})?";
	
	@Transient
	public transient LotStateMachine stateMachine;
	
	@Column(name="MOLD_ID")
	private String moldId;
	
	@Column(name="MOLD_RRN")
	private Long moldRrn;
	
	@Column(name="LOT_ID")
	private String lotId;
	
	@Column(name="CURRENT_SEQ")
	private Long curSeq;
	
	@Column(name="PROCESS_INSTANCE_RRN")
	private Long processInstanceRrn;
	
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

//	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
//	@OrderBy(value = "seqNo ASC")
//	@JoinColumn(name = "LOT_RRN", referencedColumnName = "OBJECT_RRN")
	@Transient
	private List<LotParameter> lotParameters;
	
	@Transient
	private Lot parentLot;
	
	@Transient
	private List<Lot> childrenLots;
	
	//为了解决MATERIAL类型的批次能追踪到工作令，引入了该属性
	@Transient
	private ManufactureOrderLineLot moLineLot;
	
	@Transient
	private ManufactureOrderLine moLine;
	
	@Transient
	private String soId;
	
    @Transient
    private String delayReason;//接收日期超过交货日期3天（不包含3天）的商品
    
    @Transient
    private String delayReasonDetail;//延误原因内容
    
    @Transient
    private Long mpsLineDeliveryRrn;//主计划交期rrn
    
    @Transient
    private String delayDept;//部门

	public String getSoId() {
		return soId;
	}

	public void setSoId(String soId) {
		this.soId = soId;
	}

	public String getLotId() {
		if(moLineLot != null){
			return moLineLot.getLotId();
		}
		return lotId;
	}

	public void setLotId(String lotId) {
		this.lotId = lotId;
	}

	public Long getCurSeq() {
		return curSeq;
	}

	public void setCurSeq(Long curSeq) {
		this.curSeq = curSeq;
	}

	public Long getProcessInstanceRrn() {
		return processInstanceRrn;
	}

	public void setProcessInstanceRrn(Long processInstanceId) {
		this.processInstanceRrn = processInstanceId;
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

	public String getPartId() {
		return partName == null ? "" : partName + "." + partVersion;
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
	
	public void setParentLot(Lot parentLot) {
		this.parentLot = parentLot;
	}

	public Lot getParentLot() {
		return parentLot;
	}
	
	public void setChildrenLots(List<Lot> childrenLots) {
		this.childrenLots = childrenLots;
	}

	public List<Lot> getChildrenLots() {
		return childrenLots;
	}
	
	public void setLotParameters(List<LotParameter> lotParameters) {
		this.lotParameters = lotParameters;
	}
	
	public List<LotParameter> getLotParameters() {
		return lotParameters;
	}
	
	public LotStateMachine getStateMachine() {
		return stateMachine;
	}
	
	public void setStateMachine(LotStateMachine stateMachine) {
		this.stateMachine = stateMachine;
	}

	public void initStateMachine() {
		stateMachine = new LotStateMachine();
		stateMachine.initialize(subState);
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
	
	public void stateTrans(String trans) throws ClientException {
		if (stateMachine == null) {
			initStateMachine();
		}
		
		State subState = stateMachine.send(trans);
		if (subState == null) {
			throw new ClientException("setStateInfo error : subState is null! Lot = " + getLotId());
		}
		State state = subState.getSuperState();
		if (state == null) {
			throw new ClientException("setStateInfo error : state is null! Lot = " + getLotId());
		}
		State comClass = state.getSuperState();
		if (comClass == null) {
			throw new ClientException("setStateInfo error : comClass is null! Lot = " + getLotId());
		}
		setPreComClass(getComClass());
		setPreState(getState());
		setPreSubState(getSubState());
		setPreStateEntryTime(getUpdated());
		setComClass(comClass.getStateId());
		setState(state.getStateId());
		setSubState(subState.getStateId());
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public String getMoldId() {
		return moldId;
	}

	public void setMoldId(String moldId) {
		this.moldId = moldId;
	}

	public Long getMoldRrn() {
		return moldRrn;
	}

	public void setMoldRrn(Long moldRrn) {
		this.moldRrn = moldRrn;
	}

	public ManufactureOrderLineLot getMoLineLot() {
		return moLineLot;
	}

	public void setMoLineLot(ManufactureOrderLineLot moLineLot) {
		this.moLineLot = moLineLot;
	}
	
	@Override
	public BigDecimal getQtyCurrent() {
		if(moLineLot != null && moLineLot.getQtyCurrent() != null){
			return moLineLot.getQtyCurrent();
		}
		return super.getQtyCurrent();
	}

	public ManufactureOrderLine getMoLine() {
		return moLine;
	}

	public void setMoLine(ManufactureOrderLine moLine) {
		this.moLine = moLine;
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

	public Long getMpsLineDeliveryRrn() {
		return mpsLineDeliveryRrn;
	}

	public void setMpsLineDeliveryRrn(Long mpsLineDeliveryRrn) {
		this.mpsLineDeliveryRrn = mpsLineDeliveryRrn;
	}

	public String getDelayDept() {
		return delayDept;
	}

	public void setDelayDept(String delayDept) {
		this.delayDept = delayDept;
	}
	
	
}

package com.graly.mes.prd.workflow.graph.def;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import com.graly.mes.prd.model.Operation;

@Entity
@DiscriminatorValue("S")
public class Step extends ProcessDefinition {	
	
	private static final long serialVersionUID = 1L;

	@Column(name="CAPABILITY")
	private Long capability;
	
	@Column(name="BASE_LEAD_TIME")
	private Long baseLeadTime;
	
	@Column(name="STAGE_RRN")
	private Long stageRrn;
	
	@Column(name="REWORK_FLOW_RRN")
	private Long reworkFlowRrn;
	
	@Column(name="IS_MULTI_EQP")
	private String isMultiEqp;
	
	@Column(name="IS_REQUIRE_EQP")
	private String isRequireEqp;
	
	@Column(name="HOLD_CODE_SRC")
	private String holdCodeSrc;
	
	@Column(name="RELEASE_CODE_SRC")
	private String releaseCodeSrc;
	
	@Column(name="SCRAP_CODE_SRC")
	private String scrapCodeSrc;
	
	@Column(name="BONUS_CODE_SRC")
	private String bonusCodeSrc;
	
	@Column(name="REWORK_CODE_SRC")
	private String reworkCodeSrc;
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@OrderBy(value = "seqNo ASC")
	@JoinColumn(name = "STEP_RRN", referencedColumnName = "OBJECT_RRN")
	private List<Operation> operations;

	
	public void setCapability(Long capability) {
		this.capability = capability;
	}

	public Long getCapability() {
		return capability;
	}
	
	public void setBaseLeadTime(Long baseLeadTime) {
		this.baseLeadTime = baseLeadTime;
	}

	public Long getBaseLeadTime() {
		return baseLeadTime;
	}
	
	public void setStageRrn(Long stageRrn) {
		this.stageRrn = stageRrn;
	}

	public Long getStageRrn() {
		return stageRrn;
	}
	
	public void setReworkFlowRrn(Long reworkFlowRrn) {
		this.reworkFlowRrn = reworkFlowRrn;
	}

	public Long getReworkFlowRrn() {
		return reworkFlowRrn;
	}

	public void setIsMultiEqp(Boolean isMultiEqp) {
		this.isMultiEqp = isMultiEqp ? "Y" : "N";
	}
	
	public Boolean getIsMultiEqp(){
		return "Y".equalsIgnoreCase(this.isMultiEqp) ? true : false; 
	}
	
	public void setIsRequireEqp(Boolean isRequireEqp) {
		this.isRequireEqp = isRequireEqp ? "Y" : "N";
	}
	
	public Boolean getIsRequireEqp(){
		return "Y".equalsIgnoreCase(this.isRequireEqp) ? true : false; 
	}
	
	public void setHoldCodeSrc(String holdCodeSrc) {
		this.holdCodeSrc = holdCodeSrc;
	}

	public String getHoldCodeSrc() {
		return holdCodeSrc;
	}
	
	public void setReleaseCodeSrc(String releaseCodeSrc) {
		this.releaseCodeSrc = releaseCodeSrc;
	}
	
	public String getReleaseCodeSrc() {
		return releaseCodeSrc;
	}	

	public void setScrapCodeSrc(String scrapCodeSrc) {
		this.scrapCodeSrc = scrapCodeSrc;
	}

	public String getScrapCodeSrc() {
		return scrapCodeSrc;
	}
	
	public void setBonusCodeSrc(String bonusCodeSrc) {
		this.bonusCodeSrc = bonusCodeSrc;
	}

	public String getBonusCodeSrc() {
		return bonusCodeSrc;
	}

	public void setReworkCodeSrc(String reworkCodeSrc) {
		this.reworkCodeSrc = reworkCodeSrc;
	}

	public String getReworkCodeSrc() {
		return reworkCodeSrc;
	}
	
	public String initStepDocument(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append("<process-definition name=\"" + getName() + "\" xmlns=\"urn:jbpm.org:jpdl-3.2\">"); 
		buffer.append("<start-state name=\"START\">");
		buffer.append("<transition name=\"\" to=\"QUEUE\"/>");
		buffer.append("</start-state>");
		buffer.append("<queue-state name='QUEUE'>");
		buffer.append("<transition to=\"RUN\"></transition>");
		buffer.append("</queue-state>");
		buffer.append("<run-state name='RUN'>");
		buffer.append("<transition to=\"END\"></transition>");
		buffer.append("</run-state>");
		buffer.append("<end-state name='END'/>");
		buffer.append("</process-definition>");	
		return buffer.toString();
	}

	public void setOperations(List<Operation> operations) {
		this.operations = operations;
	}

	public List<Operation> getOperations() {
		return operations;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		Step step = (Step)super.clone();
		
		List<Operation> operations = new ArrayList<Operation>();
		if (this.getOperations() != null) {
			for (Operation operation : this.getOperations()) {
				operations.add((Operation)operation.clone());
			}
		}
		step.setOperations(operations);
		
		return step;
	}
}
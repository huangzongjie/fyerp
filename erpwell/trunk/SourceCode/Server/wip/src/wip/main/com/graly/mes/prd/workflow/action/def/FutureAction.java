package com.graly.mes.prd.workflow.action.def;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADUpdatable;

@Entity
@Table(name="WIP_FUTURE_ACTION")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CLASS", discriminatorType = DiscriminatorType.STRING, length = 1)
public abstract class FutureAction extends ADUpdatable {
	
	public static final String ACTIONTYPE_QUEUE = "QUEUE";
	public static final String ACTIONTYPE_TRACKOUT = "TRACKOUT";

	@Column(name="NAME")
	private String name;
	
	@Column(name="DESCRIPTION")
	protected String description;

	@Column(name="CONDITION")
	protected String condition;
	
	@Column(name = "ACTION_TYPE")
	private String actionType = null;
	
	@Column(name="STEP_STATE_RRN")
	private Long stepStateRrn;
	
	@Column(name="LOT_RRN")
	private Long lotRrn;
	
	@Column(name="SEQ_NO")
	private Long seqNo;
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public Long getStepStateRrn() {
		return stepStateRrn;
	}

	public void setStepStateRrn(Long stepStateRrn) {
		this.stepStateRrn = stepStateRrn;
	}

	public Long getLotRrn() {
		return lotRrn;
	}

	public void setLotRrn(Long lotRrn) {
		this.lotRrn = lotRrn;
	}

	public Long getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(Long seqNo) {
		this.seqNo = seqNo;
	}
		
}

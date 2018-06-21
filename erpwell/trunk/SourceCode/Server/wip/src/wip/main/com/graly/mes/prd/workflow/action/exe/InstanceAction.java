package com.graly.mes.prd.workflow.action.exe;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.apache.log4j.Logger;

import com.graly.framework.activeentity.model.ADBase;
import com.graly.mes.prd.workflow.graph.exe.ExecutionContext;
import com.graly.mes.prd.workflow.graph.exe.Token;

@Entity
@Table(name="WF_INSTANCE_ACTION")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CLASS", discriminatorType = DiscriminatorType.STRING, length = 1)
public class InstanceAction extends ADBase {
	
	static final long serialVersionUID = 1L;
	static final Logger logger = Logger.getLogger(InstanceAction.class);
	
	public static final String ACTIONTYPE_QUEUE = "QUEUE";
	public static final String ACTIONTYPE_TRACKOUT = "TRACKOUT";

	@Column(name="INSTANCE_KEY")
	private Long instanceKey;
	
	@Column(name="SEQ_NO")
	private Integer seqNo;
	
	public void enter(ExecutionContext executionContext) {
		Token token = executionContext.getToken();
		InstanceToken instanceToken = token.getInstanceToken();
		instanceToken.setInstanceAction(this);
		
		execute(executionContext);
	}
	
	public void execute(ExecutionContext executionContext) {
		leave(executionContext);
	}
	
	public void leave(ExecutionContext executionContext) {
		Token token = executionContext.getToken();
		InstanceToken instanceToken = token.getInstanceToken();
		
		List<InstanceAction> instanceActions = instanceToken.getInstanceActions();
		if (instanceActions == null || instanceActions.size() == 0) {
			instanceToken.setIsEnd(true);
			return;
		}
		if (instanceActions.indexOf(this) != -1) {
			int index = instanceActions.indexOf(this);
			if (index < instanceActions.size() - 1) {
				InstanceAction to = instanceActions.get(index + 1);
				to.enter(executionContext);
			} else {
				instanceToken.setIsEnd(true);
			}
		} else {
			instanceToken.setIsEnd(true);
		}
	}

	public void setInstanceKey(Long instanceKey) {
		this.instanceKey = instanceKey;
	}

	public Long getInstanceKey() {
		return instanceKey;
	}
	
	public void setSeqNo(Integer seqNo) {
		this.seqNo = seqNo;
	}

	public Integer getSeqNo() {
		return seqNo;
	}


}

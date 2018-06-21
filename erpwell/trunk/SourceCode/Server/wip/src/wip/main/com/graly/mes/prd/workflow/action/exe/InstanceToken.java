package com.graly.mes.prd.workflow.action.exe;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.log4j.Logger;

import com.graly.framework.activeentity.model.ADBase;
import com.graly.mes.prd.workflow.graph.exe.ExecutionContext;
import com.graly.mes.prd.workflow.graph.exe.Token;
import com.graly.mes.prd.workflow.graph.node.InstanceActionResolver;

@Entity
@Table(name="WF_INSTANCE_TOKEN")
public class InstanceToken extends ADBase {
	
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(InstanceToken.class);

	@Transient
	private InstanceActionResolver resolver = new InstanceActionResolver();

	@Column(name="NAME")
	protected String name;
	
	@Column(name="INSTANCE_KEY")
	private Long instanceKey = null;
	
	@ManyToOne
	@JoinColumn(name = "INSTANCE_ACTION_RRN", referencedColumnName = "OBJECT_RRN")
	private InstanceAction instanceAction = null;
	
	@ManyToOne
	@JoinColumn(name = "TOKEN_RRN", referencedColumnName = "OBJECT_RRN")
	private Token token = null;
	
	@Column(name="IS_END")
	protected String isEnd = "N";
	
	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	@OrderBy(value = "seqNo ASC")
	@JoinColumn(name = "INSTANCE_KEY", referencedColumnName = "INSTANCE_KEY")
	private List<InstanceAction> instanceActions;
	
	public InstanceToken() {
	}
	
	public InstanceToken(Token token) {
		this.setInstanceKey(token.getProcessInstance().getInstanceKey());
		this.token = token;
		
		this.token.setInstanceToken(this);
	}
	
	public void init(ExecutionContext executionContext) {
		if (getInstanceActions() == null || getInstanceActions().size() == 0) {
			this.setIsEnd(true);
		} else {
			this.setIsEnd(false);
		}
	}
	
	public void signal(ExecutionContext executionContext) {
		try {
			if (!this.getIsEnd()) {
				if (this.getInstanceAction() == null) {
					this.setInstanceAction(getInstanceActions().get(0));
					this.getInstanceAction().enter(executionContext);
				} else {
					this.getInstanceAction().leave(executionContext);
				}
			}
		} finally {
		}
	}
	
	public void setInstanceAction(InstanceAction instanceAction) {
		this.instanceAction = instanceAction;
	}

	public InstanceAction getInstanceAction() {
		return instanceAction;
	}
	
	public void setIsEnd(Boolean isEnd) {
		this.isEnd = isEnd ?  "Y" : "N";
	}

	public Boolean getIsEnd() {
		return "Y".equalsIgnoreCase(this.isEnd) ? true : false; 
	}

	public void setInstanceActions(List<InstanceAction> instanceActions) {
		this.instanceActions = instanceActions;
	}

	public List<InstanceAction> getInstanceActions() {
		return instanceActions;
	}

	public void setInstanceKey(Long instanceKey) {
		this.instanceKey = instanceKey;
	}

	public Long getInstanceKey() {
		return instanceKey;
	}
}

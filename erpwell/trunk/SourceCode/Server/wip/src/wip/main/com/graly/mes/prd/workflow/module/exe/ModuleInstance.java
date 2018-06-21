package com.graly.mes.prd.workflow.module.exe;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.graly.framework.activeentity.model.ADBase;
import com.graly.mes.prd.workflow.graph.exe.ProcessInstance;

@Entity
@Table(name="WF_MODULEINSTANCE")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CLASS", discriminatorType = DiscriminatorType.STRING, length = 1)
@DiscriminatorValue("M")
public class ModuleInstance extends ADBase {

	private static final long serialVersionUID = 1L;
	
	@Column(name="NAME")
	private String name = null;
	
	@ManyToOne(cascade=CascadeType.REFRESH)
	@JoinColumn(name = "PROCESS_INSTANCE_RRN", referencedColumnName = "OBJECT_RRN")
	protected ProcessInstance processInstance = null;

	public ModuleInstance() {
	}

	// getters and setters //////////////////////////////////////////////////////
	public ProcessInstance getProcessInstance() {
		return processInstance;
	}

	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}

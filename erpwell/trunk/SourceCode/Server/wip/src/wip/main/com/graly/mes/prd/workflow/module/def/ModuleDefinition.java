package com.graly.mes.prd.workflow.module.def;

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
import com.graly.mes.prd.workflow.graph.def.ProcessDefinition;
import com.graly.mes.prd.workflow.module.exe.ModuleInstance;

@Entity
@Table(name="WF_MODULEDEFINITION")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="CLASS", discriminatorType = DiscriminatorType.STRING, length = 1)
@DiscriminatorValue("M")
public class ModuleDefinition extends ADBase {

	@Column(name="NAME")
	private String name = null;
	
	@ManyToOne
	@JoinColumn(name = "PROCESS_DEFINITION_RRN", referencedColumnName = "OBJECT_RRN")
	protected ProcessDefinition processDefinition = null;

	public ModuleDefinition() {
	}

	public ModuleInstance createInstance() {
		return null;
	}
	
//	public boolean equals(Object o) {
//		return EqualsUtil.equals(this, o);
//	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}
	
}

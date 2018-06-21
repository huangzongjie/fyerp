package com.graly.mes.prd.workflow.graph.node;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.graly.mes.prd.workflow.graph.def.Node;
import com.graly.mes.prd.workflow.graph.exe.ExecutionContext;

@Entity
@DiscriminatorValue("A")
public class State extends Node {
	private static final long serialVersionUID = 1L;

	public State() {
		this(null);
	}

	public State(String name) {
		super(name);
	}

	public void execute(ExecutionContext executionContext) {
	}
}

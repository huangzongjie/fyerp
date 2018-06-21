package com.graly.mes.prd.workflow.graph.node;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.graly.mes.prd.workflow.graph.def.Node;
import com.graly.mes.prd.workflow.graph.exe.ExecutionContext;

@Entity
@DiscriminatorValue("Q")
public class QueueState extends Node {
	private static final long serialVersionUID = 1L;

	public QueueState() {
		this(null);
	}

	public QueueState(String name) {
		super(name);
	}

	public void execute(ExecutionContext executionContext) {
	}
}

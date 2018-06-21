package com.graly.mes.prd.designer.common.command;



public class BendpointCreateCommand extends AbstractBendpointCommand {
	
	public void execute() {
		edge.addBendPoint(index, bendpoint);
	}
	
	public void undo() {
		edge.removeBendPoint(index);
	}
	
}

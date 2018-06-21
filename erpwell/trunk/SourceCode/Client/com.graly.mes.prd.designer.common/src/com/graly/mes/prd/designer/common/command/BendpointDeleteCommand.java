package com.graly.mes.prd.designer.common.command;



public class BendpointDeleteCommand extends AbstractBendpointCommand {
	
	public void execute() {
		edge.removeBendPoint(index);
	}
	
	public void undo() {
		edge.addBendPoint(index, bendpoint);
	}
	
}

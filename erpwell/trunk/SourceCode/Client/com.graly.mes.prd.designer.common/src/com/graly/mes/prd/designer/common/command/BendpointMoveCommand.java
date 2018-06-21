package com.graly.mes.prd.designer.common.command;

import com.graly.mes.prd.designer.common.notation.BendPoint;


public class BendpointMoveCommand extends AbstractBendpointCommand {
	
	private BendPoint oldBendpoint;
	
	public void execute() {
		oldBendpoint = (BendPoint)edge.getBendPoints().get(index);
		edge.setBendPoint(index, bendpoint);
	}
	
	public void undo() {
		edge.setBendPoint(index, oldBendpoint);
	}
	
}

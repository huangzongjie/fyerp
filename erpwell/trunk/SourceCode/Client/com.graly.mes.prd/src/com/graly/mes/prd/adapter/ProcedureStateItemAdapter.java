package com.graly.mes.prd.adapter;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;

import com.graly.mes.prd.client.PrdManager;
import com.graly.mes.prd.workflow.graph.def.Procedure;
import com.graly.mes.prd.workflow.graph.node.ProcedureState;
import com.graly.framework.base.application.Activator;
import com.graly.framework.runtime.Framework;

public class ProcedureStateItemAdapter extends AbstractFlowItemAdapter {
	
	private static final Logger logger = Logger.getLogger(ProcedureStateItemAdapter.class);
	private static final Object[] EMPTY = new Object[0];

	@Override
	public Object[] getChildren(Object object) {
		if (object instanceof ProcedureState) {
			ProcedureState procedureState = (ProcedureState)object;
			Procedure procedure = getProcedure(procedureState);
			return super.getChildren(procedure);
		}
		return EMPTY;
	}
	
	public boolean hasChildren(Object object) {
		return true;
	}
	
	@Override
	public String getText(Object element) {
		if (element instanceof ProcedureState) {
			ProcedureState procedureState = (ProcedureState)element;
			Procedure procedure = getProcedure(procedureState);
			return super.getText(procedure);
		}
		return "";
	}
	
	@Override
	public ImageDescriptor getImageDescriptor(Object object, String id) {
		if (object instanceof ProcedureState){
			return Activator.getImageDescriptor("procedure");
		}
		return null;
	}
	
	protected Procedure getProcedure(ProcedureState procedureState) {
		Procedure procedure = null;
		try {
			procedure = procedureState.getUsedProcedure();
			if (procedure == null) {
				procedure = new Procedure();
				procedure.setName(procedureState.getProcedureName());
				procedure.setOrgRrn(procedureState.getOrgRrn());
				PrdManager prdManager = Framework.getService(PrdManager.class);
				procedure = (Procedure)prdManager.getActiveProcessDefinition(procedure);
			}
		} catch (Exception e) {
        	logger.error(e.getMessage(), e);
        }
		return procedure;
	}

}

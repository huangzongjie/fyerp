package com.graly.mes.prd.viewer;

import org.apache.log4j.Logger;

import com.graly.mes.prd.workflow.graph.def.Procedure;
import com.graly.mes.prd.workflow.graph.node.ProcedureState;
import com.graly.mes.prd.workflow.graph.node.StepState;
import com.graly.framework.base.ui.views.ItemAdapterFactory;
import com.graly.framework.base.ui.views.TreeViewerManager;
import com.graly.mes.prd.adapter.ProcedureItemAdapter;
import com.graly.mes.prd.adapter.ProcedureStateItemAdapter;
import com.graly.mes.prd.adapter.StepStateItemAdapter;

public class ProcedureTreeManager extends TreeViewerManager {

	private static final Logger logger = Logger.getLogger(ProcedureTreeManager.class);
	
	@Override
	protected ItemAdapterFactory createAdapterFactory() {
        ItemAdapterFactory factory = new ItemAdapterFactory();
        try{
        	factory.registerAdapter(Procedure.class, new ProcedureItemAdapter());
	        factory.registerAdapter(ProcedureState.class, new ProcedureStateItemAdapter());
	        factory.registerAdapter(StepState.class, new StepStateItemAdapter());
        } catch (Exception e){
        	logger.error(e.getMessage(), e);
        }
        return factory;
    }
}

package com.graly.mes.prd.viewer;

import org.apache.log4j.Logger;

import com.graly.mes.prd.workflow.graph.def.Process;
import com.graly.mes.prd.workflow.graph.node.ProcedureState;
import com.graly.mes.prd.workflow.graph.node.StepState;
import com.graly.framework.base.ui.views.ItemAdapterFactory;
import com.graly.framework.base.ui.views.TreeViewerManager;
import com.graly.mes.prd.adapter.ProcedureStateItemAdapter;
import com.graly.mes.prd.adapter.ProcessItemAdapter;
import com.graly.mes.prd.adapter.StepStateItemAdapter;

public class ProcessTreeManager extends TreeViewerManager {

	private static final Logger logger = Logger.getLogger(ProcessTreeManager.class);
	
	@Override
	protected ItemAdapterFactory createAdapterFactory() {
        ItemAdapterFactory factory = new ItemAdapterFactory();
        try{
        	factory.registerAdapter(Process.class, new ProcessItemAdapter());
	        factory.registerAdapter(ProcedureState.class, new ProcedureStateItemAdapter());
	        factory.registerAdapter(StepState.class, new StepStateItemAdapter());
        } catch (Exception e){
        	logger.error(e.getMessage(), e);
        }
        return factory;
    }
}

package com.graly.mes.prd.viewer;

import java.util.List;

import org.apache.log4j.Logger;

import com.graly.framework.base.ui.views.ItemAdapter;
import com.graly.framework.base.ui.views.ItemAdapterFactory;
import com.graly.framework.base.ui.views.TreeViewerManager;
import com.graly.mes.prd.adapter.FullProcessFlowItemAdapter;
import com.graly.mes.prd.adapter.ProcedureStateItemAdapter;
import com.graly.mes.prd.adapter.StepStateItemAdapter;
import com.graly.mes.prd.workflow.graph.def.Process;
import com.graly.mes.prd.workflow.graph.node.ProcedureState;
import com.graly.mes.prd.workflow.graph.node.StepState;

public class FullProcessFlowTreeManager extends TreeViewerManager {
	private static final Logger logger = Logger.getLogger(FullProcessFlowTreeManager.class);
	
	@Override
	protected ItemAdapterFactory createAdapterFactory() {
        ItemAdapterFactory factory = new ItemAdapterFactory();
        ItemAdapter itemAdapter = new FullProcessFlowItemAdapter();
        try{
        	factory.registerAdapter(Process.class, itemAdapter);
        	factory.registerAdapter(List.class, itemAdapter);
	        factory.registerAdapter(ProcedureState.class, new ProcedureStateItemAdapter());
	        factory.registerAdapter(StepState.class, new StepStateItemAdapter());
        } catch (Exception e){
        	logger.error(e.getMessage(), e);
        }
        return factory;
    }
}

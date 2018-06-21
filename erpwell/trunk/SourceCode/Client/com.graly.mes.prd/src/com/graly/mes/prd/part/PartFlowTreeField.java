package com.graly.mes.prd.part;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.base.ui.views.TreeViewerManager;
import com.graly.framework.runtime.Framework;
import com.graly.mes.prd.FlowTreeField;
import com.graly.mes.prd.client.PrdManager;
import com.graly.mes.prd.model.Part;
import com.graly.mes.prd.workflow.graph.def.Node;
import com.graly.mes.prd.workflow.graph.def.Process;

public class PartFlowTreeField extends FlowTreeField {
	
	private static final Logger logger = Logger.getLogger(PartFlowTreeField.class);
	
	protected List<Node> flowList;

	public PartFlowTreeField(String id, String label, TreeViewerManager manager) {
		super(id, label, manager);
	}

	@Override
	public void createContent(Composite composite, FormToolkit toolkit) {
		super.createContent(composite, toolkit);

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent e) {
				TreeItem[] items = tree.getSelection();
				if(items != null && items.length > 0) {
					flowList = new ArrayList<Node>();
					TreeItem item = items[0];
					while(item != null && (item.getData() instanceof Node)){
						flowList.add(0, (Node)item.getData());
						item = item.getParentItem();
					}	
				} else {
					flowList = null;
				}
			}
		});
	}

	@Override
	public void refresh() {
		if (getValue() != null) {
			Part part = (Part)getValue();
			if (part != null) {
				try {
					List<Process> processes = new ArrayList<Process>();
					PrdManager prdManager = Framework.getService(PrdManager.class);
					Process process = (Process)prdManager.getPartProcess(part);
					processes.add(process);
					manager.setInput(processes);
					viewer.expandToLevel(2);
				} catch(Exception e) {
					logger.error("Error: " + e);
				}
			}
		}
	}

	public List<Node> getFlowList() {
		return flowList;
	}
}

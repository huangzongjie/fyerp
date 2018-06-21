package com.graly.mes.prd;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.base.ui.forms.field.AbstractField;
import com.graly.framework.base.ui.views.TreeViewerManager;
import com.graly.mes.prd.workflow.graph.def.ProcessDefinition;

public class FlowTreeField extends AbstractField {

	protected TreeViewer viewer;
	protected Tree tree;
	protected TreeViewerManager manager;

	public FlowTreeField(String id, String label, TreeViewerManager manager) {
		super(id);
		setLabel(label);
		this.manager = manager;
	}

	@Override
	public void createContent(Composite composite, FormToolkit toolkit) {
		int i = 0;
		String labelStr = getLabel();
		if (labelStr != null) {
			mControls = new Control[2];
			Label label = toolkit.createLabel(composite, labelStr);
			mControls[0] = label;
			i = 1;
		} else {
			mControls = new Control[1];
		}

		viewer = (TreeViewer) manager.createViewer(new Tree(composite, SWT.SINGLE | SWT.BORDER), toolkit);

		tree = viewer.getTree();
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalAlignment = SWT.FILL;
		gd.verticalAlignment = SWT.FILL;
		gd.heightHint = tree.getItemHeight() * 18;
		gd.widthHint = 60;
		tree.setLayoutData(gd);
		mControls[i] = tree;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public void refresh() {
		ProcessDefinition val = (ProcessDefinition) getValue();
//		if (val != null && val.getObjectRrn() != null) {//这样写CopyFrom时流程不显示
		if (val != null){
			manager.setInput(val);
		} else {
			manager.setInput(null);
		}
	}

	@Override
	public String getFieldType() {
		return "tree";
	}
}

package com.graly.mes.prd.part;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IMessageManager;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.prd.viewer.FullProcessFlowTreeManager;
import com.graly.mes.prd.workflow.graph.def.Node;

public class PartFlowForm extends EntityForm {
	
	private static final Logger logger = Logger.getLogger(PartFlowForm.class);
	private static final String FIELD_ID = "process";
	protected IField field;
	
	public PartFlowForm(Composite parent, int style, ADTable table,
			IMessageManager mmng) {
		super(parent, style, table, mmng);
		super.createForm();
	}
	
	@Override
	public void createForm() {
	}

	@Override
	public void addFields() {
		try {
			FullProcessFlowTreeManager treeManager = new FullProcessFlowTreeManager();
			field = new PartFlowTreeField(FIELD_ID, Message.getString("common.flow"), treeManager);
			addField(FIELD_ID, field);
		} catch (Exception e) {
			logger.error("NewPartForm : createForm", e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}

	@Override
	public boolean saveToObject() {
		return true;
	}

	@Override
	public void loadFromObject() {
		if (object != null) {
			field.setValue(object);
			refresh();
		}
	}

	@Override
	public boolean validate() {
		return true;
	}

	public List<Node> getFlowList() {
		return ((PartFlowTreeField)field).getFlowList();
	}
}

package com.graly.mes.prd.procedure;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IMessageManager;

import com.graly.mes.prd.FlowTreeField;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.mes.prd.designer.FlowEditorInput;
import com.graly.mes.prd.procedure.designer.ProcedureDialog;
import com.graly.mes.prd.viewer.ProcedureTreeManager;
import com.graly.mes.prd.workflow.graph.def.ProcessDefinition;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class ProcedureFlowForm extends Form {

	private static final Logger logger = Logger.getLogger(ProcedureFlowForm.class);
	private static final String FIELD_ID = "children";
	protected IMessageManager mmng;
	protected ProcedureProperties properties;
	protected Button btnEdit;

	public ProcedureFlowForm(ProcedureProperties properties, Composite parent,
			int style, IMessageManager mmng) {
		super(parent, style, null);
		this.mmng = mmng;
		this.properties = properties;
		createForm();
	}

	@Override
	public void createForm() {
		super.createForm();
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = ((GridLayout) form.getBody().getLayout()).numColumns;
		data.horizontalAlignment = SWT.END;
		Composite buttonBar = toolkit.createComposite(form.getBody(), SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		layout.horizontalSpacing = 0;
		buttonBar.setLayout(layout);
		buttonBar.setLayoutData(data);
		btnEdit = toolkit.createButton(buttonBar, Message.getString("common.edit"), SWT.NULL);
		decorateButton(btnEdit);

		btnEdit.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (properties.saveToObject()) {
					for (Form detailForm : properties.getDetailForms()) {
						PropertyUtil.copyProperties(properties.getAdObject(), detailForm.getObject(), detailForm.getFields());
					}
					ProcessDefinition pf = (ProcessDefinition) properties.getAdObject();
					IEditorInput input = new FlowEditorInput(pf.getName(),	pf);
					ProcedureDialog d = new ProcedureDialog(UI.getActiveShell(), input, ProcedureFlowForm.this);

					boolean validateFlag = true;
					for (Form detailForm : properties.getDetailForms()) {
						if (!detailForm.validate()) {
							validateFlag = false;
						}
					}
					if (validateFlag) { // 打开流程编辑器之前,校验一下页面有没有错误信息,没有才弹出
						mmng.removeAllMessages();
						d.open();
					}
				}
			}

		});
	}

	private void decorateButton(Button button) {
		button.setFont(JFaceResources.getDialogFont());
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		int widthHint = 88;
		Point minSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		data.widthHint = Math.max(widthHint, minSize.x);
		button.setLayoutData(data);
	}

	@Override
	public void addFields() {
		try {
			ProcedureTreeManager treeManager = new ProcedureTreeManager();
			IField field = new FlowTreeField(FIELD_ID, Message.getString("common.flow"), treeManager);
			addField(FIELD_ID, field);
		} catch (Exception e) {
			logger.error("ProcessFlowForm : createForm", e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}

	@Override
	public boolean validate() {
		return true;
	}

	@Override
	public boolean saveToObject() {
		return true;
	}

	@Override
	public void loadFromObject() {
		if (object != null) {
			IField f = fields.get(FIELD_ID);
			f.setValue(object);
			refresh();
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.btnEdit.setEnabled(enabled);
	}

	public ProcedureProperties getProperties() {
		return properties;
	}
}

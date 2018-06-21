package com.graly.mes.prd.process.designer;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Env;
import com.graly.mes.prd.client.PrdManager;
import com.graly.mes.prd.designer.FlowEditorInput;
import com.graly.mes.prd.procedure.ProcedureFlowForm;
import com.graly.mes.prd.process.ProcessFlowForm;
import com.graly.mes.prd.workflow.graph.def.ProcessDefinition;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;


public class ProcessDialog extends Dialog {
	
	IEditorInput input;
	Form form;
	
	public ProcessDialog(Shell parent, IEditorInput input, Form form) {
		super(parent);
		setShellStyle(SWT.TITLE);
		this.input = input;
		this.form = form;
	}

	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite)super.createDialogArea(parent);
		area.setFont(parent.getFont());

		ProcessGraphForm form = new ProcessGraphForm(this, new FormToolkit(getShell().getDisplay()), area, input);

		return area;
	}

	@Override
	protected void constrainShellSize() {
		getShell().setMaximized(true);
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		//do nothing
	}

	public boolean doSave(IEditorInput input) {
		try {
			PrdManager prdManager = Framework.getService(PrdManager.class);
			FlowEditorInput flowInput = (FlowEditorInput)input;
			ProcessDefinition obj = prdManager.saveProcessDefinition(flowInput.getProcessDefinition(), Env.getUserRrn());
			
			UI.showInfo(Message.getString("common.save_successed"));
			((ProcessFlowForm)form).getProperties().setAdObject(prdManager.getProcessDefinition(obj));
			((ProcessFlowForm)form).getProperties().refresh();//保存成功后调用ProcessProperties的refresh()刷新页面
			((ProcessFlowForm)form).getProperties().getMasterParent().refresh();
			forceClose();//关闭Dialog
			return true;
        } catch (Exception e) {
        	ExceptionHandlerManager.asyncHandleException(e);
        }
        return false;
	}

	@Override
	public boolean close() {
		boolean sure = UI.showConfirm(Message.getString("common.confirm_exit"));
		if(sure){
			return super.close();
		}
		return false;
	}
	
	public void forceClose(){
		super.close();
	}
}

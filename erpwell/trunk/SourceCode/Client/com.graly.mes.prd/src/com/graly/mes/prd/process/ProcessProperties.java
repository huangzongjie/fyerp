package com.graly.mes.prd.process;

import java.util.List;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.mes.prd.PrdProperties;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.mes.prd.designer.FlowEditorInput;
import com.graly.mes.prd.process.designer.ProcessDialog;
import com.graly.framework.security.model.ADAuthority;

public class ProcessProperties extends PrdProperties {

	@Override
	protected void createSectionTitle(Composite client) {
		super.createSectionTitle(client);
	}

	@Override
	public void createSectionContent(Composite parent) {
		super.createSectionContent(parent);
		IMessageManager mmng = form.getMessageManager();
		CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
		item.setText(Message.getString("common.flow"));
		ProcessFlowForm itemForm = new ProcessFlowForm(this, getTabs(), SWT.NONE, mmng);
		getDetailForms().add(itemForm);
		item.setControl(itemForm);
		
	}
	
	public boolean saveToObject() {
		boolean saveFlag = true;
		for (Form detailForm : getDetailForms()){
			if (!detailForm.saveToObject()){
				saveFlag = false;
			}
		}
		return saveFlag;
	}
	
	@Override
	public void dispose(){
		super.dispose();
	}
}

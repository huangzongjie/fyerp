package com.graly.mes.prd.procedure;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.IMessageManager;

import com.graly.mes.prd.PrdProperties;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.security.model.ADAuthority;;

public class ProcedureProperties extends PrdProperties {

	@Override
	protected void createSectionTitle(Composite client) {
		// TODO Auto-generated method stub
		super.createSectionTitle(client);
	}

	@Override
	public void createSectionContent(Composite parent) {
		super.createSectionContent(parent);
		IMessageManager mmng = form.getMessageManager();
		CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
		item.setText(Message.getString("common.flow"));
		ProcedureFlowForm itemForm = new ProcedureFlowForm(this, getTabs(), SWT.NONE, mmng);
		getDetailForms().add(itemForm);
		item.setControl(itemForm);
	}
	
	protected void createToolItemFrozen(ToolBar tBar) {
		itemFrozen = new AuthorityToolItem(tBar, SWT.PUSH, ADAuthority.KEY_PRD_PROCEDURE_FROZEN);
		itemFrozen.setImage(SWTResourceCache.getImage("frozen"));
		itemFrozen.setText(Message.getString("common.frozen"));
		itemFrozen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				frozenAdapter();
			}
		});
	}

	protected void createToolItemActive(ToolBar tBar) {
		itemActive = new AuthorityToolItem(tBar, SWT.PUSH, ADAuthority.KEY_PRD_PROCEDURE_ACTIVE);
		itemActive.setImage(SWTResourceCache.getImage("active"));
		itemActive.setText(Message.getString("common.active"));
		itemActive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				activeAdapter();
			}
		});
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
}
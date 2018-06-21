package com.graly.erp.pur.request.query;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.UI;

public class PrQuerySection extends MasterSection {
	private static final Logger logger = Logger.getLogger(PrQuerySection.class);

	protected PrQueryDialog onlineDialog;

	public PrQuerySection(EntityTableManager tableManager) {
		super(tableManager);
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSearch(tBar);
		createToolItemExport(tBar);
		section.setTextClient(tBar);
	}
	
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else if(this.onlineDialog != null && onlineDialog.getEntityQueryDialog() != null) {
			queryDialog = onlineDialog.getEntityQueryDialog();
			queryDialog.setVisible(true);
		} else {
			// 此种情况一般不会出现,因为在VendorAssess.open()已将queryDialog设置过来.之所以用
			// VendorAssessDialog(false)表示不创建queryDialog.而是显示调用VendorAssessQueryDialog.
			// 以便传入tableManager,否则会因为在vaDialog无tableId而导致调用getEntityTableManager时出错.
			PrQueryDialog vaDialog = new PrQueryDialog(false);
			queryDialog = vaDialog.new PrInternalQueryDialog(UI.getActiveShell(),
					getADTable(), this);
			vaDialog.setEntityQueryDialog(queryDialog);
			queryDialog.open();
		}
	}

	public void setExtendDialog(ExtendDialog dialog) {
		if(dialog instanceof PrQueryDialog) {
			this.onlineDialog = (PrQueryDialog)dialog;
		} else {
			this.onlineDialog = null;
		}
	}

}

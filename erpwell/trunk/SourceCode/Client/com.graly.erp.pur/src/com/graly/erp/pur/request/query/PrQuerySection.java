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
			// �������һ�㲻�����,��Ϊ��VendorAssess.open()�ѽ�queryDialog���ù���.֮������
			// VendorAssessDialog(false)��ʾ������queryDialog.������ʾ����VendorAssessQueryDialog.
			// �Ա㴫��tableManager,�������Ϊ��vaDialog��tableId�����µ���getEntityTableManagerʱ����.
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

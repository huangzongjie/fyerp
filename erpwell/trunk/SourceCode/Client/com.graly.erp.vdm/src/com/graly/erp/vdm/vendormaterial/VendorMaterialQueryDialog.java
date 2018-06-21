package com.graly.erp.vdm.vendormaterial;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.ui.util.Message;

public class VendorMaterialQueryDialog extends EntityQueryDialog {
	private Button mainVendorOnly;

	public VendorMaterialQueryDialog(Shell parent,
			EntityTableManager tableManager, IRefresh refresh) {
		super(parent, tableManager, refresh);
	}

	public VendorMaterialQueryDialog(Shell parent) {
		super(parent);
	}

	protected void createDialogForm(Composite composite) {
		super.createDialogForm(composite);
		// 创建是否查询仅有主供应商的采购物料列表
		FormToolkit toolkit = new FormToolkit(composite.getDisplay());
		Composite client = toolkit.createComposite(composite, SWT.NULL);
		client.setLayout(new GridLayout(1, false));
		client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mainVendorOnly = toolkit.createButton(client, "", SWT.CHECK);
		mainVendorOnly.setText(Message.getString("vdm.has_main_vendor_only_list"));
	}
	
	@Override
    protected void okPressed() {
		if(!mainVendorOnly.getSelection()) {
			createWhereClause();
			setReturnCode(OK);
			iRefresh.setWhereClause(sb.toString());			
		}
		if(iRefresh instanceof VendorMaterialEntityBlock) {
			((VendorMaterialEntityBlock)iRefresh).setQueryMainVendorOnly(mainVendorOnly.getSelection());
		}
		setVisible(false);
		iRefresh.refresh();			
    }
}

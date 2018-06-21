package com.graly.erp.pdm.material;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.bomedit.BomTreeDialog;

public class UsageInfoBomTreeDialog extends BomTreeDialog {

	public UsageInfoBomTreeDialog(Shell parent, IManagedForm form,
			Material material) {
		super(parent, form, material);
	}

	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemVerify(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemExpendAll(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemView(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemReferenceDoc(tBar);
		section.setTextClient(tBar);
	}
	
}

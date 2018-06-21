package com.graly.erp.ppm.saleplan.temp.prepare2;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.erp.ppm.saleplan.SalePlanEditor;

/**
 *此编辑器与com.graly.erp.ppm.saleplan.temp.prepare.TpsPrepareEditor不同之处在于
 *界面显示不一致但是功能一致
 * */
public class TpsPrepareEditor2 extends SalePlanEditor {
	public static final String EDITOR_ID = "com.graly.erp.ppm.saleplan.temp.prepare2.TempTpsPrepareEditor";
	protected IFormPage page;

	@Override
	protected void addPages() {
		try {
			page = new TpsPrepareEntryPage2(this, "", "");
			addPage(page);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setFocus() {
		page.setFocus();
	}
}

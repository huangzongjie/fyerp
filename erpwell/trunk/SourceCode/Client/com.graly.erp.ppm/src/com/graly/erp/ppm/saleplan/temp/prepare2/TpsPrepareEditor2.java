package com.graly.erp.ppm.saleplan.temp.prepare2;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;

import com.graly.erp.ppm.saleplan.SalePlanEditor;

/**
 *�˱༭����com.graly.erp.ppm.saleplan.temp.prepare.TpsPrepareEditor��֮ͬ������
 *������ʾ��һ�µ��ǹ���һ��
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

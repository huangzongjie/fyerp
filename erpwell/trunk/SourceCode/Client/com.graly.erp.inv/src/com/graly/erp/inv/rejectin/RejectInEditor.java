package com.graly.erp.inv.rejectin;

import org.eclipse.ui.PartInitException;

import com.graly.erp.inv.otherin.OtherInEditor;
/**
 * @author Administrator
 * ÕÀªı»Îø‚
 */
public class RejectInEditor extends OtherInEditor {
	public static final String EDITOR_ID = "com.graly.erp.inv.rejectin.RejectInEditor";

	public RejectInEditor() {
		super();
	}
	
	@Override
	protected void addPages() {
		try {
			page = new RejectInEntryPage(this, "", "");
			addPage(page);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
}

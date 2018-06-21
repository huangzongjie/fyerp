package com.graly.framework.base.uiX.viewmanager.forms;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;

public class XSectionEditor extends FormEditor {
	
	public static final String EDITOR_ID = "com.graly.framework.base.uiX.viewmanager.forms.XSectionEditor";
	
	private String editorTitle;
	
	public XSectionEditor(){
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public String getPartName() {
        return this.editorTitle;
    }
	
	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void init(IEditorSite arg0, IEditorInput arg1) throws PartInitException {
		super.init(arg0, arg1);
	}
	
	public String getEditorId() {
		return getEditorSite().getId();
	}
	
	@Override
	protected void addPages(){
		try {
			IFormPage page = new XSectionEntryPage(this, "", "");
			addPage(page);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}

	public void setEditorTitle(String editorTitle) {
		this.editorTitle = editorTitle;
	}

	public String getEditorTitle() {
		return editorTitle;
	}
}

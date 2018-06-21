package com.graly.framework.base.uiX.viewmanager.forms;

import org.apache.log4j.Logger;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityEditorInput;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.uiX.viewmanager.XTableViewerAdapter;
import com.graly.framework.base.uiX.viewmanager.XTableViewerManager;

public class XSectionEntryPage extends FormPage {
	private static final Logger logger = Logger.getLogger(SectionEntryPage.class);
	protected IManagedForm form;
	protected XMasterSection masterSection;
	
	public XSectionEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}

	public XSectionEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		this.form = managedForm;
		Composite body = managedForm.getForm().getBody();
		
		ADTable adTable = ((EntityEditorInput)this.getEditor().getEditorInput()).getTable();
		try{
			String editorTitle = String.format(Message.getString("common.editor"), I18nUtil.getI18nMessage(adTable, "label"));
			((XSectionEditor)this.getEditor()).setEditorTitle(editorTitle);
		} catch (Exception e){
			logger.error("Error At XSectionEntryPage.createFormContent() Method :" + e);
		}
		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		body.setLayout(layout);
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createSection(adTable);
		masterSection.createContents(form, body);
		setFocus();
	}
	
	protected void createSection(ADTable adTable) {
		masterSection = new XMasterSection(new XTableViewerManager(new XTableViewerAdapter(adTable)));
	}

	public XMasterSection getMasterSection() {
		return masterSection;
	}

}

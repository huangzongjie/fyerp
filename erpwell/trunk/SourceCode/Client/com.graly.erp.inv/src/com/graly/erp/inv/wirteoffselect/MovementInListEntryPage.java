package com.graly.erp.inv.wirteoffselect;

import org.apache.log4j.Logger;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityEditor;
import com.graly.framework.base.entitymanager.editor.EntityEditorInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;

public class MovementInListEntryPage extends SectionEntryPage {
	private static final Logger logger = Logger.getLogger(SectionEntryPage.class);
	protected MasterSection inListSection;
	
	public MovementInListEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}
	
	public MovementInListEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		this.form = managedForm;
		Composite body = managedForm.getForm().getBody();
		
		ADTable adTable = ((EntityEditorInput)this.getEditor().getEditorInput()).getTable();
		try{
			String editorTitle = String.format(Message.getString("common.editor"), I18nUtil.getI18nMessage(adTable, "label"));
			((EntityEditor)this.getEditor()).setEditorTitle(editorTitle);
		} catch (Exception e){
			logger.error("Error At HistoryEntryPage.createFormContent() Method :" + e);
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
	
	@Override
	protected void createSection(ADTable adTable) {
		inListSection = new MovementInListSection(new EntityTableManager(adTable),this);
		masterSection = inListSection;
		masterSection.setWhereClause(" 1 <> 1 ");
	}

	public MasterSection getInListSection() {
		return inListSection;
	}
}

package com.graly.erp.wip.mo.material_standtime;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityEditor;
import com.graly.framework.base.entitymanager.editor.EntityEditorInput;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;

public class StandTimeQueryEntryPage extends SectionEntryPage {
	private static final Logger logger = Logger.getLogger(StandTimeQueryEntryPage.class);
//	private StandTimeQuerySection2 standTimeQuerySection;

	private StandTimeSection standTimeSection;
	
	protected IManagedForm form;
	public StandTimeQueryEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	public StandTimeQueryEntryPage(FormEditor editor, String id, String name,ADTable table) {
		super(editor, id, name);
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
//		super.createFormContent(managedForm);
		this.form = managedForm;
		
		Composite body = managedForm.getForm().getBody();//获得当前form的body
		ADTable table = ((EntityEditorInput)this.getEditor().getEditorInput()).getTable();
		try{
			String editorTitle = String.format(Message.getString("common.editor"),
					I18nUtil.getI18nMessage(table, "label"));
			((EntityEditor)this.getEditor()).setEditorTitle(editorTitle);
		} catch (Exception e){
			logger.error("Error At SplitEntryPage.createFormContent(): ", e);
		}
//		standTimeQuerySection = new StandTimeQuerySection2(table);
//		standTimeQuerySection.createContents(managedForm, body);
		
		standTimeSection = new StandTimeSection(table);
		standTimeSection.createContents(managedForm, body);
	}
	
//	@Override
//	protected void createSection(ADTable adTable) {
//		masterSection = new StandTimeQuerySection(new EntityTableManager(adTable));
//	}
}

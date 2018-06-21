package com.graly.promisone.base.entitymanager.editor;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.jface.viewers.StructuredSelection;

import com.graly.promisone.base.entitymanager.forms.EntityBlock;
import com.graly.promisone.base.security.login.ChangeAreaDialog;
import com.graly.promisone.base.ui.util.Env;
import com.graly.promisone.base.ui.util.I18nUtil;
import com.graly.promisone.base.ui.util.Message;
import com.graly.promisone.runtime.Framework;
import com.graly.promisone.activeentity.client.ADManager;
import com.graly.promisone.activeentity.model.ADBase;
import com.graly.promisone.activeentity.model.ADTable;

public class EntityEntryPage extends FormPage {
	
	private final static Logger logger = Logger.getLogger(EntityEntryPage.class);
	
	protected EntityBlock block;
	
	public EntityEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}
	
	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		body.setLayout(layout);
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	protected void createBlock(ADTable adTable) {
		block = new EntityBlock(new EntityTableManager(adTable));
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		Composite body = managedForm.getForm().getBody();
		configureBody(body);
		
		ADTable adTable = ((EntityEditorInput)this.getEditor().getEditorInput()).getTable();
		createBlock(adTable);
		block.createContent(managedForm);    
		try{
			String editorTitle = String.format(Message.getString("common.editor"), I18nUtil.getI18nMessage(adTable, "label"));
			((EntityEditor)this.getEditor()).setEditorTitle(editorTitle);
		} catch (Exception e){
		}
		fireSelectionChanged(managedForm);
	}
	
	protected void fireSelectionChanged(IManagedForm managedForm){
		try{
			EntityTableManager tableManager = block.getTableManager();
			ADTable table = tableManager.getADTable();
			Object obj = Class.forName(table.getModelClass()).newInstance();
			if (obj instanceof ADBase) {
				((ADBase)obj).setOrgId(Env.getOrgId());
			}
			for(IFormPart part : managedForm.getParts()){
				if (part instanceof SectionPart) {
					managedForm.fireSelectionChanged(part, new StructuredSelection(new Object[] {obj}));
				}
			}
		} catch (Exception e){
			logger.error("fireSelectionChanged error: ", e);
		}
	}
}

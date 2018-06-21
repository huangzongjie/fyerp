package com.graly.erp.vdm.materialassessment;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityEditor;
import com.graly.framework.base.entitymanager.editor.EntityEditorInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.Message;

public class MaterialAssEntryPage extends FormPage {
	private static final Logger logger = Logger.getLogger(MaterialAssEntryPage.class);
	private ADTable adTable;
	protected IManagedForm form;
	protected MaterialAssSection materialAssSection;

	public MaterialAssEntryPage(FormEditor editor, String id, String name, ADTable table) {
		super(editor, id, name);
	}

	public MaterialAssEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		this.form = managedForm;
		form.getForm().setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		Composite body = managedForm.getForm().getBody();//获得当前form的body
		try {
			adTable = ((EntityEditorInput) this.getEditor().getEditorInput()).getTable();
			((EntityEditor) this.getEditor()).setEditorTitle(Message.getString("vdm.editortitle_material_assessment"));
		} catch (Exception e) {
			logger.error("Error At MaterialAssEntryPage.createFormContent() Method :" + e);
		}
		materialAssSection = new MaterialAssSection(new EntityTableManager(adTable));
		materialAssSection.createContents(form, body);
	}

}
package com.graly.erp.wip.calendarhour;

import org.apache.log4j.Logger;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import com.graly.erp.wip.calendarday.WipCalendarDayEntryPage;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityEditor;
import com.graly.framework.base.entitymanager.editor.EntityEditorInput;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;

public class CalendarHourEntryPage extends FormPage {
	private static final Logger logger = Logger.getLogger(WipCalendarDayEntryPage.class);
	protected IManagedForm form;
	protected CalendarHourSection calendarHourSection;
	
	public CalendarHourEntryPage(FormEditor editor, String id, String name,
			ADTable table) {
		super(editor, id, name);
	}

	public CalendarHourEntryPage(FormEditor editor, String id, String name) {
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
			logger.error("Error At CalendarHourEntryPage.createFormContent() Method :" + e);
		}
		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		body.setLayout(layout);
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createSection(adTable);
		calendarHourSection.createContents(form, body);
		setFocus();
	}
	
	protected void createSection(ADTable adTable) {
		calendarHourSection = new CalendarHourSection(adTable);
	}

}

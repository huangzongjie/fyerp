package com.graly.erp.wip.querychart;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.birt.chart.model.Chart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.chart.builder.AbstractChartBuilder;
import com.graly.erp.chart.model.ChartCanvas;
import com.graly.erp.chart.model.ChartWithToolTipCanvas;
import com.graly.erp.wip.model.DailyMoMaterial;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityEditor;
import com.graly.framework.base.entitymanager.editor.EntityEditorInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;

public class ChartEntryPage extends FormPage {
	private static final Logger logger = Logger.getLogger(ChartEntryPage.class);
	
	protected IManagedForm form;
	protected ChartSection masterSection;
	private FormEditor editor;

	public ChartEntryPage(FormEditor editor, String id, String name) {
		super(editor, id, name);
		this.editor = editor;
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		form = managedForm;
		Composite body = form.getForm().getBody();
		
		ADTable adTable = ((EntityEditorInput)this.getEditor().getEditorInput()).getTable();
		try{
			String editorTitle = String.format(Message.getString("common.editor"), I18nUtil.getI18nMessage(adTable, "label"));
			((EntityEditor)this.getEditor()).setEditorTitle(editorTitle);
		} catch (Exception e){
			logger.error("Error At ChartEntryPage.createFormContent() Method :" + e);
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
		masterSection = new ChartSection(new EntityTableManager(adTable));
	}

	public ChartSection getMasterSection() {
		return masterSection;
	}

	public void setMasterSection(ChartSection masterSection) {
		this.masterSection = masterSection;
	}
}

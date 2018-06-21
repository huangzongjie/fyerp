package com.graly.erp.wip.workcenter;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.application.Activator;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.MDSashForm;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.model.WorkCenter;

public class WorkCenterSection {
	private static final Logger logger = Logger.getLogger(WorkCenterSection.class);
	public static final String TABLE_ANME_MO_LINE = "WIPManufactureOrderLine";
	public static final String FIELD_DATE_END = "dateEnd";
	
	protected IManagedForm form;
	protected WorkCenter workCenter;
	protected ADTable moLineAdTable, wcAdTable;
	protected SashForm sashForm;
	protected MoLineSection moLineSection;
	protected InvMaterialSection invSection;
	
	public WorkCenterSection(IManagedForm form){
		this.form = form;
	}
	
	public WorkCenterSection(ADTable moLineAdTable, ADTable wcAdTable) {
		this.moLineAdTable = moLineAdTable;
		this.wcAdTable = wcAdTable;
	}

	public void refresh() {
		try {
			if(moLineSection != null)
				moLineSection.refreshAll();
			if(invSection != null)
				invSection.refresh();
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("WorkCenterEntryPage : refresh()", e);
		}
	}
	
	public void createContent(Composite parent) {
		if(wcAdTable == null) {
			wcAdTable = getAdTableByName(WorkCenterEntryPage.TABLE_NAME);
		}
		FormToolkit toolkit = form.getToolkit();
		sashForm = new MDSashForm(parent, SWT.NULL);
		sashForm.setData("form", form); //$NON-NLS-1$
		toolkit.adapt(sashForm, false, false);
		sashForm.setMenu(parent.getMenu());
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		createMOSectionContent(form, sashForm);
		createInvMaterialContent(form, sashForm);
		createToolBarActions(form);
		form.getForm().updateToolBar();
	}
	
	protected void createMOSectionContent(IManagedForm form, Composite parent) {
		if(moLineAdTable == null) {
			moLineAdTable = getAdTableByName(TABLE_ANME_MO_LINE);
		}
		TableListManager tableManager = new MoLineTableListManager(moLineAdTable);
		ADTable queryTable = getAdTableByName(TABLE_ANME_MO_LINE);
		resetQueryADTable(queryTable);
		EntityTableManager entityTableManager = new EntityTableManager(queryTable);
		moLineSection = new MoLineSection(tableManager, entityTableManager);
		moLineSection.createContent(form, parent);
	}
	
	protected void createInvMaterialContent(IManagedForm form, Composite parent) {
		invSection = new InvMaterialSection();
		invSection.createContent(form, parent);
	}
	
	// 设置查询的控件每行显示一个, 完成日期为FROMTO_CALENDAR控件
	private void resetQueryADTable(ADTable queryTable) {
		if(queryTable != null) {
			for(ADTab tab : queryTable.getTabs()) {
				tab.setGridY(1L);
				break;
			}
			for(ADField adField : queryTable.getFields()) {
				if(FIELD_DATE_END.equals(adField.getName())) {
					adField.setDisplayType(FieldType.FROMTO_CALENDAR);
					break;
				}
			}
		}
	}

	protected void createToolBarActions(IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();    
	    Action haction = new Action("hor", Action.AS_RADIO_BUTTON) {    
	        public void run() {
	          sashForm.setOrientation(SWT.HORIZONTAL);  
	        }    
	    };    
	    haction.setChecked(true);    
	    haction.setToolTipText(Message.getString("common.horizontal"));    
	    haction.setImageDescriptor(Activator.getImageDescriptor("horizontal"));    
	        
	    Action vaction = new Action("ver", Action.AS_RADIO_BUTTON) {    
	        public void run() {    
	          sashForm.setOrientation(SWT.VERTICAL);    
	        }    
	    };    
	    vaction.setChecked(false);
	    vaction.setToolTipText(Message.getString("common.vertical"));
	    vaction.setImageDescriptor(Activator.getImageDescriptor("vertical"));
	    form.getToolBarManager().add(haction);
	    form.getToolBarManager().add(vaction);
	    
	    if (wcAdTable.getIsVertical()) {
			sashForm.setOrientation(SWT.VERTICAL);  
			this.setOrientation(new int[]{5, 5});
		} else {
			sashForm.setOrientation(SWT.HORIZONTAL);
			this.setOrientation(new int[]{3, 7});
		}
	}
	
	protected void setOrientation(int[] weights) {
		sashForm.setWeights(weights);		
	}
	
	protected ADTable getAdTableByName(String tableName) {
		ADTable adTable = null;;
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = (ADTable)entityManager.getADTable(0, tableName);
		} catch(Exception e) {
			logger.error("InvMaterialSection : getAdTableOfInvMaterial()", e);
		}
		return adTable;
	}
	
	public void setIManagedForm(IManagedForm form) {
		this.form = form;
	}
	
	public WorkCenter getWorkCenter() {
		return workCenter;
	}

	public void setWorkCenter(WorkCenter workCenter) {
		this.workCenter = workCenter;
		if(workCenter != null) {
			if(moLineSection != null)
				moLineSection.setWorkCenter(workCenter);
			if(invSection != null)
				invSection.setWorkCenter(workCenter);
		}
	}

	public ADTable getWcAdTable() {
		return wcAdTable;
	}

	public void setWcAdTable(ADTable wcAdTable) {
		this.wcAdTable = wcAdTable;
	}
}

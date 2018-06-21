package com.graly.erp.wip.workcenter;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.EntityDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.views.TableViewerManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;
/**
 * 计划通知提醒--不用接收也能看见提醒
 * */
public class MpsLineDeliveryDialog extends EntityDialog {
	protected TableViewerManager tableManager;
	protected TableViewer viewer;
	protected ADTable adTable;
	protected static String DIALOG_TABLE_NAME="MpsLineDeliveryNotice";
	protected MpsLineDeliveryDialog alarmDialog;
	protected String whereClause;
	protected static String PAGE_CATEGORY_IQC = "newIqcAlarm";
	private ADTable dialogADTable;
	private ManufactureOrderLine moLine;
	
	
	public MpsLineDeliveryDialog(Shell parent, ADTable table, ADBase adObject,ManufactureOrderLine moLine) {
		super(parent, table, adObject);
		this.moLine = moLine;
	}
	@Override
	protected void createFormContent(Composite composite) {
		setTitleImage(SWTResourceCache.getImage("entity-dialog"));
	    setTitle("-------------计划通知提醒请您，请您及时处理-------------");
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		managedForm = new ManagedForm(toolkit, sForm);
		final IMessageManager mmng = managedForm.getMessageManager();
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		ADManager entityManager;
		try {
			FormToolkit formToolkit  = new FormToolkit(Display.getCurrent());
			entityManager = Framework.getService(ADManager.class);
			dialogADTable = entityManager.getADTable(0L, DIALOG_TABLE_NAME);
			tableManager = new EntityTableManager(dialogADTable);
			viewer = (TableViewer) getTableManager().createViewer(body, formToolkit);
//			viewer.setInput(null);
			initTableViewer();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public TableViewerManager getTableManager() {
		return tableManager;
	}

	public void setTableManager(TableViewerManager tableManager) {
		this.tableManager = tableManager;
	}
	 
 
	protected ADTable getADTableOfRequisition(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
		}
		return null;
	}
	public void initTableViewer(){
		try {
			ADManager adManager = Framework.getService(ADManager.class);
			ManufactureOrder mo=new ManufactureOrder();
			mo.setObjectRrn(moLine.getMasterMoRrn());
			adManager=Framework.getService(ADManager.class);
			mo=(ManufactureOrder)adManager.getEntity(mo);
			
			String where = " mpsId = '" + mo.getMpsId()+ "' AND  materialRrn= " + moLine.getMaterialRrn() +" AND docStatus = 'APPROVED'";
			List<ADBase> list = adManager.getEntityList(Env.getOrgRrn(), dialogADTable.getObjectRrn(), Env.getMaxResult(), where, null);
			
			tableManager.setInput(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	@Override
	protected void buttonPressed(int buttonId) {
		if (IDialogConstants.OK_ID == buttonId) {
			okPressed();
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.cancel"), false);
	}
}

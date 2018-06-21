package com.graly.erp.inv.material.online;

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

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.model.AlarmData;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.EntityDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.views.TableViewerManager;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;

public class OnlineErrorDialog extends EntityDialog {


	protected TableViewerManager tableManager;
	protected TableViewer viewer;
	protected ADTable adTable;
	protected static String DIALOG_TABLE_NAME="MaterialOnlineQueryError";
	private AlarmData alarmData;//当前选中的警报
	private ADTable dialogADTable;
	private List<Material> errorMaterials;
	
	
	public OnlineErrorDialog(Shell parent, ADTable table, ADBase adObject,List<Material> errorMaterials) {
		super(parent, table, adObject);
		this.errorMaterials = errorMaterials;
	}
	@Override
	protected void createFormContent(Composite composite) {
		setTitleImage(SWTResourceCache.getImage("entity-dialog"));
	    setTitle("物料统计失败记录");
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
	public AlarmData getAlarmData() {
		return alarmData;
	}
	public void setAlarmData(AlarmData alarmData) {
		this.alarmData = alarmData;
	}
	public void initTableViewer(){
		try {
			tableManager.setInput(errorMaterials);
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
//		Button btnOk = createButton(parent, IDialogConstants.OK_ID,
//				Message.getString("common.ok"), false);
//		if (DIALOGTYPE_VIEW.equals(dialogType)) {
//			btnOk.setEnabled(false);
//		}
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.cancel"), false);
	}
	public List<Material> getErrorMaterials() {
		return errorMaterials;
	}
	public void setErrorMaterials(List<Material> errorMaterials) {
		this.errorMaterials = errorMaterials;
	}
}

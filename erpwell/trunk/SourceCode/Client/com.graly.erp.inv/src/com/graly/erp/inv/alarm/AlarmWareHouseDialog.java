package com.graly.erp.inv.alarm;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.inv.in.createfrom.iqc.CreateContext;
import com.graly.erp.inv.in.createfrom.iqc.CreateDialog;
import com.graly.erp.inv.in.createfrom.iqc.IqcCreateWizard;
import com.graly.erp.inv.model.AlarmData;
import com.graly.erp.inv.model.MovementIn;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.EntityDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.entitymanager.views.TableViewerManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;

public class AlarmWareHouseDialog extends EntityDialog {


	protected TableViewerManager tableManager;
	protected TableViewer viewer;
	protected ADTable adTable;
	protected static String DIALOG_TABLE_NAME="AlarmData";
	protected AlarmWareHouseDialog alarmDialog;
	protected String whereClause;
	protected static String PAGE_CATEGORY_WAREHOUSE = "createIqcAlarm";
	private AlarmData alarmData;//当前选中的警报
	private ADTable dialogADTable;
	private List<AlarmData> alarmDatas;
	
	public AlarmWareHouseDialog(Shell parent, ADTable table, ADBase adObject,List<AlarmData> alarmDatas) {
		super(parent, table, adObject);
		this.alarmDatas = alarmDatas;
	}
	@Override
	protected void createFormContent(Composite composite) {
		setTitleImage(SWTResourceCache.getImage("entity-dialog"));
	    setTitle("-------------采购入库提醒请您，请您及时处理-------------");
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
			viewer.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					try {
						StructuredSelection ss = (StructuredSelection) event.getSelection();
						AlarmData alarmData = (AlarmData) ss.getFirstElement();
						setAlarmData(alarmData);
						menuIqcAdapter();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			//new EntityItemInput(getTableManager().getADTable(), getWhereClause(), "")
			tableManager.updateView(viewer);
			if(viewer != null && viewer instanceof CheckboxTableViewer){
				((CheckboxTableViewer)viewer).remove(getAdObject());
			}
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
 
	//仓库警报
	protected void menuIqcAdapter() {
		CreateContext context = new CreateContext();
		context.setCategory(CreateContext.CATEGORY_NEW_IQC);
		context.setAlarmWhereClause(" and receiptId = '"+alarmData.getField1()+"'");
		ADTable table = context.getTable(CreateContext.TableName_Iqc);
		TableListManager listTableManager = new TableListManager(table);

		IqcCreateWizard wizard = new IqcCreateWizard(context, PAGE_CATEGORY_WAREHOUSE);
		CreateDialog dialog = new CreateDialog(UI.getActiveShell(), wizard, listTableManager);
		context.setDialog(dialog);
		int code = dialog.open();
		if (code == Dialog.OK) {
			MovementIn mi = context.getIn();
			if(mi != null && mi.getObjectRrn() != null) {
//				selectedIn = mi;
//				refreshSection();
//				refreshAdd(selectedIn);
			}
		}
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
	
	/**
	 * 初始化警报数据
	 * */
	public void initTableViewer(){
		try {
//			ADManager adManager = Framework.getService(ADManager.class);
//			List<AlarmData> alarmDatas = adManager.getEntityList(Env.getOrgRrn(), AlarmData.class,Integer.MAX_VALUE,"userRrn="+Env.getUserRrn()
//					+" and status ='"+AlarmData.STATUS_OPEN+"'  and alarmType ='WAREHOUSE' ",null);
			tableManager.setInput(alarmDatas);
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
}

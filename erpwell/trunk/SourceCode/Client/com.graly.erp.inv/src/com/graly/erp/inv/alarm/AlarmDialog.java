package com.graly.erp.inv.alarm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.inv.in.createfrom.iqc.CreateContext;
import com.graly.erp.inv.in.createfrom.iqc.CreateDialog;
import com.graly.erp.inv.iqc.IqcLineDialog;
import com.graly.erp.inv.iqc.createfrom.CreateIqcContext;
import com.graly.erp.inv.iqc.createfrom.CreateIqcDialog;
import com.graly.erp.inv.iqc.createfrom.IqcCreateWizard;
import com.graly.erp.inv.model.AlarmData;
import com.graly.erp.inv.model.AlarmTarget;
import com.graly.erp.inv.model.Iqc;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.po.model.PurchaseOrderLine;
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

public class AlarmDialog extends EntityDialog {


	protected TableViewerManager tableManager;
	protected TableViewer viewer;
	protected ADTable adTable;
	protected static String DIALOG_TABLE_NAME="AlarmData";
	protected AlarmDialog alarmDialog;
	protected String whereClause;
	protected static String PAGE_CATEGORY_IQC = "newIqcAlarm";
	private static final String TABLE_NAME_IqcLine = "INVIqcLine";
	private AlarmData alarmData;//当前选中的警报
	private ADTable dialogADTable;
	private List<AlarmData> alarmDatas;
	
	
	public AlarmDialog(Shell parent, ADTable table, ADBase adObject,List<AlarmData> alarmDatas) {
		super(parent, table, adObject);
		this.alarmDatas = alarmDatas;
	}
	@Override
	protected void createFormContent(Composite composite) {
		setTitleImage(SWTResourceCache.getImage("entity-dialog"));
	    setTitle("-------------收货检验提醒请您，请您及时处理-------------");
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
			
			viewer.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					try {
						StructuredSelection ss = (StructuredSelection) event.getSelection();
						AlarmData alarmData = (AlarmData) ss.getFirstElement();
						setAlarmData(alarmData);
						copyFrom();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			initTableViewer();
			//new EntityItemInput(getTableManager().getADTable(), getWhereClause(), "")
//			tableManager.updateView(viewer);
//			if(viewer != null && viewer instanceof CheckboxTableViewer){
//				((CheckboxTableViewer)viewer).remove(getAdObject());
//			}
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
	//检验警报
	public void copyFrom(){
		CreateIqcContext context = new CreateIqcContext();
		context.setCategory(CreateIqcContext.CATEGORY_NEW_IQC);
		context.setAlarmWhereClause(" and docId = '"+alarmData.getField1()+"'");
		ADTable table = context.getTable(CreateIqcContext.TableName_Receipt);
		TableListManager listTableManager = new TableListManager(table);

		IqcCreateWizard wizard = new IqcCreateWizard(context, PAGE_CATEGORY_IQC);
		CreateIqcDialog dialog = new CreateIqcDialog(UI.getActiveShell(), wizard, listTableManager);
		context.setDialog(dialog);
		int code = dialog.open();
		if (code == Dialog.OK) {
			Iqc iqc = context.getIqc();
			String where = " iqcId= '" + iqc.getDocId() + "'";
			adTable = getADTableOfRequisition(TABLE_NAME_IqcLine);
			IqcLineDialog iqcLineDialog = new IqcLineDialog(UI.getActiveShell(), adTable, where, iqc);
			if(iqcLineDialog.open() == Dialog.OK){
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
	public void initTableViewer(){
		try {
//			ADManager adManager = Framework.getService(ADManager.class);
//			List<AlarmData> alarmDatas = adManager.getEntityList(Env.getOrgRrn(), AlarmData.class,Integer.MAX_VALUE,"userRrn="+Env.getUserRrn()
//					+" and status ='"+AlarmData.STATUS_OPEN+"'  and alarmType ='IQC' ",null);
			tableManager.setInput(alarmDatas);
			//对于检验周期：当系统时间减去制单时间超过检验周期，超1天提醒无颜色，超2天提醒为绿色，超3天为黄色，4天及4天以上为红色
			if (viewer instanceof TableViewer) {
				TableViewer tViewer = (TableViewer) viewer;
				Table table = tViewer.getTable();
				for (TableItem item : table.getItems()) {
					AlarmData alarmData = (AlarmData) item.getData();
					if(alarmData.getField13()!=null){
//						long iqcTime = Long.parseLong(alarmData.getField4());//检验周期
//						DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//						Date d1 = df.parse("2008-03-26 13:31:40"); 
//						Date d2 = df.parse("2008-01-02 11:30:24");
//						if(iqcTime>=2){
							Date d1 = alarmData.getField13();
							Date d2 = Env.getSysDate();
							long diff = d2.getTime() - d1.getTime(); 
//							long days = diff / (1000 * 60 * 60 * 24);
//							long overIqcTime = days - iqcTime;//超过检验周期天数/
							if(diff >86400000 && diff <=86400000*2){
								Color greenColor = Display.getDefault().getSystemColor(SWT.COLOR_GREEN);
								item.setBackground(greenColor);
							}else if(diff >86400000*2 && diff <=86400000*3 ){
								Color yellowColor = Display.getDefault().getSystemColor(SWT.COLOR_YELLOW);
								item.setBackground(yellowColor);
							}else if(diff >86400000*3){
								Color redColor = Display.getDefault().getSystemColor(SWT.COLOR_RED);
								item.setBackground(redColor);
							}
//						}
					}
				}
			}
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
 
	public List<AlarmData> getAlarmDatas() {
		return alarmDatas;
	}
	public void setAlarmDatas(List<AlarmData> alarmDatas) {
		this.alarmDatas = alarmDatas;
	}
	
	
}

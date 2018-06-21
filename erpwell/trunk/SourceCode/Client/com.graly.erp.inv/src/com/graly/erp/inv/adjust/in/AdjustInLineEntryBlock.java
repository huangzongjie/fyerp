package com.graly.erp.inv.adjust.in;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Material;
import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.in.ApprovedDialog;
import com.graly.erp.inv.in.WarehouseEntityForm;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.pur.model.Requisition;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.ParentChildEntityBlock;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;
public class AdjustInLineEntryBlock extends ParentChildEntityBlock {
	Logger logger = Logger.getLogger(AdjustInLineEntryBlock.class);
	private static final String TABLE_NAME_INVOICE_MOVEMENTLINE = "INVFinanceMovementLine";
	protected ToolItem itemApprove;
	protected MovementLine selectMovementLine;
	private ADTable movementLineTable; //审核或冲销时的movement line Table
	protected ToolItem itemGen;
	protected ToolItem itemLot;
	private ToolItem itemWriteOff;
	protected ToolItem itemPreview;
	protected AdjustInLineProperties oinLineProperties;
	private String FieldName_Warehouse = "warehouseRrn";
	protected boolean flag = false;

	public AdjustInLineEntryBlock(ADTable parentTable, Object parentObject, String whereClause, ADTable childTable, boolean flag) {
		super(parentTable, parentObject, whereClause, childTable);
		this.parentObject = parentObject;
		this.flag= flag;
	}

	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		super.createMasterPart(managedForm, parent);
		refresh();
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setMovementLineSelect(ss.getFirstElement());
				lotAdapter();
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setMovementLineSelect(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		MovementIn movementIn = (MovementIn) parentObject;
		refreshAll(movementIn.getDocStatus());
		
		addLocatorListener();
		
	}

	protected void createParentContent(Composite client) {
		final FormToolkit toolkit = form.getToolkit();
		final IMessageManager mmng = form.getMessageManager();
		setTabs(new CTabFolder(client, SWT.FLAT | SWT.TOP));
		getTabs().marginHeight = 10;
		getTabs().marginWidth = 5;
		toolkit.adapt(getTabs(), true, true);
		GridData gd = new GridData(GridData.FILL_BOTH);
		getTabs().setLayoutData(gd);
		Color selectedColor = toolkit.getColors().getColor(FormColors.SEPARATOR);
		getTabs().setSelectionBackground(
						new Color[] { selectedColor,
								toolkit.getColors().getBackground() },
						new int[] { 50 });
		toolkit.paintBordersFor(getTabs());

		for (ADTab tab : parentTable.getTabs()) {
			CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
			item.setText(I18nUtil.getI18nMessage(tab, "label"));
			WarehouseEntityForm itemForm = new WarehouseEntityForm(getTabs(), SWT.NONE, tab, mmng);
			getDetailForms().add(itemForm);
			item.setControl(itemForm);
		}
		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void registerPages(DetailsPart detailsPart) {
		try {
			ADTable table = getTableManager().getADTable();
			Class klass = Class.forName(table.getModelClass());
			oinLineProperties = new AdjustInLineProperties(this, table, getParentObject(), flag);
			detailsPart.registerPage(klass, oinLineProperties);
		} catch (Exception e) {
			logger.error("InLineEntryBlock : registerPages ", e);
		}
	}

	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemGenerate(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemGenerateLot(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemApprove(tBar);
		createToolItemWriteOff(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemPreview(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemGenerate(ToolBar tBar) {
		itemGen = new ToolItem(tBar, SWT.PUSH);
		itemGen.setText(Message.getString("inv.lot_create"));
		itemGen.setImage(SWTResourceCache.getImage("barcode"));
		itemGen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				generateAdapter();
			}
		});
	}
	
	protected void createToolItemWriteOff(ToolBar tBar) {
		itemWriteOff = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_OIN_EDIT_WRITEOFF);
		itemWriteOff.setText(Message.getString("inv.in_write_off"));
		itemWriteOff.setImage(SWTResourceCache.getImage("voice"));
		itemWriteOff.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				writeOffAdapter();
			}
		});
	}
	
	protected void createToolItemPreview(ToolBar tBar) {
		itemPreview = new ToolItem(tBar, SWT.PUSH);
		itemPreview.setText(Message.getString("common.print"));
		itemPreview.setImage(SWTResourceCache.getImage("print"));
		itemPreview.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				previewAdapter();
			}
		});
	}
	
	protected void generateAdapter() {
		try {
			if(selectMovementLine != null) {
				String whereClause = " movementLineRrn = " + selectMovementLine.getObjectRrn() + " ";
				ADManager adManager = Framework.getService(ADManager.class);
				List<MovementLineLot> list = adManager.getEntityList(Env.getOrgRrn(), MovementLineLot.class, 
	            		Env.getMaxResult(), whereClause, "");
				if(list != null && list.size() > 0) {
					UI.showError(String.format(Message.getString("inv.in_lot_has_had_aleardy"),
							selectMovementLine.getMaterialName()));
					return;
				}
				
				Material material = new Material();
				material.setObjectRrn(selectMovementLine.getMaterialRrn());
				material = (Material)adManager.getEntity(material);
				if(Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
					UI.showError(String.format(Message.getString("inv.material_is_not_need_generate_lot"),
							material.getMaterialId()));
					return;
				}
				MovementIn in = (MovementIn)parentObject;
				in = (MovementIn)adManager.getEntity(in);
				parentObject = in;
				selectMovementLine = (MovementLine)adManager.getEntity(selectMovementLine);
				// 根据Lot Type获得Lots
				INVManager invManager = Framework.getService(INVManager.class);
				List<Lot> lots = null;
				if(Lot.LOTTYPE_BATCH.equals(material.getLotType())) {
					BatchSetupDialog dialog = new BatchSetupDialog(UI.getActiveShell(), selectMovementLine);
					if(dialog.open() == Dialog.OK) {
						int batchQty = dialog.getBatchQty();
						lots = invManager.generateBatchLot(Env.getOrgRrn(), material,
								selectMovementLine.getQtyMovement(), batchQty, Env.getUserRrn());
					}
				} else if(Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
					lots = invManager.generateSerialLot(Env.getOrgRrn(), material,
							selectMovementLine.getQtyMovement().intValue(), Env.getUserRrn());
				} else {
					UI.showError(String.format(Message.getString("wip.material_has_not_lot_type"),
							material.getMaterialId()));
					return;
				}
				if(lots == null || lots.size() == 0) return;
				// 打开生成的Lots界面，可以完成删除、保存和打印等功能
				LotGenerateDialog genLotDialog = new LotGenerateDialog(UI.getActiveShell(),
						in, selectMovementLine, lots);
				if (genLotDialog.open() == Dialog.CANCEL) {
					parentObject = (MovementIn)adManager.getEntity((MovementIn)in);
					selectMovementLine = null;
					this.viewer.setSelection(null);
					refresh();
				}
			} else {
				UI.showWarning(Message.getString("inv.entityisnull"));
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("GenerateLotSection generateAdapter(): error!");
		}
	}
	
	protected void writeOffAdapter() {
		try {
			if (parentObject != null) {
				ApprovedDialog approvedDialog = new ApprovedDialog(UI.getActiveShell(),
						form, getInvoiceMovementLineTable(), (MovementIn)parentObject, true);
				if(approvedDialog.open() == Dialog.OK) {
					form.getMessageManager().removeAllMessages();
					MovementIn in = (MovementIn)approvedDialog.getPin();
					
					INVManager invManager = Framework.getService(INVManager.class);
					parentObject = invManager.writeOffMovementIn(in, Env.getUserRrn());
					UI.showInfo(Message.getString("inv.in_write_off_successful"));
					itemWriteOff.setEnabled(false);
					refresh();
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	protected void previewAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			
			//保存打印次数
			MovementIn mi = (MovementIn)getParentObject();
			Long time = mi.getPrintTime();
			if(time == null){
				mi.setPrintTime(1L);
			}else{
				mi.setPrintTime(time + 1L);
			}
			ADManager manager = Framework.getService(ADManager.class);
			parentObject = manager.saveEntity(mi, Env.getUserRrn());
			
			String report = "oin_report.rptdesign";

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();

			if(!Movement.STATUS_APPROVED.equals(mi.getDocStatus())){
				UI.showWarning(Message.getString("common.is_not_approved")+","+Message.getString("common.can_not_print"));
				return;
			}
			if(mi == null){
				UI.showWarning(Message.getString("common.choose_one_record"));
				return;
			}
			Long objectRrn = mi.getObjectRrn();
			userParams.put("INV_OBJECT_RRN", String.valueOf(objectRrn));
				
			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
			dialog.open();
			refresh();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void createToolItemApprove(ToolBar tBar) {
		itemApprove = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_OIN_APPROVED);
		itemApprove.setText(Message.getString("common.approve"));
		itemApprove.setImage(SWTResourceCache.getImage("approve"));
		itemApprove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				approveAdapter();
			}
		});
	}

	protected void createToolItemGenerateLot(ToolBar tBar) {
		itemLot = new ToolItem(tBar, SWT.PUSH);
		itemLot.setText(Message.getString("inv.lot"));
		itemLot.setImage(SWTResourceCache.getImage("barcode"));
		itemLot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				lotAdapter();
			}
		});
	}

	protected void refreshAll(String status) {
		Form form = (Form) getDetailForms().get(0);
		IField fieldWarehouseRrn = (IField) form.getFields().get("warehouseRrn");
		IField fieldInId = (IField) form.getFields().get("docId");
		if (!(MovementIn.STATUS_DRAFTED.equals(status))) {
			if(itemGen != null) {
				itemGen.setEnabled(false);				
			}
			itemApprove.setEnabled(false);
			fieldWarehouseRrn.setEnabled(false);
			fieldInId.setEnabled(false);
		} else {
			if(itemGen != null) {
				itemGen.setEnabled(true);
			}
			itemApprove.setEnabled(true);
			fieldWarehouseRrn.setEnabled(true);
			fieldInId.setEnabled(true);
		}
		if(MovementIn.STATUS_APPROVED.equals(status)) {
			itemPreview.setEnabled(true);
		} else {
			itemPreview.setEnabled(false);
		}
		if(flag) {
			if(itemGen != null) {
				itemGen.setEnabled(false);
			}
			itemApprove.setEnabled(false);
			itemPreview.setEnabled(false);
			fieldWarehouseRrn.setEnabled(false);
			fieldInId.setEnabled(false);
		}
	}

	protected void approveAdapter() {
		try {
			boolean confirm = UI.showConfirm(Message.getString("common.approve_confirm"), Message.getString("common.title_confirm"));
			if(!confirm) return;
			form.getMessageManager().removeAllMessages();
			if (parentObject != null) {
				MovementIn in = (MovementIn) parentObject;
				INVManager invManager = Framework.getService(INVManager.class);
				parentObject = invManager.approveMovementIn(in,
						this.getInType(), Env.getUserRrn(), false);
				refreshAll(MovementIn.STATUS_APPROVED);
				oinLineProperties.setStatusChanged(MovementIn.STATUS_APPROVED);
				UI.showInfo(Message.getString("common.approve_successed"));
				refresh();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void lotAdapter() {
		try {
			MovementIn in = (MovementIn)parentObject;
			if (in != null && in.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				in = (MovementIn)adManager.getEntity(in);
				parentObject = in;
				List<MovementLine> lines = new ArrayList<MovementLine>();
				if (selectMovementLine != null) {
					selectMovementLine = (MovementLine)adManager.getEntity(selectMovementLine);					
				}
				lines = adManager.getEntityList(Env.getOrgRrn(), MovementLine.class, 
						Env.getMaxResult(), getWhereClause(), null);
				if((lines == null || lines.size() == 0) && selectMovementLine == null) 
					return;
				AdjustInLotDialog dialog = createInLotDialog(lines);
				dialog.setInType(getInType());
				if (dialog.open() == Dialog.CANCEL) {
					in = (MovementIn)adManager.getEntity((MovementIn)in);
					parentObject = in;
					selectMovementLine = null;
					this.viewer.setSelection(null);
					refresh();
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	protected AdjustInLotDialog createInLotDialog(List<MovementLine> lines) {
		return new AdjustInLotDialog(UI.getActiveShell(),
				(MovementIn)parentObject, selectMovementLine, lines, flag);
	}

	private void setMovementLineSelect(Object obj) {
		if (obj instanceof MovementLine) {
			selectMovementLine = (MovementLine) obj;
		} else {
			selectMovementLine = null;
		}
	}

	public boolean isEnableByParentObject() {
		MovementIn in = (MovementIn) this.getParentObject();
		if (in == null) {
			return false;
		}
		String status = in.getDocStatus();
		if (!(Requisition.STATUS_DRAFTED.equals(status))) {
			return false;
		}
		return true;
	}
	
	private void addLocatorListener() {
		IField targetWarehouseField = getIField(FieldName_Warehouse);
		targetWarehouseField.addValueChangeListener(getTargetWarehouseChangedListener());
	}
	
	private IField getIField(String fieldId) {
		IField f = null;
		for(Form form : getDetailForms()) {
			f = form.getFields().get(fieldId);
			if(f != null) {
				return f;
			}
		}
		return f;
	}
	
	private IValueChangeListener getTargetWarehouseChangedListener(){
		return new IValueChangeListener() {
			public void valueChanged(Object sender, Object newValue) {
				if(oinLineProperties != null){
					oinLineProperties.reputLocator(newValue);
				}
			}
		};
	};
	
	protected MovementIn.InType getInType() {
		return MovementIn.InType.ADIN;
	}
	
	protected ADTable getInvoiceMovementLineTable() throws Exception {
//		try {
			if (movementLineTable == null) {
				ADManager adManager = Framework.getService(ADManager.class);
				movementLineTable = adManager.getADTable(0L, TABLE_NAME_INVOICE_MOVEMENTLINE);
			}
			return movementLineTable;
//		} catch (Exception e1) {
//			ExceptionHandlerManager.asyncHandleException(e1);
//		}
	}
	
	@Override
	public void refresh() {
		super.refresh();
		if (parentObject instanceof MovementIn) {
			MovementIn parent = (MovementIn) parentObject;
			refreshToolItem(parent.getDocStatus());
		}
	}
	
	protected void refreshToolItem(String status) {
		if (!(MovementIn.STATUS_DRAFTED.equals(status))) {
			itemApprove.setEnabled(false);
		}
		if (MovementIn.STATUS_APPROVED.equals(status)) {
			if(itemWriteOff != null)
				itemWriteOff.setEnabled(true);
		} else {
			if(itemWriteOff != null)
				itemWriteOff.setEnabled(false);
		}
	}
}

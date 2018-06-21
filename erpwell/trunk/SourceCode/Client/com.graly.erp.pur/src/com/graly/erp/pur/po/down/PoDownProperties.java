package com.graly.erp.pur.po.down;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.calendar.BusinessCalendar;
import com.graly.erp.base.client.BASManager;
import com.graly.erp.base.model.Constants;
import com.graly.erp.inv.model.VUserWarehouse;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.erp.pur.client.PURManager;
import com.graly.erp.pur.po.copyfrom.ApprovedPRListDialog;
import com.graly.erp.pur.request.WarehouseChildEntityForm;
import com.graly.erp.vdm.model.VendorMaterial;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.ChildEntityProperties;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.CalendarField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.forms.field.SearchField;
import com.graly.framework.base.ui.forms.field.TextField;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class PoDownProperties extends ChildEntityProperties {
	private static final Logger	logger				= Logger.getLogger(PoDownProperties.class);
	private static final String	FieldName_Material	= "materialRrn";
	private static final String	FieldName_Vendor	= "vendorRrn";
	private static final String	FieldName_Purchaser	= "purchaser";
	private static final String	FieldName_UnitPrice	= "unitPrice";
	private static final String	FieldName_UomId		= "uomId";
	private static final String	FieldName_DateEnd	= "dateEnd";
	private static final String	TABLE_NAME			= "PURPurchaseOrder";

	protected ToolItem			itemClose;
	protected ToolItem			itemSave2;														// ֻ���浽�����ڡ�������
	protected ToolItem			itemOpenPR;
	private ADTable				adTable;
	private static String	AUTHORITY_UNITPRICE	= "PUR.PoLine.UnitPrice";

	public PoDownProperties() {
		super();
	}

	public PoDownProperties(EntityBlock masterParent, ADTable table, Object parentObject) {
		super(masterParent, table, parentObject);
	}

	@Override
	protected void createSectionContent(Composite client) {
		final FormToolkit toolkit = form.getToolkit();
		final IMessageManager mmng = form.getMessageManager();
		setTabs(new CTabFolder(client, SWT.FLAT | SWT.TOP));
		getTabs().marginHeight = 10;
		getTabs().marginWidth = 5;
		toolkit.adapt(getTabs(), true, true);
		GridData gd = new GridData(GridData.FILL_BOTH);
		getTabs().setLayoutData(gd);
		Color selectedColor = toolkit.getColors().getColor(FormColors.SEPARATOR);
		getTabs().setSelectionBackground(new Color[] { selectedColor,
				toolkit.getColors().getBackground() }, new int[] { 50 });
		toolkit.paintBordersFor(getTabs());

		for (ADTab tab : getTable().getTabs()) {
			CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
			item.setText(I18nUtil.getI18nMessage(tab, "label"));
			WarehouseChildEntityForm itemForm = new WarehouseChildEntityForm(getTabs(), SWT.NONE, null, tab, mmng, parentObject);
			getDetailForms().add(itemForm);
			item.setControl(itemForm);
		}

		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}
		if (parentObject != null) {
			loadFromParent();
		}

		addMaterialAndVendorListener();
	}

	public void refresh() {
		try {
			if (getAdObject() == null) {
				this.setAdObject(createAdObject());
			}
			PurchaseOrderLine poLine = (PurchaseOrderLine) getAdObject();
			IField materialField = getIField(FieldName_Material);
			IField uomId = getIField(FieldName_UomId);
			if (poLine.getRequisitionLineRrn() != null) {
				materialField.setEnabled(false);
				uomId.setEnabled(false);
			} else {
				materialField.setEnabled(true);
				uomId.setEnabled(true);
			}
			super.refresh();
			IField unitPriceField = getIField(FieldName_UnitPrice);
			if(!Env.getAuthority().contains(AUTHORITY_UNITPRICE)){//���û�м۸�Ȩ�ޣ����޷��������ۣ�Ҳ�޷��ĵ���
				unitPriceField.setValue("-");
				unitPriceField.setEnabled(false);
				unitPriceField.refresh();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}

	protected ADTable getADTableOfPO() {
		try {
			if (adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, TABLE_NAME);
				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			}
			return adTable;
		} catch (Exception e) {
			logger.error("RequisitionLineDialog : initAdTableOfBom()", e);
		}
		return null;
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
//		createToolItemOpenPR(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
//	createToolItemNew(tBar);
	//	createToolItemSave(tBar);
	//	createToolItemSave2(tBar);
	//	new ToolItem(tBar, SWT.SEPARATOR);
	//	createToolItemDelete(tBar);
	//	new ToolItem(tBar, SWT.SEPARATOR);
	//	createToolItemRefresh(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);

//		createToolItemClosePOLine(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemSave2(ToolBar tBar) {
		itemSave2 = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_POLINE_SAVE2);
		itemSave2.setText("�޸�");
		itemSave2.setImage(SWTResourceCache.getImage("edit"));
		itemSave2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				save2Adapter();
			}
		});
	}

	protected void save2Adapter() {
		try {
			PurchaseOrderLine poLine = new PurchaseOrderLine();
			boolean saveFlag = true;
			for (Form detailForm : getDetailForms()) {
				if (!detailForm.saveToObject()) {
					saveFlag = false;
					break;
				}
			}
			if (saveFlag) {
				for (Form detailForm : getDetailForms()) {
					PropertyUtil.copyProperties(poLine, detailForm.getObject(), detailForm.getFields());
				}

				PurchaseOrderLine object = (PurchaseOrderLine) getAdObject();
				ADManager adManager = Framework.getService(ADManager.class);
				object = (PurchaseOrderLine) adManager.getEntity(object);
				object.setDateEnd(poLine.getDateEnd());// ���浽������
				object.setUrgency(poLine.getUrgency());// ���������
				object.setDatePromised(poLine.getDatePromised());//�������µ�������
				adManager.saveEntity(object, Env.getUserRrn());
				this.setAdObject(adManager.getEntity(object));
				// ��ʾ����ɹ�
				UI.showInfo(Message.getString("common.save_successed"));
				refresh();
				this.getMasterParent().refresh();
				changedMasterParentStatus();
			}
		} catch (Exception e) {
			logger.error("POLineProperties : save2Adapter()", e);
		}
	}

	protected void createToolItemClosePOLine(ToolBar tBar) {
		itemClose = new ToolItem(tBar, SWT.PUSH);
		itemClose.setText(Message.getString("common.close"));
		itemClose.setImage(SWTResourceCache.getImage("close"));
		itemClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				closePOLineAdapter();
			}
		});
	}

	protected void createToolItemOpenPR(ToolBar tBar) {
		itemOpenPR = new ToolItem(tBar, SWT.PUSH);
		itemOpenPR.setText(Message.getString("pur.copyfrom"));
		itemOpenPR.setImage(SWTResourceCache.getImage("copy"));
		itemOpenPR.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				openPRAdapter();
			}
		});
	}

	protected void closePOLineAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null && getAdObject().getObjectRrn() != null) {
				if (UI.showConfirm(Message.getString("common.confirm_repeal"))) {
					PURManager pudManager = Framework.getService(PURManager.class);
					PurchaseOrderLine poLine = pudManager.closePOLine((PurchaseOrderLine) getAdObject(), Env.getUserRrn());
					setAdObject(poLine);
					// ���»�ȡPO, ˢ��PO״̬(��Ϊ�ر�POLine���ܻ�����PO״̬�ı�)
					if (poLine.getPoRrn() != null) {
						PoDownLineEntityBlock poLineBlock = (PoDownLineEntityBlock) getMasterParent();
						PurchaseOrder po = new PurchaseOrder();
						po.setObjectRrn(poLine.getPoRrn());
						ADManager adManager = Framework.getService(ADManager.class);
						poLineBlock.setParentObject(adManager.getEntity(po));
						getMasterParent().setWhereClause(" poRrn = '"
								+ po.getObjectRrn() + "'");
					}
					UI.showInfo(Message.getString("common.close_successed"));
					refresh();
					getMasterParent().refresh();
					changedMasterParentStatus();
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void openPRAdapter() {
		form.getMessageManager().removeAllMessages();
		PoDownLineEntityBlock poLineBlock = (PoDownLineEntityBlock) getMasterParent();
		PurchaseOrder po = (PurchaseOrder) poLineBlock.getParentObject();
		ApprovedPRListDialog apd = new ApprovedPRListDialog(UI.getActiveShell(), po);
		if (apd.open() == Dialog.CANCEL) {
			poLineBlock.setParentObject(apd.getPo());
			if (apd.getPo().getObjectRrn() != null) {
				poLineBlock.setWhereClause(" poRrn = '"
						+ apd.getPo().getObjectRrn() + "'");
			}
		}
		refresh();
		getMasterParent().refresh();
		changedMasterParentStatus();
	}

	protected void saveAdapter() {
		try {
			form.getMessageManager().setAutoUpdate(false);
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				PoDownLineEntityBlock poLineBlock = (PoDownLineEntityBlock) getMasterParent();
				if (!poLineBlock.saveParent()) {
					form.getMessageManager().setAutoUpdate(true);
					return; // �жϱ��游�����Ƿ�ɹ������򷵻�
				}

				// if (!poLineBlock.validatePayRules()){
				// return;
				// }
				boolean saveFlag = true;
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
						break;
					}
				}
				if (saveFlag) {
					for (Form detailForm : getDetailForms()) {
						PropertyUtil.copyProperties(getAdObject(), detailForm.getObject(), detailForm.getFields());
					}
					setPOlineTotal(); // ����POLine.lineTotal
					PurchaseOrderLine poLine = (PurchaseOrderLine) getAdObject();
					PurchaseOrder po = (PurchaseOrder) poLineBlock.getParentObject();

					// ����savePOLine()����
					PURManager purManager = Framework.getService(PURManager.class);
					poLine = purManager.savePOLine(po, poLine, Env.getUserRrn());
					// ˢ�¸��Ӷ��󣬲����¸������whereClause
					po = new PurchaseOrder();
					ADManager adManager = Framework.getService(ADManager.class);
					po.setObjectRrn(poLine.getPoRrn());
					po = (PurchaseOrder) adManager.getEntity(po);
					poLineBlock.setParentObject(po);
					this.setParentObject(po);
					this.setAdObject(adManager.getEntity(poLine));
					getMasterParent().setWhereClause(" poRrn = '"
							+ po.getObjectRrn() + "'");
					// ��ʾ����ɹ�
					UI.showInfo(Message.getString("common.save_successed"));
					refresh();
					this.getMasterParent().refresh();
					changedMasterParentStatus();
				}
			}
			form.getMessageManager().setAutoUpdate(true);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void setPOlineTotal() {
		PurchaseOrderLine poLine = (PurchaseOrderLine) getAdObject();
		if (poLine.getQty() == null || poLine.getUnitPrice() == null) {
			poLine.setLineTotal(BigDecimal.ZERO);
		} else {
			poLine.setLineTotal(poLine.getQty().multiply(poLine.getUnitPrice()));
		}
	}

	protected void changedMasterParentStatus() {
		PoDownLineEntityBlock poLineBlock = (PoDownLineEntityBlock) getMasterParent();
		poLineBlock.setParentObjectStatusChanged();
	}

	protected void deleteAdapter() {
		if (!PurchaseOrder.STATUS_DRAFTED.equals(((PurchaseOrderLine) getAdObject()).getLineStatus())) {
			return;
		}
		try {
			boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
			if (confirmDelete) {
				if (getAdObject().getObjectRrn() != null) {
					PURManager purManager = Framework.getService(PURManager.class);
					purManager.deletePOLine((PurchaseOrderLine) getAdObject(), Env.getUserRrn());
					setAdObject(createAdObject());
					refresh();
					// ɾ��poLine�����po�������,����Ҫ����ˢ�»��po��֪ͨMaster��ť״̬�ı�
					PoDownLineEntityBlock block = (PoDownLineEntityBlock) getMasterParent();
					PurchaseOrder mm = (PurchaseOrder) block.getParentObject();
					ADManager adManager = Framework.getService(ADManager.class);
					mm = (PurchaseOrder) adManager.getEntity(mm);
					block.setParentObject(mm);
					this.setParentObject(mm);
					getMasterParent().refresh();
					changedMasterParentStatus();
				}
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}
	}

	@Override
	public ADBase createAdObject() throws Exception {
		PurchaseOrderLine poLine = null;
		try {
			PoDownLineEntityBlock block = (PoDownLineEntityBlock) this.getMasterParent();
			if (block.isEnableByParentObject()) {
				PURManager purManager = Framework.getService(PURManager.class);
				PoDownLineEntityBlock poLineBlock = (PoDownLineEntityBlock) getMasterParent();
				PurchaseOrder po = (PurchaseOrder) poLineBlock.getParentObject();
				poLine = purManager.newPOLine((PurchaseOrder) poLineBlock.getParentObject());

				ADManager adManager = Framework.getService(ADManager.class);
				String whereClause = "VUserWarehouse.userRrn = "
						+ Env.getUserRrn();
				List<VUserWarehouse> wHouses = adManager.getEntityList(Env.getOrgRrn(), VUserWarehouse.class, Integer.MAX_VALUE, whereClause, null);
				for (VUserWarehouse whouse : wHouses) {
					if ("Y".equals(whouse.getIsDefault())) {
						po.setWarehouseRrn(whouse.getObjectRrn());// ����Ĭ�ϲֿ�
						poLineBlock.setParentObject(po);
						poLineBlock.refresh();
						break;
					}
				}
			} else {
				poLine = new PurchaseOrderLine();
			}
			poLine.setUrgency(PurchaseOrderLine.URGENCY_NORMAL);// Ĭ�Ͻ�����Ϊ����
			poLine.setOrgRrn(Env.getOrgRrn());
			return poLine;
		} catch (Exception e) {
			logger.error("Error at POLineProperties : createAdObject()" + e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return poLine;
	}

	private void addMaterialAndVendorListener() {
		IField f = getIFieldFromParent(FieldName_Vendor); // ��MaseterParent�л�ù�Ӧ��IField
		IField materialField = getIField(FieldName_Material);

		// Ϊ��Ӧ��������ϼ�����,������PO(SearchField)��POLine(RefTableField)֮�����Ϣ�����ó���ʵ��
		if (materialField instanceof RefTableField) {
			SearchField sf = (SearchField) f;
			RefTableField rtf = (RefTableField) materialField;
			sf.addValueChangeListener(rtf);

			rtf.valueChanged(f, sf.getData());
		}
		// Ϊ������Ӽ�����,�����ϸı�ʱ����ȷ�����ۺ͵�λ�ĸı�
		if (materialField != null) {
			materialField.addValueChangeListener(getMaterialChangedListener());
		}
	}

	private IField getIFieldFromParent(String fieldId) {
		IField f = null;
		PoDownLineEntityBlock poLineBlock = (PoDownLineEntityBlock) getMasterParent();
		for (Form form : poLineBlock.getDetailForms()) {
			f = form.getFields().get(fieldId);
			if (f != null) {
				return f;
			}
		}
		return f;
	}

	private IField getIField(String fieldId) {
		IField f = null;
		for (Form form : getDetailForms()) {
			f = form.getFields().get(fieldId);
			if (f != null) {
				return f;
			}
		}
		return f;
	}

	private IValueChangeListener getMaterialChangedListener() {
		return new IValueChangeListener() {
			public void valueChanged(Object sender, Object newValue) {
				// ���ݹ�Ӧ�̡�����ȷ������
				changedUnitPriceValue(sender, newValue);
				// ��������,�ı����ϵ�λ,��δʵ��
				if (sender instanceof RefTableField) {
					refreshUomId(sender, newValue);
					refreshPurchase(sender, newValue);
				}
			}
		};
	};

	protected void refreshPurchase(Object sender, Object newValue) {
		// ˢ��POLineEntityBlockҳ���ϵĲɹ�Ա
		PoDownLineEntityBlock poLineBlock = (PoDownLineEntityBlock) getMasterParent();
		IField ifield = poLineBlock.getIField(FieldName_Purchaser);
		if (ifield != null && ifield instanceof TextField) {
			TextField rtf = (TextField) ifield;

			RefTableField mrField = (RefTableField) sender;
			VendorMaterial vm = (VendorMaterial) mrField.getData();
			if (vm != null) {
				rtf.setValue(vm.getPurchaser());
			} else {
				// rtf.setValue(null);
			}
			rtf.refresh();
		}
	}

	private void changedUnitPriceValue(Object sender, Object newValue) {
		IField ifield = getIField(FieldName_UnitPrice);
		IField ifieldDateEnd = getIField(FieldName_DateEnd);

		RefTableField refField = (RefTableField) sender;
		VendorMaterial vm = (VendorMaterial) refField.getData();

		if (ifield instanceof TextField) {
			TextField tf = (TextField) ifield;
			if (vm != null) {
				if (vm.getLastPrice() != null) {
					tf.setValue(vm.getLastPrice().toString());// �����ϴμ۸�
				} else if (vm.getReferencedPrice() != null) {
					tf.setValue(vm.getReferencedPrice().toString());
				} else {
					tf.setValue("");
				}
			} else
				tf.setValue("");
			tf.refresh();
		}

		if (ifieldDateEnd instanceof CalendarField) {
			CalendarField calendarField = (CalendarField) ifieldDateEnd;
			if (vm != null && vm.getLeadTime() != null) {
				BASManager basManager;
				try {
					basManager = Framework.getService(BASManager.class);
					BusinessCalendar prCalendar = basManager.getCalendarByDay(Env.getOrgRrn(), BusinessCalendar.CALENDAR_PURCHASE);
					Date now = Env.getSysDate();
					Date dateStart = prCalendar.findStartOfNextDay(now);
					int leadTime = Integer.parseInt(vm.getLeadTime().toString());
					Date dateEnd = prCalendar.addDay(dateStart, leadTime);
					calendarField.setValue(dateEnd);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else
				calendarField.setValue(null);
			calendarField.refresh();
		}
	}

	private void refreshUomId(Object sender, Object newValue) {
		IField ifield = getIField(FieldName_UomId);
		if (ifield instanceof RefTableField) {
			RefTableField rtf = (RefTableField) ifield;

			RefTableField mrField = (RefTableField) sender;
			VendorMaterial vm = (VendorMaterial) mrField.getData();
			if (vm != null) {
				rtf.setValue(vm.getMaterial().getInventoryUom());
			} else {
				rtf.setValue(null);
			}
			rtf.refresh();
		}
	}

	@Override
	public void setAdObject(ADBase adObject) {
		this.adObject = adObject;
		PurchaseOrderLine poLine = (PurchaseOrderLine) adObject;
		if (poLine != null) {
			setStatusChanged(poLine.getLineStatus());
		} else {
			setStatusChanged("");
		}
	}

	protected void setStatusChanged(String status) {
//		if (PoDownLineEntityBlock.flag) {
//			itemOpenPR.setEnabled(false);
//			itemNew.setEnabled(false);
//			itemSave.setEnabled(false);
//			itemDelete.setEnabled(false);
//			itemRefresh.setEnabled(false);
//			itemClose.setEnabled(false);
//			itemSave2.setEnabled(false);
//		} else if (PurchaseOrder.STATUS_DRAFTED.equals(status)) {
//			itemOpenPR.setEnabled(true);
//			itemNew.setEnabled(true);
//			itemSave.setEnabled(true);
//			itemDelete.setEnabled(true);
//			itemClose.setEnabled(false);
//			itemSave2.setEnabled(false);
//		} else if (PurchaseOrder.STATUS_APPROVED.equals(status)) {
//			itemOpenPR.setEnabled(false);
//			itemNew.setEnabled(false);
//			itemSave.setEnabled(false);
//			itemDelete.setEnabled(false);
//			itemClose.setEnabled(true);
//			itemSave2.setEnabled(true);
//		} else {
//			itemOpenPR.setEnabled(false);
//			itemNew.setEnabled(false);
//			itemSave.setEnabled(false);
//			itemDelete.setEnabled(false);
//			itemClose.setEnabled(false);
//			itemSave2.setEnabled(false);
//		}
	}
}

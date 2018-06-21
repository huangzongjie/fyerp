package com.graly.erp.xz.pur.po;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.calendar.BusinessCalendar;
import com.graly.erp.base.client.BASManager;
import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Material;
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
import com.graly.framework.base.ui.forms.field.SeparatorField;
import com.graly.framework.base.ui.forms.field.TextField;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class XZPOLineProperties extends ChildEntityProperties {
	private static final Logger	logger				= Logger.getLogger(XZPOLineProperties.class);
	private static final String	FieldName_Material	= "materialRrn";
	private static final String	FieldName_Vendor	= "vendorRrn";
	private static final String	FieldName_Purchaser	= "purchaser";
	private static final String	FieldName_UnitPrice	= "unitPrice";
	private static final String	FieldName_UomId		= "uomId";
	private static final String	FieldName_PackageSpec	= "packageSpec";
	private static final String	FieldName_DateEnd	= "dateEnd";			//��������
	private static final String FieldName_Urgency   = "urgency";			//������
	private static final String FieldName_DatePromised = "datePromised";	//���µ�������
	private static final String FieldName_LineTotal = "lineTotal";
	private static final String	TABLE_NAME			= "BJPURPurchaseOrder";

	protected ToolItem			itemClose;
	protected ToolItem			itemSave2;														// ֻ���浽�����ڡ������ȣ����µ�������
	protected ToolItem			itemLineTotalSave;
	protected ToolItem			itemReceiptDate;
	private ADTable				adTable;
	private static String	AUTHORITY_UNITPRICE	= "PUR.PoLine.UnitPrice";
	private static String   AUTHORITY_LINETOTAL = "PUR.PoLine.LineTotal"; 
	private static String   AUTHORITY_REVOKE 	= "PUR.Po.Revoke";
	private int  Operation_Flag = 1;//1:�����¼�  2:�޸ĺ󱣴��¼�  3:���ܼۺ󱣴��¼�

	public XZPOLineProperties() {
		super();
	}

	public XZPOLineProperties(EntityBlock masterParent, ADTable table, Object parentObject) {
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
			//2012-03-07 Simon  ����˺�ˢ�£����ϲ�������
//			PurchaseOrderLine poLine = (PurchaseOrderLine) getAdObject();
//			IField materialField = getIField(FieldName_Material);
//			IField uomId = getIField(FieldName_UomId);
//			if (poLine.getRequisitionLineRrn() != null) {
//				materialField.setEnabled(false);
//				uomId.setEnabled(false);
//			} 
//			else {
//				materialField.setEnabled(true);
//				uomId.setEnabled(true);
//			}
			super.refresh();
			IField unitPriceField = getIField(FieldName_UnitPrice);
			if(!Env.getAuthority().contains(AUTHORITY_UNITPRICE)){//���û�м۸�Ȩ�ޣ����޷��������ۣ�Ҳ�޷��ĵ���
				unitPriceField.setValue("-");
				unitPriceField.setEnabled(false);
				unitPriceField.refresh();
			}
			//�������ܼ��ı��򲻿ɱ༭
			IField linetotalField = getIField(FieldName_LineTotal);
			linetotalField.setEnabled(false);
			linetotalField.refresh();
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
//		createToolItemReceiptDate(tBar);
//		new ToolItem(tBar, SWT.SEPARATOR);
//		createToolItemOpenPR(tBar);
//		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemLineTotalSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemNew(tBar);
		createToolItemSave(tBar);
		createToolItemSave2(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);

		createToolItemClosePOLine(tBar);//����
		section.setTextClient(tBar);
	}

	protected void createToolItemReceiptDate(ToolBar tBar) {
		itemReceiptDate = new ToolItem(tBar, SWT.PUSH);
		itemReceiptDate.setText(Message.getString("pur.change_receipt_date"));
		itemReceiptDate.setImage(SWTResourceCache.getImage("edit"));
		itemReceiptDate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				receiptDateAdapter();
			}
		});
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

	protected void createToolItemLineTotalSave(ToolBar tBar)
	{
		itemLineTotalSave = new AuthorityToolItem(tBar, SWT.PUSH,AUTHORITY_LINETOTAL);//�ж��û��Ƿ��и�Ȩ�ް�ť
		itemLineTotalSave.setText("���ܼ�");
		itemLineTotalSave.setImage(SWTResourceCache.getImage("edit"));
		itemLineTotalSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				LineTotalSaveAdapter();
			}
		});
	}

	protected void receiptDateAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				boolean saveFlag = true;
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
						break;
					}
				}
				if (saveFlag) {
					for (Form detailForm : getDetailForms()) {
						copyProperties(getAdObject(), detailForm.getObject(), detailForm.getFields());
					}
					PurchaseOrderLine poLine = (PurchaseOrderLine) getAdObject();
					ADManager adManager = Framework.getService(ADManager.class);
					if(poLine.getObjectRrn()!=null){
						PurchaseOrderLine oldPoLine = new PurchaseOrderLine();
						oldPoLine.setObjectRrn(poLine.getObjectRrn());
						oldPoLine = (PurchaseOrderLine) adManager.getEntity(oldPoLine);
						oldPoLine.setReceiptDate(poLine.getReceiptDate());
						oldPoLine.setReceiptDateHour(poLine.getReceiptDateHour());
						adManager.saveEntity(oldPoLine, Env.getUserRrn());
						UI.showInfo(Message.getString("common.save_successed"));
					} 
					
					refresh();
					this.getMasterParent().refresh();
//					changedMasterParentStatus();
				}
			}
			form.getMessageManager().setAutoUpdate(true);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void save2Adapter() {
		//���ò�����ʶΪ �޸ĺ󱣴�
		Operation_Flag = 2;
		//���������༭״̬Ϊfalse
		for (Form form : getDetailForms()) {
			Iterator<Entry<String,IField>> iter = form.getFields().entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry<String,IField> element=(Map.Entry<String,IField>)iter.next();
				IField f =(IField)element.getValue();
				if(element.getKey().toString().equals(FieldName_Urgency) || 
						element.getKey().toString().equals(FieldName_DateEnd) || 
						element.getKey().toString().equals(FieldName_DatePromised)){
					f.setEnabled(true);
				}
				else{
					f.setEnabled(false);
				}
			}
		}
		//���ð�ť״̬Ϊ������
		itemClose.setEnabled(false);//����
		itemRefresh.setEnabled(false);//ˢ��
		itemLineTotalSave.setEnabled(false);//���ܼ�
		itemSave.setEnabled(true);
	}

	protected void LineTotalSaveAdapter()
	{
		//���ò�����ʶΪ ���ܼۺ󱣴�
		Operation_Flag = 3;
		//���������༭״̬Ϊfalse
		for (Form form : getDetailForms()) {
			Iterator<Entry<String,IField>> iter = form.getFields().entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry<String,IField> element=(Map.Entry<String,IField>)iter.next();
				IField f =(IField)element.getValue();
				if(!element.getKey().toString().equals(FieldName_LineTotal) ){
					f.setEnabled(false);
				}
				else{
					f.setEnabled(true);
				}
			}
		}
		//���ð�ť�ؼ�Ϊfalse
//		itemOpenPR.setEnabled(false);//������
		itemClose.setEnabled(false);//����
		itemSave2.setEnabled(false);//�޸�
		itemNew.setEnabled(false);//�½�
		itemDelete.setEnabled(false);//ɾ��
		itemRefresh.setEnabled(false);//ˢ��
		itemSave.setEnabled(true);//����
		
		IField linetotalField = getIField(FieldName_LineTotal);
		linetotalField.setEnabled(true);
		linetotalField.refresh();
	}
	
	protected void createToolItemClosePOLine(ToolBar tBar) {
		itemClose = new AuthorityToolItem(tBar, SWT.PUSH, AUTHORITY_REVOKE);//new ToolItem(tBar, SWT.PUSH);
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
//		itemOpenPR = new ToolItem(tBar, SWT.PUSH);
//		itemOpenPR.setText(Message.getString("pur.copyfrom"));
//		itemOpenPR.setImage(SWTResourceCache.getImage("copy"));
//		itemOpenPR.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent event) {
//				openPRAdapter();
//			}
//		});
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
						XZPOLineEntityBlock poLineBlock = (XZPOLineEntityBlock) getMasterParent();
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
		XZPOLineEntityBlock poLineBlock = (XZPOLineEntityBlock) getMasterParent();
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
				XZPOLineEntityBlock poLineBlock = (XZPOLineEntityBlock) getMasterParent();
				switch(Operation_Flag){
				case 1://��ͨ����,���游�ڵ�
					if (!poLineBlock.saveParent()) {
						form.getMessageManager().setAutoUpdate(true);
						return; // �жϱ��游�����Ƿ�ɹ������򷵻�
					}
					break;
				case 2://�޸ĺ󱣴�
					break;
				case 3://���ܼۺ󱣴�
					break;
					default:
						break;
				}
				
				boolean saveFlag = true;
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
						break;
					}
				}
				if (saveFlag) {
					for (Form detailForm : getDetailForms()) {
						copyProperties(getAdObject(), detailForm.getObject(), detailForm.getFields());
					}
					switch(Operation_Flag){
					case 1://��ͨ����
						setPOlineTotal(); // ����POLine.lineTotal
						break;
					case 2://�޸ĺ󱣴�	
						setIFieldStatus(true);
						break;
					case 3://���ܼۺ󱣴�
						setIFieldStatus(true);
						break;
					default:
						break;
					}
					//��ʶ��λ
					Operation_Flag = 1;
					PurchaseOrderLine poLine = (PurchaseOrderLine) getAdObject();
					PurchaseOrder po = (PurchaseOrder) poLineBlock.getParentObject();
					ADManager adManager = Framework.getService(ADManager.class);
					if(poLine.getObjectRrn()!=null){
						PurchaseOrderLine oldPoLine = new PurchaseOrderLine();
						oldPoLine.setObjectRrn(poLine.getObjectRrn());
						oldPoLine = (PurchaseOrderLine) adManager.getEntity(oldPoLine);
						if(!poLine.getMaterialRrn().equals(oldPoLine.getMaterialRrn())){
							UI.showError("�¾����ϲ�һ�£�����������ϣ���ɾ�������Ϻ����½���");
							return;
						}
					} 
					// ����savePOLine()����
					PURManager purManager = Framework.getService(PURManager.class);
					poLine = purManager.savePOLine(po, poLine, Env.getUserRrn());
					// ˢ�¸��Ӷ��󣬲����¸������whereClause
					po = new PurchaseOrder();
					
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

	private void copyProperties(Object destBean, Object sourceBean, LinkedHashMap<String, IField> fields){
		PurchaseOrderLine poLine = (PurchaseOrderLine)destBean;
		String datehis = poLine.getDateHisPromised() == null ? "" : poLine.getDateHisPromised() ;
		for (String name : fields.keySet()){
			try {
				if(name != null && !"".equals(name)){
					IField f = fields.get(name);
					if (!(f instanceof SeparatorField)){
						Object obj = PropertyUtils.getProperty(sourceBean, name);
						if(name.equals("datePromised")){
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
							if(datehis == ""){
								if(obj!=null) datehis = sdf.format((Date)obj);
							}
							else{
								datehis += "/" + sdf.format((Date)obj);
							}
							poLine.setDateHisPromised(datehis);
						}
						PropertyUtils.setProperty(destBean, name, obj);					
					}
				}
				
			} catch (Exception e) {
				logger.error("PropertyUtil : copyProperties ", e);
			}
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
		XZPOLineEntityBlock poLineBlock = (XZPOLineEntityBlock) getMasterParent();
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
					XZPOLineEntityBlock block = (XZPOLineEntityBlock) getMasterParent();
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
			XZPOLineEntityBlock block = (XZPOLineEntityBlock) this.getMasterParent();
			if (block.isEnableByParentObject()) {
				PURManager purManager = Framework.getService(PURManager.class);
				XZPOLineEntityBlock poLineBlock = (XZPOLineEntityBlock) getMasterParent();
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
		XZPOLineEntityBlock poLineBlock = (XZPOLineEntityBlock) getMasterParent();
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
					refreshPackageSpec(sender, newValue);
					refreshPurchase(sender, newValue);
				}
			}
		};
	};

	protected void refreshPackageSpec(Object sender, Object newValue) {
		IField ifield = getIField(FieldName_PackageSpec);
		if (ifield instanceof TextField) {
			TextField txtf = (TextField) ifield;

			RefTableField mrField = (RefTableField) sender;
			VendorMaterial vm = (VendorMaterial) mrField.getData();
			
			if (vm != null) {
				Material mater = vm.getMaterial();
				if(mater != null && mater.getPackageSpec() != null && mater.getPackageSpec().trim().length() > 0){
					txtf.setValue(mater.getPackageSpec());
				}else{
					txtf.setValue(vm.getPackageSpec());
				}
			} else {
				txtf.setValue(null);
			}
			txtf.refresh();
		}
	}

	protected void refreshPurchase(Object sender, Object newValue) {
		// ˢ��BJPOLineEntityBlockҳ���ϵĲɹ�Ա
		XZPOLineEntityBlock poLineBlock = (XZPOLineEntityBlock) getMasterParent();
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
		if (XZPOLineEntityBlock.flag) {
//			itemOpenPR.setEnabled(false);
			itemNew.setEnabled(false);
			itemSave.setEnabled(false);
			itemDelete.setEnabled(false);
			itemRefresh.setEnabled(false);
			itemClose.setEnabled(false);
			itemSave2.setEnabled(false);
			itemLineTotalSave.setEnabled(false);
		} else if (PurchaseOrder.STATUS_DRAFTED.equals(status)) {
//			itemOpenPR.setEnabled(true);
			itemNew.setEnabled(true);
			itemSave.setEnabled(true);
			itemDelete.setEnabled(true);
			itemClose.setEnabled(false);
			itemSave2.setEnabled(false);
			if(Env.getAuthority().contains(AUTHORITY_LINETOTAL)){
				itemLineTotalSave.setEnabled(true);
			}
			setIFieldStatus(true);
		} else if (PurchaseOrder.STATUS_APPROVED.equals(status)) {
//			itemOpenPR.setEnabled(false);
			itemNew.setEnabled(false);
			itemSave.setEnabled(false);
			itemDelete.setEnabled(false);
			if(Env.getAuthority().contains(AUTHORITY_REVOKE)){//����Ȩ��
				itemClose.setEnabled(true);
			}
			if(Env.getAuthority().contains(Constants.KEY_POLINE_SAVE2)){
				itemSave2.setEnabled(true);
			}
			if(Env.getAuthority().contains(AUTHORITY_LINETOTAL)){
				itemLineTotalSave.setEnabled(true);
			}
			setIFieldStatus(false);
		} else {
//			itemOpenPR.setEnabled(false);
			itemNew.setEnabled(false);
			itemSave.setEnabled(false);
			itemDelete.setEnabled(false);
			itemClose.setEnabled(false);
			itemSave2.setEnabled(false);
			itemLineTotalSave.setEnabled(false);
		}
	}

	private void setIFieldStatus(boolean b ){
		IField f = null;
		f = getIField("lineNo");//�к�
		f.setEnabled(b);
		f = getIField("materialRrn");//���ϱ��
		f.setEnabled(b);
		f = getIField("qty");//������
		f.setEnabled(b);
		f = getIField("unitPrice");//����
		f.setEnabled(b);
		f = getIField("urgency");//������
		f.setEnabled(b);
		f = getIField("dateEnd");//��������
		f.setEnabled(b);
		f = getIField("datePromised");//���µ�������
		f.setEnabled(b);
	}
}

class BrowserDialog extends TrayDialog{
	protected String url;
	private static int MIN_DIALOG_WIDTH = 700;
	private static int MIN_DIALOG_HEIGHT = 500;
	
	public BrowserDialog(Shell parentShell, String url) {
		super(parentShell);
		this.url = url;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Browser browser = new Browser(parent,SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
        gd.minimumWidth = MIN_DIALOG_WIDTH ;
        gd.minimumHeight = MIN_DIALOG_HEIGHT ;
        browser.setUrl(url);
        browser.setLayoutData(gd);
		browser.setUrl(url);
		return browser;
	}
	
	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
						shellSize.y));
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {}
	
}

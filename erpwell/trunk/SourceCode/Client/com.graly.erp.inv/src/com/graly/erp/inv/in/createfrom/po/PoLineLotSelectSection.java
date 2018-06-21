package com.graly.erp.inv.in.createfrom.po;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.StringUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class PoLineLotSelectSection {
	private static final Logger logger = Logger.getLogger(PoLineLotSelectSection.class);
	private String VALUE_FIELD_NAME = "objectRrn";
	private String KEY_FIELD_WAREHOUSEID = "warehouseId";
	private String KEY_FIELD_MATERIALNAME = "materialName";
	private String TableName_VUser_Warehouse = "VUserWarehouse";
	
	private PoLineLotSelectPage parentPage;
	private ADTable adTableLot;
	private ADTable adTablePoLine;
	private ManagedForm form;
	private TableListManager tableManager;
	private CheckboxTableViewer viewer;
	private List<Lot> lotOfPoLine ;
	private Text txtLotId;
	protected List<Lot> lots = new ArrayList<Lot>();
	protected RefTableField refField_PoLine, refField_Warehouse;
	private PurchaseOrder po;
	private List<PurchaseOrderLine> poLineList;

	public PoLineLotSelectSection(ADTable table, PoLineLotSelectPage parentPage, PoCreateWizard wizard, ADTable adTablePoLine) {
		this.adTableLot = table;
		this.parentPage = parentPage;
		this.po = wizard.getContext().getPo();
		this.poLineList = wizard.getContext().getPoLines();
		this.adTablePoLine = adTablePoLine;
	}
	
	public void createContents(ManagedForm form, Composite parent) {
		this.form = form;
		parent.setLayout(new GridLayout(1, false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.createSectionContent(parent);
	}

	protected void createSectionContent(Composite client) {
		try {
			FormToolkit toolkit = form.getToolkit();
			Composite composit = toolkit.createComposite(client, SWT.BORDER);
			composit.setLayout(new GridLayout(1, false));
			composit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			Composite compPoList = toolkit.createComposite(composit, SWT.NONE);
			compPoList.setLayout(new GridLayout(4, false));
			compPoList.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			/* 创建POLINE控件*/
			ADRefTable refTablePoList = new ADRefTable();
			refTablePoList.setKeyField(VALUE_FIELD_NAME);
			refTablePoList.setValueField(KEY_FIELD_MATERIALNAME);
			refTablePoList.setWhereClause("");
			refTablePoList.setTableRrn(adTablePoLine.getObjectRrn());
			TableListManager tlm = new TableListManager(adTablePoLine);
			TableViewer viewerPoList = (TableViewer)tlm.createViewer(UI.getActiveShell(), toolkit);
			viewerPoList.setInput(poLineList);
			refField_PoLine = new RefTableField(KEY_FIELD_MATERIALNAME, viewerPoList, refTablePoList, SWT.BORDER | SWT.READ_ONLY);
			refField_PoLine.setLabel(Message.getString("inv.in_form_po_poline") + " *");
			refField_PoLine.createContent(compPoList, toolkit);
			refField_PoLine.addValueChangeListener(getPoLineChangedListener());
			
			Composite comp = toolkit.createComposite(composit, SWT.NONE);
			comp.setLayout(new GridLayout(4, false));
			comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			Label label = toolkit.createLabel(comp, Message.getString("inv.lotid"));
			label.setForeground(SWTResourceCache.getColor("Folder"));
			label.setFont(SWTResourceCache.getFont("Verdana"));
			txtLotId = toolkit.createText(comp, "", SWT.BORDER);
			txtLotId.setTextLimit(48);
			GridData gd = new GridData();//GridData.FILL_HORIZONTAL
			gd.heightHint = 13;
			gd.widthHint = 340;
			txtLotId.setLayoutData(gd);
			txtLotId.addKeyListener(getKeyListener());
			
//			label = toolkit.createLabel(comp, "       ", SWT.NULL);
//			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			/* 创建仓库控件*/
			ADTable whTable = getADTableOfVUserWarehouse();
			ADRefTable refTable = new ADRefTable();
			refTable.setKeyField(VALUE_FIELD_NAME);
			refTable.setValueField(KEY_FIELD_WAREHOUSEID);
			String where = " userRrn = " + Env.getUserRrn() + " AND (isVirtual = 'N' OR isVirtual is null)";
			refTable.setWhereClause(where);
			refTable.setTableRrn(whTable.getObjectRrn());
			tlm = new TableListManager(whTable);
			TableViewer tv = (TableViewer)tlm.createViewer(UI.getActiveShell(), toolkit);
			ADManager adManager = Framework.getService(ADManager.class);
			if (refTable.getWhereClause() == null || "".equalsIgnoreCase(refTable.getWhereClause().trim())
					|| StringUtil.parseClauseParam(refTable.getWhereClause()).size() == 0){
				List<ADBase> list = adManager.getEntityList(Env.getOrgRrn(), whTable.getObjectRrn(), Env.getMaxResult(), refTable.getWhereClause(), refTable.getOrderByClause());
				tv.setInput(list);
			}
			refField_Warehouse = new RefTableField(KEY_FIELD_WAREHOUSEID, tv, refTable, SWT.BORDER | SWT.READ_ONLY);
			refField_Warehouse.setLabel(Message.getString("inv.warehouse_id") + " *");
			refField_Warehouse.createContent(comp, toolkit);
			/*若po单中有Warehouse，则将WarehouseId值直接带出，并不可再修改*/
			PurchaseOrderLine poLine = poLineList.get(0);
			if(poLine != null && poLine.getWarehouseRrn() != null){
				refField_Warehouse.setValue(poLine.getWarehouseRrn());
//				refField_Warehouse.setEnabled(false);
				refField_Warehouse.refresh();
			}
		
			tableManager = new TableListManager(adTableLot);
			tableManager.addStyle(SWT.CHECK);
			viewer = (CheckboxTableViewer)tableManager.createViewer(client, form.getToolkit());
		} catch(Exception e) {
			logger.error("ReceiptSelectSection : createSectionContent() ");
			ExceptionHandlerManager.asyncHandleException(e);
        	return;
		}
	}
	
	private IValueChangeListener getPoLineChangedListener() {
		return new IValueChangeListener() {
			public void valueChanged(Object sender, Object newValue) {
				// 根据Poline确定入库的lot
				if(lots != null){
					lotOfPoLine = new ArrayList<Lot>();
					for (Lot lot : lots) {
						if((lot.getPoLineRrn()).equals(Long.valueOf(newValue.toString()))){
							lotOfPoLine.add(lot);
						}
					}
					refresh();
					txtLotId.setText("");
					txtLotId.setFocus();
				}
			}
		};
	};
	
	private KeyListener getKeyListener() {
		try {
			return new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					switch (e.keyCode) {
					case SWT.CR:
						if(refField_PoLine.getValue() == null || refField_PoLine.getValue().equals("")){
							UI.showWarning(Message.getString("wip.poline_is_null"));
							return;
						}
						String lotId = txtLotId.getText();
						addLot(lotId);						
						break;
					}
				}
			};			
		} catch(Exception e) {
			logger.error("Error at MoLineReceiveSection ：getKeyListener() ", e);
		}
		return null;
	}
	
	private void addLot(String lotId) {
		Lot lot = null;
		try {
			if(lotId != null && !"".equals(lotId)) {
				INVManager invManager = Framework.getService(INVManager.class);
				lot = invManager.getLotByLotId(Env.getOrgRrn(), lotId);
				if(lot == null || lot.getMaterialRrn() == null) {
					UI.showError(Message.getString("inv.lotnotexist"));
					return;
				}
				if(lot.getIsUsed()){
					UI.showError(String.format(Message.getString("inv.lot_already_used"), lot.getLotId()));
					return;
				}
				//lot位置检查
				if(lot.getPosition().equals(Lot.POSITION_INSTOCK)){
					UI.showError(String.format(Message.getString("inv.lot_already_in"), lot.getLotId()));
					return;
				}
				
				//lot是否已经包含
				if(lotOfPoLine.contains(lot)) {
					UI.showError(String.format(Message.getString("wip.lot_list_contains_lot"), lot.getLotId()));
					return;
				}
				//校验入库物料及其数量
				BigDecimal total = BigDecimal.ZERO;
				for(Lot lotsplit : lotOfPoLine) {
					total = total.add(lotsplit.getQtyCurrent());
				}
				total.add(lot.getQtyCurrent());
				
				PurchaseOrderLine line = new PurchaseOrderLine();
				for (PurchaseOrderLine l : poLineList) {
					if((l.getObjectRrn()).equals(Long.valueOf(refField_PoLine.getValue().toString()))){
						line = l;
						break;
					}
				}
				if((line.getMaterialRrn()).equals(lot.getMaterialRrn().toString())){
					UI.showError(Message.getString("wip.lotmaterial_is_different_polinemateiral"));
					return;
				}
				BigDecimal qty = line.getQtyIn() == null ? line.getQty() : line.getQty().subtract(line.getQtyIn());
				if(total.compareTo(qty) >= 0) {
					UI.showError(Message.getString("wip.qtyin_is_large_qty"));
					return;
				}
				
				lot.setPoRrn(po.getObjectRrn());
				lot.setPoId(po.getDocId());
				lot.setPoLineRrn(Long.valueOf(refField_PoLine.getValue().toString()));
				
				lots.add(lot);
				if(lotOfPoLine != null){
					lotOfPoLine.add(lot);
				}
				refresh();
				updateParentPage(true);
			}
		} catch(Exception e) {
			logger.error("Error at PoLineLotSelectSection ：addLot() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		} finally {
			txtLotId.selectAll();
		}
	}

	public void updateParentPage(boolean isChecked) {
		parentPage.setPageComplete(isChecked);
	}
	
	public void refresh() {
		if(lotOfPoLine != null){
			viewer.setInput(lotOfPoLine);
			viewer.setAllChecked(true);
			tableManager.updateView(viewer);
		}
	}
	
	public List<Lot> getLots() {
		return lots;
	}
	
	private ADTable getADTableOfVUserWarehouse() {
		ADTable table = null;
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			table = entityManager.getADTable(0L, TableName_VUser_Warehouse);
		} catch(Exception e) {
			logger.error("IqcCreateContext : getADTableOfVUserWarehouse()", e);
		}
		return table;
	}
	
	/* 若输入了入库仓库，则新建一个入库单，并将入库仓库数据赋给入库单*/
	protected MovementIn getInWarehouse() {
		Object obj = refField_Warehouse.getValue();
		if(obj instanceof String) {
			String longValue = (String)obj;
			if(!"".equals(longValue.trim())) {
				MovementIn in = new MovementIn();
				in.setOrgRrn(Env.getOrgRrn());
				in.setDocStatus(MovementIn.STATUS_DRAFTED);
				in.setWarehouseRrn(Long.parseLong(longValue));
				return in;
			}
		} else {
			this.parentPage.setErrorMessage(String.format(Message.getString("common.ismandatory"), Message.getString("inv.warehouse_id")));
		}
		return null;
	}

}

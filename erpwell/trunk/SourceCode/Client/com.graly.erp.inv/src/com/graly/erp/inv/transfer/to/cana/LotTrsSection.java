package com.graly.erp.inv.transfer.to.cana;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.MovementTransfer;
import com.graly.erp.inv.model.Warehouse;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.StringUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class LotTrsSection extends TransferToCanaLineLotSection {
	private static final Logger logger = Logger.getLogger(LotTrsSection.class);
	public static final String TABLE_NAME = "VUserWarehouse";
	public static final String TABLE_NAME_LOCATOR = "INVLocator";
	private static final String WHERE_CLAUSE = " userRrn = " + Env.getUserRrn()
			+ " AND (isVirtual = 'N' OR isVirtual is null) ";

	public static final String TABLE_NAME_REFTABLE = "ADUserRefName";
	public static final String TRS_TYPE = "TrsType";
	public static final String KEY_USER_REF = "value";
	public static final String VALUE_USER_ERF = "key";

	public static final String KEY = "objectRrn";
	public static final String VALUE = "warehouseId";
	public static final String VALUE_LOCATOR = "locatorId";
	protected ToolItem itemPrintLot;
	protected HashMap<String, BigDecimal> headerLabels;
	protected ADTable whTable; // 用户仓库ADTable
	protected RefTableField destRefField, preRefField; // , locatorRefField
	protected RefTableField trsTypeField;
	protected MovementTransfer transfer;
	

	public LotTrsSection(ADTable adTable, TransferToCanaLineLotDialog parentDialog) {
		super(parentDialog, adTable);
		headerLabels = new LinkedHashMap<String, BigDecimal>();
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemPrintLot(tBar);
		section.setTextClient(tBar);
	}

	@Override
	protected void setItemInitStatus() {
		this.setPrintabled(false);
	}

	protected void createToolItemPrintLot(ToolBar tBar) {
		itemPrintLot = new ToolItem(tBar, SWT.PUSH);
		itemPrintLot.setText(Message.getString("common.print"));
		itemPrintLot.setImage(SWTResourceCache.getImage("print"));
		itemPrintLot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				printLotAdapter();
			}
		});
	}

	protected void createParentContent(Composite client, FormToolkit toolkit) {
		try {
			Composite comp = toolkit.createComposite(client, SWT.BORDER);
			GridLayout gl = new GridLayout(6, false);
			comp.setLayout(gl);
			comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			whTable = getADTableBy(TABLE_NAME);
			// 创建原仓库控件
			createPreWarehouseContent(comp, toolkit);
			// 创建目标仓库控件
			createDestWarehouseContent(comp, toolkit);
			// 创建目标仓库库位控件
			// createDestLocatorWarehouseContent(comp, toolkit);
			// 创建调拨类型控件
			createTrsTypeContent(comp, toolkit);

			Label label = toolkit.createLabel(client, "", SWT.HORIZONTAL
					| SWT.SEPARATOR);
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	protected void addLot(String lotId){
		try {			
			if(lotId != null && !"".equals(lotId)) {				
				INVManager invManager = Framework.getService(INVManager.class);
				lot = invManager.getLotByLotId(Env.getOrgRrn(), lotId);
				if(lot == null || lot.getMaterialRrn() == null) {
					txtLotId.setForeground(SWTResourceCache.getColor("Red"));
					UI.showError(Message.getString("inv.lotnotexist"));
					return;
				}
				if(validLot(lot)) {
					// 如果l不为null，表示lot所对应的物料在lines中或与transferLine对应的物料一致
					MovementLine l = this.isContainsLot(lot);
					if(l == null) {
						return;
					}
					
					MovementLineLot lineLot = null;
					// Batch类型需要设置调拨数量
					if(Lot.LOTTYPE_BATCH.equals(lot.getLotType())
							|| Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
						Warehouse wh = getOutWarehouse();
						if(wh == null) {
							UI.showError(Message.getString("inv.batch_must_be_select_warehouse_first"));
							return;
						}
						TrsQtySetupDialog trsQtyDialog = new TrsQtySetupDialog(UI.getActiveShell(),
								null, lot, wh);
						int openId = trsQtyDialog.open();
						if(openId == Dialog.OK) {
							lineLot = pareseMovementLineLot(l, trsQtyDialog.getInputQty(), lot, false);
						} else if(openId == Dialog.CANCEL) {
							return;
						}
					} else if(Lot.LOTTYPE_SERIAL.equals(lot.getLotType())) {
						lineLot = pareseMovementLineLot(l, lot.getQtyCurrent(), lot, false);
					}
					if(contains(lineLot)) {
						UI.showError(String.format(Message.getString("wip.lot_list_contains_lot"), lot.getLotId()));
						return;
					}
					getLineLots().add(lineLot);
					refresh();
					setDoOprationsTrue();
				}
			}
		} catch(Exception e) {
			txtLotId.setForeground(SWTResourceCache.getColor("Red"));
			logger.error("Error at LotTrsSection ：addLot() ", e);
			if(e instanceof ClientException && "inv.lotnotexist".equals(((ClientException)e).getErrorCode())){
				errorLots.add(lotId);
			}else{
				ExceptionHandlerManager.asyncHandleException(e);
			}
		} finally {
			txtLotId.selectAll();
		}
	}
	
	protected void createPreWarehouseContent(Composite parent,
			FormToolkit toolkit) throws Exception {
		if (whTable == null)
			whTable = this.getADTableBy(TABLE_NAME);
		ADRefTable refTable = new ADRefTable();
		refTable.setKeyField(KEY);
		refTable.setValueField(VALUE);
		refTable.setTableRrn(whTable.getObjectRrn());
		refTable.setWhereClause(WHERE_CLAUSE);
		TableListManager tableManager = new TableListManager(whTable);
		TableViewer viewer = (TableViewer) tableManager.createViewer(UI
				.getActiveShell(), toolkit);
		ADManager adManager = Framework.getService(ADManager.class);
		if (refTable.getWhereClause() == null
				|| "".equalsIgnoreCase(refTable.getWhereClause().trim())
				|| StringUtil.parseClauseParam(refTable.getWhereClause())
						.size() == 0) {
			List<ADBase> list = adManager.getEntityList(Env.getOrgRrn(),
					whTable.getObjectRrn(), Env.getMaxResult(), refTable
							.getWhereClause(), refTable.getOrderByClause());
			viewer.setInput(list);
		}
		preRefField = new RefTableField("warehouseRrn", viewer, refTable,
				SWT.READ_ONLY);
		preRefField
				.setLabel(Message.getString("inv.trs_pre_warehouseId") + "*");
		preRefField.createContent(parent, toolkit);
	}

	protected void createDestWarehouseContent(Composite parent,
			FormToolkit toolkit) throws Exception {
		if (whTable == null)
			whTable = this.getADTableBy(TABLE_NAME);
		ADRefTable refTable = new ADRefTable();
		refTable.setKeyField(KEY);
		refTable.setValueField(VALUE);
		refTable.setTableRrn(whTable.getObjectRrn());
		refTable.setWhereClause(WHERE_CLAUSE);
		TableListManager tableManager = new TableListManager(whTable);
		TableViewer viewer = (TableViewer) tableManager.createViewer(UI
				.getActiveShell(), toolkit);
		ADManager adManager = Framework.getService(ADManager.class);
		if (refTable.getWhereClause() == null
				|| "".equalsIgnoreCase(refTable.getWhereClause().trim())
				|| StringUtil.parseClauseParam(refTable.getWhereClause())
						.size() == 0) {
			List<ADBase> list = adManager.getEntityList(Env.getOrgRrn(),
					whTable.getObjectRrn(), Env.getMaxResult(), refTable
							.getWhereClause()+" and objectRrn =42803113", refTable.getOrderByClause());
			viewer.setInput(list);
		}
		destRefField = new RefTableField("targetWarehouseRrn", viewer,
				refTable, SWT.READ_ONLY);
		destRefField.setLabel(Message.getString("inv.trs_target_warehouseId")
				+ "*");
		destRefField.createContent(parent, toolkit);
//		destRefField.setSelectionIndex(0);
	}

	protected void createDestLocatorWarehouseContent(Composite parent,
			FormToolkit toolkit) throws Exception {
		ADTable locator = this.getADTableBy(TABLE_NAME_LOCATOR);
		ADRefTable refTable = new ADRefTable();
		refTable.setKeyField(KEY);
		refTable.setValueField(VALUE_LOCATOR);
		refTable.setTableRrn(locator.getObjectRrn());
		refTable.setWhereClause("warehouseRrn=:targetWarehouseRrn");
		TableListManager tableManager = new TableListManager(locator);
		TableViewer viewer = (TableViewer) tableManager.createViewer(UI
				.getActiveShell(), toolkit);
		ADManager adManager = Framework.getService(ADManager.class);
		if (refTable.getWhereClause() == null
				|| "".equalsIgnoreCase(refTable.getWhereClause().trim())
				|| StringUtil.parseClauseParam(refTable.getWhereClause())
						.size() == 0) {
			List<ADBase> list = adManager.getEntityList(Env.getOrgRrn(),
					locator.getObjectRrn(), Env.getMaxResult(), refTable
							.getWhereClause(), refTable.getOrderByClause());
			viewer.setInput(list);
		}
		// locatorRefField = new RefTableField("locatorRrn", viewer, refTable,
		// SWT.READ_ONLY);
		// locatorRefField.setADField(new ADField());
		// locatorRefField.setLabel(Message.getString("inv.trs_target_locatorId"));
		// locatorRefField.createContent(parent, toolkit);
		//    	
		// if(destRefField != null)
		// destRefField.addValueChangeListener(locatorRefField);
	}

	protected void createTrsTypeContent(Composite parent, FormToolkit toolkit)
			throws Exception {
		ADTable refAdTable = this.getADTableBy(TABLE_NAME_REFTABLE);
		ADRefTable refTable = new ADRefTable();
		refTable.setKeyField(KEY_USER_REF);
		refTable.setValueField(VALUE_USER_ERF);
		refTable.setTableRrn(refAdTable.getObjectRrn());
		String whereClause = " referenceName = '" + TRS_TYPE + "'";
		refTable.setWhereClause(whereClause);
		TableListManager tableManager = new TableListManager(refAdTable);
		TableViewer viewer = (TableViewer) tableManager.createViewer(UI
				.getActiveShell(), toolkit);
		ADManager adManager = Framework.getService(ADManager.class);
		if (refTable.getWhereClause() == null
				|| "".equalsIgnoreCase(refTable.getWhereClause().trim())
				|| StringUtil.parseClauseParam(refTable.getWhereClause())
						.size() == 0) {
			List<ADBase> list = adManager.getEntityList(Env.getOrgRrn(),
					refAdTable.getObjectRrn(), Env.getMaxResult(), refTable
							.getWhereClause(), refTable.getOrderByClause());
			viewer.setInput(list);
		}
		trsTypeField = new RefTableField("trsType", viewer, refTable,
				SWT.READ_ONLY);
		trsTypeField.setLabel(Message.getString("inv.trs_type"));//
		trsTypeField.createContent(parent, toolkit);
	}

	protected ADTable getADTableBy(String tableName) throws Exception {
		ADTable adTable = null;
		ADManager adManager = Framework.getService(ADManager.class);
		adTable = adManager.getADTable(0L, tableName);
		return adTable;
	}

	protected boolean validLot(Lot lot) {
		if (!getLineLots().contains(lot)) {
			if (!(Lot.POSITION_INSTOCK.equals(lot.getPosition())||Lot.POSITION_GEN.equals(lot.getPosition()))) {
				if (!Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
					UI
							.showError(String.format(Message
									.getString("wip.lot_not_in_stock"), lot
									.getLotId()));
					return false;
				}
			}
			headerLabels.put(lot.getLotId(), lot.getQtyCurrent());
			return true;
		} else {
			UI.showError(String.format(Message
					.getString("wip.lot_list_contains_lot"), lot.getLotId()));
		}
		return false;
	}

	protected MovementLine isContainsLot(Lot lot) {
		return new MovementLine();
	}

	protected void saveAdapter() {
		try {
			if (validate()) {
				transfer = createMovementTrs();
				List<MovementLine> lines = createMovementLines();
				INVManager invManager = Framework.getService(INVManager.class);
				transfer = invManager.saveMovementTransferLineToCana(transfer, lines,
						Env.getUserRrn());
				UI.showInfo(Message.getString("common.save_successed"));
				setEnabled(false);
				setPrintabled(true);
				setIsSaved(true);
				initTableContent();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at LotTrsSection : saveAdapter() ", e);
		}
	}

	// 验证原仓库、目标仓库是否输入; 是否有出库的批次
	protected boolean validate() {
		setErrorMessage(null);
		if (this.preRefField.getValue() == null) {
			setErrorMessage(String.format(Message
					.getString("common.ismandatory"), Message
					.getString("inv.trs_pre_warehouseId")));
			return false;
		}
		if (destRefField.getValue() == null) {
			setErrorMessage(String.format(Message
					.getString("common.ismandatory"), Message
					.getString("inv.trs_target_warehouseId")));
			return false;
		}
		if (this.getLineLots().size() == 0) {
			return false;
		} else {// 判断是否自制件，如果是警告
			StringBuffer lotIds = new StringBuffer();
			for (MovementLineLot mLot : getLineLots()) {
				Material m = new Material();
				m.setObjectRrn(mLot.getMaterialRrn());
				try {
					ADManager manager = Framework.getService(ADManager.class);
					m = (Material) manager.getEntity(m);
				} catch (Exception e) {
					logger.error("LotTrsSection : validate()", e);
				}
				if (m.getMaterialCategory1() != null
						&& m.getMaterialCategory2() != null) {
					boolean f1 = "生产物料".equalsIgnoreCase(m
							.getMaterialCategory1());
					boolean f2 = "自制"
							.equalsIgnoreCase(m.getMaterialCategory2());
					if (f1 && f2) {
						if (lotIds.toString().trim().length() > 0) {
							lotIds.append(", ");
						}
						lotIds.append(mLot.getLotId());
					}
				}
				if (lotIds.toString().length() > 0) {
					boolean confirm = UI.showConfirm("以下批次 " + lotIds.toString()
							+ " 是自制件，是否确定要继续?");
					return confirm;
				}
			}
		}
		return true;
	}

	protected MovementTransfer createMovementTrs() {
		if (transfer != null && transfer.getObjectRrn() != null) {
			if (trsTypeField.getValue() != null) {
				transfer.setTrsType(String.valueOf(trsTypeField.getValue()));
			}
			return transfer;
		}
		MovementTransfer trs = new MovementTransfer();
		trs.setOrgRrn(Env.getOrgRrn());
		trs.setWarehouseRrn(Long.parseLong(String.valueOf(this.preRefField
				.getValue())));
		trs.setTargetWarehouseRrn(Long.parseLong(String
				.valueOf(this.destRefField.getValue())));
		if (trsTypeField.getValue() != null) {
			trs.setTrsType(String.valueOf(trsTypeField.getValue()));
		}
		return trs;
	}

	protected List<MovementLine> createMovementLines() throws Exception {
		List<MovementLine> lines = new ArrayList<MovementLine>();

		List<Long> materialRrns = new ArrayList<Long>();
		List<MovementLineLot> lineLots = null;
		BigDecimal total = BigDecimal.ZERO;
		int i = 1;
		for (MovementLineLot lineLot : getLineLots()) {
			if (materialRrns.contains(lineLot.getMaterialRrn()))
				continue;
			lineLots = new ArrayList<MovementLineLot>();
			total = BigDecimal.ZERO;
			for (MovementLineLot tempLineLot : getLineLots()) {
				if (tempLineLot.getMaterialRrn().equals(
						lineLot.getMaterialRrn())) {
					lineLots.add(tempLineLot);
					total = total.add(tempLineLot.getQtyMovement());
				}
			}
			materialRrns.add(lineLot.getMaterialRrn());
			if (lineLots.size() > 0) {
				lines.add(generateLine(lineLots, total, i * 10));
				i++;
			}
		}
		return lines;
	}

	// 单价和行总价没有，movementRrn, movementId, movementLineRrn在后台设置
	// 在此不像出库那样可以连续保存
	protected MovementLine generateLine(List<MovementLineLot> lineLots,
			BigDecimal qtyTrs, int lineNo) throws Exception {
		MovementLine line = new MovementLine();
		line.setOrgRrn(Env.getOrgRrn());
		line.setLineStatus(MovementTransfer.STATUS_DRAFTED);
		line.setLineNo(new Long(lineNo));
		line.setQtyMovement(qtyTrs);

		MovementLineLot lineLot = lineLots.get(0);
		// 库位暂不显示
		// if(locatorRefField.getValue() != null) {
		// String value = (String)locatorRefField.getValue();
		// if(!"".equals(value.trim())) {
		// line.setLocatorRrn(Long.parseLong(String.valueOf(locatorRefField.getValue())));
		// }
		// }
		line.setMovementLots(lineLots);
		line.setMaterialId(lineLot.getMaterialId());
		line.setMaterialName(lineLot.getMaterialName());
		line.setMaterialRrn(lineLot.getMaterialRrn());
		// 通过物料获得库存单位
		ADManager adManager = Framework.getService(ADManager.class);
		Material mater = new Material();
		mater.setObjectRrn(lineLot.getMaterialRrn());
		mater = (Material) adManager.getEntity(mater);
		line.setUomId(mater.getInventoryUom());
		return line;
	}

	protected Warehouse getOutWarehouse() {
		if (this.preRefField.getValue() == null) {
			return null;
		}
		Warehouse wh = new Warehouse();
		wh.setObjectRrn(Long.parseLong(String.valueOf(this.preRefField
				.getValue())));
		return wh;
	}

	protected void setErrorMessage(String msg) {
		parentDialog.setErrorMessage(msg);
	}

	protected void setEnabled(boolean enabled) {
		this.itemSave.setEnabled(enabled);
		this.itemDelete.setEnabled(enabled);
	}

	protected void setPrintabled(boolean enabled) {
		this.itemPrintLot.setEnabled(enabled);
	}

	public MovementTransfer getTransfer() {
		return transfer;
	}

	public void setTransfer(MovementTransfer transfer) {
		this.transfer = transfer;
	}

	@Override
	protected String getWhereClause() {
		StringBuffer whereClause = new StringBuffer("");
		if (this.transfer != null && transfer.getObjectRrn() != null) {
			whereClause.append(" movementRrn = '");
			whereClause.append(this.transfer.getObjectRrn());
			whereClause.append("' ");
			return whereClause.toString();
		}
		return " 1 <> 1 ";
	}

}

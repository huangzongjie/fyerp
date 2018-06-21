package com.graly.erp.inv.outother;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.model.MovementTransfer;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.inv.out.OutLineLotSection;
import com.graly.erp.inv.out.OutQtySetupDialog;
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
/**
 * @author Jim
 * 实现按批次出库，直接输入批次，保存成功后生成出库单
 */
public class ByLotOutSection extends OutLineLotSection {
	private static final Logger logger = Logger.getLogger(ByLotOutSection.class);
	public static final String TABLE_NAME = "VUserWarehouse";
	public static final String TABLE_NAME_REFTABLE = "ADUserRefName";
	public static final String TABLE_NAME_LOCATOR = "INVLocator";
	public static final String OUT_TYPE = "OutType";
	private static final String WHERE_CLAUSE = " userRrn = " + Env.getUserRrn() + " AND (isVirtual = 'N' OR isVirtual is null) ";
	
	public static final String KEY = "objectRrn";
	public static final String KEY_USER_REF = "value";
	public static final String VALUE = "warehouseId";
	public static final String VALUE_USER_ERF = "key";
	
	protected HashMap<String, BigDecimal> headerLabels;
	protected ADTable whTable; //用户仓库ADTable
	protected RefTableField outWhField, outTypeFiled;
	public ByLotOutSection(ADTable adTable, LotDialog parentDialog) {
		super(adTable, parentDialog);
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		setEnabled(true);
		section.setTextClient(tBar);
	}

	protected void createParentContent(Composite client, FormToolkit toolkit) {
		try {
			Composite comp = toolkit.createComposite(client, SWT.BORDER);
			GridLayout gl = new GridLayout(4, false);
			comp.setLayout(gl);
			comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			whTable = getADTableBy(TABLE_NAME);
			// 创建出库仓库控件
			createOutWarehouseContent(comp, toolkit);
			// 创建出库类型控件
			createOutTypeContent(comp, toolkit);
			
			Label label = toolkit.createLabel(client, "", SWT.HORIZONTAL | SWT.SEPARATOR);
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	protected void createOutWarehouseContent(Composite parent, FormToolkit toolkit) throws Exception {		
		if(whTable == null) whTable = this.getADTableBy(TABLE_NAME);
		ADRefTable refTable = new ADRefTable();
		refTable.setKeyField(KEY);
		refTable.setValueField(VALUE);
		refTable.setTableRrn(whTable.getObjectRrn());
		refTable.setWhereClause(WHERE_CLAUSE);
		TableListManager tableManager = new TableListManager(whTable);
		TableViewer viewer = (TableViewer)tableManager.createViewer(UI.getActiveShell(), toolkit);
		ADManager adManager = Framework.getService(ADManager.class);
		if (refTable.getWhereClause() == null || "".equalsIgnoreCase(refTable.getWhereClause().trim())
				|| StringUtil.parseClauseParam(refTable.getWhereClause()).size() == 0){
			List<ADBase> list = adManager.getEntityList(Env.getOrgRrn(), whTable.getObjectRrn(),
					Env.getMaxResult(), refTable.getWhereClause(), refTable.getOrderByClause());
			viewer.setInput(list);
		}
		outWhField = new RefTableField("warehouseRrn", viewer, refTable, SWT.READ_ONLY);
		outWhField.setLabel(Message.getString("inv.warehouse_id") + "*");
		outWhField.createContent(parent, toolkit);
	}
	
	protected void createOutTypeContent(Composite parent, FormToolkit toolkit) throws Exception {
		ADTable refAdTable = this.getADTableBy(TABLE_NAME_REFTABLE);
		ADRefTable refTable = new ADRefTable();
		refTable.setKeyField(KEY_USER_REF);
		refTable.setValueField(VALUE_USER_ERF);
		refTable.setTableRrn(refAdTable.getObjectRrn());
		String whereClause =  " referenceName = '" + OUT_TYPE + "'";
		refTable.setWhereClause(whereClause);
		TableListManager tableManager = new TableListManager(refAdTable);
		TableViewer viewer = (TableViewer)tableManager.createViewer(UI.getActiveShell(), toolkit);
		ADManager adManager = Framework.getService(ADManager.class);
		if (refTable.getWhereClause() == null || "".equalsIgnoreCase(refTable.getWhereClause().trim())
				|| StringUtil.parseClauseParam(refTable.getWhereClause()).size() == 0) {
			List<ADBase> list = adManager.getEntityList(Env.getOrgRrn(), refAdTable.getObjectRrn(),
					Env.getMaxResult(), refTable.getWhereClause(), refTable.getOrderByClause());
			viewer.setInput(list);
		}
		outTypeFiled = new RefTableField("outType", viewer, refTable, SWT.READ_ONLY);
		outTypeFiled.setLabel(Message.getString("inv.out_type"));
		outTypeFiled.createContent(parent, toolkit);
	}
	
	protected ADTable getADTableBy(String tableName) throws Exception {
		ADTable adTable = null;
		ADManager adManager = Framework.getService(ADManager.class);
		adTable = adManager.getADTable(0L, tableName);
		return adTable;
	}

	// 验证出库仓库是否输入; 是否有出库的批次
	protected boolean validate() {
		setErrorMessage(null);
		if(outWhField.getValue() == null || "".equals(outWhField.getValue().toString())) {
			setErrorMessage(String.format(Message.getString("common.ismandatory"),
					Message.getString("inv.warehouse_id")));
			return false;
		}
		if(this.getLineLots().size() == 0) {
			return false;
		}
		return true;
	}
	
	@Override
	protected void saveAdapter() {
		try {
			if(validate()) {
				out = createMovementOut();
				List<MovementLine> lines = createMovementLines();
				INVManager invManager = Framework.getService(INVManager.class);
				out = invManager.saveMovementOutLine(out, lines, getOutType(), Env.getUserRrn());
				UI.showInfo(Message.getString("common.save_successed"));
				setEnabled(false);
				setIsSaved(true);
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at ByLotOutSection : saveAdapter() ", e);
		}
	}
	
	protected MovementOut createMovementOut() {
		// 多次保存时，仍保存在同一出库单中
		if(out != null && out.getObjectRrn() != null) {
			if(this.outWhField.getValue() != null) {
				out.setOutType(String.valueOf(outWhField.getValue()));			
			}
			return out;			
		}
		MovementOut out = new MovementOut();
		out.setOrgRrn(Env.getOrgRrn());
		out.setWarehouseRrn(Long.parseLong(String.valueOf(outWhField.getValue())));
		if(outTypeFiled.getValue() != null) {
			out.setOutType(String.valueOf(outTypeFiled.getValue()));			
		}
		return out;
	}
	
	protected MovementLine isContainsLot(Lot lot) {
		return new MovementLine();
	}
	
	protected List<MovementLine> createMovementLines() throws Exception {
		List<MovementLine> lines = new ArrayList<MovementLine>();
		
		List<Long> materialRrns = new ArrayList<Long>();
		List<MovementLineLot> lineLots = null;
		BigDecimal total = BigDecimal.ZERO;
		int i = 1;
		for(MovementLineLot lineLot : this.getLineLots()) {
			if(materialRrns.contains(lineLot.getMaterialRrn()))
				continue;
			
			lineLots = new ArrayList<MovementLineLot>();
			total = BigDecimal.ZERO;
			for(MovementLineLot tempLineLot : getLineLots()) {
				if(tempLineLot.getMaterialRrn().equals(lineLot.getMaterialRrn())) {
					lineLots.add(tempLineLot);
					total = total.add(tempLineLot.getQtyMovement());
				}
			}
			materialRrns.add(lineLot.getMaterialRrn());
			if(lineLots.size() > 0) {
				lines.add(generateLine(lineLots, total, i * 10));
				i++;
			}
		}
		return lines;
	}
	
	// 单价和行总价没有，movementRrn, movementId, movementLineRrn在后台设置
	protected MovementLine generateLine(List<MovementLineLot> lineLots,
			BigDecimal qtyOut, int lineNo) throws Exception {
		MovementLine line = null;
		// 如果为再次保存则，根据物料找到已保存的出库单行，重新赋给lineLots, qtyOut
		if(out != null && out.getObjectRrn() != null) {
			MovementLineLot lineLot = lineLots.get(0);
			for(MovementLine movementLine : out.getMovementLines()) {
				if(lineLot.getMaterialRrn().equals(movementLine.getMaterialRrn())
						&& movementLine.getObjectRrn() != null) {
					movementLine.setMovementLots(lineLots);
					movementLine.setQtyMovement(qtyOut);
					return movementLine;
				}
			}
		}
		// 否则重新建个出库单行
		line = new MovementLine();
		line.setOrgRrn(Env.getOrgRrn());
		line.setLineStatus(MovementTransfer.STATUS_DRAFTED);
		line.setLineNo(new Long(lineNo));
		line.setQtyMovement(qtyOut);
		
		line.setMovementLots(lineLots);
		MovementLineLot lineLot = lineLots.get(0);
		line.setMaterialId(lineLot.getMaterialId());
		line.setMaterialName(lineLot.getMaterialName());
		line.setMaterialRrn(lineLot.getMaterialRrn());
		ADManager adManager = Framework.getService(ADManager.class);
		Material material = new Material();
		material.setObjectRrn(lineLot.getMaterialRrn());
		material = (Material)adManager.getEntity(material);
		line.setUomId(material.getInventoryUom());
		return line;
	}
	
	protected Warehouse getOutWarehouse() {
		if(outWhField.getValue() == null || "".equals(outWhField.getValue().toString())) {
			return null;
		}
		Warehouse wh = new Warehouse();
		wh.setObjectRrn(Long.parseLong(String.valueOf(outWhField.getValue())));
		return wh;
	}
	
	@Override
	protected String getWhereClause() {
		StringBuffer whereClause = new StringBuffer("");
		if(out != null && out.getObjectRrn() != null) {
			whereClause.append(" movementRrn = '");
			whereClause.append(out.getObjectRrn());
			whereClause.append("' ");
		}
		return " 1 <> 1 ";
	}
	
	protected MovementOut.OutType getOutType() {
		return MovementOut.OutType.OOU;
	}
	
	protected void setErrorMessage(String msg) {
		parentDialog.setErrorMessage(msg);
	}
	
	protected void setEnabled(boolean enabled) {
		this.itemSave.setEnabled(enabled);
		this.itemDelete.setEnabled(enabled);
	}
	
	
	protected KeyListener getKeyListener() {
		try {
			return new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					txtLotId.setForeground(SWTResourceCache.getColor("Black"));
					switch (e.keyCode) {
					case SWT.CR :
						addLot();
						break;
					case SWT.TRAVERSE_RETURN :
						addLot();
						break;
					}
				}
			};
		} catch(Exception e) {
			logger.error("Error at LotMasterSection ：getKeyListener() ", e);
		}
		return null;
	}
	
	@Override
	protected void addLot() {
		String lotId = txtLotId.getText();
		errorLots.clear();
		try {
			switch (optional) {
			case 0://仅输入
				addLot(lotId);
				break;
			case 1://连续序列
				String input = UI.showInput("序列设置","请输入最后一个批次的末尾序号");
				if(input == null || input.trim().length() == 0){
					UI.showError(Message.getString("common.input_error"));
				}
				
				try {
					int nums = Integer.parseInt(input);
					String beginSerial = lotId.substring(lotId.length()-input.length());
					String lotIdPrefix = lotId.substring(0, lotId.length()-input.length());
					int startNum = Integer.parseInt(beginSerial);
					for(;startNum <= nums;startNum++){
						StringBuffer sb = new StringBuffer(lotIdPrefix);
						int len1 = String.valueOf(startNum).trim().length();
						int len2 = input.length();
						if(len1 < len2){
							for(int i=0;i<len2-len1;i++){
								sb.append(0);
							}
						}
						sb.append(startNum);
						lotId = sb.toString();
						addLot(lotId);
					}
					StringBuffer sb = new StringBuffer();
					if(errorLots != null && errorLots.size() > 0){
						int i = 0;
						for(String str : errorLots){
							sb.append(str);
							if(errorLots.size() > 1){
								if(i < errorLots.size()-1){
									sb.append("|");
								}
							}
							if(++i % 5 ==0 ){
								sb.append("\r\n");
							}
						}
						UI.showError("  "+Message.getString("inv.lotnotexist")+":\r\n"+sb.substring(0, sb.length()));
					}
					} catch (NumberFormatException e) {
						UI.showError(Message.getString("common.input_error"));
					}
				break;
			case 2://手动选取
				break;
			}
		} catch(Exception e) {
			txtLotId.setForeground(SWTResourceCache.getColor("Red"));
			logger.error("Error at LotMasterSection ：addLot() ", e);
		} 
	}
	
	@Override
	protected void addLot(String lotId){
		try {
			if(lotId != null && lotId.startsWith("LL")){
				INVManager invManager = Framework.getService(INVManager.class);
				lots = invManager.getLotsById(Env.getOrgRrn(), lotId);
				for(Lot lot : lots){
					MovementLineLot lineLot = null;
					MovementLine l = this.isContainsLot(lot);
					if(l == null) {
						return;
					}
					lineLot = pareseMovementLineLot(l, lot.getQtyCurrent(), lot);
					getLineLots().add(lineLot);	
				}
				refresh();
				setDoOprationsTrue();
				
				}
			else
			if(lotId != null && !"".equals(lotId)) {
				
				INVManager invManager = Framework.getService(INVManager.class);
//				lot = invManager.getLotByLotId(Env.getOrgRrn(), lotId);
				if(Env.getOrgRrn()==139420L){
					lot = invManager.getLotByLotIdNoWms(Env.getOrgRrn(), lotId);
				}else{
					lot = invManager.getLotByLotId(Env.getOrgRrn(), lotId);
				}
				
				if(lot == null || lot.getMaterialRrn() == null) {
					txtLotId.setForeground(SWTResourceCache.getColor("Red"));
					UI.showError(Message.getString("inv.lotnotexist"));
					return;
				}
				if (lot.getIsUsed() || Lot.POSITION_OUT.equals(lot.getPosition())) {
					UI.showError(String.format(Message.getString("wip.lot_is_used"), lot.getLotId()));
					return;
				}
				if(!Lot.POSITION_INSTOCK.equals(lot.getPosition())) {
					if(!Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
						UI.showError(String.format(Message.getString("inv.lot_not_in"), lot.getLotId()));
						return;						
					}
				}
				// 如果l不为null，表示lot所对应的物料在lines中或与outLine对应的物料一致
				MovementLine l = this.isContainsLot(lot);
				if(l == null) {
					return;
				}
				
				MovementLineLot lineLot = null;
				if(Lot.LOTTYPE_BATCH.equals(lot.getLotType())
						|| Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
					Warehouse wh = getOutWarehouse();
					if(wh == null) {
						UI.showError(Message.getString("inv.batch_must_be_select_warehouse_first"));
						return;
					}
					OutQtySetupDialog outQtyDialog = new OutQtySetupDialog(UI.getActiveShell(), outLine, lot, wh);
					int openId = outQtyDialog.open();
					if(openId == Dialog.OK) {
						lineLot = pareseMovementLineLot(l, outQtyDialog.getInputQty(), lot);
					} else if(openId == Dialog.CANCEL) 
						return;
				} else if(Lot.LOTTYPE_SERIAL.equals(lot.getLotType())) {
					lineLot = pareseMovementLineLot(l, lot.getQtyCurrent(), lot);
				}
				if(contains(lineLot)) {
					UI.showError(String.format(Message.getString("wip.lot_list_contains_lot"), lot.getLotId()));
					return;
				}
				getLineLots().add(lineLot);						
				refresh();
				setDoOprationsTrue();
			}
		} catch(Exception e) {
			txtLotId.setForeground(SWTResourceCache.getColor("Red"));
			logger.error("Error at LotMasterSection ：addLot(String lotId) ", e);
			if(e instanceof ClientException && "inv.lotnotexist".equals(((ClientException)e).getErrorCode())){
				errorLots.add(lotId);
			}else{
				ExceptionHandlerManager.asyncHandleException(e);
			}
		} finally {
			txtLotId.selectAll();
		}
	}
}

package com.graly.erp.inv.transfer.hy.dpk;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.barcode.LotMasterSection;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.lotprint.LotPrintDialog;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.MovementTransfer;
import com.graly.erp.inv.model.Warehouse;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class DpkTransferLineLotSection extends LotMasterSection {
	private static final Logger logger = Logger.getLogger(DpkTransferLineLotSection.class);
	protected ToolItem itemPrintLot;
	private MovementTransfer mt;
	private MovementLine transferLine;
	
	private List<MovementLineLot> lineLots;
	private MovementLineLot selectedLineLot;
	
	protected boolean isView = false;
	
	protected int optional;
	protected List<String> errorLots = new ArrayList<String>();
	
	public DpkTransferLineLotSection(DpkTransferLineLotDialog parentDialog, ADTable adTable) {
		super(adTable, parentDialog);
	}
	
	public DpkTransferLineLotSection(DpkTransferLineLotDialog parentDialog, ADTable adTable,
			MovementTransfer mt, MovementLine transferLine, boolean isView) {
		super(adTable, parentDialog);
		this.mt = mt;
		this.transferLine = transferLine;
		this.isView = isView;
	}
	
	public DpkTransferLineLotSection(DpkTransferLineLotDialog olld, ADTable adTable,
			MovementTransfer mt, MovementLine transferLine,
			List<MovementLine> lines, boolean isView) {
		this(olld, adTable, mt, transferLine, isView);
		this.lines = lines;
	}
	
	protected void setItemInitStatus() {
		if (isView || (transferLine != null && !Movement.STATUS_DRAFTED.equals(transferLine.getLineStatus()))) {
			this.setEnabled(false);
			setPrintabled(true);
		}
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
	
	protected void printLotAdapter() {
		try {
			this.lineLots = (List<MovementLineLot>)viewer.getInput();
			if(lineLots != null && lineLots.size() != 0){
				List<Lot> lots = new ArrayList<Lot>();
				for(MovementLineLot lineLot : lineLots) {
					Lot lot = new Lot();
					lot.setObjectRrn(lineLot.getLotRrn());
					lot.setLotId(lineLot.getLotId());
					lots.add(lot);
					
					if(selectedLineLot != null) {
						if(selectedLineLot.getLotRrn().equals(lot.getObjectRrn())) {
							this.selectLot = lot;
						}
					}
				}
				LotPrintDialog printDialog = new LotPrintDialog(lots, this.selectLot);
				printDialog.open();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void setMovementLineSelect(Object obj) {
		if (obj instanceof MovementLineLot) {
			selectedLineLot = (MovementLineLot) obj;
		} else {
			selectedLineLot = null;
		}
	}
	
	protected void initTableContent() {
		List<ADBase> list = null;
		try {
        	ADManager manager = Framework.getService(ADManager.class);
            list = manager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(), 
            		Env.getMaxResult(), getWhereClause(), getOrderByClause());
            List<MovementLineLot> l = new ArrayList<MovementLineLot>();
            for(ADBase ab : list) {
            	MovementLineLot lineLot = (MovementLineLot)ab;
            	l.add(lineLot);
            }
            setLineLots(l);
            refresh();
            if(getLineLots() != null && getLineLots().size() > 0) {
//            	this.setEnabled(false);
    			this.setPrintabled(true);
    		} else {
    			this.setPrintabled(false);
    		}
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
        }
	}
	
	protected void addLot() {
		String lotId = txtLotId.getText();
		errorLots.clear();
		try {
			switch (optional) {
			case 0://仅输入
				addLot(lotId);
				break;
			case 1://连续序列
				String input = UI.showInput("序列设置","请序入最后一个批次的末尾数值");
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
					}
					UI.showError("  "+Message.getString("inv.lotnotexist")+":\r\n"+sb.substring(0, sb.length()));
					} catch (NumberFormatException e) {
						UI.showError(Message.getString("common.input_error"));
					}
				break;
			case 2://手动选取
				break;
			}
		} catch(Exception e) {
			txtLotId.setForeground(SWTResourceCache.getColor("Red"));
			logger.error("Error at TransferLineLotSection ：addLot() ", e);
		} 
	}
	
	protected void addLot(String lotId) {
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
						DpkTrsQtySetupDialog trsQtyDialog = new DpkTrsQtySetupDialog(UI.getActiveShell(),
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
			logger.error("Error at TransferLineLotSection ：addLot() ", e);
			if(e instanceof ClientException && "inv.lotnotexist".equals(((ClientException)e).getErrorCode())){
				errorLots.add(lotId);
			}else{
				ExceptionHandlerManager.asyncHandleException(e);
			}
		} finally {
			txtLotId.selectAll();
		}
	}
	
	public void refresh() {
		if(lotManager != null && viewer != null) {
			lotManager.setInput(this.getLineLots());
			lotManager.updateView(viewer);
			createSectionDesc(section);
		}
	}
	
	protected Warehouse getOutWarehouse() {
		if(mt == null || mt.getWarehouseRrn() == null)
			return null;
		Warehouse wh = new Warehouse();
		wh.setObjectRrn(mt.getWarehouseRrn());
		return wh;
	}
	
	protected MovementLine isContainsLot(Lot lot) {
		MovementLine l = null;
		if(transferLine != null && transferLine.getMaterialRrn().equals(lot.getMaterialRrn()))
			return transferLine;
		if(lines != null && lines.size() > 0) {
			// 根据物料找到lot对应的line, 并验证出库物料是否含有该lot对应的物料
			boolean flag = false;
			for(MovementLine line : lines) {
				if(line.getMaterialRrn().equals(lot.getMaterialRrn())) {
					l = line;
					return l;
				}
			}
			if(!flag) {
				UI.showError(String.format(Message.getString("inv.material_by_lot_isnot_exist_outlines"),
						lot.getLotId(), lot.getMaterialId()));
				return l;
			}
		}
		return l;
	}

	protected boolean contains(MovementLineLot lineLot) {
		if(lineLot == null) return true;
		for(MovementLineLot temp : this.getLineLots()) {
			if(temp.getLotId().equals(lineLot.getLotId()))
				return true;
		}
		return false;
	}

	protected void saveAdapter() {
		try {
			if(validateAll()) {
				INVManager invManager = Framework.getService(INVManager.class);
				mt = invManager.saveMovementTransferLine(mt, lines, Env.getOrgRrn());
				UI.showInfo(Message.getString("common.save_successed"));
				setPrintabled(true);
				setIsSaved(true);
				((DpkTransferLineLotDialog)parentDialog).buttonPressed(IDialogConstants.CANCEL_ID);
			}
		} catch(Exception e) {
			logger.error("Error at TransferLineLotSection : saveAdapter() " + e);
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	/* 验证批次总数是否等于调拨行调拨数量 */
	protected boolean validate() {
		BigDecimal total = BigDecimal.ZERO;
		for(MovementLineLot lineLot : getLineLots()) {
			total = total.add(lineLot.getQtyMovement());
		}
		if(total.doubleValue() == transferLine.getQtyMovement().doubleValue()) {
			return true;
		} else {
			UI.showError(String.format(Message.getString("wip.transfer_qty_isnot_equal"),
					transferLine.getMaterialName(), transferLine.getQtyMovement().toString(), String.valueOf(total)));
		}
		return false;
	}
	
	// 验证各个入库物料的入库数量是否等于对应的批次数量之和
	protected boolean validateAll() {
		if(lines == null || lines.size() == 0)
			return false;
		BigDecimal total = null;
		List<MovementLineLot> lLots = null;
		StringBuffer emsg = new StringBuffer(""); //提示批次数量与调拨数量不相等时，是否继续保存信息
		for(MovementLine line : lines) {
			if(Lot.LOTTYPE_MATERIAL.equals(line.getLotType()))
				continue;
			total = BigDecimal.ZERO;
			lLots = getLineLotsByMaterial(line.getMaterialRrn());
			for(MovementLineLot linelot : lLots) {
				if(linelot.getLotId() == null || "".equals(linelot.getLotId().trim())) {
					UI.showError(Message.getString("inv.invalid_lotId"));
					return false;
				}
				total = total.add(linelot.getQtyMovement());
			}
			if(total.compareTo(line.getQtyMovement()) == 0) {
				line.setMovementLots(lLots);
				continue;
			} else {
				line.setMovementLots(lLots);
				emsg.append(String.format(Message.getString("wip.out_qty_isnot_equal"),
						total.toString(), line.getQtyMovement().toString(), line.getMaterialId()));
				emsg.append(";\n");
//				UI.showError(String.format(Message.getString("wip.out_qty_isnot_equal"),
//						total.toString(), line.getQtyMovement().toString(), line.getMaterialId()));
//				return false;
			}
		}
		if(!"".equals(emsg.toString())) {
			emsg.append(Message.getString("common.continue_to_save"));
			if(UI.showConfirm(emsg.toString())) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
	
	private List<MovementLineLot> getLineLotsByMaterial(Long materialRrn) {
		List<MovementLineLot> lLots = new ArrayList<MovementLineLot>();
		for(MovementLineLot lineLot : getLineLots()) {
			if(lineLot.getMaterialRrn().equals(materialRrn))
				lLots.add(lineLot);
		}
		return lLots;
	}
	
	/* 删除MovementLineLot列表中选中的MovementLineLot*/
	protected void deleteAdapter() {
		try {
			TableItem[] items = viewer.getTable().getSelection();
        	if (items != null && items.length > 0){
        		TableItem item = items[0];
        		Object obj = item.getData();
        		if(obj instanceof MovementLineLot) {
        			boolean confirmDelete = UI.showConfirm(Message
        					.getString("common.confirm_delete"));
        			if (confirmDelete) {
        				getLineLots().remove(obj);
        				refresh();
        				this.setDoOprationsTrue();
        			}
        		}
        	}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}
	}

	protected boolean validLot(Lot lot) {
//		if(!transferLine.getMaterialRrn().equals(lot.getMaterialRrn())) {
//			UI.showError(String.format(Message.getString("wip.material_does't_exisit_moboms"),
//					lot.getLotId(), lot.getMaterialId(), transferLine.getMaterialId()));
//			return false;
//		}
		if(!Lot.POSITION_INSTOCK.equals(lot.getPosition())) {
			if(!Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
				UI.showError(String.format(Message.getString("wip.lot_not_in_stock"), lot.getLotId()));
				return false;				
			}
		}
		// 若为Batch，则可以再次分批调拨，如果分批的数量之和大于该批次的数量，保存时会提示批次数量不够
		if(isContainsInLineLots(lot)) {
			UI.showError(String.format(Message.getString("wip.lot_list_contains_lot"), lot.getLotId()));
			return false;
		}
		return true;
	}
	
	protected boolean isContainsInLineLots(Lot lot) {
		for(MovementLineLot lineLot : getLineLots()) {
			if(lineLot.getLotRrn().equals(lot.getObjectRrn()))
				if(Lot.LOTTYPE_SERIAL.equals(lot.getLotType()))
					return true;
		}
		return false;
	}

	protected MovementLineLot pareseMovementLineLot(MovementLine line,
			BigDecimal outQty, Lot lot, boolean isGenNewLotId) throws Exception {
		Date now = Env.getSysDate();
		MovementLineLot trsLineLot = new MovementLineLot();
		trsLineLot.setOrgRrn(Env.getOrgRrn());
		trsLineLot.setIsActive(true);
		trsLineLot.setCreated(now);
		trsLineLot.setCreatedBy(Env.getUserRrn());
		trsLineLot.setUpdated(now);
		trsLineLot.setUpdatedBy(Env.getUserRrn());
		
		if(mt != null) {
			trsLineLot.setMovementRrn(mt.getObjectRrn());
			trsLineLot.setMovementId(mt.getDocId());
		}
		trsLineLot.setMovementLineRrn(line.getObjectRrn());
		trsLineLot.setLotRrn(lot.getObjectRrn());
		if(isGenNewLotId) {
			trsLineLot.setLotId(getNextLotId(lot.getMaterialRrn(), outQty));
		} else {
			trsLineLot.setLotId(lot.getLotId());			
		}
		trsLineLot.setMaterialRrn(lot.getMaterialRrn());
		trsLineLot.setMaterialId(lot.getMaterialId());
		trsLineLot.setMaterialName(lot.getMaterialName());
		// 将用户输入的出库数量设置到outLineLot.qtyMovement中
		trsLineLot.setQtyMovement(outQty);
		return trsLineLot;
	}
	
	protected String getNextLotId(Long materialRrn, BigDecimal qty) throws Exception {
		String lotId = null;
		Material material = new Material();
		material.setObjectRrn(materialRrn);
		ADManager adManager = Framework.getService(ADManager.class);
		material = (Material)adManager.getEntity(material);
		INVManager invManager = Framework.getService(INVManager.class);
		List<Lot> list = invManager.generateBatchLot(material.getOrgRrn(), material,
				qty, 1, Env.getUserRrn());
		if(list != null && list.size() > 0) {
			lotId = list.get(0).getLotId();
		}
		return lotId;
	}
	
	protected String getWhereClause() {
		StringBuffer whereClause = new StringBuffer("");
//		if(this.transferLine != null) {
//			whereClause.append(" movementLineRrn = '");
//			whereClause.append(this.transferLine.getObjectRrn());
//			whereClause.append("' ");
//		} else 
		if(lines != null) {
			whereClause.append(" movementLineRrn IN (");
			for(MovementLine line : lines) {
				whereClause.append("'");
				whereClause.append(line.getObjectRrn());
				whereClause.append("', ");
			}
			int length = whereClause.length();
			whereClause = whereClause.delete(length - 2, length);
			whereClause.append(")");			
		}
		if(!"".equals(whereClause.toString().trim())) {
			return whereClause.toString();
		}
		return " 1 <> 1 ";
	}

	protected void setEnabled(boolean enabled) {
		itemSave.setEnabled(false);
		itemDelete.setEnabled(false);
	}
	
	protected void setPrintabled(boolean enabled) {
		this.itemPrintLot.setEnabled(enabled);
	}

	public List<MovementLineLot> getLineLots() {
		if(lineLots == null) {
			lineLots = new ArrayList<MovementLineLot>();
		}
		return lineLots;
	}

	public void setLineLots(List<MovementLineLot> lineLots) {
		this.lineLots = lineLots;
	}

	@Override
	protected void createLotInfoComposite(Composite client, FormToolkit toolkit) {
			Composite comp = toolkit.createComposite(client, SWT.BORDER);
			comp.setLayout(new GridLayout(3, false));
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			comp.setLayoutData(gridData);
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
			txtLotId.setFocus();
			
			Composite radioComp = toolkit.createComposite(comp, SWT.NONE);
			radioComp.setLayout(new GridLayout(8, false));
			GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
	//		gridData2.horizontalSpan = 2;
			radioComp.setLayoutData(gridData2);
			Button b1 = toolkit.createButton(radioComp, "仅输入", SWT.RADIO);
			b1.setSelection(true);
			b1.addSelectionListener(new SelectionAdapter(){
				@Override
				public void widgetSelected(SelectionEvent e) {
					optional = 0;
				}
			});
			Button b2 = toolkit.createButton(radioComp, "连续序号", SWT.RADIO);
			b2.addSelectionListener(new SelectionAdapter(){
				@Override
				public void widgetSelected(SelectionEvent e) {
					optional = 1;
				}
			});
	//		Button b3 = toolkit.createButton(radioComp, "选取", SWT.RADIO);
	//		b3.addSelectionListener(new SelectionAdapter(){
	//			@Override
	//			public void widgetSelected(SelectionEvent e) {
	//				optional = 2;
	//			}
	//		});
		}
}

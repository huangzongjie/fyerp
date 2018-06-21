package com.graly.erp.inv.out;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import au.com.bytecode.opencsv.CSVWriter;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.barcode.LotMasterSection;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.model.Warehouse;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.custom.XSearchComposite;
import com.graly.framework.base.ui.forms.field.SearchField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class OutLineLotSection extends LotMasterSection {
	private static final Logger logger = Logger.getLogger(OutLineLotSection.class);

	protected MovementLine outLine;
	protected MovementOut out;
	
	private List<MovementLineLot> lineLots;
	
	protected boolean isView = false;
	protected List<Lot> lots;

	protected int optional;

	protected List<String> errorLots = new ArrayList<String>();
	

	protected MovementLine selectedOutLine;
	protected Object parentObject;	
	protected ToolItem itemExport;
	
	public OutLineLotSection(ADTable adTable, LotDialog parentDialog) {
		super(adTable, parentDialog);
	}
	
	public OutLineLotSection(ADBase parent, ADBase child, ADTable adTable,
			OutLineLotDialog olld, boolean isView) {
		super(adTable, olld);
		this.out =(MovementOut)parent;
		this.outLine = (MovementLine)child;
		this.isView = isView;
	}
	
	public OutLineLotSection(ADTable adTable, MovementOut out,
			MovementLine outLine, List<MovementLine> lines,
			OutLineLotDialog olld, boolean isView) {
		this(out, outLine, adTable, olld, isView);
		this.lines = lines;
	}
	
    public void createToolBar(Section section) {
        ToolBar tBar = new ToolBar(section, SWT. FLAT | SWT.HORIZONTAL );

         new ToolItem(tBar, SWT. SEPARATOR);
        createToolItemSave(tBar);
         new ToolItem(tBar, SWT. SEPARATOR);
        createToolItemDelete(tBar);
        new ToolItem(tBar, SWT. SEPARATOR);
        createToolItemExport(tBar);
        section.setTextClient(tBar);
  }
    
	protected void createToolItemExport(ToolBar tBar) {
		itemExport = new ToolItem(tBar, SWT.PUSH);
		itemExport.setText(Message.getString("common.export"));
		itemExport.setImage(SWTResourceCache.getImage("export"));
		itemExport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				exportAdapter();
			}
		});
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
		gridData2.horizontalSpan = 2;
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
	
	protected void createTableViewer(Composite client, FormToolkit toolkit) {
		lotManager = new TableListManager(adTable);
		viewer = (TableViewer)lotManager.createViewer(client, toolkit);
	}
	
	protected void setItemInitStatus() {
		if (isView || (out != null && !Movement.STATUS_DRAFTED.equals(out.getDocStatus()))) {
			itemSave.setEnabled(false);
			itemDelete.setEnabled(false);
		}
		if(Env.getOrgRrn() ==12644730L && isView || (out != null && Movement.STATUS_APPROVED.equals(out.getDocStatus()))){
			itemSave.setEnabled(true);
			itemDelete.setEnabled(true);
		}
	}
	
	protected void saveAdapter() {
		try {
			if(lines != null && lines.size() > 0) {
				if(validateAll()) {
					//奔泰特殊处理
					INVManager invManager = Framework.getService(INVManager.class);
					if(Env.getOrgRrn() == 12644730L){
						if(!MovementOut.STATUS_APPROVED.equals(out.getDocStatus())){
							UI.showError("奔泰出库单必须先审核，后挂批次");
							return;
						}
						
						for(MovementLine movementLine : lines){
							ADManager adManager = Framework.getService(ADManager.class);
							List<MovementLineLot>  lineLots =adManager.getEntityList(Env.getOrgRrn(), MovementLineLot.class,Integer.MAX_VALUE,
									"movementRrn = "+out.getObjectRrn()+" and movementLineRrn ="+movementLine.getObjectRrn(),"");
							if(lineLots!=null && lineLots.size() > 0){
								UI.showError("已经保存过不能再次保存");
								return;
							}
							break;//只执行一次，因为：要么lineLot全部存在要么全部不存在
						}
						invManager.saveMovementOutLineBT(out, lines, getOutType(), Env.getUserRrn());
					}else{
						invManager.saveMovementOutLine(out, lines, getOutType(), Env.getUserRrn());
					}
					UI.showInfo(Message.getString("common.save_successed"));
					this.setIsSaved(true);
					((OutLineLotDialog)parentDialog).buttonPressed(IDialogConstants.CANCEL_ID);
				}				
			}
		} catch(Exception e) {
			logger.error("Error at OutLineLotSection : saveAdapter() " + e);
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected MovementOut.OutType getOutType() {
		return MovementOut.OutType.SOU;
	}

	// 验证各个入库物料的入库数量是否等于对应的批次数量之和
	protected boolean validateAll() {
		if(lines == null || lines.size() == 0)
			return false;
		BigDecimal total = null;
		List<MovementLineLot> lLots = null;
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
				UI.showError(String.format(Message.getString("wip.out_qty_isnot_equal"),
						total.toString(), line.getQtyMovement().toString(), line.getMaterialId()));
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

	public void setLineLots(List<MovementLineLot> lineLots) {
		this.lineLots = lineLots;
	}

	// 重载addLot(), 实现将lot转化为movementLineLot
//	protected void addLot() {
//		String lotId = txtLotId.getText();
//		try {			
//			if(lotId != null && !"".equals(lotId)) {
//				
//				INVManager invManager = Framework.getService(INVManager.class);
//				lot = invManager.getLotByLotId(Env.getOrgRrn(), lotId);
//				if(lot == null || lot.getMaterialRrn() == null) {
//					txtLotId.setForeground(SWTResourceCache.getColor("Red"));
//					UI.showError(Message.getString("inv.lotnotexist"));
//					return;
//				}
//				if (lot.getIsUsed() || Lot.POSITION_OUT.equals(lot.getPosition())) {
//					UI.showError(String.format(Message.getString("wip.lot_is_used"), lot.getLotId()));
//					return;
//				}
//				if(!Lot.POSITION_INSTOCK.equals(lot.getPosition())) {
//					if(!Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
//						UI.showError(String.format(Message.getString("inv.lot_not_in"), lot.getLotId()));
//						return;						
//					}
//				}
//				// 如果l不为null，表示lot所对应的物料在lines中或与outLine对应的物料一致
//				MovementLine l = this.isContainsLot(lot);
//				if(l == null) {
//					return;
//				}
//				
//				MovementLineLot lineLot = null;
//				if(Lot.LOTTYPE_BATCH.equals(lot.getLotType())
//						|| Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
//					Warehouse wh = getOutWarehouse();
//					if(wh == null) {
//						UI.showError(Message.getString("inv.batch_must_be_select_warehouse_first"));
//						return;
//					}
//					OutQtySetupDialog outQtyDialog = new OutQtySetupDialog(UI.getActiveShell(), outLine, lot, wh);
//					int openId = outQtyDialog.open();
//					if(openId == Dialog.OK) {
//						lineLot = pareseMovementLineLot(l, outQtyDialog.getInputQty(), lot);
//					} else if(openId == Dialog.CANCEL) 
//						return;
//				} else if(Lot.LOTTYPE_SERIAL.equals(lot.getLotType())) {
//					lineLot = pareseMovementLineLot(l, lot.getQtyCurrent(), lot);
//				}
//				if(contains(lineLot)) {
//					UI.showError(String.format(Message.getString("wip.lot_list_contains_lot"), lot.getLotId()));
//					return;
//				}
//				getLineLots().add(lineLot);						
//				refresh();
//				setDoOprationsTrue();
//			}
//		} catch(Exception e) {
//			txtLotId.setForeground(SWTResourceCache.getColor("Red"));
//			logger.error("Error at LotMasterSection ：addLot() ", e);
//			ExceptionHandlerManager.asyncHandleException(e);
//		} finally {
//			txtLotId.selectAll();
//		}
//	}
	
	protected MovementLine isContainsLot(Lot lot) {
		MovementLine l = null;
		if(outLine != null && outLine.getMaterialRrn().equals(lot.getMaterialRrn()))
			return outLine;
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
	
	/* 删除MovementLineLot列表中选中的MovementLineLot*/
	protected void deleteAdapter() {
		try {
			TableItem[] items = viewer.getTable().getSelection();
        	if (items != null && items.length > 0){
        		TableItem item = items[0];
        		Object obj = item.getData();
        		if(obj != null && obj instanceof MovementLineLot) {
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
	
	protected Warehouse getOutWarehouse() {
		if(out == null || out.getWarehouseRrn() == null)
			return null;
		Warehouse wh = new Warehouse();
		wh.setObjectRrn(out.getWarehouseRrn());
		return wh;
	}
	
	public void refresh() {
		if(lotManager != null && viewer != null) {
			lotManager.setInput(getLineLots());
			lotManager.updateView(viewer);
			createSectionDesc(section);
		}
	}
	
	protected List<MovementLineLot> getLineLots() {
		if(lineLots == null) {
			lineLots = new ArrayList<MovementLineLot>();
			return lineLots;
		}
		return lineLots;
	}
	
	protected MovementLineLot pareseMovementLineLot(MovementLine line, BigDecimal uoutQtytQty, Lot lot) {
		Date now = Env.getSysDate();
		MovementLineLot outLineLot = new MovementLineLot();
		outLineLot.setOrgRrn(Env.getOrgRrn());
		outLineLot.setIsActive(true);
		outLineLot.setCreated(now);
		outLineLot.setCreatedBy(Env.getUserRrn());
		outLineLot.setUpdated(now);
		outLineLot.setUpdatedBy(Env.getUserRrn());
		
		if(out != null) {
			outLineLot.setMovementRrn(out.getObjectRrn());
			outLineLot.setMovementId(out.getDocId());
		}
		outLineLot.setMovementLineRrn(line.getObjectRrn());
		outLineLot.setLotRrn(lot.getObjectRrn());
		outLineLot.setLotId(lot.getLotId());
		outLineLot.setMaterialRrn(lot.getMaterialRrn());
		outLineLot.setMaterialId(lot.getMaterialId());
		outLineLot.setMaterialName(lot.getMaterialName());
		// 将用户输入的出库数量设置到outLineLot.qtyMovement中
		outLineLot.setQtyMovement(uoutQtytQty);
		if(Env.getOrgRrn() == 139420L){
			outLineLot.setReverseField7(lot.getReverseField7());
			outLineLot.setReverseField8(lot.getReverseField8());
			outLineLot.setReverseField9(lot.getReverseField9());
		}
		return outLineLot;
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
            	if(Env.getOrgRrn()==139420L){
                	if(lineLot!=null && lineLot.getLotRrn()!=null){
                		Lot lot = new Lot();
                		lot.setObjectRrn(lineLot.getLotRrn());
                		lot = (Lot) manager.getEntity(lot);
                		lineLot.setReverseField9(lot.getReverseField9());
                		lineLot.setReverseField8(lot.getReverseField8());
                		lineLot.setReverseField7(lot.getReverseField7());
                	}
            	}
            	l.add(lineLot);
            }
            setLineLots(l);
            refresh();
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
        }
	}
	
	protected String getWhereClause() {
		StringBuffer whereClause = new StringBuffer("");
//		if(this.outLine != null) {
//			whereClause.append(" movementLineRrn = '");
//			whereClause.append(this.outLine.getObjectRrn());
//			whereClause.append("' ");
//		} else  
		if(lines != null){
			whereClause.append(" movementLineRrn IN (");
			for(MovementLine line : lines) {
				whereClause.append("'");
				whereClause.append(line.getObjectRrn());
				whereClause.append("', ");
			}
			int length = whereClause.length();
			whereClause = whereClause.delete(length - 2, length);
			whereClause.append(")");			
			return whereClause.toString();
		}
		return " 1 <> 1 ";
	}
	
	public MovementOut getMovementOut() {
		return out;
	}

	public void setTransfer(MovementOut out) {
		this.out = out;
	}

	class LotSearchField extends SearchField { 
		 public LotSearchField(String id, ADTable adTable, ADRefTable refTable,
		    		String whereClause, int style) {
			 super(id, adTable, refTable, whereClause, style);
		 }
		 
		 public XSearchComposite getXSearch() {
			 return this.xSearch;
		 }
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
			logger.error("Error at OutLineLotSection ：addLot() ", e);
		} 
	}
	
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
			
			}else
			if(lotId != null && !"".equals(lotId)) {
				
				INVManager invManager = Framework.getService(INVManager.class);
				lot = invManager.getLotByLotId(Env.getOrgRrn(), lotId);
//				if(Env.getOrgRrn()==139420L){
//					if(out!=null && out.getWmsWarehouse()!=null){
//						lot = invManager.getLotByLotIdInWms(Env.getOrgRrn(), lotId);
//					}else{
//						lot = invManager.getLotByLotIdNoWms(Env.getOrgRrn(), lotId);
//					}
//					
//				}else{
//					lot = invManager.getLotByLotId(Env.getOrgRrn(), lotId);
//				}
				if(lot == null || lot.getMaterialRrn() == null) {
					txtLotId.setForeground(SWTResourceCache.getColor("Red"));
					UI.showError(Message.getString("inv.lotnotexist"));
					return;
				}
				if(Env.getOrgRrn() !=12644730L){//除奔泰外其他区域都需要校验
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
					if(out!=null){//WMS判断，临时占用该字段
						lot.setDelayDept(out.getWmsWarehouse());
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
			logger.error("Error at OutLineLotSection ：addLot(String lotId) ", e);
			if(e instanceof ClientException && "inv.lotnotexist".equals(((ClientException)e).getErrorCode())){
				errorLots.add(lotId);
			}else{
				ExceptionHandlerManager.asyncHandleException(e);
			}
		} finally {
			txtLotId.selectAll();
		}
	}
	
	protected void exportAdapter() {
		try {
			FileDialog dialog = new FileDialog(UI.getActiveShell(), SWT.SAVE);
			dialog.setFilterNames(new String[] { "CSV (*.csv)" });
			dialog.setFilterExtensions(new String[] { "*.csv" }); 
			String fn = dialog.open();
			if (fn != null) {
				Table table = ((TableViewer)viewer).getTable();
				String[][] datas = new String[table.getItemCount() + 1][table.getColumnCount()];
				for (int i = 0; i < table.getColumnCount(); i++) {
					TableColumn column = table.getColumn(i);
					datas[0][i] = column.getText();
				}
				for (int i = 0; i < table.getItemCount(); i++) {
					TableItem item = table.getItem(i);
					for (int j = 0; j < table.getColumnCount(); j++) {
						datas[i + 1][j] = item.getText(j);
					}
				}
				
				File file = new File(fn);
				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();
				CSVWriter writer = new CSVWriter(new FileWriter(file));
		        for (int i = 0; i < datas.length; i++) {
		            writer.writeNext(datas[i]);
		        }
		        writer.close();
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
}

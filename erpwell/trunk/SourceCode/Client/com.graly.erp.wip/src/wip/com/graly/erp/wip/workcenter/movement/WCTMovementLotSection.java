package com.graly.erp.wip.workcenter.movement;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.barcode.LotMasterSection;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.wip.model.WCTMovement;
import com.graly.erp.wip.model.WCTMovementLine;
import com.graly.erp.wip.model.WCTMovementLineLot;
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
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wip.model.Lot;


public class WCTMovementLotSection extends LotMasterSection{
	private static final Logger logger = Logger.getLogger(WCTMovementLotSection.class);

	protected WCTMovement wctMovement;
	protected WCTMovementLine wctMovementLine;
	protected List<WCTMovementLine> wctMovementLines;
	private List<WCTMovementLineLot> wctMovementLineLots;
	
	protected boolean isView = false;

	protected int optional;

	protected List<String> errorLots = new ArrayList<String>();
	
	public WCTMovementLotSection(ADTable adTable, LotDialog parentDialog) {
		super(adTable, parentDialog);
	}
	
	public WCTMovementLotSection(ADBase parent, ADBase child, ADTable adTable,
			WCTMovementLotDialog olld, boolean isView) {
		super(adTable, olld);
		this.wctMovementLine =(WCTMovementLine)child;
		this.wctMovement =(WCTMovement)parent;
//		this.outLine = (MovementLine)child;
		this.isView = isView;
	}
	
	public WCTMovementLotSection(ADTable adTable, WCTMovement out,WCTMovementLine outLine, List<WCTMovementLine> lines,WCTMovementLotDialog olld, boolean isView) {
		this(out, outLine, adTable, olld, isView);
		this.wctMovementLines = lines;
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		section.setTextClient(tBar);
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
//		if (isView || (out != null && !Movement.STATUS_DRAFTED.equals(out.getDocStatus()))) {
//			itemSave.setEnabled(false);
//			itemDelete.setEnabled(false);
//		}
	}
	
	protected void saveAdapter() {
		try {
			if(wctMovementLines != null && wctMovementLines.size() > 0) {
				if(validateAll()) {
					WipManager wipManager = Framework.getService(WipManager.class);
					wipManager.saveWCTMovementLineLots( wctMovementLines, wctMovementLineLots ,Env.getUserRrn());
//					wipManager.saveWCTMovementLine(wctMovement, wctMovementLines, Env.getOrgRrn(), Env.getUserRrn());
					UI.showInfo(Message.getString("common.save_successed"));
					this.setIsSaved(true);
					((WCTMovementLotDialog)parentDialog).buttonPressed(IDialogConstants.CANCEL_ID);
				}				
			}
		} catch(Exception e) {
			logger.error("Error at WCTMovementLotSection : saveAdapter() " + e);
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	private List<WCTMovementLineLot> getLineLotsByMaterial(Long materialRrn) {
		List<WCTMovementLineLot> lLots = new ArrayList<WCTMovementLineLot>();
		for(WCTMovementLineLot lineLot : getWctMovementLineLots()) {
			if(lineLot.getMaterialRrn().equals(materialRrn))
				lLots.add(lineLot);
		}
		return lLots;
	}

	// 验证各个入库物料的入库数量是否等于对应的批次数量之和
	protected boolean validateAll() {
		if(wctMovementLines == null || wctMovementLines.size() == 0)
			return false;
		BigDecimal total = null;
		List<WCTMovementLineLot> lLots = null;
		for(WCTMovementLine line : wctMovementLines) {
			total = BigDecimal.ZERO;
			lLots = getLineLotsByMaterial(line.getMaterialRrn());
			for(WCTMovementLineLot linelot : lLots) {
				if(linelot.getLotId() == null || "".equals(linelot.getLotId().trim())) {
					UI.showError(Message.getString("inv.invalid_lotId"));
					return false;
				}
				total = total.add(linelot.getQtyMovement());
			}
			if(total.compareTo(line.getQtyMovement()) == 0) {
				line.setLineLots(lLots);
				continue;
			} else {
				UI.showError(String.format(Message.getString("wip.out_qty_isnot_equal"),
						total.toString(), line.getQtyMovement().toString(), line.getMaterialId()));
				return false;
			}			
		}
		return true;
	}

	
	/* 删除MovementLineLot列表中选中的MovementLineLot*/
	protected void deleteAdapter() {
		try {
			TableItem[] items = viewer.getTable().getSelection();
        	if (items != null && items.length > 0){
        		TableItem item = items[0];
        		Object obj = item.getData();
        		if(obj != null && obj instanceof WCTMovementLineLot) {
        			boolean confirmDelete = UI.showConfirm(Message
        					.getString("common.confirm_delete"));
        			if (confirmDelete) {
        				getWctMovementLineLots().remove(obj);
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
	
	
	public void refresh() {
		if(lotManager != null && viewer != null) {
			lotManager.setInput(getWctMovementLineLots());
			lotManager.updateView(viewer);
			createSectionDesc(section);
		}
	}
	
	
	
	public List<WCTMovementLineLot> getWctMovementLineLots() {
		if(wctMovementLineLots == null) {
			wctMovementLineLots = new ArrayList<WCTMovementLineLot>();
			return wctMovementLineLots;
		}
		return wctMovementLineLots;
	}

	public void setWctMovementLineLots(List<WCTMovementLineLot> wctMovementLineLots) {
		this.wctMovementLineLots = wctMovementLineLots;
	}


	protected void initTableContent() {
		List<ADBase> list = null;
		try {
        	ADManager manager = Framework.getService(ADManager.class);
            list = manager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(), 
            		Env.getMaxResult(), getWhereClause(), getOrderByClause());
            List<WCTMovementLineLot> wctMovementLineLots = new ArrayList<WCTMovementLineLot>();
            for(ADBase ab : list) {
            	WCTMovementLineLot wctMovementlineLot = (WCTMovementLineLot)ab;
            	wctMovementLineLots.add(wctMovementlineLot);
            }
            setWctMovementLineLots(wctMovementLineLots);
            refresh();
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
        }
	}
	
	protected String getWhereClause() {
		StringBuffer whereClause = new StringBuffer("");
//		if(this.wctMovementLine != null) {
//			whereClause.append(" movementLineRrn = '");
//			whereClause.append(this.wctMovementLine.getObjectRrn());
//			whereClause.append("' ");
//		} else  
		if(wctMovementLines != null){
			whereClause.append(" movementLineRrn IN (");
			for(WCTMovementLine line : wctMovementLines) {
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
			logger.error("Error at WCTMovementLotSections ：addLot() ", e);
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
				
				//选择的行不为空  判断 lot的物料批号和行是否相同
//				if(wctMovementLine !=null ){
//					if(!lot.getMaterialId().equals(wctMovementLine.getMaterialId())){
//						UI.showError("对不起与所选择的物料批号不相同");
//						return;
//					}
//				}
//				//选择的行为空   判断lot的物料批号是否在list当中
//				if(wctMovementLine ==null ){
					boolean isInlines = true;
					for(WCTMovementLine wctLine : wctMovementLines){
						if(!lot.getMaterialId().equals(wctLine.getMaterialId())){
							isInlines =false;
							break;
						}
						
					}
					if(!isInlines){ 
						UI.showError("对不起，LOT的物料批号不符合要求");
						return;
					}
//				}

				WCTMovementLineLot wctMovementLineLot = null;
				if(Lot.LOTTYPE_BATCH.equals(lot.getLotType())
						|| Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
					Warehouse wh = invManager.getWriteOffWarehouse(Env.getOrgRrn());
					if(wh == null) {
						UI.showError(Message.getString("inv.batch_must_be_select_warehouse_first"));
						return;
					}
					WCTOutQtySetupDialog outQtyDialog = new WCTOutQtySetupDialog(UI.getActiveShell(), wctMovementLine, lot, wh);
					int openId = outQtyDialog.open();
					if(openId == Dialog.OK) {
						wctMovementLineLot = pareseWCTMovementLineLot(wctMovementLine, outQtyDialog.getInputQty(), lot);
					} else if(openId == Dialog.CANCEL) 
						return;
				} else if(Lot.LOTTYPE_SERIAL.equals(lot.getLotType())) {
					wctMovementLineLot = pareseWCTMovementLineLot(wctMovementLine, lot.getQtyCurrent(), lot);
				}
				
				for(WCTMovementLineLot wctLineLot :wctMovementLineLots){
					if(wctLineLot.getLotId().equals(wctMovementLineLot.getLotId())){
						UI.showError("对不起，"+wctMovementLineLot.getLotId()+"已经存在！");
						return;
					}
				}
				
				
				
				getWctMovementLineLots().add(wctMovementLineLot);						
				refresh();
				setDoOprationsTrue();
			}
		} catch(Exception e) {
			txtLotId.setForeground(SWTResourceCache.getColor("Red"));
			logger.error("Error at WCTMovementLotSection ：addLot(String lotId) ", e);
			if(e instanceof ClientException && "inv.lotnotexist".equals(((ClientException)e).getErrorCode())){
				errorLots.add(lotId);
			}else{
				ExceptionHandlerManager.asyncHandleException(e);
			}
		} finally {
			txtLotId.selectAll();
		}
	}

	public List<WCTMovementLine> getWctMovementLines() {
		return wctMovementLines;
	}

	public void setWctMovementLines(List<WCTMovementLine> wctMovementLines) {
		this.wctMovementLines = wctMovementLines;
	}
	protected WCTMovementLineLot pareseWCTMovementLineLot(WCTMovementLine line, BigDecimal outQty, Lot lot) {
		
		WCTMovementLineLot wctMovementLineLot =  new WCTMovementLineLot();
		wctMovementLineLot.setOrgRrn(Env.getOrgRrn());
		wctMovementLineLot.setIsActive(true);
		if(line != null){
			wctMovementLineLot.setMovementRrn(line.getMovementRrn());
			wctMovementLineLot.setMovementLineRrn(line.getObjectRrn());
		}else{
			for(WCTMovementLine wctLine :getWctMovementLines()){
				if(wctLine.getMaterialRrn().equals(lot.getMaterialRrn())){
					wctMovementLineLot.setMovementRrn(wctLine.getMovementRrn());
					wctMovementLineLot.setMovementLineRrn(wctLine.getObjectRrn());
				}
			}
		}

		wctMovementLineLot.setLotRrn(lot.getObjectRrn());
		wctMovementLineLot.setLotId(lot.getLotId());
		wctMovementLineLot.setMaterialRrn(lot.getMaterialRrn());
		wctMovementLineLot.setMaterialId(lot.getMaterialId());
		wctMovementLineLot.setMaterialName(lot.getMaterialName());
		wctMovementLineLot.setQtyMovement(outQty);	
		return wctMovementLineLot;
	}
	
	
}

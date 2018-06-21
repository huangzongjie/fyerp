package com.graly.erp.inv.adjust.in;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.barcode.LotMasterSection;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.lotprint.LotPrintDialog;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.Warehouse;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class AdjustInLotSection extends LotMasterSection {
	private static final Logger logger = Logger.getLogger(AdjustInLotSection.class);

	protected MovementLine movementInLine;
	protected MovementIn in;
	protected List<ADBase> selectedItems = new ArrayList<ADBase>();
	protected ToolItem itemPrint;
	private List<MovementLineLot> lineLots;
	protected boolean isView = false;
	private MovementLineLot selectedLineLot;
	protected MovementIn.InType inType;
	
	public AdjustInLotSection(ADTable adTable, LotDialog parentDialog) {
		super(adTable, parentDialog);
	}

	public AdjustInLotSection(ADTable adTable, MovementIn in,
			MovementLine movementInLine) {
		super(adTable);
		this.in = in;
		this.movementInLine = movementInLine;
	}

	public AdjustInLotSection(ADTable adTable, MovementIn in,
			MovementLine movementInLine, List<MovementLine> lines, boolean isView) {
		this(adTable, in, movementInLine);
		this.lines = lines;
		this.isView = isView;
	}

	public void createContents(IManagedForm form, Composite parent) {
		super.createContents(form, parent, Section.DESCRIPTION | Section.TITLE_BAR);
		setEnableByStatus();
	}
	
	protected void setEnableByStatus() {
		// 若以关联进入查看或审核后，则各控件不可用
		if(isView || (in != null && MovementIn.STATUS_APPROVED.equals(in.getDocStatus()))) {
			setEnable(false);
			setPrintEnable(true);
			((AdjustInLotTableManager)lotManager).setCanEdit(false);
		}
//		else if(in != null && MovementIn.STATUS_DRAFTED.equals(in.getDocStatus())) {
//			((AdjustInLotTableManager)lotManager).setCanEdit(true);
//		}
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemPrint(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createTableViewer(Composite client, FormToolkit toolkit) {
		lotManager = new AdjustInLotTableManager(adTable);
		viewer = (TableViewer)lotManager.createViewer(client, toolkit);
		lotManager.updateView(viewer);
	}

	protected void createToolItemPrint(ToolBar tBar) {
		itemPrint = new ToolItem(tBar, SWT.PUSH);
		itemPrint.setText(Message.getString("common.print"));
		itemPrint.setImage(SWTResourceCache.getImage("print"));
		itemPrint.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				printAdapter();
			}
		});
	}
	
	// 重载addLot(), 实现将lot转化为movementLineLot
	protected void addLot() {
		String lotId = txtLotId.getText();
		try {			
			if(lotId != null && !"".equals(lotId)) {				
				INVManager invManager = Framework.getService(INVManager.class);
				lot = invManager.getLotByLotId(Env.getOrgRrn(), lotId);
				if(lot == null || lot.getMaterialRrn() == null
						|| Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
					if(lot == null) {
						// 判断是否为MATERIAL类型物料
						String whereClause = " materialId = '" + lotId + "' ";
						ADManager adManager = Framework.getService(ADManager.class);
						List<Material> list = adManager.getEntityList(Env.getOrgRrn(), Material.class,
								Env.getMaxResult(), whereClause, null);
						Material material = null;
						if(list != null && list.size() > 0)
							material = list.get(0);
						if(material == null || !Lot.LOTTYPE_MATERIAL.equals(material.getLotType())) {
							txtLotId.setForeground(SWTResourceCache.getColor("Red"));
							UI.showError(Message.getString("inv.lotnotexist"));
							return;
						}
						lot = new Lot();
						lot.setMaterialId(material.getMaterialId());
						lot.setMaterialName(material.getName());
						lot.setMaterialRrn(material.getObjectRrn());
						lot.setPosition(Lot.POSITION_GEN);
						lot.setLotType(Lot.LOTTYPE_MATERIAL);
					}
					MovementLine l = this.isContainsLot(lot);
					if(l == null) {
						return;
					}
					InQtySetupDialog inQtyDialog = new InQtySetupDialog(UI.getActiveShell(),
							null, lot, null);
					int openId = inQtyDialog.open();
					if(openId == Dialog.OK) {
						MovementLineLot lineLot = pareseMovementLineLot(l, inQtyDialog.getInputQty(), lot);
						addLineLotToTable(lineLot);
					}
				}
				else if(validLot(lot)) {
					// 如果l不为null，表示lot所对应的物料在lines中或与inLine对应的物料一致
					MovementLine l = this.isContainsLot(lot);
					if(l == null) {
						return;
					}
//					MovementLineLot lineLot = null;
//					lineLot = pareseMovementLineLot(l, lot.getQtyCurrent(), lot);
//					addLineLotToTable(lineLot);
					
					InQtySetupDialog inQtyDialog = new InQtySetupDialog(UI.getActiveShell(),
							null, lot, null);
					int openId = inQtyDialog.open();
					if(openId == Dialog.OK) {
						MovementLineLot lineLot = pareseMovementLineLot(l, inQtyDialog.getInputQty(), lot);
						addLineLotToTable(lineLot);
					}
				}
			}
		} catch(Exception e) {
			txtLotId.setForeground(SWTResourceCache.getColor("Red"));
			logger.error("Error at LotMasterSection ：addLot() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		} finally {
			txtLotId.selectAll();
		}
	}
	
	protected void addLineLotToTable(MovementLineLot lineLot) {
		getLineLots().add(lineLot);						
		refresh();
		setDoOprationsTrue();
	}
	
	protected MovementLine isContainsLot(Lot lot) {
		MovementLine l = null;
		if(movementInLine != null && movementInLine.getMaterialRrn().equals(lot.getMaterialRrn()))
			return movementInLine;
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
				UI.showError(String.format(Message.getString("inv.material_by_lot_isnot_exist_inlines"),
						lot.getLotId(), lot.getMaterialId()));
				return l;
			}
		}
		return l;
	}

	// 验证lot对应的物料必须在lines中, 并且lot不能在IQC, GEN和WIP上并且是未使用的
	protected boolean validLot(Lot lot) {
		if(isContainsInLineLots(lot)) {
			UI.showError(String.format(Message.getString("wip.lot_list_contains_lot"), lot.getLotId()));
			return false;
		}
		boolean flag = false;
		if(lines != null && lines.size() > 0) {
			for(MovementLine line : lines) {
				if(line.getMaterialRrn().equals(lot.getMaterialRrn())) {
					flag = true;
					break;
				}
			}
		}
		if(!flag) {
			UI.showError(String.format(Message.getString("inv.material_by_lot_isnot_exist_inlines"),
					lot.getLotId(), lot.getMaterialId()));
			return false;
		}
		//调整入库不判断批次位置
//		if (!(Lot.POSITION_IQC.equals(lot.getPosition()) ||
//				Lot.POSITION_GEN.equals(lot.getPosition()) || Lot.POSITION_WIP.equals(lot.getPosition()))) {
//			UI.showError(String.format(Message.getString("inv.lot_already_in"), lot.getLotId()));
//			return false;
//		} else {
//			if (lot.getIsUsed()) {
//				UI.showError(String.format(Message.getString("wip.lot_is_used"), lot.getLotId()));
//				return false;
//			}
//		}
		return true;
	}
	
	// 入库时同一批次为Batch类型时也不能再次入库，因为入库不存在分批入库
	protected boolean isContainsInLineLots(Lot lot) {
		for(MovementLineLot lineLot : getLineLots()) {
			if(lot.getObjectRrn().equals(lineLot.getLotRrn()))
					return true;
		}
		return false;
	}

	protected void saveAdapter() {
		try {
			if(lines != null && lines.size() > 0) {
				if(validateAll()) {
					INVManager invManager = Framework.getService(INVManager.class);
					in = invManager.saveMovementInLine(in, lines, MovementIn.InType.ADIN, Env.getUserRrn());
					UI.showInfo(Message.getString("common.save_successed"));
					((AdjustInLotTableManager)lotManager).setCanEdit(false);
					this.setIsSaved(true);
					this.setEnable(false);
					setPrintEnable(true);
				}
			}
		} catch (Exception e) {
			logger.error("GenerateLotSection saveAdapter(): error!");
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	protected MovementLineLot pareseMovementLineLot(MovementLine line, BigDecimal outQty, Lot lot) {
		Date now = Env.getSysDate();
		MovementLineLot inLineLot = new MovementLineLot();
		inLineLot.setOrgRrn(Env.getOrgRrn());
		inLineLot.setIsActive(true);
		inLineLot.setCreated(now);
		inLineLot.setCreatedBy(Env.getUserRrn());
		inLineLot.setUpdated(now);
		inLineLot.setUpdatedBy(Env.getUserRrn());
		
		if(in != null) {
			inLineLot.setMovementRrn(in.getObjectRrn());
			inLineLot.setMovementId(in.getDocId());
		}
		inLineLot.setMovementLineRrn(line.getObjectRrn());
		inLineLot.setLotRrn(lot.getObjectRrn());
		inLineLot.setLotId(lot.getLotId());
		inLineLot.setMaterialRrn(lot.getMaterialRrn());
		inLineLot.setMaterialId(lot.getMaterialId());
		inLineLot.setMaterialName(lot.getMaterialName());
		// 将用户输入的出库数量设置到outLineLot.qtyMovement中
		inLineLot.setQtyMovement(outQty);
		return inLineLot;
	}

	// 将MovementLineLot改为Lot
	protected void printAdapter() {
		try {
			this.lineLots = (List<MovementLineLot>)viewer.getInput();
			if(lineLots != null && lineLots.size() != 0) {
				List<Lot> lots = new ArrayList<Lot>();
				for(MovementLineLot lineLot : lineLots) {
					Lot lot = new Lot();
					lot.setObjectRrn(lineLot.getLotRrn());
					lot.setLotId(lineLot.getLotId());
					lots.add(lot);
					if(selectedLineLot != null) {
						if(lot.getLotId() != null && selectedLineLot.getLotId().equals(lot.getLotId())) {
							this.selectLot = lot;
						}
					}
				}
				LotPrintDialog printDialog = new LotPrintDialog(lots, this.selectLot);
				printDialog.open();
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	public void refresh() {
		if(lotManager != null && viewer != null) {
			lotManager.setInput(getLineLots());
			lotManager.updateView(viewer);
			createSectionDesc(section);
		}
	}
	
	// 验证各个入库物料的入库数量是否等于对应的批次数量之和
	protected boolean validateAll() {
		if(lines == null || lines.size() == 0)
			return false;
		BigDecimal total = null;
		List<MovementLineLot> mLineLots = null;
		for(MovementLine line : lines) {
			if(Lot.LOTTYPE_MATERIAL.equals(line.getLotType()))
				continue;
			total = BigDecimal.ZERO;
			mLineLots = getLineLotsByMaterial(line.getMaterialRrn());
			for(MovementLineLot lineLot : mLineLots) {
				if(lineLot.getLotId() == null || "".equals(lineLot.getLotId().trim())) {
					UI.showError(Message.getString("inv.invalid_lotId"));
					return false;
				}
				total = total.add(lineLot.getQtyMovement());
			}
			if(total.compareTo(line.getQtyMovement()) == 0) {
				line.setMovementLots(mLineLots);
				continue;
			} else {
				UI.showError(String.format(Message.getString("inv.lot_qtyTotal_isnot_equals_inQty"),
						total.toString(), line.getQtyMovement().toString(), line.getMaterialId()));
				return false;
			}
		}
		return true;
	}
	
//	protected boolean validateAll(int other) {
//		if(lines == null || lines.size() == 0)
//			return false;
//		Set<Material> materials = new LinkedHashSet<Material>();
//		for(MovementLine line : lines) {
//			if(!materials.contains(line.getMaterialRrn())) {
//				materials.add(line.getMaterial());
//			}
//		}
//		BigDecimal lineQtyTotal = null, total = null;
//		List<MovementLineLot> mLineLots = null;
//		for(Material m : materials) {
//			if(Lot.LOTTYPE_MATERIAL.equals(m.getLotType()))
//				continue;
//			lineQtyTotal = getLineMoveInQtyByMaterial(m.getObjectRrn()); //得到某物料的入库总数
//			total = BigDecimal.ZERO;
//			mLineLots = getLineLotsByMaterial(m.getObjectRrn());
//			for(MovementLineLot lineLot : mLineLots) {
//				if(lineLot.getLotId() == null || "".equals(lineLot.getLotId().trim())) {
//					UI.showError(Message.getString("inv.invalid_lotId"));
//					return false;
//				}
//				total = total.add(lineLot.getQtyMovement());
//			}
//			if(total.compareTo(lineQtyTotal) == 0) {
//				continue;
//			} else {
//				UI.showError(String.format(Message.getString("inv.lot_qtyTotal_isnot_equals_inQty"),
//						total.toString(), String.valueOf(lineQtyTotal.doubleValue()), m.getMaterialId()));
//				return false;
//			}
//		}
//		return true;
//	}
//	
//	protected void setLineLotForMovementLine() {
//		Set<Long> mObjRrns = new LinkedHashSet<Long>();
//		for(MovementLine line : lines) {
//			if(!mObjRrns.contains(line.getMaterialRrn())) {
//				mObjRrns.add(line.getMaterialRrn());
//			}
//		}
//		List<MovementLine> ls = null;
//		List<MovementLineLot> lls = null;
//		for(Long materialRrn : mObjRrns) {
//			ls = new ArrayList<MovementLine>();
//			for(MovementLine line : lines) {
//				if(materialRrn.equals(line.getMaterialRrn()))
//					ls.add(line);
//			}
//			lls = getLineLotsByMaterial(materialRrn);
//			
//			BigDecimal total = null;
//			List<MovementLineLot> tempLineLots = null;
//			for(MovementLine tempLine : ls) {
//				total = BigDecimal.ZERO;
//				tempLineLots = new ArrayList<MovementLineLot>();
//				for(MovementLineLot lineLot : lls) {
//					if(tempLine.getQtyMovement().compareTo(total.add(lineLot.getQtyMovement())) >= 0) {
//						tempLineLots.add(lineLot);						
//						total = total.add(lineLot.getQtyMovement());
//					}
//					if(total.compareTo(tempLine.getQtyMovement()) == 0) {
//						tempLine.setMovementLots(tempLineLots);
//						lls.removeAll(tempLineLots);
//						break;
//					}
//				}
//			}
//		}
//	}
	
	@Override
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
        				delete((MovementLineLot)obj);
        			}
        		}
        	}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}
	}
	
	protected void delete(MovementLineLot lineLot) {
		getLineLots().remove(lineLot);
		refresh();
		setDoOprationsTrue();
	}

	private List<MovementLineLot> getLineLotsByMaterial(Long materialRrn) {
		List<MovementLineLot> ls = new ArrayList<MovementLineLot>();
		for(MovementLineLot linelot : getLineLots()) {
			if(linelot.getMaterialRrn().equals(materialRrn))
				ls.add(linelot);
		}
		return ls;
	}
	
	private BigDecimal getLineMoveInQtyByMaterial(Long materialRrn) {
		BigDecimal total = BigDecimal.ZERO;
		if(lines != null) {
			for(MovementLine line : lines) {
				if(line.getMaterialRrn().equals(materialRrn))
					total = total.add(line.getQtyMovement());
			}
		}
		return total;
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
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
        }
	}
	
	protected Warehouse getOutWarehouse() {
		if(in == null || in.getWarehouseRrn() == null)
			return null;
		Warehouse wh = new Warehouse();
		wh.setObjectRrn(in.getWarehouseRrn());
		return wh;
	}
	
	protected MovementIn.InType getInType() {
		return inType;
	}
	
	protected String getWhereClause() {
		StringBuffer whereClause = new StringBuffer("");
//		if(this.movementInLine != null) {
//			whereClause.append(" movementLineRrn = '");
//			whereClause.append(this.movementInLine.getObjectRrn());
//			whereClause.append("' ");
//		} else 
		if(lines != null && lines.size() > 0){
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
		if(!"".equals(whereClause.toString())) {
			return whereClause.toString();
		}
		return " 1 <> 1 ";
	}

	@SuppressWarnings("unchecked")
	public void refresh(Lot newLot) {
		List<Lot> listLot = (List<Lot>) viewer.getInput() == null ?
				new ArrayList<Lot>() : (List<Lot>) viewer.getInput();
		if (newLot != null) {
			listLot.add(newLot);
		}
	}
	
	protected void setMovementLineSelect(Object obj) {
		if (obj instanceof MovementLineLot) {
			selectedLineLot = (MovementLineLot) obj;
		} else {
			selectedLineLot = null;
		}
	}
	
	protected void setPrintEnable(boolean enabled) {
		itemPrint.setEnabled(enabled);
	}
	
	protected void setEnable(boolean enabled) {
		itemSave.setEnabled(enabled);
		itemDelete.setEnabled(enabled);
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
	
	public MovementIn getMovementIn() {
		return in;
	}

	public void setInType(MovementIn.InType inType) {
		this.inType = inType;
	}
}

package com.graly.erp.inv.out;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.barcode.LotMasterSection;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.model.RacKMovementLot;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.inv.model.WarehouseRack;
import com.graly.erp.inv.racklot.QtySetupDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.custom.XSearchComposite;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.forms.field.RefTextField;
import com.graly.framework.base.ui.forms.field.SearchField;
import com.graly.framework.base.ui.util.ADFieldUtil;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.StringUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class RackLotSection extends OutLineLotSection {
	private static final Logger logger = Logger.getLogger(RackLotSection.class);

	protected LinkedHashMap<String, ADField> adFields = new LinkedHashMap<String, ADField>(10, (float)0.75, false);
	protected LinkedHashMap<String, IField> fields = new LinkedHashMap<String, IField>(10, (float)0.75, false);
	private int gridY = 2; 
	protected int mLeftPadding = 5;
    protected int mTopPadding = 0;
    protected int mRightPadding = 5;
    protected int mBottomPadding = 0;
    protected int mHorizSpacing = 5;
    protected int mVertSpacing = 5;
    public static final String tableName = "INVRackLot";
    private ADTable adTable;
    private String rackId;
    
    private List<MovementLineLot> lineLots;
    
	protected MovementLine outLine;
	protected MovementOut out;
	
	protected boolean isView = false;
	
	protected List<RacKMovementLot> rLots = new ArrayList<RacKMovementLot>();
	protected List<String> errorLots = new ArrayList<String>();

	public RackLotSection(ADTable adTable, LotDialog parentDialog) {
		super(adTable, parentDialog);
	}
	
	public RackLotSection(ADBase parent, ADBase child, ADTable adTable,
			RackLotDialog rmd, boolean isView) {
		super(adTable, rmd);
		this.out =(MovementOut)parent;
		this.outLine = (MovementLine)child;
		this.isView = isView;
	}
	
	public RackLotSection(ADTable adTable, MovementOut out,
			MovementLine outLine, List<MovementLine> lines,
			RackLotDialog rmd, boolean isView) {
		this(out, outLine, adTable, rmd, isView);
		this.lines = lines;
	}
	
    public void createToolBar(Section section) {
        ToolBar tBar = new ToolBar(section, SWT. FLAT | SWT.HORIZONTAL );
        createToolItemSave(tBar);
         new ToolItem(tBar, SWT. SEPARATOR);
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
		getADTableOfInvLot();
	}
	
	protected ADTable getADTableOfInvLot() {
		try {
			if(adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, tableName);
			}
			return adTable;
		} catch(Exception e) {
			logger.error("RequisitionLineDialog : initAdTableOfBom()", e);
		}
		return null;
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
	}
	
	//对批次输入框添加事件监听
	@Override
    protected KeyListener getKeyListener() {
		try {
			return new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					txtLotId.setForeground(SWTResourceCache.getColor("Black"));
					switch (e.keyCode) {
					case SWT.CR :
						addRackLot();
						break;
					case SWT.TRAVERSE_RETURN :
						addRackLot();
						break;
					}
				}
			};
		} catch(Exception e) {
			logger.error("Error at NewRackLotSection ：getKeyListener() ", e);
		}
		return null;
	}
	
	//按输入的批次号查询批次
	protected void addRackLot() {
		String lotId = txtLotId.getText();
		try {
			if(lotId != null && !"".equals(lotId)) {				
				INVManager invManager = Framework.getService(INVManager.class);
				lot = invManager.getLotByLotId(Env.getOrgRrn(), lotId);
				if(lot == null || lot.getMaterialRrn() == null) {
					txtLotId.setForeground(SWTResourceCache.getColor("Red"));
					UI.showError(Message.getString("inv.lotnotexist"));
					return;
				}
				// 如果l不为null，表示lot所对应的物料在lines中或与outLine对应的物料一致
				MovementLine l = this.isContainsLot(lot);
				if(l == null) {
					return;
				}
				
				QtyRackDialog qsd = new QtyRackDialog(UI.getActiveShell());
				if(qsd.open() == Window.OK){
					RacKMovementLot rLot = new RacKMovementLot();
					
					rLot.setIoType(RacKMovementLot.IO_TYPE_OUT);
					rLot.setLotRrn(lot.getObjectRrn());
					rLot.setLotId(lot.getLotId());
					rLot.setMaterialRrn(lot.getMaterialRrn());
					rLot.setMaterialId(lot.getMaterialId());
					rLot.setMaterialName(lot.getMaterialName());
					rLot.setQty(qsd.getInputQty());
					rLot.setMovementRrn(out.getObjectRrn());
					rLot.setMovementLineRrn(l.getObjectRrn());
					rLot.setRackId(qsd.getInputRack());
					WarehouseRack rack = invManager.getWarehouseRackById(Env.getOrgRrn(), rLot.getRackId());
					if(rack == null){
						UI.showInfo("请输入正确的货架号");
						return;
					}
					rLot.setRackRrn(rack.getObjectRrn());
					rLot.setWarehouseRrn(rack.getWarehouseRrn());
					if(validate(rLot.getLotRrn(),rLot.getRackRrn(),rLot.getWarehouseRrn(),rLot.getQty())==false){
						return;
					}
					rLots.add(rLot);					
					refresh();
					setDoOprationsTrue();
				}
			}
		} catch(Exception e) {
			txtLotId.setForeground(SWTResourceCache.getColor("Red"));
			logger.error("Error at NewRackLotSection ：addLot() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		} finally {
			txtLotId.selectAll();
		}
	}
	
	protected boolean validate(Long lotRrn,Long rackRrn,Long warehouseRrn, BigDecimal qty) {
		try{
		INVManager invManager = Framework.getService(INVManager.class);
		BigDecimal qtyonhand = invManager.getWarehouseRackQtyonhand(lotRrn, rackRrn);
		if(warehouseRrn.longValue()!=out.getWarehouseRrn().longValue()){
			UI.showInfo("您所输入的货架号并不在选定的仓库中");
			return false;
		}
		if(qtyonhand==null){
			UI.showInfo("批次没有存放此货架");
			return false;
		}
		if(qty.compareTo(qtyonhand)==1){
			UI.showInfo("输入数量大于货架库存");
			return false;
		}
		return true;
	} catch(Exception e) {
		logger.error("Error at OutLineLotSection : saveAdapter() " + e);
		ExceptionHandlerManager.asyncHandleException(e);
		return false;
	}
	}
	
	@Override
	public void refresh() {
		if(lotManager != null && viewer != null) {
			lotManager.setInput(getrLots());
			lotManager.updateView(viewer);
			createSectionDesc(section);
		}
	}
	
	public List<RacKMovementLot> getrLots() {
		return rLots;
	}

	public void setrLots(List<RacKMovementLot> rLots) {
		this.rLots = rLots;
	}

	protected void deleteAdapter() {
		try {
			TableItem[] items = viewer.getTable().getSelection();
        	if (items != null && items.length > 0){
        		TableItem item = items[0];
        		Object obj = item.getData();
        		if(obj != null && obj instanceof RacKMovementLot) {
        			boolean confirmDelete = UI.showConfirm(Message
        					.getString("common.confirm_delete"));
        			if (confirmDelete) {
        				getrLots().remove(obj);
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
	
	protected void saveAdapter() {
		try {
			if(lines != null && lines.size() > 0) {
				if(validateAll()) {
					INVManager invManager = Framework.getService(INVManager.class);
					invManager.batchSaveRacKMovementLot(Env.getOrgRrn(), getrLots(), Env.getUserRrn(), false);
					invManager.saveMovementOutLine(out, lines, getOutType(), Env.getUserRrn());
					UI.showInfo(Message.getString("common.save_successed"));
					this.setIsSaved(true);
					((RackLotDialog)parentDialog).buttonPressed(IDialogConstants.CANCEL_ID);
				}				
			}
		} catch(Exception e) {
			logger.error("Error at OutLineLotSection : saveAdapter() " + e);
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	// 验证各个入库物料的入库数量是否等于对应的批次数量之和
	protected boolean validateAll() {
		rlotstolots();
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
				lineLots = null;
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
	
	protected List<MovementLineLot> getLineLots() {
		if(lineLots == null) {
			lineLots = new ArrayList<MovementLineLot>();
			return lineLots;
		}
		return lineLots;
	}
	
	protected void initTableContent() {
		List<ADBase> list = null;
		try {
        	ADManager manager = Framework.getService(ADManager.class);
            list = manager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(), 
            		Env.getMaxResult(), getWhereClause(), getOrderByClause());
            List<RacKMovementLot> l = new ArrayList<RacKMovementLot>();
            for(ADBase ab : list) {
            	RacKMovementLot rLot = (RacKMovementLot)ab;
            	l.add(rLot);
            }
            setrLots(l);
            refresh();
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
        }
	}
	
	protected String getWhereClause() {
		StringBuffer whereClause = new StringBuffer("");
		if(lines != null){
			whereClause.append(" movementRrn IN (");
			whereClause.append(out.getObjectRrn());
			whereClause.append(")");			
			return whereClause.toString();
		}
		return " 1 <> 1 ";
	}
	
	protected void rlotstolots(){
		try {
			HashMap<Long,List<RacKMovementLot>> map=new HashMap<Long,List<RacKMovementLot>>();
			for (RacKMovementLot rLot:rLots){
				Long lotrrn = rLot.getLotRrn();				
				if(map.containsKey(lotrrn)){
					List<RacKMovementLot> lst = map.get(lotrrn);
					lst.add(rLot);
				}else{
					List<RacKMovementLot> lst = new ArrayList<RacKMovementLot>();
					lst.add(rLot);
					map.put(lotrrn, lst);
				}
			}
			
			Set<Long> lotrrns = map.keySet();
			for(Long lotrrn : lotrrns){
				ADManager adManager = Framework.getService(ADManager.class);
				Lot lot = new Lot();
				lot.setObjectRrn(lotrrn);
				lot = (Lot) adManager.getEntity(lot);
				BigDecimal outQty  = BigDecimal.ZERO;
				List<RacKMovementLot> values = map.get(lotrrn);
				for(RacKMovementLot rLot : values){
					outQty = outQty.add(rLot.getQty());
				}
				MovementLine line = isContainsLot(lot);
				MovementLineLot lineLot = pareseMovementLineLot(line, outQty, lot);
				getLineLots().add(lineLot);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getGridY() {
		return gridY;
	}

	public void setGridY(int gridY) {
		this.gridY = gridY;
	}
}
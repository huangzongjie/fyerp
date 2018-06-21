package com.graly.erp.wip.virtualhouse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.MovementWorkShop;
import com.graly.erp.inv.model.MovementWorkShopLine;
import com.graly.erp.inv.model.MovementWorkShopLineLot;
import com.graly.erp.inv.model.MovementWorkShopVirtualHouse;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.entitymanager.views.TableViewerManager;
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
//extends LotMasterSection
public class ByLotVirtualHouseSection  {
	private static final Logger logger = Logger.getLogger(ByLotVirtualHouseSection.class);
	public static final String TABLE_NAME = "VUserWarehouse";
	public static final String TABLE_NAME_REFTABLE = "ADUserRefName";
//	public static final String TABLE_NAME_LOCATOR = "INVLocator";
//	public static final String ADJUSTOUT_TYPE = "AdjustOutType";
	private static final String WHERE_CLAUSE = " userRrn = " + Env.getUserRrn() + " AND (isVirtual = 'N' OR isVirtual is null) ";
	
	public static final String KEY = "objectRrn";
	public static final String KEY_USER_REF = "value";
	public static final String VALUE = "warehouseId";
	public static final String VALUE_USER_ERF = "key";
	
	
	protected TableViewerManager lotManager;
	protected CheckboxTableViewer checkViewer;
	protected boolean isSaved = false; // 是否进行了保存动作
	protected boolean isDid = false;   // 是否对界面进行了操作
	
	
	protected HashMap<String, BigDecimal> headerLabels;
	protected ADTable whTable; //用户仓库ADTable
	protected RefTableField outWhField;
	
	
	protected ADTable adTable;
	protected Section section;
	protected IFormPart spart;
	protected IManagedForm form;
	
	protected ToolItem itemSave;
	protected ToolItem itemDelete;

	protected Text txtLotId;
	protected Text txtQtyTotal;
	protected BigDecimal qtyTotal =BigDecimal.ZERO;
	protected Lot lot = null;
	protected Lot selectLot;
	protected ByLotVirtualHouseDialog parentDialog;
	private List<MovementWorkShopLineLot> lineLots;
	protected List<String> errorLots = new ArrayList<String>();
	protected MovementWorkShopVirtualHouse virtualHouse;
	
	
	protected String moId;//
	
	public ByLotVirtualHouseSection(ADTable adTable, ByLotVirtualHouseDialog parentDialog) {
		this.adTable = adTable;
		this.parentDialog = parentDialog;
	}
	
	public void createContents(IManagedForm form, Composite parent){
		createContents(form, parent, Section.DESCRIPTION | Section.TITLE_BAR);
	}
	protected void setSectionTitle() {
		section.setText(Message.getString("inv.lot_list"));
	}
	protected void createLotInfoComposite(Composite client, FormToolkit toolkit) {
		Composite comp = toolkit.createComposite(client, SWT.BORDER);
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
		txtLotId.setFocus();
		
		
		Label label2 = toolkit.createLabel(comp, Message.getString("inv.total"));
		label2.setForeground(SWTResourceCache.getColor("Folder"));
		label2.setFont(SWTResourceCache.getFont("Verdana"));
		txtQtyTotal = toolkit.createText(comp, "", SWT.BORDER);
		txtQtyTotal.setTextLimit(48);
		txtQtyTotal.setLayoutData(gd);
		txtQtyTotal.setEnabled(false);
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
	
	
	
	protected void addLot() {
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
				if(lot.getMoId()==null || "".equals(lot.getMoId())){
					UI.showError("工作令编号不存在");
					return;
				}
				if(moId==null|| "".equals(moId)){
					moId = lot.getMoId();
				}
				if(!lot.getMoId().equals(moId)){
					UI.showError("不同工作令编号的物料不允许一起扫描");
					return;
				}
				// 如果l不为null，表示lot所对应的物料在lines中或与outLine对应的物料一致
				MovementWorkShopLine l = this.isContainsLot(lot);
				if(l == null) {
					return;
				}
				
				MovementWorkShopLineLot lineLot  = pareseMovementLineLot(l, BigDecimal.ONE, lot);
//				if(Lot.LOTTYPE_BATCH.equals(lot.getLotType())
//						|| Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
//					Warehouse wh = getOutWarehouse();
//					if(wh == null) {
//						UI.showError(Message.getString("inv.batch_must_be_select_warehouse_first"));
//						return;
//					}
//					lineLot = pareseMovementLineLot(l, outQtyDialog.getInputQty(), lot);
//				} else if(Lot.LOTTYPE_SERIAL.equals(lot.getLotType())) {
//					lineLot = pareseMovementLineLot(l, lot.getQtyCurrent(), lot);
//				}
//				if(contains(lineLot)) {
//					UI.showError(String.format(Message.getString("wip.lot_list_contains_lot"), lot.getLotId()));
//					return;
//				}
//				getLots().add(lot);
				qtyTotal= qtyTotal.add(BigDecimal.ONE);
				txtQtyTotal.setText(qtyTotal.toString());
				getLineLots().add(lineLot);
				refresh();
				setDoOprationsTrue();
			}
		} catch(Exception e) {
			txtLotId.setForeground(SWTResourceCache.getColor("Red"));
			logger.error("Error at LotMasterSection ：addLot() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		} finally {
			txtLotId.selectAll();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
//	protected void addLot() {
//		String lotId = txtLotId.getText();
//		try {
//			if(lotId != null && !"".equals(lotId)) {				
////				INVManager invManager = Framework.getService(INVManager.class);
////				lot = invManager.getLotByLotId(Env.getOrgRrn(), lotId);
////				if(lot == null || lot.getMaterialRrn() == null) {
////					txtLotId.setForeground(SWTResourceCache.getColor("Red"));
////					UI.showError(Message.getString("inv.lotnotexist"));
////					return;
////				}
////				if(validLot(lot)) {
////					if(getLots().contains(lot)) {
////						if(checkViewer != null) {
////							checkViewer.setChecked(lot, true);
////						}
////					} else {
////						getLots().add(lot);					
////					}
////					refresh();
////					setDoOprationsTrue();
////				}
////				getLots().add(lot);	
////				refresh();
////				setDoOprationsTrue();
//			}
//		} catch(Exception e) {
//			txtLotId.setForeground(SWTResourceCache.getColor("Red"));
//			logger.error("Error at LotMasterSection ：addLot() ", e);
//			ExceptionHandlerManager.asyncHandleException(e);
//		} finally {
//			txtLotId.selectAll();
//		}
//	}
	
	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		this.form = form;
		final FormToolkit toolkit = form.getToolkit();
		
		section = toolkit.createSection(parent, sectionStyle);
		setSectionTitle();
		section.marginWidth = 3;
	    section.marginHeight = 4;
	    toolkit.createCompositeSeparator(section);

	    createToolBar(section);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		section.setLayout(layout);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
	    
	    Composite client = toolkit.createComposite(section);    
	    GridLayout gridLayout = new GridLayout();    
	    layout.numColumns = 1;    
	    client.setLayout(gridLayout);
	    
	    spart = new SectionPart(section);    
	    form.addPart(spart);
	    
	    createLotInfoComposite(client, toolkit);
	    createTableContent(client, toolkit);
	    section.setClient(client);
	    createViewAction(viewer);
	}
	protected void createTableContent(Composite client, FormToolkit toolkit) {
		createTableViewer(client, toolkit);
		if(viewer instanceof CheckboxTableViewer) {
			checkViewer = (CheckboxTableViewer)viewer;
		}
		initTableContent();
	}
	protected void initTableContent() {
		List<ADBase> list = null;
		try {
        	ADManager manager = Framework.getService(ADManager.class);
            list = manager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(), 
            		Env.getMaxResult(), getWhereClause(), "lotId");
            List<MovementWorkShopLineLot> l = new ArrayList<MovementWorkShopLineLot>();
            for(ADBase ab : list) {
            	MovementWorkShopLineLot lineLot = (MovementWorkShopLineLot)ab;
            	l.add(lineLot);
            }
            setLineLots(l);
            refresh();
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
        }
	}
	public void setLineLots(List<MovementWorkShopLineLot> lineLots) {
		this.lineLots = lineLots;
	}
	protected void createTableViewer(Composite client, FormToolkit toolkit) {
		lotManager = new TableListManager(adTable);
		viewer = (TableViewer)lotManager.createViewer(client, toolkit);
		lotManager.updateView(viewer);
	}
	protected void createViewAction(StructuredViewer viewer){
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setMovementLineSelect(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	protected void setMovementLineSelect(Object obj) {
		if (obj instanceof Lot) {
			selectLot = (Lot) obj;
		} else {
			selectLot = null;
		}
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
//		setEnabled(true);
		section.setTextClient(tBar);
	}
	protected void createToolItemDelete(ToolBar tBar) {
		itemDelete = new ToolItem(tBar, SWT.PUSH);
		itemDelete.setText(Message.getString("common.delete"));
		itemDelete.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_DELETE));
		itemDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteAdapter();
			}
		});
	}
	protected void createToolItemSave(ToolBar tBar) {
		itemSave = new ToolItem(tBar, SWT.PUSH);
		itemSave.setText(Message.getString("common.save"));
		itemSave.setImage(SWTResourceCache.getImage("save"));
		itemSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				saveAdapter();
			}
		});
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
	
//	protected void addLot(String lotId) {
//		try {
//			if(lotId != null && !"".equals(lotId)) {				
//				INVManager invManager = Framework.getService(INVManager.class);
//				lot = invManager.getLotByLotId(Env.getOrgRrn(), lotId);
//				if(lot == null || lot.getMaterialRrn() == null) {
//					// 如果退货入库中有位Material类型的物料,默认其对应的批次已经在Lot表中存在
//					//(因为只有先入库才能出库，然后再退货),所以如果lot为null,即使为Material类型,
//					//表示其还未入库或还未审核，也会提示该批次不存在
//					txtLotId.setForeground(SWTResourceCache.getColor("Red"));
//					UI.showError(Message.getString("inv.lotnotexist"));
//					return;
//				}
//				else if(validLot(lot)) {
//					// 如果l不为null，表示lot所对应的物料在lines中或与inLine对应的物料一致
//					MovementWorkShopLine l = this.isContainsLot(lot);
//					if(l == null) {
//						return;
//					}
//					// Batch或Material类型需要设置调拨数量
//					MovementWorkShopLineLot lineLot = null;
//					if(Lot.LOTTYPE_BATCH.equals(lot.getLotType())
//							|| Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
//						VirtualHouseQtySetupDialog outQtyDialog = new VirtualHouseQtySetupDialog(UI.getActiveShell(),
//								null, lot, null);
//						int openId = outQtyDialog.open();
//						if(openId == Dialog.OK) {
//							lineLot = pareseMovementLineLot(l, outQtyDialog.getInputQty(), lot);
//						} else if(openId == Dialog.CANCEL) {
//							return;
//						}
//					} else if(Lot.LOTTYPE_SERIAL.equals(lot.getLotType())) {
//						// 此时Lot.QtyCurrent可能为零(已使用或已出库) 所以传入的值应为BigDecimal.ONE
//						lineLot = pareseMovementLineLot(l, BigDecimal.ONE.negate(), lot);
//					}
//					getLineLots().add(lineLot);						
//					refresh();
//					setDoOprationsTrue();
//				}
//			}
//		} catch(Exception e) {
//			txtLotId.setForeground(SWTResourceCache.getColor("Red"));
//			logger.error("Error at ByLotAdjustOutSection ：addLot(String lotId) ", e);
//			if(e instanceof ClientException && "inv.lotnotexist".equals(((ClientException)e).getErrorCode())){
//				errorLots.add(lotId);
//			}else{
//				ExceptionHandlerManager.asyncHandleException(e);
//			}
//		} finally {
//			txtLotId.selectAll();
//		}
//	}
	
	protected MovementWorkShopLineLot pareseMovementLineLot(MovementWorkShopLine line, BigDecimal uoutQtytQty, Lot lot) {
		Date now = Env.getSysDate();
		MovementWorkShopLineLot outLineLot = new MovementWorkShopLineLot();
		outLineLot.setOrgRrn(Env.getOrgRrn());
		outLineLot.setIsActive(true);
		outLineLot.setCreated(now);
		outLineLot.setCreatedBy(Env.getUserRrn());
		outLineLot.setUpdated(now);
		outLineLot.setUpdatedBy(Env.getUserRrn());
		
		if(virtualHouse != null) {
			outLineLot.setMovementRrn(virtualHouse.getObjectRrn());
			outLineLot.setMovementId(virtualHouse.getDocId());
		}
		outLineLot.setMovementLineRrn(line.getObjectRrn());
		outLineLot.setLotRrn(lot.getObjectRrn());
		outLineLot.setLotId(lot.getLotId());
		outLineLot.setMaterialRrn(lot.getMaterialRrn());
		outLineLot.setMaterialId(lot.getMaterialId());
		outLineLot.setMaterialName(lot.getMaterialName());
		// 将用户输入的出库数量设置到outLineLot.qtyMovement中
		outLineLot.setQtyMovement(uoutQtytQty);
		return outLineLot;
	}
	
	protected List<MovementWorkShopLineLot> getLineLots() {
		if(lineLots == null) {
			lineLots = new ArrayList<MovementWorkShopLineLot>();
			return lineLots;
		}
		return lineLots;
	}
	
	protected ADTable getADTableBy(String tableName) throws Exception {
		ADTable adTable = null;
		ADManager adManager = Framework.getService(ADManager.class);
		adTable = adManager.getADTable(0L, tableName);
		return adTable;
	}

	protected boolean validLot(Lot lot) {
//		Object outType = outTypeFiled.getValue();
//		if(outType == null){
//			UI.showError(String.format(Message.getString("inv.must_select"), Message.getString("inv.out_type")));
//			return false;
//		}
		if(isContainsInLineLots(lot)) {
			UI.showError(String.format(Message.getString("wip.lot_list_contains_lot"), lot.getLotId()));
			return false;
		}
		
		return true;
	}
	
	// 入库时同一批次为Batch类型时也不能再次入库，因为入库不存在分批入库
	protected boolean isContainsInLineLots(Lot lot) {
		for(MovementWorkShopLineLot lineLot : getLineLots()) {
			if(lot.getObjectRrn().equals(lineLot.getLotRrn()))
					return true;
		}
		return false;
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
//		Object outType = outTypeFiled.getValue();
//		if(outType == null){
//			UI.showError(String.format(Message.getString("inv.must_select"), Message.getString("inv.out_type")));
//			return false;
//		}
		for(MovementWorkShopLineLot line : getLineLots()){
			Long lotRrn = line.getLotRrn(); 
			Lot lineLot = new Lot();
			lineLot.setObjectRrn(lotRrn);
			try {
				ADManager adManager = Framework.getService(ADManager.class);
				lineLot = (Lot) adManager.getEntity(lineLot);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				ExceptionHandlerManager.asyncHandleException(e);
			}
			
//			//入库调整 和 研发用料 都需要批次在仓库中
//			if(MovementOut.OUT_TYPE_IN_ADJUST.equals(outType) || MovementOut.OUT_TYPE_RD_ADJUST.equals(outType)){
//				if(Lot.LOTTYPE_SERIAL.equals(lineLot.getLotType()) && !Lot.POSITION_INSTOCK.equals(lineLot.getPosition())) {
//					UI.showError(String.format(Message.getString("inv.lot_not_in"), lineLot.getLotId()));
//					return false;
//				}
//			}else if(Lot.LOTTYPE_SERIAL.equals(lot.getLotType()) && !Lot.POSITION_OUT.equals(lot.getPosition())) {
//				UI.showError(String.format(Message.getString("inv.lot_not_out"), lot.getLotId()));
//				return false;
//			}
		}
		return true;
	}
	
	protected void saveAdapter() {
		try {
//			if(validate()) {
				virtualHouse = createMovementVirtualHouse();
//				if(MovementOut.OUT_TYPE_IN_ADJUST.equals(out.getOutType()) || MovementOut.OUT_TYPE_RD_ADJUST.equals(out.getOutType())){
//					//如果是采购调整 或 研发用料，按正常出库进行，即出正数
//					for(MovementLineLot lineLot : this.getLineLots()){
//						lineLot.setQtyMovement(lineLot.getQtyMovement().abs());
//					}
//				}
				List<MovementWorkShopLine> lines = createMovementLines();
				INVManager invManager = Framework.getService(INVManager.class);
				
				ADManager adManager = Framework.getService(ADManager.class);
				List<ManufactureOrder>  mos = adManager.getEntityList(Env.getOrgRrn(), ManufactureOrder.class,Integer.MAX_VALUE,"docId ='"+virtualHouse.getMoId()+"'",null);
				ManufactureOrder mo =mos.get(0);
				
				List<MovementWorkShop>  wses = adManager.getEntityList(Env.getOrgRrn(), MovementWorkShop.class,Integer.MAX_VALUE,"moId ='"+virtualHouse.getMoId()+"'",null);
				StringBuffer sf = new StringBuffer();
				if(wses!=null &wses.size() >0){
					for(MovementWorkShop ws :wses){
						sf.append("'");
						sf.append(ws.getDocId());
						sf.append("',");
					}
				}
				BigDecimal hisQty = BigDecimal.ZERO; 
				for(MovementWorkShopLine line : lines){
					if(sf!=null && sf.length()>0){
						List<MovementWorkShopLine>  hisLines = adManager.getEntityList(Env.getOrgRrn(), MovementWorkShopLine.class,Integer.MAX_VALUE,
								"movementId in("+sf.substring(0,sf.length()-1)+") and lineStatus in ('DRAFTED','APPROVED','COMPLETED') ",null);
						
						for(MovementWorkShopLine hisLine : hisLines){
							hisQty = hisQty.add(hisLine.getQtyMovement());
						}
		
					}
					if(line.getQtyMovement().compareTo(mo.getQtyProduct().subtract(hisQty)) > 0 ){
						UI.showError("超过实际需要入库数量");
						return;
					}
				}
				
				invManager.saveMovementVirtualHouseLine(virtualHouse, lines, Env.getUserRrn());
//				virtualHouse = invManager.saveMovementOutLine(out, lines, getOutType(), Env.getUserRrn());
				UI.showInfo(Message.getString("common.save_successed"));
				setEnabled(false);
				setIsSaved(true);
				parentDialog.close();
//			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at ByLotAdjustOutSection : saveAdapter() ", e);
		}
	}
	
	protected MovementWorkShopVirtualHouse createMovementVirtualHouse() {
		// 多次保存时，仍保存在同一出库单中
		if(virtualHouse != null && virtualHouse.getObjectRrn() != null) {
//			if(this.outWhField.getValue() != null) {
//				out.setOutType(String.valueOf(outWhField.getValue()));			
//			}
			return virtualHouse;			
		}
		MovementWorkShopVirtualHouse out = new MovementWorkShopVirtualHouse();
		out.setMoId(moId);
		out.setOrgRrn(Env.getOrgRrn());
		out.setWarehouseRrn(77321470L);
		out.setUserCreated(Env.getUserName());
		
//		if(outTypeFiled.getValue() != null) {
//			out.setOutType(String.valueOf(outTypeFiled.getValue()));			
//		}
		return out;
	}
	
	protected MovementWorkShopLine isContainsLot(Lot lot) {
		return new MovementWorkShopLine();
	}
	
	protected List<MovementWorkShopLine> createMovementLines() throws Exception {
		List<MovementWorkShopLine> lines = new ArrayList<MovementWorkShopLine>();
		
		List<Long> materialRrns = new ArrayList<Long>();
		List<MovementWorkShopLineLot> lineLots = null;
		BigDecimal total = BigDecimal.ZERO;
		int i = 1;
		for(MovementWorkShopLineLot lineLot : this.getLineLots()) {//遍历所有Lot将相同物料的批合并成一个MovementLine
			if(materialRrns.contains(lineLot.getMaterialRrn()))
				continue;
			
			lineLots = new ArrayList<MovementWorkShopLineLot>();
			total = BigDecimal.ZERO;
			for(MovementWorkShopLineLot tempLineLot : getLineLots()) {
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
	protected MovementWorkShopLine generateLine(List<MovementWorkShopLineLot> lineLots,
			BigDecimal qtyOut, int lineNo) throws Exception {
		MovementWorkShopLine line = null;
		// 如果为再次保存则，根据物料找到已保存的出库单行，重新赋给lineLots, qtyOut
		if(virtualHouse != null && virtualHouse.getObjectRrn() != null) {
			MovementWorkShopLineLot lineLot = lineLots.get(0);
			for(MovementWorkShopLine movementLine : virtualHouse.getMovementWorkShopLines()) {
				if(lineLot.getMaterialRrn().equals(movementLine.getMaterialRrn())
						&& movementLine.getObjectRrn() != null) {
					movementLine.setMovementWorkShopLots(lineLots);
					movementLine.setQtyMovement(qtyOut);
					return movementLine;
				}
			}
		}
		// 否则重新建个出库单行
		line = new MovementWorkShopLine();;
		line.setOrgRrn(Env.getOrgRrn());
		line.setLineStatus(MovementWorkShopVirtualHouse.STATUS_DRAFTED);
		line.setLineNo(new Long(lineNo));
		line.setQtyMovement(qtyOut);
		
		line.setMovementWorkShopLots(lineLots);
		MovementWorkShopLineLot lineLot = lineLots.get(0);
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
	
	protected String getWhereClause() {
		StringBuffer whereClause = new StringBuffer("");
		if(virtualHouse != null && virtualHouse.getObjectRrn() != null) {
			whereClause.append(" movementRrn = '");
			whereClause.append(virtualHouse.getObjectRrn());
			whereClause.append("' ");
		}
		return " 1 <> 1 ";
	}
	
 
//	protected MovementOut.OutType getOutType() {
//		return MovementOut.OutType.AOU;//出库类型改为财务调整
//	}
	
	protected void setErrorMessage(String msg) {
		parentDialog.setErrorMessage(msg);
	}
	
	protected void setEnabled(boolean enabled) {
		this.itemSave.setEnabled(enabled);
		this.itemDelete.setEnabled(enabled);
	}
	protected TableViewer viewer;
//	protected List<Lot> lots;
	
 
	public void refresh() {
		if(lotManager != null && viewer != null) {
			lotManager.setInput(getLineLots());
			lotManager.updateView(viewer);
			createSectionDesc(section);
		}
	}
	
	protected void createSectionDesc(Section section){
		String text = Message.getString("common.totalshow");
		int count = viewer.getTable().getItemCount();
		if (count > Env.getMaxResult()) {
			text = String.format(text, String.valueOf(count), String.valueOf(Env.getMaxResult()));
		} else {
			text = String.format(text, String.valueOf(count), String.valueOf(count));
		}
		section.setDescription("  " + text);
	}
	
	protected void setDoOprationsTrue() {
		if(!isDid) this.isDid = true;
		//如果进行了操作(即isDid = true)，若isSaved为真，则将其置为false，表示以前的保存已经无效
		if(isSaved()) {
			setIsSaved(false);
		}
	}
	protected void setDoOprationsFalse() {
		if(isDid) this.isDid = false;
	}

	public boolean isSaved() {
		return isSaved;
	}

	public boolean isDid() {
		return isDid;
	}

	public void setDid(boolean isDid) {
		this.isDid = isDid;
	}
	protected Lot parentLot;
	public Lot getParentLot() {
		return parentLot;
	}

	public void setParentLot(Lot parentLot) {
		this.parentLot = parentLot;
	}
	
	// 如果进行了保存动作 或 没有进行界面操作则返回真
	protected boolean isSureExit() {
		if(isSaved() || !isDid())
			return true;
		return false;
	}
	
	public void setIsSaved(boolean isSaved) {
		this.isSaved = isSaved;
		// 如果完成了保存(即isSaved = true)，则将isDid置为false，表示以前的操作已经保存了
		// 重新将isDid置为false, 表示从现在开始没有进行任何操作
		if(isSaved)
			this.setDoOprationsFalse();
	}

	public MovementWorkShopVirtualHouse getVirtualHouse() {
		return virtualHouse;
	}

	public void setVirtualHouse(MovementWorkShopVirtualHouse virtualHouse) {
		this.virtualHouse = virtualHouse;
	}
	/* 删除MovementLineLot列表中选中的MovementLineLot*/
	protected void deleteAdapter() {
		try {
			TableItem[] items = viewer.getTable().getSelection();
        	if (items != null && items.length > 0){
        		TableItem item = items[0];
        		Object obj = item.getData();
        		if(obj != null && obj instanceof MovementWorkShopLineLot) {
        			boolean confirmDelete = UI.showConfirm(Message
        					.getString("common.confirm_delete"));
        			if (confirmDelete) {
        				getLineLots().remove(obj);
        				qtyTotal= qtyTotal.subtract(BigDecimal.ONE);
        				txtQtyTotal.setText(qtyTotal.toString());
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
}

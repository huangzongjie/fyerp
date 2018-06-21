package com.graly.erp.wip.partlydisassemble;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.erp.wip.workcenter.receive.GainableMoBoms;
import com.graly.erp.wip.workcenter.receive.MoBomTableManager;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.views.TableViewerManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotComponent;

public class AttachSubLotSection implements GainableMoBoms {
	private static final Logger logger = Logger.getLogger(AttachSubLotSection.class);

	protected IManagedForm form;

	private MoBomTableManager moBomManager;
	private AttachSubLotTableManager lotManager;
	private TableViewer moBomViewer, lotViewer;
	private Text txtLotId;
	private Label receiveInfo;
	private Button btDelete;
	private HashMap<ManufactureOrderBom, List<Lot>> moBomMap;//用来存放BOM清单中每个物料所使用的Lot
	private HashMap<Lot, ManufactureOrderBom> lotMap;
	private List<ManufactureOrderBom> moBoms;
	private List<Lot> inputLots;
	private List<ManufactureOrderBom> receivedFinishedBoms;

	
	INVManager invManager;
	WipManager wipManager;
	ADManager adManager;

	protected Lot parentLot;

	public AttachSubLotSection() {}
	
	/**
	 * @param parentLot 需要拆分的批
	 */
	public AttachSubLotSection(Lot parentLot) {
		this.parentLot = parentLot;
		moBomMap = new LinkedHashMap<ManufactureOrderBom, List<Lot>>();
		lotMap = new LinkedHashMap<Lot, ManufactureOrderBom>();
		inputLots = new ArrayList<Lot>();
		receivedFinishedBoms = new ArrayList<ManufactureOrderBom>();
	}
	
	public void createContent(IManagedForm form, Composite parent) {
		this.form = form;		
		createMoBomListViewerContent(parent);
		createActinBarConent(parent);
		createLotListViewerContent(parent);
		
		refreshMoBomListViewer();
		autoInputLot();
	}
	
	protected void createMoBomListViewerContent(Composite client) {
		moBomManager = new MoBomTableManager(this);
		moBomViewer = (TableViewer)moBomManager.createViewer(client, form.getToolkit());
	}

	protected void createActinBarConent(Composite client) {
		FormToolkit toolkit = form.getToolkit();
		Composite title = toolkit.createComposite(client, SWT.NULL);
		GridData g = new GridData(GridData.FILL_HORIZONTAL);
		g.heightHint = 0;
		title.setLayoutData(g);
		Composite comp = toolkit.createComposite(client, SWT.BORDER);
		comp.setLayout(new GridLayout(4, false));
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label label = toolkit.createLabel(comp, Message.getString("inv.lotid"));
		label.setForeground(SWTResourceCache.getColor("Folder"));
		label.setFont(SWTResourceCache.getFont("Verdana"));
		txtLotId = toolkit.createText(comp, "在此输入子料的批号", SWT.BORDER);
		txtLotId.setTextLimit(32);
		GridData gd = new GridData();//GridData.FILL_HORIZONTAL
		gd.heightHint = 13;
		gd.widthHint = Dialog.convertWidthInCharsToPixels(this.getFontMetric(txtLotId), 32);
		txtLotId.setLayoutData(gd);
		txtLotId.addKeyListener(getKeyListener());
		txtLotId.setFocus();
		
		receiveInfo = toolkit.createLabel(comp, "Receive Info", SWT.NULL);
		receiveInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		receiveInfo.setForeground(SWTResourceCache.getColor("Function"));

		btDelete = toolkit.createButton(comp, Message.getString("common.delete"), SWT.PUSH);
		decorateButton(btDelete);
		btDelete.addSelectionListener(getSelectionListener());
	}
	
	protected void setReceiveInfo(String msg, boolean isFull) {
//		if(isFull) {
//			receiveInfo.setForeground(SWTResourceCache.getColor("Red"));
//		}
//		else {
//		}
		receiveInfo.setForeground(SWTResourceCache.getColor("Function"));
		receiveInfo.setText(msg);
	}
	
	protected void createLotListViewerContent(Composite client) {
		lotManager = new AttachSubLotTableManager(this);
		lotViewer = (TableViewer)lotManager.createViewer(client, form.getToolkit());
	}
	
	private KeyListener getKeyListener() {
		try {
			return new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					switch (e.keyCode) {
					case SWT.CR:
						String lotId = txtLotId.getText();
						addLot(lotId);						
						break;
					}
				}
			};
		} catch(Exception e) {
			logger.error("Error at AttachSubLotSection ：getKeyListener() ", e);
		}
		return null;
	}
	
	private void addLot(String lotId) {
		Lot lot = null;
		try {
			if(lotId != null && lotId.trim().length() > 0) {
				setReceiveInfo("", false);
				if(invManager == null) {
					invManager = Framework.getService(INVManager.class);
				}
				lot = invManager.getLotByLotId(Env.getOrgRrn(), lotId);
				if(lot == null || lot.getMaterialRrn() == null) {
					UI.showError(Message.getString("inv.lotnotexist"));
					return;
				}
				// 如果为Material类型，则提示不用输入批次，系统会自动根据BOM关系扣除所消耗的物料
				if(Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
					UI.showError(Message.getString("wip.material_lot_needn't_receive"));
					return;
				}	
				if(wipManager == null) {
					wipManager = Framework.getService(WipManager.class);
				}
				//如果为BATCH_A或BATCH_B类型(即BATCH)
//				if(Lot.LOTTYPE_BATCH.equals(lot.getLotType()) 
//						|| Lot.LOTTYPE_BATCH_A.equals(lot.getLotType())) {
//					lot = wipManager.getWipLotByLotId(lotId, Env.getOrgRrn());
//					if (lot.getQtyCurrent().compareTo(BigDecimal.ZERO) <= 0) {
//						lot.setQtyTransaction(BigDecimal.ZERO);
//					} else {
//						lot.setQtyTransaction(lot.getQtyCurrent());
//					}
//					//批次数量为零时仍可以用该批次接收，但若为LOTTYPE_BATCH_A类型，则不可以，暂不考虑LOTTYPE_BATCH_A情况
////					if(lot.getQtyCurrent().doubleValue() <= 0) {
////						UI.showError(String.format(Message.getString("wip.lot_currentqty_used_out"), lot.getLotId()));
////						return;
////					}
//				}
				if(Lot.LOTTYPE_SERIAL.equals(lot.getLotType())) {
					if(lot.getIsUsed() || lot.getQtyCurrent().doubleValue() <= 0) {
						UI.showError(String.format(Message.getString("wip.lot_currentqty_used_out"), lot.getLotId()));
						return;
					}
					if (!Lot.POSITION_INSTOCK.equals(lot.getPosition()) && !Lot.POSITION_WIP.equals(lot.getPosition())) {
						UI.showError(String.format(Message.getString("wip.lot_not_in_wip_or_stock"), lot.getLotId()));
						return;
					}
					lot.setQtyTransaction(lot.getQtyCurrent());
				}
				if(inputLots.contains(lot)) {
					UI.showError(String.format(Message.getString("wip.lot_list_contains_lot"), lot.getLotId()));
					return;
				}
				
				boolean isLotExistBoms = false; // 标识lot对应的物料是否在moBoms中含有
				for(ManufactureOrderBom moBom : moBoms) {
					if(lot.getMaterialRrn().equals(moBom.getMaterialRrn())) {
						if(Lot.LOTTYPE_BATCH.equals(lot.getLotType()) 
								|| Lot.LOTTYPE_BATCH_A.equals(lot.getLotType())) {
							lot.setQtyTransaction(moBom.getUnitQty());
						}
						isLotExistBoms = true;
						List<Lot> lots = moBomMap.get(moBom);//取得已经输入的批次
						if(lots == null) {//如果还没有输入批，就创建一个List用来存放本次输入的批
							lots = new ArrayList<Lot>();
						}
						BigDecimal total = BigDecimal.ZERO;
						for(Lot lt : lots) {
							total = total.add(lt.getQtyTransaction());//计算已经输入的批次的总数量
						}
						
						if(total.compareTo(moBom.getUnitQty()) < 0) {//moBom.getUnitQty()就是计算的生产需消耗的该物料的总量
							// 总数量 + lot当前数量 > moBom中需要数量(即unitQty), 则Lot的消耗数量qtyTransaction为
							// qtyTransaction = moBom.getUnitQty() - total。
							if(total.add(lot.getQtyTransaction()).compareTo(moBom.getUnitQty()) > 0) {
								lot.setQtyTransaction(moBom.getUnitQty().subtract(total));//注释A：如果本次lot的数量加上以前输入的批次的数量大于该物料的需求数的话只需剩余的差额
							}
						} else {//不可能出现总量大于需求数的情况，因为上面注释A：处进行了控制
//							UI.showWarning(String.format(Message.getString("wip.qty_already_full"),
//									moBom.getMaterial().getMaterialId()));
							lot.setQtyTransaction(BigDecimal.ZERO);
						}
						lots.add(lot);
						moBomMap.put(moBom, lots);
						lotMap.put(lot, moBom);
						if(lot != null) {
							inputLots.add(lot);
							refreshLotListViewer();
							
							// 判断bom列表的颜色是否需要改变，改变接收的提示信息
							total = total.add(lot.getQtyTransaction());
							if(total.compareTo(moBom.getUnitQty()) < 0) {//如果接受总数还不够需求数，则显示已经接受了多少的信息
								this.setReceiveInfo(String.format(Message.getString("wip.has_received_qty"),
										moBom.getMaterialId(), total.toString()), false);
							} else {//否则就加入到数量已经满足需求数的List中
								receivedFinishedBoms.add(moBom);
								moBomViewer.refresh(moBom);
								setReceiveInfo(String.format(Message.getString("wip.material_receive_finished"),
										moBom.getMaterialId()), true);
							}
						}
						break;
					}
				}
				
				if(!isLotExistBoms) {
					UI.showError(String.format(Message.getString("wip.material_does't_exisit_moboms"),
							lot.getLotId(), lot.getMaterialId(), parentLot.getMaterialId()));
					return;
				}
			}
		} catch(Exception e) {
			logger.error("Error at MoLineReceiveSection ：getLotByLotId() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		} finally {
			txtLotId.selectAll();
		}
	}
	
	private SelectionListener getSelectionListener() {
		return new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				try {
					if(lotManager != null && lotViewer != null) {
						TableItem[] items = lotViewer.getTable().getSelection();
						if (items != null && items.length > 0){
							Lot lot = (Lot)items[0].getData();
							boolean confirmDelete = UI.showConfirm(Message
									.getString("common.confirm_delete"));
							if (confirmDelete && lot != null) {
								ManufactureOrderBom moBom = lotMap.get(lot);
								List<Lot> lots = moBomMap.get(moBom);
								lots.remove(lot);
								moBomMap.put(moBom, lots);
								lotMap.remove(lot);
								inputLots.remove(lot);
								refreshLotListViewer();
								
								// 删除后重新计算lot当前总数，并提示接收信息
								BigDecimal total = BigDecimal.ZERO;
								for(Lot l : lots) {
									total = total.add(l.getQtyTransaction());
								}
								if(total.compareTo(moBom.getUnitQty()) < 0) {
									receivedFinishedBoms.remove(moBom);
									moBomViewer.refresh(moBom);
									setReceiveInfo(String.format(Message.getString("wip.has_received_qty"),
											moBom.getMaterialId(), total.toString()), false);									
								}
							}
						}
					}
				} catch(Exception e) {
					ExceptionHandlerManager.asyncHandleException(e);
					logger.error("Error at MoLineReceiveSection ：getSelectionListener() ", e);
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		};
	}
	
	private void refreshMoBomListViewer() {
		if(moBomManager != null && moBomViewer != null) {
			if(Lot.LOTTYPE_MATERIAL.equals(parentLot.getLotType())){
				StringBuffer whereClause = new StringBuffer();
				whereClause.append(" materialRrn = ");
				whereClause.append(parentLot.getMaterialRrn());
				MoLineListDialog mld = new MoLineListDialog(UI.getActiveShell(), form, whereClause.toString());
				if(mld.open() == Dialog.OK){
					ManufactureOrderLine moLine = mld.getMoLine();
					if(moLine != null){
						parentLot.setMoLineRrn(moLine.getObjectRrn());
						moBoms = getChildrenLotBom(parentLot);
						moBomManager.setInput(moBoms);
						moBomManager.updateView(moBomViewer);
					}
				}
			}else{
				moBoms = getChildrenLotBom(parentLot);
				moBomManager.setInput(moBoms);
				moBomManager.updateView(moBomViewer);
			}
			
		}
	}
	
	private void refreshLotListViewer() {
		if(lotManager != null && lotViewer != null) {
			lotManager.setInput(inputLots);
			lotViewer.refresh();
		}
	}
	
	/* 单击保存并进入下一个MoLine的接收, 更新已存在数据 */
	public void refresh() {
		setReceiveInfo("", false);
		moBomMap = new LinkedHashMap<ManufactureOrderBom, List<Lot>>();
		lotMap = new LinkedHashMap<Lot, ManufactureOrderBom>();
		inputLots = new ArrayList<Lot>();
		receivedFinishedBoms = new ArrayList<ManufactureOrderBom>();
		refreshMoBomListViewer();
		if(txtLotId != null && !txtLotId.isDisposed()) {
			txtLotId.setText("");
		}
		refreshLotListViewer();
	}

	protected List<ManufactureOrderBom> getChildrenLotBom(Lot parentLot) {
		List<ManufactureOrderBom> moBoms = null;
		try {
			//如果为Serial类型，此时的QtyTransaction为接收数量
			WipManager wipManager = Framework.getService(WipManager.class);
			moBoms = wipManager.getLotBom(parentLot);
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at MoLineReceiveSection ：ctreateDetailTableViewer() ", e);
		}
		return moBoms;
	}
	
	// 获得已经存在的Batch类型的Lot
//	protected void setExistBatchQty(WipManager wipManager) {
//		List<Lot> preLots = inputLots;
//		this.refresh();
//		inputLots = wipManager.getNextChildLot(parentDialog.getParentLot(), preLots);
//		resetReferenceQty();
//		moBomManager.setInput(moBoms);
//		moBomManager.updateView(moBomViewer);
//		refredLotListViewer();
//	}
	
	//为Batch类型物料自动加入批次，并将批次已满的Bom加入到receivedFinishedBoms，在刷新时可以蓝色显示
	//该方法暂时未用
	protected void resetReferenceQty() {
		List<Lot> newInputLots = new ArrayList<Lot>();
		for(ManufactureOrderBom moBom : moBoms) {
			List<Lot> list = getLotListByMaterial(moBom.getMaterialRrn());
			BigDecimal total = BigDecimal.ZERO;
			List<Lot> lots = new ArrayList<Lot>();
			for(Lot lot : list) {
				if(total.compareTo(moBom.getUnitQty()) >= 0)
					break;
//				// 如果继续往下执行，说明total数仍小于unitQty，此时的lot仍会在下面用到
//				this.currentQtyMap.put(lot, lot.getQtyCurrent());				
				
				// 总数量 + lot当前数量 > moBom中需要数量(既unitQty)
				if(total.doubleValue() + lot.getQtyTransaction().doubleValue() > moBom.getUnitQty().doubleValue()) {
					lot.setQtyTransaction(moBom.getUnitQty().subtract(total));
				}
				total = total.add(lot.getQtyTransaction());
				if(total.compareTo(moBom.getUnitQty()) >= 0)
					receivedFinishedBoms.add(moBom);
				lots.add(lot);
				this.lotMap.put(lot, moBom);
			}
			this.moBomMap.put(moBom, lots);
			newInputLots.addAll(lots);
		}
		inputLots = newInputLots;
	}
	
	private List<Lot> getLotListByMaterial(Long materialRrn) {
		List<Lot> lots = new ArrayList<Lot>();
		for(Lot lot : inputLots) {
			if(lot.getMaterialRrn().equals(materialRrn)) {
				lots.add(lot);
			}
		}
		return lots;
	}
	
	public boolean isBatchLot(Lot lot) {
		if(lotMap == null || !lotMap.containsKey(lot))
			return false;
		ManufactureOrderBom bom = lotMap.get(lot);
		if(bom.getMaterial() != null
				&& Lot.LOTTYPE_BATCH.equals(bom.getMaterial().getLotType())) 
			return true;
		return false;
	}
	
	public boolean isGreaterInputQty(Lot lot, BigDecimal inputQty, BigDecimal qtyCurrent) {
		if(inputQty.compareTo(lot.getQtyTransaction()) <= 0) {
			return false;
		}
		return true;
	}
	
	public BigDecimal getLotActualCurrentQty(Lot lot) {
		return lot.getQtyTransaction();
	}
	
	public boolean validate() {
		if(moBoms == null || moBoms.size() == 0)
			return false;
		StringBuffer emsg = new StringBuffer("");//提示没有输入批次信息
		StringBuffer emsg2 = new StringBuffer("");//提示批次不够时是否继续保存
		HashMap<ManufactureOrderBom, BigDecimal> mobs =  new LinkedHashMap<ManufactureOrderBom, BigDecimal>();
		boolean isValid = true;
		
		for(ManufactureOrderBom moBom : moBoms) {
			List<Lot> lots = moBomMap.get(moBom);
			BigDecimal total = BigDecimal.ZERO;
			if(lots != null && lots.size() > 0) {
				for(Lot lt : lots) {
					total = total.add(lt.getQtyTransaction());
				}
			} else {
				if(moBom.getMaterial() != null && Lot.LOTTYPE_MATERIAL.equals(moBom.getMaterial().getLotType()))
					continue;
				isValid = false;
				emsg.append(String.format(Message.getString("wip.didn't_input_lot"), moBom.getMaterialName()));
				emsg.append(";\n");
			}
			//如果批次总数量大于BOM需求数量，则提示数量超出，不能保存
			if(total.compareTo(moBom.getUnitQty()) > 0) {
					isValid = false;
					emsg.append(String.format(Message.getString("wip.quantity_is_larger_equal"),
							moBom.getMaterialName(), String.valueOf(total.doubleValue()), String.valueOf(moBom.getUnitQty().doubleValue())));
					emsg.append(";\n");
			}
			if(total.compareTo(moBom.getUnitQty()) < 0) {
				isValid = false;
				//小于需求数并且为Serial类型，提示不能保存，若为Batch或Material类型，则提示时是否继续保存
				if(moBom.getMaterial() != null && Lot.LOTTYPE_SERIAL.equals(moBom.getMaterial().getLotType())) {
					emsg.append(String.format(Message.getString("wip.quantity_is_smaller_equal"),
							moBom.getMaterialName(), String.valueOf(moBom.getUnitQty().doubleValue()), String.valueOf(total.doubleValue())));
					emsg.append(";\n");
				} else {
					emsg2.append(String.format(Message.getString("wip.quantity_is_smaller_equal"),
							moBom.getMaterialName(), String.valueOf(moBom.getUnitQty().doubleValue()), String.valueOf(total.doubleValue())));
					emsg2.append(";\n");
					mobs.put(moBom, total);					
				}
			}
		}
		if(!isValid) {
			if(!"".equals(emsg.toString())) {
				UI.showError(emsg.toString());
				return false;
			} else if(!"".equals(emsg2.toString())) {
				emsg2.append(Message.getString("common.continue_to_save"));
				if(UI.showConfirm(emsg2.toString())) {
					for(ManufactureOrderBom moBom : mobs.keySet()) {
						//如果批次总数量小于BOM需求数量并确认保存，则用最后一个批次将其数量补满
						List<Lot> lots = moBomMap.get(moBom);
						Lot lot  = lots.get(lots.size() - 1);
						lot.setQtyTransaction(lot.getQtyTransaction().add(moBom.getUnitQty().subtract(mobs.get(moBom))));
					}
				} else {
					return false;
				}
			}
		}
		return true;
	}
	
	private void decorateButton(Button button) {
		button.setFont(JFaceResources.getDialogFont());
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalAlignment = GridData.END;
		int widthHint = 50;  //IDialogConstants.BUTTON_WIDTH
		Point minSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		data.widthHint = Math.max(widthHint, minSize.x);
		button.setLayoutData(data);
	}
	
	@Override
	public List<ManufactureOrderBom> getReceivedFinishedMoBoms() {
		return this.receivedFinishedBoms;
	}
	
	public List<Lot> getInputLots() {
		return inputLots;
	}
	
	public FontMetrics getFontMetric(Control ctrl) {
        FontMetrics fm;
        GC gc = new GC(ctrl);
        fm = gc.getFontMetrics();
        gc.dispose();
        return fm;
    }
	
	private void autoInputLot(){
		try{
			Long moLineRrn = parentLot.getMoLineRrn();
			ManufactureOrderLine moLine = null;
			if(moLineRrn != null){
				if(adManager == null) {
					adManager = Framework.getService(ADManager.class);
				}
				moLine = new ManufactureOrderLine();
				moLine.setObjectRrn(moLineRrn);
				moLine = (ManufactureOrderLine) adManager.getEntity(moLine);
				WipManager wipManager = Framework.getService(WipManager.class);
				List<LotComponent> components = wipManager.receiveDetail(Env.getOrgRrn(), moLine.getObjectRrn(), parentLot.getLotId());
				List<String> lots = new ArrayList<String>();
				HashMap<String, String> materialLotMap = new LinkedHashMap<String,String>() ;
				HashMap<String, List<String>> multiLotMap = new LinkedHashMap<String, List<String>>();
				for(LotComponent com : components){
					Material childMaterial = new Material();
					childMaterial.setObjectRrn(com.getMaterialChildRrn());
					childMaterial = (Material) adManager.getEntity(childMaterial);
					if(Lot.LOTTYPE_BATCH.equalsIgnoreCase(childMaterial.getLotType()) || 
						Lot.LOTTYPE_BATCH_A.equalsIgnoreCase(childMaterial.getLotType())){
						String key = com.getMaterialChildId();
						if(!materialLotMap.containsKey(key)){
							materialLotMap.put(key, com.getLotChildId());
						}else{
							List<String> list = multiLotMap.get(key);
							if(list == null){
								list = new ArrayList<String>();
							}
							list.add(com.getLotChildId());
							multiLotMap.put(key, list);
						}
					}
				}
				for(String key : multiLotMap.keySet()){
					materialLotMap.remove(key);
				}
				
				for(String lotId : materialLotMap.values()){
					addLot(lotId);
				}
				
				if(multiLotMap.size() > 0){
					StringBuffer materials = new StringBuffer();
					for(String materialId : multiLotMap.keySet()){
						materials.append(materialId);
						materials.append(",");
					}
					UI.showWarning("物料 " + materials + " 使用了多个批次，需要手动添加");
				}
			}
		}catch(Exception e){
			logger.error("AttachSubLotSection : autoInputLot()",e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
}

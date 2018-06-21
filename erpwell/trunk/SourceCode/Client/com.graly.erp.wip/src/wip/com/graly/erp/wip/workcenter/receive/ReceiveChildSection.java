package com.graly.erp.wip.workcenter.receive;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
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
import com.graly.erp.base.model.Storage;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.inv.model.WorkShopStorage;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.erp.wip.model.WorkSchopMaterial;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.model.WorkCenter;
import com.graly.mes.wip.client.LotManager;
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotTemp;

public class ReceiveChildSection implements GainableMoBoms {
	private static final Logger logger = Logger.getLogger(ReceiveChildSection.class);
	protected MoLineReceiveSection parentSection;

	protected IManagedForm form;

	private MoBomTableManager moBomManager;
	private LotTableManager lotManager;
	private TableViewer moBomViewer, lotViewer;
	private Text txtLotId;
	private Label receiveInfo;
	private Button btDelete;
	private HashMap<ManufactureOrderBom, List<Lot>> moBomMap;
	private HashMap<Lot, ManufactureOrderBom> lotMap;
	private List<ManufactureOrderBom> moBoms;
	private List<Lot> inputLots;
	private List<ManufactureOrderBom> receivedFinishedBoms;
	
	INVManager invManager;
	WipManager wipManager;

	public ReceiveChildSection() {}
	
	public ReceiveChildSection(MoLineReceiveSection parentSection) {
		this.parentSection = parentSection;
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
		toAddInput();//���ڽ����ݿ���ʱ��������ݷ���tableViewer
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
		txtLotId = toolkit.createText(comp, "", SWT.BORDER);
		txtLotId.setTextLimit(32);
		GridData gd = new GridData();//GridData.FILL_HORIZONTAL
		gd.heightHint = 13;
		gd.widthHint = Dialog.convertWidthInCharsToPixels(this.getFontMetric(txtLotId), 32);
		txtLotId.setLayoutData(gd);
		txtLotId.addKeyListener(getKeyListener());
		txtLotId.setFocus();
		
		receiveInfo = toolkit.createLabel(comp, "", SWT.NULL);
		receiveInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		receiveInfo.setForeground(SWTResourceCache.getColor("Function"));

		btDelete = toolkit.createButton(comp, Message.getString("common.delete"), SWT.PUSH);
		decorateButton(btDelete);
		btDelete.addSelectionListener(getSelectionListener());
	}
	
	public void toAddInput(){
		try {
			LotManager lotManager = Framework.getService(LotManager.class);
			List<LotTemp> lotTemps =lotManager.getLotTemp(this.parentSection.getMoLine().getObjectRrn(), Env.getOrgRrn());
			List<Lot> lots = new ArrayList<Lot>();
			for(LotTemp lotTemp : lotTemps){
				addLot2(lotTemp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void addLot2(LotTemp lotTemp) {
		Lot lot = null;
		String lotId= lotTemp.getLotID();
		try {
			if(lotId != null && !"".equals(lotId)) {
				setReceiveInfo("", false);
				if(invManager == null) {
					invManager = Framework.getService(INVManager.class);
				}
				lot = invManager.getLotByLotId(Env.getOrgRrn(), lotId);
				if(lot == null || lot.getMaterialRrn() == null) {
					UI.showError(Message.getString("inv.lotnotexist"));
					return;
				}
				
				// ���ΪMaterial���ͣ�����ʾ�����������Σ�ϵͳ���Զ�����BOM��ϵ�۳������ĵ�����
				if(Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
					UI.showError(Message.getString("wip.material_lot_needn't_receive"));
					return;
				}	
				if(wipManager == null) {
					wipManager = Framework.getService(WipManager.class);
				}
				//���ΪBATCH_A��BATCH_B����(��BATCH)
				if(Lot.LOTTYPE_BATCH.equals(lot.getLotType()) 
						|| Lot.LOTTYPE_BATCH_A.equals(lot.getLotType())) {
					lot = wipManager.getWipLotByLotId(lotId, Env.getOrgRrn());
				}
				ADManager adManager = Framework.getService(ADManager.class);
//				LotTemp lotTemp =adManager.getEntityList(Env.getOrgRrn(), LotTemp.class, 1, "lotId='"+lotId+"'", null).get(0);
				Material material = new Material();
				material.setObjectRrn(lot.getMaterialRrn());
				material=(Material) adManager.getEntity(material);
//				if(!material.getAns()){// ������ϲ���������Ҫ�Ƚ�һ�¿���Ƿ�
//				// �ȽϿ���Ƿ�
//					if (lot.getQtyCurrent().compareTo(lotTemp.getMainQty()) < 0) {
//						lot.setQtyTransaction(lot.getQtyCurrent());
//					} else {
//						lot.setQtyTransaction(lotTemp.getMainQty());
//					}
//				} else {
					lot.setQtyTransaction(lotTemp.getMainQty());
//				}
				
				for(ManufactureOrderBom moBom : moBoms) {
					if(lot.getMaterialRrn().equals(moBom.getMaterialRrn())) {
						List<Lot> lots = moBomMap.get(moBom);
						if(lots == null) {
							lots = new ArrayList<Lot>();
						}
						lots.add(lot);
						moBomMap.put(moBom, lots);
						lotMap.put(lot, moBom);
						if(lot != null) {
						inputLots.add(lot);
						}
						break;
					}
				}
				refredLotListViewer();
			}
		} catch(Exception e) {
			logger.error("Error at MoLineReceiveSection ��getLotByLotId() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		} finally {
			txtLotId.selectAll();
		}
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
		lotManager = new LotTableManager(this);
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
			logger.error("Error at MoLineReceiveSection ��getKeyListener() ", e);
		}
		return null;
	}
	
	private void addLot(String lotId) {
		Lot lot = null;
		try {
			if(lotId != null && !"".equals(lotId)) {
				setReceiveInfo("", false);
				if(invManager == null) {
					invManager = Framework.getService(INVManager.class);
				}
				lot = invManager.getLotByLotId(Env.getOrgRrn(), lotId);
				if(lot == null || lot.getMaterialRrn() == null) {
					UI.showError(Message.getString("inv.lotnotexist"));
					return;
				}
				
				// ���ΪMaterial���ͣ�����ʾ�����������Σ�ϵͳ���Զ�����BOM��ϵ�۳������ĵ�����
				if(Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
					UI.showError(Message.getString("wip.material_lot_needn't_receive"));
					return;
				}	
				if(wipManager == null) {
					wipManager = Framework.getService(WipManager.class);
				}
				//���ΪBATCH_A��BATCH_B����(��BATCH)
				if(Lot.LOTTYPE_BATCH.equals(lot.getLotType()) 
						|| Lot.LOTTYPE_BATCH_A.equals(lot.getLotType())) {
					lot = wipManager.getWipLotByLotId(lotId, Env.getOrgRrn());
					if (lot.getQtyCurrent().compareTo(BigDecimal.ZERO) <= 0) {
						lot.setQtyTransaction(BigDecimal.ZERO);
					} else {
						lot.setQtyTransaction(lot.getQtyCurrent());
					}
					//��������Ϊ��ʱ�Կ����ø����ν��գ�����ΪLOTTYPE_BATCH_A���ͣ��򲻿��ԣ��ݲ�����LOTTYPE_BATCH_A���
//					if(lot.getQtyCurrent().doubleValue() <= 0) {
//						UI.showError(String.format(Message.getString("wip.lot_currentqty_used_out"), lot.getLotId()));
//						return;
//					}
				}
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
				
				boolean isLotExistBoms = false; // ��ʶlot��Ӧ�������Ƿ���moBoms�к���
				for(ManufactureOrderBom moBom : moBoms) {
					if(lot.getMaterialRrn().equals(moBom.getMaterialRrn())) {
						isLotExistBoms = true;
						List<Lot> lots = moBomMap.get(moBom);
						if(lots == null) {
							lots = new ArrayList<Lot>();
						}
						BigDecimal total = BigDecimal.ZERO;
						for(Lot lt : lots) {
							total = total.add(lt.getQtyTransaction());
						}
						
						if(total.doubleValue() < moBom.getUnitQty().doubleValue()) {
							// ������ + lot��ǰ���� > moBom����Ҫ����(��unitQty), ��Lot����������qtyTransactionΪ
							// qtyTransaction = moBom.getUnitQty() - total��
							if(total.doubleValue() + lot.getQtyTransaction().doubleValue() > moBom.getUnitQty().doubleValue()) {
								lot.setQtyTransaction(moBom.getUnitQty().subtract(total));
							}else{
////								ADManager adManager = Framework.getService(ADManager.class);
////								Material material = new Material();
////								material.setObjectRrn(lot.getMaterialRrn());
////								material=(Material) adManager.getEntity(material);
////								if(!material.getAns()){//������ϲ���������Ҫ�Ƚ�һ�¿���Ƿ�
//////									//�ȽϿ���Ƿ�
//									BigDecimal qtyNeedStill = moBom.getUnitQty().subtract(total);
////									BigDecimal qtyStock = lot.getQtyTransaction();
////									if(qtyStock.compareTo(BigDecimal.ZERO) <= 0){//qtyStock�Ǿ�������Ŀ�棬lot.getQtyCurrent()����ʵ�Ŀ��
////										UI.showError("���� " + lot.getLotId() + " ���Ϊ " + lot.getQtyCurrent().toString() + ",�޷���ʹ��!");
////										return;
////									}
////									if(qtyStock.compareTo(qtyNeedStill) < 0){//���������Ȼ���Ҫ������С�Ļ����������ʹ��
////										lot.setQtyTransaction(qtyStock);
////									}
////								}else{
//									if(!Lot.LOTTYPE_SERIAL.equals(lot.getLotType())){
//										lot.setQtyTransaction(qtyNeedStill);
//									}
////								}
								
								/*�ָ���2012.2.24֮ǰ�İ汾,�����������ο��,�������ϵ��Ƿ�����������Ծ����Ƿ�ʹ����һ����������������Ҫ*/
								ADManager adManager = Framework.getService(ADManager.class);
								Material material = new Material();
								material.setObjectRrn(lot.getMaterialRrn());
								material=(Material) adManager.getEntity(material);
								if(!material.getAns()){//������ϲ���������Ҫ�Ƚ�һ�¿���Ƿ�
									//�ȽϿ���Ƿ�
									BigDecimal qtyNeedStill = moBom.getUnitQty().subtract(total);
									BigDecimal qtyStock = lot.getQtyTransaction();
									if(qtyStock.compareTo(BigDecimal.ZERO) <= 0){//qtyStock�Ǿ�������Ŀ�棬lot.getQtyCurrent()����ʵ�Ŀ��
										UI.showError("���� " + lot.getLotId() + " ���Ϊ " + lot.getQtyCurrent().toString() + ",�޷���ʹ��!");
										return;
									}
									if(qtyStock.compareTo(qtyNeedStill) < 0){//���������Ȼ���Ҫ������С�Ļ����������ʹ��
										lot.setQtyTransaction(qtyStock);
									}
								}else{
									lot.setQtyTransaction(moBom.getUnitQty().subtract(total));
								}
							}
						} else {
//							UI.showWarning(String.format(Message.getString("wip.qty_already_full"),
//									moBom.getMaterial().getMaterialId()));
							lot.setQtyTransaction(BigDecimal.ZERO);
						}
						
						lots.add(lot);
						moBomMap.put(moBom, lots);
						lotMap.put(lot, moBom);
						if(lot != null) {
							inputLots.add(lot);
							refredLotListViewer();
							
							// �ж�bom�б����ɫ�Ƿ���Ҫ�ı䣬�ı���յ���ʾ��Ϣ
							total = total.add(lot.getQtyTransaction());
							if(total.compareTo(moBom.getUnitQty()) < 0) {
								this.setReceiveInfo(String.format(Message.getString("wip.has_received_qty"),
										moBom.getMaterialId(), total.toString()), false);
							} else {
								receivedFinishedBoms.add(moBom);
								this.moBomViewer.refresh(moBom);
								this.setReceiveInfo(String.format(Message.getString("wip.material_receive_finished"),
										moBom.getMaterialId()), true);
							}
							parentSection.setDoOprationsTrue();
						}
						break;
					}
				}
				
				if(!isLotExistBoms) {
					UI.showError(String.format(Message.getString("wip.material_does't_exisit_moboms"),
							lot.getLotId(), lot.getMaterialId(), parentSection.getMoLine().getMaterialId()));
					return;
				}
			}
		} catch(Exception e) {
			logger.error("Error at MoLineReceiveSection ��getLotByLotId() ", e);
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
								refredLotListViewer();
								
								// ɾ�������¼���lot��ǰ����������ʾ������Ϣ
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
					logger.error("Error at MoLineReceiveSection ��getSelectionListener() ", e);
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		};
	}
	
	private void refreshMoBomListViewer() {
		if(moBomManager != null && moBomViewer != null) {
			Lot parentLot = parentSection.getParentLot();
			moBoms = getChildrenLotBom(parentLot);
			moBomManager.setInput(moBoms);
			moBomManager.updateView(moBomViewer);
		}
	}
	
	private void refredLotListViewer() {
		if(lotManager != null && lotViewer != null) {
			lotManager.setInput(inputLots);
			lotViewer.refresh();
		}
	}
	
	/* �������沢������һ��MoLine�Ľ���, �����Ѵ������� */
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
		refredLotListViewer();
	}

	protected List<ManufactureOrderBom> getChildrenLotBom(Lot parentLot) {
		List<ManufactureOrderBom> moBoms = null;
		try {
			//���ΪSerial���ͣ���ʱ��QtyTransactionΪ��������
			WipManager wipManager = Framework.getService(WipManager.class);
			moBoms = wipManager.getLotBom(parentLot);
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at MoLineReceiveSection ��ctreateDetailTableViewer() ", e);
		}
		return moBoms;
	}
	
	// ����Ѿ����ڵ�Batch���͵�Lot
//	protected void setExistBatchQty(WipManager wipManager) {
//		List<Lot> preLots = inputLots;
//		this.refresh();
//		inputLots = wipManager.getNextChildLot(parentDialog.getParentLot(), preLots);
//		resetReferenceQty();
//		moBomManager.setInput(moBoms);
//		moBomManager.updateView(moBomViewer);
//		refredLotListViewer();
//	}
	
	//ΪBatch���������Զ��������Σ���������������Bom���뵽receivedFinishedBoms����ˢ��ʱ������ɫ��ʾ
	//�÷�����ʱδ��
	protected void resetReferenceQty() {
		List<Lot> newInputLots = new ArrayList<Lot>();
		for(ManufactureOrderBom moBom : moBoms) {
			List<Lot> list = getLotListByMaterial(moBom.getMaterialRrn());
			BigDecimal total = BigDecimal.ZERO;
			List<Lot> lots = new ArrayList<Lot>();
			for(Lot lot : list) {
				if(total.compareTo(moBom.getUnitQty()) >= 0)
					break;
//				// �����������ִ�У�˵��total����С��unitQty����ʱ��lot�Ի��������õ�
//				this.currentQtyMap.put(lot, lot.getQtyCurrent());				
				
				// ������ + lot��ǰ���� > moBom����Ҫ����(��unitQty)
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
	
	public boolean validate(){
		return validate(false);
	}
	
	public boolean validate(boolean isAdvance) {
		if(moBoms == null || moBoms.size() == 0)
			return false;
		StringBuffer emsg = new StringBuffer("");//��ʾû������������Ϣ
		StringBuffer emsg2 = new StringBuffer("");//��ʾ���β���ʱ�Ƿ��������
		StringBuffer emsg3 = new StringBuffer("");//��ʾ��Щ���ο�治�㣬�Լ��Ƿ��������
		HashMap<ManufactureOrderBom, BigDecimal> mobs =  new LinkedHashMap<ManufactureOrderBom, BigDecimal>();
		boolean isValid = true;
		
		for(ManufactureOrderBom moBom : moBoms) {
			List<Lot> lots = moBomMap.get(moBom);
			if(Env.getOrgRrn()== 139420L){
				if(!validateStorage(moBom,emsg3 )){
					return false;
				}
			}else if(Env.getOrgRrn()== 49204677L || Env.getOrgRrn()== 70000000L ){
				if(!validateStorageFY(moBom,emsg3 )){
					return false;
				}
			}
			
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
			//�����������������BOM��������������ʾ�������������ܱ���
			if(total.compareTo(moBom.getUnitQty()) > 0) {
					isValid = false;
					emsg.append(String.format(Message.getString("wip.quantity_is_larger_equal"),
							moBom.getMaterialName(), String.valueOf(total.doubleValue()), String.valueOf(moBom.getUnitQty().doubleValue())));
					emsg.append(";\n");
			}
			if(total.compareTo(moBom.getUnitQty()) < 0) {
				isValid = false;
				//С������������ΪSerial���ͣ���ʾ���ܱ��棬��ΪBatch��Material���ͣ�����ʾʱ�Ƿ��������
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
						//�������������С��BOM����������ȷ�ϱ��棬�������һ�����ν�����������
						List<Lot> lots = moBomMap.get(moBom);
						Lot lot  = lots.get(lots.size() - 1);
						lot.setQtyTransaction(lot.getQtyTransaction().add(moBom.getUnitQty().subtract(mobs.get(moBom))));
					}
					lotViewer.refresh();
				} else {
					return false;
				}
			}
		}
		
		{
			boolean canGoOn = true;
			for(Lot lot : inputLots) {
				try {
					ADManager adManager = Framework.getService(ADManager.class);
					Material material = new Material();
					material.setObjectRrn(lot.getMaterialRrn());
					material=(Material) adManager.getEntity(material);
					//�ȽϿ���Ƿ�
					BigDecimal qtyUse = lot.getQtyTransaction();//��ŵ��ǵ�ǰ�����ʹ������
					BigDecimal qtyOnHand = lot.getQtyCurrent();//��������ģ���ŵ��ǵ�ǰ���ÿ��
					if(qtyOnHand.compareTo(qtyUse) < 0){
						if(!material.getAns()){//������ϲ���������Ҫ�Ƚ�һ�¿���Ƿ�
							if(!isAdvance){
								canGoOn = false;
							}
							emsg3.append("���� " + lot.getLotId() + " ���Ϊ " + lot.getQtyCurrent().toString() + ",���������!");
							emsg3.append("\n");
						}else{
							emsg3.append("���� " + lot.getLotId() + " ���Ϊ " + lot.getQtyCurrent().toString() + ",�������!");
							emsg3.append("\n");
						}
					}
				} catch (Exception e) {
					logger.error("ReceiveChildSection : validate()", e);
				}
			}
			if(!isAdvance){
				if(StringUtils.isNotEmpty(emsg3.toString())) UI.showWarning(emsg3.toString(), "��ʾ");
			}else{
				if(StringUtils.isNotEmpty(emsg3.toString())){
					canGoOn = UI.showConfirm(emsg3.append("\n �Ƿ����?").toString(), "���");
				}
			}
			return canGoOn;
		}
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
		return this.inputLots;
	}
	
	public FontMetrics getFontMetric(Control ctrl) {
        FontMetrics fm;
        GC gc = new GC(ctrl);
        fm = gc.getFontMetrics();
        gc.dispose();
        return fm;
    }
	
	//���������Ž���У��
	public boolean validateStorage(ManufactureOrderBom moBom,StringBuffer emsg3 ){
		boolean flag = true;
			try {
			Material material = moBom.getMaterial();
			if(!material.getAns()){
				ADManager adManager = Framework.getService(ADManager.class);
				List<WorkSchopMaterial> wsMaterials = adManager.getEntityList(Env.getOrgRrn(), WorkSchopMaterial.class,2,"materialId='"+material.getMaterialId()+"'","");
				if(wsMaterials.size()<=0){//ֻ������Ҫ�ܿص������嵥 �ҵ���У���� ����FALSE��ʾУ�鲻ͨ��ʣ�µĴ��벻��ִ�У�TRUE��ʶУ����ͨ�����ǻ���ʣ�µĴ���ûִ��
					return flag;
				}
				
				WorkCenter wc = new WorkCenter();
				wc.setObjectRrn(parentSection.getMoLine().getWorkCenterRrn());
				wc = (WorkCenter) adManager.getEntity(wc);
				Warehouse warehouse = new Warehouse();
				warehouse.setObjectRrn(wc.getWarehouseRrn());
				warehouse = (Warehouse) adManager.getEntity(warehouse);
				
				Warehouse workWarehouse = new Warehouse();
				if(warehouse.getDefaultLocatorRrn()!=null && !"".equals(warehouse.getDefaultLocatorRrn())){//�޸�BUGû������defaultRrn�Ͳ���У����
					workWarehouse.setObjectRrn(warehouse.getDefaultLocatorRrn());
					workWarehouse = (Warehouse) adManager.getEntity(workWarehouse);
				}else{
					workWarehouse=null;
				}
				if(workWarehouse!=null){
					INVManager invManager = Framework.getService(INVManager.class);
					//
					Storage storage = invManager.getMaterialStorage(moBom.getOrgRrn(), moBom.getMaterialRrn(), 151046L, Env.getUserRrn());
					BigDecimal qtyStorage = storage.getQtyOnhand()!=null?storage.getQtyOnhand():BigDecimal.ZERO;
					//��ǰ�������
					WorkShopStorage  wsStorage= invManager.getMaterialWorkShopStorage(moBom.getOrgRrn(), moBom.getMaterialRrn(), workWarehouse.getObjectRrn(), Env.getUserRrn());
					if(qtyStorage.subtract(moBom.getUnitQty()).compareTo(BigDecimal.ZERO) <0){
						flag = false;
						emsg3.append("����: " + material.getMaterialId() + " ���Ϊ " + qtyStorage);
						emsg3.append(". ��Ҫ���ĵ�����Ϊ " +  moBom.getUnitQty() + ",���������!");
						emsg3.append("\n");
						UI.showError(emsg3.toString());
						return flag;
					}
					BigDecimal qtyWorkStorage = wsStorage.getQtyOnhand()!=null?wsStorage.getQtyOnhand():BigDecimal.ZERO;
					if(qtyWorkStorage.subtract(moBom.getUnitQty()).compareTo(BigDecimal.ZERO) <0){
						flag = false;
						emsg3.append("����: " + material.getMaterialId() + " ������Ϊ " + qtyWorkStorage);
						emsg3.append(". ��Ҫ���ĵ�����Ϊ " +  moBom.getUnitQty() + ",���������!");
						emsg3.append("\n");
						UI.showError(emsg3.toString());
						return flag;
					}
				}
				
			}
			} catch (Exception e) {
				logger.error("ReceiveChildSection : validateStorage()", e);
				ExceptionHandlerManager.asyncHandleException(e);
				return false;
			}
		return flag;
	}
	//���������Ž���У��
	public boolean validateStorageFY(ManufactureOrderBom moBom,StringBuffer emsg3 ){
		boolean flag = true;
			try {
			Material material = moBom.getMaterial();
			if(!material.getAns()){
				INVManager invManager = Framework.getService(INVManager.class);
				//9
				Warehouse warehouse = invManager.getWriteOffWarehouse(Env.getOrgRrn());
				Storage storage = invManager.getMaterialStorage(moBom.getOrgRrn(), moBom.getMaterialRrn(), warehouse.getObjectRrn(), Env.getUserRrn());
				BigDecimal qtyStorage = storage.getQtyOnhand()!=null?storage.getQtyOnhand():BigDecimal.ZERO;
				//��ǰ�������
				if(qtyStorage.subtract(moBom.getUnitQty()).compareTo(BigDecimal.ZERO) <0){
					flag = false;
					emsg3.append("����: " + material.getMaterialId() + " ���Ϊ " + qtyStorage);
					emsg3.append(". ��Ҫ���ĵ�����Ϊ " +  moBom.getUnitQty() + ",���������!");
					emsg3.append("\n");
					UI.showError(emsg3.toString());
					return flag;
				}
				
			}
			} catch (Exception e) {
				logger.error("ReceiveChildSection : validateStorage()", e);
				ExceptionHandlerManager.asyncHandleException(e);
				return false;
			}
		return flag;
	}
}

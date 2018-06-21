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
	private HashMap<ManufactureOrderBom, List<Lot>> moBomMap;//�������BOM�嵥��ÿ��������ʹ�õ�Lot
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
	 * @param parentLot ��Ҫ��ֵ���
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
		txtLotId = toolkit.createText(comp, "�ڴ��������ϵ�����", SWT.BORDER);
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
			logger.error("Error at AttachSubLotSection ��getKeyListener() ", e);
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
				// ���ΪMaterial���ͣ�����ʾ�����������Σ�ϵͳ���Զ�����BOM��ϵ�۳������ĵ�����
				if(Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
					UI.showError(Message.getString("wip.material_lot_needn't_receive"));
					return;
				}	
				if(wipManager == null) {
					wipManager = Framework.getService(WipManager.class);
				}
				//���ΪBATCH_A��BATCH_B����(��BATCH)
//				if(Lot.LOTTYPE_BATCH.equals(lot.getLotType()) 
//						|| Lot.LOTTYPE_BATCH_A.equals(lot.getLotType())) {
//					lot = wipManager.getWipLotByLotId(lotId, Env.getOrgRrn());
//					if (lot.getQtyCurrent().compareTo(BigDecimal.ZERO) <= 0) {
//						lot.setQtyTransaction(BigDecimal.ZERO);
//					} else {
//						lot.setQtyTransaction(lot.getQtyCurrent());
//					}
//					//��������Ϊ��ʱ�Կ����ø����ν��գ�����ΪLOTTYPE_BATCH_A���ͣ��򲻿��ԣ��ݲ�����LOTTYPE_BATCH_A���
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
				
				boolean isLotExistBoms = false; // ��ʶlot��Ӧ�������Ƿ���moBoms�к���
				for(ManufactureOrderBom moBom : moBoms) {
					if(lot.getMaterialRrn().equals(moBom.getMaterialRrn())) {
						if(Lot.LOTTYPE_BATCH.equals(lot.getLotType()) 
								|| Lot.LOTTYPE_BATCH_A.equals(lot.getLotType())) {
							lot.setQtyTransaction(moBom.getUnitQty());
						}
						isLotExistBoms = true;
						List<Lot> lots = moBomMap.get(moBom);//ȡ���Ѿ����������
						if(lots == null) {//�����û�����������ʹ���һ��List������ű����������
							lots = new ArrayList<Lot>();
						}
						BigDecimal total = BigDecimal.ZERO;
						for(Lot lt : lots) {
							total = total.add(lt.getQtyTransaction());//�����Ѿ���������ε�������
						}
						
						if(total.compareTo(moBom.getUnitQty()) < 0) {//moBom.getUnitQty()���Ǽ�������������ĵĸ����ϵ�����
							// ������ + lot��ǰ���� > moBom����Ҫ����(��unitQty), ��Lot����������qtyTransactionΪ
							// qtyTransaction = moBom.getUnitQty() - total��
							if(total.add(lot.getQtyTransaction()).compareTo(moBom.getUnitQty()) > 0) {
								lot.setQtyTransaction(moBom.getUnitQty().subtract(total));//ע��A���������lot������������ǰ��������ε��������ڸ����ϵ��������Ļ�ֻ��ʣ��Ĳ��
							}
						} else {//�����ܳ��������������������������Ϊ����ע��A���������˿���
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
							
							// �ж�bom�б����ɫ�Ƿ���Ҫ�ı䣬�ı���յ���ʾ��Ϣ
							total = total.add(lot.getQtyTransaction());
							if(total.compareTo(moBom.getUnitQty()) < 0) {//�����������������������������ʾ�Ѿ������˶��ٵ���Ϣ
								this.setReceiveInfo(String.format(Message.getString("wip.has_received_qty"),
										moBom.getMaterialId(), total.toString()), false);
							} else {//����ͼ��뵽�����Ѿ�������������List��
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
								refreshLotListViewer();
								
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
		refreshLotListViewer();
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
	
	public boolean validate() {
		if(moBoms == null || moBoms.size() == 0)
			return false;
		StringBuffer emsg = new StringBuffer("");//��ʾû������������Ϣ
		StringBuffer emsg2 = new StringBuffer("");//��ʾ���β���ʱ�Ƿ��������
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
					UI.showWarning("���� " + materials + " ʹ���˶�����Σ���Ҫ�ֶ����");
				}
			}
		}catch(Exception e){
			logger.error("AttachSubLotSection : autoInputLot()",e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
}

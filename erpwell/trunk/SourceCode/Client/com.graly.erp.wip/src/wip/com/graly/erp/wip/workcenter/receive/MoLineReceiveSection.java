package com.graly.erp.wip.workcenter.receive;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.graly.erp.base.model.Constants;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.erp.wip.workcenter.SeeLotDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.MDSashForm;
import com.graly.framework.base.ui.forms.field.IField;
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

public class MoLineReceiveSection {
	private static final Logger logger = Logger
			.getLogger(MoLineReceiveSection.class);
	protected MoLineReceiveDialog parentDialog;

	protected IManagedForm form;
	protected FormToolkit toolkit;
	protected Section section;
	protected SashForm sashForm;
	protected ToolItem itemPrint;
	protected ToolItem itemNext;
	protected ToolItem itemSave;
	protected ToolItem itemAdvanceSave;//�쵼�ã��ɺ��Ը���������
	protected ToolItem itemChooseIdsFromSystem;
	protected ToolItem itemCheckOnhand;
	protected ToolItem itemSaveExit;
	protected ToolItem itemTempSave;
	protected ToolItem itemWmsIn;
	public static final String KEY_RECEIVE_TEMPSAVE="WIP.Receive.MoLineTempSave";
	private ReceiveParentSection ps;
	private ReceiveChildSection cs;

	private String INSUFFICIENT_BOM_TABLE = "InsufficientMoBom";
	private String SPECIFY_LOT_TABLE = "INVLot";

	private List<Lot> currentLots = new ArrayList<Lot>();
	
	private Lot tempLot;

	protected boolean isSaved = false; // �Ƿ�����˱��涯��
	protected boolean isDid = false;
	String moComments;
	
	private ADManager adManager;

	public MoLineReceiveSection() {
	}

	public MoLineReceiveSection(MoLineReceiveDialog parentDialog,
			String lotType, String moComments) {
		this.parentDialog = parentDialog;
		this.moComments = moComments;
		ps = new ReceiveParentSection(this, lotType, moComments);
		cs = new ReceiveChildSection(this);
		parentLotClone();
	}
	public void parentLotClone(){
		try {
			this.tempLot =(Lot) parentDialog.getParentLot().clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	public void createContent(IManagedForm form, Composite parent) {
		this.form = form;
		toolkit = form.getToolkit();
		section = toolkit.createSection(parent, Section.TITLE_BAR);
		section.setText(Message.getString("wip.receive_lot_valid"));
		toolkit.createCompositeSeparator(section);

		createToolBar(section);

		TableWrapLayout layout = new TableWrapLayout();
		layout.topMargin = 0;
		layout.leftMargin = 2;
		layout.rightMargin = 2;
		layout.bottomMargin = 0;
		parent.setLayout(layout);

		section.setLayout(layout);
		TableWrapData td = new TableWrapData(TableWrapData.FILL,
				TableWrapData.FILL);
		td.grabHorizontal = true;
		td.grabVertical = false;
		section.setLayoutData(td);

		Composite client = toolkit.createComposite(section);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		client.setLayout(gridLayout);

		createSectionDesc(section);
		createSectionContent(client);

		toolkit.paintBordersFor(section);
		section.setClient(client);
	}

	protected void createSectionDesc(Section section) {
	}

	protected void createSectionContent(Composite client) {
		FormToolkit toolkit = form.getToolkit();
		sashForm = new MDSashForm(client, SWT.NULL);
		sashForm.setData("form", form); //$NON-NLS-1$
		toolkit.adapt(sashForm, false, false);
		sashForm.setMenu(client.getMenu());
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		createParentSectionContent(sashForm);
		createChildSectionContent(sashForm);
		sashForm.setWeights(new int[] { 4, 6 });
	}

	protected void createParentSectionContent(Composite client) {
		Composite parent = form.getToolkit()
				.createComposite(client, SWT.BORDER);
		parent.setLayout(new GridLayout(1, false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		ps.createContent(form, parent);
	}

	protected void createChildSectionContent(Composite client) {
		Composite parent = form.getToolkit()
				.createComposite(client, SWT.BORDER);
		parent.setLayout(new GridLayout(1, false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		cs.createContent(form, parent);
	}

	/* �������沢������һ��MoLine�Ľ���, �����Ѵ������� */
	public void refresh() {
		ps.refresh();
		cs.refresh();
		this.currentLots.clear();
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		// createToolItemNext(tBar);
		// new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemWmsIn(tBar);
		createToolItemSave(tBar);
//		createToolItemAdvanceSave(tBar);//���θ߼����
		createToolItemCheckOnhand(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
//		createToolItemSaveExit(tBar);//���α��沢�˳�
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemPrint(tBar);
		createToolItemChooseIdsFromSystem(tBar);
		createToolItemTempSave(tBar);
		section.setTextClient(tBar);
	}
	protected void createToolItemWmsIn(ToolBar tBar) {
		itemWmsIn = new ToolItem(tBar, SWT.PUSH);
		itemWmsIn.setText("WMS�������");
		itemWmsIn.setImage(SWTResourceCache.getImage("preview"));
		itemWmsIn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				wmsInAdapter();
			}
		});
	}
	
	protected void createToolItemCheckOnhand(ToolBar tBar) {
		itemCheckOnhand = new ToolItem(tBar, SWT.PUSH);
		itemCheckOnhand.setText("�����");
		itemCheckOnhand.setImage(SWTResourceCache.getImage("approve"));
		itemCheckOnhand.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				checkOnhandAdapter();
			}
		});
	}
	protected void createToolItemTempSave(ToolBar tBar) {
		itemTempSave = new AuthorityToolItem(tBar, SWT.PUSH,KEY_RECEIVE_TEMPSAVE);
		itemTempSave.setText("��ʱ����");
		itemTempSave.setImage(SWTResourceCache.getImage("save"));
		itemTempSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				tempSaveAdapter();
			}
		});
	}

	protected void tempSaveAdapter() {
		try {
			//��ʱ����Ի�����ͷ�����ݣ����ϵ���Ϣ���豸��Ϣ���������ε���Ϣ
			Lot parentLot =null;
			parentLot = ps.getParentLot();
			LinkedHashMap<String, IField> fields = ps.getInfoForm().getFields();		
			IField userQc = (IField) fields.get("userQc");
			IField moldId = (IField) fields.get("moldId");
			IField equipmentId = (IField) fields.get("equipmentId");
			IField reverseField1 = (IField) fields.get("reverseField1");
			IField reverseField2 = (IField) fields.get("reverseField2");
			IField reverseField3 = (IField) fields.get("reverseField3");
			IField reverseField4 = (IField) fields.get("reverseField4");
			IField reverseField5 = (IField) fields.get("reverseField5");
			IField lotComment = (IField) fields.get("lotComment");
			parentLot.setUserQc((String)userQc.getValue());
			parentLot.setEquipmentId((String)equipmentId.getValue());
			parentLot.setMoldId((String)moldId.getValue());
			parentLot.setReverseField1((String)reverseField1.getValue());
			parentLot.setReverseField2((String)reverseField2.getValue());
			parentLot.setReverseField3((String)reverseField3.getValue());
			parentLot.setReverseField4((String)reverseField4.getValue());
			parentLot.setReverseField5((String)reverseField5.getValue());
			parentLot.setLotComment((String)lotComment.getValue().toString());
			// ��һɾ�����ݿ���ԭ�����ڵ�����
			// �ڶ�����lot����Ϣ��һ�����������ε���Ϣ��һ����parentLot����Ϣ
			LotManager lotManager = Framework.getService(LotManager.class);
			lotManager.newLotTemp(cs.getInputLots(),ps.getParentLot(),this.getMoLine(), Env.getOrgRrn());
			UI.showInfo("��ʱ����ɹ�");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	protected void checkOnhandAdapter() {
		List<ManufactureOrderBom> moBoms = getChildrenLotBom(parentDialog.getParentLot());
		checkQtyOnhand(moBoms);
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

	private void checkQtyOnhand(List<ManufactureOrderBom> moBoms) {
		try {
			List<ManufactureOrderBom> insufficientBOMs = new ArrayList<ManufactureOrderBom>();

			if (moBoms != null && moBoms.size() > 0) {
				for (ManufactureOrderBom bom : moBoms) {
					INVManager invManager = Framework.getService(INVManager.class);
					Warehouse writeoffWarehouse = invManager
							.getWriteOffWarehouse(Env.getOrgRrn());
					List<Lot> storages = invManager.getLotStorage(writeoffWarehouse
							.getObjectRrn(), bom.getMaterialRrn());
					if (storages.size() == 0) {
						ManufactureOrderBom insuffBom = new ManufactureOrderBom();
						insuffBom = (ManufactureOrderBom) bom.clone();
						insuffBom.setQty(BigDecimal.ZERO);
						insufficientBOMs.add(insuffBom);
						continue;
					} else {
						BigDecimal qtyOnHand = BigDecimal.ZERO;
						for (Lot lot : storages) {
							qtyOnHand = qtyOnHand.add(lot.getQtyCurrent());
						}
						// �ж����εĿ�������Ƿ񹻽���
						if (qtyOnHand.compareTo(bom.getUnitQty()) < 0) {// �˴�bom��unitQty�Ǻ�̨��������ģ���ŵ��ܵ�����
							ManufactureOrderBom insuffBom = new ManufactureOrderBom();
							insuffBom = (ManufactureOrderBom) bom.clone();
							insuffBom.setQty(qtyOnHand);
							insufficientBOMs.add(insuffBom);
							continue;
						}
					}
				}
			}

			ADTable aTable = getADTableByTablename(INSUFFICIENT_BOM_TABLE);

			if (insufficientBOMs.size() > 0) {
				InsufficientBomsDialog ibd = new InsufficientBomsDialog(UI
						.getActiveShell(), aTable, insufficientBOMs);
				if (ibd.open() == Dialog.CANCEL) {
					return;
				}
			}else{
				UI.showInfo("�������Ͽ��������������");
			}
		} catch (Exception e) {
			logger.error("MoLineReceiveSection : checkQtyOnhand()",e);
		}
	}

	private ADTable getADTableByTablename(String tableName) {
		ADTable t = null;
		try {
			if (adManager == null)
				adManager = Framework.getService(ADManager.class);
			t = adManager.getADTable(0L, tableName);
			t = adManager.getADTableDeep(t.getObjectRrn());
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return t;
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

	protected void createToolItemNext(ToolBar tBar) {
		itemNext = new ToolItem(tBar, SWT.PUSH);
		itemNext.setText(Message.getString("inv.lot_create"));
		itemNext.setImage(SWTResourceCache.getImage("barcode"));
		itemNext.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				nextAdapter();
			}
		});
	}

	protected void createToolItemSave(ToolBar tBar) {
		itemSave = new ToolItem(tBar, SWT.PUSH);
		itemSave.setText(Message.getString("common.save")); // inv.next_number
		itemSave.setImage(SWTResourceCache.getImage("next"));
		itemSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				saveAdapter();
			}
		});
	}
	
	protected void createToolItemAdvanceSave(ToolBar tBar) {
		itemAdvanceSave = new ToolItem(tBar, SWT.PUSH);
		itemAdvanceSave.setText(Message.getString("wip.workcenter.advance")); // inv.next_number
		itemAdvanceSave.setImage(SWTResourceCache.getImage("approve"));
		itemAdvanceSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				advanceSave();
			}
		});
	}

	protected void createToolItemSaveExit(ToolBar tBar) {
		itemSaveExit = new ToolItem(tBar, SWT.PUSH);
		itemSaveExit.setText(Message.getString("common.save_exit"));
		itemSaveExit.setImage(SWTResourceCache.getImage("save"));
		itemSaveExit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				saveExitAdapter();
			}
		});
	}
	
	protected void createToolItemChooseIdsFromSystem(ToolBar tBar) {
//		itemChooseIdsFromSystem = new ToolItem(tBar, SWT.PUSH);
		itemChooseIdsFromSystem = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WORKCENTER_ERCEIPT_SPECIFY);
		itemChooseIdsFromSystem.setText(Message.getString("wip.mo_specifylots"));
		itemChooseIdsFromSystem.setImage(SWTResourceCache.getImage("preview"));
		itemChooseIdsFromSystem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				chooseIdsFromSystemAdapter();
			}
		});
	}

	protected void chooseIdsFromSystemAdapter() {
		Lot parentLot = ps.getParentLot();
		if(parentLot != null){
			if(!Lot.LOTTYPE_SERIAL.equals(parentLot.getLotType()) && !Lot.LOTTYPE_BATCH.equals(parentLot.getLotType())){
				UI.showWarning("�˹���ֻ������serial��batch���� ������");
				return;
			}
			int needNums = 0;
			if(Lot.LOTTYPE_SERIAL.equals(parentLot.getLotType())){
				needNums = tempLot.getQtyCurrent().intValue();//�޸���ΪsetQtyInitial(BigDecimal.ONE)����������1��BUG
			}else if(Lot.LOTTYPE_BATCH.equals(parentLot.getLotType())){
				needNums = 1;
			}
			SpecifyLotIdsFromSystemDialog slifsd = new SpecifyLotIdsFromSystemDialog(UI.getActiveShell(), this.getADTableByTablename(SPECIFY_LOT_TABLE), parentDialog.getParentLot(), this, needNums);
			if(slifsd.open() == Dialog.OK){
				List specifiedLots = slifsd.getSelectLots();
				if(currentLots!= null) currentLots.clear();
				if(specifiedLots != null){
					for(Object o : specifiedLots){
						if(o != null && o instanceof Lot){
							currentLots.add((Lot)o);
						}
					}
				}
			}
		}
	}

	protected void printAdapter() {
		try {
			ADManager adManager = Framework.getService(ADManager.class);
			ManufactureOrderLine moLine = (ManufactureOrderLine) adManager
					.getEntity(parentDialog.getMoLine());
			SeeLotDialog seeLotDialog = new SeeLotDialog(UI.getActiveShell(),
					moLine);
			if (seeLotDialog.open() == Dialog.CANCEL) {
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void nextAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			ps.createParentLotId();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("MoLineReceiveSetion : nextAdapter()", e);
		}
	}

	private boolean validate() {
		return cs.validate();
	}
	
	private boolean validate(boolean isAdvance) {
		return cs.validate(isAdvance);
	}
	
	protected void saveAdapter() {
//		saveAdapter(false);
		saveAdapter(true);
	}

	protected void advanceSave(){
		saveAdapter(true);
	}
	
	protected void saveAdapter(boolean isAdvance) {
		try {
			form.getMessageManager().removeAllMessages();

			boolean canSave = true;
			if (!ps.saveToObject()) {
				canSave = false;
			}
			String workCenterId = parentDialog.getWorkCenter().getName();
			if(Env.getOrgRrn()==139420L && ("ע�ܳ���".equals(workCenterId) || "���ܳ���".equals(workCenterId))){
				Lot parentLot = ps.getParentLot();
				if(parentLot.getMoldId()==null || "".equals(parentLot.getMoldId())){
					UI.showError("ģ�߱�Ų���Ϊ��");
					return;
				}
				if(parentLot.getEquipmentId()==null || "".equals(parentLot.getEquipmentId())){
					UI.showError("�豸��Ų���Ϊ��");
					return;
				}
			}
			if (!validate(isAdvance)) {
				canSave = false;
			}
			if (canSave) {
				//tempLotΪ��ʱ����ʱ��parentLot����Ϣ��һ�ݿ�¡
				//��¡�󣬽���������Զ����䣬��������setQtyCurrent(BigDecimal.ONE)���ı䡣
				//�����ҵ���߼����ǰ���ԭ���ķ�ʽ������QTY���ڶԻ������ȡ�������Ա����ʱ����lot�Ľ�������.parentLot
//				if(tempLot ==null){
//					this.tempLot=(Lot) ps.getParentLot().clone();
//				}
				WipManager wipManager = Framework.getService(WipManager.class);
				// ���ΪSerial���ͣ���qtyCurrent��qtyTransaction�ȸ�ΪBigDecimal.ONE�������ò�ͬ�ı��淽��
				if (Lot.LOTTYPE_SERIAL.equals(ps.getParentLot().getLotType())) {
//					ps.setQtySerialReceive(ps.getParentLot().getQtyCurrent());
					ps.setQtySerialReceive(tempLot.getQtyCurrent());
					int qty = tempLot.getQtyCurrent().intValue();
					ps.getParentLot().setQtyInitial(BigDecimal.ONE);
					ps.getParentLot().setQtyCurrent(BigDecimal.ONE);
					ps.getParentLot().setQtyTransaction(BigDecimal.ONE);

					List<String> lotIds = new ArrayList<String>();
					//��serial���͵���������������
					for (Lot cLot : getCurrentLots()) {
						lotIds.add(cLot.getLotId());
					}
					
					
					//serial���͵����ϲ������������ţ����Ǹ��ݽ��ܵ�������������Ӧ����������������
					INVManager invManager = Framework
							.getService(INVManager.class);
					
					//Ҳ�����˹�ָ����Ӧ���������� --1
					//����ѡ��ϵͳ�Զ�����  --2
					int specifiedQty = 0;
					if(currentLots != null && currentLots.size() > 0){
						specifiedQty = currentLots.size();
					}
					//���ָ����currenLots��Ϊ����ô�Ի�����ʾָ�������Ρ�
					//���ָ��Ϊ�գ���ô����ʵ�Զ����ɵ����Ρ�
					List<Lot> autoLots =new ArrayList<Lot>();
					for (int i = 0; i < qty-specifiedQty; i++) {
						String lotId = invManager.generateNextNumber(Env
								.getOrgRrn(), ps.getMaterial());
						lotIds.add(lotId);
						Lot newLot = new Lot();
						newLot.setOrgRrn(Env.getOrgRrn());
						newLot.setLotId(lotId);
						newLot.setMaterialRrn(ps.getMaterial()
								.getObjectRrn());
						newLot.setMaterialId(ps.getMaterial()
								.getMaterialId());
						newLot.setMaterialName(ps.getMaterial().getName());
						newLot.setQtyCurrent(BigDecimal.ONE);
						autoLots.add(newLot);
					}
					SpecifyLotsDialog lotsDialog = new SpecifyLotsDialog(UI.getActiveShell(),currentLots,autoLots,tempLot);
					int b =lotsDialog.open() ;	
					if(b ==Dialog.CANCEL){
						return;
					}
					wipManager.receiveMultiSerialLot(parentDialog
							.getWorkCenter(), ps.getParentLot(), lotIds, qty,
							cs.getInputLots(), Env.getUserRrn());
				} else {
					//���ΪBacth���ͣ����ָ�����β�Ϊ����ô�Ի�����ʾ����ָ��������,ԭҵ���߼���û���޸ġ�
					//���ָ������Ϊ�գ���ô��ʾ�ĵ�parentLot��ͷ������Ҳ����ϵͳĬ�ϵ����Σ�ԭҵ���߼���û���޸�.
					List<Lot> autoLots =new ArrayList<Lot>();
					Lot genLot = null;
					if(currentLots != null && currentLots.size() > 0){
						genLot = currentLots.get(0);
					}
					if(genLot == null){
						autoLots.add(tempLot);
					}
					SpecifyLotsDialog lotsDialog = new SpecifyLotsDialog(UI.getActiveShell(),currentLots,autoLots,tempLot);
					int b =lotsDialog.open() ;
					if(b ==Dialog.CANCEL){
						return;
					}
					wipManager.receiveLot(parentDialog.getWorkCenter(), ps
							.getParentLot(), cs.getInputLots(), genLot, Env
							.getUserRrn());
				}
				UI.showInfo(Message.getString("common.save_successed"));
				// ����������һ��ʱ�����½�isSaved �� isDid��Ϊfalse
				setIsSaved(false);
				isDid = false;

				this.refresh();

				// setExistBatchQty(wipManager);
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("MoLineReceiveSetion : saveAdapter()", e);
		}
	}

	protected void setExistBatchQty(WipManager wipManager) {
		// cs.setExistBatchQty(wipManager);
	}

	protected void saveExitAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			boolean canSave = true;
			if (!ps.saveToObject()) {
				canSave = false;
			}
			if (!validate()) {
				canSave = false;
			}
			if (canSave) {
				WipManager wipManager = Framework.getService(WipManager.class);
				// ���ΪSerial���ͣ���qtyCurrent��qtyTransaction�ȸ�ΪBigDecimal.ONE�������ò�ͬ�ı��淽��
				if (Lot.LOTTYPE_SERIAL.equals(ps.getParentLot().getLotType())) {
					ps.setQtySerialReceive(ps.getParentLot().getQtyCurrent());
					int qty = ps.getParentLot().getQtyCurrent().intValue();
					ps.getParentLot().setQtyInitial(BigDecimal.ONE);
					ps.getParentLot().setQtyCurrent(BigDecimal.ONE);
					ps.getParentLot().setQtyTransaction(BigDecimal.ONE);

					List<String> lotIds = new ArrayList<String>();
					if (this.getCurrentLots().size() > 0) {
						for (Lot cLot : getCurrentLots()) {
							lotIds.add(cLot.getLotId());
						}
					} else {
						INVManager invManager = Framework
								.getService(INVManager.class);
						for (int i = 0; i < qty; i++) {
							String lotId = invManager.generateNextNumber(Env
									.getOrgRrn(), ps.getMaterial());
							lotIds.add(lotId);
							Lot newLot = new Lot();
							newLot.setOrgRrn(Env.getOrgRrn());
							newLot.setLotId(lotId);
							newLot.setMaterialRrn(ps.getMaterial()
									.getObjectRrn());
							newLot.setMaterialId(ps.getMaterial()
									.getMaterialId());
							newLot.setMaterialName(ps.getMaterial().getName());
							newLot.setQtyCurrent(BigDecimal.ONE);
							currentLots.add(newLot);
						}
					}

					wipManager.receiveMultiSerialLot(parentDialog
							.getWorkCenter(), ps.getParentLot(), lotIds, qty,
							cs.getInputLots(), Env.getUserRrn());
				} else {
					wipManager.receiveLot(parentDialog.getWorkCenter(), ps
							.getParentLot(), cs.getInputLots(), Env
							.getUserRrn());
				}
				UI.showInfo(Message.getString("common.save_successed"));
				setIsSaved(true);
				parentDialog.buttonPressed(Dialog.OK);

			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("MoLineReceiveSetion : saveExitAdapter()", e);
		}
	}

	// ��������˱��涯�� �� û�н��н�������򷵻���
	protected boolean isSureExit() {
		if (isSaved() || !isDid())
			return true;
		return false;
	}

	public void setIsSaved(boolean isSaved) {
		this.isSaved = isSaved;
		// �������˱���(��isSaved = true)����isDid��Ϊfalse����ʾ��ǰ�Ĳ����Ѿ�������
		// ���½�isDid��Ϊfalse, ��ʾ�����ڿ�ʼû�н����κβ���
		if (isSaved)
			this.setDoOprationsFalse();
	}

	protected void setDoOprationsTrue() {
		if (!isDid)
			this.isDid = true;
		// ��������˲���(��isDid = true)����isSavedΪ�棬������Ϊfalse����ʾ��ǰ�ı����Ѿ���Ч
		if (isSaved()) {
			setIsSaved(false);
		}
	}

	protected void setDoOprationsFalse() {
		if (isDid)
			this.isDid = false;
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

	public ManufactureOrderLine getMoLine() {
		return this.parentDialog.getMoLine();
	}

	public Lot getParentLot() {
		return this.parentDialog.getParentLot();
	}

	public void setParentLot(Lot parentLot) {
		this.parentDialog.setParentLot(parentLot);
	}

	public List<Lot> getCurrentLots() {
//		if (currentLots.size() == 0) {
//			if (ps.getParentLot() != null) {
//				Lot lot = ps.getParentLot();
//				if (!Lot.LOTTYPE_SERIAL.equals(lot.getLotType())) {
//					currentLots.add(lot);
//				}
//			}
//		}
		return this.currentLots;
	}
	
	public WorkCenter getWorkCenter(){
		return parentDialog.getWorkCenter();
	}
	
	protected void wmsInAdapter() {

		String tableName = "WMSSubMoLine";
		ADTable invAdTable = null;
		try {
			ADManager adManager = Framework.getService(ADManager.class);
			invAdTable = adManager.getADTable(0L, tableName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		TableListManager listTableManager = new TableListManager(invAdTable);
		int style = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
		ManufactureOrderLine moLine = null;
		moLine = this.parentDialog.getMoLine();
		WmsMoLineInDialog invDialog = new WmsMoLineInDialog(listTableManager, null, null,
				style, moLine,this.parentDialog.getParentLot());
		if (invDialog.open() == IDialogConstants.OK_ID) {

		}
		invDialog.setParentLot(null);
		invDialog.setSelectMoLine(null);
		invDialog.setMoQty(null);
	}
}

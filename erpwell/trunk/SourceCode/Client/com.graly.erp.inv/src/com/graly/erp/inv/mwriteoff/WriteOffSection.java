package com.graly.erp.inv.mwriteoff;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.barcode.LotMasterSection;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.LotStorage;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.MovementTransfer;
import com.graly.erp.inv.model.MovementWriteOff;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.inv.otherin.OtherInLotSection;
import com.graly.erp.inv.out.OutQtySetupDialog;
import com.graly.erp.inv.outother.ByLotOutSection;
import com.graly.erp.inv.transfer.TrsQtySetupDialog;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADRefList;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.ComboField;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.forms.field.TextField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.StringUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wip.model.Lot;

public class WriteOffSection extends LotMasterSection {
	
	private static final Logger logger = Logger.getLogger(ByLotOutSection.class);
	
	private List<MovementLineLot> lineLots;
	
	protected HashMap<String, BigDecimal> headerLabels;
	protected TextField moField;
	protected Text txtMoId;
	protected TextField materialIdField;
	protected TextField materialNameField;
	protected ComboField wirteoffTypeField;
	protected final static String REFERENCE_NAME = "WriteoffType";
	
	public WriteOffSection(ADTable adTable, LotDialog parentDialog) {
		super(adTable, parentDialog);
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		setEnabled(true);
		section.setTextClient(tBar);
	}

	protected void createParentContent(Composite client, FormToolkit toolkit) {
		try {
			Composite comp = toolkit.createComposite(client, SWT.BORDER);
			GridLayout gl = new GridLayout(4, false);
			comp.setLayout(gl);
			comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			// 创建出库类型控件
			createContent(comp, toolkit);
			
			Label label = toolkit.createLabel(client, "", SWT.HORIZONTAL | SWT.SEPARATOR);
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	protected void createContent(Composite parent, FormToolkit toolkit) throws Exception {
		moField = new TextField("moId", true);
		moField.setLabel(Message.getString("pur.relation_mo"));
		moField.createContent(parent, toolkit);
		txtMoId = moField.getTextControl();
		txtMoId.addKeyListener(getMoKeyListener());
		
		materialIdField = new TextField("materialId", SWT.READ_ONLY);
		materialIdField.setLabel(Message.getString("pdm.material_id"));
		materialIdField.createContent(parent, toolkit);
		materialNameField = new TextField("materialName", SWT.READ_ONLY);
		materialNameField.setLabel(Message.getString("pdm.material_name"));
		materialNameField.createContent(parent, toolkit);
		
		createWriteoffTypeField(parent, toolkit);
	}

	protected void createWriteoffTypeField(Composite parent, FormToolkit toolkit) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			List<ADRefList> refList = entityManager.getADRefList(Env.getOrgRrn(), REFERENCE_NAME);
			for (ADRefList listItem : refList){
				map.put(listItem.getValue(), listItem.getKey());
			}
        } catch (Exception e) {
        	logger.error("WriteOffSection : createWriteoffTypeField()", e);
        }
        	wirteoffTypeField = createDropDownList("writeoffType", Message.getString("inv.writeoff_type") + "*", map);
        	wirteoffTypeField.createContent(parent, toolkit);
	}
	
	public ComboField createDropDownList(String id, String label, LinkedHashMap<String, String> items) {
    	ComboField fe = new ComboField(id, items, SWT.BORDER | SWT.READ_ONLY);
        fe.setLabel(label);
        return fe;
    }

	protected KeyListener getMoKeyListener() {
		try {
			return new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					txtMoId.setForeground(SWTResourceCache.getColor("Black"));
					switch (e.keyCode) {
					case SWT.CR :
						addMo();
						break;
					}
				}
			};
		} catch(Exception e) {
			logger.error("Error at LotMasterSection ：getKeyListener() ", e);
		}
		return null;
	}
	
	protected void addMo() {
		String moId = txtMoId.getText();
		try {
			materialIdField.setText("");
			materialNameField.setText("");
			if(moId != null && !"".equals(moId)) {				
				WipManager wipManager = Framework.getService(WipManager.class);
				ManufactureOrder mo = wipManager.getMoById(Env.getOrgRrn(), moId);
				if(mo == null || mo.getMaterialRrn() == null) {
					txtMoId.setForeground(SWTResourceCache.getColor("Red"));
					return;
				}
				materialIdField.setText(mo.getMaterialId());
				materialNameField.setText(mo.getMaterialName());
			}
		} catch(Exception e) {
			txtLotId.setForeground(SWTResourceCache.getColor("Red"));
			logger.error("Error at LotMasterSection ：addLot() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		} finally {
			txtMoId.selectAll();
		}
	}
	
	public void refresh() {
		if(lotManager != null && viewer != null) {
			lotManager.setInput(this.getLineLots());
			lotManager.updateView(viewer);
			createSectionDesc(section);
		}
	}
	
	// 验证入库仓库是否输入; 是否有入库的批次
	protected boolean validate() {
		setErrorMessage(null);
		
		Object value = this.moField.getValue();
		if (value == null || String.valueOf(value).trim().length() == 0) {
			setErrorMessage(String.format(Message.getString("common.ismandatory"),
					Message.getString("pur.relation_mo")));
			return false;
		}
		
		Object value2 = this.wirteoffTypeField.getValue();
		if(value2 == null || String.valueOf(value2).trim().length() == 0){
			setErrorMessage(String.format(Message.getString("common.ismandatory"),
					Message.getString("inv.writeoff_type")));
			return false;
		}
		
		if(this.getLineLots().size() == 0) {
			return false;
		}
		return true;
	}
	
	@Override
	protected boolean validLot(Lot lot) {
		if(!getLineLots().contains(lot)) {
			if(!Lot.POSITION_INSTOCK.equals(lot.getPosition()) &&
					!Lot.POSITION_WIP.equals(lot.getPosition())) {
				if(!Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
					UI.showError(String.format(Message.getString("wip.lot_not_in_stock"), lot.getLotId()));
					return false;					
				}
			}
			return true;
		} else {
			UI.showError(String.format(Message.getString("wip.lot_list_contains_lot"), lot.getLotId()));
		}
		return false;
	}
	
	@Override
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
				if(validLot(lot)) {
					// 如果l不为null，表示lot所对应的物料在lines中或与transferLine对应的物料一致
					MovementLine l = this.isContainsLot(lot);
					if(l == null) {
						return;
					}
					
					MovementLineLot lineLot = null;
					Warehouse wh = getWriteOffWarehouse();
					// Batch类型需要设置调拨数量
					if(Lot.LOTTYPE_BATCH.equals(lot.getLotType())
							|| Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
						
						if(wh == null) {
							UI.showError(Message.getString("inv.batch_must_be_select_warehouse_first"));
							return;
						}
						OutQtySetupDialog qtyDialog = new OutQtySetupDialog(UI.getActiveShell(),
								null, lot, wh);
						int openId = qtyDialog.open();
						if(openId == Dialog.OK) {
							lineLot = pareseMovementLineLot(l, qtyDialog.getInputQty(), lot);
						} else if(openId == Dialog.CANCEL) {
							return;
						}
					} else if(Lot.LOTTYPE_SERIAL.equals(lot.getLotType())) {
						LotStorage lotStorage = invManager.getLotStorage(Env.getOrgRrn(), lot.getObjectRrn(), wh.getObjectRrn(), Env.getUserRrn());
						if (lotStorage.getQtyOnhand().compareTo(BigDecimal.ZERO) <= 0) {
							UI.showError(String.format(Message.getString("wip.lot_not_in_stock"), lot.getLotId()));
							return;
						}
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
			}
		} catch(Exception e) {
			txtLotId.setForeground(SWTResourceCache.getColor("Red"));
			logger.error("Error at LotMasterSection ：addLot() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		} finally {
			txtLotId.selectAll();
		}
	}
	
	protected MovementLineLot pareseMovementLineLot(MovementLine line,
			BigDecimal outQty, Lot lot) throws Exception {
		Date now = Env.getSysDate();
		MovementLineLot lineLot = new MovementLineLot();
		lineLot.setOrgRrn(Env.getOrgRrn());
		lineLot.setIsActive(true);
		lineLot.setCreated(now);
		lineLot.setCreatedBy(Env.getUserRrn());
		lineLot.setUpdated(now);
		lineLot.setUpdatedBy(Env.getUserRrn());
	
		lineLot.setMovementLineRrn(line.getObjectRrn());
		lineLot.setLotRrn(lot.getObjectRrn());
		lineLot.setLotId(lot.getLotId());			
		
		lineLot.setMaterialRrn(lot.getMaterialRrn());
		lineLot.setMaterialId(lot.getMaterialId());
		lineLot.setMaterialName(lot.getMaterialName());
		// 将用户输入的出库数量设置到outLineLot.qtyMovement中
		lineLot.setQtyMovement(outQty);
		return lineLot;
	}


	@Override
	protected void saveAdapter() {
		try {
			if(validate()) {
				MovementWriteOff mw = createMovementWriteOff();
				WipManager wipManager = Framework.getService(WipManager.class);
				ManufactureOrder mo = wipManager.getMoById(Env.getOrgRrn(), mw.getMoId());
				if (mo == null) {
					UI.showError(Message.getString("inv.mo_is_not_exist"));
					return;
				}
				mw.setMoRrn(mo.getObjectRrn());
				if (UI.showConfirm(Message.getString("common.confirm_save"))) {
					List<MovementLine> lines = createMovementLines();
					INVManager invManager = Framework.getService(INVManager.class);
					invManager.manualWriteOff(mw, lines, Env.getUserRrn());
					UI.showInfo(Message.getString("common.save_successed"));
					setIsSaved(true);
					setEnabled(false);
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at LotTrsSection : saveAdapter() ", e);
		}
	}
	
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
	
	protected MovementWriteOff createMovementWriteOff() {
		MovementWriteOff mw = new MovementWriteOff();
		mw.setOrgRrn(Env.getOrgRrn());
		mw.setMoId(String.valueOf(this.moField.getValue()).trim());
		
		return mw;
	}
	
	protected List<MovementLine> createMovementLines() throws Exception {
		List<MovementLine> lines = new ArrayList<MovementLine>();
		
		List<Long> materialRrns = new ArrayList<Long>();
		List<MovementLineLot> lineLots = null;
		BigDecimal qtyWriteOff = BigDecimal.ZERO;
		int i = 1;
		for(MovementLineLot lineLot : getLineLots()) {
			if(materialRrns.contains(lineLot.getMaterialRrn()))
				continue;
			lineLots = new ArrayList<MovementLineLot>();
			qtyWriteOff = BigDecimal.ZERO;
			for(MovementLineLot tempLineLot : getLineLots()) {
				if(tempLineLot.getMaterialRrn().equals(lineLot.getMaterialRrn())) {
					lineLots.add(tempLineLot);
					qtyWriteOff = qtyWriteOff.add(tempLineLot.getQtyMovement());
				}
			}
			materialRrns.add(lineLot.getMaterialRrn());
			if(lineLots.size() > 0) {
				lines.add(generateLine(lineLots, qtyWriteOff, i * 10));
				i++;
			}
		}
		return lines;
	}
	
	protected MovementLine generateLine(List<MovementLineLot> lineLots,
			BigDecimal qtyWriteOff, int lineNo) throws Exception {
		MovementLine line = new MovementLine();
		line.setOrgRrn(Env.getOrgRrn());
		line.setLineStatus(MovementTransfer.STATUS_DRAFTED);
		line.setLineNo(new Long(lineNo));
		line.setQtyMovement(qtyWriteOff);
		
		MovementLineLot lineLot = lineLots.get(0);
		line.setMovementLots(lineLots);
		line.setMaterialId(lineLot.getMaterialId());
		line.setMaterialName(lineLot.getMaterialName());
		line.setMaterialRrn(lineLot.getMaterialRrn());
		// 通过物料获得库存单位
		ADManager adManager = Framework.getService(ADManager.class);
		Material mater = new Material();
		mater.setObjectRrn(lineLot.getMaterialRrn());
		mater = (Material)adManager.getEntity(mater);
		line.setUomId(mater.getInventoryUom());
		return line;
	}
	
	protected MovementLine isContainsLot(Lot lot) {
		return new MovementLine();
	}

	protected void setErrorMessage(String msg) {
		parentDialog.setErrorMessage(msg);
	}
	
	protected void setEnabled(boolean enabled) {
		this.itemSave.setEnabled(enabled);
		this.itemDelete.setEnabled(enabled);
	}
	
	protected Warehouse getWriteOffWarehouse() {
		INVManager invManager;
		try {
			invManager = Framework.getService(INVManager.class);
			return invManager.getWriteOffWarehouse(Env.getOrgRrn());
		} catch (Exception e) {
			// TODO Auto-generated catch section
			e.printStackTrace();
		}
		return null;
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
	
	private boolean contains(MovementLineLot lineLot) {
		if(lineLot == null) return true;
		for(MovementLineLot temp : this.getLineLots()) {
			if(temp.getLotId().equals(lineLot.getLotId()))
				return true;
		}
		return false;
	}
	

}

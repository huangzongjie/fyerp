package com.graly.erp.inv.generatelot;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Material;
import com.graly.erp.inv.barcode.IqcLotSection;
import com.graly.erp.inv.client.INVManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

/*
 * 批次录入Section
 */
public class InputLotSection extends IqcLotSection {
	private static final Logger logger = Logger.getLogger(InputLotSection.class);
	private InputLotDialog ild;
	private BigDecimal qty;
	private Long batchNum;
	BigDecimal batchSize = BigDecimal.ONE;

	public InputLotSection(ADTable adTable, InputLotDialog ild) {
		super(adTable, ild.getMaterial().getLotType());
		this.ild = ild;
	}
	
	// 只进行保存和打印
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemPrint(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createLotInfoComposite(Composite client, FormToolkit toolkit) {
		Composite comp = toolkit.createComposite(client, SWT.BORDER);
		comp.setLayout(new GridLayout(2, false));
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
	}
	
	protected void addLot() {
		String lotId = txtLotId.getText();
		try {
			if(ild.getMaterial() != null && ild.getConItem() != null) {
				if(lotId != null && !"".equals(lotId.trim())) {
					List<Lot> lots = this.getLots();
					if (Lot.LOTTYPE_BATCH.equals(ild.getMaterial().getLotType())) {
						lots.add(generateLot(ild.getMaterial(), batchSize, lotId));
					} else {
						lots.add(generateLot(ild.getMaterial(), BigDecimal.ONE, lotId));
					}
					this.setDoOprationsTrue();
					refresh();
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
	
	protected void initTableContent() {
		try {
			if(ild.getMaterial() != null && ild.getConItem() != null) {
				qty = ild.getConItem().getNumber() == null ? BigDecimal.ONE : ild.getConItem().getNumber();
				batchNum = ild.getConItem().getBatchNumber() == null ? 1 : ild.getConItem().getBatchNumber();
				if (Lot.LOTTYPE_BATCH.equals(ild.getMaterial().getLotType())) {
					double qtyLine = qty.doubleValue();
					int intQtyLine = qty.intValue();
					if (qtyLine == intQtyLine) {
						batchSize = qty.divideToIntegralValue(new BigDecimal(batchNum));
					} else {
						batchSize = qty.divide(new BigDecimal(batchNum), Constants.DIVIDE_SCALE, RoundingMode.FLOOR);
					}
				}
			}
			setEnabled(true);
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
        }
	}
	
	protected void saveAdapter() {
		try {
			if(validate()) {
				for(Lot lot : lots) {
					lot.setQtyInitial(lot.getQtyCurrent());
				}
				INVManager invManager = Framework.getService(INVManager.class);
				invManager.saveGenLot(Env.getOrgRrn(), ild.getMaterial(), getLots(), Env.getUserRrn());
				UI.showInfo(Message.getString("common.save_successed"));
				setEnabled(false);
				lotManager.setCanEdit(false);
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected boolean validate() {
		try {
			BigDecimal qty = ild.getConItem().getNumber() == null ? BigDecimal.ONE : ild.getConItem().getNumber();
			List<String> lotIds = new ArrayList<String>();
			String lotId;
			BigDecimal usedQty = BigDecimal.ZERO;
			for(Lot lot : getLots()) {
				lotId = lot.getLotId();
				if(lotId == null || "".equals(lotId.trim())) {
					UI.showError(Message.getString("inv.invalid_lotId"));
					return false;
				}
				if(lotIds.contains(lotId)) {
					UI.showError(String.format(Message.getString("inv.duplicate_lotId"), lotId));
					return false;
				}
				lotIds.add(lotId);
				usedQty = usedQty.add(lot.getQtyCurrent());
			}
			if(qty.compareTo(usedQty) != 0) {
				UI.showError(String.format(Message.getString("inv.lot_qty_total_is_not_equal"),
						usedQty.toString(), qty.toString()));
				return false;
			}
		} catch(Exception e) {
			logger.error("Error at LotMasterSection : barcodeAdapter " + e);
			return false;
		}
		return true;
	}
	
	// 创建一个不含lotId的Lot
	private Lot generateLot(Material material, BigDecimal qty, String lotId) {
		Date now = Env.getSysDate();
		Lot lot = new Lot();
		lot.setIsActive(true);
		lot.setCreatedBy(Env.getUserRrn());
		lot.setCreated(now);
		lot.setUpdatedBy(Env.getUserRrn());
		lot.setOrgRrn(Env.getOrgRrn());
		lot.setLotId(lotId);
		lot.setLotType(material.getLotType());
		lot.setMaterialRrn(material.getObjectRrn());
		lot.setMaterialId(material.getMaterialId());
		lot.setMaterialName(material.getName());
		lot.setQtyInitial(qty);
		lot.setQtyCurrent(qty);
		lot.setIsUsed(false);
		return lot;
	}
	
	protected void setEnabled(boolean enabled) {
		itemSave.setEnabled(enabled);
		this.itemPrint.setEnabled(itemSave.getEnabled() ? false : true);
	}
}

package com.graly.erp.inv.barcode;

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
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.lotprint.LotPrintDialog;
import com.graly.erp.inv.model.Iqc;
import com.graly.erp.inv.model.IqcLine;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class IqcLotSection extends LotMasterSection {
	private static final Logger logger = Logger.getLogger(IqcLotSection.class);
	
	private String lotType = "SERIAL";
	protected LotTableManager lotManager;
	
	protected ToolItem itemCreateBarcode;
	protected ToolItem itemAdd;
	protected ToolItem itemNexCode;
	protected ToolItem itemPrint;
	protected ToolItem itemAddLots;
	
	Iqc iqc;
	IqcLine iqcLine;

	public IqcLotSection(ADTable adTable) {
		super(adTable);
	}
	
	public IqcLotSection(ADTable adTable, String lotType) {
		super(adTable);
		this.lotType = lotType;
	}
	
	public IqcLotSection(ADBase parent, ADBase child, ADTable adTable, String lotType, LotDialog ld) {
		super(adTable, ld);
		this.iqc =(Iqc)parent;
		this.iqcLine = (IqcLine)child;
		this.lotType = lotType;
	}
	
	protected void createTableViewer(Composite client, FormToolkit toolkit) {
		lotManager = new LotTableManager(adTable, lotType);
		viewer = (TableViewer)lotManager.createViewer(client, toolkit, 400);
	}
	
	protected void createLotInfoComposite(Composite client, FormToolkit toolkit) {
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemAddLots(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemBarcode(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemAdd(tBar);
		createToolItemNextCode(tBar);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemPrint(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemBarcode(ToolBar tBar) {
		itemCreateBarcode = new ToolItem(tBar, SWT.PUSH);
		itemCreateBarcode.setText(Message.getString("inv.lot_create"));
		itemCreateBarcode.setImage(SWTResourceCache.getImage("barcode"));
		itemCreateBarcode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				barcodeAdapter();
			}
		});
	}
	
	protected void createToolItemAdd(ToolBar tBar) {
		itemAdd = new ToolItem(tBar, SWT.PUSH);
		itemAdd.setText(Message.getString("common.add"));
		itemAdd.setImage(SWTResourceCache.getImage("new"));
		itemAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				addAdapter();
			}
		});
	}
	
	protected void createToolItemAddLots(ToolBar tBar) {
		itemAddLots = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_IQCLINE_ADDLOTS);
		itemAddLots.setText(" ‰»Î≈˙¥Œ");
		itemAddLots.setImage(SWTResourceCache.getImage("barcode"));
		itemAddLots.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				attachLotsToIqc();
			}
		});
	}

	protected void createToolItemNextCode(ToolBar tBar) {
		itemNexCode = new ToolItem(tBar, SWT.PUSH);
		itemNexCode.setText(Message.getString("inv.next_number"));
		itemNexCode.setImage(SWTResourceCache.getImage("next"));
		itemNexCode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				nextCodeAdapter();
			}
		});
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
	
	protected void barcodeAdapter() {
		try {
			INVManager invManager = Framework.getService(INVManager.class);
			if(Lot.LOTTYPE_BATCH.equals(lotType)) {
				BatchNumberSetDialog bd = new BatchNumberSetDialog(UI.getActiveShell(),
						form, iqcLine.getQtyQualified().intValue());
				if(bd.open() == Dialog.OK) {
					lots = invManager.generateBatchLot(iqc, iqcLine, bd.getBatchNumber(), Env.getUserRrn());
				}
			} else if(Lot.LOTTYPE_SERIAL.equals(lotType)) {
				lots = invManager.generateSerialLot(iqc, iqcLine, Env.getUserRrn());
			}
			refresh();
			this.setDoOprationsTrue();
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at LotMasterSection : barcodeAdapter " + e);
			return;
		}
	}
	
	protected void attachLotsToIqc(){
		try {
			InputLotDialog ild = new InputLotDialog(UI.getActiveShell(), iqc, iqcLine, this);
			ild.open();
			refresh();
			this.setDoOprationsTrue();
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at LotMasterSection : barcodeAdapter " + e);
			return;
		}
	}
	
	protected void addAdapter() {
		try {
			getLots().add(getNewLot(""));
			refresh();
			this.setDoOprationsTrue();
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void nextCodeAdapter() {
		try {
			INVManager invManager = Framework.getService(INVManager.class);
			String lotId = invManager.generateNextNumber(Env.getOrgRrn(), iqcLine.getMaterial());

			getLots().add(getNewLot(lotId));
			refresh();
			this.setDoOprationsTrue();
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void saveAdapter() {
		try {
			if(validate()) {
				for(Lot lot : lots) {
					lot.setQtyInitial(lot.getQtyCurrent());
				}
				INVManager invManager = Framework.getService(INVManager.class);
				invManager.saveIqcLot(iqcLine, lots, Env.getUserRrn());
				UI.showInfo(Message.getString("common.save_successed"));
				setEnabled(false);
				lotManager.setCanEdit(false);
				this.setIsSaved(true);
//				ld.buttonPressed(IDialogConstants.CANCEL_ID);
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void printAdapter() {
		try {
			lots = (List<Lot>)viewer.getInput();
			if(lots != null && lots.size() != 0){
				LotPrintDialog printDialog = new LotPrintDialog(lots, this.selectLot);
				printDialog.open();
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	public void refresh() {
		lotManager.setInput(getLots());
		lotManager.updateView(viewer);
		createSectionDesc(section);
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
	
	protected boolean validate() {
		try {
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
			if(iqcLine.getQtyQualified().compareTo(usedQty) != 0) {
				UI.showError(Message.getString("inv.iqcline_lot_qty_different"));
				return false;
			}
		} catch(Exception e) {
			logger.error("Error at LotMasterSection : barcodeAdapter " + e);
			return false;
		}
		return true;
	}

	protected Lot getNewLot(String lotId) {
		Date now = Env.getSysDate();
		Lot lot = new Lot();
		lot.setLotId(lotId);
		lot.setIsActive(true);
		lot.setCreatedBy(Env.getUserRrn());
		lot.setCreated(now);
		lot.setUpdatedBy(Env.getUserRrn());
		
		if(iqcLine.getMaterial() != null) {
			lot.setLotType(iqcLine.getMaterial().getLotType());
			lot.setMaterialRrn(iqcLine.getMaterial().getObjectRrn());	
			lot.setMaterialId(iqcLine.getMaterial().getMaterialId());
			lot.setMaterialName(iqcLine.getMaterial().getName());
		}
		lot.setPoRrn(iqc.getPoRrn());
		lot.setPoId(iqc.getPoId());
		lot.setPoLineRrn(iqcLine.getPoLineRrn());
		lot.setReceiptRrn(iqc.getReceiptRrn());
		lot.setReceiptId(iqc.getReceiptId());
		lot.setIqcRrn(iqc.getObjectRrn());
		lot.setIqcId(iqc.getDocId());
		lot.setIqcLineRrn(iqcLine.getObjectRrn());
		lot.setQtyInitial(BigDecimal.ONE);
		lot.setQtyCurrent(BigDecimal.ONE);
		lot.setIsUsed(false);
		
		return lot;
	}
	
	protected void initTableContent() {
		super.initTableContent();
		if(getLots().size() > 0) {
			setEnabled(false);
			lotManager.setCanEdit(false);			
		} else {
			setEnabled(true);
		}
	}
	
	protected String getWhereClause() {
		if(iqcLine != null && iqcLine.getObjectRrn() != null) {
			return " iqcLineRrn = " + iqcLine.getObjectRrn() + " ";
		}
		return "";
	}
	
	protected void setEnabled(boolean enabled) {
		itemCreateBarcode.setEnabled(enabled);
		itemAdd.setEnabled(enabled);
		itemNexCode.setEnabled(enabled);
		itemDelete.setEnabled(enabled);
		itemSave.setEnabled(enabled);
		this.itemPrint.setEnabled(itemSave.getEnabled() ? false : true);
	}

	public String getLotType() {
		return lotType;
	}

	public void setLotType(String lotType) {
		this.lotType = lotType;
	}
}

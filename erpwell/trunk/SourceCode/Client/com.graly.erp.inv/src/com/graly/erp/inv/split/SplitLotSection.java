package com.graly.erp.inv.split;

import java.math.BigDecimal;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.barcode.LotMasterSection;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.lotprint.SingleLotPrintDialog;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class SplitLotSection extends LotMasterSection {
	protected ToolItem itemPrint;
	private BigDecimal splitQty;
	
	public SplitLotSection(ADTable adTable) {
		this.adTable = adTable;
	}
	
	public SplitLotSection(ADTable adTable, LotDialog parentDialog, Lot lot, BigDecimal splitQty) {
		this.adTable = adTable;
		this.parentDialog = parentDialog;
		this.lot = lot;
		this.splitQty = splitQty;
	}

	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemPrint(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createLotInfoComposite(Composite client, FormToolkit toolkit) {}
	
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
	
	protected void printAdapter() {
		try {
			List<Lot> lots = (List<Lot>)viewer.getInput();
			if(lots != null && lots.size() == 1){
				SingleLotPrintDialog lptd = new SingleLotPrintDialog(UI.getActiveShell(), lots.get(0));
				if(lptd.open() == Dialog.OK) {
					UI.showInfo(Message.getString("bas.lot_print_finished"));
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	protected void initTableContent() {
		try {
			INVManager invManager = Framework.getService(INVManager.class);
			List<Lot> newlots = invManager.splitLot(lot, splitQty, 1, Env.getUserRrn());
			getLots().addAll(newlots);
            refresh();
        } catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

}

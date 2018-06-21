package com.graly.erp.inv.out.adjust.sell;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.out.adjust.AdjustOutLineBlockDialog;
import com.graly.erp.inv.out.adjust.AdjustOutSection;
import com.graly.erp.inv.out.adjust.ByLotAdjustOutDialog;
import com.graly.erp.inv.out.adjust.ByLotAdjustOutSection;
import com.graly.erp.inv.receipt.NewReceiptDialog;
import com.graly.erp.inv.receipt.ReceiptSection;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;

public class SellAdjustSection extends AdjustOutSection{
	private static final Logger logger = Logger.getLogger(SellAdjustSection.class);
	protected ToolItem itemSellAll;
	private ADTable adTable;
	private static final String TABLE_NAME_SELL = "INVMovementOut";
	protected TableListManager listTableManager;
	int style = SWT.CHECK | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
	public SellAdjustSection(EntityTableManager tableManager) {
		super(tableManager);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void outByLotAdapter() {
		SellAdjustDialog olbd = new SellAdjustDialog(UI.getActiveShell());
		if(olbd.open() == Dialog.CANCEL) {
			MovementOut out = ((ByLotAdjustOutSection)olbd.getLotMasterSection()).getMovementOut();
			if(out != null && out.getObjectRrn() != null) {
				this.selectedOut = out;
				if(selectedOut != null && selectedOut.getObjectRrn() != null)
					refreshAdd(selectedOut);
				editAdapter();
			}
		}
	}
	
	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSellAll(tBar);
		createToolItemByLotOut(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		//createToolItemNew(tBar);
		createToolItemEditor(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemSellAll(ToolBar tBar) {
		itemSellAll = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_OOU_LOTOUT);
		itemSellAll.setText(Message.getString("inv.adjust_sell"));
		itemSellAll.setImage(SWTResourceCache.getImage("barcode"));
		itemSellAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				allSellAdapter();
			}
		});
	}
	
	protected void allSellAdapter() {
			String where = " docStatus='" + PurchaseOrder.STATUS_APPROVED +"'"+"AND docType='SOU'";
			adTable = getADTableOfSellAdjust(TABLE_NAME_SELL);
			listTableManager = new TableListManager(adTable);
			SellAdjustInfoDialog poQueryDialog = new SellAdjustInfoDialog(listTableManager, null, where, style);
			if (poQueryDialog.open() == Dialog.CANCEL) {
				refreshSection();
			}
			refresh();
		}
	
	protected ADTable getADTableOfSellAdjust(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
			logger.error("SellAdjustSection : getADTableOfSellAdjust()", e);
		}
		return null;
	}
	}


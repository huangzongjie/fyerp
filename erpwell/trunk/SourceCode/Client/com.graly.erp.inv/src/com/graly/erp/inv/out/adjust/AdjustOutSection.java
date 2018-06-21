package com.graly.erp.inv.out.adjust;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Documentation;
import com.graly.erp.inv.in.MaterialWCAndInvoiceQueryDialog;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.out.OutSection;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class AdjustOutSection extends OutSection {	
	private static final Logger logger = Logger.getLogger(AdjustOutSection.class);
	private static final String TABLE_NAME = "INVMovementAdjustOutLine";//调整单行管理
	private ADTable adTable;
	protected ToolItem itemByLotOut;
	
	public AdjustOutSection(EntityTableManager tableManager){
		super(tableManager);
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
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

	protected void newAdapter() {
		String where = " 1!=1 ";
		MovementOut out = new MovementOut();
		out.setOrgRrn(Env.getOrgRrn());
		AdjustOutLineBlockDialog olbd = new AdjustOutLineBlockDialog(UI.getActiveShell(),
				this.getTableManager().getADTable(), where, out, getADTableOfPOLine());
		if(olbd.open() == Dialog.CANCEL) {
			out = (MovementOut)olbd.getParentObject();
			if (out != null && out.getObjectRrn() != null) {
				selectedOut = out;
				refreshSection();
				refreshAdd(selectedOut);
			}
//			refreshSection();
		}
	}
	
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new MaterialWCAndInvoiceQueryDialog(UI.getActiveShell(), tableManager, this, Documentation.DOCTYPE_AOU);
			queryDialog.open();
		}
	}
	
	protected ADTable getADTableOfPOLine() {
		try {
			if(adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, TABLE_NAME);
				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			}
			return adTable;
		} catch(Exception e) {
			logger.error("AdjustOutSection : getADTableOfPOLine()", e);
		}
		return null;
	}
	
	protected void createToolItemByLotOut(ToolBar tBar) {
		itemByLotOut = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_OOU_LOTOUT);
		itemByLotOut.setText(Message.getString("inv.adjust_out"));
		itemByLotOut.setImage(SWTResourceCache.getImage("barcode"));
		itemByLotOut.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				outByLotAdapter();
			}
		});
	}
	
	protected void outByLotAdapter() {
		ByLotAdjustOutDialog olbd = new ByLotAdjustOutDialog(UI.getActiveShell());
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

	protected void editAdapter() {
		try {
			if(selectedOut != null && selectedOut.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedOut = (MovementOut)adManager.getEntity(selectedOut);
				String whereClause = ( " movementRrn = '" + selectedOut.getObjectRrn().toString() + "' ");
				AdjustOutLineBlockDialog cd = new AdjustOutLineBlockDialog(UI.getActiveShell(),
						this.getTableManager().getADTable(), whereClause, selectedOut, getADTableOfPOLine());
				if(cd.open() == Dialog.CANCEL) {
					refreshSection();
					this.refreshUpdate(selectedOut);
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at AdjustOutSection : editAdapter() " + e);
		}
	}

	@Override
	protected void createToolItemNew(ToolBar tBar) {
		itemNew = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_OOU_NEW);
		itemNew.setText(Message.getString("common.new"));
		itemNew.setImage(SWTResourceCache.getImage("new"));
		itemNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				newAdapter();
			}
		});
	}
	
	@Override
	protected void createToolItemEditor(ToolBar tBar) {
		itemEdit = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_OOU_EDIT);
		itemEdit.setText(Message.getString("pdm.editor"));
		itemEdit.setImage(SWTResourceCache.getImage("edit"));
		itemEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				editAdapter();
			}
		});
	}
	
	@Override
	protected void createToolItemDelete(ToolBar tBar) {
		itemDelete = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_OOU_DELETE);
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
	

}

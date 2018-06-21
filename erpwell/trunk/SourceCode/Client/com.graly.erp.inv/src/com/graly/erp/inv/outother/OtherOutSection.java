package com.graly.erp.inv.outother;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
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

public class OtherOutSection extends OutSection {	
	private static final Logger logger = Logger.getLogger(OtherOutSection.class);
	private static final String TABLE_NAME = "INVMovementOutOtherLine";
	private ADTable adTable;
	protected ToolItem itemByLotOut;
	protected ToolItem importStorage;
	protected static String URL = "http://192.168.0.235:81/products/import.jsp?user_name="+Env.getUserName();
	
	public OtherOutSection(EntityTableManager tableManager){
		super(tableManager);
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemImport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemByLotOut(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemNew(tBar);
		createToolItemEditor(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	private void createToolItemImport(ToolBar tBar) {
		importStorage = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_OOU_IMPORT);	
		importStorage.setText(Message.getString("oou.import"));
		importStorage.setImage(SWTResourceCache.getImage("export"));
		importStorage.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				importAdapter();
			}
		});
	}
	
	protected void importAdapter() {
		BrowserDialog bd = new BrowserDialog(UI.getActiveShell(), URL);
		bd.open();
	}
	

	protected void newAdapter() {
		String where = " 1!=1 ";
		MovementOut out = new MovementOut();
		out.setOrgRrn(Env.getOrgRrn());
		OtherOutLineBlockDialog olbd = new OtherOutLineBlockDialog(UI.getActiveShell(),
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
			queryDialog =  new MaterialWCAndInvoiceQueryDialog(UI.getActiveShell(), tableManager, this, Documentation.DOCTYPE_OOU);
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
			logger.error("RequisitionLineDialog : initAdTableOfBom()", e);
		}
		return null;
	}
	
	protected void createToolItemByLotOut(ToolBar tBar) {
		itemByLotOut = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_OOU_LOTOUT);
		itemByLotOut.setText(Message.getString("inv.by_lot_out"));
		itemByLotOut.setImage(SWTResourceCache.getImage("barcode"));
		itemByLotOut.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				outByLotAdapter();
			}
		});
	}
	
	protected void outByLotAdapter() {
		ByLotOutDialog olbd = new ByLotOutDialog(UI.getActiveShell());
		if(olbd.open() == Dialog.CANCEL) {
			MovementOut out = ((ByLotOutSection)olbd.getLotMasterSection()).getMovementOut();
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
				OtherOutLineBlockDialog cd = new OtherOutLineBlockDialog(UI.getActiveShell(),
						this.getTableManager().getADTable(), whereClause, selectedOut, getADTableOfPOLine());
				if(cd.open() == Dialog.CANCEL) {
					refreshSection();
					this.refreshUpdate(selectedOut);
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at OutSection : editAdapter() " + e);
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


class BrowserDialog extends TrayDialog{
	protected String url;
	private static int MIN_DIALOG_WIDTH = 700;
	private static int MIN_DIALOG_HEIGHT = 500;
	
	public BrowserDialog(Shell parentShell, String url) {
		super(parentShell);
		this.url = url;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Browser browser = new Browser(parent,SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
        gd.minimumWidth = MIN_DIALOG_WIDTH ;
        gd.minimumHeight = MIN_DIALOG_HEIGHT ;
        browser.setUrl(url);
        browser.setLayoutData(gd);
		browser.setUrl(url);
		return browser;
	}
	
	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
						shellSize.y));
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {}
	
}
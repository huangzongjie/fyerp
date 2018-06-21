package com.graly.erp.bj.inv.in;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Documentation;
import com.graly.erp.bj.inv.in.createfrom.po.BJCreateContext;
import com.graly.erp.bj.inv.in.createfrom.po.BJCreateDialog;
import com.graly.erp.bj.inv.in.createfrom.po.BJPoCreateWizard;
import com.graly.erp.inv.DelInvMovementAuthorityManager;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.in.MaterialWCAndInvoiceQueryDialog;
import com.graly.erp.inv.in.UnWriteOffMovemetDialog;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.pur.model.Requisition;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class BJInSection extends MasterSection {

	private static final Logger logger = Logger.getLogger(BJInSection.class);
	protected ToolItem itemEdit;
	protected ToolItem itemNew;
	protected ToolItem itemDelete;
	private ToolItem itemBatchWriteOff;

	protected TableListManager listTableManager;
	private static final String TABLE_NAME_MOVEMENTLINE = "BJINVMovementLine";
	private ADTable adTable;
	private MovementIn selectedIn;
	int style = SWT.CHECK | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
	private Menu menu;
	protected static String PAGE_CATEGORY_BJPO = "bjCreatePo";
	
	public BJInSection(EntityTableManager tableManager) {
		super(tableManager);
	}

	@Override
	protected void createViewAction(StructuredViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setSelectionRequisition(ss.getFirstElement());
				editAdapter();
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionRequisition(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemNew(tBar);
		createToolItemEditor(tBar);
//		createToolItemBatchWriteOff(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);	
		createMenu(tBar);
		section.setTextClient(tBar);
	}

	protected void createToolItemNew(final ToolBar tBar) {
		itemNew = new AuthorityToolItem(tBar, SWT.DROP_DOWN, Constants.KEY_PIN_CREATEFROM);
		itemNew.setText(Message.getString("pur.copyfrom"));
		itemNew.setImage(SWTResourceCache.getImage("copy"));
		itemNew.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == SWT.ARROW) {
					Rectangle bounds = itemNew.getBounds();
					Point point = tBar.toDisplay(bounds.x, bounds.y + bounds.height);
					menu.setLocation(point);
					menu.setVisible(true);
				}
			}
		});
	}
	
	/* 创建dropDownMenu及监听事件 */
	private void createMenu(final ToolBar toolBar) {
		menu = new Menu(UI.getActiveShell(), SWT.POP_UP);
//		MenuItem menuItemIQC = new MenuItem(menu, SWT.PUSH);
//		menuItemIQC.setText(Message.getString("inv.relationship_iqc"));
		MenuItem menuItemPO = new MenuItem(menu, SWT.PUSH);
		menuItemPO.setText(Message.getString("inv.relationship_po"));
		new MenuItem(menu, SWT.SEPARATOR);
		new MenuItem(menu, SWT.PUSH).setText(Message.getString("common.cancel"));

//		menuItemIQC.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				menuIqcAdapter();
//			}
//		});
		menuItemPO.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				menuPoAdapter();
			}
		});
	}

	protected void createToolItemEditor(ToolBar tBar) {
		itemEdit = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PIN_EDIT);
		itemEdit.setText(Message.getString("pdm.editor"));
		itemEdit.setImage(SWTResourceCache.getImage("edit"));
		itemEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				editAdapter();
			}
		});
	}

	protected void createToolItemDelete(ToolBar tBar) {
		itemDelete = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PIN_DELETE);
		itemDelete.setText(Message.getString("common.delete"));
		itemDelete.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE));
		itemDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteAdapter();
			}
		});
	}
	
	protected void createToolItemBatchWriteOff(ToolBar tBar) {
		itemBatchWriteOff = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PIN_BATCHWRITEOFF);
		itemBatchWriteOff.setText(Message.getString("inv.in_batch_write_off"));
		itemBatchWriteOff.setImage(SWTResourceCache.getImage("voice"));
		itemBatchWriteOff.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				batchWriteOffAdapter();
			}
		});
	}

	protected void batchWriteOffAdapter() {
		UnWriteOffMovemetDialog uwfDialog = new UnWriteOffMovemetDialog(UI.getActiveShell(), getADTable());
		uwfDialog.open();
	}


	protected void menuPoAdapter() {
		BJCreateContext context = new BJCreateContext();
		String CATEGORY_NEW_PO = "bjCreatePo";
//		context.setCategory(CreateContext.CATEGORY_NEW_PO);
		context.setCategory(CATEGORY_NEW_PO);
		ADTable table = context.getTable(BJCreateContext.TableName_Po);
		TableListManager listTableManager = new TableListManager(table);
		
		BJPoCreateWizard wizard = new BJPoCreateWizard(context, PAGE_CATEGORY_BJPO);
		BJCreateDialog dialog = new BJCreateDialog(UI.getActiveShell(), wizard, listTableManager);
		context.setDialog(dialog);
		int code = dialog.open();
		if (code == Dialog.OK) {
			MovementIn mi = context.getIn();
			if(mi != null && mi.getObjectRrn() != null) {
				selectedIn = mi;
				refreshSection();
				refreshAdd(selectedIn);
			}
		}
	}
	protected void deleteAdapter() {
		if (selectedIn != null) {
			try {
				boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
				if (confirmDelete) {
					if (selectedIn.getObjectRrn() != null) {
						if(DelInvMovementAuthorityManager.hasDeleteAuthority(Env.getUserRrn(),
								selectedIn.getWarehouseRrn(), selectedIn.getWarehouseId())) {
							INVManager invManager = Framework.getService(INVManager.class);
							invManager.deleteMovementIn(selectedIn, MovementIn.InType.PIN, Env.getUserRrn());
							this.refreshDelete(selectedIn);
							this.selectedIn = null;
							refreshSection();							
						}
					}
				}
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
				return;
			}
		}
	}

	protected void editAdapter() {
		try {
			if(selectedIn != null && selectedIn.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedIn = (MovementIn)adManager.getEntity(selectedIn);
				adTable = getADTableOfMovement(TABLE_NAME_MOVEMENTLINE);
				String whereClause = " movementId='" + selectedIn.getDocId().toString() + "'";
				BJInLineDialog inLineDialog = new BJInLineDialog(UI.getActiveShell(), this.getTableManager().getADTable(), whereClause, selectedIn,
						adTable, false);
				if (inLineDialog.open() == Dialog.CANCEL) {
					refreshSection();
					this.refreshUpdate(selectedIn);
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at InSection : editAdapter() " + e);
		}
	}

	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new MaterialWCAndInvoiceQueryDialog(UI.getActiveShell(), tableManager, this,Documentation.DOCTYPE_PIN, true);
			queryDialog.open();
		}
	}

	protected void refreshSection() {
//		refresh();
		try {
			if (selectedIn != null && selectedIn.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedIn = (MovementIn) adManager.getEntity(selectedIn);
				setStatusChanged(selectedIn.getDocStatus());
			} else {
				setStatusChanged("");
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			selectedIn = null;
			return;
		}
	}

	protected ADTable getADTableOfMovement(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
			logger.error("InSection : getADTableOfRequisition()", e);
		}
		return null;
	}

	private void setSelectionRequisition(Object obj) {
		if (obj instanceof MovementIn) {
			selectedIn = (MovementIn) obj;
			setStatusChanged(selectedIn.getDocStatus());
		} else {
			selectedIn = null;
			setStatusChanged("");
		}
	}

	protected void setStatusChanged(String status) {
		if (Requisition.STATUS_DRAFTED.equals(status)) {
			itemNew.setEnabled(true);
			itemEdit.setEnabled(true);
			itemDelete.setEnabled(true);
		} else if (Requisition.STATUS_CLOSED.equals(status)) {
			itemNew.setEnabled(false);
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
		} else {
			itemNew.setEnabled(true);
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
		}
	}
}

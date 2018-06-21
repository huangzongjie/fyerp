package com.graly.erp.inv.iqc;

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
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.MaterialQueryDialog;
import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Documentation;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.iqc.createfrom.CreateIqcContext;
import com.graly.erp.inv.iqc.createfrom.CreateIqcDialog;
import com.graly.erp.inv.iqc.createfrom.IqcCreateWizard;
import com.graly.erp.inv.model.Iqc;
import com.graly.erp.inv.model.IqcLine;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.otherin.OtherInLineDialog;
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
/*
 * »Îø‚ºÏ—È
 */
public class IqcSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(IqcSection.class);
	public static final String TABLE_NAME_IQCLINE = "IQCMovementLine";
	protected ToolItem itemEdit;
	protected ToolItem itemNew;
	private ToolItem itemDelete;
	protected Iqc selectedRec;
	private ADTable adTable;
	protected IqcLine selectedIn;
	protected TableListManager listTableManager;
	private static final String TABLE_NAME_Receipt = "INVReceiptProve";
	private static final String TABLE_NAME_IqcLine = "INVIqcLine";
	protected static String PAGE_CATEGORY_IQC = "newIqc";

	int style = SWT.CHECK | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

	public IqcSection(EntityTableManager tableManager) {
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
//		createToolItemNew2(tBar);
		createToolItemNew(tBar);
		createToolItemEditor(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		//Add by BruceYou 2012-03-14
		//createToolItemExport(tBar);
		
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
//	protected void createToolItemNew2(ToolBar tBar) {
//		itemNew = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_OIN_NEW );
//		itemNew.setText(Message.getString("common.new"));
//		itemNew.setImage(SWTResourceCache.getImage("new"));
//		itemNew.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent event) {
//				newAdapter2();
//			}
//		});
//	}
	
//	protected void newAdapter2() {
//		try {
//			adTable = getADTableOfRequisition(TABLE_NAME_IQCLINE);
//			listTableManager = new TableListManager(adTable);
//			MovementIn mi = new MovementIn();
//			mi.setOrgRrn(Env.getOrgRrn());
//			IqcLineDialog newInDialog = new IqcLineDialog(UI.getActiveShell(), this.getTableManager().getADTable(), " 1<>1 ",
//					 adTable);
//			if (newInDialog.open() == Dialog.CANCEL) {
//				mi = (IqcLine)newInDialog.getParentObject();
//				if (mi != null && mi.getObjectRrn() != null) {
//					selectedIn = mi;
//					refreshSection();
//					refreshAdd(selectedIn);
//				}
//			}
//		} catch (Exception e1) {
//			ExceptionHandlerManager.asyncHandleException(e1);
//			return;
//		}
//	}

	protected void createToolItemNew(ToolBar tBar) {
		itemNew = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_IQC_CREATEFROM);
		itemNew.setText(Message.getString("pur.copyfrom"));
		itemNew.setImage(SWTResourceCache.getImage("copy"));
		itemNew.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				newAdapter();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	}

	protected void createToolItemDelete(ToolBar tBar) {
		itemDelete = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_IQC_DELETE);
		itemDelete.setText(Message.getString("common.delete"));
		itemDelete.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE));
		itemDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteAdapter();
			}
		});
	}

	protected void createToolItemEditor(ToolBar tBar) {
		itemEdit = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_IQC_EDIT);
		itemEdit.setText(Message.getString("pdm.editor"));
		itemEdit.setImage(SWTResourceCache.getImage("edit"));
		itemEdit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				editAdapter();
			}
		});
	}

	protected void newAdapter() {
		
		CreateIqcContext context = new CreateIqcContext();
		context.setCategory(CreateIqcContext.CATEGORY_NEW_IQC);
		ADTable table = context.getTable(CreateIqcContext.TableName_Receipt);
		TableListManager listTableManager = new TableListManager(table);

		IqcCreateWizard wizard = new IqcCreateWizard(context, PAGE_CATEGORY_IQC);
		CreateIqcDialog dialog = new CreateIqcDialog(UI.getActiveShell(), wizard, listTableManager);
		context.setDialog(dialog);
		int code = dialog.open();
		if (code == Dialog.OK) {
			Iqc iqc = context.getIqc();
			String where = " iqcId= '" + iqc.getDocId() + "'";
			adTable = getADTableOfRequisition(TABLE_NAME_IqcLine);
			IqcLineDialog iqcLineDialog = new IqcLineDialog(UI.getActiveShell(), adTable, where, iqc);
			if(iqcLineDialog.open() == Dialog.OK){
			}
		}
		refreshSection();
	}
//	protected void newAdapter() {
//		String where = " isIqc != 'Y' " + " AND docStatus='" + Iqc.STATUS_APPROVED + "' ";
//		adTable = getADTableOfRequisition(TABLE_NAME_Receipt);
//		listTableManager = new TableListManager(adTable);
//		NewIqcDialog newIqcDialog = new NewIqcDialog(listTableManager, null, where, style);
//		if (newIqcDialog.open() == Dialog.OK) {
//			try {
//				adTable = getADTableOfRequisition(TABLE_NAME_IqcLine);
//				ADManager adManager = Framework.getService(ADManager.class);
//				Receipt receipt = newIqcDialog.getReceipt();
//				receipt = (Receipt)adManager.getEntity(receipt);
//				
//				INVManager invManager = Framework.getService(INVManager.class);
//				Iqc iqc = null;
//				iqc = invManager.createIqcFromReceipt(iqc, receipt, Env.getUserRrn());
//				where = " iqcId= '" + iqc.getDocId() + "'";
//				IqcLineDialog iqcLineDialog = new IqcLineDialog(UI.getActiveShell(), adTable, where, iqc);
//				if(iqcLineDialog.open() == Dialog.OK){
//				}
//			} catch (Exception e) {
//				ExceptionHandlerManager.asyncHandleException(e);
//				return;
//			}
//		}
//		refreshSection();
//	}

	protected void editAdapter() {
		try {
			if(selectedRec != null && selectedRec.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedRec = (Iqc)adManager.getEntity(selectedRec);
				Shell shell = Display.getCurrent().getActiveShell();
				adTable = getADTableOfRequisition(TABLE_NAME_IqcLine);
				String where = " iqcId='" + selectedRec.getDocId().toString() + "'";
				IqcLineDialog iqcLineDialog = new IqcLineDialog(shell, adTable, where, selectedRec);
				if (iqcLineDialog.open() == Dialog.CANCEL) {
					refresh();
					refreshSection();
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at IqcSection : editAdapter() " + e);
		}
	}

	protected void deleteAdapter() {
		if (selectedRec != null) {
			try {
				boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
				if (confirmDelete) {
					if (selectedRec.getObjectRrn() != null) {
						INVManager invManager = Framework.getService(INVManager.class);
						invManager.deleteIqc(selectedRec, Env.getUserRrn());
						this.selectedRec = null;
						refresh();
					}
				}
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
				return;
			}
		}
	}

	protected ADTable getADTableOfRequisition(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
			logger.error("ReceiptSection : getADTableOfRequisition()", e);
		}
		return null;
	}

	protected void refreshSection() {
		refresh();
		try {
			if (selectedRec != null && selectedRec.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedRec = (Iqc) adManager.getEntity(selectedRec);
				setStatusChanged(selectedRec.getDocStatus());
			} else {
				setStatusChanged("");
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			selectedRec = null;
			return;
		}
	}

	private void setSelectionRequisition(Object obj) {
		if (obj instanceof Iqc) {
			selectedRec = (Iqc) obj;
			setStatusChanged(selectedRec.getDocStatus());
		} else {
			selectedRec = null;
			setStatusChanged("");
		}
	}

	protected void setStatusChanged(String status) {
		if (Iqc.STATUS_DRAFTED.equals(status)) {
			itemEdit.setEnabled(true);
			itemDelete.setEnabled(true);
		} else {
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
		}
	}
	
	
	
	@Override
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new MaterialQueryDialog(UI.getActiveShell(), tableManager, this, Documentation.DOCTYPE_IQC);
			queryDialog.open();
		}
	}
}

package com.graly.erp.internalorder;

import java.util.List;

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
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.ppm.model.InternalOrder;
import com.graly.erp.pur.client.PURManager;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class InternalOrderSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(InternalOrderSection.class);

	protected ToolItem itemEdit;
	protected ToolItem itemPO;
	protected ToolItem itemPPM;
	protected ToolItem itemDelete;
	protected InternalOrder pi;
	protected TableListManager listTableManager;
	int style = SWT.CHECK | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

	public InternalOrderSection(EntityTableManager tableManager) {
		super(tableManager);
	}

	@Override
	protected void createViewAction(StructuredViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setSelectionMovementOut(ss.getFirstElement());
				editAdapter();
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionMovementOut(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
//		createToolItemDelete(tBar);
		createToolItemSearch2(tBar);
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemDelete(ToolBar tBar) {
		itemDelete = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PO_DELETE);
		itemDelete.setText(Message.getString("common.delete"));
		itemDelete.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE));
		itemDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteAdapter();
			}
		});
	}
	
	protected void deleteAdapter() {
		if (pi != null) {
			try {
				boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
				if (confirmDelete) {
					if (pi.getObjectRrn() != null) {
						ADManager adManager = Framework.getService(ADManager.class);
						adManager.deleteEntity(pi);
						this.pi = null;
						refresh();
					}
				}
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
				return;
			}
		}
	}
	protected void createToolItemSearch2(ToolBar tBar) {
		itemQuery = new ToolItem(tBar, SWT.PUSH);
		itemQuery.setText("内部订单转ERP");
		itemQuery.setImage(SWTResourceCache.getImage("export"));
		itemQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				queryAdapter2();
			}
		});
	}
	
	protected void queryAdapter2() {
		try {
			if(1==1) {
				ADManager adManager = Framework.getService(ADManager.class);
				ADTable adTable = adManager.getADTable(0L, "CANAInnerOrder");
//				pi = (InternalOrder)adManager.getEntity(pi);
//				String whereClause = ( " ioRrn = '" + pi.getObjectRrn().toString() + "' ");
				listTableManager = new TableListManager(adTable);
    			int style = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
    			| SWT.FULL_SELECTION | SWT.HIDE_SELECTION ;
				InternalOrderQueryDialog cd = new InternalOrderQueryDialog(listTableManager, null, "", style);
				if(cd.open() == Dialog.CANCEL) {
//					refreshSection();
//					this.refreshUpdate(selectedOut);
				}
				this.refresh();
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at OutSection : editAdapter() " + e);
		}
	}
	
	
	
	
	private void setSelectionMovementOut(Object obj) {
		if (obj instanceof InternalOrder) {
			pi = (InternalOrder) obj;
		} else {
			pi = null;
		}
	}


	@Override
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new EntityQueryDialog(UI.getActiveShell(), tableManager, this);
			queryDialog.open();
		}
	}
	
//	protected void createSectionDesc(List<SalesOrder> salesOrders){
//		try{ 
//			String text = Message.getString("common.totalshow");
//			long count = salesOrders.size();
//			if (count > Env.getMaxResult()) {
//				text = String.format(text, String.valueOf(count), String.valueOf(Env.getMaxResult()));
//			} else {
//				text = String.format(text, String.valueOf(count), String.valueOf(count));
//			}
//			section.setDescription("  " + text);
//		} catch (Exception e){
//			logger.error("MasterSection : createSectionDesc ", e);
//		}
//	}
//	
//	protected void createSectionDesc(Section section){
//	}
//	
	
	protected ADTable getADTableOfPOLine() {
		try {
			ADTable adTable = null;
			String TABLE_NAME = "PPMInternalOrderLine";
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = entityManager.getADTable(0L, TABLE_NAME);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch(Exception e) {
			logger.error("RequisitionLineDialog : initAdTableOfBom()", e);
		}
		return null;
	}
	
	protected void editAdapter() {
		try {
			if(pi != null && pi.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				pi = (InternalOrder)adManager.getEntity(pi);
				String whereClause = ( " ioRrn = '" + pi.getObjectRrn().toString() + "' ");
				InternalOrderLineBlockDialog cd = new InternalOrderLineBlockDialog(UI.getActiveShell(),
						this.getTableManager().getADTable(), whereClause, pi, getADTableOfPOLine());
				if(cd.open() == Dialog.CANCEL) {
					refresh();
//					this.refreshUpdate(selectedOut);
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at OutSection : editAdapter() " + e);
		}
	}
	
}

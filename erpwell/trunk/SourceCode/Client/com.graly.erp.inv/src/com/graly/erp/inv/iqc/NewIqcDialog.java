package com.graly.erp.inv.iqc;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.inv.model.Receipt;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.entitymanager.query.SingleEntityQueryDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;

public class NewIqcDialog extends SingleEntityQueryDialog implements IRefresh{
	Logger logger = Logger.getLogger(NewIqcDialog.class);
	protected String where;
	private ADTable adTable;
	protected EntityQueryDialog queryDialog;
	private String TABLE_NAME_RECEIPTLINE = "INVReceiptLine";
	private String BASE_CONDITION = "";
	protected Receipt receipt;
	int style = SWT.CHECK | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

	public NewIqcDialog() {
		super();
	}

	public NewIqcDialog(TableListManager listTableManager, IManagedForm managedForm, String whereClause, int style) {
		super(listTableManager, managedForm, whereClause, style);
		BASE_CONDITION = whereClause;
	}

	public NewIqcDialog(StructuredViewer viewer, Object object) {
		super();
		this.tableViewer = (TableViewer) viewer;
		super.object = object;
	}

	protected Point getInitialSize() {
		Point p = super.getInitialSize();
		p.x = 850;
		p.y = 550;
		return p;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("search-dialog"));
		setTitle(Message.getString("inv.newiqc"));

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());
		Label titleBarSeparator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite queryComp = new Composite(composite, SWT.NULL);
		queryComp.setLayout(new GridLayout());
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.END;
		queryComp.setLayoutData(gd);
		
		ToolBar tBar = new ToolBar(queryComp, SWT.FLAT | SWT.HORIZONTAL);
		ToolItem itemQuery = new ToolItem(tBar, SWT.PUSH);
		itemQuery.setText(Message.getString("common.search_Title"));
		itemQuery.setImage(SWTResourceCache.getImage("search"));
		itemQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				queryAdapter();
			}
		});

		Composite resultComp = new Composite(composite, SWT.NONE);
		resultComp.setLayout(new GridLayout());
		resultComp.setLayoutData(new GridData(GridData.FILL_BOTH));

		createSearchTableViewer(resultComp);
		getInitSearchResult();

		// whereClause = " docStatus='"+Iqc.STATUS_APPROVED+"'";
		createWhereClause();
		createViewAction(tableViewer);
		refresh(true);
		return composite;
	}

	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			EntityTableManager tableManager = new EntityTableManager(listTableManager.getADTable());
			queryDialog =  new EntityQueryDialog(UI.getActiveShell(), tableManager, this);
			queryDialog.open();
		}
	}

	protected void createViewAction(StructuredViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setSelectionRequisition(ss.getFirstElement());
				seeDetialsAdapter();
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

	protected void seeDetialsAdapter() {
		adTable = getADTableOfRequisition(TABLE_NAME_RECEIPTLINE);
		if (selectEntity != null) {
			receipt = (Receipt) selectEntity;
			String where = " receiptId='" + receipt.getDocId() + "'";
			listTableManager = new TableListManager(adTable);
			SeeDetialsDialog seeDetialsDialog = new SeeDetialsDialog(listTableManager, null, where, style, receipt);
			if (seeDetialsDialog.open() == Dialog.CANCEL) {
			}
		}
	}

	@Override
	protected void createSearchTableViewer(Composite parent) {
		mStyle = SWT.FULL_SELECTION | SWT.BORDER;
		listTableManager.setStyle(mStyle);
		tableViewer = (TableViewer) listTableManager.createViewer(parent, new FormToolkit(Display.getCurrent()));
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			if(selectEntity == null){
				UI.showWarning(Message.getString("inv.null"));
				return;
			}
			boolean confirmCreate = UI.showConfirm(Message.getString("inv.confirm_new_iqc"));
			if (confirmCreate) {
				if (tableViewer.getTable().getSelection().length > 0) {
					TableItem ti = tableViewer.getTable().getSelection()[0];
					selectEntity = (ADBase) ti.getData();
				}
				if (selectEntity instanceof Receipt) {
					setReceipt((Receipt) selectEntity);
				} else {
					setReceipt(null);
				}
				okPressed();
			}
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}

	protected ADTable getADTableOfRequisition(String TABLE_NAME) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = entityManager.getADTable(0L, TABLE_NAME);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
			logger.error("NewIqcDialog : getADTableOfRequisition()", e);
		}
		return null;
	}

	private void setSelectionRequisition(Object obj) {
		if (obj instanceof Receipt) {
			selectEntity = (Receipt) obj;
		} else {
			selectEntity = null;
		}
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Message.getString("common.ok"), false);
		createButton(parent, IDialogConstants.CANCEL_ID, Message.getString("common.cancel"), false);
	}

	public Receipt getReceipt() {
		return receipt;
	}

	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}

	@Override
	public String getWhereClause() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void refresh() {
		refresh(true);
	}

	@Override
	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause + "AND " + BASE_CONDITION;
		createWhereClause();
	}
}

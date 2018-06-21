package com.graly.erp.inv.in.createfrom.iqc;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.model.Iqc;
import com.graly.erp.inv.model.IqcLine;
import com.graly.erp.inv.model.Receipt;
import com.graly.erp.inv.receipt.ReceiptLineDialog;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.erp.pur.po.POLineBlockDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class IqcLineSelectSection {
	private static final Logger logger = Logger.getLogger(IqcLineSelectSection.class);
	
	private IqcLineSelectPage parentPage;
	private ADTable adTable;
	private ManagedForm form;
	private Section section;
	
	private EntityTableManager tableManager;
	private CheckboxTableViewer viewer;
	private Iqc parentIqc;
	
	private ToolItem relationShipItem;
	private Menu menu;
	private static final String TABLE_NAME_LOT = "INVLot";
	private static final String TABLE_NAME_RECEIPTLINE = "INVReceiptLine";
	private static final String TABLE_NAME_IQCLINE = "INVIqcLine";
	private static final String TABLE_NAME_POLINE = "PURPurchaseOrderLine";
	private static final String TABLE_NAME_PO = "PURPurchaseOrder";
	private String where;

	public IqcLineSelectSection(ADTable table, IqcLineSelectPage parentPage) {
		this.adTable = table;
		this.parentPage = parentPage;
	}
	
	public void createContents(ManagedForm form, Composite parent){
		createContents(form, parent, Section.DESCRIPTION | Section.TITLE_BAR);
	}

	public void createContents(ManagedForm form, Composite parent, int sectionStyle) {
		this.form = form;
		parent.setLayout(new GridLayout(1, false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		final FormToolkit toolkit = form.getToolkit();
		
		section = toolkit.createSection(parent, sectionStyle);
		section.setText(String.format(Message.getString("common.list"),I18nUtil.getI18nMessage(adTable, "label")));
		section.marginWidth = 0;
	    section.marginHeight = 0;
	    toolkit.createCompositeSeparator(section);

	    createToolBar(section);
	    
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		section.setLayout(layout);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
	    
	    Composite client = toolkit.createComposite(section);    
	    GridLayout gridLayout = new GridLayout();    
	    layout.numColumns = 1;    
	    client.setLayout(gridLayout);
	    
	    createSectionContent(client);
	    section.setClient(client);
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemRelationShip(tBar);
		
		createMenu(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemRelationShip(final ToolBar tBar) {
		relationShipItem = new ToolItem(tBar, SWT.DROP_DOWN);
		relationShipItem.setText(Message.getString("inv.relationship"));
		relationShipItem.setImage(SWTResourceCache.getImage("search"));
		relationShipItem.setToolTipText(Message.getString("inv.relationship_tip"));
		relationShipItem.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				if (event.detail == SWT.ARROW) {
					Rectangle bounds = relationShipItem.getBounds();
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
		MenuItem menuItemPO = new MenuItem(menu, SWT.PUSH);
		menuItemPO.setText(Message.getString("inv.relationship_po"));
		MenuItem menuItemReceipt = new MenuItem(menu, SWT.PUSH);
		menuItemReceipt.setText(Message.getString("inv.relationship_receipt"));
		new MenuItem(menu, SWT.SEPARATOR);
		new MenuItem(menu, SWT.PUSH).setText(Message.getString("common.cancel"));

		menuItemPO.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				menuPoAdapter();
			}
		});
		menuItemReceipt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				menuReceiptAdapter();
			}
		});
	}

	private void menuPoAdapter() {
		ADTable adTablePO = getADTableOfRequisition(TABLE_NAME_PO);
		ADTable adTablePOLine = getADTableOfRequisition(TABLE_NAME_POLINE);
		ADManager adManager;
		try {
			adManager = Framework.getService(ADManager.class);
			if (parentIqc != null) {
				if (parentIqc.getPoRrn() == null) {
					UI.showInfo(Message.getString("inv.relationship_is_null"));
					return;
				}
				where = " objectRrn='" + parentIqc.getPoRrn() + "'";

				List<PurchaseOrder> listPO = adManager.getEntityList(Env.getOrgRrn(), PurchaseOrder.class, 2, where, "");
				PurchaseOrder po = new PurchaseOrder();
				if (listPO.size() > 0) {
					po = listPO.get(0);
				}
				where = (" poRrn = '" + po.getObjectRrn().toString() + "' ");
				POLineBlockDialog cd = new POLineBlockDialog(UI.getActiveShell(), adTablePO, where, po, adTablePOLine, true);
				if (cd.open() == Dialog.CANCEL) {
				}
			}
		} catch (Exception e1) {
			return;
		}
	}

	private void menuReceiptAdapter() {
		adTable = getADTableOfRequisition(TABLE_NAME_RECEIPTLINE);
		ADManager adManager;
		try {
			adManager = Framework.getService(ADManager.class);
			if (parentIqc != null) {
				if (parentIqc.getReceiptRrn() == null) {
					UI.showInfo(Message.getString("inv.relationship_is_null"));
					return;
				}
				where = " objectRrn='" + parentIqc.getReceiptRrn() + "'";

				List<Receipt> listReceipt = adManager.getEntityList(Env.getOrgRrn(), Receipt.class, 2, where, "");
				Receipt receipt = new Receipt();
				if (listReceipt.size() > 0) {
					receipt = listReceipt.get(0);
				}

				where = " receiptId='" + receipt.getDocId().toString() + "'";
				ReceiptLineDialog receiptlineDialog = new ReceiptLineDialog(UI.getActiveShell(), adTable, where, receipt, true);
				if (receiptlineDialog.open() == Dialog.CANCEL) {
				}
			}
		} catch (Exception e1) {
			return;
		}
	}
	
	protected ADTable getADTableOfRequisition(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			ADTable t = entityManager.getADTable(0L, tableName);
			t = entityManager.getADTableDeep(t.getObjectRrn());
			return t;
		} catch (Exception e) {
			logger.error("InLineEntityBlock : getADTableOfRequisition()", e);
		}
		return null;
	}
	
	protected void createSectionContent(Composite client) {
		try {
			tableManager = new CheckEntityTableManager(adTable, this);
			tableManager.addStyle(SWT.CHECK);
			viewer = (CheckboxTableViewer)tableManager.createViewer(client, form.getToolkit());
			viewer.addCheckStateListener(getCheckStateListener());
		} catch(Exception e) {
			logger.error("IqcSelectSection : createAdObject() ");
			ExceptionHandlerManager.asyncHandleException(e);
        	return;
		}
	}

	private ICheckStateListener getCheckStateListener() {
		return new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				boolean isChecked = event.getChecked();
				if(isChecked || hasOtherChecked()) {
					parentPage.setPageComplete(true);
				} else {
					parentPage.setPageComplete(false);
				}
			}
	    };
	}
	
	private boolean hasOtherChecked() {
		Object[] os = viewer.getCheckedElements();
		if(os.length > 0) return true;
		else return false;
	}
	
	public void refresh() {
		EntityItemInput input = new EntityItemInput(adTable, getWhereClause(), null);
		viewer.setInput(input);
		tableManager.updateView(viewer);
		filtrateLines(viewer);
	}
	
	private void filtrateLines(CheckboxTableViewer v) {//过滤掉对应的POLine不是APPROVED状态的IQCLine
		for(TableItem item : v.getTable().getItems()){
			try {
				IqcLine iqcLine = (IqcLine) item.getData();
				Long poLineRrn = iqcLine.getPoLineRrn();
				PurchaseOrderLine poLine = new PurchaseOrderLine();
				poLine.setObjectRrn(poLineRrn);
				ADManager manager = Framework.getService(ADManager.class);
				poLine = (PurchaseOrderLine) manager.getEntity(poLine);
				if(!PurchaseOrderLine.LINESTATUS_APPROVED.equals(poLine.getLineStatus())){
					item.dispose();//如果iqcLine对应的poLine的状态不是Approved,则从列表中删除该条目
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setParentIqc(Iqc parentIqc) {
		this.parentIqc = parentIqc;
	}
	
	protected String getWhereClause() {
		if(parentIqc != null) {
			return " iqcRrn = " + parentIqc.getObjectRrn() + " AND lineStatus='APPROVED' ";
		}
		return null;
	}
	
	public List<IqcLine> getSelectedIqcLine() {
		List<IqcLine> lines = new ArrayList<IqcLine>();
		Object[] os = viewer.getCheckedElements();
		if(os.length != 0) {
			for(Object o : os) {
				IqcLine line = (IqcLine)o;
				lines.add(line);						
			}
		}
		return lines;
	}
	
	public void updateParentPage(boolean isChecked) {
		parentPage.setPageComplete(isChecked);
	}

}

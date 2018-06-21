package com.graly.erp.inv.in.createfrom.po;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
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
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.in.createfrom.iqc.CheckEntityTableManager;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.erp.pur.model.Requisition;
import com.graly.erp.pur.model.RequisitionLine;
import com.graly.erp.pur.request.RequisitionLineBlockDialog;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class PoLineSelectSection {
	private static final Logger logger = Logger.getLogger(PoSelectSection.class);
	
	private PoLineSelectPage parentPage;
	private ADTable adTable;
	private ManagedForm form;
	private Section section;
	
	private EntityTableManager tableManager;
	private CheckboxTableViewer viewer;
	private PurchaseOrder parentPo;
	
	private ToolItem relationShipItem;
	private Menu menu;
	private PurchaseOrderLine selectedPOline;

	public PoLineSelectSection(ADTable table, PoLineSelectPage parentPage) {
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
	    createViewAction(viewer);
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
		menuItemPO.setText(Message.getString("inv.relationship_pr"));
		new MenuItem(menu, SWT.SEPARATOR);
		new MenuItem(menu, SWT.PUSH).setText(Message.getString("common.cancel"));

		menuItemPO.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				menuPrAdapter();
			}
		});
	}
	
	private void menuPrAdapter() {
		if (selectedPOline == null) {
			UI.showWarning(Message.getString("inv.poline_select_is_null"));
			return;
		}
		try {
			if (selectedPOline != null) {
				PurchaseOrderLine poLine = (PurchaseOrderLine) selectedPOline;
				if (poLine.getRequisitionLineRrn() != null) {
					ADManager entityManager = Framework.getService(ADManager.class);
					adTable = entityManager.getADTable(0L, "PURRequisitionLine");
					adTable = entityManager.getADTableDeep(adTable.getObjectRrn());

					String where = " objectRrn = '" + poLine.getRequisitionLineRrn() + "' ";
					List<RequisitionLine> prLines = entityManager.getEntityList(Env.getOrgRrn(), RequisitionLine.class, 2, where, "");
					if (prLines.size() == 0) {
						UI.showError(Message.getString("wip.prLine_is_deleted"));
						return;
					}
					if (prLines.size() > 0) {
						RequisitionLine prLine = prLines.get(0);
						Requisition pr = new Requisition();
						pr.setObjectRrn(prLine.getRequisitionRrn());
						String whereClause = (" requisitionRrn = " + prLine.getRequisitionRrn() + " AND objectRrn = "
								+ prLine.getObjectRrn() + " ");
						RequisitionLineBlockDialog cd = new RequisitionLineBlockDialog(UI.getActiveShell(), adTable, whereClause, pr,true);
						if (cd.open() == Dialog.CANCEL) {
						}
					}
				} else {
					UI.showInfo(Message.getString("inv.relationship_is_null"));
					return;
				}
			}
		} catch (Exception e) {
			logger.error("SubMOLineSection : prViewAdapter()", e);
		}
	}

	protected ADTable getADTableOfRequisition(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
			logger.error("InLineEntityBlock : getADTableOfRequisition()", e);
		}
		return null;
	}
	
	protected void createSectionContent(Composite client) {
		try {
			tableManager = new CheckEntityTableManager(adTable,this);
			tableManager.addStyle(SWT.CHECK);
			viewer = (CheckboxTableViewer)tableManager.createViewer(client, form.getToolkit());
			viewer.addCheckStateListener(getCheckStateListener());
		} catch(Exception e) {
			logger.error("ReceiptSelectSection : createAdObject() ");
			ExceptionHandlerManager.asyncHandleException(e);
        	return;
		}
	}

	protected void createViewAction(StructuredViewer viewer) {
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

	private void setSelectionRequisition(Object obj) {
		if (obj instanceof PurchaseOrderLine) {
			selectedPOline = (PurchaseOrderLine) obj;
		} else {
			selectedPOline = null;
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
	}
	
	public void setParentPo(PurchaseOrder parentPo) {
		this.parentPo = parentPo;
	}
	
	protected String getWhereClause() {
		if(parentPo != null) {
			String where = " poRrn = " + parentPo.getObjectRrn() + " AND lineStatus = 'APPROVED' AND (qty >  qtyIn OR qtyIn IS NULL) ";
			return where;
		}
		return null;
	}
	
	public List<PurchaseOrderLine> getSelectedPoLine() {
		List<PurchaseOrderLine> lines = new ArrayList<PurchaseOrderLine>();
		Object[] os = viewer.getCheckedElements();
		if(os.length != 0) {
			for(Object o : os) {
				PurchaseOrderLine line = (PurchaseOrderLine)o;
				lines.add(line);						
			}
		}
		return lines;
	}
	
	public void updateParentPage(boolean isChecked) {
		parentPage.setPageComplete(isChecked);
	}

}

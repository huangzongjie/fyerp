package com.graly.erp.inv.rackstorage;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.RackLotStorage;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;

public class RackStorageSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(RackStorageSection.class);
	
	protected INVManager invManager;
	protected ToolItem itemTransfer;
	protected RackLotStorage selectStorage;
	
	public RackStorageSection() {
		super();
	}

	public RackStorageSection(EntityTableManager tableManager) {
		super(tableManager);
	}

	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemTransfer(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	@Override
	protected void createViewAction(StructuredViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setSelectionStorage(ss.getFirstElement());
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionStorage(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	protected void setSelectionStorage(Object firstElement) {
		this.selectStorage = (RackLotStorage) firstElement;
	}

	protected void createToolItemTransfer(ToolBar tBar) {
		itemTransfer = new ToolItem(tBar, SWT.PUSH);
		itemTransfer.setText("µ÷Õû");
		itemTransfer.setImage(SWTResourceCache.getImage("justify"));
		itemTransfer.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent event) {
				transferAdapter();
			}
		});
	}
	
	public void refresh(){
		try {
			if(invManager == null){
				invManager = Framework.getService(INVManager.class);
			}
			List list = invManager.getRackStorage(Env.getOrgRrn(), getWhereClause());
			viewer.setInput(list);		
			tableManager.updateView(viewer);
			createSectionDesc(section);
		} catch (Exception e) {
			logger.error("RackStorageSection:refresh",e);
		}
	}

	protected void transferAdapter() {
		TransferRackLotDialog trld = new TransferRackLotDialog(UI.getActiveShell(), selectStorage, invManager);
		trld.open();
		refresh();
	}

	private ADTable getTableByName(String tableName){
		return null;
	}
}

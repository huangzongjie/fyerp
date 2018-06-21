package com.graly.erp.inv.in.createfrom.po;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;

import com.graly.erp.inv.in.createfrom.iqc.EntityListTableManager;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class PoSelectSection {
	private static final Logger logger = Logger.getLogger(PoSelectSection.class);
	private static String PoWhereClause = " docStatus = '" + PurchaseOrder.STATUS_APPROVED + "' " + " AND warehouseRrn <> 151046 ";
	private PoSelectPage parentPage;
	private ADTable adTable;
	private ManagedForm form;
	private EntityListTableManager tableManager;
	private TableViewer viewer;

	public PoSelectSection(ADTable table, PoSelectPage parentPage) {
		this.adTable = table;
		this.parentPage = parentPage;
	}
	
	public void createContents(ManagedForm form, Composite parent) {
		this.form = form;
		parent.setLayout(new GridLayout(1, false));
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));
		this.createSectionContent(parent);
	}

	protected void createSectionContent(Composite client) {
		try {
			tableManager = new EntityListTableManager(adTable);
			viewer = (TableViewer)tableManager.createViewer(client, form.getToolkit());
			viewer.addSelectionChangedListener(getSelectionChangedListener());
			
			EntityItemInput input = new EntityItemInput(adTable, PoWhereClause, null);
			viewer.setInput(input);
			tableManager.updateView(viewer);
		} catch(Exception e) {
			logger.error("PoSelectSection : createSectionContent() ");
			ExceptionHandlerManager.asyncHandleException(e);
        	return;
		}
	}

	private ISelectionChangedListener getSelectionChangedListener() {
		return new ISelectionChangedListener() {
	    	public void selectionChanged(SelectionChangedEvent event) {
				try{
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					parentPage.setSelectionPo((PurchaseOrder)ss.getFirstElement());
				} catch (Exception e){
					e.printStackTrace();
				}
			}
	    };
	}

	public TableViewer getViewer() {
		return viewer;
	}

}

package com.graly.erp.wip.workcenter.schedule.purchase3;

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
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.wip.model.VWorkShopStorage;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;



public class PurRunDetailSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(PurRunDetailSection.class);
	protected ToolItem itemBarcode;
	protected VWorkShopStorage selectedWorkShopStorage;
	protected ToolItem itemFinancialOverseas;
	protected ToolItem itemFinancialOverseasDetail;
	protected PurchaseMaterialSection purMaterialSection;

	public PurRunDetailSection(EntityTableManager tableManager,PurchaseMaterialSection purMaterialSection) {
		super(tableManager);
		this.purMaterialSection = purMaterialSection;
		setWhereClause("1=1");//刚打开时显示空内容
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	
	
	@Override
	protected void createViewAction(StructuredViewer viewer){
	    viewer.addDoubleClickListener(new IDoubleClickListener() {
	    	public void doubleClick(DoubleClickEvent event) {
	    		StructuredSelection ss = (StructuredSelection) event.getSelection();
	    		setSelectionLine(ss.getFirstElement());
	    	}
	    });
	    viewer.addSelectionChangedListener(new ISelectionChangedListener(){
	    	public void selectionChanged(SelectionChangedEvent event) {
				try{
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionLine(ss.getFirstElement());
				} catch (Exception e){
					e.printStackTrace();
				}
			}
	    });
	}
	
	private void setSelectionLine(Object obj) {
		if(obj instanceof VWorkShopStorage) {
			selectedWorkShopStorage = (VWorkShopStorage)obj;
		} else {
			selectedWorkShopStorage = null;
		}
	}
	
	protected void queryAdapter() {
//		if (queryDialog != null) {
//			queryDialog.setVisible(true);
//		} else {
//			queryDialog =  new EntityQueryDialogWS(UI.getActiveShell(), tableManager, this);
//			queryDialog.open();
//		}
	}
	
	protected void refreshSection() {
		refresh();
	}
	
 
	
	 
}

package com.graly.erp.ppm.mps.delivery;

import java.util.ArrayList;
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
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.ppm.model.VMpsLineDelivery;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
public class MpsDeliverySection extends MasterSection {
	private static final Logger logger = Logger.getLogger(MpsDeliverySection.class);
	protected VMpsLineDelivery selectMpsLineDelivery;
	
	public MpsDeliverySection(EntityTableManager tableManager){
		super(tableManager);
	}
	
	@Override
	protected void createViewAction(StructuredViewer viewer){
	    viewer.addDoubleClickListener(new IDoubleClickListener() {
	    	public void doubleClick(DoubleClickEvent event) {
	    		StructuredSelection ss = (StructuredSelection) event.getSelection();
	    		setSelectionDelivery(ss.getFirstElement());
	    		barcodeAdapter();
	    	}
	    });
	    viewer.addSelectionChangedListener(new ISelectionChangedListener(){
	    	public void selectionChanged(SelectionChangedEvent event) {
				try{
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionDelivery(ss.getFirstElement());
				} catch (Exception e){
					e.printStackTrace();
				}
			}
	    });
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
 
	
	
	public void refresh(){
		try {
		List<VMpsLineDelivery> moLineDelays = new ArrayList<VMpsLineDelivery>();
			ADManager adManager;
			adManager = Framework.getService(ADManager.class);
			moLineDelays = adManager.getEntityList(Env.getOrgRrn(),
					VMpsLineDelivery.class, Integer.MAX_VALUE, "mpsId='1303'", "");
			viewer.setInput(moLineDelays);
			VMpsLineDelivery.class.getSimpleName();
			tableManager.updateView(viewer);
		} catch (Exception e) {
			e.printStackTrace();
		}

//		this.createSectionDesc(moLineDelays);
	}
	
	protected void barcodeAdapter() {
		try {
			if(selectMpsLineDelivery != null) {
				MpsLineDeliveryDialog ld = new MpsLineDeliveryDialog(UI.getActiveShell(), selectMpsLineDelivery);
				if(ld.open() == Dialog.CANCEL) {
//					refreshSection();
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at IqcSection : editAdapter() " + e);
		}
	}
	
//	
//	protected void refreshSection() {
//		refresh();
//		try {
//			if(selectedIQC != null && selectedIQC.getObjectRrn() != null) {
//				ADManager adManager = Framework.getService(ADManager.class);
//				selectedIQC = (Iqc)adManager.getEntity(selectedIQC);
//				setStatusChanged(selectedIQC.getDocStatus());
//			} else {
//				setStatusChanged("");
//			}
//		} catch (Exception e) {
//			ExceptionHandlerManager.asyncHandleException(e);
//			selectedIQC = null;
//			return;
//		}
//	}

//	protected ADTable getADTableOfIqcLine() {
//		try {
//			if(adTable == null) {
//				ADManager entityManager = Framework.getService(ADManager.class);
//				adTable = entityManager.getADTable(0L, TABLE_NAME);
//				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
//			}
//			return adTable;
//		} catch(Exception e) {
//			logger.error("IqcLineSection : getADTableOfIqcLine()", e);
//		}
//		return null;
//	}
	
	protected void setSelectionDelivery(Object obj) {
		if(obj instanceof VMpsLineDelivery) {
			selectMpsLineDelivery = (VMpsLineDelivery)obj;
		} else {
			selectMpsLineDelivery = null;
		}
	}
 
}

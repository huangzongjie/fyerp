package com.graly.erp.inv.barcode;

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
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.model.Iqc;
import com.graly.erp.inv.model.IqcLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class IqcLineSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(IqcLineSection.class);
	protected ToolItem itemBarcode;
	protected ManagedForm managedForm;
	
	protected IqcLine selectedIQCLine;
	protected Iqc iqc;
	protected IqcLineDialog bcld;
	
	public IqcLineSection(EntityTableManager tableManager){
		super(tableManager);
	}
	
	public IqcLineSection(EntityTableManager tableManager,
			Iqc iqc, ManagedForm managedForm){
		super(tableManager);
		this.iqc = iqc;
		this.managedForm = managedForm;
	}
	
	@Override
	protected void createViewAction(StructuredViewer viewer){
	    viewer.addDoubleClickListener(new IDoubleClickListener() {
	    	public void doubleClick(DoubleClickEvent event) {
	    		StructuredSelection ss = (StructuredSelection) event.getSelection();
	    		setSelectionIQCLine(ss.getFirstElement());
	    		barcodeAdapter();
	    	}
	    });
	    viewer.addSelectionChangedListener(new ISelectionChangedListener(){
	    	public void selectionChanged(SelectionChangedEvent event) {
				try{
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionIQCLine(ss.getFirstElement());
				} catch (Exception e){
					e.printStackTrace();
				}
			}
	    });
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemBarcode(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemBarcode(ToolBar tBar) {
		itemBarcode = new ToolItem(tBar, SWT.PUSH);
		itemBarcode.setText(Message.getString("inv.barcode"));
		itemBarcode.setImage(SWTResourceCache.getImage("barcode"));
		itemBarcode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				barcodeAdapter();
			}
		});
	}
	
	protected void barcodeAdapter() {
		try {
			if(selectedIQCLine != null && itemBarcode.getEnabled()) {
				if(selectedIQCLine.getMaterial() != null
						&& Lot.LOTTYPE_MATERIAL.equals(selectedIQCLine.getMaterial().getLotType())) {
					UI.showError(String.format(Message.getString("inv.material_is_not_need_generate_lot"),
							selectedIQCLine.getMaterial().getMaterialId()));
					return;
				}
				ADManager adManager = Framework.getService(ADManager.class);
				iqc = (Iqc)adManager.getEntity(iqc);
				selectedIQCLine = (IqcLine)adManager.getEntity(selectedIQCLine);
				IqcLotDialog ld = new IqcLotDialog(UI.getActiveShell(), iqc, selectedIQCLine);
				if(ld.open() == Dialog.CANCEL) {
					refreshSection();
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at IqcSection : editAdapter() " + e);
		}
	}
	
	protected void refreshSection() {
		refresh();
	}
	
	private void setSelectionIQCLine(Object obj) {
		if(obj instanceof IqcLine) {
			selectedIQCLine = (IqcLine)obj;
			setStatusChanged(selectedIQCLine);
		} else {
			selectedIQCLine = null;
			setStatusChanged(null);
		}
	}
	
	public void setParentDilog(IqcLineDialog bcld) {
		this.bcld = bcld;
	}
	
	protected void setStatusChanged(IqcLine iqcLine) {
		if(iqcLine == null) {
			itemBarcode.setEnabled(false);
		} else if(iqcLine.getQtyQualified() == null || iqcLine.getQtyQualified().doubleValue() < 1.0) {
			itemBarcode.setEnabled(false);
		} else if(iqcLine.getMaterial() != null
				&& Lot.LOTTYPE_MATERIAL.equals(iqcLine.getMaterial().getLotType())) {
			
		} else {
			itemBarcode.setEnabled(true);
		}
	}
}

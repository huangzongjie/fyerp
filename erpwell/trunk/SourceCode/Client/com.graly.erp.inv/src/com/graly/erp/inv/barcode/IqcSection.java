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
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.inv.model.Iqc;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
/**
 * 
 * @author Jim
 * 检验批次
 *
 */
public class IqcSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(IqcSection.class);
	private static final String TABLE_NAME = "INVIqcLine";
	protected ToolItem itemBarcode;
	protected Iqc selectedIQC;
	private ADTable adTable;
	
	public IqcSection(EntityTableManager tableManager){
		super(tableManager);
	}
	
	@Override
	protected void createViewAction(StructuredViewer viewer){
	    viewer.addDoubleClickListener(new IDoubleClickListener() {
	    	public void doubleClick(DoubleClickEvent event) {
	    		StructuredSelection ss = (StructuredSelection) event.getSelection();
	    		setSelectionIQC(ss.getFirstElement());
	    		barcodeAdapter();
	    	}
	    });
	    viewer.addSelectionChangedListener(new ISelectionChangedListener(){
	    	public void selectionChanged(SelectionChangedEvent event) {
				try{
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionIQC(ss.getFirstElement());
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
		createToolItemSearch(tBar);
		// Add by BruceYou 2012-03-14
		//createToolItemExport(tBar);
		
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemBarcode(ToolBar tBar) {
		itemBarcode = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_BARCODE_IQCLINE);
		itemBarcode.setText(Message.getString("inv.iqclines"));
		itemBarcode.setImage(SWTResourceCache.getImage("lines"));
		itemBarcode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				// 打开检验单对应的检验单行
				barcodeAdapter();
			}
		});
	}
	
	protected void barcodeAdapter() {
		if(selectedIQC != null && selectedIQC.getObjectRrn() != null) {
			IqcLineDialog bcld = new IqcLineDialog(UI.getActiveShell(),
					getADTableOfIqcLine(), selectedIQC);
			if(bcld.open() == Dialog.OK) {
				
			}
		}
	}
	
	public void refresh(){
		super.refresh();
		if(selectedIQC != null) {
			setStatusChanged(selectedIQC.getDocStatus());
		} else {
			setStatusChanged("");
		}
	}
	
	protected void refreshSection() {
		refresh();
		try {
			if(selectedIQC != null && selectedIQC.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedIQC = (Iqc)adManager.getEntity(selectedIQC);
				setStatusChanged(selectedIQC.getDocStatus());
			} else {
				setStatusChanged("");
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			selectedIQC = null;
			return;
		}
	}

	protected ADTable getADTableOfIqcLine() {
		try {
			if(adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, TABLE_NAME);
				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			}
			return adTable;
		} catch(Exception e) {
			logger.error("IqcLineSection : getADTableOfIqcLine()", e);
		}
		return null;
	}
	
	protected void setSelectionIQC(Object obj) {
		if(obj instanceof Iqc) {
			selectedIQC = (Iqc)obj;
			setStatusChanged(selectedIQC.getDocStatus());
		} else {
			selectedIQC = null;
			setStatusChanged("");
		}
	}
	
	protected void setStatusChanged(String status) {
		
	}
}

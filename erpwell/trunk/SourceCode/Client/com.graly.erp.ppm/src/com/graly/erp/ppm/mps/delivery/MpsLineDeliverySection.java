package com.graly.erp.ppm.mps.delivery;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.ppm.model.VMpsLineDelivery;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;

public class MpsLineDeliverySection extends MasterSection {
	private static final Logger logger = Logger.getLogger(MpsLineDeliverySection.class);
	protected ManagedForm managedForm;
	protected MpsLineDeliveryDialog bcld;
	protected VMpsLineDelivery vmpsLineDelivery;
	
	public MpsLineDeliverySection(EntityTableManager tableManager){
		super(tableManager);
	}
	
	public MpsLineDeliverySection(EntityTableManager tableManager,
			VMpsLineDelivery vmpsLineDelivery, ManagedForm managedForm){
		super(tableManager);
		this.vmpsLineDelivery = vmpsLineDelivery;
		this.managedForm = managedForm;
	}
	
	@Override
	protected void createViewAction(StructuredViewer viewer){
	    viewer.addDoubleClickListener(new IDoubleClickListener() {
	    	public void doubleClick(DoubleClickEvent event) {
	    		StructuredSelection ss = (StructuredSelection) event.getSelection();
//	    		setSelectionIQCLine(ss.getFirstElement());
	    	}
	    });
	    viewer.addSelectionChangedListener(new ISelectionChangedListener(){
	    	public void selectionChanged(SelectionChangedEvent event) {
				try{
					StructuredSelection ss = (StructuredSelection) event.getSelection();
//					setSelectionIQCLine(ss.getFirstElement());
				} catch (Exception e){
					e.printStackTrace();
				}
			}
	    });
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	protected void refreshSection() {
		refresh();
	}
	
	public void setParentDilog(MpsLineDeliveryDialog bcld) {
		this.bcld = bcld;
	}
	
}

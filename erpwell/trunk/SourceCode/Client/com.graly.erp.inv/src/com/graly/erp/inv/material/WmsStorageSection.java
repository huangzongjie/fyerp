package com.graly.erp.inv.material;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.VStorageMaterial;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class WmsStorageSection {
	private static final Logger logger = Logger.getLogger(WmsStorageSection.class);
	public static final String TABLE_NAME_PR_LINE = "WorkCenterRequisitionLine";
	private TableViewer viewer;
	
	private TableListManager tableManager;
	
	protected Section section;
	protected IManagedForm form;
	protected ADTable adTable;
	protected String materialId;
	protected String warehouseId;

	public WmsStorageSection() {}
	
	public WmsStorageSection(ADTable adTable,String materialId,String warehouseId) {
		this.adTable = adTable;
		this.materialId = materialId;
		this.warehouseId = warehouseId;
	}
	
	public void createContents(IManagedForm form, Composite parent){
		this.form = form;
		final FormToolkit toolkit = form.getToolkit();		
		section = toolkit.createSection(parent, SWT.FLAT | SWT.HORIZONTAL);
		section.setText("WMS¿â´æ");
		section.marginWidth = 3;
		section.marginHeight = 4;
	    toolkit.createCompositeSeparator(section);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		section.setLayout(layout);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));	    
	    Composite client = toolkit.createComposite(section);    
	    GridLayout gridLayout = new GridLayout();    
	    layout.numColumns = 1;    
	    client.setLayout(gridLayout);
	    createSectionContents(form, client);

	    section.setClient(client);
	}

	protected void createSectionContents(IManagedForm form, Composite parent){
		tableManager = new TableListManager(adTable);
		viewer = (TableViewer)tableManager.createViewer(parent, form.getToolkit());
		List<VStorageMaterial> vms = getWmsStorageList();
		viewer.setInput(vms);
		tableManager.updateView(viewer);
	}
	
	private List<VStorageMaterial> getWmsStorageList() {
		List<VStorageMaterial> storageMaterials = new ArrayList<VStorageMaterial>();
		try {
			INVManager invManager = Framework.getService(INVManager.class);
			storageMaterials= invManager.getWmsStorage(materialId,warehouseId);
		} catch(Exception e) {
			logger.error("Error at WorkOrderBomSection £ºgetBomList() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return storageMaterials;
	}
}

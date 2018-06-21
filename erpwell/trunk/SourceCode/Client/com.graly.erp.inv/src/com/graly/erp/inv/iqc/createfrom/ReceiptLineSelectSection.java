package com.graly.erp.inv.iqc.createfrom;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.model.IqcLine;
import com.graly.erp.inv.model.Receipt;
import com.graly.erp.inv.model.ReceiptLine;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class ReceiptLineSelectSection {
	private static final Logger logger = Logger.getLogger(ReceiptLineSelectSection.class);
	
	private ReceiptLineSelectPage parentPage;
	private ADTable adTable;
	private ManagedForm form;
	private Section section;
	
	private EntityTableManager tableManager;
	private CheckboxTableViewer viewer;
	private Receipt parentReceipt;
	

	public ReceiptLineSelectSection(ADTable table, ReceiptLineSelectPage parentPage) {
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

	protected void createSectionContent(Composite client) {
		try {
			tableManager = new CheckEntityTableManager(adTable, this);
			tableManager.addStyle(SWT.CHECK);
			viewer = (CheckboxTableViewer)tableManager.createViewer(client, form.getToolkit());
			viewer.addCheckStateListener(getCheckStateListener());
		} catch(Exception e) {
			logger.error("ReceiptSelectSection : createAdObject() ");
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
				ReceiptLine receiptLine = (ReceiptLine) item.getData();
				Long poLineRrn = receiptLine.getPoLineRrn();
				PurchaseOrderLine poLine = new PurchaseOrderLine();
				poLine.setObjectRrn(poLineRrn);
				ADManager manager = Framework.getService(ADManager.class);
				poLine = (PurchaseOrderLine) manager.getEntity(poLine);
				if(!PurchaseOrderLine.LINESTATUS_APPROVED.equals(poLine.getLineStatus())){
					item.dispose();//如果receiptLine对应的poLine的状态不是Approved,则从列表中删除该条目
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected String getWhereClause() {
		if(parentReceipt != null) {
			return " receiptRrn = " + parentReceipt.getObjectRrn() + " AND isIqc <> 'Y' AND lineStatus='APPROVED' ";
		}
		return null;
	}
	
	public List<ReceiptLine> getSelectedReceiptLines() {
		List<ReceiptLine> lines = new ArrayList<ReceiptLine>();
		Object[] os = viewer.getCheckedElements();
		if(os.length != 0) {
			for(Object o : os) {
				ReceiptLine line = (ReceiptLine)o;
				lines.add(line);						
			}
		}
		return lines;
	}
	
	public void updateParentPage(boolean isChecked) {
		parentPage.setPageComplete(isChecked);
	}

	public Receipt getParentReceipt() {
		return parentReceipt;
	}

	public void setParentReceipt(Receipt parentReceipt) {
		this.parentReceipt = parentReceipt;
	}

}

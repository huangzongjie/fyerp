package com.graly.erp.pur.po.copyfrom;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.pdm.model.MaterialOptional;
import com.graly.erp.pur.model.Requisition;
import com.graly.erp.pur.model.RequisitionLine;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class PRLineMasterForm extends Form {
	private static final Logger logger = Logger.getLogger(PRLineMasterForm.class);
	protected ADTable table;
	protected CheckboxTableViewer viewer;
	protected PRLinesDialog prld;
	
	private String whereClause;
	private static final String SUFFIX = " AND lineStatus = 'APPROVED' AND (qty > qtyOrdered OR qtyOrdered is null) ";
	protected RequisitionLine currentPRLine;
	protected Requisition pr;
	
	protected List<ADField> tableFields = new ArrayList<ADField>();
	
	public PRLineMasterForm(Composite parent, int style, Requisition pr,
			IMessageManager mmng) {
    	super(parent, style, null);
    	createForm();
    }
	
	public PRLineMasterForm(Composite parent, int style, ADTable table,
			Requisition pr, PRLinesDialog prld) {
		super(parent, style, null);
    	this.table = table;
    	this.pr = pr;
    	this.prld = prld;
    	createForm();
    }
	
	@Override
    public void createForm(){
        try {
        	String prefix = " lineStatus = 'APPROVED' AND  requisitionRrn = ";
        	whereClause = prefix + pr.getObjectRrn().toString() + SUFFIX;  // + SUFFIX
        } catch (Exception e) {
        	logger.error("OptionalForm : createForm", e);
        	ExceptionHandlerManager.asyncHandleException(e);
        }
        super.createForm();
    }
	
	@Override
	public void addFields() {
	}
	
	@Override
	protected void createContent() {
		toolkit = new FormToolkit(getDisplay());
        setLayout(new FillLayout());
        form = toolkit.createScrolledForm(this);
        
        Composite body = form.getBody();
        GridLayout layout = new GridLayout();
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        layout.marginLeft = 0;
        layout.marginRight = 0;
        layout.marginTop = 0;
        layout.marginBottom = 0;
        body.setLayout(layout);
        
        EntityTableManager tableManager = new EntityTableManager(table);
        tableManager.setStyle(SWT.CHECK | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
        viewer = (CheckboxTableViewer)tableManager.createViewer(body, toolkit);
	    viewer.getTable().addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = viewer.getTable().getSelection();
				Object obj = items[0].getData();
				if(obj instanceof RequisitionLine) {
					currentPRLine = (RequisitionLine)obj;
				} else {
					currentPRLine = null;
				}
				prld.prLineChanaged(currentPRLine);
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	    EntityItemInput input = new EntityItemInput(tableManager.getADTable(), whereClause, "");
	    viewer.setInput(input);
	    tableManager.updateView(viewer);
	}
	
	public List<RequisitionLine> getSelectedPRLines() {
		List<RequisitionLine> prLines = new ArrayList<RequisitionLine>();
		Object[] objs = viewer.getCheckedElements();
		for(Object obj : objs) {
			RequisitionLine prLine = (RequisitionLine)obj;
			prLines.add(prLine);
		}
		return prLines;
	}

	@Override
	public boolean validate() {
		return true;
	}

}

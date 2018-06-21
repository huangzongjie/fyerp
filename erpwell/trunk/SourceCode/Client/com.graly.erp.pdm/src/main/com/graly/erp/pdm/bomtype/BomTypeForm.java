package com.graly.erp.pdm.bomtype;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.model.Bom;
import com.graly.erp.pdm.model.VBomType;
import com.graly.erp.pdm.optional.OptionalForm;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class BomTypeForm extends Form {
	private static final Logger logger = Logger.getLogger(OptionalForm.class);
	protected Material material;
	protected ADTable table;
	EntityTableManager tableManager;
	protected TableViewer viewer;
	protected String whereClause;
	protected VBomType currentBomType;
	protected BomTypeDialog bomTypeDialog;
	
//	private static final String PREFIX = " materialRrn = ";
	
//	protected List<ADField> tableFields = new ArrayList<ADField>();
	
	public BomTypeForm(Composite parent, int style, Material material,
			Bom bom, IMessageManager mmng) {
    	super(parent, style, bom);
    	this.material = material;
    	createForm();
    }
	
	public BomTypeForm(Composite parent, int style, ADTable table,
			Material material,BomTypeDialog bomTypeDialog) {
		super(parent, style, null);
    	this.table = table;
    	this.material = material;
    	this.bomTypeDialog = bomTypeDialog;
    	createForm();
    }
	
	@Override
    public void createForm(){
        try {
//        	if(object != null) {
//        		whereClause = PREFIX + ((Bom)object).getChildRrn().toString() + " ";
//        	}
        } catch (Exception e) {
        	logger.error("BomTypeForm : createForm", e);
        	ExceptionHandlerManager.asyncHandleException(e);
        }
        super.createForm();
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
        
        tableManager = new EntityTableManager(table);
        viewer = (TableViewer)tableManager.createViewer(body, toolkit, 300);
	    viewer.getTable().addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = viewer.getTable().getSelection();
				Object obj = items[0].getData();
				if(obj instanceof VBomType) {
					currentBomType = (VBomType)obj;
				} else {
					currentBomType = null;
				}
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	    
	    viewer.addDoubleClickListener(new IDoubleClickListener() {
	    	public void doubleClick(DoubleClickEvent event) {
	    		bomTypeDialog.buttonPressed(IDialogConstants.OK_ID );
	    	}
	    });
	}

	public void refresh() {
		EntityItemInput input = new EntityItemInput(tableManager.getADTable(), whereClause, "");
	    viewer.setInput(input);
	    tableManager.updateView(viewer);
	}
	
	public VBomType getCurrentBomType() {
		return currentBomType;
	}

	public void setCurrentBomType(VBomType currentBomType) {
		this.currentBomType = currentBomType;
	}
	
	@Override
	public void addFields() {
	}

	@Override
	public boolean validate() {
		return false;
	}

	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}

	public String getWhereClause() {
		return whereClause;
	}
}

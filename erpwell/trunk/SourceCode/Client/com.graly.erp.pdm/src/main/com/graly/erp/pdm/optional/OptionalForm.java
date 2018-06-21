package com.graly.erp.pdm.optional;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
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
import com.graly.erp.pdm.model.MaterialOptional;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class OptionalForm extends Form {
	private static final Logger logger = Logger.getLogger(OptionalForm.class);
	private static final String PREFIX = " materialRrn = ";
	protected ADTable table;
	protected TableViewer viewer;
	protected OptionalDialog od;
	
	protected String whereClause;
	protected MaterialOptional currentOptional;
	private Long rootMaterialRrn;
	
	protected List<ADField> tableFields = new ArrayList<ADField>();
	
	public OptionalForm(Composite parent, int style, Material material,
			Bom bom, IMessageManager mmng) {
    	super(parent, style, bom);
    	createForm();
    }
	
	public OptionalForm(Composite parent, int style, ADTable table,
			Long rootMaterialRrn, Bom bom, OptionalDialog od) {
		super(parent, style, bom);
		this.rootMaterialRrn = rootMaterialRrn;
    	this.table = table;
    	this.od = od;
    	createForm();
    }
	
	@Override
    public void createForm(){
        try {
        	if(object != null) {
        		/* 找出父物料为rootMaterialRrn, 物料为childRrn的可选料列表 */
        		Bom bom = (Bom)getObject();
        		long childRrn = bom.getChildRrn();
        		if(bom.getBomTypeChildRrn() != null)
        			childRrn = bom.getBomTypeChildRrn();
        		whereClause = PREFIX + rootMaterialRrn + " AND childRrn = " + childRrn;
        	}
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
        
        EntityTableManager em = new EntityTableManager(table);
        viewer = (TableViewer)em.createViewer(body, toolkit);
	    viewer.getTable().addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = viewer.getTable().getSelection();
				Object obj = items[0].getData();
				if(obj instanceof MaterialOptional) {
					currentOptional = (MaterialOptional)obj;
				} else {
					currentOptional = null;
				}
				od.optionalChanaged(currentOptional);
			}
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
	    viewer.addDoubleClickListener(new IDoubleClickListener() {
	    	public void doubleClick(DoubleClickEvent event) {
	    		doDoubleClickListener();
	    	}
	    });
	    EntityItemInput input = new EntityItemInput(em.getADTable(), whereClause, "");
	    viewer.setInput(input);
	    em.updateView(viewer);
	}
	
	protected void doDoubleClickListener() {
		od.buttonPressed(Dialog.OK);
	}

	@Override
	public boolean validate() {
		return true;
	}

	public MaterialOptional getCurrentOptional() {
		return currentOptional;
	}

	public void setCurrentOptional(MaterialOptional currentOptional) {
		this.currentOptional = currentOptional;
	}
}

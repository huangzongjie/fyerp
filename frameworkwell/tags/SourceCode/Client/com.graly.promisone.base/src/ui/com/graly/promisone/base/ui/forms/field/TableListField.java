package com.graly.promisone.base.ui.forms.field;

import java.util.List;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.jface.viewers.TableViewer;

import com.graly.promisone.activeentity.model.ADTable;
import com.graly.promisone.base.entitymanager.views.TableEditorManager;

public class TableListField extends AbstractField {

	protected int mStyle = SWT.READ_ONLY | SWT.BORDER;
    protected TableViewer viewer;
    protected Table table;
    
    public TableListField(String id, TableViewer viewer) {
        super(id);
        this.viewer = viewer;
    }
    
    public TableListField(String id, TableViewer viewer, int style) {
        super(id);
        this.viewer = viewer;
        mStyle = style;
    }
    
	@Override
	public void createContent(Composite composite, FormToolkit toolkit) {
		int i = 0;
		String labelStr = getLabel();
        if (labelStr != null) {
        	mControls = new Control[2];
        	Label label = toolkit.createLabel(composite, labelStr);
            mControls[0] = label;
            i = 1;
        } else {
        	mControls = new Control[1];
        }
        
        Composite tableContainer = toolkit.createComposite(composite, SWT.NONE);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        tableContainer.setLayoutData(gd);
        tableContainer.setLayout(new GridLayout());
        
        table = viewer.getTable();
    	table.setParent(tableContainer);
    	Rectangle listRect = table.getBounds ();
    	gd.heightHint = table.getItemHeight () * 12;
    	table.setBounds(listRect);
    	tableContainer.setLayoutData(gd);
    	
    	List<Object> val = (List<Object>)getValue();
        if (val != null) {
        	viewer.setInput(val);
        }
        mControls[i] = tableContainer;
	}

    public Label getLabelControl() {
        Control[] ctrl = getControls();
        if(ctrl.length >  1) {
            return (Label)ctrl[0];
        } else {
            return null;
        }
    }
    
	@Override
	public void refresh() {
		List<Object> val = (List<Object>)getValue();
        if (val != null) {
        	viewer.setInput(val);
        }
	}
	
	public String getFieldType() {
		return "tablelist";
	}
}

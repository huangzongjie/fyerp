package com.graly.framework.base.ui.forms.field;

import java.util.List;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.ui.custom.TableDualListComposite;
import com.graly.framework.base.ui.custom.DualListComposite.ListContentChangedListener;

public class DualListField extends AbstractField {

	private static final Logger logger = Logger.getLogger(EntityForm.class);

	protected int mStyle = SWT.READ_ONLY | SWT.BORDER;
	protected TableDualListComposite<Object> dualList;
	protected ADTable adTable; 
	protected List<Object> inputList;
	
	public DualListField(String id, ADTable adTable, List<Object> inputList) {
        super(id);
        this.adTable = adTable;
        this.inputList = (List<Object>)inputList;
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
        
        List<Object> chosenList = (List<Object>)getValue();
        List<Object> avaliableList = getAvaliableList(inputList, chosenList);
        dualList = new TableDualListComposite(composite, SWT.NONE, adTable, avaliableList, chosenList);
        toolkit.adapt(dualList);
        toolkit.paintBordersFor(dualList);

        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        dualList.setLayoutData(gd);
        mControls[i] = dualList;
        dualList.addChosenListChangedSelectionListener(new ListContentChangedListener<Object>() {
        	public void listContentChanged(List<Object> list) { 
            	setValue(list);
            }
        });
        
	}
	
	@Override
	public void refresh() {
		List<Object> chosenList = (List<Object>)getValue();
        List<Object> avaliableList = getAvaliableList(inputList, chosenList);
        if (chosenList == null) {
        	chosenList = new ArrayList<Object>();
        }
        dualList.refresh(avaliableList, chosenList);
	}
	
	public String getFieldType() {
		return "duallist";
	}
	
	private List<Object> getAvaliableList(List<Object> inputList, List<Object> chosenList){
		List<Object> avaliableList = new ArrayList<Object>();
		if (inputList == null){
			return avaliableList;
		}
		avaliableList.addAll(inputList);
		//java.util.Collections.copy(avaliableList, inputList);
		if (chosenList == null){
			return avaliableList;
		}
		avaliableList.removeAll(chosenList);
		return avaliableList;
	}
	
	@Override
	public void enableChanged(boolean enabled) {
		dualList.setEnabled(enabled);
		super.enableChanged(enabled);
    }
}

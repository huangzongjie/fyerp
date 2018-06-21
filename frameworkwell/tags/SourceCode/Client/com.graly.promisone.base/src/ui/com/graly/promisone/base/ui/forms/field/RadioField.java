package com.graly.promisone.base.ui.forms.field;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class RadioField extends AbstractField {
	
    protected int mStyle = SWT.NONE;
    protected LinkedHashMap<String, String> mItems;
    protected Button[] mButtons;
    protected int mCols = 1;
    
    public RadioField(String id, LinkedHashMap<String, String> items) {
        super(id);
        mItems = items;
    }

    public RadioField(String id, LinkedHashMap<String, String> items, int style) {
        super(id);
        mStyle = style;
        mItems = items;
    }
    
	@Override
	public void createContent(Composite composite, FormToolkit toolkit) {
		String labelStr = getLabel();
		int i = 0;
        if (labelStr != null) {
        	mControls = new Control[2];
        	Label label = toolkit.createLabel(composite, labelStr);
            mControls[0] = label;
            i = 1;
        } else {
        	mControls = new Control[1];
        }
		
		Composite parent = toolkit.createComposite(composite);
        GridLayout layout = new GridLayout();
        parent.setLayout(layout);
		toolkit.adapt(parent);
        toolkit.paintBordersFor(parent);
        
        GridData gd;
		if (mItems.size() > 0) {
			layout.numColumns = mItems.size();
            mButtons = new Button[mItems.size()];
            int j = 0;
            for (Entry<String, String> entry : mItems.entrySet()) {
                mButtons[j] = toolkit.createButton(parent, entry.getValue(), SWT.RADIO | SWT.LEFT);
                mButtons[j].setData(entry.getKey());
                gd = new GridData();
                gd.horizontalAlignment = SWT.FILL;
                gd.grabExcessHorizontalSpace = true;
                mButtons[j].setLayoutData(gd);
                mButtons[j].addListener (SWT.Selection, new Listener () {
                    public void handleEvent (Event event) {
                        Button widget = (Button)event.widget;
                        if (widget.getSelection()) {
                        	setValue(widget.getData());
                        }
                    }
                });
                j++;
            }
        }
		String b = (String)getValue();
        if (b != null) {
        	for (Button button : mButtons){
        		if (b.equals(button.getData())){
        			button.setSelection(true);
        		}
        	}
        }
        mControls[i] = parent;
        
	}
	
	public Composite getGroupControl() {
		Control[] ctrl = getControls();
        if(ctrl.length >  1) {
            return (Composite)ctrl[1];
        } else {
            return (Composite)ctrl[0];
        }
    }
	
	public void setSelectionIndex(int index) {
        mButtons[index].setSelection(true);
    }

    public int getSelectionIndex() {
        for (int i=0; i<mButtons.length; i++) {
            if (mButtons[i].getSelection()) {
                return i;
            }
        }
        return -1;
    }

    public int getStyle() {
        return mStyle;
    }
    
    public void setColumns(int cols) {
        mCols = cols;
    }
    
	@Override
	public void refresh() {
		for (int i = 0; i < mButtons.length; i++) {
			mButtons[i].setSelection(false);
            if (((String)mButtons[i].getData()).equalsIgnoreCase((String) getValue())) {
            	mButtons[i].setSelection(true);
            }
        }
	}
	
	public String getFieldType() {
		return "radio";
	}
	
	@Override
	public void enableChanged(boolean enabled) {
		for (Button button: mButtons){
			button.setEnabled(enabled);
		}
		super.enableChanged(enabled);
    }
}

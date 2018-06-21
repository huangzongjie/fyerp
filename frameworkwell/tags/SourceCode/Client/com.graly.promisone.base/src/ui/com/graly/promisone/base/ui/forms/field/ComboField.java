package com.graly.promisone.base.ui.forms.field;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.promisone.activeentity.model.ADField;

public class ComboField extends AbstractField {

	protected int mStyle = SWT.BORDER;
    protected LinkedHashMap<String, String> mItems;
    CCombo combo;
    
    public ComboField(String id, LinkedHashMap<String, String> items) {
        super(id);
        mItems = items;
    }
    
    public ComboField(String id, LinkedHashMap<String, String> items, int style) {
        super(id);
        mStyle = style;
        mItems = items;
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
        combo = new CCombo(composite, mStyle);
        toolkit.adapt(combo);
        toolkit.paintBordersFor(combo);
        
        if (mItems != null){
        	combo.setItems(mItems.values().toArray(new String[]{}));
        }
        
        String val = (String)getValue();
        if (val != null) {
            String text = (String)combo.getData(val);
            if (text != null) {
            	combo.setText(text);
            } else {
            	combo.setText("");
            }
        }
        
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        combo.setLayoutData(gd);
        if (getToolTipText() != null) {
            combo.setToolTipText(getToolTipText());
        }
        mControls[i] = combo;
        combo.addSelectionListener(new SelectionListener() {
        	public void widgetDefaultSelected(SelectionEvent e) {
                setSelectedValue(
                        ((CCombo)e.widget).getSelectionIndex());
            }
        
            public void widgetSelected(SelectionEvent e) {
                setSelectedValue(
                        ((CCombo)e.widget).getSelectionIndex());
            } 
        
            private void setSelectedValue(int index) {
            	int i = 0; 
            	for (Entry<String, String> entry : mItems.entrySet()){            		
            		if (i == index){
            			setValue(entry.getKey());
            			break;
            		}
            		i++;
            	}
            }
        });
        
        combo.addModifyListener(new ModifyListener(){
        	public void modifyText(ModifyEvent e) {
        		setValue(((CCombo)e.widget).getText());
        	}
        });
	}

	public CCombo getComboControl() {
        Control[] ctrl = getControls();
        if(ctrl.length >  1) {
            return (CCombo)ctrl[1];
        } else {
            return (CCombo)ctrl[0];
        }
    }

    public Label getLabelControl() {
        Control[] ctrl = getControls();
        if(ctrl.length >  1) {
            return (Label)ctrl[0];
        } else {
            return null;
        }
    }
    
    public void setSelectionIndex(int index) {
        getComboControl().select(index);
    }

    public int getSelectionIndex() {
        return getComboControl().getSelectionIndex();
    }
    
	@Override
	public void refresh() {
		String val = (String)getValue();
		String text = "";
        if (val != null) {
        	text = (String)mItems.get(val);
        }
        if (text != null) {
        	getComboControl().setText(text);
        } else {
        	getComboControl().setText("");
        }
        setValue(val);
	}
	
	public String getFieldType() {
		return "combo";
	}
	
	@Override
	public void enableChanged(boolean enabled) {
		combo.setEnabled(enabled);
		super.enableChanged(enabled);
    }

	public LinkedHashMap<String, String> getMItems() {
		return mItems;
	}

	public void setMItems(LinkedHashMap<String, String> items) {
		mItems = items;
		if(mItems != null && combo != null)
			combo.setItems(mItems.values().toArray(new String[]{}));
	}
}

package com.graly.framework.base.ui.forms.field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.base.ui.custom.XUrlComposite;
import com.graly.framework.base.ui.util.RCPUtil;

public class UrlField extends AbstractField {
	
	protected XUrlComposite xUrl;
	protected int mStyle = SWT.BORDER;

    public UrlField(String id, int style) {
        super(id);
        mStyle = mStyle | style;
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
        xUrl = new XUrlComposite(composite, mStyle);
        toolkit.adapt(xUrl);
        toolkit.paintBordersFor(xUrl);
            
        String val = (String)getValue();
        if (val != null) {
        	xUrl.setText(val);
        } else {
        	xUrl.setText("");
		}
        
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        xUrl.setLayoutData(gd);
        if (getToolTipText() != null) {
        	xUrl.setToolTipText(getToolTipText());
        }
        mControls[i] = xUrl;
        xUrl.addArrowSelectionListener(getSelectionListener());
        xUrl.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
            	setValue(xUrl.getText());
            }
        });
	}

    protected SelectionListener getSelectionListener() {
    	return new SelectionAdapter() {
    		public void widgetSelected(SelectionEvent e) {
    			String urlString = xUrl.getText();
    			if (urlString != null && urlString.length() > 0) {
    				RCPUtil.startBrowser(urlString);
    			}
    		}
    	};    		
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
		String text = (String)getValue();
		if(text != null) {
			xUrl.setText(text);		
		} else {
			xUrl.setText("");
		}
	}
	
	@Override
    public void setEnabled(boolean enabled) {
    	super.setEnabled(enabled);
    	xUrl.setEnabled(enabled);
    }
    
	public String getFieldType() {
		return "url";
	}
	
}
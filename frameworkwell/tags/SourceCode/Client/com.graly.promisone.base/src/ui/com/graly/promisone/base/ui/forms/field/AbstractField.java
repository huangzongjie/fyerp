package com.graly.promisone.base.ui.forms.field;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.promisone.activeentity.model.ADField;
import com.graly.promisone.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.promisone.base.ui.forms.field.listener.IEnableChangeListener;

public abstract class AbstractField implements IField, IEnableChangeListener {
	
	protected Object adObject; 
	protected Control[] mControls;
	protected String mId;
    protected String label;
    protected Object value;
    protected String mToolTip;
    protected boolean mEnabled = true;
    protected List<IValueChangeListener> valueChangeListeners = new LinkedList<IValueChangeListener>();
    protected IEnableChangeListener enableChangeListener;
    protected String isSampleLine;


    public AbstractField(String id) {
        mId = id;
        enableChangeListener = this;
    }
    
    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }
    
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
    	if (getADField() != null && ((ADField)getADField()).getIsReadonly()){
    		this.setEnabled(false);
    	}
    	Object oldValue = this.value;
    	if (oldValue == null && value == null){
    		return;
    	} else if (oldValue != null){
    		if (!oldValue.equals(value)){
    			notifyValueChangeListeners(this, value);
    		}
    	} else if (value != null){
    		notifyValueChangeListeners(this, value);
    	}
        this.value = value;
    }
    
    public void setToolTipText(String text){
        mToolTip = text;
    }

    public String getToolTipText() {
        return mToolTip;
    }
    
    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
    	if (mEnabled != enabled){
    		mEnabled = enabled;
    		enableChangeListener.enableChanged(enabled);
    	}
    }
    
    public static FontMetrics getFontMetric(Control ctrl) {
        FontMetrics fm;
        GC gc = new GC(ctrl);
        fm = gc.getFontMetrics();
        gc.dispose();
        return fm;
    }
    
    public Control[] getControls() {
        return mControls;
    }
    
    public int getColumnsCount() {
        return mControls.length;
    }
    
    public void addValueChangeListener(IValueChangeListener listener) {
    	synchronized (valueChangeListeners) {
			valueChangeListeners.add(listener);
		}
	}
    
    public void removeValueChangeListener(IValueChangeListener listener) {
    	synchronized (valueChangeListeners) {
			valueChangeListeners.remove(listener);
		}
	}
    
    protected void notifyValueChangeListeners(Object sender, Object newValue){
		synchronized (valueChangeListeners) {
			for (IValueChangeListener listener : valueChangeListeners) {
				try {
					listener.valueChanged(sender, newValue);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
	}
    
    public void setADField(Object adObject) {
    	this.adObject = adObject;
    }
    
    public Object getADField() {
    	return adObject;
    }
    
    public Object getField() {
    	return null;
    }
    
    public void enableChanged(boolean enabled) {
    	if (getADField() != null && ((ADField)getADField()).getIsReadonly()){
    		this.setEnabled(false);
    	}
    }
    
    public String getFieldType() {
    	return "";
    }
    
}

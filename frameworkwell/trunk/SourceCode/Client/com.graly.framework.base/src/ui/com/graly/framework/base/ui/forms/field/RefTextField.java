package com.graly.framework.base.ui.forms.field;

import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.PropertyUtil;

public class RefTextField extends AbstractField implements IValueChangeListener {

	private static final Logger logger = Logger.getLogger(RefTextField.class);
	
	private int mStyle = SWT.BORDER;
	private int mRows = 4;
	private int mCols = 32;
	private int mLimit = 32;
	Text text;
	
	public RefTextField(String id) {
		this(id, SWT.BORDER);
	}

	public RefTextField(String id, int style) {
		super(id);
		this.mStyle &= style;
	}
	
	public void createContent(Composite composite, FormToolkit toolkit) {
		String labelStr = getLabel();
		if (labelStr != null) {
			Label label = toolkit.createLabel(composite, labelStr);
			text = toolkit.createText(composite, (String)getValue(), mStyle);
			mControls = new Control[2];
			mControls[0] = label;
			mControls[1] = text;
            if (getToolTipText() != null) {
                text.setToolTipText(getToolTipText());
            }
		} else {
            text = toolkit.createText(composite, (String)getValue(), mStyle);
            mControls = new Control[1];
            mControls[0] = text;
            if (getToolTipText() != null) {
                text.setToolTipText(getToolTipText());
            }
        }
		text.setTextLimit(mLimit);
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        text.setLayoutData(gd);
        text.setBackground(text.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        FontMetrics fm = getFontMetric(text);

        if ((mStyle & SWT.MULTI) == SWT.MULTI) {
            if (mRows > 0) {
                gd.heightHint = Dialog.convertHeightInCharsToPixels(fm, mRows);
            }
        }
        if (mCols > 0) {
            gd.widthHint = Dialog.convertWidthInCharsToPixels(fm, mCols);
        }
	}
	
	public Text getTextControl() {
        Control[] ctrls = getControls();
        return (Text)ctrls[ctrls.length-1];
    }

    public Label getLabelControl() {
        Control[] ctrls = getControls();
        return ctrls.length > 1 ? (Label)ctrls[0] : null;
    }
    
	public void setReadOnly(boolean readOnly) {
        if (readOnly) {
            mStyle |= SWT.READ_ONLY;
        } else {
            mStyle &= ~SWT.READ_ONLY;
        }
    }

    public void setMultiLine(boolean isMultiLine) {
        if (isMultiLine) {
            mStyle |= SWT.MULTI | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL;
        } else {
            mStyle &= ~(SWT.MULTI | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL);
        }
    }

    public void setWrap(boolean wrapText) {
        if (wrapText) {
            mStyle |= SWT.WRAP;
        } else {
            mStyle &= ~SWT.WRAP;
        }
    }

    public int getStyle() {
        return mStyle;
    }

    public void setStyle(int style) {
        mStyle = style;
    }

    public void appendStyle(int style) {
        mStyle |= style;
    }

    public void setText(String text) {
    	if (text == null)
    		text = "";
    	if (getTextControl() != null) {
    		getTextControl().setText(text);
    	}
    }

    public void setHeight(int rows) {
        mRows = rows;
    }

    public void setWidth(int cols) {
        mCols = cols;
    }
    
    public void setLength(int limit) {
    	mLimit = limit;
    }
	
    @Override
	public void refresh() {
    }
    
	public void valueChanged(Object sender, Object newValue){
		try {
			if (sender instanceof RefTableField) {
				RefTableField refField = (RefTableField)sender;
				newValue = refField.getData();
			}
			if (newValue != null) {
				String refName = ((ADField)this.getADField()).getReferenceRule();
				String firstName = refName.substring(0, refName.indexOf("."));
				String lastName = refName.substring(firstName.length() + 1, refName.length());
				String text = (String)PropertyUtil.getPropertyForString(newValue, lastName);
				this.setValue(text);
				this.setText(text);
			} else {
				this.setValue("");
				this.setText("");
			}
		} catch (Exception e){
			logger.error("RefTextField : valueChanged", e);
		}
	}
	
	public String getFieldType() {
		return "reftext";
	}
}

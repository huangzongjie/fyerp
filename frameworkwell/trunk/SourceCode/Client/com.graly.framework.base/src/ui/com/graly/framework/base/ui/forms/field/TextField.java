package com.graly.framework.base.ui.forms.field;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class TextField extends AbstractField {

	private int mStyle = SWT.NONE;
	private int mRows = 4;
	private int mCols = 32;
	private int mLimit = 32;
	private boolean autoUpper = false;
	Text text;
	
	public TextField(String id) {
		this(id, SWT.BORDER);
	}

	public TextField(String id, int style) {
		super(id);
		this.mStyle = style;
	}
	
	public TextField(String id, boolean autoUpper) {
		super(id);
		this.autoUpper = autoUpper;
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
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        text.setLayoutData(gd);
        if ((mStyle & SWT.READ_ONLY) == SWT.READ_ONLY) {
            text.setBackground(text.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        }
        FontMetrics fm = getFontMetric(text);
        if ((mStyle & SWT.MULTI) == SWT.MULTI) {
            if (mRows > 0) {
                gd.heightHint = Dialog.convertHeightInCharsToPixels(fm, mRows);
            }
        } else {
        	text.setTextLimit(mLimit);
        }
        if (mCols > 0) {
            gd.widthHint = Dialog.convertWidthInCharsToPixels(fm, mCols);
        }
        text.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
            	if (autoUpper) {
            		setValue(text.getText().toUpperCase());
            	} else {
            		setValue(text.getText());
            	}
            }
        });
        text.addFocusListener(new FocusListener() {
        	public void focusGained(FocusEvent e) {
            }

            public void focusLost(FocusEvent e) {
            	if (autoUpper) {
            		text.setText(text.getText().toUpperCase());
            	}
            }
        });
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

    public String getText() {
        return getTextControl().getText();
    }

    public void setText(String text) {
    	if (text == null)
    		text = "";
    	if (autoUpper) {
    		text = text.toUpperCase();
    	}
        getTextControl().setText(text);
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
    
	public void refresh() {
		setText((String) getValue());
	}
	
	public String getFieldType() {
		return "text";
	}
	
	@Override
	public void enableChanged(boolean enabled) {
		if ((mStyle & SWT.READ_ONLY) != SWT.READ_ONLY) {
			text.setEnabled(enabled);
		}
		super.enableChanged(enabled);
    }
}

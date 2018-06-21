package com.graly.promisone.base.ui.forms.field;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.promisone.activeentity.model.ADBase;
import com.graly.promisone.activeentity.model.ADRefTable;
import com.graly.promisone.activeentity.model.ADTable;
import com.graly.promisone.base.entitymanager.dialog.SingleEntityQueryDialog;
import com.graly.promisone.base.entitymanager.views.TableListManager;
import com.graly.promisone.base.ui.custom.XSearchComposite;

public class SearchField extends AbstractField {
	
	protected XSearchComposite xSearch;
	protected int mStyle = SWT.BORDER | SWT.READ_ONLY;
	private ADRefTable refTable;
    protected ADTable adTable;
    protected TableListManager listTableManager;
    protected String whereClause;

    public SearchField(String id, TableViewer viewer, int style) {
        super(id);
        mStyle = mStyle | style;
    }
    
    public SearchField(String id, ADTable adTable, ADRefTable refTable,
    		String whereClause, int style) {
    	super(id);
    	this.adTable = adTable;
    	this.refTable = refTable;
    	this.whereClause = whereClause;
    	this.mStyle = mStyle | style;
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
        xSearch = new XSearchComposite(composite, adTable, refTable.getValueField(), mStyle);
        toolkit.adapt(xSearch);
        toolkit.paintBordersFor(xSearch);
            
        String val = (String)getValue();
        if (val != null) {
        	xSearch.setKey(Long.parseLong(val));
        } else {
			xSearch.setText("");
		}
        
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        xSearch.setLayoutData(gd);
        if (getToolTipText() != null) {
        	xSearch.setToolTipText(getToolTipText());
        }
        mControls[i] = xSearch;
        xSearch.addArrowSelectionListener(getSelectionListener());
	}

    protected SelectionListener getSelectionListener() {
    	return new SelectionAdapter() {
    		public void widgetSelected(SelectionEvent e) {
    			listTableManager = new TableListManager(adTable);
    			int style = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
    			| SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
    			
    			SingleEntityQueryDialog singleDialog = new SingleEntityQueryDialog(e.widget.getDisplay().getActiveShell()
						, listTableManager, null, whereClause, style);
    			singleDialog.setTempSearchCondition(createTempWhereClause());
    			if(singleDialog.open() == IDialogConstants.OK_ID) {
    				ADBase adBase = singleDialog.getSelectionEntity();
    				if(adBase != null && adBase.getObjectId() != null) {
    					setValue(adBase.getObjectId().toString());
    				}
    				refresh();
    			}
    		}
    	};    		
    }
    
    protected String createTempWhereClause() {
    	StringBuffer sb = new StringBuffer(" ");
    	if(xSearch.getText() != null && !"".equals(xSearch.getText().trim())) {
			sb.append(adTable.getModelName());
			sb.append(".");
			sb.append(refTable.getValueField());
			sb.append(" = '");
			sb.append(xSearch.getText());
			sb.append("'");
			return sb.toString();
		}
    	return null;
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
		String key = (String)getValue();
		if(key != null) {
			xSearch.setKey(Long.parseLong(key));		
		} else {
			xSearch.setText("");
		}
	}
	
	@Override
    public void setEnabled(boolean enabled) {
    	super.setEnabled(enabled);
    	xSearch.setEnabled(enabled);
    }
    
	public String getFieldType() {
		return "search";
	}
	
}
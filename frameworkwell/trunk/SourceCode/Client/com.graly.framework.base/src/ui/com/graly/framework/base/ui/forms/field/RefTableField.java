package com.graly.framework.base.ui.forms.field;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.ui.custom.XCombo;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.StringUtil;
import com.graly.framework.runtime.Framework;

public class RefTableField extends AbstractField implements IValueChangeListener{

	private static final Logger logger = Logger.getLogger(RefTableField.class);

	protected int mStyle = SWT.BORDER;
    protected TableViewer viewer;
    XCombo combo;
	private ADRefTable refTable;
	private Map<String, String> paramMap = new HashMap<String, String>();
	private boolean isReadOnly;
	
    public RefTableField(String id, TableViewer viewer, ADRefTable refTable) {
        super(id);
        this.viewer = viewer;
    	this.setRefTable(refTable);
    }
    
    public RefTableField(String id, TableViewer viewer, ADRefTable refTable, int style) {
        super(id);
        mStyle = style;
        this.viewer = viewer;
    	this.setRefTable(refTable);
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
        combo = new XCombo(composite, viewer, getRefTable().getKeyField(), getRefTable().getValueField(), mStyle, isReadOnly);
        toolkit.adapt(combo);
        toolkit.paintBordersFor(combo);
            
        String val = (String)getValue();
        if (val != null) {
            combo.setKey(val);
        } else {
			combo.setKey("");
			combo.setText("");
		}
        
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        combo.setLayoutData(gd);
        if (getToolTipText() != null) {
            combo.setToolTipText(getToolTipText());
        }
        mControls[i] = combo;
        combo.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
            	if (getValue() != null) {
            		if (!getValue().equals(combo.getKey())) {
            			setValue(combo.getKey());
            		}
            	} else {
            		if (combo.getKey() != null && !"".equals(combo.getKey())) {
            			setValue(combo.getKey());
            		}
            	}
            }
        });
	}

	public XCombo getComboControl() {
        Control[] ctrl = getControls();
        if(ctrl.length >  1) {
            return (XCombo)ctrl[1];
        } else {
            return (XCombo)ctrl[0];
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
    
    public Object getData() {
    	return getComboControl().getData();
    }
    
	@Override
	public void refresh() {
		if (getValue() != null) {
			combo.setKey(getValue().toString());
		} else {
			combo.setKey("");
			combo.setText("");
		}
	}

	public void setRefTable(ADRefTable refTable) {
		this.refTable = refTable;
	}

	public ADRefTable getRefTable() {
		return refTable;
	}
	
	public void valueChanged(Object sender, Object newValue){
		if (StringUtil.parseClauseParam(refTable.getWhereClause()).size() > 0){
			IField field = (IField)sender;
			try {
				String name = field.getId();
				String value = "";
				if (sender instanceof RefTableField) {
					value = PropertyUtil.getValueString(newValue);
				} else if (sender instanceof SearchField){
					SearchField sField = (SearchField)sender;
					value = PropertyUtil.getValueString(sField.getValue());
				} else {
					value = PropertyUtil.getValueString(newValue);
				}
				paramMap.put(name, value);
				List<String> paramList = StringUtil.parseClauseParam(refTable.getWhereClause());
				for (String param : paramList){
					if (paramMap.get(param) == null){
						return;
					}
				}
				String whereClause = StringUtil.parseClause(refTable.getWhereClause(), paramMap);
				ADManager entityManager = Framework.getService(ADManager.class);
				ADTable adTable = entityManager.getADTable(refTable.getTableRrn());
				List<ADBase> list = entityManager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(), Env.getMaxResult(), whereClause, refTable.getOrderByClause());
				if (!((ADField)this.getADField()).getIsMandatory()) {
					String className = adTable.getModelClass();
					list.add((ADBase)Class.forName(className).newInstance());
				}
				viewer.setInput(list);
				
				refresh();
			} catch (Exception e){
				logger.error("RefTableField : valueChanged", e);
			}
		}
	}
	
	public String getFieldType() {
		return "reftable";
	}
	
	@Override
	public void enableChanged(boolean enabled) {
		combo.setEnabled(enabled);
		super.enableChanged(enabled);
    }

	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}

	public void setInput(List input){
		viewer.setInput(input);
	}
}

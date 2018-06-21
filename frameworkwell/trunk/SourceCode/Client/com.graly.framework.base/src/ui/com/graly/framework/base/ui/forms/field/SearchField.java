package com.graly.framework.base.ui.forms.field;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
import com.graly.framework.base.entitymanager.query.SingleEntityQueryDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.custom.XSearchComposite;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;

public class SearchField extends AbstractField {
	
	Logger logger = Logger.getLogger(SearchField.class);
	
	protected XSearchComposite xSearch;
	protected int mStyle = SWT.BORDER;
	private ADRefTable refTable;
    protected ADTable adTable;
    protected TableListManager listTableManager;
    protected String whereClause;
    private Object data;
    private Object key;
    private String PRIMARY_KEY_FILE = "objectRrn";
    
    public SearchField(String id, TableViewer viewer, int style) {
        super(id);
        mStyle = mStyle | style;
    }
    
    public SearchField(String id, ADTable adTable, ADRefTable refTable,
    		String whereClause, int style) {
    	super(id);
    	this.adTable = adTable;
    	this.setRefTable(refTable);
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
        xSearch = new XSearchComposite(composite, adTable, getRefTable().getValueField(), mStyle);
        toolkit.adapt(xSearch);
        toolkit.paintBordersFor(xSearch);
            
        refresh();
        
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        xSearch.setLayoutData(gd);
        if (getToolTipText() != null) {
        	xSearch.setToolTipText(getToolTipText());
        }
        mControls[i] = xSearch;
        xSearch.addArrowSelectionListener(getSelectionListener());
        xSearch.addKeyListener(getKeyListener());
        xSearch.addFocusListener(getFocusListener());
	}

    protected SelectionListener getSelectionListener() {
    	return new SelectionAdapter() {
    		public void widgetSelected(SelectionEvent e) {
    			listTableManager = new TableListManager(adTable);
    			int style = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
    			| SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
    			
    			SingleEntityQueryDialog singleDialog = new SingleEntityQueryDialog(
    					listTableManager, null, whereClause, style);
    			singleDialog.setTempSearchCondition(createLikeWhereClause());
    			if(singleDialog.open() == IDialogConstants.OK_ID) {
    				ADBase adBase = singleDialog.getSelectionEntity();
    				if(adBase != null && adBase.getObjectRrn() != null) {
    					setKey(adBase.getObjectRrn().toString(), adBase);
    				}
    				refresh();
    				setFocus();
    			}
    		}
    	};    		
    }
    
    protected String createWhereClause() {
    	StringBuffer sb = new StringBuffer(" ");
    	if(xSearch.getText() != null && !"".equals(xSearch.getText().trim())) {
			sb.append(adTable.getModelName());
			sb.append(".");
			sb.append(getRefTable().getValueField());
			sb.append(" = '");
			sb.append(xSearch.getText().trim());
			sb.append("'");
			return sb.toString();
		}
    	return null;
    }
    
    protected String createLikeWhereClause() {
    	StringBuffer sb = new StringBuffer(" ");
    	if(xSearch.getText() != null && !"".equals(xSearch.getText().trim())) {
			sb.append(adTable.getModelName());
			sb.append(".");
			sb.append(getRefTable().getValueField());
			sb.append(" LIKE '");
			sb.append(xSearch.getText().trim());
			sb.append("%'");
			return sb.toString();
		}
    	return null;
    }

    protected KeyListener getKeyListener() {
    	return new KeyAdapter() {
    		 public void keyPressed(KeyEvent event) {
    			 xSearch.setForeground(SWTResourceCache.getColor("Black"));
    			 switch (event.keyCode) {
    		        case SWT.CR:
    		        	ADBase data = getObjectByValue();
    		        	if (data == null) {
    		        		xSearch.setForeground(SWTResourceCache.getColor("Red"));
    		        		setKey(null, null);
    		        	} else {
    		        		setKey(data.getObjectRrn(), data);
    		        	}
    		          break;
    		        }
    		 }
    	};
    }
    
	protected FocusListener getFocusListener() {
		return new FocusListener() {
			public void focusGained(FocusEvent e) {
			}
			public void focusLost(FocusEvent e) {
				if(xSearch.getText() != null && !"".equals(xSearch.getText().trim())) {
					ADBase data = getObjectByValue();
					if (data == null) {
		        		xSearch.setForeground(SWTResourceCache.getColor("Red"));
		        		setKey(null, null);
		        	} else {
		        		setKey(data.getObjectRrn(), data);
		        		xSearch.setForeground(SWTResourceCache.getColor("Black"));
		        	}
				} else {
					setKey(null);
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
		if(getKey() != null) {
			xSearch.setKey(Long.parseLong(getKey().toString()), getData());
			xSearch.setForeground(SWTResourceCache.getColor("Black"));
		} else {
			xSearch.setText("");
		}
	}
	
	@Override
    public void setEnabled(boolean enabled) {
    	super.setEnabled(enabled);
    	xSearch.setEnabled(enabled);
    }
    
	public void setKey(Object key, Object data) {
		if (getADField() != null && ((ADField)getADField()).getIsReadonly()){
    		this.setEnabled(false);
    	}
		this.data = data;
		this.key = key;
		setValueInternal(data);
		notifyValueChangeListeners(this, data);
	}
	
	public void setKey(Object key) {
		boolean nofifyFlag = false;
		if (getADField() != null && ((ADField)getADField()).getIsReadonly()){
    		this.setEnabled(false);
    	}
    	Object oldKey = this.key;
    	if (oldKey == null && key == null){
    		return;
    	} else if (oldKey != null){
    		if (!oldKey.equals(key)){
    			nofifyFlag = true;
    		}
    	} else if (key != null){
    		nofifyFlag = true;
    	}
    	
    	if (nofifyFlag) {
    		try {
    			Object data = null;
    			if (key != null) {
	            	ADManager manager = Framework.getService(ADManager.class);
	            	long objectRrn = Long.parseLong((String)key);
	            	String className = adTable.getModelClass();
	        		ADBase base = (ADBase)Class.forName(className).newInstance();
	        		base.setObjectRrn(objectRrn);
	        		data = manager.getEntity(base);
    			}
    			this.key = key;
    			this.data = data;
    			setValueInternal(data);
                notifyValueChangeListeners(this, data);
            } catch (Exception e) {
            	logger.error(e);
            }
    	}
	}
	
	@Override
	public void setValue(Object data) {
		if (data == null || data.toString().trim().length() == 0) {
			setKey(null);
			return;
		}
		if (this.PRIMARY_KEY_FILE.equals(getRefTable().getKeyField())) {
			setKey(data);
		} else {
			String strValue = data.toString();
			StringBuffer sb = new StringBuffer(" ");
	    	sb.append(adTable.getModelName());
			sb.append(".");
			sb.append(getRefTable().getKeyField());
			sb.append(" = '");
			sb.append(strValue);
			sb.append("'");
			try {
				ADManager manager = Framework.getService(ADManager.class);
				List<ADBase> list = manager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(), 1, sb.toString(), "");
				if (list != null && list.size() > 0) {
					ADBase base = (ADBase)list.get(0);
					setKey(base.getObjectRrn(), base);
					return;
				}
			} catch (Exception e) {
	        	logger.error(e);
	        }
			setKey(null);
		}
	}
	
	private void setValueInternal(Object data) {
		if (data == null) {
			this.value = null;
		} else {
			try{
				String valueStr = (String)PropertyUtil.getPropertyForString(data, getRefTable().getKeyField());
				this.value = valueStr;
			} catch (Exception e){
				this.value = null;
				logger.error(e);
			}
		}
	}
	
	public Object getKey() {
		return key;
	}
	
	public String getFieldType() {
		return "search";
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Object getData() {
		return data;
	}
	
	private ADBase getObjectByValue() {
		try {
			String condition = createWhereClause();
			if (condition != null) {
				ADManager manager = Framework.getService(ADManager.class);
				List<ADBase> list = manager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(), 1, condition, "");
				if (list != null && list.size() > 0) {
					return list.get(0);
				}
			}
		} catch (Exception e) {
        	logger.error(e);
        }
		return null;
	}

	public void setRefTable(ADRefTable refTable) {
		this.refTable = refTable;
	}

	public ADRefTable getRefTable() {
		return refTable;
	}
	
	public void setFocus() {
		xSearch.setFocus();
	}
	
	public String getText() {
		if(xSearch.getText() != null && !"".equals(xSearch.getText().trim())) {
			return xSearch.getText().trim();
		}
		return null;
	}
}
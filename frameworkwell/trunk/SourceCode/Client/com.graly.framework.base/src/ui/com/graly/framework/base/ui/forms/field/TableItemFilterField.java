package com.graly.framework.base.ui.forms.field;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.query.SingleQueryDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.security.usergroup.UserQueryDialog;
import com.graly.framework.base.ui.util.Message;

public class TableItemFilterField extends AbstractField {
	
	protected Button add, delete;
	protected int mStyle = SWT.READ_ONLY | SWT.BORDER | SWT.FULL_SELECTION;
    protected CheckboxTableViewer viewer;
    protected Table table;
    protected List<Object> mItems;
    protected ADTable adTable;
    protected TableListManager listTableManager;
    protected List<ADBase> selectedList;
    protected List<Object> totalValueList;
    protected String whereClause;
	
	public TableItemFilterField(String id, TableViewer viewer, ADTable adTable, int style, String whereClause) {
        super(id);
        this.viewer = (CheckboxTableViewer)viewer;
        this.adTable = adTable;
        this.mStyle = this.mStyle | style;
        this.whereClause = whereClause;
    }
	
	public TableItemFilterField(String id, TableViewer viewer, ADTable adTable, int style) {
		this(id, viewer, adTable, style, null);
    }
    
    public TableItemFilterField(String id, TableViewer viewer, int style) {
        super(id);
        this.viewer = (CheckboxTableViewer)viewer;
        mStyle = mStyle | style;
    }

    @Override
	public void createContent(Composite composite, FormToolkit toolkit) {
		int i = 0;
		String labelStr = getLabel();
        if (labelStr != null) {
        	mControls = new Control[2];
        	i = 1;
        } else {
        	mControls = new Control[1];
        }
		
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        Composite top = toolkit.createComposite(composite, SWT.NULL);
        top.setLayout(new GridLayout(2, false));
        top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));        
        Label label = toolkit.createLabel(top, labelStr);
        mControls[0] = label;
        
        Composite tableContainer = toolkit.createComposite(top, SWT.NULL);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        tableContainer.setLayout(new GridLayout());
        tableContainer.setLayoutData(gd);
        table = viewer.getTable();
        table.setParent(tableContainer);
        Rectangle listRect = table.getBounds ();
    	gd.heightHint = table.getItemHeight () * 13;
    	table.setBounds(listRect);
    	tableContainer.setLayoutData(gd);
           	
    	createButtons(toolkit, composite);

		add.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					List<ADBase> values = (List<ADBase>)getValue();
					listTableManager = new TableListManager(adTable);
					SingleQueryDialog singleDialog = null;
					int style = SWT.CHECK | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
					| SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
					singleDialog = new SingleQueryDialog(listTableManager, null, whereClause, style);
					singleDialog.setObject(values);
					if(singleDialog.open() == IDialogConstants.OK_ID) {
						List<ADBase> selectedList = singleDialog.getSelectionList();
						if(values != null) {
							for(ADBase b : selectedList){
								if (!values.contains(b)) {
									values.add(b);
								}
							}
							filter(values);
						} else {
							filter(selectedList);
						}
					}
				}
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
		});
		
		delete.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					List<ADBase> list = (List<ADBase>)getValue();
					Object[] os = viewer.getCheckedElements();
					if(os.length != 0) {
						for(Object o : os) {
							ADBase pe = (ADBase)o;
							list.remove(pe);						
						}
					}
					filter(list);
				}
				
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
		});
  	
    	mControls[i] = table;
    	List<Object> val = (List<Object>)getValue();
        if (val != null) {
        	viewer.setInput(val);
        }
	}
    
    public void createButtons(FormToolkit toolkit, Composite composite) {
    	Composite bn = toolkit.createComposite(composite, SWT.NULL);
    	bn.setLayout( new GridLayout(3, false));
    	GridData g = new GridData();
    	g.horizontalAlignment = GridData.END;
    	bn.setLayoutData(g); 
    	add = toolkit.createButton(bn, Message.getString("common.add"), SWT.PUSH);
    	delete = toolkit.createButton(bn, Message.getString("common.delete"), SWT.PUSH);
    	decorateButton(add);
    	decorateButton(delete);
    }

	public void filter(List<ADBase> list) {
		setValue(list);
		refresh();
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
		List<Object> val = (List<Object>)getValue();
        if (val != null) {
        	viewer.setInput(val);
        } else {
        	viewer.setInput(new ArrayList<Object>());
        }
        if(viewer != null && listTableManager != null) {
        	listTableManager.updateView(viewer);
        }
	}
	
	@Override
    public void setEnabled(boolean enabled) {
    	super.setEnabled(enabled);
    	this.add.setEnabled(enabled);
    	this.delete.setEnabled(enabled);
    }
    
	public String getFieldType() {
		return "tableselect";
	}
	
	public void decorateButton(Button button) {
		button.setFont(JFaceResources.getDialogFont());
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		int widthHint = 88;  //IDialogConstants.BUTTON_WIDTH
		Point minSize = button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		data.widthHint = Math.max(widthHint, minSize.x);
		button.setLayoutData(data);
	}	
}
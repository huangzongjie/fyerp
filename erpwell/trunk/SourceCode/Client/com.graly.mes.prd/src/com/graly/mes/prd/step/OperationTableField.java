package com.graly.mes.prd.step;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
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

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.AbstractField;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.prd.model.Operation;

public class OperationTableField extends AbstractField {
	
	protected List<Object> values;
	protected Button delete, up, down, append;
	protected int mStyle = SWT.READ_ONLY | SWT.BORDER;
    protected CheckboxTableViewer viewer;
    protected Table table;
    protected List<Object> mItems;
    protected ADTable adTable;
    protected TableListManager listTableManager;
	
	public OperationTableField(String id, TableViewer viewer, ADTable adTable, int style) {
		super(id);
        this.viewer = (CheckboxTableViewer)viewer;
        this.adTable = adTable;
        this.mStyle = this.mStyle | style;
    }
    
    public OperationTableField(String id, TableViewer viewer, int style) {
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
    	ViewerSorter viewerSorter = createrViewerSorter();
    	viewer.setSorter(viewerSorter);
    	
    	createButtons(toolkit, composite);
    	append.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
            	int seqNo = 0;
            	values = (List<Object>)getValue();
            	if(values != null) {
            		seqNo = values.size(); 
            	} 
            	OperationDialog od = new OperationDialog(e.widget.getDisplay().getActiveShell());
            	if(od.open() == IDialogConstants.OK_ID) {
            		Operation operation = od.getOperation();
            		createPRDOperation(operation, seqNo);
            	}
            }
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
    	});
    	delete.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				List<Object> list = (List<Object>)getValue();
				Object[] os = viewer.getCheckedElements();
				if(os.length != 0) {
					for(Object o : os) {
						Operation pe = (Operation)o;
						changeAfterOperationSeqNo(pe);
						if(list.contains(pe)) {
							list.remove(pe);
						}
					}
				}
				filter(list);
			}			
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			
			public void changeAfterOperationSeqNo(Operation pe) {
				TableItem[] tis = table.getItems();
				int deleteOperationSeqNo = pe.getSeqNo().intValue();
				for(TableItem ti : tis) {
    				Operation op = (Operation)ti.getData();
    				int afterSeqNo = op.getSeqNo().intValue();
    				if (afterSeqNo > deleteOperationSeqNo) {
    					op.setSeqNo(new Long(afterSeqNo -1));
    				}
    			}
			}
		});
    	up.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
            	Object[] os = viewer.getCheckedElements();
            	if(os.length > 0) {
            		for(int i = 0; i < os.length; i++) {
            			int seqNo = ((Operation)os[i]).getSeqNo().intValue();
            			if(seqNo > 1){
            				Operation op = (Operation)os[i];
            				searchLastOperation(seqNo);
            				op.setSeqNo(new Long(seqNo - 1));
            			} else break;            				
            		}
            	}
            	viewer.refresh();
            	viewer.setCheckedElements(os);
            }
            
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
            
            public void searchLastOperation(int seqNo) {
    			TableItem[] tis = table.getItems();
    			for(TableItem ti : tis) {
    				Operation op = (Operation)ti.getData();
    				if (op.getSeqNo() == seqNo - 1) {
    					op.setSeqNo(new Long(seqNo));
    					break;
    				}
    			}
    		}
    	});
    	down.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
            	Object[] os = viewer.getCheckedElements();
            	int count = table.getItems().length;
            	if(os.length > 0) {
            		int checkedLength = os.length;
            			for(int i = checkedLength -1; i >= 0; i--) {
            				int seqNo = ((Operation)os[i]).getSeqNo().intValue();
            				if(seqNo < count){
            					Operation op = (Operation)os[i];
            					searchNextOperation(seqNo);
            					op.setSeqNo(new Long(seqNo + 1));
            				} else break;
            				
            			}
            	}
            	viewer.refresh();
            	viewer.setCheckedElements(os);
            }
            
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
            
            public void searchNextOperation(int seqNo) {
    			TableItem[] tis = table.getItems();
    			for(TableItem ti : tis) {
    				Operation op = (Operation)ti.getData();
    				if (op.getSeqNo() == seqNo + 1) {
    					op.setSeqNo(new Long(seqNo));
    					break;
    				}
    			}
    		}
    	});
    	
    	viewer.addDoubleClickListener(new IDoubleClickListener() {
    		public void doubleClick(DoubleClickEvent event) {
    			values = (List<Object>)getValue();
    			StructuredSelection ss = (StructuredSelection)event.getSelection();
    			Operation operation = (Operation)ss.getFirstElement();
    			OperationDialog od = new OperationDialog(Display.getCurrent().getActiveShell(), 
    					operation);
    			if(od.open() == IDialogConstants.OK_ID) {
    				operation = od.getOperation();
    				createPRDOperation(operation, -1);
    			}
    			
    		}
    	});
    	
    	mControls[i] = table;
    	List<Object> val = (List<Object>)getValue();
        if (val != null) {
        	viewer.setInput(val);
        }
    }

    public void filter(List<Object> list) {
		setValue(list);
		refresh();
	}
    
    protected void createPRDOperation(Operation operation, int seqNo) {
    	if(operation == null) return ;
    	try {
    		if(seqNo != -1) {
				operation.setSeqNo(new Long(seqNo + 1));
			}
    		if(values == null) {
    			values = new ArrayList<Object>();
    			values.add(operation);
    		} else if(!values.contains(operation)) {
    			values.add(operation);
    		}
    		filter(values);
        } catch (Exception e) {
        	ExceptionHandlerManager.asyncHandleException(e);
        	return;
       }
    }
    
    protected void createPRDOperationContect(String operationContent, int seqNo) {
    	Operation o = new Operation();
    	o.setDesciption(operationContent);
    	o.setSeqNo(new Long(seqNo+1));
    }
    
    public void createButtons(FormToolkit toolkit, Composite composite) {
    	Composite bn = toolkit.createComposite(composite, SWT.NULL);
    	bn.setLayout( new GridLayout(5, false));
    	GridData g = new GridData();
    	g.horizontalAlignment = GridData.END;
    	bn.setLayoutData(g);
    	append = toolkit.createButton(bn, Message.getString("common.add"), SWT.PUSH);
    	delete = toolkit.createButton(bn, Message.getString("common.delete"), SWT.PUSH);
    	up = toolkit.createButton(bn, Message.getString("common.up"), SWT.PUSH);
    	down = toolkit.createButton(bn, Message.getString("common.down"), SWT.PUSH);
    	decorateButton(append);
    	decorateButton(delete);
    	decorateButton(up);
    	decorateButton(down);
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

    public ViewerSorter createrViewerSorter() {
    	return new ViewerSorter() {
    		@Override
    		public int compare(Viewer viewer, Object e1, Object e2) {
    			Long o1 = ((Operation)e1).getSeqNo();
            	Long o2 = ((Operation)e2).getSeqNo();
            	return o1.compareTo(o2);
    		}
    	};
    }
    
    public String getFieldType() {
		return "";
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
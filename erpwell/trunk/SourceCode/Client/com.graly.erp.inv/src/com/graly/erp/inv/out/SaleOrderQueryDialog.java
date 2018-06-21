package com.graly.erp.inv.out;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.sal.client.SALManager;
import com.graly.erp.sal.model.SalesOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADRefList;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.query.SingleEntityQueryDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.ComboField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.SearchField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class SaleOrderQueryDialog extends InClosableTitleAreaDialog {
	private static final Logger logger = Logger.getLogger(SaleOrderQueryDialog.class);
	private static final String TABLE_ANME = "INVSaleOrder";
	private static final int MAX_RESULT = 1000;
	static String WhereClause_PREFIX = " 1=1 ";
	static String OrderBy = " deliverDate DESC ";
	
	private IField warehouse; //WMS≤÷ø‚
	
	IManagedForm form;
	SaleOrderSearchField sfSo;
	MovementOut out;

	public SaleOrderQueryDialog(IManagedForm managedForm, Shell parentShell) {
		super(parentShell);
		this.form = managedForm;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		String dialogTitle = String.format(Message.getString("inv.sale_order_input"));
		Composite composite = (Composite) super.createDialogArea(parent);
		try {
			setTitle(dialogTitle);
			FormToolkit toolkit = new FormToolkit(composite.getDisplay());		
			ScrolledForm sForm = toolkit.createScrolledForm(composite);
			sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
			Composite body = sForm.getForm().getBody();
			configureBody(body);
			
			Composite content = toolkit.createComposite(body, SWT.NULL);
			content.setLayout(new GridLayout(2, false));
			content.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
			ADManager entityManager = Framework.getService(ADManager.class);
			ADRefTable refTable = new ADRefTable();
			refTable.setKeyField("id");
			refTable.setValueField("serialNumber");
			ADTable adTable = entityManager.getADTable(0L, TABLE_ANME);
			refTable.setTableRrn(adTable.getObjectRrn());
			
			String whereClause = "";
			if (refTable.getWhereClause() != null && !"".equals(refTable.getWhereClause().trim())) {
				whereClause = refTable.getWhereClause();
			}
			sfSo = new SaleOrderSearchField("serialNumber", adTable, refTable, whereClause, SWT.BORDER);
			sfSo.setLabel(Message.getString("wip.sale_order_id"));
			sfSo.createContent(content, toolkit);
			
	    	LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
			try {
				List<ADRefList> refList = entityManager.getADRefList(Env.getOrgRrn(), "WmsWarehouse");
				for (ADRefList listItem : refList){
					map.put(listItem.getValue(), listItem.getKey());
				}
	        } catch (Exception e) {
	        	logger.error("WriteOffSection : createWriteoffTypeField()", e);
	        }
	    	warehouse = createDropDownList("wmsWarehouse", "ø‚Œª", map);
	    	warehouse.createContent(content, toolkit);
			
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return composite;
	}

	public ComboField createDropDownList(String id, String label, LinkedHashMap<String, String> items) {
    	ComboField fe = new ComboField(id, items, SWT.BORDER | SWT.READ_ONLY);
        fe.setLabel(label);
        return fe;
    }
	
	@Override
	protected void buttonPressed(int buttonId) {
		if(buttonId == Dialog.OK) {
			String serialNumber = sfSo.getText();
			if(serialNumber == null || "".equals(serialNumber.trim())) 
				return;
			out = this.getMovementOutBySaleOrder(serialNumber);
			if(out == null) return;
		}
		super.buttonPressed(buttonId);
	}
	
	protected MovementOut getMovementOutBySaleOrder(String serialNumber) {
		MovementOut out = null;
		try {
			SALManager salManager = Framework.getService(SALManager.class);
			out = salManager.createMovementOutFromSo(Env.getOrgRrn(), serialNumber, Env.getUserRrn());
			String wmsWarehouse = warehouse.getValue()!=null?warehouse.getValue().toString():null;
			out.setWmsWarehouse(wmsWarehouse);
			INVManager invManager = Framework.getService(INVManager.class);
			out = invManager.saveMovementOutLine(out, out.getMovementLines(), MovementOut.OutType.SOU, Env.getUserRrn());
		} catch(Exception e) {
			logger.error("SaleOrderQueryDialog : getMovementOutBySaleOrder()", e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return out;
	}

	public MovementOut getOut() {
		return out;
	}
	
	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout(1, true);
		body.setLayout(layout);
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	class SaleOrderSearchField extends SearchField {

		public SaleOrderSearchField(String id, ADTable adTable, ADRefTable refTable,
				String whereClause, int style) {
			super(id, adTable, refTable, whereClause, style);
		}

		public SaleOrderSearchField(String id, TableViewer viewer, int style) {
			super(id, viewer, style);
		}
		
		protected SelectionListener getSelectionListener() {
	    	return new SelectionAdapter() {
	    		public void widgetSelected(SelectionEvent e) {
	    			listTableManager = new TableListManager(adTable);
	    			int style = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
	    			| SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
	    			
	    			SaleOrderSingleQueryDialog singleDialog = new SaleOrderSingleQueryDialog(
	    					listTableManager, null, whereClause, style);
	    			singleDialog.setTempSearchCondition(createLikeWhereClause());
	    			if(singleDialog.open() == IDialogConstants.OK_ID) {
	    				SalesOrder so = singleDialog.getSelectionSalesOrder();
	    				if(so != null && so.getId() != null) {
	    					setKey(so.getId(), so);
	    				}
	    				refresh();
	    				setFocus();
	    			}
	    		}
	    	};
	    }
		
		protected KeyListener getKeyListener() {
	    	return new KeyAdapter() {
	    		 public void keyPressed(KeyEvent event) {
	    			 xSearch.setForeground(SWTResourceCache.getColor("Black"));
	    			 switch (event.keyCode) {
	    		        case SWT.CR:
	    		        	SalesOrder so = getObjectByValue();
	    		        	if (so == null) {
	    		        		xSearch.setForeground(SWTResourceCache.getColor("Red"));
	    		        		setKey(null, null);
	    		        	} else {
	    		        		setKey(so.getId(), so);
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
						SalesOrder so = getObjectByValue();
						if (so == null) {
			        		xSearch.setForeground(SWTResourceCache.getColor("Red"));
			        		setKey(null, null);
			        	} else {
			        		setKey(so.getId(), so);
			        		xSearch.setForeground(SWTResourceCache.getColor("Black"));
			        	}
					} else {
						setKey(null);
					}
				}
			};
		}
		
		private SalesOrder getObjectByValue() {
			try {
				String condition = createWhereClause();
				if (condition != null) {
					SALManager salManager = Framework.getService(SALManager.class);
					List<SalesOrder> list = salManager.getSelesOrderList(MAX_RESULT, condition, OrderBy);
					if (list != null && list.size() > 0) {
						return list.get(0);
					}
				}
			} catch (Exception e) {
	        	logger.error(e);
	        }
			return null;
		}
		
	}
	
	class SaleOrderSingleQueryDialog extends SingleEntityQueryDialog {
		protected SalesOrder so;

		public SaleOrderSingleQueryDialog(TableListManager listTableManager,
				IManagedForm managedForm, String whereClause, int style) {
			super(listTableManager, managedForm, whereClause, style);
		}
		
		@Override
		protected void getInitSearchResult() {
			if(tempSearchCondition != null && !"".equals(tempSearchCondition.trim())) {
				List<SalesOrder> l = null;
				try {
					SALManager salManager = Framework.getService(SALManager.class);
					l = salManager.getSelesOrderList(MAX_RESULT, tempSearchCondition, OrderBy);
				} catch (Exception e) {
					logger.error("Error SaleOrderSingleQueryDialog : getInitSearchResult() ", e);
				}
				tableViewer.setInput(l);			
				listTableManager.updateView(tableViewer);			
			}
		}
		
		protected void refresh(boolean clearFlag) {
			List<SalesOrder> l = null;
			StringBuffer temp = new StringBuffer("");
			temp.append(WhereClause_PREFIX);
			if(getKeys() != null && !"".equals(getKeys().trim())) {
				temp.append(" AND ");
				temp.append(getKeys());
			}
			try {
				SALManager salManager = Framework.getService(SALManager.class);
				l = salManager.getSelesOrderList(MAX_RESULT, temp.toString(), OrderBy);
	        } catch (Exception e) {
	        	ExceptionHandlerManager.asyncHandleException(e);
	        	logger.error("Error SaleOrderSingleQueryDialog : refresh() " + e.getMessage(), e);
	        }
			tableViewer.setInput(l);
			listTableManager.updateView(tableViewer);
		}
		
		protected void buttonPressed(int buttonId) {
			if(buttonId == IDialogConstants.OK_ID) {
				if(tableViewer.getTable().getSelection().length > 0) {
					TableItem ti = tableViewer.getTable().getSelection()[0];
					so = (SalesOrder)ti.getData();
				}
				okPressed();
			} else if (IDialogConstants.CANCEL_ID == buttonId) {
				cancelPressed();
			}
		}
		
		public SalesOrder getSelectionSalesOrder() {
			return so;
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Message.getString("common.ok"),
				false);
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.cancel"), false);
	}

}

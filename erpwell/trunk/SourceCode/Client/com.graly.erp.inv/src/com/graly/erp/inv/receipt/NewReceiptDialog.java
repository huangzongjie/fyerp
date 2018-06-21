package com.graly.erp.inv.receipt;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.Receipt;
import com.graly.erp.inv.model.ReceiptLine;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADRefList;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.query.SingleEntityQueryDialog;
import com.graly.framework.base.entitymanager.query.SingleQueryDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.ComboField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.SearchField;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class NewReceiptDialog extends SingleQueryDialog {
	Logger logger = Logger.getLogger(NewReceiptDialog.class);
	private static final String WHERE_CLAUSE_SUFFIX = " AND 1=1 AND warehouseRrn <> 151046";//" AND (PurchaseOrderLine.qty >=  PurchaseOrderLine.qtyDelivered OR PurchaseOrderLine.qtyDelivered IS NULL)";
	protected String where;
	private ADTable adTable;
	private String TABLE_NAME = "INVReceiptLine";
	protected List<ReceiptLine> receiptLineList = new ArrayList<ReceiptLine>();
	private PurchaseOrderLine purchaseOrderLine;
	private static final String FieldName_poRrn = "poRrn";
	private IField f; // SearchField控件(采购订单编号)
	private IField warehouse; // SearchField控件(采购订单编号)
	private Receipt receipt;
	private Object boject;

	public NewReceiptDialog() {
		super();
	}

	public NewReceiptDialog(TableListManager listTableManager, IManagedForm managedForm, String whereClause, int style) {
		super(listTableManager, managedForm, whereClause, style);
	}

	public NewReceiptDialog(TableListManager listTableManager, IManagedForm managedForm, String whereClause, int style, Object boject) {
		super(listTableManager, managedForm, whereClause, style);
		this.boject = boject;
	}

	public NewReceiptDialog(StructuredViewer viewer, Object object) {
		super();
		this.viewer = (CheckboxTableViewer) viewer;
		this.object = object;
	}

	protected Point getInitialSize() {
		Point p = super.getInitialSize();
		p.x = 950;
		p.y = 550;
		return p;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("search-dialog"));
		setTitle(Message.getString("inv.copyFromPO"));

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());
		Label titleBarSeparator = new Label(composite, SWT.HORIZONTAL | SWT.SEPARATOR);
		titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		GridLayout gl = new GridLayout();
		gl.numColumns = 1;
		composite.setLayout(gl);

		FormToolkit toolkit = new FormToolkit(composite.getDisplay());
		Composite queryComp = toolkit.createComposite(composite, SWT.BORDER);
		queryComp.setLayout(new GridLayout(2, false));
		queryComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite resultComp = new Composite(composite, SWT.NONE);
		resultComp.setLayout(new GridLayout());
		resultComp.setLayoutData(new GridData(GridData.FILL_BOTH));

		if (boject != null) {
			receipt = (Receipt) boject;
			createSearchTableViewer(resultComp);
			getInitSearchResult();
			whereClause = " poId = '" + receipt.getPoId() + "'";
			createWhereClause();
			refresh(true);
		} else {
			createSearchContent(queryComp, toolkit);
			createSearchTableViewer(resultComp);
			getInitSearchResult();
		}
		return composite;
	}
	
	protected void createSearchContent(Composite parent, FormToolkit toolkit) {
		try{
			ADField poField = null;
			for(ADField adField : listTableManager.getADTable().getFields()) {
				if(FieldName_poRrn.equals(adField.getName())) {
					poField = adField;
					break;
				}
			}
			if(poField != null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				ADRefTable refTable = new ADRefTable();
				refTable.setObjectRrn(poField.getReftableRrn());
				refTable = (ADRefTable)entityManager.getEntity(refTable);
				if (refTable == null || refTable.getTableRrn() == null){
					return;
				}
				ADTable adTable = entityManager.getADTable(refTable.getTableRrn());
				String whereClause = "";
				if (refTable.getWhereClause() != null && !"".equals(refTable.getWhereClause().trim())) {
					whereClause = refTable.getWhereClause();
				}
				f = new PoUnInOutSearchField(poField.getName(), adTable, refTable, whereClause, SWT.BORDER);
		    	f.setLabel(I18nUtil.getI18nMessage(poField, "label"));
		    	f.addValueChangeListener(getPOChangedListener());
		    	f.createContent(parent, toolkit);
		    	
		    	LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
				try {
					List<ADRefList> refList = entityManager.getADRefList(Env.getOrgRrn(), "WmsWarehouse");
					for (ADRefList listItem : refList){
						map.put(listItem.getValue(), listItem.getKey());
					}
		        } catch (Exception e) {
		        	logger.error("WriteOffSection : createWriteoffTypeField()", e);
		        }
		    	warehouse = createDropDownList("wmsWarehouse", "库位", map);
		    	warehouse.createContent(parent, toolkit);
			}
		} catch (Exception e){
			logger.error("EntityForm : Init tablelist", e);
		}
	}
	
	public ComboField createDropDownList(String id, String label, LinkedHashMap<String, String> items) {
    	ComboField fe = new ComboField(id, items, SWT.BORDER | SWT.READ_ONLY);
        fe.setLabel(label);
        return fe;
    }
	
	private IValueChangeListener getPOChangedListener() {
		return new IValueChangeListener() {
			public void valueChanged(Object sender, Object newValue) {
				createWhereClause();
				refresh(true);
			}
		};
	};
	
	@Override
	protected void refresh(boolean clearFlag) {
		List<PurchaseOrderLine> l = new ArrayList<PurchaseOrderLine>();
		try {
        	INVManager manager = Framework.getService(INVManager.class);
            l = manager.getPoLineForReceive(Env.getOrgRrn(), Env.getMaxResult(), getKeys());
        } catch (Exception e) {
        	logger.error("Error NewReceiptDialog : refresh() " + e.getMessage(), e);
        }
		if (object instanceof List) {
			exsitedItems = (List)object;
			if (exsitedItems != null) {
				l.removeAll(exsitedItems);
			}
		}
		viewer.setInput(l);			
		listTableManager.updateView(viewer);
	}

	@Override
	protected void createWhereClause() {
		StringBuffer temp = new StringBuffer(whereClause);
		temp.append(WHERE_CLAUSE_SUFFIX);
		if (f instanceof SearchField && ((SearchField)f).getKey() != null) {
			temp.append(" AND poRrn = " +((SearchField)f).getKey());
		}
		sb = temp;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		List<PurchaseOrderLine> poList = new ArrayList<PurchaseOrderLine>();
		if (buttonId == IDialogConstants.OK_ID) {
			Object[] os = viewer.getCheckedElements();
			Map<Long, PurchaseOrderLine> poLineMap = new HashMap<Long, PurchaseOrderLine>();
			for (Object object : os) {
				if (object instanceof PurchaseOrderLine) {
					purchaseOrderLine = (PurchaseOrderLine) object;
					if (!poLineMap.containsKey(purchaseOrderLine.getWarehouseRrn())) {
						poLineMap.put(purchaseOrderLine.getWarehouseRrn(), purchaseOrderLine);
					}
					poList.add(purchaseOrderLine);
				} else {
					purchaseOrderLine = null;
				}
			}
			if(poLineMap.size() > 1){
				UI.showError(Message.getString("inv.different_warehouse"));
				return;
			}
			try {
				INVManager invManager = Framework.getService(INVManager.class);
				Object object =  warehouse.getValue();
				String wmsWarehouse = object!=null?object.toString():null;
				if (boject != null) {
					receipt = (Receipt) boject;
					receipt = invManager.createReceiptFromPO(receipt, poList, Env.getUserRrn(),wmsWarehouse);
				} else {
					if (os.length == 0) {
						UI.showWarning(Message.getString("inv.polineisnull"));
						return;
					}
					receipt = invManager.createReceiptFromPO(receipt, poList, Env.getUserRrn(),wmsWarehouse);
					whereClause = " receiptId= '" + receipt.getDocId() + "'";
					getADTableOfRequisition();
					ReceiptLineDialog receiptlineDialog = new ReceiptLineDialog(UI.getActiveShell(), adTable, whereClause, receipt);
					receiptlineDialog.open();
				}
				ADManager adManager = Framework.getService(ADManager.class);
				receipt = (Receipt)adManager.getEntity(receipt);				
				okPressed();
			} catch (Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
				return;
			}
		} else if (IDialogConstants.CANCEL_ID == buttonId) {
			cancelPressed();
		}
	}

	protected ADTable getADTableOfRequisition() {
		try {
			if (adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, TABLE_NAME);
				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			}
			return adTable;
		} catch (Exception e) {
			logger.error("NewReceiptDialog : getADTableOfRequisition()", e);
		}
		return null;
	}

	public Receipt getReceipt() {
		return receipt;
	}

	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, Message.getString("common.ok"), false);
		createButton(parent, IDialogConstants.CANCEL_ID, Message.getString("common.cancel"), false);
	}
	
	/**
	 * @author Jim
	 * 已审核的未全部入库的采购订单查询控件
	 */
	class PoUnInOutSearchField extends SearchField {
		
		public PoUnInOutSearchField(String id, ADTable adTable,
				ADRefTable refTable, String whereClause, int style) {
			super(id, adTable, refTable, whereClause, style);
		}
		
		protected SelectionListener getSelectionListener() {
	    	return new SelectionAdapter() {
	    		public void widgetSelected(SelectionEvent e) {
	    			listTableManager = new TableListManager(adTable);
	    			int style = SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
	    			| SWT.FULL_SELECTION | SWT.HIDE_SELECTION;
	    			
	    			SingleEntityQueryDialog singleDialog = new PoQueryDialog(
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
	}
	
	class PoQueryDialog extends SingleEntityQueryDialog {
		public PoQueryDialog(TableListManager listTableManager,
				IManagedForm managedForm, String whereClause, int style){
			super(listTableManager, managedForm, whereClause, style);
		}
		
		@Override
		protected void refresh(boolean clearFlag) {
			List<PurchaseOrder> l = new ArrayList<PurchaseOrder>();
			try {
	        	INVManager invManager = Framework.getService(INVManager.class);
	        	String wc = getKeys();
	        	l = invManager.getUnInCompletedPoList(Env.getOrgRrn(), Env.getMaxResult(), wc, null);
	        } catch (Exception e) {
	        	logger.error("Error PoQueryDialog : refresh() " + e.getMessage(), e);
	        }
	        if (object instanceof List) {
				exsitedItems = (List)object;
				if (exsitedItems != null) {
					l.removeAll(exsitedItems);
				}
			}
			tableViewer.setInput(l);
			listTableManager.updateView(tableViewer);
		}
		
		protected void  createWhereClause() {
			String modelName = listTableManager.getADTable().getModelName() + ".";
			sb = new StringBuffer(" 1=1 ");
			if (queryForm!=null){
				LinkedHashMap<String, IField> fields = queryForm.getFields();
		        for(IField f : fields.values()) {
		        	if(f.getLabel().endsWith("*")){
		        		if(f.getValue() == null)
		        			sb.append(" AND 1<>1 ");
		        	}
		        	Object t = f.getValue();	        	
					if (t instanceof Date) {
						Date cc = (Date)t;
						if(cc != null) {
							sb.append(" AND ");
							sb.append("TO_CHAR(");
							sb.append(modelName);
							sb.append(f.getId());
							sb.append(", '" + I18nUtil.getDefaultDatePattern() + "') = '");
							sb.append(I18nUtil.formatDate(cc));
							sb.append("'");
						}
					} else if(t instanceof String) {
						String txt = (String)t;
						if(!txt.trim().equals("") && txt.length() != 0) {
							sb.append(" AND ");
							sb.append(modelName);
							sb.append(f.getId());
							sb.append(" LIKE '");
							sb.append(txt);
							sb.append("'");
						}
					} else if(t instanceof Boolean) {
						 Boolean bl = (Boolean)t;
						 sb.append(" AND ");
						 sb.append(modelName);
						 sb.append(f.getId());
						 sb.append(" = '");
						 if(bl) {
							sb.append("Y");
						 } else if(!bl) {
							sb.append("N");
						 }
						 sb.append("'");
					} else if(t instanceof Long) {
						long l = (Long)t;
						sb.append(" AND ");
						sb.append(modelName);
						sb.append(f.getId());
						sb.append(" = " + l + " ");
					}
		        }
			}			
		}
	}
}

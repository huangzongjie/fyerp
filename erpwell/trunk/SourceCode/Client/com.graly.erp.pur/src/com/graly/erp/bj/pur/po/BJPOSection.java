package com.graly.erp.bj.pur.po;


import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.MaterialQueryDialog;
import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Documentation;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.pur.client.PURManager;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.query.AdvanceQueryTray;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.ui.forms.field.FieldType;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.model.ADUser;

public class BJPOSection extends MasterSection {
	public static final Map<Long, String> DELIVERY_ADDRESS = new HashMap<Long, String>();
	static {
		DELIVERY_ADDRESS.put(139420L, "上海市浦东新区川大路518号");
		DELIVERY_ADDRESS.put(12644730L, "上海市浦东新区川展路588号");
	}
	private static final Logger logger = Logger.getLogger(BJPOSection.class);
	private static final String ISPAYMENTFULL="pur.ispaymentfull";
	protected ToolItem itemEdit;
	protected ToolItem itemNew;
	protected ToolItem itemDelete;
	protected ToolItem itemIspaymentFull;
	protected ToolItem itemPreview;
	protected PurchaseOrder selectedPO;
	EntityTableManager tableManager;
	private final String WHERE_CLAUSE = "ISPAYMENT_FULL='Y' AND DOC_STATUS='APPROVED'";
	
	private static final String TABLE_NAME = "BJPURPurchaseOrderLine";
	private ADTable adTable;
	protected PrintQueryDialog printQueryDialog;
	protected ADTable printADTable;
	
	private FormToolkit toolkit = null;
	public BJPOSection(EntityTableManager tableManager) {
		super(tableManager);
		this.tableManager=tableManager;
		initPrintTable();
	}
	protected void createSectionTitle(Composite client) {
//		if(Env.getOrgRrn()!=139420L){
//			return;
//		}
//		try {
//		ADUser user = Env.getUser();
//		if(user.getComments()==null || "".equals(user.getComments())){
//			return;
//		}
//		final String purchaser= user.getComments();
//		toolkit = new FormToolkit(client.getDisplay());
//		Composite titleBody = toolkit.createComposite(client);
//		Font font = new Font(Display.getDefault(),"宋体",10,SWT.BOLD); 
//		GridLayout gl = new GridLayout(4, false);
//		titleBody.setLayout(gl);
//		PURManager purManager = Framework.getService(PURManager.class);
//		ImageHyperlink receipttLink = toolkit.createImageHyperlink(titleBody, SWT.WRAP);
//		receipttLink.setText("已收货：("+purManager.getAlarmReceiptCount(Env.getOrgRrn(), purchaser)+")");
//		receipttLink.setFont(font);
//		receipttLink.setImage(SWTResourceCache.getImage("email"));
//		receipttLink.addHyperlinkListener(new IHyperlinkListener(){
//			@Override
//			public void linkActivated(HyperlinkEvent e) {
//				POAlarmDialog poAlarmDialog = new POAlarmDialog(UI.getActiveShell(),null,null,"RECEIPT",purchaser);
//				if(poAlarmDialog.open() == Dialog.OK){
//				}
//			}
//			@Override
//			public void linkEntered(HyperlinkEvent e) {}
//			@Override
//			public void linkExited(HyperlinkEvent e) {}
//			
//		});
//		ImageHyperlink iqcLink = toolkit.createImageHyperlink(titleBody, SWT.WRAP);
//		iqcLink.setFont(font);
//		iqcLink.setImage(SWTResourceCache.getImage("email"));
//		iqcLink.setText("已检验：("+purManager.getAlarmIqcCount(Env.getOrgRrn(), purchaser)+")");
//		iqcLink.addHyperlinkListener(new IHyperlinkListener(){
//			@Override
//			public void linkActivated(HyperlinkEvent e) {
//				POAlarmDialog poAlarmDialog = new POAlarmDialog(UI.getActiveShell(),null,null,"IQC",purchaser);
//				if(poAlarmDialog.open() == Dialog.OK){}
//			}
//			@Override
//			public void linkEntered(HyperlinkEvent e) {}
//			@Override
//			public void linkExited(HyperlinkEvent e) {}
//			
//		});
//		ImageHyperlink invLink = toolkit.createImageHyperlink(titleBody, SWT.WRAP);
//		invLink.setFont(font);
////			long l = purManager.getAlarmInvCount(Env.getOrgRrn(), Env.getUserName());
//		
//		invLink.setText("已入库：("+purManager.getAlarmInvCount(Env.getOrgRrn(), purchaser)+")");
//		invLink.setImage(SWTResourceCache.getImage("email"));
//		invLink.addHyperlinkListener(new IHyperlinkListener(){
//			@Override
//			public void linkActivated(HyperlinkEvent e) {
//				POAlarmDialog poAlarmDialog = new POAlarmDialog(UI.getActiveShell(),null,null,"INV",purchaser);
//				if(poAlarmDialog.open() == Dialog.OK){}
//			}
//			@Override
//			public void linkEntered(HyperlinkEvent e) {}
//			@Override
//			public void linkExited(HyperlinkEvent e) {}
//			
//		});
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
	}
	
	@Override
	protected void createViewAction(StructuredViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				StructuredSelection ss = (StructuredSelection) event.getSelection();
				setSelectionPO(ss.getFirstElement());
				editAdapter();
			}
		});
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionPO(ss.getFirstElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemPreview(tBar);//全额付款订单
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemNew(tBar);
		createToolItemEdit(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	private void createToolItemPreview(ToolBar bar){
		itemPreview = new ToolItem(bar, SWT.PUSH);
		itemPreview.setText("网页采购");
		itemPreview.setImage(SWTResourceCache.getImage("print"));
		itemPreview.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				previewAdapter();
			}
		});
		
	}
	
	protected void createToolItemNew(ToolBar tBar) {
		itemNew = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PO_NEW);
		itemNew.setText(Message.getString("common.new"));
		itemNew.setImage(SWTResourceCache.getImage("new"));
		itemNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				newAdapter();
			}
		});
	}

	protected void createToolItemEdit(ToolBar tBar) {
		itemEdit = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PO_EDIT);
		itemEdit.setText(Message.getString("pdm.editor"));
		itemEdit.setImage(SWTResourceCache.getImage("edit"));
		itemEdit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				editAdapter();
			}
		});
	}

	protected void createToolItemDelete(ToolBar tBar) {
		itemDelete = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_PO_DELETE);
		itemDelete.setText(Message.getString("common.delete"));
		itemDelete.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE));
		itemDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteAdapter();
			}
		});
	}
	
	public void isPaymentFullAdapter(){
		try {
			ADManager adManager = Framework.getService(ADManager.class);	
			List<ADBase> list = adManager.getEntityList(Env.getOrgRrn(), tableManager.getADTable().getObjectRrn(),
					Env.getMaxResult(), WHERE_CLAUSE, "");
			TableViewer tableViewer=(TableViewer) getViewer();
			tableViewer.setInput(list);
			tableManager.updateView(tableViewer);
		} catch (ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	protected void newAdapter() {
		String where = " 1!=1 ";
		PurchaseOrder po = new PurchaseOrder();
		po.setDeliveryAddress(BJPOSection.DELIVERY_ADDRESS.get(Env.getOrgRrn()));
		po.setOrgRrn(Env.getOrgRrn());
		po.setPaymentRule11("Y");//默认开具发票
		po.setInvoiceType(PurchaseOrder.INVOICE_TYPE_REGULAR);//发票类型默认为普通发票
		
		List<Warehouse> wareHouses =null;
		try {
			ADManager adManager = Framework.getService(ADManager.class);
			wareHouses= adManager.getEntityList(Env.getOrgRrn(), Warehouse.class,Integer.MAX_VALUE,"","");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(wareHouses!=null && wareHouses.size() >0){
			po.setWarehouseRrn(wareHouses.get(0).getObjectRrn());
		}
		BJPOLineBlockDialog cd = new BJPOLineBlockDialog(UI.getActiveShell(), this.getTableManager().getADTable(), where, po,
				getADTableOfPOLine());
		if (cd.open() == Dialog.CANCEL) {
			refreshSection();
		}
	}

	protected void editAdapter() {
		try {
			if (selectedPO != null && selectedPO.getObjectRrn() != null) {
				ADTable adTable = getADTableOfPOLine();
				ADManager adManager = Framework.getService(ADManager.class);
				selectedPO = (PurchaseOrder)adManager.getEntity(selectedPO);
				String whereClause = (" poRrn = '" + selectedPO.getObjectRrn().toString() + "' ");
				BJPOLineBlockDialog cd = new BJPOLineBlockDialog(UI.getActiveShell(), this.getTableManager().getADTable(), whereClause, selectedPO,
						adTable);
				if (cd.open() == Dialog.CANCEL) {
					refreshSection();
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at POSection : editAdapter() " + e);
		}
	}

	protected void deleteAdapter() {
		if (selectedPO != null) {
			try {
				boolean confirmDelete = UI.showConfirm(Message.getString("common.confirm_delete"));
				if (confirmDelete) {
					if (selectedPO.getObjectRrn() != null) {
						PURManager purManager = Framework.getService(PURManager.class);
						purManager.deletePO(selectedPO, Env.getUserRrn());
						this.selectedPO = null;
						refresh();
					}
				}
			} catch (Exception e1) {
				ExceptionHandlerManager.asyncHandleException(e1);
				return;
			}
		}
	}
	

	protected void refreshSection() {
		try {
			refresh();
			if (selectedPO != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedPO = (PurchaseOrder) adManager.getEntity(selectedPO);
				this.setStatusChanged(selectedPO.getDocStatus());
			}
		} catch (Exception e) {
			logger.error("Error at POSection : refreshSection() " + e);
		}
	}
	
	protected ADTable getADTableOfPOLine() {
		try {
			if (adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, TABLE_NAME);
				adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			}
			return adTable;
		} catch (Exception e) {
			logger.error("RequisitionLineDialog : initAdTableOfBom()", e);
		}
		return null;
	}

	private void setSelectionPO(Object obj) {
		if (obj instanceof PurchaseOrder) {
			selectedPO = (PurchaseOrder) obj;
			setStatusChanged(selectedPO.getDocStatus());
		} else {
			selectedPO = null;
			setStatusChanged("");
		}
	}

	protected void setStatusChanged(String status) {
		if (PurchaseOrder.STATUS_DRAFTED.equals(status)) {
			itemEdit.setEnabled(true);
			itemDelete.setEnabled(true);
		} else if (PurchaseOrder.STATUS_CLOSED.equals(status)) {
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
		} else {
			itemEdit.setEnabled(false);
			itemDelete.setEnabled(false);
		}
	}

	@Override
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new MaterialQueryDialog(UI.getActiveShell(), tableManager, this, Documentation.DOCTYPE_TPO);
			queryDialog.open();
		}
	}
	
	protected void previewAdapter() {
		String report = "bj_purchase_order_report.rptdesign";
		if (printQueryDialog != null) {
			printQueryDialog.setReportName(report);
			printQueryDialog.setVisible(true);
		} else {
			EntityTableManager tableManager = new EntityTableManager(printADTable);
			printQueryDialog =  new PrintQueryDialog(UI.getActiveShell(), tableManager, this);
			printQueryDialog.setReportName(report);
			printQueryDialog.open();
		}
	}
	
	class PrintQueryDialog extends EntityQueryDialog{
		private String reportName =null;
		public PrintQueryDialog(Shell parent,
				EntityTableManager tableManager, IRefresh refresh) {
			super(parent, tableManager, refresh);
		}
		
		@Override
		protected void okPressed() {
			if(!validateQueryKey()) return;
			fillQueryKeys();
			createWhereClause();
			setReturnCode(OK);
	        this.setVisible(false);
	        try {
	        	HashMap<String, Object> params = new HashMap<String, Object>();
	    		params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
	    		
	    		HashMap<String, String> userParams = new HashMap<String, String>();
	    		Map<String, Object> keys = printQueryDialog.getQueryKeys();
	    		for(String name : keys.keySet()){
	    			if("created".equals(name)){
	    				Object val = keys.get(name);
	    				if(val == null || String.valueOf(val).trim().length() == 0){
	    					userParams.put("REPORT_MONTH", null);
	    				}else{
	    					userParams.put("REPORT_MONTH", (String) val);
	    				}
	    			}					
	    		}
	    		String parms = convertParams(userParams);
	    		StringBuffer sf = new StringBuffer();
	    		sf.append("http://192.168.0.193:8080/ERPreport/frameset?__report=");
	    		sf.append(printQueryDialog.getReportName());
	    		sf.append("&__format=html&__resourceFolder=");
	    		sf.append(parms);
	    		Runtime.getRuntime().exec("rundll32  url.dll,FileProtocolHandler "+sf.toString() );
	    	    
//	    		PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), reportName, params, userParams);
//	    		dialog.open();
			} catch (Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
				return;
			}
		}

		public String getReportName() {
			return reportName;
		}

		public void setReportName(String reportName) {
			this.reportName = reportName;
		}
		
		public void createWhereClause() {
			LinkedHashMap<String, IField> fields = queryForm.getFields();
			String modelName = tableManager.getADTable().getModelName() + ".";
			sb = new StringBuffer("");
			
			sb.append(" 1=1 ");
					
	        for(IField f : fields.values()) {
				Object t = f.getValue();
				if (t instanceof Date) {
					Date cc = (Date)t;
					Class<?> clazz = null;
					Field objProperty = null;
					try {
						clazz = Class.forName(printADTable.getModelClass());
						if(clazz != null){
							objProperty = clazz.getDeclaredField(f.getId());
							Field[] fs = clazz.getDeclaredFields();
							assert fs.length != 0;
						}
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
					}
					if(objProperty != null){
						if(objProperty.getType().equals(String.class)){
							//如果对象的属性是String类型的
							if(cc != null) {
								sb.append(" AND ");
								sb.append(modelName);
								sb.append(f.getId());
								if(FieldType.SHORTDATE.equals(f.getFieldType())){
									sb.append(" = '");
									sb.append(I18nUtil.formatShortDate(cc));
								}else{
									sb.append(" = '");
									sb.append(I18nUtil.formatDate(cc));
								}
								sb.append("'");
							}
						}else if(objProperty.getType().equals(Date.class)){
							//如果对象的属性是Date类型的
							if(cc != null) {
								sb.append(" AND ");
								sb.append("TO_CHAR(");
								sb.append(modelName);
								sb.append(f.getId());
								if(FieldType.SHORTDATE.equals(f.getFieldType())){
									sb.append(", '" + I18nUtil.getShortDatePattern() + "') = '");
									sb.append(I18nUtil.formatShortDate(cc));
								}else{
									sb.append(", '" + I18nUtil.getDefaultDatePattern() + "') = '");
									sb.append(I18nUtil.formatDate(cc));
								}
								sb.append("'");
							}
						}
					}else{
						if(cc != null) {
							sb.append(" AND ");
							sb.append("TO_CHAR(");
							sb.append(modelName);
							sb.append(f.getId());
							if(FieldType.SHORTDATE.equals(f.getFieldType())){
								sb.append(", '" + I18nUtil.getShortDatePattern() + "') = '");
								sb.append(I18nUtil.formatShortDate(cc));
							}else{
								sb.append(", '" + I18nUtil.getDefaultDatePattern() + "') = '");
								sb.append(I18nUtil.formatDate(cc));
							}
							sb.append("'");
						}
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
				} else if(t instanceof Map){//只可能是FromToCalendarField
					Map m = (Map)t;
					Date from = (Date) m.get(FromToCalendarField.DATE_FROM);
					Date to = (Date) m.get(FromToCalendarField.DATE_TO);
					if(from != null) {
						sb.append(" AND trunc(");
						sb.append(modelName);
						sb.append(f.getId());
						sb.append(") >= TO_DATE('" + I18nUtil.formatDate(from) +"', '" + I18nUtil.getDefaultDatePattern() + "') ");
					}
					
					if(to != null){
						sb.append(" AND trunc(");
						sb.append(modelName);
						sb.append(f.getId());
						sb.append(") <= TO_DATE('" + I18nUtil.formatDate(to) + "', '" + I18nUtil.getDefaultDatePattern() + "') ");
					}
				}
	        }
	        if (getTray() != null) {
	        	AdvanceQueryTray tray = (AdvanceQueryTray)this.getTray();
	        	String advance = tray.getAdvaceWhereClause();
	        	sb.append(advance);
	        }
		}
		
	}
	
	private static String convertParams(Map<String, String> params) {
		if (params != null && !params.isEmpty()) {
			StringBuffer sb = new StringBuffer();

			for (Entry<String, String> entry : params.entrySet()) {
				sb.append("&").append(entry.getKey()); 

				if (entry.getValue() != null) {
					sb.append("=").append(entry.getValue()); 
				}
			}

			return sb.toString();
		}
		return ""; 
	}
	
	private ADTable initPrintTable(){
		try{
			String TABLE_NAME = "XZPURRequisitionLineQuery";
			ADManager adManager = Framework.getService(ADManager.class);
			printADTable = adManager.getADTable(Env.getOrgRrn(), TABLE_NAME);
		}catch(Exception e){
			
		}
		return printADTable;
	}
}

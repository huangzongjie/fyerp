package com.graly.erp.wip.mo.wms.out;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.client.BASManager;
import com.graly.erp.base.model.Material;
import com.graly.erp.inv.model.StockIn;
import com.graly.erp.wip.mo.wms.ApprovedInvoiceTableManager;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.entitymanager.views.TableViewerManager;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.model.WorkCenter;
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wip.model.Lot;
/**
 * 半成品入库
 * */
public class WmsMoLineInDialog extends ExtendDialog {
	
	Logger logger = Logger.getLogger(WmsMoLineInDialog.class);
	
	protected TableViewer viewer;
	protected final int SEARCH_OK = 1001;
	protected final int SEARCH_CANCEL = 1002;
	protected Object object;
	protected TableListManager listTableManager;
	protected TableViewerManager lotManager;
	//protected QueryForm queryForm;
	protected EntityForm queryForm;
	protected StringBuffer sb = new StringBuffer("");
	protected List<ADBase> exsitedItems;
	protected String whereClause;
	protected int mStyle = SWT.FULL_SELECTION | SWT.BORDER | SWT.CHECK;
	private int MIN_DIALOG_WIDTH=600;
	private int MIN_DIALOG_HEIGHT=200;
	private static final String TABLE_NAME_INVOICE_MOVEMENTLINE = "WIPWmsStockIn";
	private ManufactureOrderLine selectMoLine;
	private List<StockIn> stockIns = new ArrayList<StockIn>();
	Map<String,Lot> mapLot = new HashMap<String,Lot>();
	private  BigDecimal moQty;
	private Lot parentLot;
	private final static String inType="BIN";
	public WmsMoLineInDialog() {
		super();
	}
	
	public WmsMoLineInDialog(TableListManager listTableManager,
			IManagedForm managedForm, String whereClause, int style,ManufactureOrderLine moLine,Lot parentLot){
		this();
		this.listTableManager = listTableManager;
		this.whereClause = whereClause;
		this.mStyle = style;
		this.selectMoLine = moLine;
		this.moQty =  moLine.getQty();
		this.parentLot = parentLot;
		initMapLot();
	}
	
	public WmsMoLineInDialog(StructuredViewer viewer, Object object) {
		super();
		this.viewer = (CheckboxTableViewer)viewer;
		this.object = object;
	}
	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),shellSize.y));
	}
	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}
	protected Section section;
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemNew(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		section.setTextClient(tBar);
	}
	protected void createToolItemNew(ToolBar tBar) {
			ToolItem itemNew = new ToolItem(tBar, SWT.PUSH);
		itemNew.setText("发送至WMS");
		itemNew.setImage(SWTResourceCache.getImage("save"));
		itemNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				saveWmsAdapter();
			}
		});
	}

	@Override
    protected Control createDialogArea(Composite parent) {
		setTitleImage();
        setTitleInfo();        
        FormToolkit toolkit =  new FormToolkit(Display.getCurrent());
        section = toolkit.createSection(parent, Section.TITLE_BAR);
		section.setText("立体库生产入库");
		section.marginWidth = 3;
		section.marginHeight = 4;
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
		toolkit.createCompositeSeparator(section);
		createToolBar(section);
        Composite composite = (Composite) super.createDialogArea(parent);
        GridLayout gl = new GridLayout();
        gl.numColumns = 1;
        composite.setLayout(gl);
        
        Composite queryComp = new Composite(composite, SWT.NULL);
        queryComp.setLayout(new GridLayout());
        queryComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        Composite resultComp = new Composite(composite, SWT.NONE);
        resultComp.setLayout(new GridLayout());
        resultComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        Composite buttonComp = new Composite(resultComp, SWT.NONE);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalAlignment = GridData.END;
        buttonComp.setLayoutData(gd);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 0;
        buttonComp.setLayout(gridLayout);
        
        queryForm = new EntityForm(queryComp, SWT.BORDER, selectMoLine,listTableManager.getADTable(),null);
        queryForm.setLayoutData(new GridData(GridData.FILL_BOTH));
        queryForm.saveToObject();
        Button ok = createButton(buttonComp, SEARCH_OK,
        		"生成托盘", false);
        this.getShell().setDefaultButton(ok);
        Button cancel = createButton(buttonComp, SEARCH_CANCEL,
        		"添加托盘", false);
        
        createSearchTableViewer(resultComp);
        getInitSearchResult();

        ok.addSelectionListener(new SelectionListener() {
        	public void widgetSelected(SelectionEvent e) {
        		saveAdapter();
        		
        		//createWhereClause();
                //refresh(true);
        	}        	
        	public void widgetDefaultSelected(SelectionEvent e) {
        		widgetSelected(e);
        	}        	
        });
        
        cancel.addSelectionListener(new SelectionListener() {
        	public void widgetSelected(SelectionEvent e) {
        		addValue();
        		//LinkedHashMap<String, IField> fields = queryForm.getFields();
                //for(IField f : fields.values()) {
                	//f.setValue(null);
                	//f.refresh();
                ////}
        	}        	
        	public void widgetDefaultSelected(SelectionEvent e){
        		widgetSelected(e);
        	}        	
        });
        return composite;
    }
	public void saveAdapter(){
		queryForm.saveToObject();
		ManufactureOrderLine moLine = (ManufactureOrderLine) queryForm.getObject();
		genViewerValue();
			//PropertyUtil.copyProperties(getAdObject(), queryForm.getObject(), queryForm.getFields());
	}
	protected void setTitleImage() {
		setTitleImage(SWTResourceCache.getImage("search-dialog"));
	}
	
	protected void setTitleInfo() {
//		setTitle(Message.getString("common.search_Title"));
//        setMessage(Message.getString("common.keys"));
	}
	
	protected void createSearchTableViewer(Composite parent) {
        //listTableManager.setStyle(mStyle);
        FormToolkit formToolkit =  new FormToolkit(Display.getCurrent());
		//viewer =  (TableViewer) listTableManager.createViewer(parent,
		//		formToolkit);     
		ADTable adTable=null;
		try {
			adTable = getInvoiceMovementLineTable();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		lotManager = new ApprovedInvoiceTableManager(adTable,SWT.NONE );
		viewer = (TableViewer)lotManager.createViewer(parent, formToolkit);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				//setLabelCount();
				}
				});
		final Table table = viewer.getTable();//获得数据表
		table.addMouseListener(new MouseAdapter() {
			 @Override
			 public void mouseDoubleClick(MouseEvent e) {
				 int index = table.getSelectionIndex();
				 //selectAdapter(index);
			   }
			  });
	}
	
	public void genViewerValue(){
		if(selectMoLine.getQtyReserved1()==null || selectMoLine.getQtyReserved2()==null){
			UI.showError("发送数量或者每托数量为空");
			return;
		}

		if(selectMoLine.getQtyReserved2().compareTo(BigDecimal.ZERO) > 0){
			BigDecimal b1 = selectMoLine.getQtyReserved1();//传送数量
			BigDecimal b2 = selectMoLine.getQtyReserved2();//每托数量
			BigDecimal b3= b1.remainder(b2);
			System.out.println(b1.remainder(b2));
			BigDecimal i = b1.divide(b2,0,BigDecimal.ROUND_DOWN) ;
			int scale=0;
			int one =b1.divide(b2,scale,BigDecimal.ROUND_UP).intValue() ;
			int lost = b1.intValue()-(one*(b2.intValue())-b2.intValue());
			
			for(int j=0;j<one;j++){
				StockIn si =new StockIn();
				si.setOrgRrn(Env.getOrgRrn());
				si.setIsActive(true);
				if(j+1<one){
					si.setQuality(b2);
				}else{
					if(b3.compareTo(BigDecimal.ZERO)==0){
						si.setQuality(b2);
					}else{
						si.setQuality(b3);
					}
				}
				stockIns.add(si);
			}
			viewer.setInput(stockIns);
			viewer.refresh();
			//最后一个的值
		}
		
	}
	
	public void addValue(){
	StockIn si =new StockIn();
	si.setOrgRrn(Env.getOrgRrn());
	si.setIsActive(true);
	si.setQuality(selectMoLine.getQtyReserved2());
	stockIns.add(si);
	viewer.setInput(stockIns);
	}
	
	@Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
    }
	
	protected String validate() {
		return null;
	}
	
	public String getKeys() {
		return sb.toString();
	}
	
	/*
	 * 当弹出该对话框，会根据传入whereCluae进行查询得到初始查询结果，同时也是为了方便子类的重载
	 */
	protected void getInitSearchResult() {
	}
	
	protected void refresh(boolean clearFlag) {
		List<ADBase> l = new ArrayList<ADBase>();
		try {
        	ADManager manager = Framework.getService(ADManager.class);
        	long objectId = listTableManager.getADTable().getObjectRrn();
        	String sb = getKeys();
            l = manager.getEntityList(Env.getOrgRrn(), objectId, Env.getMaxResult(), getKeys(), "");
        } catch (Exception e) {
        	logger.error("Error SingleQueryDialog : refresh() " + e.getMessage(), e);
        }
		viewer.setInput(l);			
		listTableManager.updateView(viewer);
	}

	public void setObject(Object object) {
		this.object = object;
	}
	
	protected void  createWhereClause() {
		String modelName = listTableManager.getADTable().getModelName() + ".";
		sb = new StringBuffer("");
		if(whereClause != null && !"".equals(whereClause)) {
			sb.append(" ");
			sb.append(whereClause);
			sb.append(" AND ");
		}
		sb.append(" 1=1 ");
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
		}
		
	}
	
	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
	}
	protected void cancelPressed() {
		if(selectMoLine!=null){
			selectMoLine.setQtyReserved1(null);
			selectMoLine.setQtyReserved2(null);
		}
		super.cancelPressed();
	}
	protected ADTable getInvoiceMovementLineTable() throws Exception {
//		try {
		ADTable movementLineTable=null;
			if (movementLineTable == null) {
				ADManager adManager = Framework.getService(ADManager.class);
				movementLineTable = adManager.getADTable(0L, TABLE_NAME_INVOICE_MOVEMENTLINE);
			}
			return movementLineTable;
//		} catch (Exception e1) {
//			ExceptionHandlerManager.asyncHandleException(e1);
//		}
	}
	public void saveWmsAdapter(){
		queryForm.saveToObject();
		Date date= Env.getSysDate();
		List<StockIn> sis = new ArrayList<StockIn>();
		if(selectMoLine.getMasterMoId()==null || "".equals(selectMoLine.getMasterMoId())){
			UI.showError("工作令或者物料编号为空");
			return;
		}
		
		if(selectMoLine.getWorkCenterId()==null || "".equals(selectMoLine.getWorkCenterId())){
			UI.showError("工作车间为空");
			return;
		}

		
		try {
		ADManager adManager = Framework.getService(ADManager.class);
		Material material = new Material();
		material.setObjectRrn(selectMoLine.getMaterialRrn());
		material = (Material) adManager.getEntity(material);
		selectMoLine.setMaterial(material);
		if(selectMoLine.getMaterial()==null ||"".equals(selectMoLine.getMaterial()) ){
		UI.showError("物料编号为空");
		return;
		}
		
		if(stockIns!=null&& stockIns.size()>0){
			BigDecimal Qty = BigDecimal.ZERO;
			for(StockIn si : stockIns){
				if(si.getTrayId()!=null){
					if(si.getTrayId().length()!=6){
						UI.showError("条码数量不能小于6位");
						return;
					}
				}
				
				if(si.getQuality()!=null&&si.getQuality().compareTo(BigDecimal.ZERO)>0){
				}else{
					UI.showError("数量不能小于0:");
					return;
				}
					if(si.getBatch()==null || !validateLot(si.getBatch())){
						UI.showError("该工作令不存在该批次:"+si.getBatch());
						return;
					}
					StockIn wmsSi = new StockIn();
					wmsSi.setOrgRrn(Env.getOrgRrn());
					wmsSi.setIsActive(true);
					wmsSi.setReceiptType(inType);
					wmsSi.setReceiptId(selectMoLine.getReceiptId());
					wmsSi.setReceiptTime(new Date());
					wmsSi.setMoId(selectMoLine.getMasterMoId());
					wmsSi.setErpWrite(1L);
					wmsSi.setErpWriteTime(date);
					wmsSi.setMaterialCode(selectMoLine.getMaterialId());
					wmsSi.setQtyProduct(selectMoLine.getQtyReserved1());
					wmsSi.setOrderId(selectMoLine.getOrderId());
					wmsSi.setBatch(si.getBatch());
					wmsSi.setQuality(si.getQuality());
					wmsSi.setTrayId(si.getTrayId());
					wmsSi.setTrayQty(selectMoLine.getQtyReserved2());
					wmsSi.setCustomer(selectMoLine.getCustomerName());
					wmsSi.setMaterialName(selectMoLine.getMaterialName());
					wmsSi.setSupplierName(selectMoLine.getWorkCenterId());//立体库没有供应商不行因此用车间代替
					wmsSi.setWorkcenterId(selectMoLine.getWorkCenterId());
					wmsSi.setWmsRead(0L);
					wmsSi.setMoQty(moQty);
					Qty =Qty.add(si.getQuality());
					sis.add(wmsSi);
			}
			
//			String whereClause = "receiptId ='"+selectMoLine.getMasterMoId()+"' and wmsRead<=3 and receiptType='BIN' AND materialCode='"+selectMoLine.getMaterialId()+"'";
//			List<StockIn> stocks = adManager.getEntityList(Env.getOrgRrn(),StockIn.class,Integer.MAX_VALUE,whereClause,null);
//			BigDecimal wmsHasQty = BigDecimal.ZERO;
//			for(StockIn stock : stocks){
//				wmsHasQty =wmsHasQty.add(stock.getQuality()!=null?stock.getQuality():BigDecimal.ZERO);
//			}
//			BigDecimal canUseQty = moQty.subtract(wmsHasQty);
//			if(Qty.compareTo(canUseQty)>0){
//				UI.showError("发送数量已经超过处理数量");
//				return;
//			}
			
		}else{
			UI.showError("明细不能为空");
			return;
		}
		for(StockIn si : sis){
			adManager.saveEntity(si, Env.getUserRrn());
		}
		UI.showInfo("保存成功");
		this.cancelPressed();
		} catch (Exception e) {
			e.printStackTrace();
			ExceptionHandlerManager.asyncHandleException(e);
		}
		
	}
	public boolean validateLot(String lotId){
//		if(mapLot.get(lotId)!=null){
//			return true;
//		}
//		return false;
		return true;
	}

	public ManufactureOrderLine getSelectMoLine() {
		return selectMoLine;
	}

	public void setSelectMoLine(ManufactureOrderLine selectMoLine) {
		this.selectMoLine = selectMoLine;
	}

	public BigDecimal getMoQty() {
		return moQty;
	}

	public void setMoQty(BigDecimal moQty) {
		this.moQty = moQty;
	}

	public Lot getParentLot() {
		return parentLot;
	}

	public void setParentLot(Lot parentLot) {
		this.parentLot = parentLot;
	}
	
	public void initMapLot(){
		BASManager basManager;
		try {
			basManager = Framework.getService(BASManager.class);
			StringBuffer moCode = new StringBuffer("");
			moCode.append(basManager.generateCodePrefix(Env.getOrgRrn(),inType));
			moCode.append(basManager.generateCodeSuffix(Env.getOrgRrn(), inType, Env.getSysDate()));
			selectMoLine.setReceiptId(moCode.toString());
		} catch (Exception e) {
			e.printStackTrace();
			ExceptionHandlerManager.asyncHandleException(e);
		}
		
	}
}
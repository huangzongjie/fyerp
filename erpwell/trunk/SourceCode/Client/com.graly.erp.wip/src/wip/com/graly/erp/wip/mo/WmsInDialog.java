package com.graly.erp.wip.mo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import org.eclipse.swt.widgets.AuthorityToolItem;
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

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.LotStorage;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.StockIn;
import com.graly.erp.wip.mo.wms.ApprovedInvoiceTableManager;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.entitymanager.query.QueryForm;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.entitymanager.views.TableViewerManager;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wip.model.Lot;

public class WmsInDialog extends ExtendDialog {
	
	Logger logger = Logger.getLogger(WmsInDialog.class);
	
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
	private ManufactureOrder selectMo;
	private List<StockIn> stockIns = new ArrayList<StockIn>();
	Map<String,Lot> mapLot = new HashMap<String,Lot>();
	private  BigDecimal moQty;
	public WmsInDialog() {
		super();
	}
	
	public WmsInDialog(TableListManager listTableManager,
			IManagedForm managedForm, String whereClause, int style,ManufactureOrder mo){
		this();
		this.listTableManager = listTableManager;
		this.whereClause = whereClause;
		this.mStyle = style;
		this.selectMo = mo;
		this.moQty =  mo.getQtyProduct();
		initMapLot();
	}
	
	public WmsInDialog(StructuredViewer viewer, Object object) {
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
        
        queryForm = new EntityForm(queryComp, SWT.BORDER, selectMo,listTableManager.getADTable(),null);
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
		ManufactureOrder mo = (ManufactureOrder) queryForm.getObject();
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
//		MovementLine li =new MovementLine();
//		li.setObjectRrn(1L);
//		li.setIsActive(true);
//		li.setMovementId("aaaa");
//		molines.add(li);
//		viewer.setInput(molines);
	}
	
	public void genViewerValue(){
		
		if(selectMo.getQtyIn()!=null&& selectMo.getQtyIn().compareTo(BigDecimal.ZERO) > 0){
			BigDecimal b1 = selectMo.getQtyProduct();
			BigDecimal b2 = selectMo.getQtyIn();
			BigDecimal b3= b1.remainder(b2);
			System.out.println(b1.remainder(b2));
			BigDecimal i = selectMo.getQtyProduct().divide(selectMo.getQtyIn(),0,BigDecimal.ROUND_DOWN) ;
			int scale=0;
			int one =selectMo.getQtyProduct().divide(selectMo.getQtyIn(),scale,BigDecimal.ROUND_UP).intValue() ;
			int lost = selectMo.getQtyProduct().intValue()-(one*(selectMo.getQtyIn().intValue())-selectMo.getQtyIn().intValue());
/*			for(int j=0;j<one;j++){
				MovementLine li =new MovementLine();
				li.setObjectRrn(Long.parseLong(j+""));
				li.setIsActive(true);
				if(j+1<one){
					li.setMovementId(selectMo.getQtyIn().toString());
				}else{
					if(b3.compareTo(BigDecimal.ZERO)==0){
						li.setMovementId(selectMo.getQtyIn().toString());
					}else{
						li.setMaterialId(b3.toString());
					}
				}
				molines.add(li);
			}*/
			
			for(int j=0;j<one;j++){
				StockIn si =new StockIn();
				si.setOrgRrn(Env.getOrgRrn());
				si.setIsActive(true);
				if(j+1<one){
					si.setQuality(selectMo.getQtyIn());
				}else{
					if(b3.compareTo(BigDecimal.ZERO)==0){
						si.setQuality(selectMo.getQtyIn());
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
	si.setQuality(selectMo.getQtyIn());
	stockIns.add(si);
	viewer.setInput(stockIns);
//		molines.add(li);
//		viewer.setInput(molines);
//		
	}
	
	@Override
    protected void createButtonsForButtonBar(Composite parent) {
//        createButton(parent, IDialogConstants.OK_ID,
//        		IDialogConstants.OK_LABEL, false);
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
//		if (object instanceof List) {
//			exsitedItems = (List)object;
//			if (exsitedItems != null) {
//				l.removeAll(exsitedItems);
//			}
//		}
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
//		if(buttonId == IDialogConstants.OK_ID) {
//			Object[] os = viewer.getCheckedElements();
//			if(os.length != 0) {
//				for(Object o : os) {
//					ADBase adBase = (ADBase)o;
//					selectedItems.add(adBase);
//					
//				}
//			}
//		}
		super.buttonPressed(buttonId);
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
		Date date= Env.getSysDate();
		List<StockIn> sis = new ArrayList<StockIn>();
		Map<String,BigDecimal> lotQtyMap = new HashMap<String,BigDecimal>();
		try {
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
					wmsSi.setReceiptType("WIN");
					wmsSi.setReceiptId(selectMo.getDocId());
					wmsSi.setReceiptTime(new Date());
					wmsSi.setMoId(selectMo.getDocId());
					wmsSi.setErpWrite(1L);
					wmsSi.setErpWriteTime(date);
					wmsSi.setMaterialCode(selectMo.getMaterialId());
					wmsSi.setQtyProduct(selectMo.getQtyProduct());
					wmsSi.setOrderId(selectMo.getOrderId());
					wmsSi.setBatch(si.getBatch());
					wmsSi.setQuality(si.getQuality());
					wmsSi.setTrayId(si.getTrayId());
					wmsSi.setTrayQty(selectMo.getQtyIn());
					wmsSi.setCustomer(selectMo.getCustomerName());
					wmsSi.setMaterialName(selectMo.getMaterialName());
					wmsSi.setWorkcenterId(selectMo.getWorkCenterId());
					wmsSi.setWmsRead(0L);
					wmsSi.setMoQty(moQty);
					
					if(lotQtyMap.containsKey(wmsSi.getBatch())){
						BigDecimal lotQty = lotQtyMap.get(wmsSi.getBatch()).add(wmsSi.getQuality());
						lotQtyMap.put(wmsSi.getBatch(), lotQty);
					}else{
						lotQtyMap.put(wmsSi.getBatch(), wmsSi.getQuality());
					}
					
					Qty =Qty.add(si.getQuality());
					sis.add(wmsSi);
			}
			INVManager invManager = Framework.getService(INVManager.class);
			Iterator iter = lotQtyMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String) entry.getKey();
				BigDecimal val = (BigDecimal) entry.getValue();
				Lot lot = invManager.getLotByLotId(Env.getOrgRrn(), key);
				if(lot.getObjectRrn()==null){
					UI.showError("批次不存在");
					return;
				}
				LotStorage lotStorage = invManager.getLotStorage(Env.getOrgRrn(), lot.getObjectRrn(), 151046L, Env.getUserRrn());
				if(val.compareTo(lotStorage.getQtyOnhand())>0){
					UI.showError("输入的批次库存大于实际批次库存"+key);
					return;
				}
			}
			
			ADManager adManager = Framework.getService(ADManager.class);
			String whereClause = "receiptId ='"+selectMo.getDocId()+"' and wmsRead<=3 and receiptType='WIN' AND materialCode='"+selectMo.getMaterialId()+"'";
			List<StockIn> stocks = adManager.getEntityList(Env.getOrgRrn(),StockIn.class,Integer.MAX_VALUE,whereClause,null);
			BigDecimal wmsHasQty = BigDecimal.ZERO;
			for(StockIn stock : stocks){
				wmsHasQty =wmsHasQty.add(stock.getQuality()!=null?stock.getQuality():BigDecimal.ZERO);
			}
			BigDecimal canUseQty = moQty.subtract(wmsHasQty);
			if(Qty.compareTo(canUseQty)>0){
				UI.showError("发送数量已经超过处理数量");
				return;
			}
			
		}else{
			UI.showError("明细不能为空");
			return;
//			StockIn si = new StockIn();
//			si.setOrgRrn(Env.getOrgRrn());
//			si.setIsActive(true);
//			si.setMoId(selectMo.getDocId());
//			si.setReceiptType("WIN");
//			si.setReceiptId(selectMo.getDocId());
//			si.setReceiptTime(new Date());
//			si.setErpWrite(1L);
//			si.setErpWriteTime(date);
//			si.setMaterialCode(selectMo.getMaterialId());
//			si.setMaterialName(selectMo.getMaterialName());
//			si.setQtyProduct(selectMo.getQtyProduct());
//			si.setOrderId(selectMo.getOrderId());
//			si.setWorkcenterId(selectMo.getWorkCenterId());
//			si.setCustomer(selectMo.getCustomerName());
//			si.setWmsRead(0L);
//			si.setMoQty(moQty);
//			sis.add(si);
//			si.setMaterialCode(selectMo.GETM);
			
		}
		ADManager adManager = Framework.getService(ADManager.class);
	
		for(StockIn si : sis){
			adManager.saveEntity(si, Env.getUserRrn());
		}
		UI.showInfo("保存成功");
		} catch (Exception e) {
			e.printStackTrace();
			ExceptionHandlerManager.asyncHandleException(e);
		}
		
	}
	public boolean validateLot(String lotId){
		if(mapLot.get(lotId)!=null){
			return true;
		}
		return false;
		
	}
	
	public void initMapLot(){
		WipManager wipManager;
		try {
			wipManager = Framework.getService(WipManager.class);
			List<Lot> lots = wipManager.getAvailableLot4In(selectMo.getObjectRrn());
			for(Lot lot: lots){
				mapLot.put(lot.getLotId(), lot);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ExceptionHandlerManager.asyncHandleException(e);
		}
		
	}
	
	
}
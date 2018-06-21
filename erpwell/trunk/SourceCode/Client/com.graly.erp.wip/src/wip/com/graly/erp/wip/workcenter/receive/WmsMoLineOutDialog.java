package com.graly.erp.wip.workcenter.receive;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.client.BASManager;
import com.graly.erp.inv.model.StockOut;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
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

/**
 * 半成品出库
 * */
public class WmsMoLineOutDialog extends ExtendDialog {
	
	Logger logger = Logger.getLogger(WmsMoLineOutDialog.class);
	
	protected TableViewer viewer;
	protected final int SEARCH_OK = 1001;
	protected final int SEARCH_CANCEL = 1002;
	protected Object object;
	protected TableListManager listTableManager;
	protected TableViewerManager lotManager;
	protected EntityForm queryForm;
	protected StringBuffer sb = new StringBuffer("");
	protected List<ADBase> exsitedItems;
	protected String whereClause;
	protected int mStyle = SWT.FULL_SELECTION | SWT.BORDER | SWT.CHECK;
	private int MIN_DIALOG_WIDTH=600;
	private int MIN_DIALOG_HEIGHT=200;
	private StockOut out;
	private  BigDecimal moQty;
	private final static String outType="BOU";
	public WmsMoLineOutDialog() {
		super();
	}
	
	public WmsMoLineOutDialog(TableListManager listTableManager,
			IManagedForm managedForm, String whereClause, int style,StockOut out){
		this();
		this.listTableManager = listTableManager;
		this.whereClause = whereClause;
		this.mStyle = style;
		this.out = out;
		initMapLot();
	}
	
	public WmsMoLineOutDialog(StructuredViewer viewer, Object object) {
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
        
        queryForm = new EntityForm(queryComp, SWT.BORDER, out,listTableManager.getADTable(),null);
        queryForm.setLayoutData(new GridData(GridData.FILL_BOTH));
        queryForm.saveToObject();
      
        return composite;
    }
	protected void setTitleImage() {
		setTitleImage(SWTResourceCache.getImage("search-dialog"));
	}
	
	protected void setTitleInfo() {
//		setTitle(Message.getString("common.search_Title"));
//        setMessage(Message.getString("common.keys"));
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
		super.buttonPressed(buttonId);
	}
	public void saveWmsAdapter(){
		try {
			queryForm.saveToObject();
		ADManager adManager = Framework.getService(ADManager.class);
		adManager.saveEntity(out, Env.getUserRrn());
		UI.showInfo("保存成功");
//		StockOut outNew = new StockOut();
//		outNew.setOrgRrn(Env.getOrgRrn());
//		outNew.setIsActive(true);
//		this.cancelPressed();
//		queryForm.setObject(outNew);
//		queryForm.refresh();
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
	
	public void initMapLot(){
		BASManager basManager;
		try {
			basManager = Framework.getService(BASManager.class);
			StringBuffer moCode = new StringBuffer("");
			Date date = Env.getSysDate();
			moCode.append(basManager.generateCodePrefix(Env.getOrgRrn(),outType));
			moCode.append(basManager.generateCodeSuffix(Env.getOrgRrn(), outType, date));
			out.setReceiptId(moCode.toString());
			out.setErpWriteTime(date);
			out.setCreated(date);
			out.setUpdated(date);
			out.setCreatedBy(Env.getUserRrn());
			out.setUpdatedBy(Env.getUserRrn());
			out.setWmsRead(0L);
			out.setErpWrite(1L);
			out.setReceiptType(outType);
			out.setReceiptTime(date);
		} catch (Exception e) {
			e.printStackTrace();
			ExceptionHandlerManager.asyncHandleException(e);
		}
		
	}

	public StockOut getOut() {
		return out;
	}

	public void setOut(StockOut out) {
		this.out = out;
	}
}
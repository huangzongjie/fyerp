package com.graly.erp.inv.transfer;

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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.query.QueryForm;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.runtime.Framework;

public class InvQueryDialog extends ExtendDialog {
	
	Logger logger = Logger.getLogger(InvQueryDialog.class);
	
	protected TableViewer viewer;
	protected final int SEARCH_OK = 1001;
	protected final int SEARCH_CANCEL = 1002;
	protected Object object;
	protected TableListManager listTableManager;
	protected QueryForm queryForm;
	protected StringBuffer sb = new StringBuffer("");
	protected List<ADBase> exsitedItems;
	protected String whereClause;
	protected int mStyle = SWT.FULL_SELECTION | SWT.BORDER | SWT.CHECK;
	private int MIN_DIALOG_WIDTH=600;
	private int MIN_DIALOG_HEIGHT=200;
	private Label label;
	public InvQueryDialog() {
		super();
	}
	
	public InvQueryDialog(TableListManager listTableManager,
			IManagedForm managedForm, String whereClause, int style){
		this();
		this.listTableManager = listTableManager;
		this.whereClause = whereClause;
		this.mStyle = style;
	}
	
	public InvQueryDialog(StructuredViewer viewer, Object object) {
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
	@Override
    protected Control createDialogArea(Composite parent) {
		setTitleImage();
        setTitleInfo();        
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
        
        queryForm = new InvQueryForm(queryComp, SWT.BORDER, listTableManager.getADTable());
        queryForm.setLayoutData(new GridData(GridData.FILL_BOTH));

        Button ok = createButton(buttonComp, SEARCH_OK,
        		Message.getString("common.search"), false);
        this.getShell().setDefaultButton(ok);
        Button cancel = createButton(buttonComp, SEARCH_CANCEL,
        		Message.getString("common.clean"), false);
        
        createSearchTableViewer(resultComp);
        getInitSearchResult();

        ok.addSelectionListener(new SelectionListener() {
        	public void widgetSelected(SelectionEvent e) {
        		createWhereClause();
                refresh(true);
        	}        	
        	public void widgetDefaultSelected(SelectionEvent e) {
        		widgetSelected(e);
        	}        	
        });
        
        cancel.addSelectionListener(new SelectionListener() {
        	public void widgetSelected(SelectionEvent e) {
        		LinkedHashMap<String, IField> fields = queryForm.getFields();
                for(IField f : fields.values()) {
                	f.setValue(null);
                	f.refresh();
                }
        	}        	
        	public void widgetDefaultSelected(SelectionEvent e){
        		widgetSelected(e);
        	}        	
        });

        label = new Label(resultComp, SWT.NONE);
        label.setText("                                        ");
        
        return composite;
    }
	
	protected void setTitleImage() {
		setTitleImage(SWTResourceCache.getImage("search-dialog"));
	}
	
	protected void setTitleInfo() {
		setTitle(Message.getString("common.search_Title"));
        setMessage(Message.getString("common.keys"));
	}
	
	protected void createSearchTableViewer(Composite parent) {
        listTableManager.setStyle(mStyle);
        FormToolkit formToolkit =  new FormToolkit(Display.getCurrent());
		viewer =  (TableViewer) listTableManager.createViewer(parent,
				formToolkit);        
	}

	@Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID,
        		IDialogConstants.OK_LABEL, false);
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
            createSectionDesc(l);
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
	
	protected void createSectionDesc(List<ADBase> adBases){
		try{ 
			String text = Message.getString("common.totalshow");
			long count = adBases.size();
			if (count > Env.getMaxResult()) {
				text = String.format(text, String.valueOf(count), String.valueOf(Env.getMaxResult()));
			} else {
				text = String.format(text, String.valueOf(count), String.valueOf(count));
			}
			label.setText("  " + text);
		} catch (Exception e){
			logger.error("MasterSection : createSectionDesc ", e);
		}
	}

}
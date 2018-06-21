package com.graly.erp.wip.workcenter.schedule.purchase2;

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

public class PurchaseCompareQueryDialog extends ExtendDialog {
	
	Logger logger = Logger.getLogger(PurchaseCompareQueryDialog.class);
	
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
	protected TableListManager listTableManager2;
	protected TableViewer viewer2;
	public PurchaseCompareQueryDialog() {
		super();
	}
	
	public PurchaseCompareQueryDialog(TableListManager listTableManager,
			IManagedForm managedForm, String whereClause, int style,TableListManager listTableManager2){
		this();
		this.listTableManager = listTableManager;
		this.whereClause = whereClause;
		this.mStyle = style;
		this.listTableManager2 = listTableManager2;
	}
	
	public PurchaseCompareQueryDialog(StructuredViewer viewer, Object object) {
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
//        
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
        
//        queryForm = new QueryForm(queryComp, SWT.BORDER, listTableManager.getADTable());
//        queryForm.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        createSearchTableViewer2( queryComp);
        createSearchTableViewer(resultComp);
        getInitSearchResult();

       
        
        return composite;
    }
	
	protected void createSearchTableViewer2(Composite parent) {
		listTableManager2.setStyle(mStyle);
        FormToolkit formToolkit =  new FormToolkit(Display.getCurrent());
        viewer2 =  (TableViewer) listTableManager2.createViewer(parent,
				formToolkit);        
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
        createButton(parent, IDialogConstants.CANCEL_ID,
                IDialogConstants.CANCEL_LABEL, false);
    }
	
	protected void getInitSearchResult() {
		refresh(true);
	}
	
	protected void refresh(boolean clearFlag) {
		List<ADBase> l = new ArrayList<ADBase>();
		try {
        	ADManager manager = Framework.getService(ADManager.class);
        	long objectId = listTableManager.getADTable().getObjectRrn();
            l = manager.getEntityList(Env.getOrgRrn(), objectId, Env.getMaxResult(), "1=1", "");
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
		refresh2(true);
	}
	
	protected void refresh2(boolean clearFlag) {
		List<ADBase> l = new ArrayList<ADBase>();
		try {
        	ADManager manager = Framework.getService(ADManager.class);
        	long objectId = listTableManager2.getADTable().getObjectRrn();
            l = manager.getEntityList(Env.getOrgRrn(), objectId, Env.getMaxResult(), "1=1", "");
        } catch (Exception e) {
        	logger.error("Error SingleQueryDialog : refresh() " + e.getMessage(), e);
        }
		viewer2.setInput(l);			
		listTableManager2.updateView(viewer);
	}

	
	
	protected void createSectionDesc(List<ADBase> adBases){
 
	}

}
package com.graly.erp.wip.workcenter.schedule.pmczzj;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.erp.wip.model.PmcZzjResult;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;



public class PmcZzjMaterialSection extends MasterSection {
	private static final Logger logger = Logger.getLogger(PmcZzjMaterialSection.class);
	protected ToolItem itemBarcode;
//	protected RepScheResult2 selectedLine;
	protected ToolItem itemRunTotal;//领用单
	protected ToolItem itemPreview;
	protected ToolItem itemNote;
	protected ToolItem itemCompare;
	protected Label labe;
	protected ToolItem itemDetail;
	private PmcZzjResult selectedRepScheResult;
	protected EntityQueryDialog queryDialog;
	public PmcZzjMaterialSection(EntityTableManager tableManager,PmcZzjMainSection mainSection) {
		super(tableManager);
//		setWhereClause("1<>1");//刚打开时显示空内容
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemDetail(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemSearch(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemDetail(ToolBar tBar) {
		itemDetail = new ToolItem(tBar, SWT.PUSH);
		itemDetail.setText("明细");
		itemDetail.setImage(SWTResourceCache.getImage("export"));
		itemDetail.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				detailAdapter();
			}
		});
	}
	
	
	@Override
	protected void createViewAction(StructuredViewer viewer){
	    viewer.addDoubleClickListener(new IDoubleClickListener() {
	    	public void doubleClick(DoubleClickEvent event) {
	    		StructuredSelection ss = (StructuredSelection) event.getSelection();
	    		setSelectionRepScheResultl(ss.getFirstElement());
	    	}
	    });
	    viewer.addSelectionChangedListener(new ISelectionChangedListener(){
	    	public void selectionChanged(SelectionChangedEvent event) {
				try{
					StructuredSelection ss = (StructuredSelection) event.getSelection();
					setSelectionRepScheResultl(ss.getFirstElement());
				} catch (Exception e){
					e.printStackTrace();
				}
			}
	    });
	}
	
//	private void setSelectionLine(Object obj) {
//		if(obj instanceof RepScheResult2) {
//			selectedLine = (RepScheResult2)obj;
//		} else {
//			selectedLine = null;
//		}
//	}
	
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new EntityQueryDialog(UI.getActiveShell(), tableManager, this);
			queryDialog.open();
		}
	}
	
	protected void refreshSection() {
		refresh();
	}
	@Override
	protected void createSectionTitle(Composite client) {
	}
	
	protected void detailAdapter() {
		try {
			PmcZzjDetailDialog dialog = new PmcZzjDetailDialog();
			dialog.open();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	
	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		super.refresh();
	}
	
	 
	protected void setSelectionRepScheResultl(Object obj) {
		if(obj instanceof PmcZzjResult) {
			selectedRepScheResult = (PmcZzjResult)obj;
		} else {
			selectedRepScheResult = null;
		}
	}
}

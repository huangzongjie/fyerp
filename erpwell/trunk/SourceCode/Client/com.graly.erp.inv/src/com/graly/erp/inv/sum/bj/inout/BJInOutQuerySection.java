package com.graly.erp.inv.sum.bj.inout;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.inv.material.EntityQueryDialog4WC;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class BJInOutQuerySection extends MasterSection {
private static final Logger logger = Logger.getLogger(BJInOutQuerySection.class);
	protected ToolItem itemView;

	public BJInOutQuerySection(EntityTableManager tableManager) {
		super(tableManager);
		setWhereClause(" 1 <> 1");
	}
	
	protected ADTable getADTableOfRequisition(String tableName) {
		ADTable adTable = null;
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
			logger.error("LotConsumeQuerySection : getADTableOfRequisition()", e);
		}
		return null;
	}
	
	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSearch(tBar);
		createToolItemExport(tBar);
		createToolItemView(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	@Override
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new EntityQueryDialog4WC(UI.getActiveShell(), tableManager, this);
			queryDialog.open();
		}
	}
	
	private void createToolItemView(ToolBar tBar) {
		itemView = new ToolItem(tBar, SWT.PUSH);
		itemView.setText(Message.getString("common.print"));
		itemView.setImage(SWTResourceCache.getImage("preview"));
		itemView.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				viewAdapter();
			}
		});
	}

	protected void viewAdapter() {
		try {
			String report = "monthInventory_report.rptdesign";

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();
			Map<String, Object> keys = queryDialog.getQueryKeys();
			for(String name : keys.keySet()){
				if("materialRrn".equals(name)){
					Object val = keys.get(name);
					if(val == null || String.valueOf(val).trim().length() == 0){
						userParams.put("MATERIAL_RRN", null);
					}else{
						userParams.put("MATERIAL_RRN", String.valueOf(val));
					}
				}else if("warehouseRrn".equals(name)){
					Object val = keys.get(name);
					if(val == null || String.valueOf(val).trim().length() == 0){
						userParams.put("WAREHOUSE_RRN", null);
					}else{
						userParams.put("WAREHOUSE_RRN", String.valueOf(val));
					}
				}else if("reportMonth".equals(name)){
					Object val = keys.get(name);
					if(val == null || String.valueOf(val).trim().length() == 0){
						userParams.put("REPORT_MONTH", null);
					}else{
						userParams.put("REPORT_MONTH", (String) val);
					}
				}					
			}
				
			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
			dialog.open();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
}

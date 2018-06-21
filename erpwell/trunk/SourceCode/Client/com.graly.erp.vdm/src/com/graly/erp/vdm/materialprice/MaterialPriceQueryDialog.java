package com.graly.erp.vdm.materialprice;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;

import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.pur.model.Requisition;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.editor.EntityEditor;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.editor.SectionEntryPage;
import com.graly.framework.base.entitymanager.query.AdvanceQueryTray;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.entitymanager.query.QueryForm;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class MaterialPriceQueryDialog extends ExtendDialog {
	private InnerQueryDialog queryDialog;
	private EntityTableManager tableManager;
	
	public MaterialPriceQueryDialog() {
		super();
		queryDialog = new InnerQueryDialog(UI.getActiveShell());
	}	
	
	@Override
	public void setTableId(String tableId) {
		super.setTableId(tableId);
		if(queryDialog.getTableManager() == null){
			ADManager manager;
			try {
				manager = Framework.getService(ADManager.class);
				tableManager = new EntityTableManager(manager.getADTableDeep(Long.valueOf(tableId)));
				queryDialog.setTableManager(tableManager);
			} catch (Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
			}

		}
	}

	
	@Override
	public int open() {
		return queryDialog.open();
	}

	class InnerQueryDialog extends EntityQueryDialog{
		public InnerQueryDialog(Shell parent) {
			super(parent);
		}	
		
		@Override
		protected void createDialogForm(Composite composite) {
			queryForm = new InnerQueryForm(composite, SWT.NONE, tableManager.getADTable());
	        queryForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		}
		
		public Map getSearchKeyVals(){
			LinkedHashMap<String, IField> fields = queryForm.getFields();
			Map vals = new HashMap();
	        for(IField f : fields.values()) {
	        	vals.put(f.getId(),f.getValue());
	        }
	        return vals;
		}		
		
		@Override
	    protected void okPressed() {
			try {
				String report = "material_price_chart.rptdesign";

				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
				
				HashMap<String, String> userParams = new HashMap<String, String>();
				
				Map vals = getSearchKeyVals();
				
				Long materialRrn = null;
				Long vendorRrn = null;
				if(vals.get("materialRrn") != null)
					materialRrn = Long.valueOf((String) vals.get("materialRrn"));
				
				if(vals.get("vendorRrn") != null){
					userParams.put("VENDOR_RRN", (String) vals.get("vendorRrn"));
				}
				
				if(materialRrn != null){
					userParams.put("MATERIAL_RRN", String.valueOf(materialRrn));
		
					Map approvedDate = null;
					approvedDate = (Map) vals.get("approvedDate");//FromToCalendarField
					Date fromDate = null;
					Date toDate = null;
					if(approvedDate != null){
						fromDate = (Date) approvedDate.get(FromToCalendarField.DATE_FROM);
						toDate = (Date) approvedDate.get(FromToCalendarField.DATE_TO);
					}
					if(fromDate != null){
						userParams.put("FROM_DATE", I18nUtil.formatDate(fromDate));
					}
					
					if(toDate != null){
						userParams.put("TO_DATE", I18nUtil.formatDate(toDate));
					}
					
					PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
					dialog.open();
				}
			} catch (Exception e) {
				ExceptionHandlerManager.asyncHandleException(e);
				return;
			}		
	    }		

		public EntityTableManager getTableManager(){
			return super.tableManager;
		}
		
		public void setTableManager(EntityTableManager tableManager){
			super.tableManager = tableManager;
		}
	}
	
	class InnerQueryForm extends QueryForm{

		public InnerQueryForm(Composite parent, int style, ADTable table) {
			super(parent, style, table);
		}
		
		@Override
		public FromToCalendarField createFromToCalendarField(String id,
				String label) {
			Date now = Env.getSysDate();
			Calendar c = Calendar.getInstance();
		    c.setTime(now);   //设置当前日期
		    c.add(Calendar.YEAR, -1); //年份减1
		    Date lastYear = c.getTime(); //结果
			return super.createFromToCalendarField(id, label, lastYear, now);
		}
	}
}

package com.graly.erp.inv.pricecurve;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.erp.pur.client.PURManager;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.entitymanager.query.QueryForm;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class POPriceQueryDialog extends ExtendDialog {
	private InnerQueryDialog queryDialog;
	private EntityTableManager tableManager;
	
	public POPriceQueryDialog() {
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
				String report = "po_unitprice_chart.rptdesign";
				
				HashMap<String, Object> params = new HashMap<String, Object>();
				params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
				HashMap<String, String> userParams = new HashMap<String, String>();
				
				Map vals = getSearchKeyVals();
				
				Long materialRrn = null;
				if(vals.get("materialRrn") != null)
					materialRrn = Long.valueOf((String) vals.get("materialRrn"));

				PURManager purManager = Framework.getService(PURManager.class);
				BigDecimal polrrn = purManager.getPoLineByMaxObject(Env.getOrgRrn(),materialRrn);
				if(polrrn == null){
					UI.showWarning(Message.getString("common.choose_one_record"));
					return;
				}
				userParams.put("POL_OBJECT_RRN", String.valueOf(polrrn));
				userParams.put("POL_USER_RRN", String.valueOf(Env.getUserRrn()));

				PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
				dialog.open();
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
		
	}
}

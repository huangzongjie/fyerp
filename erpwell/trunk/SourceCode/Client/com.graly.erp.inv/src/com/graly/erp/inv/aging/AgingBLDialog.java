package com.graly.erp.inv.aging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;

import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.util.UI;

public class AgingBLDialog extends EntityQueryDialog {

	public AgingBLDialog(Shell parent, EntityTableManager tableManager, IRefresh irefresh) {
		super(parent, tableManager, irefresh);
	}
	@Override
    protected void okPressed() {
		setErrorMessage(null);
		if(queryForm.validate()) {
			fillQueryKeys();
			createWhereClause();
			this.setVisible(false);
			
			String report = "aging_bl.rptdesign";
		
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();
			
			Map<String, Object> keys = getQueryKeys();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String from =null;
			String to =null;
			String MATERIAL_ID=null;
			for(String name : keys.keySet()){
				if("approvedDate".equals(name)){
					Map approvedDate = (Map) keys.get(name);
					Date fromDate = null;
					Date toDate = null;
					if(approvedDate != null){
						fromDate = (Date) approvedDate.get(FromToCalendarField.DATE_FROM);
						toDate = (Date) approvedDate.get(FromToCalendarField.DATE_TO);
						from = df.format(fromDate);
						to = df.format(toDate);
					}
				}
				if("materialId".equals(name)){
					MATERIAL_ID =  (String) keys.get(name);
				}
			}
			if(from==null || to==null  ){
				UI.showError("开始和结束时间不能为空");
				return;
			}
			userParams.put("START_DATE", from);
			userParams.put("END_DATE", to);
			userParams.put("MATERIAL_ID", MATERIAL_ID);

			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
			dialog.open();
			
		}
    }

}

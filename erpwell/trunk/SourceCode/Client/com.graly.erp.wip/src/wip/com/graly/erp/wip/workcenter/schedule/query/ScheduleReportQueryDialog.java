package com.graly.erp.wip.workcenter.schedule.query;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.ui.forms.field.FromToCalendarField;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.UI;

public class ScheduleReportQueryDialog extends EntityQueryDialog {
	private static final Logger logger = Logger.getLogger(ScheduleReportQueryDialog.class);
	
	public ScheduleReportQueryDialog(Shell parent,
			EntityTableManager tableManager, IRefresh refresh) {
		super(parent, tableManager, refresh);
	}
	
	@Override
    protected void okPressed() {
		if(!validateQueryKey()) return;
		fillQueryKeys();
		String report = "ws_schedule.rptdesign";
		HashMap<String, Object> params = new HashMap<String, Object>();
		HashMap<String, String> userParams = new HashMap<String, String>();
		if(iRefresh instanceof ScheduleSection){
			ScheduleSection parentSection = (ScheduleSection)iRefresh;
			
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			Map<String, Object> keys = this.queryKeys;
			for(String name : keys.keySet()){
    			if("scheduleDate".equals(name)){
    				Map schedDate = (Map) keys.get(name);
    				Date fromDate = null;
    				Date toDate = null;
    				if(schedDate != null){
    					fromDate = (Date) schedDate.get(FromToCalendarField.DATE_FROM);
    					toDate = (Date) schedDate.get(FromToCalendarField.DATE_TO);
    				}
    				if(fromDate != null){
    					userParams.put("SCHE_STARTDATE", I18nUtil.formatDate(fromDate));
    				}
    				
    				if(toDate != null){
    					userParams.put("SCHE_ENDDATE", I18nUtil.formatDate(toDate));
    				}
    			}else if("workcenterId".equals(name)){
    				userParams.put("WORKCENTER_ID",keys.get(name).toString());
    			}
			}
//			parentSection.setQueryKeys(queryKeys);
		}
		setReturnCode(OK);
//        this.setVisible(false);
		PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params,userParams );
		dialog.open();
    }
	
	protected void createDialogForm(Composite composite) {
		queryForm = new ScheduleQueryForm(composite, SWT.NONE, tableManager.getADTable());
        queryForm.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
}

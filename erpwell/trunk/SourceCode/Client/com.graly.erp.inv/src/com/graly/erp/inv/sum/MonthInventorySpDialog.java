package com.graly.erp.inv.sum;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.client.INVManager;
import com.graly.framework.base.entitymanager.IRefresh;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;

public class MonthInventorySpDialog extends EntityQueryDialog {

	public MonthInventorySpDialog(Shell parent, EntityTableManager tableManager, IRefresh irefresh) {
		super(parent, tableManager, irefresh);
	}
	 
	@Override
    protected void okPressed() {
		if(!validateQueryKey()) return;
		fillQueryKeys();
		setReturnCode(OK);
		try {
			INVManager invManager = Framework.getService(INVManager.class);
			Map<String, Object> keys = getQueryKeys();
			String monthday = (String) keys.get("monthnum");
			
			String[] months = monthday.split("-");
			int year = Integer.parseInt(months[0]);
			int month = Integer.parseInt(months[1]);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			/*获得某月的第一天*/
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month - 1);
			calendar.set(Calendar.DATE, 1);
			Date first=calendar.getTime();
		
			Date firstDate = dateFormat.parse(dateFormat.format(first));
		
			/*获得下月的第一天*/
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.DATE, 1);
			Date last=calendar.getTime();
			Date lastDate = dateFormat.parse(dateFormat.format(last));
			
			dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String operDate = dateFormat.format(new Date());
			
			invManager.runSpGetQtyAllocation(firstDate, lastDate, monthday, Env.getUserName(),operDate);
			UI.showInfo("运算成功");
			this.setVisible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

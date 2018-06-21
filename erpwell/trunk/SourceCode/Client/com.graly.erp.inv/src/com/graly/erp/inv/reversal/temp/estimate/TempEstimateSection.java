package com.graly.erp.inv.reversal.temp.estimate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.QuerySection;
import com.graly.erp.inv.client.INVManager;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.runtime.Framework;

public class TempEstimateSection extends QuerySection {
	private Logger logger = Logger.getLogger(TempEstimateSection.class);
	
	private Map<String,Object>	queryKeys;
	
	public TempEstimateSection(EntityTableManager tableManager) {
		super(tableManager);
		setWhereClause(" 1 <> 1 ");
	}
	
	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSearch(tBar);
		createToolItemExport(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
 
	
	@Override
	public void refresh() {
		try {
			if(queryDialog != null){
				queryKeys = queryDialog.getQueryKeys();
			}
			List ls = new ArrayList();
			INVManager invManager = Framework.getService(INVManager.class);
			if(queryKeys != null && !queryKeys.isEmpty()){
					StringBuffer sql = new StringBuffer();
					
					String dateWriteOff = (String) queryKeys.get("dateWriteOff");
					Date from = null;
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM");
					from = df.parse(dateWriteOff);

					Calendar c = Calendar.getInstance();
					c.set(c.YEAR, from.getYear());
					c.set(c.MONTH,from.getMonth());
					String toDate = dateWriteOff+"-"+c.getActualMaximum(c.DAY_OF_MONTH);
					Date to = null;
					SimpleDateFormat todf = new SimpleDateFormat("yyyy-MM-dd");
					to = todf.parse(toDate);
					
					if(from != null) {
						sql.append(" AND trunc(");
						sql.append(" im.date_write_off ");
						sql.append(") >= TO_DATE('" + I18nUtil.formatDate(from) +"', '" + I18nUtil.getDefaultDatePattern() + "') ");
					}
					if(to != null){
						sql.append(" AND trunc(");
						sql.append(" im.date_write_off ");
						sql.append(") <= TO_DATE('" + I18nUtil.formatDate(to) + "', '" + I18nUtil.getDefaultDatePattern() + "') ");
					}
					
					ls = invManager.getReversalTempEstimate(Env.getOrgRrn(),sql.toString());
			}
			viewer.setInput(ls);		
			tableManager.updateView(viewer);
			createSectionDesc(section);
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	@Override
	protected void createSectionDesc(Section section) {
		try{ 
			String text = Message.getString("common.totalshow");
			long count = ((List)viewer.getInput()).size();
			if (count > Env.getMaxResult()) {
				text = String.format(text, String.valueOf(count), String.valueOf(Env.getMaxResult()));
			} else {
				text = String.format(text, String.valueOf(count), String.valueOf(count));
			}
			section.setDescription("  " + text);
		} catch (Exception e){
			logger.error("EntityBlock : createSectionDesc ", e);
		}
	}
}


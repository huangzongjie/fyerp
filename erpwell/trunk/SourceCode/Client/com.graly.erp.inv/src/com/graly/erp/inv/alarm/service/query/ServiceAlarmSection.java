package com.graly.erp.inv.alarm.service.query;

import java.util.ArrayList;
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
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.runtime.Framework;

public class ServiceAlarmSection extends QuerySection {
	private Logger logger = Logger.getLogger(ServiceAlarmSection.class);
	
	private Map<String,Object>	queryKeys;
	
	public ServiceAlarmSection(EntityTableManager tableManager) {
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
			StringBuffer sql = new StringBuffer();
			if (queryKeys != null && !queryKeys.isEmpty()) {
				if (queryKeys.get("objectRrn") != null) {
					long materialRrn = Long.valueOf((String) queryKeys.get("objectRrn"));
					sql.append(" AND PM1.OBJECT_RRN = ");
					sql.append(materialRrn);
				}
			}
//			ls = invManager.getServiceMaterialAlarm(Env.getOrgRrn(), sql.toString());
			if(Env.getOrgRrn()==63506125L){
				ls = invManager.getServiceMaterialAlarmYS(Env.getOrgRrn(),null);
			}else{
				ls = invManager.getServiceMaterialAlarm(Env.getOrgRrn(),null);
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


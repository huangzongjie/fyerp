package com.graly.erp.inv.material.storage.list;

 
import java.util.ArrayList;
 
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
 
import org.eclipse.swt.SWT; 
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.QuerySection;
import com.graly.erp.inv.client.INVManager;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.query.EntityQueryDialog;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.runtime.Framework;

public class MaterialStorageListSection extends QuerySection {
	private Logger logger = Logger.getLogger(MaterialStorageListSection.class);
	
	private Map<String,Object>	queryKeys;

	protected EntityQueryDialog minQueryDialog;
	
	public MaterialStorageListSection(EntityTableManager tableManager) {
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
			if(queryKeys != null){
				if(!queryKeys.isEmpty()){
					StringBuffer sql = new StringBuffer();
					if(queryKeys.get("materialId")!=null){
						String materialName = (String) queryKeys.get("materialId");
						sql.append(" AND m.material_Id LIKE '");
						sql.append(materialName);
						sql.append("'");
					}
					ls = invManager.getMaterialStorageList(Env.getOrgRrn(), sql.toString());
				}else{
					ls = invManager.getMaterialStorageList(Env.getOrgRrn(),null);
				}
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


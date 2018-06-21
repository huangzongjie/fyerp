package com.graly.erp.bj.pdm.material;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.DetailsPart;


import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
public class BJMaterialEntityBlock extends EntityBlock {
	protected ToolItem itemExport;
	private final static Logger logger = Logger.getLogger(BJMaterialEntityBlock.class);
	public BJMaterialEntityBlock(EntityTableManager tableManager) {
		super(tableManager);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void registerPages(DetailsPart detailsPart) {
		try{
			ADTable table = getTableManager().getADTable();
			Class klass = Class.forName(table.getModelClass());
			detailsPart.registerPage(klass, new BJMaterialProperties(this, table));
			
		} catch (Exception e){
			logger.error("EntityBlock : registerPages ", e);
		}
	}
}
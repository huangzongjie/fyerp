package com.graly.erp.xz.pdm.material;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.DetailsPart;


import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
public class XZMaterialEntityBlock extends EntityBlock {
	protected ToolItem itemExport;
	private final static Logger logger = Logger.getLogger(XZMaterialEntityBlock.class);
	public XZMaterialEntityBlock(EntityTableManager tableManager) {
		super(tableManager);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void registerPages(DetailsPart detailsPart) {
		try{
			ADTable table = getTableManager().getADTable();
			Class klass = Class.forName(table.getModelClass());
			detailsPart.registerPage(klass, new XZMaterialProperties(this, table));
			
		} catch (Exception e){
			logger.error("EntityBlock : registerPages ", e);
		}
	}
}
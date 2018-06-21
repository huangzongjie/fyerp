package com.graly.erp.pdm.material.query;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.graly.erp.inv.model.VInDetail;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.framework.base.entitymanager.adapter.EntityItemInput;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.runtime.Framework;

public class MaterialQuerySection extends MasterSection {
	private static final Logger logger = Logger.getLogger(MaterialQuerySection.class);

	public MaterialQuerySection(EntityTableManager tableManager) {
		super(tableManager);
		setWhereClause(" 1 <> 1");
	}

	@Override
	public void refresh(){
		try {
			PDMManager pdmManager = Framework.getService(PDMManager.class);
			List l = pdmManager.queryMaterial(Env.getOrgRrn(), Env.getMaxResult(), getWhereClause());
			viewer.setInput(l);		
			tableManager.updateView(viewer);
			createSectionDesc(section);
		} catch (Exception e) {
			logger.error(e);
		}
	}
}

package com.graly.erp.pur.po.down;

import org.apache.log4j.Logger;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.ChildEntityProperties;
import com.graly.framework.base.entitymanager.forms.EntityBlock;

public class deleteAllButtonChildProperties extends ChildEntityProperties {

	
	public deleteAllButtonChildProperties() {
		super();
    }
	
	public deleteAllButtonChildProperties(EntityBlock masterParent, ADTable table, Object parentObject) {
		super(masterParent, table ,parentObject);
	}

	@Override
	public void createToolBar(Section section) {
	}
	
	
}

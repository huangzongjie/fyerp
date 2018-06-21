package com.graly.erp.wip.mo.workhoursum;

import org.apache.log4j.Logger;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.runtime.Framework;

public class WorkHoursQuerySection extends MasterSection {
	private static final Logger logger = Logger.getLogger(WorkHoursQuerySection.class);
	
	public WorkHoursQuerySection(EntityTableManager tableManager) {
		super(tableManager);
		setWhereClause(" 1 <> 1");
	}
	
	protected ADTable getADTableOfRequisition(String tableName) {
		ADTable adTable = null;
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
			logger.error("WorkHoursQuerySection : getADTableOfRequisition()", e);
		}
		return null;
	}
}

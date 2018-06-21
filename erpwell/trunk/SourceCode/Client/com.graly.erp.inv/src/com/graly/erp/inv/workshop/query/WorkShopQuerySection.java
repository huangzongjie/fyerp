package com.graly.erp.inv.workshop.query;

import org.apache.log4j.Logger;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;

public class WorkShopQuerySection extends MasterSection {
	private static final Logger logger = Logger.getLogger(WorkShopQuerySection.class);
	
	public WorkShopQuerySection(EntityTableManager tableManager) {
		super(tableManager);
		setWhereClause(" 1 <> 1");
	}
	
//	protected ADTable getADTableOfRequisition(String tableName) {
//		ADTable adTable = null;
//		try {
//			ADManager entityManager = Framework.getService(ADManager.class);
//			adTable = entityManager.getADTable(0L, tableName);
//			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
//			return adTable;
//		} catch (Exception e) {
//			logger.error("WorkHoursQuerySection : getADTableOfRequisition()", e);
//		}
//		return null;
//	}
}

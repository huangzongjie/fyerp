package com.graly.erp.bj.inv.in.createfrom.po;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.ui.forms.IManagedForm;

import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.po.model.PurchaseOrder;
import com.graly.erp.po.model.PurchaseOrderLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.wizard.IWizardContext;
import com.graly.framework.runtime.Framework;
import com.graly.mes.wip.model.Lot;

public class BJCreateContext implements IWizardContext {
	private static final Logger logger = Logger.getLogger(BJCreateContext.class);
	public  static final String CATEGORY_NEW_BJPO = "bjCreatePo";
	
	public static final String TableName_Iqc = "INVIqc";
	public static final String TableName_IqcLine = "INVIqcLine";
	public static final String TableName_Lot = "INVLot";
	public static final String TableName_Po = "BJPURPurchaseOrder";
	public static final String TableName_PoLine = "BJPURPurchaseOrderLine";
	private ADTable adTable;

	private IManagedForm mangedForm;
	private BJCreateDialog dialog;

	private String category;
	private MovementIn in;
	private List<Lot> lots;
	private PurchaseOrder po;
	private List<PurchaseOrderLine> poLines;
	private String alarmWhereClause;//新增字段用于警报处理

	public IManagedForm getMangedForm() {
		return mangedForm;
	}
	
	public void setMangedForm(IManagedForm mangedForm) {
		this.mangedForm = mangedForm;
	}
	
	public BJCreateDialog getDialog() {
		return dialog;
	}
	
	public void setDialog(BJCreateDialog dialog) {
		this.dialog = dialog;
	}

	public ADTable getTable(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = entityManager.getADTable(0L, tableName);
		} catch(Exception e) {
			logger.error("IqcCreateContext : getTable_Lot()", e);
		}
		return adTable;
	}

	public List<Lot> getLots() {
		return lots;
	}

	public void setLots(List<Lot> lots) {
		this.lots = lots;
	}

	public MovementIn getIn() {
		return in;
	}

	public void setIn(MovementIn in) {
		this.in = in;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	public PurchaseOrder getPo() {
		return po;
	}

	public void setPo(PurchaseOrder po) {
		this.po = po;
	}

	public List<PurchaseOrderLine> getPoLines() {
		return poLines;
	}

	public void setPoLines(List<PurchaseOrderLine> poLines) {
		this.poLines = poLines;
	}

	public String getAlarmWhereClause() {
		return alarmWhereClause;
	}

	public void setAlarmWhereClause(String alarmWhereClause) {
		this.alarmWhereClause = alarmWhereClause;
	}

}

package com.graly.erp.inv.iqc.createfrom;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.ui.forms.IManagedForm;

import com.graly.erp.inv.model.Iqc;
import com.graly.erp.inv.model.Receipt;
import com.graly.erp.inv.model.ReceiptLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.wizard.IWizardContext;
import com.graly.framework.runtime.Framework;

public class CreateIqcContext implements IWizardContext {
	private static final Logger logger = Logger.getLogger(CreateIqcContext.class);
	
	public  static final String CATEGORY_NEW_IQC = "newIqc";
	
	public static final String TableName_Receipt = "INVReceiptProve";
	public static final String TableName_ReceiptLine = "INVReceiptLine";
	
	private String category;
	private Receipt receipt;
	private Iqc iqc;
	private List<ReceiptLine> receiptLines;
	
	private IManagedForm mangedForm;
	private CreateIqcDialog dialog;
	
	private String alarmWhereClause;//新增字段用于警报处理
	
	public ADTable getTable(String tableName) {
		ADTable adTable = null;
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = entityManager.getADTable(0L, tableName);
		} catch(Exception e) {
			logger.error("IqcCreateContext : getTable_Lot()", e);
		}
		return adTable;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Receipt getReceipt() {
		return receipt;
	}

	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}

	public List<ReceiptLine> getReceiptLines() {
		return receiptLines;
	}

	public void setReceiptLines(List<ReceiptLine> receiptLines) {
		this.receiptLines = receiptLines;
	}

	public IManagedForm getMangedForm() {
		return mangedForm;
	}

	public void setMangedForm(IManagedForm mangedForm) {
		this.mangedForm = mangedForm;
	}

	public CreateIqcDialog getDialog() {
		return dialog;
	}

	public void setDialog(CreateIqcDialog dialog) {
		this.dialog = dialog;
	}

	public Iqc getIqc() {
		return iqc;
	}

	public void setIqc(Iqc iqc) {
		this.iqc = iqc;
	}

	public String getAlarmWhereClause() {
		return alarmWhereClause;
	}

	public void setAlarmWhereClause(String alarmWhereClause) {
		this.alarmWhereClause = alarmWhereClause;
	}
	
}

package com.graly.erp.inv.workshop.requisition;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementWorkShopLine;
import com.graly.erp.inv.model.MovementWorkShopRequestion;
import com.graly.framework.activeentity.model.ADTable;

public class WorkShopRequestionLineLotDialog extends LotDialog {
	private static final String TableName = "INVMovementWorkShopLineLot";
	private MovementWorkShopRequestion wsRequestion;
	private MovementWorkShopLine wsLine;
	
	protected boolean isView = false;
	
	protected List<MovementWorkShopLine> lines;
	
	public WorkShopRequestionLineLotDialog(Shell shell) {
		super(shell);
	}
	
	public WorkShopRequestionLineLotDialog(Shell shell, ADTable table) {
		super(shell, table);
	}

	public WorkShopRequestionLineLotDialog(Shell shell, Object parent, Object child, boolean isView) {
		super(shell);
		this.wsRequestion = (MovementWorkShopRequestion)parent;
		this.wsLine = (MovementWorkShopLine)child;
		this.isView = isView;
	}
	
	public WorkShopRequestionLineLotDialog(Shell shell, Object out, Object movementInLine,
			List<MovementWorkShopLine> lines, boolean isView) {
		this(shell, out, movementInLine, isView);
		this.lines = lines;
	}
	
	protected void createSection(Composite composite) {
		lotSection = new WorkShopRequestionLineLotSection(this, table, wsRequestion, wsLine, lines, isView);
		lotSection.createContents(managedForm, composite);
	}
	
	public String getADTableName() {
		return TableName;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
	}
	
	protected boolean isSureExit() {
		if(wsRequestion != null &&
				MovementWorkShopRequestion.STATUS_APPROVED.equals(wsRequestion.getDocStatus())) {
			return true;
		}
		return super.isSureExit();
	}

}

package com.graly.erp.inv.workshop.reclaim;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.model.MovementWorkShopLine;
import com.graly.erp.inv.model.MovementWorkShopReclaim;
import com.graly.erp.inv.model.MovementWorkShopRequestion;
import com.graly.framework.activeentity.model.ADTable;

public class WorkShopReclaimLineLotDialog extends LotDialog {
	private static final String TableName = "INVMovementWorkShopLineLot";
	private MovementWorkShopReclaim wsReclaim;
	private MovementWorkShopLine wsLine;
	
	protected boolean isView = false;
	
	protected List<MovementWorkShopLine> lines;
	
	public WorkShopReclaimLineLotDialog(Shell shell) {
		super(shell);
	}
	
	public WorkShopReclaimLineLotDialog(Shell shell, ADTable table) {
		super(shell, table);
	}

	public WorkShopReclaimLineLotDialog(Shell shell, Object parent, Object child, boolean isView) {
		super(shell);
		this.wsReclaim = (MovementWorkShopReclaim)parent;
		this.wsLine = (MovementWorkShopLine)child;
		this.isView = isView;
	}
	
	public WorkShopReclaimLineLotDialog(Shell shell, Object out, Object movementInLine,
			List<MovementWorkShopLine> lines, boolean isView) {
		this(shell, out, movementInLine, isView);
		this.lines = lines;
	}
	
	protected void createSection(Composite composite) {
		lotSection = new WorkShopReclaimLineLotSection(this, table, wsReclaim, wsLine, lines, isView);
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
		if(wsReclaim != null &&
				MovementWorkShopRequestion.STATUS_APPROVED.equals(wsReclaim.getDocStatus())) {
			return true;
		}
		return super.isSureExit();
	}

}

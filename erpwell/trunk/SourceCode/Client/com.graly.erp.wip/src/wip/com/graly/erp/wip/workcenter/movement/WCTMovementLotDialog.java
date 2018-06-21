package com.graly.erp.wip.workcenter.movement;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.wip.model.WCTMovement;
import com.graly.erp.wip.model.WCTMovementLine;

public class WCTMovementLotDialog extends LotDialog {
	public static final String TableName = "WCTMovementLineLot";
	protected WCTMovement wctMovement;
	protected WCTMovementLine wctMovementLine;
	protected List<WCTMovementLine> wctMovementLines;
	protected boolean isView = false;
	
	public WCTMovementLotDialog(Shell shell) {
		super(shell);
	}
	
	public WCTMovementLotDialog(Shell shell, Object parent, Object child,  boolean isView) {
		this(shell);
		this.wctMovement = (WCTMovement)parent;
		this.wctMovementLine = (WCTMovementLine)child;
		this.isView = isView;
	}
	
	public WCTMovementLotDialog(Shell shell, Object out, Object movementInLine,
			List<WCTMovementLine> lines, boolean isView) {
		this(shell, out, movementInLine, isView);
		this.wctMovementLines = lines;
	}
	
	protected void createSection(Composite composite) {
		lotSection = new WCTMovementLotSection(table, wctMovement, wctMovementLine, wctMovementLines, this, isView);
		lotSection.createContents(managedForm, composite);
	}

	public String getADTableName() {
		return TableName;
	}
	
	@Override
	public void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
	}

	public List<WCTMovementLine> getWctMovementLines() {
		return wctMovementLines;
	}

	public void setWctMovementLines(List<WCTMovementLine> wctMovementLines) {
		this.wctMovementLines = wctMovementLines;
	}
	
//	protected boolean isSureExit() {
//		if(movementOut != null &&
//				MovementOut.STATUS_APPROVED.equals(movementOut.getDocStatus())) {
//			return true;
//		}
//		return super.isSureExit();
//	}

}

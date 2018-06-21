package com.graly.erp.inv.out;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementOut;

public class OutLineLotDialog extends LotDialog {
	public static final String TableName = "INVMovementLineLot";
	protected MovementOut movementOut;
	protected MovementLine outLine;
	
	protected boolean isView = false;
	
	public OutLineLotDialog(Shell shell) {
		super(shell);
	}
	
	public OutLineLotDialog(Shell shell, Object parent, Object child,  boolean isView) {
		this(shell);
		this.movementOut = (MovementOut)parent;
		this.outLine = (MovementLine)child;
		this.isView = isView;
	}
	
	public OutLineLotDialog(Shell shell, Object out, Object movementInLine,
			List<MovementLine> lines, boolean isView) {
		this(shell, out, movementInLine, isView);
		this.lines = lines;
	}
	
	protected void createSection(Composite composite) {
		lotSection = new OutLineLotSection(table, movementOut, outLine, lines, this, isView);
		lotSection.createContents(managedForm, composite);
	}

	public String getADTableName() {
		return TableName;
	}
	
	@Override
	public void buttonPressed(int buttonId) {
		super.buttonPressed(buttonId);
	}
	
	protected boolean isSureExit() {
		if(movementOut != null &&
				MovementOut.STATUS_APPROVED.equals(movementOut.getDocStatus())) {
			return true;
		}
		return super.isSureExit();
	}

}

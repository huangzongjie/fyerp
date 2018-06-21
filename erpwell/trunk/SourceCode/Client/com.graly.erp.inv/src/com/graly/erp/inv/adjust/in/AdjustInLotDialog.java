package com.graly.erp.inv.adjust.in;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.mes.wip.model.Lot;

public class AdjustInLotDialog extends LotDialog {
	public static final String TableName = "INVMovementLineLot";
	protected ADTable adTable;
	protected MovementLine movementInLine;
	protected MovementIn in;
	protected TableListManager listTableManager;
	protected List<Lot> selectedLots = new ArrayList<Lot>();
	
	protected boolean isView = false;
	protected MovementIn.InType inType;

	public AdjustInLotDialog(Shell shell) {
		super(shell);
	}

	public AdjustInLotDialog(Shell shell, MovementIn in, MovementLine movementInLine) {
		super(shell);
		this.in = in;
		this.movementInLine = movementInLine;
	}

	public AdjustInLotDialog(Shell shell, MovementIn in, MovementLine movementInLine,
			List<MovementLine> lines, boolean isView) {
		this(shell, in, movementInLine);
		this.lines = lines;
		this.isView = isView;
	}

	protected void createSection(Composite composite) {
		adTable = getADTableOfInvLot();
		lotSection = new AdjustInLotSection(adTable, in, movementInLine, lines, isView);
		((AdjustInLotSection)lotSection).setInType(inType);
		lotSection.createContents(managedForm, composite);
	}
	
	protected boolean isSureExit() {
		if(in != null && MovementIn.STATUS_APPROVED.equals(in.getDocStatus())) {
			return true;
		}
		return super.isSureExit();
	}

	@Override
	public String getADTableName() {
		return TableName;
	}

	public MovementIn.InType getInType() {
		return inType;
	}

	public void setInType(MovementIn.InType inType) {
		this.inType = inType;
	}
}

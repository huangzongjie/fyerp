package com.graly.erp.inv.workshop.unqualified;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.model.MovementWorkShopLine;
import com.graly.erp.inv.model.MovementWorkShopRequestion;
import com.graly.erp.inv.model.MovementWorkShopUnqualified;
import com.graly.framework.activeentity.model.ADTable;

public class UnqualifiedLineLotDialog extends LotDialog {
	private static final String TableName = "INVMovementWorkShopLineLot";
	private MovementWorkShopUnqualified wsDelivery;
	private MovementWorkShopLine wsLine;
	
	protected boolean isView = false;
	
	protected List<MovementWorkShopLine> lines;
	
	public UnqualifiedLineLotDialog(Shell shell) {
		super(shell);
	}
	
	public UnqualifiedLineLotDialog(Shell shell, ADTable table) {
		super(shell, table);
	}

	public UnqualifiedLineLotDialog(Shell shell, Object parent, Object child, boolean isView) {
		super(shell);
		this.wsDelivery = (MovementWorkShopUnqualified)parent;
		this.wsLine = (MovementWorkShopLine)child;
		this.isView = isView;
	}
	
	public UnqualifiedLineLotDialog(Shell shell, Object out, Object movementInLine,
			List<MovementWorkShopLine> lines, boolean isView) {
		this(shell, out, movementInLine, isView);
		this.lines = lines;
	}
	
	protected void createSection(Composite composite) {
		lotSection = new UnqualifiedLineLotSection(this, table, wsDelivery, wsLine, lines, isView);
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
		if(wsDelivery != null &&
				MovementWorkShopRequestion.STATUS_APPROVED.equals(wsDelivery.getDocStatus())) {
			return true;
		}
		return super.isSureExit();
	}

}

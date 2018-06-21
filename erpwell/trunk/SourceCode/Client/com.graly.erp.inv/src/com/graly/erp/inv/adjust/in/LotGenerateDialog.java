package com.graly.erp.inv.adjust.in;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.mes.wip.model.Lot;

public class LotGenerateDialog extends AdjustInLotDialog {
	protected List<Lot> genLots;

	public LotGenerateDialog(Shell shell, MovementIn in,
			MovementLine movementInLine, List<Lot> lots) {
		super(shell, in, movementInLine);
		this.genLots = lots;
	}
	
	@Override
	protected void createSection(Composite composite) {
		adTable = getADTableOfInvLot();
		lotSection = new LotGenerateSection(adTable, in, movementInLine, genLots);
		lotSection.createContents(managedForm, composite);
	}

}

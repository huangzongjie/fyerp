package com.graly.erp.inv.out;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.mes.wip.model.Lot;
/**
 * 
 * @author Administrator
 * 选择批次对话框，在其他出库时，创建了一个出库单行，然后从数据库中选中符合条件的可以出库的批次
 */
public class LotSelectFromDbDialogOld extends OutLineLotDialog {
	protected List<Lot> selectedLots;

	public LotSelectFromDbDialogOld(Shell shell, Object parent, Object child, List<Lot> selectedLots) {
		super(shell, parent, child, false);
		this.selectedLots = selectedLots;
	}

	@Override
	protected void createSection(Composite composite) {
		lotSection = new LotSelectFromDbSectionOld(movementOut, outLine, table, selectedLots, this);
		lotSection.createContents(managedForm, composite);
	}
}

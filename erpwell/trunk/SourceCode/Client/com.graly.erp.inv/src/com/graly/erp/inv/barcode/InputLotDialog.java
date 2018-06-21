package com.graly.erp.inv.barcode;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.model.Iqc;
import com.graly.erp.inv.model.IqcLine;
import com.graly.mes.wip.model.Lot;

/*
 * 批次录入对话框
 */
public class InputLotDialog extends IqcLotDialog {
	Iqc iqc;
	IqcLine iqcLine;
	IqcLotSection section;
	List<Lot> lots;

	public InputLotDialog(Shell shell, Iqc iqc,	IqcLine iqcLine, IqcLotSection section) {
		super(shell);
		this.iqc = iqc;
		this.iqcLine = iqcLine;
		this.section = section;
	}
	
	protected void createSection(Composite composite) {
		lotSection = new InputLotSection(iqc, iqcLine, table, iqcLine.getMaterial().getLotType(), this);
		lotSection.createContents(managedForm, composite);
	}

	public List<Lot> getLots() {
		return lots;
	}

	public void setLots(List<Lot> lots) {
		this.lots = lots;
	}
	
	@Override
	protected void cancelPressed() {
		if(getLots() != null && getLots().size() != 0){
			List lst = section.getLots();
			lst.addAll(getLots());
			section.setLots(lst);
			section.refresh();
		}
		super.cancelPressed();
	}
}

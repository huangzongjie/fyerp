package com.graly.erp.inv.barcode;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.model.Iqc;
import com.graly.erp.inv.model.IqcLine;
import com.graly.framework.activeentity.model.ADBase;

public class IqcLotDialog extends LotDialog {
	private IqcLine iqcLine;
	private Iqc iqc;
	
	public IqcLotDialog(Shell shell) {
		super(shell);
	}
	
	public IqcLotDialog(Shell shell, ADBase parent, ADBase child){
		this(shell);
		this.iqc = (Iqc)parent;
		this.iqcLine = (IqcLine)child;
	}
	
	protected void createSection(Composite composite) {
		lotSection = new IqcLotSection(iqc, iqcLine, table, iqcLine.getMaterial().getLotType(), this);
		lotSection.createContents(managedForm, composite);
	}

}

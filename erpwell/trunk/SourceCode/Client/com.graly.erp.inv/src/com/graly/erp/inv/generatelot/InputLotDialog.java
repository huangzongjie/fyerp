package com.graly.erp.inv.generatelot;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.barcode.IqcLotDialog;
import com.graly.erp.inv.model.ConditionItem;

/*
 * 批次录入对话框
 */
public class InputLotDialog extends IqcLotDialog {
	private ConditionItem conItem;
	private Material material;

	public InputLotDialog(Shell shell, ConditionItem conItem, Material material) {
		super(shell);
		this.conItem = conItem;
		this.material = material;
	}
	
	protected void createSection(Composite composite) {
		lotSection = new InputLotSection(table, this);
		lotSection.createContents(managedForm, composite);
	}
	

	public ConditionItem getConItem() {
		return conItem;
	}

	public void setConItem(ConditionItem conItem) {
		this.conItem = conItem;
	}
	

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}
}

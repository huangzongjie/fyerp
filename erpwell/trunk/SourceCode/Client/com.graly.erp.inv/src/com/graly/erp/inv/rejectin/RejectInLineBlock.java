package com.graly.erp.inv.rejectin;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.otherin.OtherInLineEntryBlock;
import com.graly.erp.inv.otherin.OtherInLotDialog;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class RejectInLineBlock extends OtherInLineEntryBlock {

	public RejectInLineBlock(ADTable parentTable, Object parentObject,
			String whereClause, ADTable childTable, boolean flag) {
		super(parentTable, parentObject, whereClause, childTable, flag);
	}

	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemGenerateLot(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemApprove(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemPreview(tBar);
		section.setTextClient(tBar);
	}
	
	@Override
	protected void registerPages(DetailsPart detailsPart) {
		try {
			ADTable table = getTableManager().getADTable();
			Class<?> klass = Class.forName(table.getModelClass());
			oinLineProperties = new RejectInLineProperties(this, table, getParentObject(), flag);
			detailsPart.registerPage(klass, oinLineProperties);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	protected OtherInLotDialog createInLotDialog(List<MovementLine> lines) {
		return new RejectInLotDialog(UI.getActiveShell(),
				(MovementIn)parentObject, selectMovementLine, lines, flag);
	}
	
	protected MovementIn.InType getInType() {
		return MovementIn.InType.RIN;
	}
}

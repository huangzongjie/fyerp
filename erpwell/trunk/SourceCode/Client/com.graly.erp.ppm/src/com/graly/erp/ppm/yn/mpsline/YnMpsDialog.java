package com.graly.erp.ppm.yn.mpsline;

import org.eclipse.swt.widgets.TableItem;

import com.graly.erp.ppm.model.Mps;
import com.graly.erp.ppm.saleplan.SalePlanDialog;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;

public class YnMpsDialog extends SalePlanDialog {
	public static final String DIALOG_ID = "com.graly.erp.ppm.yn.mpsline.YnMpsDialog";

	@Override
	public void initTableViewer() {
		if (salePlanDialogForm.getViewer().getTable().getSelection().length > 0) {
			TableItem ti = salePlanDialogForm.getViewer().getTable().getSelection()[0];
			selectEntity = (ADBase) ti.getData();
			YnMpsEditor editor = (YnMpsEditor) getParent();
			YnMpsEntryPage page = (YnMpsEntryPage) editor.getActivePageInstance();
			mps = (Mps) this.selectEntity;

			if (this.selectEntity != null) {
				page.getPlanBlock().setWhereClause(" mpsId='" + mps.getMpsId() + "' ");
				result = reservedDateCompare(mps);
				if (result.intValue() >= 0) {
					mps.setFrozen(true);
				} else {
					mps.setFrozen(false);
				}
				page.getPlanBlock().setParentObject(mps);
			}
			page.getPlanBlock().refresh();
		}
		okPressed();

		if (this.selectEntity == null) {
			UI.showInfo(Message.getString("ppm.notchacksaleplan"));
		} else if (reservedDateCompare(mps).intValue() >= 0) {
			UI.showInfo(Message.getString("ppm.datereserve"));
		}
	}
}

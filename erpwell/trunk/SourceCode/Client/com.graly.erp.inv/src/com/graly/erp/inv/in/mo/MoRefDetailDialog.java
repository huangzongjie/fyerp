package com.graly.erp.inv.in.mo;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
/**
 * @author Jim
 *  生产接收审核后退库对话框
 */
public class MoRefDetailDialog extends MoInDetailDialog {
	private MovementIn preIn;

	public MoRefDetailDialog(Shell shell, ManufactureOrder mo, MovementIn win,
			MovementIn preIn, ADTable winTable, List<MovementLineLot> lineLots) {
		super(shell, mo, win, winTable, lineLots);
		this.preIn = preIn;
	}
	
	protected void setTitleMessage() {
		String dialogTitle = String.format(Message.getString("common.editor"),
				Message.getString("inv.moin_refund"));
		setTitle(dialogTitle);
	}

	protected void createSection(Composite composite) {
		try {
			ADManager adManager = Framework.getService(ADManager.class);
			if(win.getObjectRrn() != null) {
				String where = " movementRrn = '" + win.getObjectRrn()+ "' ";
				List<MovementLine> lists = adManager.getEntityList(Env.getOrgRrn(), MovementLine.class,Integer.MAX_VALUE,where,"");
				MovementLine line = lists.size() == 0 ? null : lists.get(0); 
				if(line != null)
					win.setLocatorRrn(line.getLocatorRrn());
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
		table = createLienLotADTable();
		lotSection = new MoRefDetailSection(table, winTable, mo, win, preIn, lineLots, this);
		lotSection.createContents(managedForm, composite);	
	}
}

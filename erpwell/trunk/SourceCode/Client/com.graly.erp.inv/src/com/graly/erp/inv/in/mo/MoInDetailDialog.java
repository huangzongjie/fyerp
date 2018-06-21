package com.graly.erp.inv.in.mo;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.barcode.LotDialog;
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

public class MoInDetailDialog extends LotDialog {
	private static final Logger logger = Logger.getLogger(MoInDetailDialog.class);
	protected static final String ADTABLE_LINE_LOT = "INVMovementLineLot";
	private static final int MIN_DIALOG_WIDTH = 700;
	private static final int MIN_DIALOG_HEIGHT = 450;
	
	protected ManufactureOrder mo;
	protected MovementIn win;
	protected ADTable winTable;
	protected List<MovementLineLot> lineLots;

	public MoInDetailDialog(Shell shell, ManufactureOrder mo,
			MovementIn win, ADTable winTable, List<MovementLineLot> lineLots) {
		super(shell);
		this.mo = mo;
		this.win = win;
		this.winTable = winTable;
		this.lineLots = lineLots;
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
		Control com = super.createDialogArea(parent);
		setTitleMessage();
		return com;
    }
	
	protected void setTitleMessage() {
		String dialogTitle = String.format(Message.getString("common.editor"), Message.getString("wip_moin"));
		setTitle(dialogTitle);
	}

	protected void createSection(Composite composite) {
		try {
			ADManager adManager = Framework.getService(ADManager.class);
			if(win.getObjectRrn() != null){
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
		lotSection = new MoInDetailSection(table, winTable, mo, win, lineLots, this);
		lotSection.createContents(managedForm, composite);	
	}
	
	protected ADTable createLienLotADTable() {
		try {
			if(table == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				table = entityManager.getADTable(0L, getADTableName());
			}
			return table;
		} catch(Exception e) {
			logger.error("RequisitionLineDialog : initAdTableOfBom()", e);
		}
		return null;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, Message.getString("common.exit"), false);
	}

	
	public String getADTableName() {
		return ADTABLE_LINE_LOT;
	}
	
	protected boolean isSureExit() {
		if(mo != null &&
				ManufactureOrder.STATUS_APPROVED.equals(mo.getDocStatus())) {
			return true;
		}
		return super.isSureExit();
	}
	
	public MovementIn getMovementIn() {
		if(lotSection != null)
			return ((MoInDetailSection)lotSection).getMovementIn();
		return win;
	}

	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x), Math.max(
				convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT), shellSize.y));
	}
}

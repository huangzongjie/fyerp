package com.graly.erp.inv.in;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class MoInOfLotDialog extends LotDialog {
	private static final Logger logger = Logger.getLogger(MoInOfLotDialog.class);
	private static int MIN_DIALOG_WIDTH = 700;
	private static int MIN_DIALOG_HEIGHT = 450;
	protected Button btOk ;
	private ManufactureOrder mo;
	private MovementIn moIn;
	private IField adFieldLocator;
	private Form form ;

	public MoInOfLotDialog(Shell parent) {
		super(parent);
	}

	public MoInOfLotDialog(Shell shell, ManufactureOrder mo, MovementIn moIn) {
		this(shell);
		this.mo = mo;
		this.moIn = moIn;
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
		Control com = super.createDialogArea(parent);
		String dialogTitle = String.format(Message.getString("common.editor"), Message.getString("wip_moin"));
		setTitle(dialogTitle);
		return com;
    }

	protected void createSection(Composite composite) {
		try {
			ADManager adManager = Framework.getService(ADManager.class);
			if(moIn.getObjectRrn() != null){
				String where = " movementRrn = '" + moIn.getObjectRrn()+ "' ";
				List<MovementLine> lists = adManager.getEntityList(Env.getOrgRrn(), MovementLine.class,Integer.MAX_VALUE,where,"");
				MovementLine line = lists.size() == 0 ? null : lists.get(0); 
				if(line != null)
				    moIn.setLocatorRrn(line.getLocatorRrn());
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
		
		table = getADTableOfInvLot();
		lotSection = new MoInOfLotSection(table, mo, moIn, this);
		lotSection.createContents(managedForm, composite);
		
		//设置LocatorRrn显示
		form = ((MoInOfLotSection)lotSection).getDetailForms().get(0);
		adFieldLocator  = (IField)form.getFields().get("locatorRrn");
		if(!(moIn.getDocStatus().equals(MovementIn.STATUS_DRAFTED))){
			adFieldLocator.setEnabled(false);
		}else{
			adFieldLocator.setEnabled(true);
		}
		adFieldLocator.refresh();
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, Message.getString("common.exit"), false);
//		refreshOk(moIn.getDocStatus());
	}

//	protected void refreshOk(String states){
//		if (!(states.equals(MovementIn.STATUS_DRAFTED))) {
//			btOk.setEnabled(false);
//			//设置LocatorRrn显示
//			adFieldLocator.setEnabled(false);
//			adFieldLocator.refresh();
//		}
//	}
	
	protected Long getLocationRrn() {
		if(adFieldLocator != null && adFieldLocator.getValue() != null && adFieldLocator.getValue() != ""){
			Long locatorRrn = Long.parseLong(adFieldLocator.getValue().toString());
			return locatorRrn;
		}
		return null;
	}
	
	protected boolean isSureExit() {
		if(mo != null &&
				ManufactureOrder.STATUS_APPROVED.equals(mo.getDocStatus())) {
			return true;
		}
		return super.isSureExit();
	}

	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x), Math.max(
				convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT), shellSize.y));
	}
}

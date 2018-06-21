package com.graly.erp.inv.rejectin;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;

import com.graly.erp.base.model.Documentation;
import com.graly.erp.inv.in.MaterialWCAndInvoiceQueryDialog;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.otherin.OtherInLotSection;
import com.graly.erp.inv.otherin.OtherInSection;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class RejectInSection extends OtherInSection {
	private static final Logger logger = Logger.getLogger(RejectInSection.class);

	public RejectInSection(EntityTableManager tableManager) {
		super(tableManager);
	}
	
	protected MovementIn.InType getInTypeForByLotIn() {
		return MovementIn.InType.RIN;
	}
	
	protected void inByLotAdapter() {
		ByLotRejectInDialog olbd = new ByLotRejectInDialog(UI.getActiveShell(), getInTypeForByLotIn());
		if(olbd.open() == Dialog.CANCEL) {
			MovementIn in = ((OtherInLotSection)olbd.getLotMasterSection()).getMovementIn();
			if(in != null && in.getObjectRrn() != null) {
				this.selectedIn = in;
				if(selectedIn != null && selectedIn.getObjectRrn() != null)
					refreshAdd(selectedIn);
				editAdapter();
			}
		}
	}
	
	protected void queryAdapter() {
		if (queryDialog != null) {
			queryDialog.setVisible(true);
		} else {
			queryDialog =  new MaterialWCAndInvoiceQueryDialog(UI.getActiveShell(), tableManager, this, Documentation.DOCTYPE_RIN);
			queryDialog.open();
		}
	}
	
	@Override
	protected void editAdapter() {
		try {
			if(selectedIn != null && selectedIn.getObjectRrn() != null) {
				ADManager adManager = Framework.getService(ADManager.class);
				selectedIn = (MovementIn)adManager.getEntity(selectedIn);
				adTable = getADTableOfRequisition(TABLE_NAME_MOVEMENTLINE);
				String whereClause = " movementId='" + selectedIn.getDocId().toString() + "'";
				RejectInLineDialog inLineDialog = new RejectInLineDialog(UI.getActiveShell(),
						this.getTableManager().getADTable(), whereClause, selectedIn, adTable, false);
				if (inLineDialog.open() == Dialog.CANCEL) {
					refreshSection();
					this.refreshUpdate(selectedIn);
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at RejectInSection : editAdapter() " + e);
		}
	}

	@Override
	protected void newAdapter() {
		adTable = getADTableOfRequisition(TABLE_NAME_MOVEMENTLINE);
		listTableManager = new TableListManager(adTable);
		MovementIn mi = new MovementIn();
		mi.setOrgRrn(Env.getOrgRrn());
		RejectInLineDialog newInDialog = new RejectInLineDialog(UI.getActiveShell(),
				this.getTableManager().getADTable(), " 1<>1 ",
				mi, adTable, false);
		if (newInDialog.open() == Dialog.CANCEL) {
			mi = (MovementIn)newInDialog.getParentObject();
			if (mi != null && mi.getObjectRrn() != null) {
				selectedIn = mi;
				refreshSection();
				refreshAdd(selectedIn);
			}
		}
	}

	// 重载实现标题的提示信息的改变inv.reject_in
	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		super.createContents(form, parent, sectionStyle);
	    section.setText(String.format(Message.getString("common.list"),
	    		Message.getString("inv.reject_in")));
	}
}

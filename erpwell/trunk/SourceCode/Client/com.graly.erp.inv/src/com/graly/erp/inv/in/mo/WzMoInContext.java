package com.graly.erp.inv.in.mo;

import java.util.List;

import org.eclipse.ui.forms.ManagedForm;

import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.wizard.IWizardContext;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class WzMoInContext implements IWizardContext {
	public static final String CATEGORY_NEW_MOIN = "generateWIN";
	private String TableName_MO = "WIPMOMovementIn";
	private String TableName_WIN = "WIPMovementIn";
	private String TableName_Lot = "INVLot";
	
	private ManagedForm managedForm;
	private WzMoInDialog dialog;
	private String category;
	private ManufactureOrder mo;
	private List<Lot> selectedLots;	
	private List<MovementLineLot> inLineLots;
	private boolean isMaterialType = false;
	
	private ADTable adTable_MO;
	private ADTable adTable_WIN;
	private ADTable adTable_Lot;

	public ManufactureOrder getMo() {
		return mo;
	}

	public void setMo(ManufactureOrder mo) {
		this.mo = mo;
	}

	public List<Lot> getSelectedLots() {
		return selectedLots;
	}

	public void setSelectedLots(List<Lot> selectedLots) {
		this.selectedLots = selectedLots;
	}

	public List<MovementLineLot> getInLineLots() {
		return inLineLots;
	}

	public void setInLineLots(List<MovementLineLot> inLineLots) {
		this.inLineLots = inLineLots;
	}
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	public boolean isMaterialType() {
		return isMaterialType;
	}

	public void setMaterialType(boolean isMaterialType) {
		this.isMaterialType = isMaterialType;
	}

	public ADTable getAdTable_ProductedMO() {
		try {
			if(adTable_MO == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable_MO = entityManager.getADTable(0L, TableName_MO);
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return adTable_MO;
	}
	
	public ADTable getAdTable_WIN(boolean isDeep) {
		try {
			if(adTable_WIN == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable_WIN = entityManager.getADTable(0L, TableName_WIN);
				if(isDeep) {
					adTable_WIN = entityManager.getADTableDeep(adTable_WIN.getObjectRrn());
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return adTable_WIN;
	}
	
	public ADTable getAdTable_LOT() {
		try {
			if(adTable_Lot == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable_Lot = entityManager.getADTable(0L, TableName_Lot);
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return adTable_Lot;
	}

	public void setAdTable_MO(ADTable adTable_MO) {
		this.adTable_MO = adTable_MO;
	}

	public void setAdTable_WIN(ADTable adTable_WIN) {
		this.adTable_WIN = adTable_WIN;
	}

	public void setAdTable_Lot(ADTable adTable_Lot) {
		this.adTable_Lot = adTable_Lot;
	}

	public ManagedForm getManagedForm() {
		return managedForm;
	}

	public void setManagedForm(ManagedForm managedForm) {
		this.managedForm = managedForm;
	}

	public WzMoInDialog getDialog() {
		return dialog;
	}

	public void setDialog(WzMoInDialog dialog) {
		this.dialog = dialog;
	}
}

package com.graly.erp.inv.out.adjust;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.MovementOut;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.inv.out.OutLineLotDialog;
import com.graly.erp.inv.out.OutLineLotSection;
import com.graly.erp.inv.out.OutQtySetupDialog;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class AdjustOutLotSection extends OutLineLotSection {
	private static final Logger logger = Logger.getLogger(AdjustOutLotSection.class);
	
	public AdjustOutLotSection(ADBase parent, ADBase child, ADTable adTable,
			OutLineLotDialog olld, boolean isView) {
		super(parent, child, adTable, olld, isView);
	}
	
	//  重载getOutType(), 使出库的参数类型为OutType.AOU
	protected MovementOut.OutType getOutType() {
		return MovementOut.OutType.AOU;
	}
}

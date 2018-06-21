package com.graly.erp.inv.in.createfrom.po;

import org.apache.log4j.Logger;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.in.createfrom.iqc.CreateContext;
import com.graly.erp.inv.in.createfrom.iqc.IqcCreateWizard;
import com.graly.erp.inv.model.MovementIn;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class PoCreateWizard extends IqcCreateWizard {
	private static final Logger logger = Logger.getLogger(PoCreateWizard.class);
	protected String PAGE_CATEGORY = "createPo";
	
	public PoCreateWizard(CreateContext context, String page) {
		super(context, page);
	}
	
	@Override
	public boolean performFinish() {
		try {			
			INVManager invManager = Framework.getService(INVManager.class);
			MovementIn mi = invManager.createInFromPo(context.getIn(),context.getPo(), context.getPoLines(), context.getLots(), Env.getUserRrn());
			context.setIn(mi);
			UI.showInfo(Message.getString("common.save_successed"));
			return true;
		} catch(Exception e) {
			logger.error("PoCreateWizard : performFinish() ");
			ExceptionHandlerManager.asyncHandleException(e);
        	return false;
		}
	}
}

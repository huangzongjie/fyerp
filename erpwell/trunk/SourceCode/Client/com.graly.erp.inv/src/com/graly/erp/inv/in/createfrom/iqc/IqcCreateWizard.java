package com.graly.erp.inv.in.createfrom.iqc;

import java.lang.reflect.Constructor;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.wizard.Wizard;

import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementIn;
import com.graly.framework.base.ui.WizardPageExtensionPoint;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.wizard.FlowWizard;
import com.graly.framework.base.ui.wizard.FlowWizardPage;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class IqcCreateWizard extends FlowWizard {
	private static final Logger logger = Logger.getLogger(IqcCreateWizard.class);
	private String PAGE_CATEGORY;
	protected CreateContext context;	
	
	public IqcCreateWizard(CreateContext context, String page) {
		super(context.getCategory());
		this.context = context;
		this.PAGE_CATEGORY = page;
	}
	
	public void addPages() {
		Map<String, IConfigurationElement> pageMap = WizardPageExtensionPoint.getPageCategoryRegistry().get(PAGE_CATEGORY);
		if (pageMap != null) {
			for (Map.Entry<String, IConfigurationElement> entry : pageMap.entrySet()) {
				try{
					String name = entry.getKey();
					IConfigurationElement configElement = entry.getValue();
					String className = configElement.getAttribute(WizardPageExtensionPoint.A_CLASS);
					String defaultDirect = configElement.getAttribute(WizardPageExtensionPoint.A_DIRECT);
					Class clazz = Class.forName(className);
					Class[] parameterTypes = {String.class, Wizard.class, String.class}; 
					Constructor constructor= clazz.getConstructor(parameterTypes);
					Object[] parameters = {name, this, defaultDirect};
					FlowWizardPage page = (FlowWizardPage)constructor.newInstance(parameters);
					if ("true".equalsIgnoreCase(configElement.getAttribute(WizardPageExtensionPoint.A_ISSTART))) {
						startPage = page;
					}
					addPage(page);
				} catch (Exception e) {
					logger.error("IqcCreateWizard : addPages ", e);
				}
			}
		}
	}
	
	@Override
	public boolean performFinish() {
		try {			
			INVManager invManager = Framework.getService(INVManager.class);
			MovementIn mi = invManager.createInFromIqc(context.getIn(), context.getIqc(),
					context.getIqcLines(), context.getLots(), Env.getUserRrn());
			context.setIn(mi);
			UI.showInfo(Message.getString("common.save_successed"));
			return true;
		} catch(Exception e) {
			logger.error("IqcCreateWizard : performFinish() ");
			ExceptionHandlerManager.asyncHandleException(e);
			return false;
		}
	}
	
	public void setContext(CreateContext context) {
		this.context = context;
	}

	public CreateContext getContext() {
		return context;
	}

}

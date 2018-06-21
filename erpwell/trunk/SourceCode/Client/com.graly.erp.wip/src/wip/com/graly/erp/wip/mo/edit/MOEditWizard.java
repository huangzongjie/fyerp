package com.graly.erp.wip.mo.edit;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import com.graly.erp.base.model.DocumentationLine;
import com.graly.erp.wip.mo.create.MOGenerateContext;
import com.graly.erp.wip.mo.create.MOGenerateWizard;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.framework.base.ui.WizardPageExtensionPoint;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.base.ui.wizard.FlowWizardPage;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;

public class MOEditWizard extends MOGenerateWizard {
	private static final Logger logger = Logger.getLogger(MOEditWizard.class);
	private static final String PAGE_CATEGORY = "editMO";

	public MOEditWizard(MOGenerateContext context) {
		super(context);
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
					logger.error("MOEditWizard : addPages ", e);
				}
			}
		}
	}
	
	@Override
	public boolean performFinish() {
		try {
			ManufactureOrder mo = context.getManufactureOrder();
			List<ManufactureOrderBom> moBoms = context.getMoBoms();
			List<DocumentationLine> moLines = context.getDoLines();
			
			WipManager wipManager = Framework.getService(WipManager.class);
			wipManager.saveMo(mo, moLines, moBoms, Env.getUserRrn());
			UI.showInfo(Message.getString("common.save_successed"));
			return true;
		} catch(Exception e) {
			logger.error("SubMOLinePage : updatePageContent() ");
			ExceptionHandlerManager.asyncHandleException(e);
        	return false;
		}
	}

}

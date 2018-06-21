package com.graly.erp.pur.request.refmo;

import java.lang.reflect.Constructor;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.wizard.Wizard;

import com.graly.framework.base.ui.WizardPageExtensionPoint;
import com.graly.framework.base.ui.wizard.FlowWizard;
import com.graly.framework.base.ui.wizard.FlowWizardPage;

public class MoViewWizard extends FlowWizard {
	private static final Logger logger = Logger.getLogger(MoViewWizard.class);
	protected MoViewContext context;
	/*
	 * 是否可以对MO进行编辑,默认为false,只能查看
	 */
	private boolean canEdit = false;
	
	public MoViewWizard(MoViewContext context) {
		super(context.getCategory());
		this.context = context;
	}
	
	public void addPages() {
		Map<String, IConfigurationElement> pageMap = WizardPageExtensionPoint.getPageCategoryRegistry().get(category);
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
					logger.error("TrackOutWizard : addPages ", e);
				}
			}
		}
	}
	
	@Override
	public boolean performFinish() {
		return true;
	}
	
	public void setContext(MoViewContext context) {
		this.context = context;
	}

	public MoViewContext getContext() {
		return context;
	}	
	
	public boolean isCanEdit() {
		return canEdit;
	}

	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
}

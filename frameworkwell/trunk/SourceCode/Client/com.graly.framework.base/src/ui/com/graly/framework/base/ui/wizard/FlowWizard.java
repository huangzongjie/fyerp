package com.graly.framework.base.ui.wizard;

import java.lang.reflect.Constructor;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;

import com.graly.framework.base.ui.WizardPageExtensionPoint;

public abstract class FlowWizard extends Wizard {

	private static final Logger logger = Logger.getLogger(FlowWizard.class);
	
	private FlowWizardDialog dialog;
	protected IWizardPage startPage;
	private IWizardContext context;
	protected String category;
	
	public FlowWizard(String category) {
		super();
		this.category = category;
	}
		
	@Override
    public void createPageControls(Composite pageContainer) {
        //do nothing; create control until showpage(); 
    }

	@Override
	public IWizardPage getStartingPage() {
		return startPage;
	}
	
	public void setStartingPage(IWizardPage startPage) {
		this.startPage = startPage;
	}

	public void setDialog(FlowWizardDialog dialog) {
		this.dialog = dialog;
	}

	public FlowWizardDialog getDialog() {
		return dialog;
	}

	public void setContext(IWizardContext context) {
		this.context = context;
	}

	public IWizardContext getContext() {
		return context;
	}
}

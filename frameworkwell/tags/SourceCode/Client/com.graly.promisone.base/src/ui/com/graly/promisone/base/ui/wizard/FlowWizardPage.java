package com.graly.promisone.base.ui.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

public abstract class FlowWizardPage extends WizardPage {

	private Wizard wizard;
	private String defaultDirect;
	public static String FINISH = "finish";
	
	protected FlowWizardPage(String pageName, Wizard wizard, String defaultDirect){
		super(pageName);
		this.setWizard(wizard);
		this.setDefaultDirect(defaultDirect);
	}
	
	protected FlowWizardPage(String pageName, String title,
            ImageDescriptor titleImage){
		super(pageName, title, titleImage);
	}
	
	public abstract String doNext();
	
	public void refresh() {		
	}
	
	public void setWizard(Wizard wizard) {
		this.wizard = wizard;
	}

	public Wizard getWizard() {
		return wizard;
	}

	private void setDefaultDirect(String defaultDirect) {
		this.defaultDirect = defaultDirect;
	}

	private String getDefaultDirect() {
		return defaultDirect;
	}
	
}

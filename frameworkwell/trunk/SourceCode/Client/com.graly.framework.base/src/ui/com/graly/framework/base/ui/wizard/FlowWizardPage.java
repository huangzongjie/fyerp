package com.graly.framework.base.ui.wizard;

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
	
	public abstract String doPrevious();
	
	public void refresh() {		
	}
	
	public void setWizard(Wizard wizard) {
		this.wizard = wizard;
	}

	public Wizard getWizard() {
		return wizard;
	}

	public void setDefaultDirect(String defaultDirect) {
		this.defaultDirect = defaultDirect;
	}

	public String getDefaultDirect() {
		return defaultDirect;
	}
	
	public void updatePageContent() {
	}
	
	//是否跳过该页面
	public boolean isJumpOver() {
		return false;
	}
}

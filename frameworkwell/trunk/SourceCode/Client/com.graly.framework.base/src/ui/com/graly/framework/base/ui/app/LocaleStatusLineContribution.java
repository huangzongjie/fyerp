package com.graly.framework.base.ui.app;

import java.util.Locale;

import org.eclipse.jface.action.StatusLineLayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import com.graly.framework.base.ui.action.AbstractContributionItem;
import com.graly.framework.base.ui.util.SWTResourceCache;

public class LocaleStatusLineContribution extends AbstractContributionItem {
	
	private Composite wrapper;
	private Label image;
	private Label text;
	
	public LocaleStatusLineContribution(String name, boolean fillToolBar, boolean fillCoolBar, boolean fillMenuBar, boolean fillComposite) {
		super(LocaleStatusLineContribution.class.getName(), name, fillToolBar, fillCoolBar, fillMenuBar, fillComposite);
		init();
	}

	public LocaleStatusLineContribution(String name) {
		super(LocaleStatusLineContribution.class.getName(), name);
		init();
	}
	
	private void init() {
	}
	
	@Override
	protected Control createControl(Composite parent) {
		wrapper = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		wrapper.setLayout(layout);
		StatusLineLayoutData layoutData = new StatusLineLayoutData();
		layoutData.widthHint = 100;
		wrapper.setLayoutData(layoutData);
		image = new Label(wrapper, SWT.ICON);
		image.setImage(SWTResourceCache.getImage("flag-" + (Locale.getDefault().getLanguage()))); 
		image.setLayoutData(new GridData());
		text = new Label(wrapper, SWT.NONE);		
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.setText(Locale.getDefault().getDisplayLanguage());
		return wrapper;
	}
}

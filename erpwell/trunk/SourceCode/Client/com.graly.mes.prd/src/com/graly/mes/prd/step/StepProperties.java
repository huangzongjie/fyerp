package com.graly.mes.prd.step;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.mes.prd.ParameterForm;
import com.graly.mes.prd.PrdProperties;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.security.model.ADAuthority;


public class StepProperties extends PrdProperties {
	
	@Override
	protected void createSectionTitle(Composite client) {
		// TODO Auto-generated method stub
		super.createSectionTitle(client);
	}

	@Override
	public void createSectionContent(Composite parent) {
		final FormToolkit toolkit = form.getToolkit();
		final IMessageManager mmng = form.getMessageManager();
		setTabs(new CTabFolder(parent, SWT.FLAT|SWT.TOP));
		getTabs().marginHeight = 10;
		getTabs().marginWidth = 5;
		toolkit.adapt(getTabs(), true, true);
		GridData gd = new GridData(GridData.FILL_BOTH);
		getTabs().setLayoutData(gd);
		Color selectedColor = toolkit.getColors().getColor(FormColors.SEPARATOR);
		getTabs().setSelectionBackground(new Color[] {selectedColor, toolkit.getColors().getBackground()}, new int[] {50});
		toolkit.paintBordersFor(getTabs());

		for (ADTab tab : getTable().getTabs()){
			EntityForm itemForm = null;
			CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
			String tabText = I18nUtil.getI18nMessage(tab, "label");
			item.setText(tabText);
			if(tabText.equalsIgnoreCase(Message.getString("common.parameter"))) {
				itemForm = new ParameterForm(getTabs(), SWT.NONE, tab, mmng);
			} else if(tabText.equalsIgnoreCase(Message.getString("common.operation"))) {
				itemForm = new StepOperationsForm(getTabs(), SWT.NONE, tab, mmng);
			} else {
				itemForm = new EntityForm(getTabs(), SWT.NONE, tab, mmng);
			}
			getDetailForms().add((Form)itemForm);
			item.setControl(itemForm);
		}
		
		if (getTabs().getTabList().length > 0){
			getTabs().setSelection(0);
		}
	}
	
	@Override
	protected void createToolItemFrozen(ToolBar tBar) {
		itemFrozen = new AuthorityToolItem(tBar, SWT.PUSH, ADAuthority.KEY_PRD_STEP_FROZEN);
		itemFrozen.setImage(SWTResourceCache.getImage("frozen"));
		itemFrozen.setText(Message.getString("common.frozen"));
		itemFrozen.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				frozenAdapter();
			}
		});
	}
	
	@Override
	protected void createToolItemActive(ToolBar tBar) {
		itemActive = new AuthorityToolItem(tBar, SWT.PUSH, ADAuthority.KEY_PRD_STEP_ACTIVE);
		itemActive.setImage(SWTResourceCache.getImage("active"));
		itemActive.setText(Message.getString("common.active"));
		itemActive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				activeAdapter();
			}
		});
	}
	
}
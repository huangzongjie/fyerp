package com.graly.framework.base.security.usergroup;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;

import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.entitymanager.forms.EntityProperties;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.security.form.AuthorityForm;

public class UserGroupProperties extends EntityProperties {
	
	public UserGroupProperties() {
		super();
    }
	
	@Override
	public void createSectionContent(Composite parent) {
		super.createSectionContent(parent);
		CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
		item.setText(Message.getString("common.authority"));
		AuthorityForm itemForm = new AuthorityForm(getTabs(), SWT.NONE);
		getDetailForms().add(itemForm);
		item.setControl(itemForm);
	}

}

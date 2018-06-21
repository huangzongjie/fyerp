package com.graly.promisone.base.security.form;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.promisone.base.entitymanager.forms.EntityForm;
import com.graly.promisone.base.security.views.MenuTreeManager;
import com.graly.promisone.base.ui.forms.Form;
import com.graly.promisone.base.ui.forms.field.IField;
import com.graly.promisone.base.ui.forms.field.TreeField;
import com.graly.promisone.base.ui.util.Env;
import com.graly.promisone.base.ui.util.Message;
import com.graly.promisone.base.ui.util.PropertyUtil;
import com.graly.promisone.runtime.Framework;
import com.graly.promisone.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.promisone.security.client.SecurityManager;
import com.graly.promisone.security.model.ADMenu;

public class AuthorityForm extends Form {
	
	private static final Logger logger = Logger.getLogger(EntityForm.class);

	private static final String FIELD_ID = "authorities";
	FormToolkit toolkit;
	
	public AuthorityForm(Composite parent, int style) {
		super(parent, style, null);
		createForm();
    }
	
	@Override
	public void createForm(){
		super.createForm();
	}
	
	@Override
	public void addFields() {
		try{
			SecurityManager securityManager = Framework.getService(SecurityManager.class);
			final List<ADMenu> list = securityManager.getAuthorityTree(Env.getOrgId());
			MenuTreeManager treeManager = new MenuTreeManager();
			IField field = new TreeField(FIELD_ID, Message.getString("common.function_list"), treeManager, list);
			addField(FIELD_ID, field);
		} catch (Exception e) {
        	logger.error("EntityForm : createForm", e);
        	ExceptionHandlerManager.asyncHandleException(e);
        }
	}

	@Override
	public boolean validate() {
		return true;
	}
	
	@Override
    public boolean saveToObject() {
		if (object != null){
			IField f = fields.get("authorities");
			
			PropertyUtil.setProperty(object, f.getId(), f.getValue());
			return true;
		}
		return false;
    }
	
	@Override
    public void loadFromObject() {
		if (object != null){
			IField f = fields.get(FIELD_ID);
			f.setValue(PropertyUtil.getPropertyForIField(object, f.getId()));
			refresh();
		}
    }
	
    @Override
    public void dispose() {
        if (toolkit != null) {
            toolkit.dispose();
            toolkit = null;
        }
        super.dispose();
    }

}

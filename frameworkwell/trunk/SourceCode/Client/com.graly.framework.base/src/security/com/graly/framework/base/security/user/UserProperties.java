package com.graly.framework.base.security.user;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.forms.IFormPart;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.entitymanager.forms.EntityProperties;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.framework.security.client.SecurityManager;
import com.graly.framework.security.model.ADUser;

public class UserProperties extends EntityProperties {
	public UserProperties() {
		super();
    }
	
	public UserProperties(EntityBlock masterParent, ADTable table) {
    	super(masterParent, table);
    }
	
	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		StructuredSelection ss = (StructuredSelection) selection;
		Object object = ss.getFirstElement();
		try {
			ADUser user = (ADUser)object;
			setAdObject(user);
			if (user != null && user.getObjectRrn() != null) {
				SecurityManager securityManager = Framework.getService(SecurityManager.class);
				//argument is current area and selected user.ObjectId
				setAdObject(securityManager.getUser(Env.getOrgRrn(), user.getObjectRrn()));  
			} else {
				setAdObject(createAdObject());
			}
			refresh();
        } catch (Exception e) {
        	ExceptionHandlerManager.asyncHandleException(e);
        	return;
        }
	}
	
	protected void saveAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				boolean saveFlag = true;
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
					}
				}
				if (saveFlag) {
					for (Form detailForm : getDetailForms()) {
						PropertyUtil.copyProperties(getAdObject(), detailForm
								.getObject(), detailForm.getFields());
					}
					SecurityManager securityManager = Framework.getService(SecurityManager.class);
					ADUser user = securityManager.saveUser(Env.getOrgRrn(), (ADUser)getAdObject(), Env.getUserRrn());
					setAdObject(securityManager.getUser(Env.getOrgRrn(), user.getObjectRrn()));
					UI.showInfo(Message.getString("common.save_successed"));// µ¯³öÌáÊ¾¿ò
					refresh();
				}
			}
			getMasterParent().refresh();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	@Override
	protected void refreshAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			ADUser user = (ADUser)getAdObject();
			if (user != null && user.getObjectRrn() != null) {
				SecurityManager securityManager = Framework.getService(SecurityManager.class);
				setAdObject(securityManager.getUser(Env.getOrgRrn(), user.getObjectRrn()));
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}
		refresh();
	}
}

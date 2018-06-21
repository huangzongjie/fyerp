package com.graly.promisone.base.security.user;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.forms.IFormPart;

import com.graly.promisone.activeentity.model.ADTable;
import com.graly.promisone.base.entitymanager.forms.EntityBlock;
import com.graly.promisone.base.entitymanager.forms.EntityProperties;
import com.graly.promisone.base.ui.forms.Form;
import com.graly.promisone.base.ui.util.Env;
import com.graly.promisone.base.ui.util.Message;
import com.graly.promisone.base.ui.util.PropertyUtil;
import com.graly.promisone.base.ui.util.UI;
import com.graly.promisone.runtime.Framework;
import com.graly.promisone.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.promisone.security.client.SecurityManager;
import com.graly.promisone.security.model.ADUser;

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
			if (user != null && user.getObjectId() != null) {
				SecurityManager securityManager = Framework.getService(SecurityManager.class);
				//argument is current area and selected user.ObjectId
				setAdObject(securityManager.getUser(Env.getOrgId(), user.getObjectId()));  
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
					ADUser user = securityManager.saveUser(Env.getOrgId(), (ADUser)getAdObject(), Env.getUserId());
					setAdObject(securityManager.getUser(Env.getOrgId(), user.getObjectId()));
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
			if (user != null && user.getObjectId() != null) {
				SecurityManager securityManager = Framework.getService(SecurityManager.class);
				setAdObject(securityManager.getUser(Env.getOrgId(), user.getObjectId()));
			}
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}
		refresh();
	}
}

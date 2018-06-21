package com.graly.promisone.base.security.usergroup;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;

import com.graly.promisone.base.entitymanager.dialog.SingleQueryDialog;
import com.graly.promisone.base.entitymanager.views.TableListManager;
import com.graly.promisone.base.ui.util.Env;
import com.graly.promisone.runtime.Framework;
import com.graly.promisone.security.client.SecurityManager;
import com.graly.promisone.security.model.ADUser;

public class UserQueryDialog extends SingleQueryDialog {
	Logger logger = Logger.getLogger(UserQueryDialog.class);
	
	public UserQueryDialog(Shell shell) {
		super(shell);
	}
	
	public UserQueryDialog(Shell parent, TableListManager listTableManager,
			IManagedForm managedForm, String whereClause, int style){
		super(parent, listTableManager, managedForm, whereClause, style);
	}
	
	public UserQueryDialog(Shell parent, StructuredViewer viewer, Object object) {
		super(parent, viewer, object);
	}
	
	@Override
	protected void refresh(boolean clearFlag) {
		List<ADUser> users = null;
		try {
			SecurityManager securityManager = Framework.getService(SecurityManager.class);
			users = (List<ADUser>)securityManager.getUsersByOrg(Env.getOrgId(), getKeys());
		} catch(Exception e) {
			logger.error("getUsersByOrg() Error at UserQueryDialog" + e);
		}

		if (object instanceof List) {
			exsitedItems = (List)object;
			if (exsitedItems != null && users != null) {
				users.removeAll(exsitedItems);
			}
		}
		viewer.setInput(users);			
		listTableManager.updateView(viewer);
	}
}

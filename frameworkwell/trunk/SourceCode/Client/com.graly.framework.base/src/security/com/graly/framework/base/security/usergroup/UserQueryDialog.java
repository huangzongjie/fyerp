package com.graly.framework.base.security.usergroup;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.base.entitymanager.query.SingleQueryDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.runtime.Framework;
import com.graly.framework.security.client.SecurityManager;
import com.graly.framework.security.model.ADUser;

public class UserQueryDialog extends SingleQueryDialog {
	Logger logger = Logger.getLogger(UserQueryDialog.class);
	
	public UserQueryDialog() {
		super();
	}
	
	public UserQueryDialog(TableListManager listTableManager,
			IManagedForm managedForm, String whereClause, int style){
		super(listTableManager, managedForm, whereClause, style);
	}
	
	public UserQueryDialog(StructuredViewer viewer, Object object) {
		super(viewer, object);
	}
	
	@Override
	protected void refresh(boolean clearFlag) {
		List<ADUser> users = new ArrayList<ADUser>();
		try {
			ADManager manager = Framework.getService(ADManager.class);
			long objectId = listTableManager.getADTable().getObjectRrn();
			List<ADBase> ls = manager.getEntityList(Env.getOrgRrn(), objectId, 
            		Env.getMaxResult(), getKeys(), "");
			for(ADBase base : ls) {
				users.add((ADUser)base);
			}

//			SecurityManager securityManager = Framework.getService(SecurityManager.class);
//			users = (List<ADUser>)securityManager.getUsersByOrg(Env.getOrgId(), getKeys());
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

package com.graly.framework.base.security.login;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.naming.CommunicationException;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.security.client.SecurityManager;
import com.graly.framework.security.model.ADAuthority;
import com.graly.framework.security.model.ADUser;
import com.graly.framework.security.model.WorkCenter;

/**
 * µÇÂ½Ò³Ãæ
 * @author DELL
 *
 */
public class Login {
	
	public static ADUser doLogin(String userName, String password) throws ClientException {
		try {
			SecurityManager manager = Framework.getService(SecurityManager.class);
			ADUser user = manager.doLogin(Env.getOrgRrn(), userName, password);
			if (user == null) {
				throw new ClientException("error.username_password_iscorrect");
			}
			return user;
		} catch (CommunicationException e) {
			throw new ClientException("Connection refused, Please connect administrator!");
		} catch (Exception e) {
			if (e instanceof ClientException) {
				throw (ClientException)e;
			} else {
				throw new ClientException(e);
			}
		}
	}
	
	public static void doAuthority(long orgId, long userId) throws ClientException {
		try {
			SecurityManager manager = Framework.getService(SecurityManager.class);
			Set<ADAuthority> authroites = manager.getUserAuthority(orgId, userId);
			List<String> keyList = new ArrayList<String>();
			if (authroites != null) {
				for (ADAuthority authority : authroites) {
					keyList.add(authority.getName());
				}
			} 
			Env.setAuthority(keyList);
			
			List<WorkCenter> wcs = Env.getUser().getWorkCenters();
			Env.setWorkCenters(wcs);
		} catch (Exception e) {
			if (e instanceof ClientException) {
				throw (ClientException)e;
			} else {
				throw new ClientException(e);
			}
		}
	}
	
	public void doLogout() {
		
	}
	
	public void doRelogin(){
		
	}
}

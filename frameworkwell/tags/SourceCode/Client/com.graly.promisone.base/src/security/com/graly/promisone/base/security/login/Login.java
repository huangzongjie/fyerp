package com.graly.promisone.base.security.login;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.naming.CommunicationException;
import com.graly.promisone.base.ui.util.Env;
import com.graly.promisone.core.exception.ClientException;
import com.graly.promisone.runtime.Framework;
import com.graly.promisone.security.client.SecurityManager;
import com.graly.promisone.security.model.ADMenu;
import com.graly.promisone.security.model.ADUser;


public class Login {
	
	public static ADUser doLogin(String userName, String password) throws ClientException {
		try {
			SecurityManager manager = Framework.getService(SecurityManager.class);
			ADUser user = manager.doLogin(Env.getOrgId(), userName, password);
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
			Set<ADMenu> authroites = manager.getUserAuthority(orgId, userId);
			List<String> keyList = new ArrayList<String>();
			if (authroites != null) {
				for (ADMenu authority : authroites) {
					keyList.add(authority.getName());
				}
			} 
			Env.setAuthority(keyList);
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

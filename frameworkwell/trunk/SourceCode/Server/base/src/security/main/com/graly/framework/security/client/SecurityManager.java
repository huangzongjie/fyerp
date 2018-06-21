package com.graly.framework.security.client;

import java.util.List;
import java.util.Set;

import com.graly.framework.core.exception.ClientException;
import com.graly.framework.security.model.ADAuthority;
import com.graly.framework.security.model.ADUser;

public interface SecurityManager {
	
	List<ADAuthority> getAuthorityTree(long orgRrn) throws ClientException;
	List<ADAuthority> getUserMenuTree(long orgRrn, long userRrn) throws ClientException;
	Set<ADAuthority> getUserAuthority(long orgRrn, long userRrn) throws ClientException;
	
	ADUser doLogin(long orgRrn, String userName, String passowrd) throws ClientException;
	ADUser getUser(long orgRrn, long userRrn) throws ClientException;
	ADUser saveUser(long orgRrn, ADUser user, long userRrn) throws ClientException;
}

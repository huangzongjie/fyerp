package com.graly.promisone.security.client;

import java.util.List;
import java.util.Set;

import com.graly.promisone.core.exception.ClientException;
import com.graly.promisone.security.model.ADMenu;
import com.graly.promisone.security.model.ADOrg;
import com.graly.promisone.security.model.ADUser;

public interface SecurityManager {
	
	List<ADMenu> getAuthorityTree(long orgId) throws ClientException;
	List<ADMenu> getUserMenu(long orgId, long userId) throws ClientException;
	Set<ADMenu> getUserAuthority(long orgId, long userId) throws ClientException;
	ADUser doLogin(long orgId, String userName, String passowrd) throws ClientException;
	ADUser getUser(long orgId, long userId) throws ClientException;
	ADUser saveUser(long orgId, ADUser user, long userId) throws ClientException;
	List<ADUser> getUsersByOrg(long orgId, String whereClause) throws ClientException;
	ADOrg saveOrg(ADOrg org, long userId) throws ClientException;
}

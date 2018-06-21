package com.graly.framework.security.ejb;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.EntityResult;
import javax.persistence.FieldResult;
import javax.persistence.Query;
import javax.ejb.Remote;
import javax.ejb.Local;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import com.graly.framework.security.client.SecurityManager;
import com.graly.framework.security.model.ADAuthority;
import com.graly.framework.security.model.ADOrg;
import com.graly.framework.security.model.ADUser;
import com.graly.framework.security.model.ADUserGroup;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADUpdatable;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.core.exception.ClientParameterException;

@Stateless
@Local(SecurityManager.class)
@Remote(SecurityManager.class)
public class SecurityManagerBean implements SecurityManager {
	
	private static final Logger logger = Logger.getLogger(SecurityManagerBean.class);
	private static final String USER_ADMINISTRATOR = "admin";
	
	@PersistenceContext
	private EntityManager em;
	   
	public List<ADAuthority> getAuthorityTree(long orgRrn) throws ClientException {
		
		String sql = "SELECT LEVEL, OBJECT_RRN " + 
			        " FROM AD_AUTHORITY " +
			        " WHERE " + ADBase.SQL_BASE_CONDITION +  
			        " START WITH PARENT_RRN IS NULL " +
			        " CONNECT BY PRIOR OBJECT_RRN = PARENT_RRN ORDER SIBLINGS BY SEQ_NO ";
		String sqlAuthority = "SELECT ADAuthority FROM ADAuthority ADAuthority ";
		logger.debug(sql);
		try{
			Query queryTree = em.createNativeQuery(sql);
			queryTree.setParameter(1, orgRrn);
			List listTree = queryTree.getResultList();
			
			Query queryAuthority = em.createQuery(sqlAuthority);
			List<ADAuthority> authorityList = (List<ADAuthority>)queryAuthority.getResultList();
			
			List<ADAuthority> authorities = new ArrayList<ADAuthority>();
			
			for(Iterator iter = listTree.iterator(); iter.hasNext(); ) {  
			    Object[] objects = (Object[])iter.next();  
			    long level = ((java.math.BigDecimal)objects[0]).longValue();
			    long objectId = ((java.math.BigDecimal)objects[1]).longValue();
			    for (ADAuthority authority : authorityList) {
			    	if (objectId == authority.getObjectRrn()) {
			    		authority.setLevel(level);
			    		authorities.add(authority);
			    		break;
			    	}
			    }
			}
			return authorities;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<ADAuthority> getUserMenuTree(long orgRrn, long userRrn) throws ClientException {
		String sql = "SELECT LEVEL, OBJECT_RRN " + 
        " FROM AD_AUTHORITY " +
        " WHERE " + ADBase.SQL_BASE_CONDITION +  
        " AND (AUTHORITY_TYPE = 'M' OR AUTHORITY_TYPE = 'F')" + 
        " START WITH PARENT_RRN IS NULL " +
        " CONNECT BY PRIOR OBJECT_RRN = PARENT_RRN ORDER SIBLINGS BY SEQ_NO ";
		try{
			Query queryTree = em.createNativeQuery(sql);
			queryTree.setParameter(1, orgRrn);
			List listTree = queryTree.getResultList();
			
			Set<ADAuthority> authorityMenus = getUserAuthority(orgRrn, userRrn);
			
			List<ADAuthority> menus = new ArrayList<ADAuthority>();
			
			for(Iterator iter = listTree.iterator(); iter.hasNext(); ) {  
			    Object[] objects = (Object[])iter.next();  
			    long level = ((java.math.BigDecimal)objects[0]).longValue();
			    long objectId = ((java.math.BigDecimal)objects[1]).longValue();
			    for (ADAuthority menu : authorityMenus) {
			    	if (objectId == menu.getObjectRrn()) {
			    		menu.setLevel(level);
			    		menus.add(menu);
			    		break;
			    	}
			    }
			}
			return menus;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public Set<ADAuthority> getUserAuthority(long orgRrn, long userRrn) throws ClientException {
		try{
			Set<ADAuthority> authorities = new HashSet<ADAuthority>();
			String sqlAuthority = "SELECT ADAuthority FROM ADAuthority ADAuthority ";
			if (userRrn == 0L) {
				Query queryAuthority = em.createQuery(sqlAuthority);
				List<ADAuthority> authorityList = (List<ADAuthority>)queryAuthority.getResultList();
				authorities.addAll(authorityList);
			} else {
				ADUser user = em.find(ADUser.class, userRrn);
				//
				if (USER_ADMINISTRATOR.equals(user.getUserName())) {
					Query queryAuthority = em.createQuery(sqlAuthority);
					List<ADAuthority> authorityList = (List<ADAuthority>)queryAuthority.getResultList();
					authorities.addAll(authorityList);
				} else {
					List<ADUserGroup> groups = user.getUserGroups();
					for (ADUserGroup group : groups) {
						if (group.getOrgRrn().equals(orgRrn) || group.getOrgRrn() == 0L) {
							List<ADAuthority> authorityList = group.getAuthorities();
							authorities.addAll(authorityList);
						}
					}
				}
			}
			return authorities;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public ADUser doLogin(long orgRrn, String userName, String passowrd) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ADUser FROM ADUser ADUser ");
		sql.append(" WHERE ");
		sql.append(ADBase.BASE_CONDITION);
		sql.append(" AND userName = ? ");   
		sql.append(" AND password = ? ");   
		logger.debug(sql);
		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, userName);
			query.setParameter(3, passowrd);
			Object obj = query.getSingleResult();
			if (obj != null) {
				ADUser user = (ADUser)obj;
				return user;
			}
		} catch (Exception e){
			if(e instanceof ClientException){
				throw (ClientException)e;
			} 
			return null;
		}
		return null;
	}
	

	//This method can not join transaction
	public ADUser getUser(long orgRrn, long userRrn) throws ClientException {
		try{ 
			ADUser user = em.find(ADUser.class, userRrn);
			List<ADUserGroup> userGroups = new ArrayList<ADUserGroup>();
			for (ADUserGroup userGroup : user.getUserGroups()) {
				if (userGroup.getOrgRrn().equals(orgRrn)) {
					userGroups.add(userGroup);
				} 
			}
			user.setUserGroups(userGroups);
			user.getOrgs().size();
			em.clear();
			return user;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public ADUser saveUser(long orgRrn, ADUser user, long userRrn) throws ClientException {
		try{
			if (user.getObjectRrn() != null) {
				ADUser oldUser = em.find(ADUser.class, user.getObjectRrn());
 				for (ADUserGroup userGroup : oldUser.getUserGroups()) {
					if (!userGroup.getOrgRrn().equals(orgRrn)) {
						if (user.getUserGroups() == null) {
							user.setUserGroups(new ArrayList<ADUserGroup>());
						}
						user.getUserGroups().add(userGroup);
					}
				}
 				String oldPassword = oldUser.getPassword();
 				if (user.getPassword() != null && !user.getPassword().equals(oldPassword)) {
 					user.setPwdChanged(new Date());
 				}
 				user.setUpdatedBy(userRrn);
				user = em.merge(user);
			} else {
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT ADUser FROM ADUser ADUser ");
				sql.append(" WHERE ");
				sql.append(" userName = ? ");
				Query query = em.createQuery(sql.toString());
				query.setParameter(1, user.getUserName());
				query.setMaxResults(1);
				List list = query.getResultList();
				if (list.size() > 0) {
					throw new ClientParameterException("error.object_duplicate", user.getUserName());
				}
				user.setOrgRrn(0L);
				user.setIsActive(true);
				user.setUpdatedBy(userRrn);
				user.setCreatedBy(userRrn);
				user.setCreated(new Date());
				user.setPwdChanged(new Date());
				em.persist(user);
			}
			return user;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			if(e instanceof ClientException){
				throw (ClientException)e;
			} 
			throw new ClientException(e);
		}
	}
	
}

package com.graly.promisone.security.ejb;

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

import com.graly.promisone.security.client.SecurityManager;
import com.graly.promisone.security.model.ADMenu;
import com.graly.promisone.security.model.ADOrg;
import com.graly.promisone.security.model.ADUser;
import com.graly.promisone.security.model.ADUserGroup;
import com.graly.promisone.activeentity.model.ADBase;
import com.graly.promisone.activeentity.model.ADUpdatable;
import com.graly.promisone.core.exception.ClientException;
import com.graly.promisone.core.exception.ClientParameterException;

@Stateless
@Local(SecurityManager.class)
@Remote(SecurityManager.class)
public class SecurityManagerBean implements SecurityManager {
	
	private static final Logger logger = Logger.getLogger(SecurityManagerBean.class);
	
	@PersistenceContext
	private EntityManager em;
	   
	public List<ADMenu> getAuthorityTree(long orgId) throws ClientException {
		String sql = "SELECT LEVEL, OBJECT_ID " + 
			        " FROM AD_MENU " +
			        " WHERE IS_ACTIVE = 'Y' AND (AD_ORG_ID = ? OR AD_ORG_ID = 0)" + 
			        " START WITH PARENT_ID IS NULL " +
			        " CONNECT BY PRIOR OBJECT_ID = PARENT_ID ORDER SIBLINGS BY SEQ_NO ";
		String sqlMenu = "SELECT ADMenu FROM ADMenu ADMenu ";
		logger.debug(sql);
		try{
			Query query = em.createNativeQuery(sql);
			query.setParameter(1, orgId);
			List list = query.getResultList();
			
			Query queryMenu = em.createQuery(sqlMenu);
			List<ADMenu> menuList = (List<ADMenu>)queryMenu.getResultList();
			
			List<ADMenu> menus = new ArrayList<ADMenu>();
			
			for(Iterator iter = list.iterator(); iter.hasNext(); ) {  
			    Object[] objects = (Object[])iter.next();  
			    long level = ((java.math.BigDecimal)objects[0]).longValue();
			    long objectId = ((java.math.BigDecimal)objects[1]).longValue();
			    for (ADMenu menu : menuList) {
			    	if (objectId == menu.getObjectId()) {
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
	
	public List<ADMenu> getUserMenu(long orgId, long userId) throws ClientException {
		String sql = "SELECT LEVEL, OBJECT_ID " + 
        " FROM AD_MENU " +
        " WHERE IS_ACTIVE = 'Y' AND (AD_ORG_ID = ? OR AD_ORG_ID = 0)" + 
        " AND (MENU_TYPE = 'M' OR MENU_TYPE = 'F')" + 
        " START WITH PARENT_ID IS NULL " +
        " CONNECT BY PRIOR OBJECT_ID = PARENT_ID ORDER SIBLINGS BY SEQ_NO ";
		try{
			Query query = em.createNativeQuery(sql);
			query.setParameter(1, orgId);
			List list = query.getResultList();
			
			Set<ADMenu> authorityMenus = getUserAuthority(orgId, userId);
			
			List<ADMenu> menus = new ArrayList<ADMenu>();
			
			for(Iterator iter = list.iterator(); iter.hasNext(); ) {  
			    Object[] objects = (Object[])iter.next();  
			    long level = ((java.math.BigDecimal)objects[0]).longValue();
			    long objectId = ((java.math.BigDecimal)objects[1]).longValue();
			    for (ADMenu menu : authorityMenus) {
			    	if (objectId == menu.getObjectId()) {
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
	
	public ADUser doLogin(long orgId, String userName, String passowrd) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ADUser FROM ADUser ADUser ");
		sql.append(" WHERE ");
		sql.append(ADBase.BASE_CONDITION);
		sql.append(" AND userName = ? ");   
		sql.append(" AND password = ? ");   
		logger.debug(sql);
		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgId);
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
	
	public Set<ADMenu> getUserAuthority(long orgId, long userId) throws ClientException {
		try{
			ADUser user = em.find(ADUser.class, userId);
			List<ADUserGroup> groups = user.getUserGroups();
			Set<ADMenu> menus = new HashSet<ADMenu>();
			for (ADUserGroup group : groups) {
				if (group.getOrgId().equals(orgId) || group.getOrgId() == 0L) {
					List<ADMenu> menuList = group.getAuthorities();
					menus.addAll(menuList);
				}
			}
			return menus;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//This method can not join transaction
	public ADUser getUser(long orgId, long userId) throws ClientException {
		try{ 
			ADUser user = em.find(ADUser.class, userId);
			List<ADUserGroup> userGroups = new ArrayList<ADUserGroup>();
			for (ADUserGroup userGroup : user.getUserGroups()) {
				if (userGroup.getOrgId().equals(orgId)) {
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
	
	public ADUser saveUser(long orgId, ADUser user, long userId) throws ClientException {
		try{
			if (user.getObjectId() != null) {
				ADUser oldUser = em.find(ADUser.class, user.getObjectId());
 				for (ADUserGroup userGroup : oldUser.getUserGroups()) {
					if (!userGroup.getOrgId().equals(orgId)) {
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
				ADUser newUser = new ADUser();
				em.persist(newUser);
				em.flush();
				em.refresh(newUser);
				user.setObjectId(newUser.getObjectId());
				user.setUpdated(newUser.getUpdated());
				user.setCreatedBy(userId);
				user.setCreated(new Date());
				user.setPwdChanged(new Date());
			}
			user.setOrgId(0L);
			user.setIsActive(true);
			user = em.merge(user);
			em.flush();
			em.refresh(user);
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
	
	public ADOrg saveOrg(ADOrg org, long userId) throws ClientException {
		try{
			if (org.getObjectId() != null) {
				org = em.merge(org);
			} else {
				org.setOrgId(0L);
				org.setIsActive(true);
				org.setUpdatedBy(userId);
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT ADOrg FROM ADOrg ADOrg ");
				sql.append(" WHERE ");
				sql.append(" name = ? ");
				Query query = em.createQuery(sql.toString());
				query.setParameter(1, org.getName());
				query.setMaxResults(1);
				List list = query.getResultList();
				if (list.size() > 0) {
					throw new ClientParameterException("error.object_duplicate", org.getName());
				}
				org.setCreatedBy(userId);
				org.setCreated(new Date());
				org = em.merge(org);
				
				ADUserGroup userGroup = createDefaultUserGroup(org.getObjectId(), userId);
				ADUser user = em.find(ADUser.class, userId);
				if (user.getUserGroups() != null) {
					user.getUserGroups().add(userGroup);
				}
				em.merge(user);
			}
			return org;
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
	
	public List<ADUser> getUsersByOrg (long orgId, String whereClause) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ADUser FROM ADUser ADUser INNER JOIN ADUser.orgs s");
		sql.append(" WHERE ");
		sql.append(" ADUser.isActive = 'Y' ");
		sql.append(" AND s.objectId = ? ");   //s.orgId = ?
		if (whereClause != null && !"".equalsIgnoreCase(whereClause.trim())){
			sql.append(" AND ");
			sql.append(whereClause);
		}
		logger.debug(sql);
		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgId);
			List<ADUser> users = query.getResultList();
			return users;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//default usergroup admin
	public ADUserGroup createDefaultUserGroup(long orgId, long userId) throws ClientException {
		try{
			ADUserGroup userGroup = new ADUserGroup();
			userGroup.setCreatedBy(userId);
			userGroup.setCreated(new Date());
			userGroup.setUpdatedBy(userId);
			userGroup.setOrgId(orgId);
			userGroup.setIsActive(true);
			userGroup.setName("admin");
			em.persist(userGroup);
			em.flush();
			em.refresh(userGroup);
			
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT ADMenu FROM ADMenu ADMenu");
			sql.append(" WHERE ");
			sql.append(ADBase.BASE_CONDITION);
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgId);
			
			List<ADMenu> authorities = (List<ADMenu>)query.getResultList();
			userGroup.setAuthorities(authorities);
			userGroup = em.merge(userGroup);
			return userGroup;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
}

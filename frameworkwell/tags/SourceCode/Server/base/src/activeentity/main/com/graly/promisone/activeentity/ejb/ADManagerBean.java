package com.graly.promisone.activeentity.ejb;

import javax.ejb.Remote;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.LockModeType;

import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.beans.PropertyDescriptor;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

import com.graly.promisone.activeentity.client.ADManager;
import com.graly.promisone.activeentity.model.ADBase;
import com.graly.promisone.activeentity.model.ADParameter;
import com.graly.promisone.activeentity.model.ADRefTable;
import com.graly.promisone.activeentity.model.ADSequence;
import com.graly.promisone.activeentity.model.ADUpdatable;
import com.graly.promisone.activeentity.model.ADTable;
import com.graly.promisone.activeentity.model.ADTab;
import com.graly.promisone.activeentity.model.ADField;
import com.graly.promisone.activeentity.model.ADRefList;
import com.graly.promisone.activeentity.model.ADMessage;
import com.graly.promisone.activeentity.model.ADUserRefList;
import com.graly.promisone.core.exception.ClientException;
import com.graly.promisone.core.exception.ClientParameterException;
import com.graly.promisone.security.model.ADOrg;
import com.graly.promisone.security.model.ADUser;

@Stateless
@Remote(ADManager.class)
@Local(ADManager.class)
public class ADManagerBean implements ADManager{
	
	private static final Logger logger = Logger.getLogger(ADManagerBean.class);
	
	@PersistenceContext
	private EntityManager em;
	
	public ADTable getADTable(long tableId) throws ClientException{
		try{
			ADTable table = em.find(ADTable.class, tableId);
			if (table != null) {
				if (table.getTabs() != null){
					table.getTabs().size();
				}
				if (table.getFields() != null){
					table.getFields().size();
				}
			} else {
				throw new ClientException("Table is not existed!");
			}
			return table;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public ADTable getADTableDeep(long tableId) throws ClientException{
		try{
			ADTable table = em.find(ADTable.class, tableId);
			if (table != null) {
				if (table.getTabs() != null){
					for (ADTab tab : table.getTabs()) {
						if (tab.getFields() != null) {
							tab.getFields().size();
						}
					}
				}
				if (table.getFields() != null){
					table.getFields().size();
				}
			} else {
				throw new ClientException("Table is not existed!");
			}
			return table;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	private ADTable getADTableSimple (long tableId) throws ClientException{
		try{
			ADTable table = em.find(ADTable.class, tableId);
			if (table == null) {
				throw new ClientException("Table is not existed!");
			}
			return table;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	public ADTab getADTab(long tabId) throws ClientException {
		try{
			ADTab tab = em.find(ADTab.class, tabId);
			if (tab != null) {
				if (tab.getFields() != null){
					tab.getFields().size();
				}
			} else {
				throw new ClientException("Tab is not existed!");
			}
			return tab;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<ADRefList> getADRefList(long orgId, String refName) throws ClientException {
		try{
			StringBuffer sql = new StringBuffer(" SELECT ADRefList FROM ADRefList ADRefList ");
			sql.append(" WHERE ");
			sql.append(ADBase.BASE_CONDITION);
			sql.append(" AND referenceName = ? ORDER BY seqNo ");
			
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgId);
			query.setParameter(2, refName);
			return query.getResultList();
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	 
	public ADBase saveEntity(ADBase adBase, long userId) throws ClientException{
		return saveEntity(0, adBase, userId);
	}
	
	public ADBase saveEntity(long tableId, ADBase adBase, long userId) throws ClientException{
		try{
			if (!(adBase instanceof ADUpdatable)){
				logger.error("Object is not a ADUpdatable model " + adBase.toString());
				throw new ClientException("Object is not a ADUpdatable model " + adBase.toString());
			}
			if (adBase instanceof ADField ||
				adBase instanceof ADMessage ||
				adBase instanceof ADRefTable ||
				adBase instanceof ADTab ||
				adBase instanceof ADTable ||
				adBase instanceof ADOrg ||
				adBase instanceof ADUser ||
				adBase instanceof ADRefList) {
				adBase.setOrgId(0L);
			} else {
				if (adBase.getOrgId() == null) {
					adBase.setOrgId(0L);
				}
			}
			ADUpdatable object = (ADUpdatable)adBase;
			Date now = new Date();
			object.setIsActive(true);
			object.setUpdatedBy(userId);
			if (object.getObjectId() == null) {
				//check key field
				if (tableId != 0) {
					ADTable table = this.getADTableSimple(tableId);
					List<String> keyList = new ArrayList<String>();
					for (ADField field : table.getFields()){
						if (field.getIsActive() && field.getIsKey()) {
							keyList.add(field.getName());
						}
					} 
					if (keyList.size() > 0){
						String valuList = "";
						StringBuffer sql = new StringBuffer(" SELECT objectId FROM ");
						sql.append(table.getModelName());
						sql.append(" ");
						sql.append(table.getModelName());
						sql.append(" WHERE ");
						sql.append(ADBase.BASE_CONDITION);
						for (String key : keyList){
							sql.append(" AND ");
							sql.append(key);
							sql.append( "='" );
							Object value = PropertyUtils.getSimpleProperty(object, key);
							if (value != null){
								if (value instanceof String){
									sql.append(value);
									if ("".equals(valuList)) {
										valuList = (String)value;
									} else {
										valuList = valuList + ";" + value;
									}
									
								} else if (value instanceof Long){
									sql.append(((Long)value).longValue());
									if ("".equals(valuList)) {
										valuList = ((Long)value).toString();
									} else {
										valuList = valuList + ";" + ((Long)value).toString();
									}
								}
							}
							sql.append( "'" );
						}
						logger.debug(sql);	
						
						Query query = em.createQuery(sql.toString());
						query.setParameter(1, adBase.getOrgId());
						query.setMaxResults(1);
						List list = query.getResultList();
						if (list.size() > 0){
							throw new ClientParameterException("error.object_duplicate", valuList);
						}
					}
				}
				
//				ADUpdatable newUpdatable = object.getClass().newInstance();
//				em.persist(newUpdatable);
//				em.flush();
//				em.refresh(newUpdatable);
//				object.setObjectId(newUpdatable.getObjectId());
//				object.setUpdated(newUpdatable.getUpdated());
				object.setCreatedBy(userId);
				object.setCreated(now);
				em.persist(object);
			} else {
				object = em.merge(object);
			}
			return this.getEntity(object);
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<ADMessage> getMessage() throws ClientException{
		try{
			String sql = "SELECT ADMessage FROM ADMessage ADMessage";
			Query query = em.createQuery(sql);
			return query.getResultList();
			
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public ADBase getEntity(ADBase object) throws ClientException{
		try{
			if (object != null){
				if (object instanceof ADTable){
					return getADTable(object.getObjectId());
				} else if (object instanceof ADTab){
					return getADTab(object.getObjectId());
				} else {
					ADBase baseObj = em.find(object.getClass(), object.getObjectId());
					PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(baseObj);
					for (PropertyDescriptor descriptor : descriptors){
						Class klass = descriptor.getPropertyType();
						if (klass.isInstance(ArrayList.class.newInstance())){
							List list = (List)PropertyUtils.getProperty(baseObj, descriptor.getName());
							if (list != null) {
								list.size();
							}
						}
					}
					
					return baseObj;
				}
			} else {
				throw new ClientException("Entity object can not be null!");
			}
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<ADBase> getEntityList(long orgId, long tableId) throws ClientException {
		return this.getEntityList(orgId, tableId, Integer.MAX_VALUE);
	}
	
	public List<ADBase> getEntityList(long orgId, long tableId, int maxResult) throws ClientException {
		return this.getEntityList(orgId, tableId, Integer.MAX_VALUE, null, null);
	}
	
	public List<ADBase> getEntityList(long orgId, long tableId, int maxResult, String whereClause, String orderByClause) throws ClientException{
		try{
			ADTable table = this.getADTableSimple(tableId);
			StringBuffer sql = new StringBuffer(" SELECT ");
			sql.append(table.getModelName());
			sql.append(" FROM ");
			sql.append(table.getModelName());
			sql.append(" ");
			sql.append(table.getModelName());
			sql.append(" WHERE ");
			sql.append(ADBase.BASE_CONDITION);
			if (table.getWhereClause() != null && !"".equalsIgnoreCase(table.getWhereClause().trim())){
				sql.append(" AND ");
				sql.append(table.getWhereClause());
			}
			if (whereClause != null && !"".equalsIgnoreCase(whereClause.trim())){
				sql.append(" AND ");
				sql.append(whereClause);
			}
			if ((table.getOrderByClause() != null && !"".equalsIgnoreCase(table.getOrderByClause().trim())) || 
					((orderByClause != null && !"".equalsIgnoreCase(orderByClause.trim())))){
				sql.append(" ORDER BY ");
				if (table.getOrderByClause() != null && !"".equalsIgnoreCase(table.getOrderByClause().trim())){
					sql.append(table.getOrderByClause());
					if ((orderByClause != null && !"".equalsIgnoreCase(orderByClause.trim()))){
						sql.append(" , ");
						sql.append(orderByClause);
					}
				} else if ((orderByClause != null && !"".equalsIgnoreCase(orderByClause.trim()))){
					sql.append(orderByClause);
				}				
			}
			logger.debug(sql);
			
			Query query = em.createQuery(sql.toString());
			query.setMaxResults(maxResult);
			query.setParameter(1, orgId);
			List list = query.getResultList();
			return list;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public long getEntityCount(long orgId, long tableId, String whereClause) throws ClientException{
		try{
			ADTable table = this.getADTableSimple(tableId);
			StringBuffer sql = new StringBuffer(" SELECT COUNT(*) FROM ");
			sql.append(table.getModelName());
			sql.append(" ");
			sql.append(table.getModelName());
			sql.append(" WHERE ");
			sql.append(ADBase.BASE_CONDITION);
			if (table.getWhereClause() != null && !"".equalsIgnoreCase(table.getWhereClause().trim())){
				sql.append(" AND ");
				sql.append(table.getWhereClause());
			}
			if (whereClause != null && !"".equalsIgnoreCase(whereClause.trim())){
				sql.append(" AND ");
				sql.append(whereClause);
			}
			logger.debug(sql);
			
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgId);
			Long result = (Long)query.getSingleResult();
			return result.longValue();
			
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	@Override
	public void deleteEntity(ADBase object) throws ClientException {
		try{
			if (object != null){
				ADBase baseObj = em.find(object.getClass(), object.getObjectId());
				em.remove(baseObj);
				em.flush();
			} else {
				throw new ClientException("Entity object can not be null!");
			}
		} catch (EntityExistsException e){
			if (e.getCause() instanceof ConstraintViolationException) {
				logger.error(e.getMessage(), e);
				throw new ClientException("error.constraintviolation");
			}
			throw e;
		}
		catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		
	}
	
	public ADTable getADTable(long orgId, String tableName) throws ClientException {
		StringBuffer sql = new StringBuffer(" SELECT ADTable FROM ADTable ADTable ");
		sql.append(" WHERE ");
		sql.append(ADBase.BASE_CONDITION);
		sql.append(" AND name = ? ");
		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgId);
			query.setParameter(2, tableName);
			ADTable table = (ADTable)query.getSingleResult();
			if (table != null) {
				if (table.getTabs() != null){
					table.getTabs().size();
				}
				if (table.getFields() != null){
					table.getFields().size();
				}
			} else {
				throw new ClientException("Table is not existed!");
			}
			return table;
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public Date getSysDate() {
		return new Date();
	}
	
	public long getNextADSequence(long orgId, String name) throws ClientException  {
		return getNextADSequence(orgId, name, 0, 0, 0);
	}
	
	public long getNextADSequenceByDay(long orgId, String name) throws ClientException  {
		Date date = new Date();
		return getNextADSequence(orgId, name, date.getYear() + 1900, date.getMonth() + 1, date.getDate());
	}
	
	private long getNextADSequence(long orgId, String name, long year, long month, long day)  throws ClientException {
		StringBuffer sql = new StringBuffer(" SELECT ADSequence FROM ADSequence ADSequence ");
		sql.append(" WHERE ");
		sql.append(ADBase.BASE_CONDITION);
		sql.append(" AND name = ? ");
		if (year != 0) {
			sql.append(" AND year = ? ");
		}
		if (month != 0) {
			sql.append(" AND month = ? ");
		}
		if (day != 0) {
			sql.append(" AND day = ? ");
		}
		try {
			int i = 0;
			Query query = em.createQuery(sql.toString());
			query.setParameter(++i, orgId);
			query.setParameter(++i, name);
			if (year != 0) {
				query.setParameter(++i, year);
			}
			if (month != 0) {
				query.setParameter(++i, month);
			}
			if (day != 0) {
				query.setParameter(++i, day);
			}
			ADSequence seqence = (ADSequence)query.getSingleResult();
			long seqValue = seqence.getNextSeq();
			seqence.setNextSeq(seqValue + 1);
			em.merge(seqence);
			return seqValue;
		} catch (NoResultException e){
			ADSequence seqence = new ADSequence();
			seqence.setOrgId(orgId);
			seqence.setIsActive(true);
			seqence.setName(name);
			if (year != 0) {
				seqence.setYear(year);
			}
			if (month != 0) {
				seqence.setMonth(month);
			}
			if (day != 0) {
				seqence.setDay(day);
			}
			seqence.setNextSeq(2L);
			em.merge(seqence);
			return 1;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		}  catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException("error.get_ad_sequence");
		}
	}
}

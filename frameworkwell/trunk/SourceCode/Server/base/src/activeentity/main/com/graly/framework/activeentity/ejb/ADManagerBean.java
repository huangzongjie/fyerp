package com.graly.framework.activeentity.ejb;

import javax.ejb.Remote;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.beans.PropertyDescriptor;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADListProperty;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADSequence;
import com.graly.framework.activeentity.model.ADSingleProperty;
import com.graly.framework.activeentity.model.ADUpdatable;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADRefList;
import com.graly.framework.activeentity.model.ADMessage;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.core.exception.ClientParameterException;
import com.graly.framework.security.model.ADOrg;
import com.graly.framework.security.model.ADUser;

@Stateless
@Remote(ADManager.class)
@Local(ADManager.class)
public class ADManagerBean implements ADManager{
	
	private static final Logger logger = Logger.getLogger(ADManagerBean.class);
	
	@PersistenceContext
	private EntityManager em;
	
	public ADTable getADTable(long tableRrn) throws ClientException{
		try{
			ADTable table = em.find(ADTable.class, tableRrn);
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
	
	public ADTable getADTable(long orgRrn, String tableName) throws ClientException {
		StringBuffer sql = new StringBuffer(" SELECT ADTable FROM ADTable ADTable ");
		sql.append(" WHERE ");
		sql.append(ADBase.BASE_CONDITION);
		sql.append(" AND name = ? ");
		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgRrn);
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
	
	public ADTable getADTableDeep(long tableRrn) throws ClientException{
		try{
			ADTable table = em.find(ADTable.class, tableRrn);
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
	
	private ADTable getADTableSimple (long tableRrn) throws ClientException{
		try{
			ADTable table = em.find(ADTable.class, tableRrn);
			if (table == null) {
				throw new ClientException("Table is not existed!");
			}
			return table;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public ADTab getADTab(long tabRrn) throws ClientException {
		try{
			ADTab tab = em.find(ADTab.class, tabRrn);
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
	
	public List<ADRefList> getADRefList(long orgRrn, String refName) throws ClientException {
		try{
			StringBuffer sql = new StringBuffer(" SELECT ADRefList FROM ADRefList ADRefList ");
			sql.append(" WHERE ");
			sql.append(ADBase.BASE_CONDITION);
			sql.append(" AND referenceName = ? ORDER BY seqNo ");
			
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, refName);
			return query.getResultList();
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	 
	public ADBase saveEntity(ADBase adBase, long userRrn) throws ClientException{
		return saveEntity(0, adBase, userRrn);
	}
	
	public ADBase saveEntity(long tableRrn, ADBase adBase, long userRrn) throws ClientException{
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
				adBase.setOrgRrn(0L);
			} else {
				if (adBase.getOrgRrn() == null) {
					adBase.setOrgRrn(0L);
				}
			}
			ADUpdatable object = (ADUpdatable)adBase;
			
			if (object.getObjectRrn() == null) {
				//check key field
				if (tableRrn != 0) {
					ADTable table = this.getADTableSimple(tableRrn);
					List<String> keyList = new ArrayList<String>();
					for (ADField field : table.getFields()){
						if (field.getIsActive() && field.getIsKey()) {
							keyList.add(field.getName());
						}
					} 
					if (keyList.size() > 0){
						String valuList = "";
						StringBuffer sql = new StringBuffer(" SELECT objectRrn FROM ");
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
								} else if (value instanceof Boolean) {
									Boolean bValue = (Boolean)value;
									if (bValue) {
										sql.append("Y");
									} else {
										sql.append("N");
									}
								} else {
									throw new ClientException("Key DataType " + value + " is not support");
								}
							}
							sql.append( "'" );
						}
						
						
						Query query = em.createQuery(sql.toString());
						query.setParameter(1, adBase.getOrgRrn());
						query.setMaxResults(1);
						List list = query.getResultList();
						if (list.size() > 0){
							throw new ClientParameterException("error.object_duplicate", valuList);
						}
					}
				}
				object.setIsActive(true);
				object.setUpdatedBy(userRrn);
				object.setCreatedBy(userRrn);
				object.setCreated(new Date());
				em.persist(object);
			} else {
				object.setUpdatedBy(userRrn);
				object = em.merge(object);
			}
			return object;
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

	public List<ADSingleProperty> getSingleProperty() throws ClientException{
		try{
			String sql = "SELECT ADSingleProperty FROM ADSingleProperty ADSingleProperty";
			Query query = em.createQuery(sql);
			return query.getResultList();
			
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<ADListProperty> getListProperty() throws ClientException{
		try{
			String sql = "SELECT ADListProperty FROM ADListProperty ADListProperty";
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
					return getADTable(object.getObjectRrn());
				} else if (object instanceof ADTab){
					return getADTab(object.getObjectRrn());
				} else {
					ADBase baseObj = em.find(object.getClass(), object.getObjectRrn());
					if (baseObj == null) {
						throw new ClientException("error.entity_not_exist_or_delete");
					}
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
	
	public <T> List<T> getEntityList(long orgRrn, Class<T> clazz) throws ClientException {
		return this.getEntityList(orgRrn, clazz, Integer.MAX_VALUE);
	}
	
	public <T> List<T> getEntityList(long orgRrn, Class<T> clazz, int maxResult) throws ClientException {
		return this.getEntityList(orgRrn, clazz, maxResult, null, null);
	}
	
	public <T> List<T> getEntityList(long orgRrn, Class<T> clazz, int maxResult, String whereClause, String orderByClause) throws ClientException {
		StringBuffer sql = new StringBuffer(" SELECT ");
		sql.append(clazz.getSimpleName());
		sql.append(" FROM ");
		sql.append(clazz.getSimpleName());
		sql.append(" ");
		sql.append(clazz.getSimpleName());
		sql.append(" WHERE ");
		sql.append(ADBase.BASE_CONDITION);
		if (whereClause != null && !"".equalsIgnoreCase(whereClause.trim())){
			sql.append(" AND ");
			sql.append(whereClause);
		}
		if (orderByClause != null && !"".equalsIgnoreCase(orderByClause.trim())){
			sql.append(" ORDER BY ");
			sql.append(orderByClause);			
		}
		
		try {
			Query query = em.createQuery(sql.toString());
			query.setMaxResults(maxResult);
			query.setParameter(1, orgRrn);
			List list = query.getResultList();
			return list;
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<ADBase> getEntityList(long orgRrn, long tableRrn) throws ClientException {
		return this.getEntityList(orgRrn, tableRrn, Integer.MAX_VALUE);
	}
	
	public List<ADBase> getEntityList(long orgRrn, long tableRrn, int maxResult) throws ClientException {
		return this.getEntityList(orgRrn, tableRrn, maxResult, null, null);
	}
	
	public List<ADBase> getEntityList(long orgRrn, long tableRrn, int maxResult, String whereClause, String orderByClause) throws ClientException{
		try{
			ADTable table = this.getADTableSimple(tableRrn);
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
			
			
			Query query = em.createQuery(sql.toString());
			query.setMaxResults(maxResult);
			query.setParameter(1, orgRrn);
			List list = query.getResultList();
			return list;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public long getEntityCount(long orgRrn, long tableRrn, String whereClause) throws ClientException{
		try{
			ADTable table = this.getADTableSimple(tableRrn);
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
			
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgRrn);
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
				ADBase baseObj = em.find(object.getClass(), object.getObjectRrn());
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
	
	public Date getSysDate() {
		return new Date();
	}
	
	public long getNextSequence(long orgRrn, String name) throws ClientException  {
		return getNextSequence(orgRrn, name, 0, 0, 0);
	}
	
	public long getNextSequenceByDay(long orgRrn, String name) throws ClientException  {
		Calendar now = Calendar.getInstance();
		return getNextSequence(orgRrn, name, now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH));
	}
	
	public long getNextSequenceByMonth(long orgRrn, String name) throws ClientException  {
		Calendar now = Calendar.getInstance();
		return getNextSequence(orgRrn, name, now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, 0);
	}
	
	public long getNextSequence(long orgRrn, String name, long year, long month, long day)  throws ClientException {
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
			query.setParameter(++i, orgRrn);
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
			seqence.setOrgRrn(orgRrn);
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
	
	public ADRefTable getADRefTable(long orgRrn, String tableName) throws ClientException {
		StringBuffer sql = new StringBuffer(" SELECT ADRefTable FROM ADRefTable ADRefTable ");
		sql.append(" WHERE ");
		sql.append(ADBase.BASE_CONDITION);
		sql.append(" AND name = ? ");
		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, tableName);
			ADRefTable table = (ADRefTable)query.getSingleResult();
			if (table == null) {
				throw new ClientException("Table is not existed!");
			}
			return table;
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
}

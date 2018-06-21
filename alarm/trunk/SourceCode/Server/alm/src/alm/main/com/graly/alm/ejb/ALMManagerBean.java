package com.graly.alm.ejb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.graly.alm.client.ALMManager;
import com.graly.alm.model.Action;
import com.graly.alm.model.AlarmDefinition;
import com.graly.alm.model.AlarmHis;
import com.graly.alm.model.AlarmMessage;
import com.graly.alm.model.AlarmPanelMessage;
import com.graly.alm.util.AlarmAction;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.security.model.ADUser;

@Stateless
@Remote(ALMManager.class)
@Local(ALMManager.class)
public class ALMManagerBean implements ALMManager {
	public static final Logger logger = Logger.getLogger(ALMManagerBean.class);

	@PersistenceContext
	public EntityManager em;

	@EJB
	private ADManager adManager;

	public AlarmDefinition saveAlarm(AlarmDefinition alarm, long userRrn) throws ClientException {
		try {
			if (alarm.getObjectRrn() == null) {
				alarm.setIsActive(true);
				alarm.setCreatedBy(userRrn);
				alarm.setCreated(new Date());
				alarm.setUpdatedBy(userRrn);
				List<Action> actionList = alarm.getActions();
				for (Action action : actionList) {
					action.setIsActive(true);
					action.setCreatedBy(userRrn);
					action.setCreated(new Date());
					action.setUpdated(new Date());
					action.setUpdatedBy(userRrn);
				}
				em.persist(alarm);
			} else {
				alarm.setUpdated(new Date());
				alarm.setUpdatedBy(userRrn);
				List<Action> actionList = alarm.getActions();
				for (Action action : actionList) {
					if (action != null && action.getObjectRrn() == null) {
						action.setIsActive(true);
						action.setCreatedBy(userRrn);
						action.setCreated(new Date());
					}
					action.setUpdated(new Date());
					action.setUpdatedBy(userRrn);
					if (action != null && action.getObjectRrn() == null) {
						em.persist(action);
					} else {
						em.merge(action);
					}
				}
				em.merge(alarm);
			}
			return alarm;
		} catch (OptimisticLockException e) {
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	public void deleteAlarm(AlarmDefinition alarm, long userRrn) throws ClientException {
		try {
			if (alarm != null && alarm.getObjectRrn() != null) {
				alarm = em.find(AlarmDefinition.class, alarm.getObjectRrn());
				String alarmActionCause = "alarmDefinitionRrn='" + alarm.getObjectRrn()
						+ "'";
				List<Action> actions = adManager.getEntityList(alarm
						.getOrgRrn(), Action.class, Integer.MAX_VALUE,
						alarmActionCause, "");
				for (Action action : actions) {
					deleteAction(action, userRrn);// 先从actionList中删除该警告
				}
				em.remove(alarm);
			}
		} catch (OptimisticLockException e) {
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	public void deleteAction(Action action, long userRrn)
			throws ClientException {
		try {
			if (action != null && action.getObjectRrn() != null) {
				action = em.find(Action.class, action.getObjectRrn());
				em.remove(action);
			}
		} catch (OptimisticLockException e) {
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	public void closeAlarmHis(AlarmHis alarmHis, long userRrn)
			throws ClientException {
		try {
			if (alarmHis != null && alarmHis.getObjectRrn() != null) {
				ADUser user = em.find(ADUser.class, userRrn);
				alarmHis.setUpdated(new Date());
				alarmHis.setUpdatedBy(userRrn);
				alarmHis.setState(AlarmHis.STATE_CLOSE);
				alarmHis.setDateClose(new Date());
				alarmHis.setUserClose(user.getUserName());
				em.merge(alarmHis);
			}
		} catch (OptimisticLockException e) {
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	public List<AlarmPanelMessage> getPanelMessages(long orgRrn, long userRrn) throws ClientException {
		StringBuffer sql=new StringBuffer();
		sql.append(" SELECT AlarmPanelMessage FROM AlarmPanelMessage AS AlarmPanelMessage ");
		sql.append(" WHERE orgRrn = ?  AND userRrn = ? ");
		sql.append(" AND state = ? ");

		try {
			Query query=em.createQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, userRrn);
			query.setParameter(3, AlarmPanelMessage.STATE_OPEN);
			return (List<AlarmPanelMessage>)query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<AlarmDefinition> getAlarmDefinitionByInstance(AlarmMessage instance) throws ClientException {
		StringBuffer sql=new StringBuffer();
		sql.append(" SELECT AlarmDefinition FROM AlarmDefinition AS AlarmDefinition ");
		sql.append(" WHERE orgRrn = ? AND objectType = ? ");
		sql.append(" AND alarmType = ? AND isEnable = 'Y' ");

		List<AlarmDefinition> alarmDfs = new ArrayList<AlarmDefinition>();
		try {		

			Query query=em.createQuery(sql.toString());
			query.setParameter(1, instance.getOrgRrn());
			query.setParameter(2, instance.getObjectType());
			query.setParameter(3, instance.getAlarmType());
			alarmDfs = (List<AlarmDefinition>)query.getResultList();
			for (AlarmDefinition alarmDf : alarmDfs) {
				alarmDf.getActions().size();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return alarmDfs;
	}
	
	public void triggerAlarm(AlarmMessage instance) throws ClientException {
		try {	
			List<AlarmDefinition> alarmDfs = getAlarmDefinitionByInstance(instance);
			for (AlarmDefinition alarmDf : alarmDfs) {
				AlarmHis his = new AlarmHis(alarmDf);
				his.setIsActive(true);
				his.setCreated(new Date());
				his.setObjectId(instance.getObjectId());
				his.setDateAlarm(new Date());
				if (his.getIsNeedClose()) {
					his.setState(AlarmHis.STATE_OPEN);
				} else {
					his.setState(AlarmHis.STATE_CLOSE);
				}
				if (instance.getAlarmText() != null && instance.getAlarmText().trim().length() != 0) {
					his.setAlarmText(instance.getAlarmText());
				} else {
					his.setAlarmText(alarmDf.getDefaultAlarmText());
				}
				his.setRefRrn(instance.getRefRrn());
				em.persist(his);
				List<Action> actionList = alarmDf.getActions();
				AlarmAction.excuteAction(actionList, his);
			}
		} catch (ClientException e) {
			throw e;
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	
//	public void doRun(String objectType,String alarmType) {	
//		AlarmDefinition alarm=null;
//		AlarmInstance alarmInstance=new AlarmInstance();
//		StringBuffer sql=new StringBuffer();
//		sql.append(" SELECT Alarm FROM Alarm as Alarm ");
//		sql.append(" Where objectType = ?  AND alarmType = ? ");
//		Query query=em.createQuery(sql.toString());
//		query.setParameter(1, objectType);
//		query.setParameter(2,alarmType);
//		alarm=(AlarmDefinition)query.getSingleResult();
//		alarmInstance.setObjectType(alarm.getObjectType());
//		alarmInstance.setAlarmType(alarm.getAlarmType());
//		alarmInstance.setObjectId(alarm.getAlarmId());
//		AlarmHelper.sendMessage(alarmInstance,this);		
//	}
//    public	List<AlarmDefinition> selectAlarmByUserRrn(long userRrn){
//    	List<AlarmDefinition> alarmIdList=new ArrayList<AlarmDefinition>();
//    	StringBuffer sql=new StringBuffer();
//    	sql.append(" select Alarm from Alarm as Alarm ");
//    	sql.append(" where alarmId in( select alarmId from AlarmPanel as AlarmPanel ");
//    	sql.append(" where userRrn = ?)");
//        Query query=em.createQuery(sql.toString());
//        query.setParameter(1, userRrn);
//        alarmIdList=query.getResultList();
//        System.out.println(alarmIdList.size());
//    	return alarmIdList;
//	}
//   public AlarmDefinition selectDetailAlarm(String objectType,String alarmType,long userRrn){
//       AlarmDefinition alarm=null;
//       try {
//		StringBuffer sql=new StringBuffer();
//		   sql.append(" select Alarm from Alarm as Alarm where objectType=? and alarmType=?");
//		   Query query=em.createQuery(sql.toString());
//		   query.setParameter(1, objectType);
//		   query.setParameter(2, alarmType);
//		   alarm=(AlarmDefinition)query.getSingleResult();
//		   List<Action> actionList=alarm.getActions();
//  	        for(Action action : actionList)
//  	        {
//  	        	if(action.getActionTypeId().equals("Mail")&&action.getParam5().equals(String.valueOf(userRrn)))
//  	        	{
//  	        		return alarm;
//  	        	}
//  	        }				
//	} catch (RuntimeException e) {
//		e.printStackTrace();
//	}
//	   return null;
//    }


   
   
}

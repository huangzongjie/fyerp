/**
 * 
 */
package com.graly.alm.client;

import java.util.Date;
import java.util.List;

import javax.persistence.OptimisticLockException;

import com.graly.alm.model.Action;
import com.graly.alm.model.AlarmDefinition;
import com.graly.alm.model.AlarmHis;
import com.graly.alm.model.AlarmMessage;
import com.graly.alm.model.AlarmPanelMessage;
import com.graly.alm.util.AlarmAction;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.security.model.ADUser;

public interface ALMManager {
	AlarmDefinition saveAlarm(AlarmDefinition alarm,long userRrn) throws ClientException ;
	void deleteAlarm(AlarmDefinition alarm, long userRrn) throws ClientException ;
	void deleteAction(Action action, long userRrn)throws ClientException;
//	void alarmHisManager(AlarmInstance alarmHis, long userRrn)throws ClientException;
	List<AlarmPanelMessage> getPanelMessages(long orgRrn, long userRrn) throws ClientException;
	
	List<AlarmDefinition> getAlarmDefinitionByInstance(AlarmMessage instance) throws ClientException ;
	void triggerAlarm(AlarmMessage instance) throws ClientException;
	void closeAlarmHis(AlarmHis alarmHis, long userRrn) throws ClientException;
	
//	List<Action> getAlarmByObjectTypeAndAlarmType(String objectType,String alarmType)throws ClientException;
//	void doRun(String objectType,String alarmType);
//	List<AlarmDefinition> selectAlarmByUserRrn(long userRrn);
//	AlarmDefinition selectDetailAlarm(String objectType,String alarmType,long userRrn);

	
}

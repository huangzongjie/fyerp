package com.graly.promisone.activeentity.client;

import java.util.Date;
import java.util.List;

import com.graly.promisone.activeentity.model.ADBase;
import com.graly.promisone.activeentity.model.ADMessage;
import com.graly.promisone.activeentity.model.ADTable;
import com.graly.promisone.activeentity.model.ADRefList;
import com.graly.promisone.activeentity.model.ADTab;
import com.graly.promisone.activeentity.model.ADUserRefList;
import com.graly.promisone.core.exception.ClientException;

public interface ADManager {
	Date getSysDate();
	ADBase saveEntity(ADBase adBase, long userId) throws ClientException;
	ADBase saveEntity(long tableId, ADBase object, long userId) throws ClientException;
	ADBase getEntity(ADBase object) throws ClientException;
	List<ADBase> getEntityList(long orgId, long tableId) throws ClientException;
	List<ADBase> getEntityList(long orgId, long tableId, int maxResult) throws ClientException;
	List<ADBase> getEntityList(long orgId, long tableId, int maxResult, String whereClause, String orderByClause) throws ClientException;
	long getEntityCount(long orgId, long tableId, String whereClause) throws ClientException;
	ADTable getADTable(long tableId) throws ClientException;
	ADTable getADTableDeep(long tableId) throws ClientException;
	ADTab getADTab(long tabId) throws ClientException;
	List<ADRefList> getADRefList(long prodArea, String refName) throws ClientException;
	List<ADMessage> getMessage() throws ClientException;
	void deleteEntity(ADBase object)throws ClientException;
	ADTable getADTable(long orgId, String tableName) throws ClientException;
	
	long getNextADSequence(long orgId, String name) throws ClientException ;
	long getNextADSequenceByDay(long orgId, String name) throws ClientException;

}

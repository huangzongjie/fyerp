package com.graly.framework.activeentity.client;

import java.util.Date;
import java.util.List;

import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADListProperty;
import com.graly.framework.activeentity.model.ADMessage;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADSingleProperty;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.activeentity.model.ADRefList;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.core.exception.ClientException;

public interface ADManager {
	
	ADTable getADTable(long tableRrn) throws ClientException;
	ADTable getADTable(long orgRrn, String tableName) throws ClientException;
	ADTable getADTableDeep(long tableRrn) throws ClientException;
	ADTab getADTab(long tabRrn) throws ClientException;
	
	List<ADRefList> getADRefList(long prodArea, String refName) throws ClientException;
	ADBase saveEntity(ADBase adBase, long userRrn) throws ClientException;
	ADBase saveEntity(long tableRrn, ADBase object, long userRrn) throws ClientException;
	List<ADMessage> getMessage() throws ClientException;
	
	ADBase getEntity(ADBase object) throws ClientException;
	
	<T> List<T> getEntityList(long orgRrn, Class<T> clazz) throws ClientException;
	<T> List<T> getEntityList(long orgRrn, Class<T> clazz, int maxResult) throws ClientException;
	<T> List<T> getEntityList(long orgRrn, Class<T> clazz, int maxResult, String whereClause, String orderByClause) throws ClientException;
	List<ADBase> getEntityList(long orgRrn, long tableRrn) throws ClientException;
	List<ADBase> getEntityList(long orgRrn, long tableRrn, int maxResult) throws ClientException;
	List<ADBase> getEntityList(long orgRrn, long tableRrn, int maxResult, String whereClause, String orderByClause) throws ClientException;
	
	long getEntityCount(long orgRrn, long tableRrn, String whereClause) throws ClientException;
	void deleteEntity(ADBase object)throws ClientException;
	
	
	Date getSysDate();
	
	long getNextSequence(long orgRrn, String name) throws ClientException ;
	long getNextSequenceByDay(long orgRrn, String name) throws ClientException;
	long getNextSequenceByMonth(long orgRrn, String name) throws ClientException;
	long getNextSequence(long orgRrn, String name, long year, long month, long day)  throws ClientException;
	
	List<ADSingleProperty> getSingleProperty() throws ClientException;
	List<ADListProperty> getListProperty() throws ClientException;
	ADRefTable getADRefTable(long orgRrn, String tableName) throws ClientException;
}

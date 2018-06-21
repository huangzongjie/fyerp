package com.graly.mes.wip.client;

import java.util.List;

import javax.persistence.EntityManager;

import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.framework.core.exception.ClientException;
import com.graly.mes.prd.model.Operation;
import com.graly.mes.prd.model.Part;
import com.graly.mes.prd.workflow.action.def.FutureAction;
import com.graly.mes.prd.workflow.action.exe.InstanceAction;
import com.graly.mes.prd.workflow.graph.def.Node;
import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotAction;
import com.graly.mes.wip.model.LotHold;
import com.graly.mes.wip.model.LotReceiveTemp;
import com.graly.mes.wip.model.LotTemp;
import com.graly.mes.wiphis.model.LotHisSBD;


public interface LotManager {
	
	EntityManager getEntityManager();
	Lot getLot(long objectId) throws ClientException;
	Lot getLotByWaferLotId(long orgId, String waferLotId) throws ClientException;
	Lot getLotByLotId(long orgId, String lotId) throws ClientException;
	List<Lot> getLotsByLotId(long orgId, String lotId) throws ClientException;
	Lot getLotWithChildren(long objectId) throws ClientException;
	Lot getLotWithParent(long objectId) throws ClientException;
	Lot getRunningLot(long objectId) throws ClientException;
	List<Operation> getCurrentOpeartionByLot(Lot lot) throws ClientException;
	
	Lot tempSaveLot(Lot lot, long userId) throws ClientException;
	Lot sceduleLot(Lot lot, long userId) throws ClientException;
	
	void trackIn(List<Lot> lots, long userId) throws ClientException;
	void trackOut(List<Lot> lots, long userId) throws ClientException;
	void holdLot(Lot lot, LotHold lotHold, String trans, long userId) throws ClientException;
	void releaseLot(Lot lot, LotAction lotAction, String holdPWd, long userId) throws ClientException;
	void splitLot(Lot parent, Lot child, long userId) throws ClientException;
	void splitLot(Lot parent, List<Lot> children, long userId) throws ClientException;
	void mergeLot(Lot parent, Lot child, long userId) throws ClientException;
	void mergeLot(Lot parent, List<Lot> children, long userId) throws ClientException;
	void shipLot(List<Lot> lots, LotAction lotAction, long userId) throws ClientException;
	void shipLot(Lot lot, LotAction lotAction, long userId) throws ClientException;
	void unShipLot(Lot lot, LotAction lotAction, long userId) throws ClientException;
	void terminate(List<Lot> lots, LotAction lotAction, long userId) throws ClientException;
	void terminate(Lot lot, LotAction lotAction, long userId) throws ClientException;
	void unTerminate(Lot lot, LotAction lotAction, long userId) throws ClientException;
	void scrapLot(Lot lot, List<LotHisSBD> lotSbds, long userId) throws ClientException;
	void unScrapLot(Lot lot, List<LotHisSBD> lotSbds, long userId) throws ClientException;
	List<Lot> signalProcess(List<Lot> lots, long userId) throws ClientException;
	void newPart(Lot lot, Part newPart, List<Node> flowList, LotAction lotAction, long userId) throws ClientException;
	void outPart(Lot lot, long newOrgId, Part newPart, List<Node> flowList, LotAction lotAction, long userId) throws ClientException;
	void changeLot(Lot lot, long userId) throws ClientException;
	void deleteLot(Lot lot, long userId) throws ClientException;
	void transfer(Lot lot, long newOrgId, String location, LotAction lotAction, long userId) throws ClientException;
	
	List<FutureAction> getFutureAction(long orgId, long stepStateRrn, long lotRrn, String actionType) throws ClientException;
	List<InstanceAction> getInstanceAction(long orgId, long lotRrn) throws ClientException;
	void removeInstanceAction(long orgId, long lotRrn) throws ClientException;
	
	void deleteLotTemp(long moLineRrn, long orgId) throws ClientException;
	void newLotTemp(List<Lot> lots, Lot parentLot,ManufactureOrderLine manufactureOrderLine,long orgId) throws ClientException;
	List<LotTemp> getLotTemp(long moLineRrn, long orgId) throws ClientException;
	
	LotReceiveTemp getLotReceiveTemp(long moLineRrn, long orgId) throws ClientException;
	void deleteLotReceiveTemp(long moLineRrn, long orgId) throws ClientException;
	void newLotReceiveTemp(Lot lot, ManufactureOrderLine manufactureOrderLine ,long orgId) throws ClientException;
}

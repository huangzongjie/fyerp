package com.graly.mes.wip.ejb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.core.exception.ClientException;
import com.graly.mes.prd.client.PrdManager;
import com.graly.mes.prd.model.Operation;
import com.graly.mes.prd.model.Part;
import com.graly.mes.prd.workflow.action.def.FutureAction;
import com.graly.mes.prd.workflow.action.exe.InstanceAction;
import com.graly.mes.prd.workflow.graph.def.Node;
import com.graly.mes.prd.workflow.graph.def.Process;
import com.graly.mes.prd.workflow.graph.def.Step;
import com.graly.mes.prd.workflow.graph.exe.ProcessInstance;
import com.graly.mes.wip.client.LotManager;
import com.graly.mes.wip.exception.LotExistException;
import com.graly.mes.wip.exception.LotNotExistException;
import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotAction;
import com.graly.mes.wip.model.LotHold;
import com.graly.mes.wip.model.LotReceiveTemp;
import com.graly.mes.wip.model.LotStateMachine;
import com.graly.mes.wip.model.LotTemp;
import com.graly.mes.wip.model.ProcessUnit;
import com.graly.mes.wip.model.QtyUnit;
import com.graly.mes.wiphis.model.ChangeLotHis;
import com.graly.mes.wiphis.model.HoldLotHis;
import com.graly.mes.wiphis.model.LotHisSBD;
import com.graly.mes.wiphis.model.LotHisSMC;
import com.graly.mes.wiphis.model.MergeLotHis;
import com.graly.mes.wiphis.model.MergeOutHis;
import com.graly.mes.wiphis.model.NewPartHis;
import com.graly.mes.wiphis.model.OutPartHis;
import com.graly.mes.wiphis.model.QtyUnitHis;
import com.graly.mes.wiphis.model.ReleaseLotHis;
import com.graly.mes.wiphis.model.ScheduleLotHis;
import com.graly.mes.wiphis.model.ScrapLotHis;
import com.graly.mes.wiphis.model.ShipLotHis;
import com.graly.mes.wiphis.model.SpiltLotHis;
import com.graly.mes.wiphis.model.SplitOutHis;
import com.graly.mes.wiphis.model.TempSaveLotHis;
import com.graly.mes.wiphis.model.TerminateLotHis;
import com.graly.mes.wiphis.model.TrackInHis;
import com.graly.mes.wiphis.model.TrackOutHis;
import com.graly.mes.wiphis.model.TransferHis;
import com.graly.mes.wiphis.model.UnScrapLotHis;
import com.graly.mes.wiphis.model.UnShipLotHis;
import com.graly.mes.wiphis.model.UnTerminateLotHis;

@Stateless
@Local(LotManager.class)
@Remote(LotManager.class)
public class LotManagerBean implements LotManager{
	private static final Logger logger = Logger.getLogger(LotManagerBean.class);
	
	@PersistenceContext
	private EntityManager em;
	
	@EJB 
	private PrdManager prdManager;
	
	public EntityManager getEntityManager() {
		return em;
	}
	
	public Lot tempSaveLot(Lot lot, long userRrn) throws ClientException {
		try{
			lot.setIsActive(true);
			lot.setUpdatedBy(userRrn);
			long partRrn = lot.getPartRrn();
			Part part = em.find(Part.class, partRrn);
			lot.setPartName(part.getName());
			lot.setPartVersion(part.getVersion());
			lot.setCurSeq(getHisSequence());
			lot.setPreTransType(LotStateMachine.TRANS_TEMPSAVE);
			if (lot.getObjectRrn() == null) {
				boolean exist = true;
				try {
					getLotByLotId(lot.getOrgRrn(), lot.getLotId());
				} catch (LotNotExistException e){
					exist = false;
				}
				if (exist) {
					throw new LotExistException();
				}
				lot.setCreatedBy(userRrn);
				lot.setCreated(new Date());
				lot.setComClass(LotStateMachine.COMCLASS_TEMP);
				lot.setState(LotStateMachine.STATE_TEMP);
				lot.setSubState(LotStateMachine.SUBSTATE_TEMP);
				em.persist(lot);
			} else {
				lot = em.merge(lot);
			}
			TempSaveLotHis lotHis = new TempSaveLotHis(lot);
			em.persist(lotHis);
			return lot;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public Lot sceduleLot(Lot lot, long userRrn) throws ClientException {
		try{
			lot.setIsActive(true);
			lot.setUpdatedBy(userRrn);
			long partRrn = lot.getPartRrn();
			Part part = em.find(Part.class, partRrn);
			lot.setPartName(part.getName());
			lot.setPartVersion(part.getVersion());
			lot.setCurSeq(getHisSequence());
			lot.setPreTransType(LotStateMachine.TRANS_SCHEDLOT);
			lot.setComClass(LotStateMachine.COMCLASS_SCHD);
			lot.setState(LotStateMachine.STATE_SCHD);
			lot.setSubState(LotStateMachine.SUBSTATE_SCHD);
			
			if (lot.getObjectRrn() == null) {
				boolean exist = true;
				try {
					getLotByLotId(lot.getOrgRrn(), lot.getLotId());
				} catch (LotNotExistException e){
					exist = false;
				}
				if (exist) {
					throw new LotExistException();
				}
				lot.setCreatedBy(userRrn);
				lot.setCreated(new Date());
				em.persist(lot);
			} else {
				lot = em.merge(lot);
			}
			ScheduleLotHis lotHis = new ScheduleLotHis(lot);
			em.persist(lotHis);
			return lot;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void deleteLot(Lot lot, long userRrn) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append(" DELETE FROM LotHis his WHERE ");
		sql.append(" his.lotRrn = ? "); 
		logger.debug(sql);
		try{
			lot = em.find(Lot.class, lot.getObjectRrn());
			em.remove(lot);
			
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, lot.getObjectRrn());
			query.executeUpdate();
			return;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void changeLot(Lot lot, long userRrn) throws ClientException {
		try{
			lot.setUpdatedBy(userRrn);
			lot.setCurSeq(getHisSequence());
			em.merge(lot);
			ChangeLotHis lotHis = new ChangeLotHis(lot);
			em.persist(lotHis);
			return;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<Operation> getCurrentOpeartionByLot(Lot lot) throws ClientException {
		try{
			long stepId = lot.getStepRrn();
			Step step = new Step();
			step.setObjectRrn(stepId);
			step = (Step)prdManager.getProcessDefinition(step);
			//.getPRDBase(step);
			List<Operation> operations = step.getOperations();
			
			long processInstanceId = lot.getProcessInstanceRrn();
			Map<String, Object> paramMap = prdManager.getCurrentParameter(processInstanceId);
			
			for (Operation operation: operations) {
				operation.replaceOperationParam(paramMap);
			}
			return operations;
//			return null;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void trackIn(List<Lot> inLots, long userRrn) throws ClientException {
		try{
			List<Lot> lots = new ArrayList<Lot>();
			for (Lot inLot : inLots) {
				Lot managedLot = em.merge(inLot);
				managedLot.setSubProcessUnit(inLot.getSubProcessUnit());
				managedLot.setParentProcessUnit(inLot.getParentProcessUnit());
				lots.add(managedLot);
			}
			//1,get Process Unit
			long orgRrn = 0;
			Set<ProcessUnit> units = new HashSet<ProcessUnit>();
			for (Lot lot : lots) {
				orgRrn = lot.getOrgRrn();
				units.add(lot);
				getProcessUnits(lot, units);
			}
			//2,get Equipment
			
			for (Lot lot : lots) {
				lot.stateTrans(LotStateMachine.TRANS_TRACKIN);
			}
			
			lots = signalProcess(lots, userRrn);
			
			long transSeq = getHisSequence();
			for (ProcessUnit unit : units) {
				unit.setUpdatedBy(userRrn);
				if (unit instanceof Lot) {
					Lot uLot = (Lot)unit;
					uLot.setCurSeq(transSeq);
					uLot.setTrackInTime(new Date());
					uLot.setPreTransType(LotStateMachine.TRANS_TRACKIN);
					TrackInHis lotHis = new TrackInHis(uLot);
					em.merge(lotHis);
				} else if (unit instanceof QtyUnit) {
					unit.setIsActive(true);
					unit.setCreatedBy(userRrn);
					unit.setCreated(new Date());
					unit.setOrgRrn(orgRrn);
					QtyUnitHis qtyHis = new QtyUnitHis((QtyUnit)unit);
					qtyHis.setHisSeq(transSeq);
					em.merge(qtyHis);
				}
				em.merge(unit);
			}

		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void trackOut(List<Lot> outLots, long userRrn) throws ClientException {
		try{
			List<Lot> lots = new ArrayList<Lot>();
			for (Lot outLot : outLots) {
				Lot managedLot = em.merge(outLot);
				managedLot.setSubProcessUnit(outLot.getSubProcessUnit());
				managedLot.setParentProcessUnit(outLot.getParentProcessUnit());
				lots.add(managedLot);
			}
			
			long orgRrn = 0;
			Set<ProcessUnit> units = new HashSet<ProcessUnit>();
			//1,get Process Unit
			for (Lot lot : lots) {
				lot = em.merge(lot);
				orgRrn = lot.getOrgRrn();
				units.add(lot);
				getProcessUnits(lot, units);
			}
		
			
			long transSeq = getHisSequence();
			for (Lot lot : lots) {
				lot.setUpdatedBy(userRrn);
				lot.setCurSeq(transSeq);
				lot.stateTrans(LotStateMachine.TRANS_TRACKOUT);
				lot.setPreTransType(LotStateMachine.TRANS_TRACKOUT);
			}
			
			lots = signalProcess(lots, userRrn);
			
			for (Lot lot : lots) {				
				TrackOutHis lotHis = new TrackOutHis(lot);
				em.merge(lotHis);
			}
			for (ProcessUnit unit : units) {
				if (unit instanceof QtyUnit) {
					QtyUnit qtyUnit = em.find(QtyUnit.class, unit.getObjectRrn());
					em.remove(qtyUnit);
					
					QtyUnitHis qtyHis = new QtyUnitHis((QtyUnit)unit);
					qtyHis.setHisSeq(transSeq);
					em.merge(qtyHis);
				}
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void holdLot(Lot lot, LotHold lotHold, String trans, long userRrn) throws ClientException {
		try{
			
			StringBuffer sql = new StringBuffer(" SELECT LotHold FROM LotHold LotHold ");
			sql.append(" WHERE lotRrn = ? ORDER BY seqNo DESC ");
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, lot.getObjectRrn());
			List<LotHold> lotHolds = query.getResultList();
			if (lotHolds.size() == 0) {
				lotHold.setSeqNo(1L);
			} else {
				lotHold.setSeqNo(lotHolds.size() + 1L);
			}
			lotHold.setLotRrn(lot.getObjectRrn());
			lotHold.setPreComClass(lot.getComClass());
			lotHold.setPreState(lot.getState());
			lotHold.setPreSubState(lot.getSubState());
			lotHold.setPreStateEntryTime(lot.getUpdated());
			em.merge(lotHold);
			lot.stateTrans(trans);
			lot.setCurSeq(getHisSequence());
			lot.setPreTransType(LotStateMachine.TRANS_HOLDLOT);
			lot = em.merge(lot);
			HoldLotHis lotHis = new HoldLotHis(lot);
			em.merge(lotHis);
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void releaseLot(Lot lot, LotAction lotAction, String holdPWd, long userRrn) throws ClientException {
		try{
			StringBuffer sql = new StringBuffer(" SELECT LotHold FROM LotHold LotHold ");
			sql.append(" WHERE lotRrn = ? ORDER BY seqNo DESC ");
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, lot.getObjectRrn());
			List<LotHold> lotHolds = query.getResultList();
			LotHold lotHold = lotHolds.get(0);
			if (lotHold != null && lotHold.getHoldPwd() != null) {
				if (!lotHold.getHoldPwd().equals(holdPWd)) {
					throw new ClientException("error.holdpwd_error");
				}
			}
			
			em.remove(lotHold);
			
			String subState = lot.getSubState();
			lot.setPreComClass(lot.getComClass());
			lot.setPreState(lot.getState());
			lot.setPreSubState(lot.getSubState());
			lot.setPreStateEntryTime(lot.getUpdated());
			lot.setComClass(lotHold.getPreComClass());
			lot.setState(lotHold.getPreState());
			lot.setSubState(lotHold.getPreSubState());
			lot.setCurSeq(getHisSequence());
			lot.setPreTransType(LotStateMachine.TRANS_RELEASELOT);
			em.merge(lot);
			ReleaseLotHis lotHis = new ReleaseLotHis(lot);
			em.merge(lotHis);
			
			if (LotStateMachine.SUBSTATE_QHLD.equals(subState) 
					|| LotStateMachine.SUBSTATE_THLD.equals(subState)) {
				List<Lot> lots = new ArrayList<Lot>();
				lots.add(lot);
				signalProcess(lots, userRrn);;
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			if (e instanceof ClientException) {
				throw (ClientException)e;
			}
			throw new ClientException(e);
		}
	}
	
	public void splitLot(Lot parent, Lot child, long userRrn) throws ClientException {
		List<Lot> children = new ArrayList<Lot>();
		children.add(child);
		splitLot(parent, children, userRrn);
	}
	
	public void splitLot(Lot parent, List<Lot> children, long userRrn) throws ClientException {
		try{
			double mainQty = 0;
			double subQty = 0;

			Long instanceId = parent.getProcessInstanceRrn();
			String lotId = parent.getLotId();
			String lotName = lotId;
			if (lotId.indexOf(".") != -1) {
				lotName = lotId.substring(0, lotId.indexOf("."));
			}
			int version = getLastVersion(parent);
			
			long transSeq = getHisSequence();
			parent.setCurSeq(transSeq);
			List<LotHisSMC> smcs = new ArrayList<LotHisSMC>();
			for (Lot child : children) {
				child.setCurSeq(transSeq);
				child.setCreatedBy(userRrn);
				child.setCreated(new Date());
				child.setUpdatedBy(userRrn);
				child.setLotId(lotName + "." + (String.format("%02d", ++version)));
				child.setParentLotRrn(parent.getObjectRrn());
				ProcessInstance instance = prdManager.cloneProcessInstance(instanceId);
				child.setProcessInstanceRrn(instance.getObjectRrn());
				mainQty += child.getMainQty() == null ? 0 : child.getMainQty();
				subQty += child.getSubQty() == null ? 0 : child.getSubQty();
				child.setPreTransType(LotStateMachine.TRANS_SPLITLOT);
				em.merge(child);
				
				LotHisSMC smc = new LotHisSMC();
				//smc.setHisSeq(transSeq);
				smc.setOrgRrn(parent.getOrgRrn());
				smc.setIsActive(parent.getIsActive());
				smc.setUpdatedBy(userRrn);
				smc.setToLotId(child.getLotId());
				smc.setToSeqNo((long)children.indexOf(child));
				smc.setToPartRrn(child.getPartRrn());
				smc.setToMainQty(child.getMainQty());
				smc.setToSubQty(child.getSubQty());
				smcs.add(smc);
				
				SplitOutHis childHis = new SplitOutHis(child);
				em.merge(childHis);
			}
			if (parent.getMainQty() != null) {
				parent.setMainQty(parent.getMainQty() - mainQty);
			}
			if (parent.getSubQty() != null) {
				parent.setSubQty(parent.getSubQty() - subQty);
			}
			if (!((parent.getMainQty() != null && parent.getMainQty() != 0) || 
					(parent.getSubQty() != null && parent.getSubQty() != 0))) {
				parent.stateTrans(LotStateMachine.TRANS_TERMLOT);
			}
			parent.setUpdatedBy(userRrn);
			parent.setPreTransType(LotStateMachine.TRANS_SPLITLOT);
			em.merge(parent);
			
			SpiltLotHis parentHis = new SpiltLotHis(parent);
			parentHis.setSmcs(smcs);
			em.persist(parentHis);
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void mergeLot(Lot parent, Lot child, long userRrn) throws ClientException {
		List<Lot> children = new ArrayList<Lot>();
		children.add(child);
		mergeLot(parent, children, userRrn);
	}
	
	public void mergeLot(Lot parent, List<Lot> children, long userRrn) throws ClientException {
		try{
			double mainQty = 0;
			double subQty = 0;
			
			long transSeq = getHisSequence();
			parent.setCurSeq(transSeq);
			List<LotHisSMC> smcs = new ArrayList<LotHisSMC>();
			for (Lot child : children) {
				child.setCurSeq(transSeq);
				child.setUpdatedBy(userRrn);
				mainQty += child.getMainQty() == null ? 0 : child.getMainQty();
				subQty += child.getSubQty() == null ? 0 : child.getSubQty();
				child.setMainQty(child.getMainQty() == null ? null : 0D);
				child.setSubQty(child.getSubQty() == null ? null : 0D);
				child.stateTrans(LotStateMachine.TRANS_TERMLOT);
				child.setPreTransType(LotStateMachine.TRANS_MERGELOT);
				em.merge(child);
				
				LotHisSMC smc = new LotHisSMC();
				//smc.setHisSeq(transSeq);
				smc.setOrgRrn(parent.getOrgRrn());
				smc.setIsActive(parent.getIsActive());
				smc.setUpdatedBy(userRrn);
				smc.setFromLotId(child.getLotId());
				smc.setFromSeqNo((long)children.indexOf(child));
				smc.setFromPartRrn(child.getPartRrn());
				smc.setFromMainQty(child.getMainQty());
				smc.setFromSubQty(child.getSubQty());
				smcs.add(smc);
				
				MergeOutHis childHis = new MergeOutHis(child);
				em.merge(childHis);
			}
			if (parent.getMainQty() != null) {
				parent.setMainQty(parent.getMainQty() + mainQty);
			}
			if (parent.getSubQty() != null) {
				parent.setSubQty(parent.getSubQty() + subQty);
			}
			parent.setUpdatedBy(userRrn);
			parent.setPreTransType(LotStateMachine.TRANS_MERGELOT);
			em.merge(parent);
			
			MergeLotHis parentHis = new MergeLotHis(parent);
			parentHis.setSmcs(smcs);
			em.persist(parentHis);
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void scrapLot(Lot lot, List<LotHisSBD> lotSbds, long userRrn) throws ClientException {
		try{
			double mainQty = 0;
			double subQty = 0;
		
			long transSeq = getHisSequence();
			lot.setCurSeq(transSeq);
			for (LotHisSBD sbd : lotSbds) {
				mainQty += sbd.getMainQty() == null ? 0 : sbd.getMainQty();
				subQty += sbd.getSubQty() == null ? 0 : sbd.getSubQty();
			}
			if (lot.getMainQty() != null) {
				lot.setMainQty(lot.getMainQty() - mainQty);
			}
			if (lot.getSubQty() != null) {
				lot.setSubQty(lot.getSubQty() - subQty);
			}
			String preState = lot.getState();
			if (!((lot.getMainQty() != null && lot.getMainQty() != 0) || 
				(lot.getSubQty() != null && lot.getSubQty() != 0))) {
				lot.stateTrans(LotStateMachine.TRANS_SCRAPLOT);
			}
			lot.setUpdatedBy(userRrn);
			lot.setPreTransType(LotStateMachine.TRANS_SCRAPLOT);
			if (LotStateMachine.STATE_RUN.equalsIgnoreCase(preState) 
					&& LotStateMachine.STATE_COM.equalsIgnoreCase(lot.getState())) {
				Set<ProcessUnit> units = new HashSet<ProcessUnit>();
				Lot runLot = getRunningLot(lot.getObjectRrn());
				getProcessUnits(runLot, units);
				for (ProcessUnit unit : units) {
					if (unit instanceof QtyUnit) {
						QtyUnit qtyUnit = em.find(QtyUnit.class, unit.getObjectRrn());
						em.remove(qtyUnit);
					}
				}
				lot.setEquipmentId(null);
				lot.setEquipmentRrn(null);
			}

			em.merge(lot);
			
			ScrapLotHis parentHis = new ScrapLotHis(lot);
			parentHis.setSbds(lotSbds);
			em.persist(parentHis);
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void unScrapLot(Lot lot, List<LotHisSBD> lotSbds, long userRrn) throws ClientException {
		try{
			double mainQty = 0;
			double subQty = 0;
		
			long transSeq = getHisSequence();
			lot.setCurSeq(transSeq);
			if (LotStateMachine.STATE_COM.equalsIgnoreCase(lot.getState())) {
				lot.stateTrans(LotStateMachine.TRANS_UNSCRAPLOT);
			}
			for (LotHisSBD sbd : lotSbds) {
				mainQty += sbd.getMainQty() == null ? 0 : sbd.getMainQty();
				subQty += sbd.getSubQty() == null ? 0 : sbd.getSubQty();
			}
			if (lot.getMainQty() != null) {
				lot.setMainQty(lot.getMainQty() + mainQty);
			}
			if (lot.getSubQty() != null) {
				lot.setSubQty(lot.getSubQty() + subQty);
			}
			lot.setUpdatedBy(userRrn);
			lot.setPreTransType(LotStateMachine.TRANS_UNSCRAPLOT);
			em.merge(lot);
			
			UnScrapLotHis parentHis = new UnScrapLotHis(lot);
			parentHis.setSbds(lotSbds);
			em.persist(parentHis);
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	public void shipLot(List<Lot> lots, LotAction lotAction, long userRrn) throws ClientException {
		if (lots != null) {
			for (Lot lot : lots) {
				shipLot(lot, lotAction, userRrn);
			}
		}
	}

	public void shipLot(Lot lot, LotAction lotAction, long userRrn) throws ClientException {
		try{
			lot.stateTrans(LotStateMachine.TRANS_SHIPLOT);
			long transSeq = getHisSequence();
			lot.setUpdatedBy(userRrn);
			lot.setCurSeq(transSeq);
			lot.setPreTransType(LotStateMachine.TRANS_SHIPLOT);
			em.merge(lot);
			ShipLotHis lotHis = new ShipLotHis(lot);
			lotHis.setActionCode(lotAction.getActionCode());
			lotHis.setActionReason(lotAction.getActionReason());
			lotHis.setActionComment(lotAction.getActionComment());
			em.merge(lotHis);
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void unShipLot(Lot lot, LotAction lotAction, long userRrn) throws ClientException {
		try{
			if (!LotStateMachine.TRANS_SHIPLOT.equalsIgnoreCase(lot.getPreTransType())) {
				throw new ClientException("error.pre_trans_error");
			}
			String preComClass = lot.getPreComClass();
			String preState = lot.getPreState();
			String preSubState = lot.getPreSubState();
			lot.setPreComClass(lot.getComClass());
			lot.setPreState(lot.getState());
			lot.setPreSubState(lot.getSubState());
			lot.setPreStateEntryTime(lot.getUpdated());
			lot.setComClass(preComClass);
			lot.setState(preState);
			lot.setSubState(preSubState);
			lot.setCurSeq(getHisSequence());
			lot.setPreTransType(LotStateMachine.TRANS_UNSHIPLOT);

			long transSeq = getHisSequence();
			lot.setUpdatedBy(userRrn);
			lot.setCurSeq(transSeq);
			em.merge(lot);
			UnShipLotHis lotHis = new UnShipLotHis(lot);
			lotHis.setActionCode(lotAction.getActionCode());
			lotHis.setActionReason(lotAction.getActionReason());
			lotHis.setActionComment(lotAction.getActionComment());
			em.merge(lotHis);
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void terminate(List<Lot> lots, LotAction lotAction, long userRrn) throws ClientException {
		if (lots != null) {
			for (Lot lot : lots) {
				terminate(lot, lotAction, userRrn);
			}
		}
	}

	public void terminate(Lot lot, LotAction lotAction, long userRrn) throws ClientException {
		try{
			lot.stateTrans(LotStateMachine.TRANS_TERMLOT);
			long transSeq = getHisSequence();
			lot.setUpdatedBy(userRrn);
			lot.setCurSeq(transSeq);
			lot.setPreTransType(LotStateMachine.TRANS_TERMLOT);
			em.merge(lot);
			TerminateLotHis lotHis = new TerminateLotHis(lot);
			lotHis.setActionCode(lotAction.getActionCode());
			lotHis.setActionReason(lotAction.getActionReason());
			lotHis.setActionComment(lotAction.getActionComment());
			em.merge(lotHis);
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void unTerminate(Lot lot, LotAction lotAction, long userRrn) throws ClientException {
		try{
			if (!LotStateMachine.TRANS_TERMLOT.equalsIgnoreCase(lot.getPreTransType())) {
				throw new ClientException("error.pre_trans_error");
			}
			String preComClass = lot.getPreComClass();
			String preState = lot.getPreState();
			String preSubState = lot.getPreSubState();
			lot.setPreComClass(lot.getComClass());
			lot.setPreState(lot.getState());
			lot.setPreSubState(lot.getSubState());
			lot.setPreStateEntryTime(lot.getUpdated());
			lot.setComClass(preComClass);
			lot.setState(preState);
			lot.setSubState(preSubState);
			lot.setPreTransType(LotStateMachine.TRANS_UNTERMLOT);

			long transSeq = getHisSequence();
			lot.setUpdatedBy(userRrn);
			lot.setCurSeq(transSeq);
			em.merge(lot);
			UnTerminateLotHis lotHis = new UnTerminateLotHis(lot);
			lotHis.setActionCode(lotAction.getActionCode());
			lotHis.setActionReason(lotAction.getActionReason());
			lotHis.setActionComment(lotAction.getActionComment());
			em.merge(lotHis);
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void newPart(Lot lot, Part newPart, List<Node> nodeList, LotAction lotAction, long userRrn) throws ClientException {
		try{
			lot = em.merge(lot);
			Process process = prdManager.getPartProcess(newPart);
			ProcessInstance instance = prdManager.createProcessInstance(process.getObjectRrn(), nodeList, lot.getObjectRrn());
			if (instance == null) {
				throw new ClientException("error.no_process_found");
			}

			long transSeq = getHisSequence();
			lot.setPartRrn(newPart.getObjectRrn());
			lot.setPartName(newPart.getName());
			lot.setPartVersion(newPart.getVersion());
			lot.setUpdatedBy(userRrn);
			lot.setCurSeq(transSeq);
			lot.setProcessInstanceRrn(instance.getObjectRrn());
			lot.setPreTransType(LotStateMachine.TRANS_NEWPART);
			em.merge(lot);
			
			NewPartHis lotHis = new NewPartHis(lot);
			lotHis.setActionComment(lotAction.getActionComment());
			em.persist(lotHis);
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void outPart(Lot lot, long newOrgRrn, Part newPart, List<Node> nodeList, LotAction lotAction, long userRrn) throws ClientException {
		try{
			boolean exist = true;
			try {
				getLotByLotId(newOrgRrn, lot.getLotId());
			} catch (LotNotExistException e){
				exist = false;
			}
			if (exist) {
				throw new LotExistException();
			}
			lot = em.merge(lot);
			lot.setOrgRrn(newOrgRrn);

			Process process = prdManager.getPartProcess(newPart);
			ProcessInstance instance = prdManager.createProcessInstance(process.getObjectRrn(), nodeList, lot.getObjectRrn());
			if (instance == null) {
				throw new ClientException("error.no_process_found");
			}
			long transSeq = getHisSequence();
			lot.setPartRrn(newPart.getObjectRrn());
			lot.setPartName(newPart.getName());
			lot.setPartVersion(newPart.getVersion());
			lot.setUpdatedBy(userRrn);
			lot.setCurSeq(transSeq);
			lot.setProcessInstanceRrn(instance.getObjectRrn());
			lot.setPreTransType(LotStateMachine.TRANS_TRANSFER);
			em.merge(lot);
			
			OutPartHis lotHis = new OutPartHis(lot);
			lotHis.setActionComment(lotAction.getActionComment());
			em.persist(lotHis);
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void transfer(Lot lot, long newOrgRrn, String location, LotAction lotAction, long userRrn) throws ClientException {
		try{
			boolean exist = true;
			try {
				getLotByLotId(newOrgRrn, lot.getLotId());
			} catch (LotNotExistException e){
				exist = false;
			}
			if (exist) {
				throw new LotExistException();
			}
			
			lot.setOrgRrn(newOrgRrn);
			lot.setLocation(location);
			long transSeq = getHisSequence();
			lot.setUpdatedBy(userRrn);
			lot.setCurSeq(transSeq);
			lot.setPreTransType(LotStateMachine.TRANS_TRANSFER);
			em.merge(lot);
			
			TransferHis lotHis = new TransferHis(lot);
			lotHis.setActionComment(lotAction.getActionComment());
			em.merge(lotHis);
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public Lot getLot(long objectId) throws ClientException {
		try{
			Lot lot = em.find(Lot.class, objectId);
//			lot.getParameters().size();
			return lot;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public Lot getLotByLotId(long orgRrn, String lotId) throws ClientException {
		try{
			StringBuffer sql = new StringBuffer(" SELECT Lot FROM Lot Lot ");
			sql.append(" WHERE ");
			sql.append(ADBase.BASE_CONDITION);
			sql.append(" AND lotId = ? ");
			
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, lotId);
			List<Lot> lotList = query.getResultList();
			if (lotList == null || lotList.size() == 0) {
				throw new LotNotExistException();
			}
			Lot lot = lotList.get(0);
//			lot.getParameters().size();
			return lot;
		} catch (LotNotExistException e){
			throw e;
		}  catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<Lot> getLotsByLotId(long orgId, String lotId) throws ClientException {
		try{
			StringBuffer sql = new StringBuffer(" SELECT Lot FROM Lot Lot ");
			sql.append(" WHERE ");
			sql.append(ADBase.BASE_CONDITION);
			sql.append(" AND state = 'WAIT' ");
			sql.append(" AND lotId LIKE ? ");
			
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgId);
			query.setParameter(2, lotId);
			List<Lot> lotList = query.getResultList();
			
			return lotList;
		}  catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public Lot getLotWithChildren(long lotRrn) throws ClientException {
		try{
			Lot lot = em.find(Lot.class, lotRrn);
			StringBuffer sql = new StringBuffer(" SELECT Lot FROM Lot Lot ");
			sql.append(" WHERE ");
			sql.append(" parentLotRrn = ? ");
			sql.append(" ORDER BY lotId ");
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, lotRrn);
			List<Lot> children = query.getResultList();
			lot.setChildrenLots(children);
			
			return lot;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public Lot getLotWithParent(long lotRrn) throws ClientException {
		try{
			Lot lot = em.find(Lot.class, lotRrn);
			if (lot.getParentLotRrn() != null) {
				Lot parent = em.find(Lot.class, lot.getParentLotRrn());
				lot.setParentLot(parent);
			} 
			return lot;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public Lot getRunningLot(long lotRrn) throws ClientException {
		try{
			Lot lot = em.find(Lot.class, lotRrn);
			if (QtyUnit.getUnitType().equalsIgnoreCase(lot.getSubUnitType())) {
				StringBuffer sql = new StringBuffer(" SELECT QtyUnit FROM QtyUnit QtyUnit ");
				sql.append(" WHERE ");
				sql.append(" parentUnitRrn = ? ");
				Query query = em.createQuery(sql.toString());
				query.setParameter(1, lotRrn);
				List<ProcessUnit> units = query.getResultList();
				lot.setSubProcessUnit(units);
			}
			return lot;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public int getLastVersion(Lot lot) throws ClientException {
		try{
			String lotId = lot.getLotId();
			String lotName = lotId;
			if (lotId.indexOf(".") != -1) {
				lotName = lotId.substring(0, lotId.indexOf("."));
			}
			
			StringBuffer sql = new StringBuffer(" SELECT Lot FROM Lot Lot ");
			sql.append(" WHERE ");
			sql.append(ADBase.BASE_CONDITION);
			sql.append(" AND lotId LIKE ? ");
			sql.append(" ORDER BY lotId DESC ");
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, lot.getOrgRrn());
			query.setParameter(2, lotName + ".%");
			List<Lot> lots = query.getResultList();
			if (lots.size() == 0) {
				return 0;
			}
			Lot lastLot = lots.get(0);
			String lastLotId = lastLot.getLotId();
			String lastVersion = lastLotId.substring(lastLotId.indexOf(".") + 1, lastLotId.length());
			return Integer.parseInt(lastVersion);
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	public List<Lot> signalProcess(List<Lot> lots, long userRrn) throws ClientException {
		try{ 
			List<Lot> rLots = new ArrayList<Lot>();
			for (Lot lot : lots) {
				Long instanceRrn = lot.getProcessInstanceRrn();
				if (instanceRrn != null) {
					prdManager.signalProcess(instanceRrn);
				} else {
					logger.error("Can not moveNext Lot " + lot.getLotId() + ", instanceId is null");
					throw new ClientException("Can not moveNext Lot " + lot.getLotId() + ", instanceId is null");
				}
			}
			return rLots;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	private long getHisSequence(){
			StringBuffer sql = new StringBuffer(" SELECT HIS_SEQ.NEXTVAL FROM DUAL ");			
			Query query = em.createNativeQuery(sql.toString());

			return ((BigDecimal)query.getSingleResult()).longValue();
	}
	
	private void getProcessUnits(ProcessUnit currentUnit, Set<ProcessUnit> units) throws ClientException {
		if (currentUnit.getSubProcessUnit() != null && currentUnit.getSubProcessUnit().size() > 0) {
			for (ProcessUnit unit : currentUnit.getSubProcessUnit()) {
				units.add(unit);
				if (unit.getSubProcessUnit() != null && unit.getSubProcessUnit().size() > 0) {
					getProcessUnits(unit, units);
				} else {
					continue;
				}
			}
		}
	}

	@Override
	public Lot getLotByWaferLotId(long orgId, String waferLotId) throws ClientException {
		try{
			StringBuffer sql = new StringBuffer(" SELECT Lot FROM Lot Lot ");
			sql.append(" WHERE ");
			sql.append(ADBase.BASE_CONDITION);
			sql.append(" AND waferLotId = ? ");
			
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgId);
			query.setParameter(2, waferLotId);
			List<Lot> lotList = query.getResultList();
			if (lotList == null || lotList.size() == 0) {
				throw new LotNotExistException();
			}
			Lot lot = lotList.get(0);
//			lot.getParameters().size();
			return lot;
		} catch (LotNotExistException e){
			throw e;
		}  catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<FutureAction> getFutureAction(long orgId, long stepStateRrn, 
			long lotRrn, String actionType) throws ClientException{
		StringBuffer sqlStep = new StringBuffer(" SELECT FutureAction FROM FutureAction FutureAction ");
		sqlStep.append(" WHERE ");
		sqlStep.append(ADBase.BASE_CONDITION);
		sqlStep.append(" AND stepStateRrn = ? AND lotRrn IS NULL ");
		sqlStep.append(" AND actionType = ? ");
		sqlStep.append(" ORDER BY seqNo ");
		StringBuffer sqlLot = new StringBuffer(" SELECT FutureAction FROM FutureAction FutureAction ");
		sqlLot.append(" WHERE ");
		sqlLot.append(ADBase.BASE_CONDITION);
		sqlLot.append(" AND stepStateRrn = ? AND lotRrn = ? ");
		sqlLot.append(" AND actionType = ? ");
		sqlLot.append(" ORDER BY seqNo ");
		List<FutureAction> actions = new ArrayList<FutureAction>();
		try{		
			Query query = em.createQuery(sqlStep.toString());
			query.setParameter(1, orgId);
			query.setParameter(2, stepStateRrn);
			query.setParameter(3, actionType);
			List<FutureAction> stepActions = query.getResultList();
			if (stepActions != null) {
				actions.addAll(stepActions);
			}
			query = em.createQuery(sqlLot.toString());
			query.setParameter(1, orgId);
			query.setParameter(2, stepStateRrn);
			query.setParameter(3, lotRrn);
			query.setParameter(4, actionType);
			List<FutureAction> lotActions = query.getResultList();
			if (lotActions != null) {
				actions.addAll(lotActions);
			}
			return actions;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<InstanceAction> getInstanceAction(long orgId, long lotRrn) throws ClientException{
		StringBuffer sql = new StringBuffer(" SELECT InstanceAction FROM InstanceAction InstanceAction ");
		sql.append(" WHERE ");
		sql.append(ADBase.BASE_CONDITION);
		sql.append(" AND instanceKey = ? ");
		sql.append(" ORDER BY seqNo ");
		try{		
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgId);
			query.setParameter(2, lotRrn);
			List<InstanceAction> actions = query.getResultList();
			return actions;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void removeInstanceAction(long orgId, long lotRrn) throws ClientException{
		StringBuffer sql = new StringBuffer(" DELETE FROM InstanceAction ");
		sql.append(" WHERE ");
		sql.append(ADBase.BASE_CONDITION);
		sql.append(" AND instanceKey = ? ");
		try{		
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgId);
			query.setParameter(2, lotRrn);
			query.executeUpdate();
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void deleteLotTemp(long moLineRrn, long orgId) throws ClientException{
		try {
			deleteLotReceiveTemp( moLineRrn,   orgId);
			StringBuffer sql = new StringBuffer(" DELETE FROM  LotTemp ");
			sql.append(" WHERE ");
			sql.append(ADBase.BASE_CONDITION);
			sql.append(" AND moLineRrn = ? ");
			
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgId);
			query.setParameter(2, moLineRrn);
			query.executeUpdate();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	public void newLotTemp(List<Lot> lots,Lot parentLot, ManufactureOrderLine manufactureOrderLine ,long orgId) throws ClientException{
		try {
			deleteLotTemp(manufactureOrderLine.getObjectRrn(), orgId);
			newLotReceiveTemp(parentLot,   manufactureOrderLine ,  orgId);
			for (Lot lot : lots) {
				LotTemp lotTemp = new LotTemp();
				lotTemp.setOrgRrn(orgId);
				lotTemp.setCreated(new Date());
				lotTemp.setIsActive(true);
				lotTemp.setMasterMoId(manufactureOrderLine.getMasterMoId());
				lotTemp.setLotID(lot.getLotId());
				lotTemp.setMoLineRrn(manufactureOrderLine.getObjectRrn());
				lotTemp.setMaterialId(lot.getMaterialId());
				lotTemp.setMaterialName(lot.getMaterialName());
				lotTemp.setMainQty(lot.getQtyTransaction());
				em.persist(lotTemp);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	public List<LotTemp> getLotTemp(long moLineRrn, long orgId) throws ClientException{
		try {
			StringBuffer sql = new StringBuffer(" SELECT LotTemp FROM LotTemp LotTemp ");
			sql.append(" WHERE ");
			sql.append(ADBase.BASE_CONDITION);
			sql.append(" AND moLineRrn = ? ");
			
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgId);
			query.setParameter(2, moLineRrn);
			List<LotTemp> lotTemps = query.getResultList();
			return lotTemps;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	public LotReceiveTemp getLotReceiveTemp(long moLineRrn, long orgId) throws ClientException{
		try {
			StringBuffer sql = new StringBuffer(" SELECT LotReceiveTemp FROM LotReceiveTemp LotReceiveTemp ");
			sql.append(" WHERE ");
			sql.append(ADBase.BASE_CONDITION);
			sql.append(" AND moLineRrn = ? ");
			
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgId);
			query.setParameter(2, moLineRrn);
			List<LotReceiveTemp> lotReceiveTemps = query.getResultList();
			LotReceiveTemp lotReceiveTemp =null;
			if(lotReceiveTemps !=null &&lotReceiveTemps.size()>0){
				lotReceiveTemp=lotReceiveTemps.get(0);
			}
			return lotReceiveTemp;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	public void deleteLotReceiveTemp(long moLineRrn, long orgId) throws ClientException{
		try {
			StringBuffer sql = new StringBuffer(" DELETE FROM  LotReceiveTemp ");
			sql.append(" WHERE ");
			sql.append(ADBase.BASE_CONDITION);
			sql.append(" AND moLineRrn = ? ");
			
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgId);
			query.setParameter(2, moLineRrn);
			query.executeUpdate();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void newLotReceiveTemp(Lot lot, ManufactureOrderLine manufactureOrderLine ,long orgId) throws ClientException{
		try {
			LotReceiveTemp lotReceiveTemp = new LotReceiveTemp();
			lotReceiveTemp.setOrgRrn(orgId);
			lotReceiveTemp.setCreated(new Date());
			lotReceiveTemp.setIsActive(true);
			lotReceiveTemp.setLotID(lot.getLotId());
			lotReceiveTemp.setMoLineRrn(manufactureOrderLine.getObjectRrn());
			lotReceiveTemp.setMaterialId(lot.getMaterialId());
			lotReceiveTemp.setMaterialName(lot.getMaterialName());
			lotReceiveTemp.setMainQty(lot.getQtyTransaction());
			lotReceiveTemp.setUserQc(lot.getUserQc());
			lotReceiveTemp.setEquipmentId(lot.getEquipmentId());
			lotReceiveTemp.setMoldId(lot.getMoldId());
			lotReceiveTemp.setReverseField1(lot.getReverseField1());
			lotReceiveTemp.setReverseField2(lot.getReverseField2());
			lotReceiveTemp.setReverseField3(lot.getReverseField3());
			lotReceiveTemp.setReverseField4(lot.getReverseField4());
			lotReceiveTemp.setReverseField5(lot.getReverseField5());
			lotReceiveTemp.setLotComment(lot.getLotComment());
			em.persist(lotReceiveTemp);
			 
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	
}

package com.graly.erp.ppm.ejb;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.graly.erp.base.model.DocumentationLine;
import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.pdm.model.BomDetail;
import com.graly.erp.ppm.client.PPMManager;
import com.graly.erp.ppm.model.InternalOrder;
import com.graly.erp.ppm.model.InternalOrderLine;
import com.graly.erp.ppm.model.Lading;
import com.graly.erp.ppm.model.Mps;
import com.graly.erp.ppm.model.MpsLine;
import com.graly.erp.ppm.model.MpsLineBom;
import com.graly.erp.ppm.model.MpsLineComparator;
import com.graly.erp.ppm.model.MpsLineDelivery;
import com.graly.erp.ppm.model.MpsStatistcLine;
import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.erp.ppm.model.SalePlanLine;
import com.graly.erp.ppm.model.TpsLine;
import com.graly.erp.ppm.model.TpsLinePrepare;
import com.graly.erp.ppm.model.TpsLineSysRun;
import com.graly.erp.pur.model.Requisition;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.erp.wip.model.MaterialSum;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.core.exception.ClientParameterException;
import com.graly.framework.security.model.ADUser;
import com.graly.mes.wip.client.WipManager;

@Stateless
@Remote(PPMManager.class)
@Local(PPMManager.class)
public class PPMManagerBean implements PPMManager{
	private static final Logger logger = Logger.getLogger(PPMManagerBean.class);
	
	@PersistenceContext
	private EntityManager em;
	
	@EJB
	private ADManager adManager;
	
	@EJB
	private PDMManager pdmManager;
	
	@EJB
	private WipManager wipManager;
	
	public Mps savePlanSetup(long tableRrn, Mps mps, long userRrn) throws ClientException {
		StringBuffer sql1 = new StringBuffer();
		sql1.append("SELECT Mps FROM Mps as Mps ");
		sql1.append(" WHERE ");
		sql1.append(ADBase.BASE_CONDITION);
		sql1.append(" AND ((dateStart <= ? AND dateEnd >= ?) ");
		sql1.append(" OR (dateStart <= ? AND dateEnd >= ?)) ");
		
		StringBuffer sql2 = new StringBuffer();
		sql2.append("SELECT Mps FROM Mps as Mps ");
		sql2.append(" WHERE ");
		sql2.append(ADBase.BASE_CONDITION);
		sql2.append(" AND ((dateStart > ? AND dateStart < ?) ");
		sql2.append(" OR (dateEnd > ? AND dateEnd < ?)) ");
		
		List<Mps> setups = new ArrayList<Mps>();

		try{
			if (mps.getObjectRrn() != null) {
				sql1.append(" AND objectRrn != '" + mps.getObjectRrn() + "'");
				sql2.append(" AND objectRrn != '" + mps.getObjectRrn() + "'");
			}
			Query query = em.createQuery(sql1.toString());
			query.setParameter(1, mps.getOrgRrn());
			query.setParameter(2, mps.getDateStart());
			query.setParameter(3, mps.getDateStart());
			query.setParameter(4, mps.getDateEnd());
			query.setParameter(5, mps.getDateEnd());
			setups = query.getResultList();
			if (setups.size() > 0) {
				throw new ClientException("ppm.time_range_overlap");
			}
			query = em.createQuery(sql2.toString());
			query.setParameter(1, mps.getOrgRrn());
			query.setParameter(2, mps.getDateStart());
			query.setParameter(3, mps.getDateEnd());
			query.setParameter(4, mps.getDateStart());
			query.setParameter(5, mps.getDateEnd());
			setups = query.getResultList();
			if (setups.size() > 0) {
				throw new ClientException("ppm.time_range_overlap");
			}
			mps = (Mps)adManager.saveEntity(tableRrn, mps, userRrn);
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (ClientException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return mps;
	}
	
	public void checkPlanSetup(long orgRrn, String mpsId, Date checkDate) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT Mps FROM Mps as Mps ");
		sql.append(" WHERE ");
		sql.append(ADBase.BASE_CONDITION);
		sql.append(" AND mpsId = ?  ");
		try{
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, mpsId);
			List<Mps> setups = query.getResultList();
			if (setups.size() > 0) {
				Mps setup = setups.get(0);
				Date current = new Date();
				if (setup.getDateReserved().compareTo(current) <= 0) {
					throw new ClientException("ppm.plan_is_lock");
				}
				if (checkDate != null) {
					if (setup.getDateStart().compareTo(checkDate) > 0 || 
							setup.getDateEnd().compareTo(checkDate) < 0) {
						throw new ClientException("ppm.deliverydate_is_out");
					}
				}
			}
		} catch (ClientException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public SalePlanLine saveSalePlanLine(long tableRrn, SalePlanLine salePlanLine, long userRrn) throws ClientException {
		try{
			checkPlanSetup(salePlanLine.getOrgRrn(), salePlanLine.getMpsId(), salePlanLine.getDateDelivered());
			salePlanLine = (SalePlanLine)adManager.saveEntity(tableRrn, salePlanLine, userRrn);
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (ClientException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return salePlanLine;
	} 
	
	public Lading saveLading(long tableRrn, Lading lading, long userRrn) throws ClientException {
		try{
			checkPlanSetup(lading.getOrgRrn(), lading.getMpsId(), null);
			lading = (Lading)adManager.saveEntity(tableRrn, lading, userRrn);
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (ClientException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return lading;
	}
	
	public MpsLine saveMpsLine(long tableRrn, MpsLine mpsLine, long userRrn) throws ClientException {
		try{
			checkPlanSetup(mpsLine.getOrgRrn(), mpsLine.getMpsId(), mpsLine.getDateDelivered());
			mpsLine = (MpsLine)adManager.saveEntity(tableRrn, mpsLine, userRrn);
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (ClientException e){
			logger.error(e.getMessage(), e);
			throw e; 
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return mpsLine;
	}
	
	//按照物料编号和发货日期对SalePlan进行汇总
	public List<SalePlanLine> getSalePlanLineSum(Mps mps) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("  SELECT MATERIAL_RRN, DATE_DELIVERED, NVL(SUM(QTY_SALEPLAN), 0) QTY_SALEPLAN FROM PPM_SALEPLAN_LINE ");
		sql.append(" WHERE ORG_RRN = ? AND MPS_ID = ? ");
		sql.append(" GROUP BY MATERIAL_RRN, DATE_DELIVERED ");
		sql.append(" ORDER BY MATERIAL_RRN, DATE_DELIVERED ");
		List<SalePlanLine> salePlanLines = new ArrayList<SalePlanLine>();
		try {
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, mps.getOrgRrn());
			query.setParameter(2, mps.getMpsId());
			List<Object[]> result = (List<Object[]>)query.getResultList();
			for (Object[] row : result) {
				SalePlanLine line = new SalePlanLine();
				line.setMaterialRrn(Long.parseLong(String.valueOf(row[0])));
				line.setDateDelivered((Date)row[1]);
				line.setQtySalePlan((BigDecimal)row[2]);
				salePlanLines.add(line);
			}
			return salePlanLines;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void generateMpsLine(Mps mps, long materialRrn, List<SalePlanLine> salePlanLines, long userRrn) throws ClientException {
		StringBuffer sqlLading = new StringBuffer();
		sqlLading.append("  SELECT NVL(SUM(QTY_LADING), 0) FROM PPM_SALEPLAN_LINE ");
		sqlLading.append(" WHERE ORG_RRN = ? AND MPS_ID = ? ");
		sqlLading.append(" AND MATERIAL_RRN = ? ");
		
		try {
			BigDecimal qtyMps = BigDecimal.ZERO;
			//汇总物料库存、已分配等数据
			MaterialSum materialSum = wipManager.getMaterialSum(mps.getOrgRrn(), materialRrn, false, true);
			if (materialSum == null) {
				return;
			}
			Query query = em.createNativeQuery(sqlLading.toString());
			query.setParameter(1, mps.getOrgRrn());
			query.setParameter(2, mps.getMpsId());
			query.setParameter(3, materialRrn);
			BigDecimal qtyLading = (BigDecimal)query.getSingleResult();
			//可用数量=库存数量+在途数量(PR+PO)+MoLine在制品数量-SO数量-已分配数量-提留数量-最低库存
			BigDecimal qtyAvailable = materialSum.getQtyOnHand().add(materialSum.getQtyTransit())
				.add(materialSum.getQtyMoLineWip()).subtract(materialSum.getQtySo())
				.subtract(materialSum.getQtyAllocation()).subtract(materialSum.getQtyMin()).subtract(qtyLading);
			BigDecimal qtyLineAvaliable = qtyAvailable;
			int k = 0;
			for (SalePlanLine line : salePlanLines) {
				if (qtyAvailable.compareTo(BigDecimal.ZERO) < 0) {
					//如果可用数量<0
					qtyMps = qtyAvailable.abs().add(line.getQtySalePlan());
					qtyAvailable = BigDecimal.ZERO;
				} else if (qtyAvailable.compareTo(line.getQtySalePlan()) < 0 ) {
					//如果可用数量<销售计划
					qtyMps = line.getQtySalePlan().subtract(qtyAvailable);
					qtyAvailable = BigDecimal.ZERO;
				} else {
					//如果可用数量>=销售计划
					qtyMps = BigDecimal.ZERO;
					qtyAvailable = qtyAvailable.subtract(line.getQtySalePlan());
				}
				
				MpsLine mpsLine = new MpsLine();
				mpsLine.setOrgRrn(mps.getOrgRrn());
				mpsLine.setIsActive(true);
				mpsLine.setCreatedBy(userRrn);
				mpsLine.setCreated(new Date());
				mpsLine.setUpdatedBy(userRrn);
				mpsLine.setMpsId(mps.getMpsId());
				mpsLine.setMaterialRrn(materialRrn);
				mpsLine.setQtySalePlan(line.getQtySalePlan());
				mpsLine.setQtyAvailable(qtyLineAvaliable);
				mpsLine.setDateDelivered(line.getDateDelivered());
				mpsLine.setOrderId(line.getOrderId());
				mpsLine.setIsGenerate(false);
				if (k == 0) {
					mpsLine.setQtyHandOn(materialSum.getQtyOnHand());
					mpsLine.setQtyTransit(materialSum.getQtyTransit().add(materialSum.getQtyMoLineWip()));
					mpsLine.setQtyLading(qtyLading);
					mpsLine.setQtyMin(materialSum.getQtyMin());
					mpsLine.setQtyAllocation(materialSum.getQtyAllocation());
				}
				k++;
				
				if (qtyMps.compareTo(BigDecimal.ZERO) > 0) {
					if (materialSum.getQtyMinProduct().compareTo(BigDecimal.ZERO) > 0) {
						BigDecimal qtyProduct = qtyMps;
						if (qtyMps.compareTo(materialSum.getQtyMinProduct()) <= 0) {
							qtyProduct = materialSum.getQtyMinProduct();
						} else {
							int i = 1;
							while (true) {//生产数必须是最小生产批量的倍数
								if (materialSum.getQtyMinProduct().add((materialSum.getQtyMinProduct().multiply(new BigDecimal(i)))).compareTo(qtyMps) >= 0) {
									qtyProduct = materialSum.getQtyMinProduct().add((materialSum.getQtyMinProduct().multiply(new BigDecimal(i))));
									break;
								}
								i++;
							}
						}
						qtyAvailable = qtyProduct.subtract(qtyMps);
						qtyMps = qtyProduct;
					}
					mpsLine.setQtyMps(qtyMps);
				} else {
					mpsLine.setQtyMps(BigDecimal.ZERO);
				}
				em.persist(mpsLine);
			}
		} catch (Exception e){ 
			logger.error("generateMpsLine mps=" + mps.getObjectRrn() 
					+ " Material=" + materialRrn + " Message: ", e);
			throw new ClientException(e);
		}
	}
	
	public void deleteMpsLineBom(MpsLine line) throws ClientException {		
		StringBuffer sql = new StringBuffer(" DELETE FROM MpsLineBom MpsLineBom ");
		sql.append(" WHERE ");
		sql.append(" mpsLineRrn = ? "); 

		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, line.getObjectRrn());
			int length = query.executeUpdate();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//获得MPSLine对应的物料所有的BOM
	public List<MpsLineBom> getMpsLineBom(MpsLine line) throws ClientException {
		List<MpsLineBom> boms = new ArrayList<MpsLineBom>();
		
		StringBuffer sql = new StringBuffer(" SELECT MpsLineBom FROM MpsLineBom MpsLineBom ");
		sql.append(" WHERE ");
		sql.append(" mpsLineRrn = ? "); 
		sql.append(" ORDER BY objectRrn ");
		try {
			//如果MpsLineBom表中有BOM信息,则使用此BOM;否则使用物料对应的BOM
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, line.getObjectRrn());
			boms = query.getResultList();
			if (boms.size() > 0) {
				return boms;
			}
			
			MpsLineBom bom = new MpsLineBom();
			bom.setMpsLineRrn(line.getObjectRrn());
			bom.setMaterialParentRrn(null);
			bom.setMaterialRrn(line.getMaterialRrn());
			bom.setPath(null);
			bom.setPathLevel(0L);
			bom.setLineNo(10L);
			bom.setUnitQty(BigDecimal.ONE);
			bom.setQtyBom(BigDecimal.ONE);
			Material material = em.find(Material.class, line.getMaterialRrn());
			bom.setMaterial(material);
			bom.setUomId(material.getInventoryUom());
			boms.add(bom);
			List<BomDetail> bomDetails = pdmManager.getActualLastBomDetails(line.getMaterialRrn());
			if(material.getIsProduct()) {
				pdmManager.verifyBOM(line.getMaterialRrn(), bomDetails);
			}
			for (BomDetail bomDetail : bomDetails) {
				bom = new MpsLineBom();
				bom.setMpsLineRrn(line.getObjectRrn());
				bom.setMaterialParentRrn(bomDetail.getParentRrn());
				bom.setMaterialRrn(bomDetail.getChildRrn());
				bom.setPath(bomDetail.getPath());
				bom.setPathLevel(bomDetail.getPathLevel());
				bom.setLineNo(bomDetail.getSeqNo());
				bom.setUnitQty(bomDetail.getUnitQty());
				bom.setQtyBom(bomDetail.getQtyBom());
				bom.setMaterial(bomDetail.getChildMaterial());
				if (bomDetail.getChildMaterial() != null) {
					bom.setUomId(bomDetail.getChildMaterial().getInventoryUom());
				}
				bom.setDescription(bomDetail.getDescription());
				boms.add(bom);
			}
			
			
		} catch (ClientParameterException e) { 
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return boms;
	}
	
	public void saveMpsLineBom(MpsLine line, List<MpsLineBom> boms) throws ClientException {
		StringBuffer sql = new StringBuffer(" DELETE FROM MpsLineBom ");
		sql.append(" WHERE mpsLineRrn = ? "); 
		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, line.getObjectRrn());
			query.executeUpdate();
			
			for (MpsLineBom bom : boms) {
				bom.setObjectRrn(null);
				em.persist(bom);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	//按照规则对MPSLine进行排序
	public List<MpsLine> getSortedMpsLine(long orgRrn, String mpsId) throws ClientException {
		String whereClause = " mpsId = '" + mpsId + "' AND qtyMps > 0 ";
		try {
			List<MpsLine> mpsLines = adManager.getEntityList(orgRrn, MpsLine.class, Integer.MAX_VALUE, whereClause, "");
			Collections.sort(mpsLines, new MpsLineComparator());
			return mpsLines;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//统计所有物料信息
	public void generateMaterialBatchSum(long orgRrn) throws ClientException {
		try {
			Query query = em.createNativeQuery("{call SP_MATERIAL_BATCHSUM(?)}");
			query.setParameter(1, orgRrn);
			query.executeUpdate();
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void generateManufactureOrder(Mps mps, MpsLine line, Requisition pr, long userRrn) throws ClientException {
		try {
			ManufactureOrder mo = new ManufactureOrder();
			mo.setOrgRrn(mps.getOrgRrn());
			mo.setMpsRrn(mps.getObjectRrn());
			mo.setMpsId(mps.getMpsId());
			mo.setMpsLineRrn(line.getObjectRrn());
			mo.setMaterialRrn(line.getMaterialRrn());
			mo.setDateStart(mps.getDateStart());
			mo.setDateEnd(line.getDateDelivered());
			mo.setDateDelivery(line.getDateDelivered());
			mo.setQtyProduct(line.getQtyMps());
			mo.setUomId(line.getUomId());
			mo.setComments(line.getComments());
			mo.setOrderId(line.getOrderId());
			
			//将MpsLineBom转换为ManufactureOrderBom
			List<MpsLineBom> mpsBoms = getMpsLineBom(line);
			List<ManufactureOrderBom> moBoms = new ArrayList<ManufactureOrderBom>();
			for (MpsLineBom mpsBom : mpsBoms) {
				ManufactureOrderBom moBom = new ManufactureOrderBom();
				moBom.setOrgRrn(mps.getOrgRrn());
				moBom.setIsActive(true);
				moBom.setMaterialParentRrn(mpsBom.getMaterialParentRrn());
				moBom.setMaterialRrn(mpsBom.getMaterialRrn());
				moBom.setPath(mpsBom.getPath());
				moBom.setRealPath(mpsBom.getPath());
				moBom.setPathLevel(mpsBom.getPathLevel());
				moBom.setRealPathLevel(mpsBom.getPathLevel());
				moBom.setLineNo(mpsBom.getLineNo());
				moBom.setUnitQty(mpsBom.getUnitQty());
				moBom.setQtyBom(mpsBom.getQtyBom());
				moBom.setUomId(mpsBom.getUomId());
				moBom.setDescription(mpsBom.getDescription());
				moBoms.add(moBom);
			}
			
			moBoms = wipManager.generateMoBomDetail(mo, moBoms, true);
			List<DocumentationLine> moLines = wipManager.generateMoLine(mo, moBoms, true, userRrn);
			
			wipManager.addMoBatch(mo, moLines, moBoms, pr, userRrn);
		} catch (ClientParameterException e){
			throw e;	
		} catch (Exception e) {
			logger.error("generateManufactureOrder MpsLine=" + line.getObjectRrn() 
					+ " Material=" + line.getMaterialRrn() + " Message: " , e);
			throw new ClientException(e);
		}
	}
	

	
//	public void generateMpsLine(Mps mps, long userRrn) throws ClientException {
//		try {
//			String whereClause = " mpsRrn = '" + mps.getObjectRrn() + "'";
//			List<Requisition> prs = adManager.getEntityList(mps.getOrgRrn(), Requisition.class, Integer.MAX_VALUE, whereClause, "");
//			if (prs.size() > 0) {
//				Requisition pr = prs.get(0);
//				if (Requisition.STATUS_DRAFTED.equals(pr.getDocStatus())) {
//					throw new ClientException("ppm.mps_pr_draft");
//				} else if (Requisition.STATUS_APPROVED.equals(pr.getDocStatus())) {
//					throw new ClientException("ppm.mps_pr_approve");
//				}
//			}
//			
//			long errCode = -1;
//			String errMessage = "";
//			Query query = em.createNativeQuery("{call ERP_PPM.SP_GENERATE_MPS(?, ?, ?, ?, ?)}");
//			query.setParameter(1, mps.getOrgRrn());
//			query.setParameter(2, mps.getMpsId());
//			query.setParameter(3, userRrn); 
//			query.setParameter(4, errCode);
//			query.setParameter(5, errMessage);
//			query.executeUpdate();
//			
//		} catch (ClientException e){
//			logger.error(e.getMessage(), e);
//			throw e; 	
//		} catch (Exception e){ 
//			logger.error(e.getMessage(), e);
//			throw new ClientException(e);
//		}
//	}
	
	public void saveErrorLog(PasErrorLog errLog, long orgRrn) throws ClientException {
		try{
			if(errLog == null) return;
			if(errLog.getObjectRrn() == null) {
				errLog.setOrgRrn(orgRrn);
				errLog.setIsActive(true);
				em.persist(errLog);
			} else {
				em.merge(errLog);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void saveMpsLineBom(MpsLineBom mpsLineBom) throws ClientException {
		try{
			if(mpsLineBom == null) return;
			if(mpsLineBom.getObjectRrn() == null) {
				em.persist(mpsLineBom);
			} else {
				em.merge(mpsLineBom);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	public void deleteMpsLineBom(MpsLine line, List<MpsLineBom> toSaves, List<MpsLineBom> toDeletes) throws ClientException {
		StringBuffer sql = new StringBuffer(" SELECT MpsLineBom FROM MpsLineBom MpsLineBom");
		sql.append(" WHERE ");
		sql.append(" mpsLineRrn = ? "); 
		sql.append(" ORDER BY objectRrn ");
		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, line.getObjectRrn());
			List<MpsLineBom> mlBoms = query.getResultList();
			// 如果mlBoms没有值表示line对应的MpsLineBom不存在,则直接保存toSaves
			// 否则从mlBoms删除所有toDeletes
			if (mlBoms.size() == 0) {
				for(MpsLineBom bom : toSaves) {
					bom.setObjectRrn(null);
					em.persist(bom);
				}
			} else {
				for(MpsLineBom bom : toDeletes) {
					MpsLineBom newBom = em.find(MpsLineBom.class, bom.getObjectRrn());
					if(newBom != null)
						em.remove(newBom);
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
	
	// 验证MpsLine对应的物料的Bom，如果mpsLineBom在MpsLineBom表中已存在，则默认Bom合法
	// 如果MpsLineBom中不存在，则调用pdm.verifyBOM()验证
	public boolean verifyMpsLine(MpsLine line) throws ClientException {
		List<MpsLineBom> boms = null;		
		StringBuffer sql = new StringBuffer(" SELECT MpsLineBom FROM MpsLineBom MpsLineBom ");
		sql.append(" WHERE ");
		sql.append(" mpsLineRrn = ? "); 
		sql.append(" ORDER BY objectRrn ");
		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, line.getObjectRrn());
			boms = query.getResultList();
			if (boms.size() > 0) {
				return true;
			}
			pdmManager.verifyBOM(line.getMaterialRrn());
			return true;
		} catch (ClientParameterException e) {
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	@Override
	public int computePlanSum(long orgRrn, String nextMonth)
			throws ClientException {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" {call SP_REP_PLAN_SUM(?, ?)} ");
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, nextMonth);
			
			query.executeUpdate();
			
			return 1;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.graly.erp.ppm.client.PPMManager#getShortageMaterials(long, java.lang.String, boolean)
	 */
	@Override
	public List<Material> getShortageMaterials(long orgRrn, String whereClause, boolean isIncludeTransit)
			throws ClientException {
		try{
			StringBuffer sql = new StringBuffer();
			sql.append( " SELECT Material.OBJECT_RRN, Material.ORG_RRN, Material.MATERIAL_ID, Material.MATERIAL_NAME, Material.MATERIAL_CATEGORY1, Material.QTY_MIN, Material.QTY_ONHAND, Material.QTY_TRANSIT ");
			sql.append( " FROM V_PPM_SHORTSTAGE Material ");
			sql.append( " WHERE ");
			
			sql.append(ADBase.SQL_BASE_CONDITION);
			
			if(isIncludeTransit){//是否包含在途
				//若包含 库存+在制品<安全库存；
				sql.append( " AND Material.QTY_ONHAND + Material.QTY_TRANSIT < Material.QTY_MIN  ");
			}else{
				//若不包含 库存<1/2安全库存
				sql.append( " AND Material.QTY_ONHAND * 2 < Material.QTY_MIN  ");
			}
			
			if(whereClause != null && whereClause.trim().length() > 0){
				sql.append( " AND " + whereClause);
			}

			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, orgRrn);
			List<Object []> list = query.getResultList();
			List<Material> rs = new ArrayList<Material>();
			for(Object[] objs : list){
				Material m = new Material();
				m.setObjectRrn(((BigDecimal) objs[0]).longValue());
				m.setMaterialId((String) objs[2]);
				m.setName((String) objs[3]);
				m.setMaterialCategory1((String) objs[4]);
				m.setQtyMin((BigDecimal) objs[5]);
				m.setQtyOnHand((BigDecimal) objs[6]);
				m.setQtyTransit((BigDecimal) objs[7]);
				rs.add(m);
			}
			
			return rs;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	@Override
	public List<SalePlanLine> getOverOutedMaterials(long orgRrn, String month, String whereClause)
			throws ClientException {
		try{
			StringBuffer sql = new StringBuffer();
			sql.append(" {call sp_ppm_overouted(?,?)} ");//调用存储过程，生成相关临时表后通过关联查询得到结果
			
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, month);
			
			query.executeUpdate();
			
			StringBuffer sql2 = new StringBuffer();
			sql2.append(" SELECT MATERIAL_RRN, "); 
			sql2.append(" T5.MATERIAL_ID, "); 
			sql2.append(" T5.INVENTORY_UOM, "); 
			sql2.append(" DEPARTMENT, "); 
			sql2.append(" NVL(T3.QTY_PLAN, 0) QTY_PLAN, "); 
			sql2.append(" T4.QTY_OUTED_SUM "); 
			sql2.append(" FROM   (SELECT MATERIAL_RRN, "); 
			sql2.append(" DEPARTMENT, "); 
			sql2.append(" NVL(T1.QTY_SALEPLAN_SUM, 0) + NVL(T2.QTY_TPS_SUM, 0) QTY_PLAN "); 
			sql2.append(" FROM   TMP_MATERIAL_DEPARTMENT_SPLAN T1 "); 
			sql2.append(" FULL   OUTER JOIN TMP_MATERIAL_DEPARTMENT_TPS T2 "); 
			sql2.append(" USING  (MATERIAL_RRN, DEPARTMENT)) T3 "); 
			sql2.append(" RIGHT  JOIN TMP_MATERIAL_DEPARTMENT_OUTED T4 "); 
			sql2.append(" USING  (MATERIAL_RRN, DEPARTMENT) "); 
			sql2.append(" LEFT   JOIN PDM_MATERIAL T5 "); 
			sql2.append(" ON     T5.OBJECT_RRN = MATERIAL_RRN "); 
			sql2.append(" WHERE  T4.QTY_OUTED_SUM > NVL(T3.QTY_PLAN, 0) "); 
			
			if(whereClause != null && !whereClause.trim().equals("")){
				sql2.append(" AND ");
				sql2.append( whereClause );
			}

			query = em.createNativeQuery(sql2.toString());
			List<Object []> list = query.getResultList();
			List<SalePlanLine> rs = new ArrayList<SalePlanLine>();//用SalePlanLine作为保存数据的载体以便于在前台显示
			for(Object[] objs : list){
				SalePlanLine spl = new SalePlanLine();
				spl.setMaterialRrn(((BigDecimal) objs[0]).longValue());
				Material m = new Material();
				m.setObjectRrn(spl.getMaterialRrn());
				m = (Material) adManager.getEntity(m);
				spl.setMaterial(m);
				spl.setSalePlanType((String)objs[3]);
				spl.setQtySalePlan((BigDecimal) objs[4]);
				spl.setQtyLading((BigDecimal) objs[5]);
				rs.add(spl);
			}
			
			return rs;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	
//	public List<MpsStatistcLine> statisticMps(long orgRrn, String mpsId, Long materialRrn) throws ClientException{
//		List<MpsStatistcLine> statisticMps = new ArrayList<MpsStatistcLine>();
//		try{
//			StringBuffer sql = new StringBuffer();
//			sql.append(" FROM MpsLine line WHERE " + ADBase.BASE_CONDITION);
//			sql.append(" AND line.mpsId = ? ");
//			if(materialRrn != null){
//				sql.append(" AND line.materialRrn = ? ");
//			}
//			Query query = em.createQuery(sql.toString());
//			query.setParameter(1, orgRrn);
//			query.setParameter(2, mpsId);
//			if(materialRrn != null){
//				query.setParameter(3, materialRrn);
//			}
//			
//			List<MpsLine> rslt = query.getResultList();
//			
//			StringBuffer sql2 = new StringBuffer();
//
//			sql2.append("SELECT SUM(SL.QTY_LADING), SUM(SL.QTY_SALEPLAN)"); 
//			sql2.append("      FROM PPM_SALEPLAN_LINE SL"); 
//			sql2.append("     WHERE " + ADBase.SQL_BASE_CONDITION);
//			sql2.append(" 		AND SL.MPS_ID = ? ");
//			sql2.append("       AND SL.MATERIAL_RRN = ?");
//			
//			//统计在制数和库存数
//			StringBuffer sql3 = new StringBuffer();
//			sql3.append(" SELECT SUM(NVL(THIS_QTY_ONHAND, 0)) QTY_ONHAND, SUM(NVL(THIS_QTY_TRANSIT, 0)) QTY_TRANSIT FROM REP_TEMP_PLAN_SUM ");
//			sql3.append(" WHERE MATERIAL_RRN = ? ");
//			
//			if(rslt != null){
//				for(MpsLine line : rslt){
//					MpsStatistcLine msl = new MpsStatistcLine();
//					
//					Query query2 = em.createNativeQuery(sql2.toString());
//					query2.setParameter(1, orgRrn);
//					query2.setParameter(2, line.getMpsId());
//					query2.setParameter(3, line.getMaterialRrn());
//					
//					List<?> reslt2 = query2.getResultList();
//					if(reslt2 != null){
//						for(Object objs : reslt2){
//							Object[] objects = (Object[]) objs;
//							msl.setMpsId(line.getMpsId());
//							msl.setQtyTransit(BigDecimal.ZERO);
//							msl.setQtyLading(objects[0]==null?BigDecimal.ZERO:(BigDecimal) objects[0]);
//							msl.setQtySalePlan(objects[1]==null?BigDecimal.ZERO:(BigDecimal) objects[1]);
//							msl.setQtyMps(line.getQtyMps()==null?BigDecimal.ZERO:line.getQtyMps());
//							msl.setTemporaryQty(line.getTemporaryQty()==null?BigDecimal.ZERO:line.getTemporaryQty());
//							msl.setInternalOrderQty(line.getInternalOrderQty()==null?BigDecimal.ZERO:line.getInternalOrderQty());
//							
//							Material material = em.find(Material.class, line.getMaterialRrn());
//							msl.setMaterial(material);
//							
//							Query query3 = em.createNativeQuery(sql3.toString());//在制数\库存数
//							query3.setParameter(1, line.getMaterialRrn());
//							Object[] qtys = (Object[])query3.getSingleResult();
//							BigDecimal qtyOnhand = (qtys[0]==null?BigDecimal.ZERO:(BigDecimal)qtys[0]);
//							BigDecimal qtyTransit = (qtys[1]==null?BigDecimal.ZERO:(BigDecimal)qtys[1]);
//							msl.setQtyHandOn(qtyOnhand);//库存数
//							msl.setQtyTransit(qtyTransit);//在途/在制数
//							
//							BigDecimal qtyFormula = msl.getQtyHandOn().add(msl.getQtyTransit()).subtract(msl.getQtyLading()).subtract(msl.getQtySalePlan()).add(msl.getQtyMps()).add(msl.getTemporaryQty()).subtract(msl.getInternalOrderQty());
//							msl.setQtyFormula(qtyFormula);
//							statisticMps.add(msl);
//						}
//					}
//				}
//			}
//		}catch (Exception e) {
//			logger.error(e.getMessage(), e);
//			throw new ClientException(e);
//		}
//		return statisticMps;
//	}
//	
//	public void updateMpsLineByStatisticLine(long orgRrn, long userRrn, MpsStatistcLine line) throws ClientException{
//		try {
//			long materialRrn = line.getMaterialRrn();
//			String mpsId = line.getMpsId();
//			
//			StringBuffer sql = new StringBuffer();
//			sql.append(" FROM MpsLine MpsLine ");
//			sql.append(" WHERE " + ADBase.BASE_CONDITION);
//			sql.append(" AND MpsLine.mpsId = ? ");
//			sql.append(" AND MpsLine.materialRrn = ? ");
//			
//			Query query = em.createQuery(sql.toString());
//			query.setParameter(1, orgRrn);
//			query.setParameter(2, mpsId);
//			query.setParameter(3, materialRrn);
//			
//			MpsLine mpsLine = (MpsLine) query.getSingleResult();
//			mpsLine.setTemporaryQty(line.getTemporaryQty());
//			mpsLine.setInternalOrderQty(line.getInternalOrderQty());
//			mpsLine.setUpdated(new Date());
//			mpsLine.setUpdatedBy(userRrn);
//			
//			em.merge(mpsLine);
//		} catch (Exception e) {
//			throw new ClientException(e);
//		}
//	}
	
	
	public List<MpsStatistcLine> statisticMps(long orgRrn, String mpsId,
			Long materialRrn) throws ClientException {
		List<MpsStatistcLine> statisticMps = new ArrayList<MpsStatistcLine>();
		try {
			StringBuffer sql0 = new StringBuffer();
			sql0.append(" FROM MpsStatistcLine line ");
			sql0.append(" WHERE line.mpsId = ? ");
			if (materialRrn != null) {
				sql0.append(" AND line.materialRrn = ? ");
			}

			Query query0 = em.createQuery(sql0.toString());
			query0.setParameter(1, mpsId);
			if (materialRrn != null) {
				query0.setParameter(2, materialRrn);
			}

			List<MpsStatistcLine> rslt0 = query0.getResultList();

			if (rslt0 != null && rslt0.size() > 0) {
				for (MpsStatistcLine msl : rslt0) {
					Material material = em.find(Material.class, msl
							.getMaterialRrn());
					msl.setMaterial(material);
					//小谢要求每次查询更新销售数量(因销售数量更新,公式值也跟着更新)
					StringBuffer sqlSalePlan = new StringBuffer();
					sqlSalePlan.append("SELECT SUM(SL.QTY_LADING), SUM(SL.QTY_SALEPLAN)");
					sqlSalePlan.append("      FROM PPM_SALEPLAN_LINE SL");
					sqlSalePlan.append("     WHERE " + ADBase.SQL_BASE_CONDITION);
					sqlSalePlan.append(" 		AND SL.MPS_ID = ? ");
					sqlSalePlan.append("       AND SL.MATERIAL_RRN = ?");
					
					Query querySalePlan = em.createNativeQuery(sqlSalePlan.toString());
					querySalePlan.setParameter(1, orgRrn);
					querySalePlan.setParameter(2, msl.getMpsId());
					querySalePlan.setParameter(3, msl.getMaterialRrn());

					List<?> resltSqlPlan = querySalePlan.getResultList();
					if(resltSqlPlan !=null && resltSqlPlan.size() > 0 ){
						Object[] objects = (Object[]) resltSqlPlan.get(0);
						msl.setQtySalePlan(objects[1] == null ? BigDecimal.ZERO
												: (BigDecimal) objects[1]);//销售数量
						
						BigDecimal qtyFormula = msl.getQtyHandOn().add(
								msl.getQtyTransit()).subtract(
								msl.getQtyLading()).subtract(
								msl.getQtySalePlan()).add(
								msl.getQtyMps()).add(
								msl.getTemporaryQty()).subtract(
								msl.getInternalOrderQty());
						msl.setQtyFormula(qtyFormula);//公示值
						em.merge(msl);
					}
				}
				return rslt0;
			} else {
				StringBuffer sql1 = new StringBuffer();
				sql1
						.append(" FROM MpsLine line WHERE "
								+ ADBase.BASE_CONDITION);
				sql1.append(" AND line.mpsId = ? ");
				if (materialRrn != null) {
					sql1.append(" AND line.materialRrn = ? ");
				}
				Query query1 = em.createQuery(sql1.toString());
				query1.setParameter(1, orgRrn);
				query1.setParameter(2, mpsId);
				if (materialRrn != null) {
					query1.setParameter(3, materialRrn);
				}

				List<MpsLine> rslt = query1.getResultList();

				StringBuffer sql2 = new StringBuffer();

				sql2.append("SELECT SUM(SL.QTY_LADING), SUM(SL.QTY_SALEPLAN)");
				sql2.append("      FROM PPM_SALEPLAN_LINE SL");
				sql2.append("     WHERE " + ADBase.SQL_BASE_CONDITION);
				sql2.append(" 		AND SL.MPS_ID = ? ");
				sql2.append("       AND SL.MATERIAL_RRN = ?");

				// 统计在制数和库存数
				StringBuffer sql3 = new StringBuffer();
				sql3.append(" SELECT SUM(NVL(THIS_QTY_ONHAND, 0)) QTY_ONHAND, SUM(NVL(THIS_QTY_TRANSIT, 0)) QTY_TRANSIT FROM REP_TEMP_PLAN_SUM ");
				sql3.append(" WHERE MATERIAL_RRN = ? ");

				if (rslt != null) {
					for (MpsLine line : rslt) {
						MpsStatistcLine msl = new MpsStatistcLine();

						Query query2 = em.createNativeQuery(sql2.toString());
						query2.setParameter(1, orgRrn);
						query2.setParameter(2, line.getMpsId());
						query2.setParameter(3, line.getMaterialRrn());

						List<?> reslt2 = query2.getResultList();
						if (reslt2 != null) {
							for (Object objs : reslt2) {
								Object[] objects = (Object[]) objs;
								msl.setMpsId(line.getMpsId());
								msl.setMpsLineRrn(line.getObjectRrn());
								msl.setQtyTransit(BigDecimal.ZERO);
								msl.setQtyLading(objects[0] == null ? BigDecimal.ZERO
												: (BigDecimal) objects[0]);
								msl.setQtySalePlan(objects[1] == null ? BigDecimal.ZERO
												: (BigDecimal) objects[1]);
								msl.setQtyMps(line.getQtyMps() == null ? BigDecimal.ZERO
												: line.getQtyMps());
								msl.setTemporaryQty(line.getTemporaryQty() == null ? BigDecimal.ZERO
												: line.getTemporaryQty());
								msl.setInternalOrderQty(line
												.getInternalOrderQty() == null ? BigDecimal.ZERO
												: line.getInternalOrderQty());

								Material material = em.find(Material.class,
										line.getMaterialRrn());
								msl.setMaterial(material);
								msl.setMaterialRrn(material.getObjectRrn());

								Query query3 = em.createNativeQuery(sql3
										.toString());// 在制数\库存数
								query3.setParameter(1, line.getMaterialRrn());
								Object[] qtys = (Object[]) query3
										.getSingleResult();
								BigDecimal qtyOnhand = (qtys[0] == null ? BigDecimal.ZERO
										: (BigDecimal) qtys[0]);
								BigDecimal qtyTransit = (qtys[1] == null ? BigDecimal.ZERO
										: (BigDecimal) qtys[1]);
								msl.setQtyHandOn(qtyOnhand);// 库存数
								msl.setQtyTransit(qtyTransit);// 在途/在制数

								BigDecimal qtyFormula = msl.getQtyHandOn().add(
										msl.getQtyTransit()).subtract(
										msl.getQtyLading()).subtract(
										msl.getQtySalePlan()).add(
										msl.getQtyMps()).add(
										msl.getTemporaryQty()).subtract(
										msl.getInternalOrderQty());
								msl.setQtyFormula(qtyFormula);

								em.persist(msl);
								statisticMps.add(msl);
							}
						}else{
							msl.setMpsId(line.getMpsId());
							msl.setMpsLineRrn(line.getObjectRrn());
							msl.setQtyTransit(BigDecimal.ZERO);
							msl.setQtyLading(BigDecimal.ZERO);
							msl.setQtySalePlan(BigDecimal.ZERO);
							msl.setQtyMps(line.getQtyMps() == null ? BigDecimal.ZERO : line.getQtyMps());
							msl .setTemporaryQty(line.getTemporaryQty() == null ? BigDecimal.ZERO : line.getTemporaryQty());
							msl.setInternalOrderQty(line.getInternalOrderQty() == null ? BigDecimal.ZERO : line.getInternalOrderQty());

							Material material = em.find(Material.class, line.getMaterialRrn());
							msl.setMaterial(material);
							msl.setMaterialRrn(material.getObjectRrn());

							Query query3 = em.createNativeQuery(sql3
									.toString());// 在制数\库存数
							query3.setParameter(1, line.getMaterialRrn());
							Object[] qtys = (Object[]) query3.getSingleResult();
							BigDecimal qtyOnhand = (qtys[0] == null ? BigDecimal.ZERO
									: (BigDecimal) qtys[0]);
							BigDecimal qtyTransit = (qtys[1] == null ? BigDecimal.ZERO
									: (BigDecimal) qtys[1]);
							msl.setQtyHandOn(qtyOnhand);// 库存数
							msl.setQtyTransit(qtyTransit);// 在途/在制数

							BigDecimal qtyFormula = msl.getQtyHandOn().add(
									msl.getQtyTransit()).subtract(
									msl.getQtyLading()).subtract(
									msl.getQtySalePlan()).add(
									msl.getQtyMps()).add(
									msl.getTemporaryQty()).subtract(
									msl.getInternalOrderQty());
							msl.setQtyFormula(qtyFormula);

							em.persist(msl);
							statisticMps.add(msl);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return statisticMps;
	}

	public void updateMpsLineByStatisticLine(long orgRrn, long userRrn,
			MpsStatistcLine line) throws ClientException {
		try {
			Long mpsLineRrn = line.getMpsLineRrn();

			BigDecimal qtyFormula = line.getQtyHandOn().add(
					line.getQtyTransit()).subtract(line.getQtyLading())
					.subtract(line.getQtySalePlan()).add(line.getQtyMps()).add(
							line.getTemporaryQty()).subtract(
							line.getInternalOrderQty());
			line.setQtyFormula(qtyFormula);
			em.merge(line);

			StringBuffer sql = new StringBuffer();
			sql.append(" FROM MpsLine MpsLine ");
			sql.append(" WHERE " + ADBase.BASE_CONDITION);
			sql.append(" AND MpsLine.objectRrn = ? ");

			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, mpsLineRrn);

			MpsLine mpsLine = (MpsLine) query.getSingleResult();
			mpsLine.setTemporaryQty(line.getTemporaryQty());
			mpsLine.setInternalOrderQty(line.getInternalOrderQty());
			mpsLine.setUpdated(new Date());
			mpsLine.setUpdatedBy(userRrn);

			em.merge(mpsLine);
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}

	public boolean validateMpsLine(long orgRrn,MpsLine line) throws ClientException {
		if(line != null){
			String mpsId = line.getMpsId();
			Long materialRrn = line.getMaterialRrn();
			try {
				StringBuffer sql0 = new StringBuffer();
				sql0.append(" SELECT line FROM MpsStatistcLine line ");
				sql0.append(" WHERE line.mpsLineRrn = ? ");

				Query query0 = em.createQuery(sql0.toString());
				query0.setParameter(1, line.getObjectRrn());

				List<MpsStatistcLine> rslt0 = query0.getResultList();

				if (rslt0 != null && rslt0.size() > 0) {
					MpsStatistcLine sLine = rslt0.get(0);
					sLine.setQtyMps(line.getQtyMps());
					//更新公式值
					BigDecimal qtyFormula = sLine.getQtyHandOn().add(
							sLine.getQtyTransit()).subtract(
							sLine.getQtyLading()).subtract(
							sLine.getQtySalePlan()).add(
							sLine.getQtyMps()).add(
							sLine.getTemporaryQty()).subtract(
							sLine.getInternalOrderQty());
					sLine.setQtyFormula(qtyFormula);
//					sLine.setQtyFormula(sLine.getQtyFormula().add(sLine.getQtyMps()));
					em.merge(sLine);
					return true;
				} else {
					StringBuffer sql = new StringBuffer();
					sql.append("  SELECT PS.* ");
					sql.append("  FROM REP_TEMP_PLAN_SUM PS");
					sql.append("  WHERE  PS.MATERIAL_RRN = ? ");
					
					Query query = em.createNativeQuery(sql.toString());
					query.setParameter(1, materialRrn);
					List results = query.getResultList();
					if(results.size() <=0){
						MaterialSum ms =wipManager.getMaterialSum(orgRrn, materialRrn, false, false);

						MpsStatistcLine msl = new MpsStatistcLine();
						msl.setMpsId(line.getMpsId());
						msl.setMpsLineRrn(line.getObjectRrn());
						msl.setQtyTransit(BigDecimal.ZERO);
						msl.setQtyLading(BigDecimal.ZERO);
						msl.setQtySalePlan(BigDecimal.ZERO);
						msl.setQtyMps(line.getQtyMps() == null ? BigDecimal.ZERO : line.getQtyMps());
						msl .setTemporaryQty(line.getTemporaryQty() == null ? BigDecimal.ZERO : line.getTemporaryQty());
						msl.setInternalOrderQty(line.getInternalOrderQty() == null ? BigDecimal.ZERO : line.getInternalOrderQty());

						Material material = em.find(Material.class, line.getMaterialRrn());
						msl.setMaterial(material);
						msl.setMaterialRrn(material.getObjectRrn());

						BigDecimal qtyOnhand = (ms.getQtyOnHand());
						BigDecimal qtyTransit = (ms.getQtyTransit());
						msl.setQtyHandOn(qtyOnhand);// 库存数
						msl.setQtyTransit(qtyTransit);// 在途/在制数

						BigDecimal qtyFormula = msl.getQtyHandOn().add(
								msl.getQtyTransit()).subtract(
								msl.getQtyLading()).subtract(
								msl.getQtySalePlan()).add(
								msl.getQtyMps()).add(
								msl.getTemporaryQty()).subtract(
								msl.getInternalOrderQty());
						msl.setQtyFormula(qtyFormula);

						em.persist(msl);
					
					}else{
						StringBuffer sql2 = new StringBuffer();
						sql2.append("SELECT SUM(SL.QTY_LADING), SUM(SL.QTY_SALEPLAN)");
						sql2.append("      FROM PPM_SALEPLAN_LINE SL");
						sql2.append("     WHERE " + ADBase.SQL_BASE_CONDITION);
						sql2.append(" 		AND SL.MPS_ID = ? ");
						sql2.append("       AND SL.MATERIAL_RRN = ?");

						// 统计在制数和库存数
						StringBuffer sql3 = new StringBuffer();
						sql3.append(" SELECT SUM(NVL(THIS_QTY_ONHAND, 0)) QTY_ONHAND, SUM(NVL(THIS_QTY_TRANSIT, 0)) QTY_TRANSIT FROM REP_TEMP_PLAN_SUM ");
						sql3.append(" WHERE MATERIAL_RRN = ? ");
						


						MpsStatistcLine msl = new MpsStatistcLine();

						Query query2 = em.createNativeQuery(sql2.toString());
						query2.setParameter(1, orgRrn);
						query2.setParameter(2, line.getMpsId());
						query2.setParameter(3, line.getMaterialRrn());

						List<?> reslt2 = query2.getResultList();
						if (reslt2 != null) {
							for (Object objs : reslt2) {
								Object[] objects = (Object[]) objs;
								msl.setMpsId(line.getMpsId());
								msl.setMpsLineRrn(line.getObjectRrn());
								msl.setQtyTransit(BigDecimal.ZERO);
								msl.setQtyLading(objects[0] == null ? BigDecimal.ZERO: (BigDecimal) objects[0]);
								msl.setQtySalePlan(objects[1] == null ? BigDecimal.ZERO: (BigDecimal) objects[1]);
								msl.setQtyMps(line.getQtyMps() == null ? BigDecimal.ZERO: line.getQtyMps());
								msl.setTemporaryQty(line.getTemporaryQty() == null ? BigDecimal.ZERO: line.getTemporaryQty());
								msl.setInternalOrderQty(line.getInternalOrderQty() == null ? BigDecimal.ZERO: line.getInternalOrderQty());

								Material material = em.find(Material.class,line.getMaterialRrn());
								msl.setMaterial(material);
								msl.setMaterialRrn(material.getObjectRrn());

								Query query3 = em.createNativeQuery(sql3.toString());// 在制数\库存数
								query3.setParameter(1, line.getMaterialRrn());
								Object[] qtys = (Object[]) query3.getSingleResult();
								BigDecimal qtyOnhand = (qtys[0] == null ? BigDecimal.ZERO: (BigDecimal) qtys[0]);
								BigDecimal qtyTransit = (qtys[1] == null ? BigDecimal.ZERO: (BigDecimal) qtys[1]);
								msl.setQtyHandOn(qtyOnhand);// 库存数
								msl.setQtyTransit(qtyTransit);// 在途/在制数

								BigDecimal qtyFormula = msl.getQtyHandOn().add(
										msl.getQtyTransit()).subtract(
										msl.getQtyLading()).subtract(
										msl.getQtySalePlan()).add(
										msl.getQtyMps()).add(
										msl.getTemporaryQty()).subtract(
										msl.getInternalOrderQty());
								msl.setQtyFormula(qtyFormula);

								em.persist(msl);
							}
						}else{
							msl.setMpsId(line.getMpsId());
							msl.setMpsLineRrn(line.getObjectRrn());
							msl.setQtyTransit(BigDecimal.ZERO);
							msl.setQtyLading(BigDecimal.ZERO);
							msl.setQtySalePlan(BigDecimal.ZERO);
							msl.setQtyMps(line.getQtyMps() == null ? BigDecimal.ZERO : line.getQtyMps());
							msl .setTemporaryQty(line.getTemporaryQty() == null ? BigDecimal.ZERO : line.getTemporaryQty());
							msl.setInternalOrderQty(line.getInternalOrderQty() == null ? BigDecimal.ZERO : line.getInternalOrderQty());

							Material material = em.find(Material.class, line.getMaterialRrn());
							msl.setMaterial(material);
							msl.setMaterialRrn(material.getObjectRrn());

							Query query3 = em.createNativeQuery(sql3.toString());// 在制数\库存数
							query3.setParameter(1, line.getMaterialRrn());
							Object[] qtys = (Object[]) query3.getSingleResult();
							BigDecimal qtyOnhand = (qtys[0] == null ? BigDecimal.ZERO
									: (BigDecimal) qtys[0]);
							BigDecimal qtyTransit = (qtys[1] == null ? BigDecimal.ZERO
									: (BigDecimal) qtys[1]);
							msl.setQtyHandOn(qtyOnhand);// 库存数
							msl.setQtyTransit(qtyTransit);// 在途/在制数

							BigDecimal qtyFormula = msl.getQtyHandOn().add(
									msl.getQtyTransit()).subtract(
									msl.getQtyLading()).subtract(
									msl.getQtySalePlan()).add(
									msl.getQtyMps()).add(
									msl.getTemporaryQty()).subtract(
									msl.getInternalOrderQty());
							msl.setQtyFormula(qtyFormula);

							em.persist(msl);
						}
					
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw new ClientException(e);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public List<TpsLinePrepare> approveTpsLinePrepare(long orgRrn, long userRrn, List<TpsLinePrepare> tpsLinePrepares) 
	throws ClientException {
		try{
			for(TpsLinePrepare tpsLinePrepare :tpsLinePrepares){
				tpsLinePrepare.setTpsStatus(TpsLinePrepare.TPSSTATUS_APPROVED);
				em.merge(tpsLinePrepare);
				TpsLine tpsLine = new TpsLine();
				tpsLine.setOrgRrn(orgRrn);
				tpsLine.setIsActive(true);
				tpsLine.setCreated(new Date());
				tpsLine.setCreatedBy(userRrn);
				tpsLine.setUpdated(new Date());
				tpsLine.setUpdatedBy(userRrn);
				tpsLine.setMaterialRrn(tpsLinePrepare.getMaterialRrn());
				tpsLine.setMaterialId(tpsLinePrepare.getMaterialId());
				tpsLine.setMaterialName(tpsLinePrepare.getMaterialName());
				tpsLine.setUomId(tpsLinePrepare.getUomId());
				tpsLine.setQtyTps(tpsLinePrepare.getQtyTps());
				tpsLine.setDateDelivered(tpsLinePrepare.getDateDelivered());
				tpsLine.setIsGenerate(tpsLinePrepare.getIsGenerate());
				tpsLine.setComments(tpsLinePrepare.getComments());
				tpsLine.setSalePlanType(tpsLinePrepare.getSalePlanType());
				tpsLine.setDateCreated(tpsLinePrepare.getDateCreated());
				tpsLine.setTpsId(tpsLinePrepare.getTpsId());
				tpsLine.setCustomerName(tpsLinePrepare.getCustomerName());
				tpsLine.setSaler(tpsLinePrepare.getSaler());
				tpsLine.setOrderId(tpsLinePrepare.getOrderId());
				tpsLine.setIsStockUp(tpsLinePrepare.getIsStockUp());
				tpsLine.setPrepareTps(tpsLinePrepare.getPrepareTps());
				tpsLine.setSysValidate(tpsLinePrepare.getSysValidate());
				tpsLine.setExcelValidate(tpsLinePrepare.getExcelValidate());
				tpsLine.setPiId(tpsLinePrepare.getPiId());
				tpsLine.setInternalOrderId(tpsLinePrepare.getInternalOrderId());
				tpsLine.setCustomerManager(tpsLinePrepare.getCustomerManager());
				em.persist(tpsLine);
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return tpsLinePrepares;
	}
	
	/**
	 * 需求人:童庆飞
	 * 功能：主计划设置--导入
	 * 业务实现：1.计划下所有行的生产数量设置为0
	 * 2.计划行与EXCEL想匹配，EXCEL导入信息替代计划信息，如生产数量，订单号，交货期,备注
	 * 3.Excel中存在但在计划中不存在的物料则添加
	 * */
	public void importMpsLine(long orgRrn,long userRrn,Mps mps,Map<Long,MpsLine> mpsLinesMap, List<MpsLine> perMpsLines )
	throws ClientException {
		try{
			List<MpsLine> dbMpsLine = adManager.getEntityList(orgRrn,MpsLine.class,Integer.MAX_VALUE,
					"mpsId = '"+mps.getMpsId()+"'",null);
			for(MpsLine mpsLine : dbMpsLine){
				if(mpsLinesMap.containsKey(mpsLine.getObjectRrn())){
					mpsLine = mpsLinesMap.get(mpsLine.getObjectRrn());
					mpsLine.setUpdated(new Date());
					mpsLine.setUpdatedBy(userRrn);
					em.merge(mpsLine);
				}else{
					mpsLine.setQtyMps(BigDecimal.ZERO);
					mpsLine.setUpdated(new Date());
					mpsLine.setUpdatedBy(userRrn);
					em.merge(mpsLine);
				}
			}
			for(MpsLine mpsLine : perMpsLines){//前台设置物料等等相关信息
				
				StringBuffer sqlLading = new StringBuffer();
				sqlLading.append("  SELECT NVL(SUM(QTY_LADING), 0) FROM PPM_SALEPLAN_LINE ");
				sqlLading.append(" WHERE ORG_RRN = ? AND MPS_ID = ? ");
				sqlLading.append(" AND MATERIAL_RRN = ? ");
				//汇总物料库存、已分配等数据
				MaterialSum materialSum = wipManager.getMaterialSum(mps.getOrgRrn(), mpsLine.getMaterialRrn(), false, true);
				if (materialSum == null) {
					return;
				}
				Query query = em.createNativeQuery(sqlLading.toString());
				query.setParameter(1, mps.getOrgRrn());
				query.setParameter(2, mps.getMpsId());
				query.setParameter(3, mpsLine.getMaterialRrn());
				BigDecimal qtyLading = (BigDecimal)query.getSingleResult();
				//可用数量=库存数量+在途数量(PR+PO)+MoLine在制品数量-SO数量-已分配数量-提留数量-最低库存
				BigDecimal qtyAvailable = materialSum.getQtyOnHand().add(materialSum.getQtyTransit())
					.add(materialSum.getQtyMoLineWip()).subtract(materialSum.getQtySo())
					.subtract(materialSum.getQtyAllocation()).subtract(materialSum.getQtyMin()).subtract(qtyLading);
				BigDecimal qtyLineAvaliable = qtyAvailable;
				
				mpsLine.setOrgRrn(orgRrn);
				mpsLine.setIsActive(true);
				mpsLine.setCreated(new Date());
				mpsLine.setCreatedBy(userRrn);
				mpsLine.setUpdated(new Date());
				mpsLine.setUpdatedBy(userRrn);
				mpsLine.setMpsId(mps.getMpsId());
				mpsLine.setQtyAvailable(qtyLineAvaliable);
				mpsLine.setQtySalePlan(BigDecimal.ZERO);
				mpsLine.setIsGenerate(false);
				
				em.persist(mpsLine);
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		
	}
	
	//童庆飞要求主计划管理生成生产计划的工作令的开始时间点击生成生产计划的时间
	public void generateManufactureOrderTime(Mps mps, MpsLine line, Requisition pr, long userRrn) throws ClientException {
		try {
			ManufactureOrder mo = new ManufactureOrder();
			mo.setOrgRrn(mps.getOrgRrn());
			mo.setMpsRrn(mps.getObjectRrn());
			mo.setMpsId(mps.getMpsId());
			mo.setMpsLineRrn(line.getObjectRrn());
			mo.setMaterialRrn(line.getMaterialRrn());
			mo.setDateStart(mps.getDateStart());
			mo.setDateEnd(line.getDateDelivered());
			mo.setDateDelivery(line.getDateDelivered());
			mo.setQtyProduct(line.getQtyMps());
			mo.setUomId(line.getUomId());
			mo.setComments(line.getComments());
			mo.setOrderId(line.getOrderId());
			
			//将MpsLineBom转换为ManufactureOrderBom
			List<MpsLineBom> mpsBoms = getMpsLineBom(line);
			List<ManufactureOrderBom> moBoms = new ArrayList<ManufactureOrderBom>();
			for (MpsLineBom mpsBom : mpsBoms) {
				ManufactureOrderBom moBom = new ManufactureOrderBom();
				moBom.setOrgRrn(mps.getOrgRrn());
				moBom.setIsActive(true);
				moBom.setMaterialParentRrn(mpsBom.getMaterialParentRrn());
				moBom.setMaterialRrn(mpsBom.getMaterialRrn());
				moBom.setPath(mpsBom.getPath());
				moBom.setRealPath(mpsBom.getPath());
				moBom.setPathLevel(mpsBom.getPathLevel());
				moBom.setRealPathLevel(mpsBom.getPathLevel());
				moBom.setLineNo(mpsBom.getLineNo());
				moBom.setUnitQty(mpsBom.getUnitQty());
				moBom.setQtyBom(mpsBom.getQtyBom());
				moBom.setUomId(mpsBom.getUomId());
				moBom.setDescription(mpsBom.getDescription());
				moBoms.add(moBom);
			}
			
			moBoms = wipManager.generateMoBomDetail(mo, moBoms, true);
			List<DocumentationLine> moLines = wipManager.generateMoLine(mo, moBoms, true, userRrn);
			
//			wipManager.addMoBatch(mo, moLines, moBoms, pr, userRrn);
			wipManager.addMoBatchTime(mo, moLines, moBoms, pr, userRrn,new Date(),line.getDateDelivered());
		} catch (ClientParameterException e){
			throw e;	
		} catch (Exception e) {
			logger.error("generateManufactureOrder MpsLine=" + line.getObjectRrn() 
					+ " Material=" + line.getMaterialRrn() + " Message: " , e);
			throw new ClientException(e);
		}
	}
	
	/***
	 * 主计划统计查询--添加主计划通知交货期
	 * @param mps
	 * @param line
	 * @param pr
	 * @param userRrn
	 * @throws ClientException
	 */
	public void addMpsLineDelivery(long orgRrn, long userRrn,MpsStatistcLine staticLine,MpsLineDelivery lineDelivery) throws ClientException{
		try {
			lineDelivery.setCreated(new Date());
			lineDelivery.setCreatedBy(userRrn);
			lineDelivery.setUpdated(new Date());
			lineDelivery.setUpdatedBy(userRrn);
			em.persist(lineDelivery);
			updateMpsLineByStatisticLine(orgRrn, userRrn, staticLine);
			StringBuffer whereClause = new StringBuffer();
			whereClause.append("mpsId = '");
			whereClause.append(lineDelivery.getMpsId());
			whereClause.append("' and materialRrn =");
			whereClause.append(lineDelivery.getMaterialRrn());
			wipManager.updateMoAndLinePlanAlarm(orgRrn,userRrn,whereClause.toString());
		} catch (ClientParameterException e){
			throw e;	
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}
	
	//小谢需求得到MO数量大于1界面主计划通知按钮不可用
	public boolean getMoByMpsStatistcLine(Long orgRrn, MpsStatistcLine msl) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("  SELECT OBJECT_RRN  FROM WIP_MO ");
		sql.append(" WHERE ORG_RRN = ? AND MPS_ID = ? ");
		sql.append(" AND DOC_STATUS in ('COMPLETED','CLOSED')  AND MPS_LINE_RRN = ? ");
 
		List<SalePlanLine> salePlanLines = new ArrayList<SalePlanLine>();
		try {
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, msl.getMpsId());
			query.setParameter(3, msl.getMpsLineRrn());
			List<Object[]> result = (List<Object[]>)query.getResultList();
			if(result!=null && result.size() > 0 ){
				return true;
			}
			return false;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<TpsLinePrepare> saveTpsLinePrepareFromIO(String tpsId,InternalOrder io,List<InternalOrderLine> selectIOLines,long userRrn) throws ClientException {
		List<TpsLinePrepare> tlps = new ArrayList<TpsLinePrepare>();
		try {
			for(InternalOrderLine selectIOLine : selectIOLines){
				TpsLinePrepare tpsLinePreare = new TpsLinePrepare();
				tpsLinePreare.setOrgRrn(io.getOrgRrn());
				tpsLinePreare.setIsActive(true);
				tpsLinePreare.setCreated(new Date());
				tpsLinePreare.setCreatedBy(userRrn);
				tpsLinePreare.setUpdated(new Date());
				tpsLinePreare.setUpdatedBy(userRrn);
				tpsLinePreare.setMaterialRrn(selectIOLine.getMaterialRrn());
				tpsLinePreare.setMaterialId(selectIOLine.getMaterialId());
				tpsLinePreare.setMaterialName(selectIOLine.getMaterialName());
				tpsLinePreare.setQtyTps(selectIOLine.getQty());
				tpsLinePreare.setUomId(selectIOLine.getUomId());
				tpsLinePreare.setTpsId(tpsId);
				tpsLinePreare.setOrderId(io.getOrderId());
				tpsLinePreare.setCustomerName(io.getCustomName());
				tpsLinePreare.setCustomerManager(selectIOLine.getCustomerManager());
				tpsLinePreare.setDateCreated(new Date());
				tpsLinePreare.setDateDelivered(selectIOLine.getDateDelivery());
				tpsLinePreare.setPiId(io.getPiNo());
				tpsLinePreare.setInternalOrderId(io.getDocId());
				tpsLinePreare.setSaler(io.getSellerName());
				tpsLinePreare.setComments(selectIOLine.getComments());
				tpsLinePreare.setTpsStatus(TpsLinePrepare.TPSSTATUS_DRAFTED);
				if(io.getOrderId()!=null && io.getOrderId().length() > 2 ){
					String subOrderId = io.getOrderId().substring(0,2);
					if("HW".equals(subOrderId)){
						tpsLinePreare.setSalePlanType("海外市场部");
					}else if("YY".equals(subOrderId)){
						tpsLinePreare.setSalePlanType("营运部");
					}else if("OE".equals(subOrderId)){
						tpsLinePreare.setSalePlanType("OEM");
					}else if("OD".equals(subOrderId)){
						tpsLinePreare.setSalePlanType("OEM");
					}else if("BT".equals(subOrderId)){
						tpsLinePreare.setSalePlanType("奔泰");
					}else if("YS".equals(subOrderId)){
						tpsLinePreare.setSalePlanType("饮水机事业部");
					}
				}
				em.persist(tpsLinePreare);
				selectIOLine.setLineType(InternalOrderLine.LINE_TYPE_PPM);
				selectIOLine.setLineStatus(InternalOrderLine.LINESTATUS_COMPLETED);
				em.merge(selectIOLine);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return tlps;
	}
	
	public InternalOrder saveInternalOrderPoFromIO(InternalOrder io,List<InternalOrderLine> selectIOLines,long userRrn) throws ClientException {
		InternalOrder ioPO  = new InternalOrder();
		try {
			ioPO.setOrgRrn(io.getOrgRrn());
			ioPO.setIsActive(true);
			ioPO.setCreated(new Date());
			ioPO.setCreatedBy(userRrn);
			ioPO.setUpdated(new Date());
			ioPO.setDocType(InternalOrder.DOC_TYPE_PO);
			String docId = ioPO.getDocId();
//			if (docId == null || docId.length() == 0) {
//				ioPO.setDocId(generateIOCode(ioPO));
//			} else {
//				String whereClause = " docId = '" + docId + "'";
//				List<InternalOrder> ios = adManager.getEntityList(ioPO.getOrgRrn(), InternalOrder.class, 2, whereClause, "");
//				if (ios.size() > 0) {
//					throw new ClientParameterException("error.object_duplicate", docId);
//				}
//			}
			ioPO.setDocId(io.getDocId());
			ioPO.setCustomId(io.getCustomId());
			ioPO.setCustomName(io.getCustomName());
			ioPO.setDateDelivered(io.getDateDelivered());
			ioPO.setOrderId(io.getOrderId());
			ioPO.setPiNo(io.getPiNo());
			ioPO.setSelfField2(io.getSelfField2());
			ioPO.setSeller(io.getSeller());
			ioPO.setSellerName(io.getSellerName());
			em.persist(ioPO);
			ioPO.setDocStatus(InternalOrder.STATUS_APPROVED);
			List<InternalOrderLine> lines = new ArrayList<InternalOrderLine>();
			int i = 0;
			for(InternalOrderLine selectOrderLine : selectIOLines){
				InternalOrderLine line = new InternalOrderLine();
				line.setOrgRrn(io.getOrgRrn());
				line.setIsActive(true);
				line.setCreated(new Date());
				line.setCreatedBy(userRrn);
				line.setUpdated(new Date());
				line.setUpdatedBy(userRrn);
				line.setLineNo(new Long((i + 1)*10));
				line.setMaterialRrn(selectOrderLine.getMaterialRrn());
				line.setMaterialId(selectOrderLine.getMaterialId());
				line.setMaterialName(selectOrderLine.getMaterialName());
				line.setUomId(selectOrderLine.getUomId());
				line.setQty(selectOrderLine.getQty());
				line.setQtyOrder(selectOrderLine.getQty());//订单数量
				line.setIoId(ioPO.getDocId());
				line.setIoRrn(ioPO.getObjectRrn());
				line.setLineType(InternalOrderLine.LINE_TYPE_PO);
				line.setLineStatus(InternalOrderLine.LINESTATUS_APPROVED);
				line.setComments(selectOrderLine.getComments());
				line.setDateDelivery(selectOrderLine.getDateDelivery());
				i++;
				em.persist(line);
				lines.add(line);
				selectOrderLine.setLineStatus(InternalOrderLine.LINESTATUS_COMPLETED);
				selectOrderLine.setLineType(InternalOrderLine.LINE_TYPE_PO);
				em.merge(selectOrderLine);
			}
			ioPO.setIoLines(lines);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return ioPO;
	}
	//范总需求生成临时计划
	public void generateManufactureOrderTpsLine2(TpsLine tpsLine, long userRrn) throws ClientException {
		try {
			ManufactureOrder toMo = new ManufactureOrder();
			ADUser adUser =  em.find(ADUser.class, userRrn);
			Date date = new Date();
			toMo.setOrgRrn(tpsLine.getOrgRrn());
			toMo.setIsActive(true);
			toMo.setCreated(date);
			toMo.setCreatedBy(userRrn);
			toMo.setUpdated(date);
			toMo.setUpdatedBy(userRrn);
			
			toMo.setDocStatus(ManufactureOrder.STATUS_DRAFTED);
			
			toMo.setMaterialRrn(tpsLine.getMaterialRrn());
			toMo.setMaterial(tpsLine.getMaterial());
			toMo.setUomId(tpsLine.getUomId());
			toMo.setQtyProduct(tpsLine.getQtyTps());
			toMo.setDateStart(date);
			toMo.setDatePlanStart(date);
			toMo.setTpsRrn(tpsLine.getObjectRrn());
			toMo.setMpsId(tpsLine.getTpsId());
			toMo.setComments(tpsLine.getComments());//从临时计划中带入备注信息
			toMo.setOrderId(tpsLine.getOrderId());//从临时计划中带入订单编号信息
			toMo.setSalePlanType(tpsLine.getSalePlanType());//从临时计划中带入销售类型信息
			toMo.setCustomerName(tpsLine.getCustomerName());//从临时计划中带入客户名信息
			toMo.setSaler(tpsLine.getSaler());//从临时计划中带入业务员信息
			toMo.setDateEnd(tpsLine.getDateDelivered());
			toMo.setDatePlanEnd(tpsLine.getDateDelivered());
			toMo.setUserCreated(adUser.getUserName());
			toMo.setPiId(tpsLine.getPiId());
			// 将交货日期设为生产结束日期
			toMo.setDateDelivery(toMo.getDatePlanEnd());
			
			if(tpsLine.getIsStockUp()){//如果是备货计划,刚生成的工作令是B开头的
				toMo.setMoType(ManufactureOrder.MOTYPE_B);
			}
			List<ManufactureOrderBom> moBoms = wipManager.getMoBom(tpsLine.getOrgRrn(), tpsLine.getMaterialRrn());
		 
			moBoms = wipManager.generateMoBomDetail(toMo, moBoms, true);
			List<DocumentationLine> moLines = wipManager.generateMoLine(toMo, moBoms, true, userRrn);
			
			Requisition pr = new Requisition();
			pr.setOrgRrn(toMo.getOrgRrn());
			pr.setMoRrn(toMo.getObjectRrn());
			pr.setMoId(toMo.getDocId());
			wipManager.addMoBatch(toMo, moLines, moBoms, pr, userRrn);
			TpsLineSysRun tr = new TpsLineSysRun();
			tr.setOrgRrn(tpsLine.getOrgRrn());
			tr.setIsActive(true);
			tr.setMoId(toMo.getDocId());
			tr.setResult(true);
			em.persist(tr);
		} catch (ClientParameterException e){
			throw e;	
		} catch (Exception e) {
			logger.error("generateManufactureOrderTpsLine MpsLine=" + tpsLine.getObjectRrn() 
					+ " Material=" + tpsLine.getMaterialRrn() + " Message: " , e);
			throw new ClientException(e);
		}
	}
}

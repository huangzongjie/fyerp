package com.graly.mes.wip.ejb;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.dialect.OracleDialect;

import com.graly.erp.base.calendar.BusinessCalendar;
import com.graly.erp.base.calendar.Duration;
import com.graly.erp.base.client.BASManager;
import com.graly.erp.base.model.Documentation;
import com.graly.erp.base.model.DocumentationLine;
import com.graly.erp.base.model.LineStartComparator;
import com.graly.erp.base.model.Material;
import com.graly.erp.base.model.Storage;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.LotStorage;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.RecWorkShopHis;
import com.graly.erp.inv.model.UseWorkShopHis;
import com.graly.erp.inv.model.Warehouse;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.pdm.model.BomDetail;
import com.graly.erp.pdm.model.BomDetailPrepare;
import com.graly.erp.ppm.model.MpsLineDelivery;
import com.graly.erp.ppm.model.PasErrorLog;
import com.graly.erp.ppm.model.TpsLine;
import com.graly.erp.pur.client.PURManager;
import com.graly.erp.pur.model.Requisition;
import com.graly.erp.pur.model.RequisitionLine;
//import com.graly.erp.pur.model.TempRequisition;
//import com.graly.erp.pur.model.TempRequisitionLine;
import com.graly.erp.vdm.client.VDMManager;
import com.graly.erp.vdm.model.VendorMaterial;
import com.graly.erp.wip.model.DailyMoMaterial;
import com.graly.erp.wip.model.LargeLot;
import com.graly.erp.wip.model.LargeWipLot;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.erp.wip.model.ManufactureOrderLineDelay;
import com.graly.erp.wip.model.MaterialSum;
import com.graly.erp.wip.model.MaterialUsed;
import com.graly.erp.wip.model.RepScheBomLine;
import com.graly.erp.wip.model.RepScheMoBom;
import com.graly.erp.wip.model.RepScheNoMoBom;
import com.graly.erp.wip.model.WorkSchopMaterial;
//import com.graly.erp.wip.model.TempManufactureOrder;
//import com.graly.erp.wip.model.TempManufactureOrderBom;
//import com.graly.erp.wip.model.TempManufactureOrderLine;
import com.graly.erp.wip.model.WCTLotStorage;
import com.graly.erp.wip.model.WCTMaterialStorage;
import com.graly.erp.wip.model.WCTMovement;
import com.graly.erp.wip.model.WCTMovementLine;
import com.graly.erp.wip.model.WCTMovementLineLot;
import com.graly.erp.wiphis.model.CloseMoLineHis;
import com.graly.erp.wiphis.model.CompleteMoLineHis;
import com.graly.erp.wiphis.model.DisassembleLotHis;
import com.graly.erp.wiphis.model.DisassembleMoLineHis;
import com.graly.erp.wiphis.model.MergeMoLineHis;
import com.graly.erp.wiphis.model.ReceiveLotHis;
import com.graly.erp.wiphis.model.RunMoLineHis;
import com.graly.erp.wiphis.model.SuspendMoLineHis;
import com.graly.erp.wiphis.model.UnMergeMoLineHis;
import com.graly.erp.wiphis.model.UsedLotHis;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.core.exception.ClientParameterException;
import com.graly.framework.security.model.ADUser;
import com.graly.framework.security.model.WorkCenter;
import com.graly.mes.prd.client.PrdManager;
import com.graly.mes.prd.workflow.JbpmException;
import com.graly.mes.prd.workflow.graph.def.Process;
import com.graly.mes.wip.client.LotManager;
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wip.model.InvLot;
import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotComponent;
import com.graly.mes.wip.model.LotConsume;
import com.graly.mes.wiphis.model.LotHis;

@Stateless
@Local(WipManager.class)
@Remote(WipManager.class)
public class WipManagerBean implements WipManager{
	
private static final Logger logger = Logger.getLogger(WipManagerBean.class);
	
	@PersistenceContext
	private EntityManager em;
	
	@EJB
	private ADManager adManager;

	@EJB
	private PDMManager pdmManager;
	
	@EJB
	private INVManager invManager;
	
	@EJB
	private VDMManager vdmManager;
	
	@EJB
	private PURManager purManager;

	@EJB
	private BASManager basManager;
	
	@EJB
	private PrdManager prdManager;
	
//	@EJB
//	private SALManager salManager;
	
	@EJB
	private LotManager lotManager;
	
	//����������е�MoBOM
	@Override
	public List<ManufactureOrderBom> getMoBom(long orgRrn, long materialRrn) throws ClientException {
		List<ManufactureOrderBom> moBoms = new ArrayList<ManufactureOrderBom>();
		try {
			ManufactureOrderBom moBom = new ManufactureOrderBom();
			moBom.setMaterialParentRrn(null);
			moBom.setMaterialRrn(materialRrn);
			moBom.setIsActive(true);
			moBom.setPath(null);
			moBom.setPathLevel(0L);
			moBom.setLineNo(10L);
			moBom.setUnitQty(BigDecimal.ONE);
			moBom.setQtyBom(BigDecimal.ONE);
			Material material = em.find(Material.class, materialRrn);
			moBom.setOrgRrn(orgRrn);
			moBom.setMaterial(material);
			moBom.setUomId(material.getInventoryUom());
			moBoms.add(moBom);
			List<BomDetail> bomDetails = pdmManager.getActualLastBomDetails(materialRrn);
			//���������������У��BOM
			if(material.getIsProduct()) {
				pdmManager.verifyBOM(materialRrn, bomDetails);				
			}
			for (BomDetail bomDetail : bomDetails) {
				moBom = new ManufactureOrderBom();
				moBom.setOrgRrn(orgRrn);
				moBom.setIsActive(true);
				moBom.setMaterialParentRrn(bomDetail.getParentRrn());
				moBom.setMaterialRrn(bomDetail.getChildRrn());
				moBom.setPath(bomDetail.getPath());
				moBom.setRealPath(bomDetail.getRealPath());//��¼�����������ϵ�path
				moBom.setPathLevel(bomDetail.getPathLevel());
				moBom.setRealPathLevel(bomDetail.getRealPathLevel());
				moBom.setLineNo(bomDetail.getSeqNo());
				moBom.setUnitQty(bomDetail.getUnitQty());
				moBom.setQtyBom(bomDetail.getQtyBom());
				moBom.setMaterial(bomDetail.getChildMaterial());
//				if(bomDetail.getChildMaterial()!=null){
//					�����Ƿ�Ԥ�������½�����������ʹ��
//					Material childMaterial = bomDetail.getChildMaterial();
//					if("����".equals(childMaterial.getMaterialCategory2())){
//						List<ManufactureOrderBom> childboms =getMoChildrenBom(orgRrn,childMaterial.getObjectRrn());
//						if(childboms==null || childboms.size() ==0){
//							moBom.setIsSelfControl("Y");
//						}
//					}
//					if(childMaterial.getIsPurchase() && "Y".equals(bomDetail.getIsPrepareBomPurchase())){
//						moBom.setIsSelfControl("Y");
//					}
//				}
				if (bomDetail.getChildMaterial() != null) {
					moBom.setUomId(bomDetail.getChildMaterial().getInventoryUom());
				}
				moBom.setDescription(bomDetail.getDescription());
				moBoms.add(moBom);
			}
		} catch (ClientParameterException e) { 
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return moBoms;
	}
	
	//ֻ���������һ����MoBOM
	public List<ManufactureOrderBom> getMoChildrenBom(long orgRrn, long materialRrn) throws ClientException {
		List<ManufactureOrderBom> moBoms = new ArrayList<ManufactureOrderBom>();
		try {
			ManufactureOrderBom moBom = new ManufactureOrderBom();
			Material material = em.find(Material.class, materialRrn);
			List<BomDetail> bomDetails = pdmManager.getActualLastBomDetails(materialRrn);
			
			List<BomDetail> childBomDetail = new ArrayList<BomDetail>();
			for (BomDetail bomDetail : bomDetails) {
				if (bomDetail.getParentRrn().equals(material.getObjectRrn())) {
					childBomDetail.add(bomDetail);
				}
			}
			
			for (BomDetail bomDetail : childBomDetail) {
				moBom = new ManufactureOrderBom();
				moBom.setOrgRrn(orgRrn);
				moBom.setIsActive(true);
				moBom.setMaterialParentRrn(bomDetail.getParentRrn());
				moBom.setMaterialRrn(bomDetail.getChildRrn());
				moBom.setPath(bomDetail.getPath());
				moBom.setRealPath(bomDetail.getRealPath());//��¼�����������ϵ�path
				moBom.setPathLevel(1L);
				moBom.setRealPathLevel(bomDetail.getRealPathLevel());
				moBom.setLineNo(bomDetail.getSeqNo());
				moBom.setUnitQty(bomDetail.getUnitQty());
				moBom.setQtyBom(bomDetail.getQtyBom());
				moBom.setMaterial(bomDetail.getChildMaterial());
				if (bomDetail.getChildMaterial() != null) {
					moBom.setUomId(bomDetail.getChildMaterial().getInventoryUom());
				}
				moBom.setDescription(bomDetail.getDescription());
				moBoms.add(moBom);
			}
			
			/*
			List<Bom> boms = pdmManager.getChildrenBoms(materialRrn, BigDecimal.ONE);
			
			for (Bom bom : boms) {
				moBom = new ManufactureOrderBom();
				moBom.setOrgRrn(orgRrn);
				moBom.setIsActive(true);
				moBom.setMaterialParentRrn(bom.getParentRrn());
				moBom.setMaterialRrn(bom.getChildRrn());
				moBom.setPath(bom.getPath());
				moBom.setPathLevel(1L);
				moBom.setLineNo(bom.getSeqNo());
				moBom.setUnitQty(bom.getUnitQty());
				moBom.setQtyBom(bom.getQtyBom());
				moBom.setMaterial(bom.getChildMaterial());
				if (bom.getChildMaterial() != null) {
					moBom.setUomId(bom.getChildMaterial().getInventoryUom());
				}
				moBom.setDescription(bom.getDescription());
				moBoms.add(moBom);
			}
			*/
			
		} catch (ClientParameterException e) { 
			throw e;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return moBoms;
	}
	
	@Override
	public List<ManufactureOrderBom> generateMoBomDetail(ManufactureOrder mo, List<ManufactureOrderBom> moBoms) throws ClientException {
		return generateMoBomDetail(mo, moBoms, false);
	}
	
	@Override
	public List<ManufactureOrderBom> generateMoBomDetail(
			ManufactureOrder mo, List<ManufactureOrderBom> moBoms, boolean batchFlag) throws ClientException {
		try {
			Map<String, BusinessCalendar> moCalendarMap = new HashMap<String, BusinessCalendar>();
			BusinessCalendar prCalendar = basManager.getCalendarByDay(mo.getOrgRrn(), BusinessCalendar.CALENDAR_PURCHASE);
			List<String> weekTypes = basManager.getWeekType(mo.getOrgRrn());
			for (String weekType : weekTypes) {
				BusinessCalendar moCalendar = basManager.getCalendarByDayHours(mo.getOrgRrn(), BusinessCalendar.CALENDAR_MANUFACTURE, weekType);
				moCalendarMap.put(weekType, moCalendar);
			}
			
			int level = 0;
			List<ManufactureOrderBom> completeBoms = new ArrayList<ManufactureOrderBom>();
			//��һ��ѭ��,��BOM��߲㿪ʼ������������
			while (true) {
				List<ManufactureOrderBom> filterMoBoms = filterMoBom(moBoms, level);
				if (filterMoBoms == null || filterMoBoms.size() == 0) {
					break;
				}
				for (ManufactureOrderBom moBom : filterMoBoms) {
					ManufactureOrderBom parentBom = getParentMoBom(moBoms, moBom);
					//������������������������
					//����������ָҪ����һ�������ĸ���������Ҫ��ָ��������(��������=����������*��λ����)
					//������������ָҪ����������������Ҫ�������������������������������桢��;�����Ƶ������Ľ��
					if (parentBom != null) {
						//����������ǲɹ�����Ҫ������
						if (parentBom.getIsMaterialNeed()) {
							moBom.setQtyNeed(BigDecimal.ZERO);
						} else {
							moBom.setQtyNeed(parentBom.getQty().multiply(moBom.getUnitQty()));
						}
						moBom = generateQtyProduct(prCalendar, mo, moBom, completeBoms, batchFlag);
					} else {
						//parentBomΪnull��ʾΪMO������������
						moBom.setIsProduct(true);
						moBom.setIsMaterialNeed(false);
						moBom.setQtyNeed(mo.getQtyProduct());
						moBom.setQty(mo.getQtyProduct());
						MaterialSum materialSum = getMaterialSum(moBom.getOrgRrn(), moBom.getMaterialRrn(), batchFlag, true);
						if(materialSum == null) {
							throw new ClientParameterException("wip.material_is_not_lot_control_or_mrp", moBom.getMaterialId());
						}
						moBom = generateDuration(moBom, materialSum);
						moBom.setIqcLeadTime(materialSum.getIqcLeadTime());
						moBom.setStandTime(materialSum.getStandTime());
					}
					completeBoms.add(moBom);
				}
				level++;
			}
			
			//�ڶ���ѭ��,����ײ㿪ʼ������ù�ʱ
			level--;
			completeBoms = new ArrayList<ManufactureOrderBom>();
			while (level >= 0) {
				List<ManufactureOrderBom> filterMoBoms = filterMoBom(moBoms, level);
				if (filterMoBoms == null || filterMoBoms.size() == 0) {
					continue;
				}
				for (ManufactureOrderBom moBom : filterMoBoms) {
					if (moBom.getIsProduct()) {
						BusinessCalendar moCalendar = getBusinessCalendar(moCalendarMap, moBom.getWeekType());
						List<ManufactureOrderBom> childBoms = getChildMoBom(moBoms, moBom);
						
						Date dateStart = moCalendar.add(mo.getDateStart(), new Duration(0 + " " + Duration.BUSINESS_MINUTES));
						//������MO�Ľ���ʱ�䣬ȷ����MO�Ŀ�ʼʱ��
						for (ManufactureOrderBom childBom : childBoms) {
							//�����MO�Ѿ���ʱ����MO�϶���ʱ
							if (childBom.getIsDateNeed() && !childBom.getIsCanStart()) {
								moBom.setIsDateNeed(true);
								break;
							}
							//MO��ʼʱ�������ڴ�MO����MO����ʱ��(�Է���Ϊ��λ)
							if (childBom.getIsCanStart()) {
								dateStart = moCalendar.add(dateStart, new Duration(0 + " " + Duration.BUSINESS_MINUTES));
							} else if (childBom.getDateEnd() == null) {
								dateStart = moCalendar.add(dateStart, new Duration(0 + " " + Duration.BUSINESS_MINUTES));
							} else {
								Date dateEnd = moCalendar.add(childBom.getDateEnd(), new Duration(0 + " " + Duration.BUSINESS_MINUTES));
								//�����PR����PR�Ľ���ʱ��+��������Ϊ��һ�������յĿ�ʼ
								if (childBom.getIsMaterialNeed()) {
									int iqcLeadTime = childBom.getIqcLeadTime() != null ? childBom.getIqcLeadTime().intValue() : 0;
									dateEnd = moCalendar.addDay(dateEnd, iqcLeadTime);
									dateEnd = moCalendar.addDay(dateEnd, 1);
									dateEnd = moCalendar.add(dateEnd, new Duration(0 + " " + Duration.BUSINESS_MINUTES));
								}
								if (dateEnd.compareTo(mo.getDateEnd()) > 0) {
									moBom.setIsDateNeed(true);
									break;
								}
								if (dateStart.compareTo(dateEnd) < 0) {
									dateStart = dateEnd;
								}
							}
						}
						
						if (!moBom.getIsDateNeed()) {
							moBom.setDateStart(dateStart);
							//���ҿ��ù�ʱ
							generateDateEnd(moCalendar, mo, moBom, completeBoms);
							if (!moBom.getIsDateNeed()) {
								completeBoms.add(moBom);
							}
						}
						if(moBom.getOrgRrn().equals(139420L)&& moBom.getMaterial()!=null){
							//���׿������� ��ʱ��Ӧ�Ĵ��ܻ���ע�����Ƽ����� ��ʼ�ͽ���ʱ�� ���ǵ�ǰʱ��
							Material bomMaterial =moBom.getMaterial();
							if(bomMaterial.getIsJit()&& "����".equals(bomMaterial.getMaterialCategory2()) ){
								if("ע��".equals(bomMaterial.getProcessName()) || "����".equals(bomMaterial.getProcessName())){
									moBom.setDateStart(new Date());
									moBom.setDateEnd(new Date());
									if(moBom.getIsDateNeed()){
										String dbaMark = moBom.getDbaMark()!=null?moBom.getDbaMark()+"Y-N":"Y-N";//ϵͳ�����ų̣���Ϊ�����ų�
										moBom.setDbaMark(dbaMark);
										moBom.setIsDateNeed(false);
									}
								}
							}
						}
					}
				}
				level--;
			}
			
			for (ManufactureOrderBom moBom : moBoms) {//ͯ���Ҫ��ʼʱ������ʱ���Ǵ���ʱ��ͼƻ�����
				if(moBom.getOrgRrn().equals(139420L)&&moBom.getPathLevel().equals(0L)){
					if (moBom.getMaterial() != null && moBom.getMaterial().getMeter()!=null) {
					} else {
						moBom.setDateStart(new Date());
						moBom.setDateEnd(mo.getDatePlanEnd());
					}
				}
				em.persist(moBom);
			}
			
		} catch (ClientParameterException e){ 
			throw e;
		} catch (ClientException e){ 
			throw e;
		}  catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return moBoms;
	}
	
	private BusinessCalendar getBusinessCalendar(Map<String, BusinessCalendar> moCalendarMap, String weekType) {
		if (weekType == null || weekType.length() == 0) {
			return moCalendarMap.get(BusinessCalendar.WEEKTYPE_DEFAULT);
		} 
		BusinessCalendar calendar = moCalendarMap.get(weekType);
		if (calendar == null) {
			return moCalendarMap.get(BusinessCalendar.WEEKTYPE_DEFAULT);
		}
		return calendar;
	}
	
	@Override
	public List<ManufactureOrderBom> getMoBomDetailFromDB(ManufactureOrder mo) throws ClientException {
		StringBuffer sql = new StringBuffer(" SELECT ManufactureOrderBom FROM ManufactureOrderBom ManufactureOrderBom ");
		sql.append(" WHERE ");
		sql.append(ADBase.BASE_CONDITION);
		sql.append(" AND moRrn = ? "); 
		sql.append(" ORDER BY pathLevel, lineNo ");
		logger.debug(sql);
		Query query = em.createQuery(sql.toString());
		query.setParameter(1, mo.getOrgRrn());
		query.setParameter(2, mo.getObjectRrn());
		List<ManufactureOrderBom> moBoms = query.getResultList();
		return moBoms;
	}
	
	@Override
	public List<DocumentationLine> getMoLine(ManufactureOrder mo, List<ManufactureOrderBom> moBoms, Long userRrn)  throws ClientException {
		if (mo.getObjectRrn() == null) {
			return generateMoLine(mo, moBoms, false, userRrn);
		} else {
			if(mo.getIsPrepareMo()){
				List<DocumentationLine> docLines = new ArrayList<DocumentationLine>();//MO�����й��������ûobjectRrn�����ɵĹ�����ʹ����ݿ���ȡ�õĹ�����
				if(mo.getIsPrepareMo()){
					//��ǰ̨��������BOMӦ�ð����Ƿ���Ҫ���ɹ�����Ҳ���Ƿ���getMoBomDetailFromDB
					List<DocumentationLine> genMolines =null;
					if(!mo.getHasFirstCountBOM() || mo.getHasFirstCountBOM()==null){
						//��һ��û�����ɹ����
						genMolines = generateMoLineFirstPrepare(mo, moBoms, false, userRrn);
					}else{
						// �����Ҫ���ɹ������BOM�������ɹ�����
						genMolines = generateMoLineCana(mo, moBoms, false, userRrn);
					}
					if(genMolines!=null && genMolines.size() >0){
						docLines.addAll(genMolines);
					}
				}
				//���ݿ��еĹ�����
				List<DocumentationLine>  moLinesFromDB= getMoLineFromDB(mo, moBoms);
				if(moLinesFromDB!=null && moLinesFromDB.size() >0){
					docLines.addAll(moLinesFromDB);
				}
				return docLines;
			}
			return getMoLineFromDB(mo, moBoms);
		}
	}
	
	//����MOLine��PRLine
	@Override
	public List<DocumentationLine> generateMoLine(ManufactureOrder mo, List<ManufactureOrderBom> moBoms, boolean batchFlag, Long userRrn) throws ClientException {
		List<DocumentationLine> moLines = new ArrayList<DocumentationLine>();
		try {
			Warehouse warehouse = invManager.getDefaultWarehouse(mo.getOrgRrn());
			for (ManufactureOrderBom moBom : moBoms) {
				if (moBom.getIsProduct() && moBom.getQty().compareTo(BigDecimal.ZERO) > 0) {
					ManufactureOrderLine moLine = new ManufactureOrderLine();
					moLine.setOrgRrn(mo.getOrgRrn());
					moLine.setIsActive(true);
					moLine.setMaterialRrn(moBom.getMaterialRrn());
					if (!batchFlag) {
						Material material = moBom.getMaterial();
						if (material == null) {
							material = em.find(Material.class, moBom.getMaterialRrn());
						}
						moLine.setMaterial(material);
						// Add by BruceYou 2012-03-13
						moLine.setMaterialName(material.getName());
						moLine.setUomId(material.getInventoryUom());
					}
					moLine.setUomId(moBom.getUomId());
					moLine.setQty(moBom.getQty());
					moLine.setQtyNeed(moBom.getQtyNeed());
					moLine.setQtyAllocation(moBom.getQtyAllocation());
					moLine.setQtyOnHand(moBom.getQtyOnHand());
					moLine.setWorkCenterRrn(moBom.getWorkCenterRrn());
					moLine.setDateStart(moBom.getDateStart());
					moLine.setDateEnd(moBom.getDateEnd());
					moLine.setPath(moBom.getPath());
					moLine.setPathLevel(moBom.getPathLevel());
					moLine.setLineStatus(Documentation.STATUS_DRAFTED);
					moLine.setMoBomRrn(moBom.getObjectRrn());
					moLine.setDescription(mo.getComments());//�����������д��뱸ע��Ϣ
					moLine.setOrderId(mo.getOrderId());//�����������д��붩�������Ϣ
					moLine.setSalePlanType(mo.getSalePlanType());//�����������д�������������Ϣ
					moLine.setCustomerName(mo.getCustomerName());//�����������д���ͻ�����Ϣ
					moLine.setSaler(mo.getSaler());//�����������д���ҵ��Ա��Ϣ
					moLines.add(moLine);
				} else if (moBom.getIsMaterialNeed()){
					RequisitionLine	prLine = new RequisitionLine();
					prLine.setOrgRrn(mo.getOrgRrn());
					prLine.setIsActive(true);
					prLine.setMaterialRrn(moBom.getMaterialRrn());
					VendorMaterial vendorMaterial = moBom.getVendorMaterial();
					if (vendorMaterial == null) {
						vendorMaterial = vdmManager.getPrimaryVendor(prLine.getMaterialRrn());
					}
					if (vendorMaterial == null) {
						Material material = em.find(Material.class, moBom.getMaterialRrn());
						throw new ClientParameterException("pur.material_no_primary_vendor", material.getMaterialId());
					}
//					if (!batchFlag) {
//						//���������С�����͵���������������������ʵ������
//						BigDecimal qtyMPS = moBom.getQty();
//						BigDecimal qtyTheory = getQtyTheory(qtyMPS, vendorMaterial.getLeastQuantity(), vendorMaterial.getIncreaseQuantity());
//						BigDecimal qty = qtyTheory;
//						prLine.setVendorRrn(vendorMaterial.getVendorRrn());
//						prLine.setQtyEconomicSize(vendorMaterial.getLeastQuantity());
//						prLine.setQtyIncreaseSize(vendorMaterial.getIncreaseQuantity());
//						prLine.setQtyMPS(qtyMPS);
//						prLine.setQtyTheory(qtyTheory);
//						prLine.setQty(qty);
//						prLine.setLineTotal(qty.multiply(vendorMaterial.getReferencedPrice()));
//					} else {
//						//�����Batch�������MergerPrLineʱ���¼���
//						prLine.setQty(moBom.getQty());
//						prLine.setLineTotal(moBom.getQty().multiply(vendorMaterial.getReferencedPrice()));
//					}

					prLine.setQty(moBom.getQty());
					prLine.setLineTotal(moBom.getQty().multiply(vendorMaterial.getReferencedPrice()));
					prLine.setLeadTime(vendorMaterial.getLeadTime());

					if (vendorMaterial.getAdvanceRatio() != null && vendorMaterial.getAdvanceRatio().compareTo(BigDecimal.ZERO) > 0) {
						prLine.setAdvancePayment(prLine.getLineTotal().multiply(vendorMaterial.getAdvanceRatio()).divide(new BigDecimal(100)));
					}
					Material material = moBom.getMaterial();
					if (material == null) {
						material = em.find(Material.class, moBom.getMaterialRrn());
					}
					prLine.setMaterial(material);
					prLine.setUomId(material.getInventoryUom());
					prLine.setPackageSpec(material.getPackageSpec());//�Զ�������װ���
					
					prLine.setQtyHandOn(moBom.getQtyOnHand());
					BigDecimal qtyHandOn2 = getWipQtyOnHand(prLine.getOrgRrn(), prLine.getMaterialRrn(), userRrn);
					prLine.setQtyHandOn2(qtyHandOn2);//ֻͳ���˻�����Ʒ������쳵����Ʒ��Ŀ��
					prLine.setQtyTransit(moBom.getQtyTransit());
					prLine.setQtyAllocation(moBom.getQtyAllocation());
					prLine.setQtyMin(moBom.getQtyMin());
					prLine.setQtyNeed(moBom.getQtyNeed());
					prLine.setUnitPrice(vendorMaterial.getLastPrice());
					prLine.setPurchaser(vendorMaterial.getPurchaser());
					prLine.setDateStart(moBom.getDateStart());
					prLine.setDateEnd(moBom.getDateEnd());
					prLine.setPath(moBom.getPath());
					prLine.setPathLevel(moBom.getPathLevel());
					prLine.setWarehouseRrn(warehouse.getObjectRrn());
					prLine.setLineStatus(Documentation.STATUS_DRAFTED);
					prLine.setMoBomRrn(moBom.getObjectRrn());
					moLines.add(prLine);
				}
			}
		} catch (ClientException e){ 
			throw e;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return moLines;
	}
	
	private List<DocumentationLine> getMoLineFromDB(ManufactureOrder mo, List<ManufactureOrderBom> moBoms) throws ClientException {
		List<DocumentationLine> moLines = new ArrayList<DocumentationLine>();
		for (ManufactureOrderBom moBom : moBoms) {
			if (moBom.getMoLineRrn() != null) {
				ManufactureOrderLine moLine = em.find(ManufactureOrderLine.class, moBom.getMoLineRrn());
				moLine.setQtyNeed(moBom.getQtyNeed());
				moLines.add(moLine);
			} else if (moBom.getRequsitionLineRrn() != null) {
				RequisitionLine	prLine = em.find(RequisitionLine.class, moBom.getRequsitionLineRrn());
				if(prLine == null) {
//					prLine = new RequisitionLine();
//					prLine.setOrgRrn(mo.getOrgRrn());
//					prLine.setQty(moBom.getQty());
//					prLine.setMaterialRrn(moBom.getMaterialRrn());
//					prLine.setDateStart(moBom.getDateStart());
//					prLine.setDateEnd(moBom.getDateEnd());
//					prLine.setMaterial(moBom.getMaterial());
//					prLine.setLineStatus(Requisition.STATUS_APPROVED);
					continue;
				}
				try {
					RequisitionLine newPrLine = (RequisitionLine)prLine.clone();
					newPrLine.setObjectRrn(prLine.getObjectRrn());
					newPrLine.setMaterial(prLine.getMaterial());
					newPrLine.setQtyNeed(moBom.getQtyNeed());
					newPrLine.setPath(moBom.getPath());
					newPrLine.setPathLevel(moBom.getPathLevel());
					moLines.add(newPrLine);
				} catch(Exception e) {
					
				}
			}
		}
		return moLines;
	}
	
	@Override
	public ManufactureOrder saveMo(ManufactureOrder mo, List<DocumentationLine> moLines, 
			List<ManufactureOrderBom> moBoms, long userRrn) throws ClientException {
		if (mo.getObjectRrn() == null) {
			return addMo(mo, moLines, moBoms, userRrn);
		} else {
			if(mo.getIsPrepareMo()){
				//����������ɵĹ�������д���
				addMoCana(mo, moLines, moBoms, userRrn);
				
				List<ManufactureOrderBom> boms =  adManager.getEntityList(mo.getOrgRrn(),
						ManufactureOrderBom.class,Integer.MAX_VALUE, 
						 "moRrn ="+mo.getObjectRrn()+" and ( isPrepareMoLine ='Y' or againGenMoLine ='Y' ) ",null);
				mo.setHasFirstCountBOM(true);
				if(boms==null || boms.size()==0){
					//û��Ԥ�������Ϻ���Ҫ���ɹ����������
					mo.setIsPrepareMo(false);
					mo.setDateApproved(new Date());
					mo.setDocStatus(ManufactureOrder.STATUS_APPROVED);
				}
				//����BOM��ҪagaginGenMoLine��BOM���� hasPrepareMoLine����ΪY
				List<ManufactureOrderBom> againMoLineBoms =  adManager.getEntityList(mo.getOrgRrn(),ManufactureOrderBom.class,Integer.MAX_VALUE, 
						 "moRrn ="+mo.getObjectRrn()+" and againGenMoLine ='Y'  ",null);
				if(againMoLineBoms!=null  && againMoLineBoms.size() >0 ){
					mo.setHasPrepareMoLine(true);
				}else{
					mo.setHasPrepareMoLine(false);
				}
				
				
				List<ManufactureOrderLine> allMoLines = (List<ManufactureOrderLine>)adManager.getEntityList(mo.getOrgRrn(),ManufactureOrderLine.class,
						Integer.MAX_VALUE,"masterMoRrn = "+mo.getObjectRrn(),null);//���ݿ����Ѿ����ڵĹ�����
				//������docLines���������
				List<DocumentationLine> docLines = new ArrayList<DocumentationLine>();
				for(ManufactureOrderLine moline : allMoLines){
					docLines.add(moline);
				}
				return updateMo(mo, docLines, moBoms, userRrn);
			}
			return updateMo(mo, moLines, moBoms, userRrn);
		}
	}
	
	public ManufactureOrder addMo(ManufactureOrder mo, List<DocumentationLine> moLines, 
			List<ManufactureOrderBom> moBoms, long userRrn) throws ClientException{
		try {
			Date moDateStart = null;
			Date moDateEnd = null;
			
			mo.setIsActive(true);
			mo.setCreatedBy(userRrn);
			mo.setUpdatedBy(userRrn);
			mo.setCreated(new Date());
			//parpare�����������
			if(mo.getIsPrepareMo()){
				mo.setDocStatus(Documentation.STATUS_PREPARE);
			}else{
				mo.setDocStatus(Documentation.STATUS_DRAFTED);
			}
			if (ManufactureOrder.MOTYPE_P.equals(mo.getMoType())) {
				mo.setDocType(Documentation.DOCTYPE_PMO);
			}else if (ManufactureOrder.MOTYPE_B.equals(mo.getMoType())) {
				mo.setDocType(Documentation.DOCTYPE_BMO);
			} else {
				mo.setDocType(Documentation.DOCTYPE_AMO);
			}
			mo.setDocId(this.generateMoCode(mo));
			ADUser user = em.find(ADUser.class, userRrn);
			mo.setUserCreated(user.getUserName());
			
			WorkCenter wc = getWorkCenterByMaterial(mo.getOrgRrn(), mo.getMaterialRrn());
			if (wc == null) {
				throw new ClientParameterException("wip.not_workcenter_found", mo.getMaterialId());
			}
			mo.setWorkCenterRrn(wc.getObjectRrn());;
			mo.setWorkCenterId(wc.getName());
			
			Material material = em.find(Material.class, mo.getMaterialRrn());
			mo.setStandTime(material.getStandTime());
			mo.setMaterialName(material.getName());
			if(mo.getTpsRrn() != null){//�����ͨ����ʱ�ƻ���������Ҫ���һ��ͬʱ�ǲ����Ѿ��������˴�������ͬ�Ĺ�����
				StringBuffer sql = new StringBuffer();
				sql.append(" SELECT ManufactureOrder FROM ManufactureOrder ManufactureOrder WHERE ManufactureOrder.tpsRrn = ? ");
				sql.append(" AND ManufactureOrder.docStatus <> 'CLOSED'");
				Query query = em.createQuery(sql.toString());
				query.setParameter(1, mo.getTpsRrn());
				List rslt = query.getResultList();
				if(rslt != null && rslt.size() > 0){
					throw new ClientException("wip.tps_has_mo_yet");
				}
			}
			em.persist(mo);
			
			for (ManufactureOrderBom moBom : moBoms) {
				moBom.setMoRrn(mo.getObjectRrn());
				if (moBom.getObjectRrn() == null) {
					em.persist(moBom);
				} else {
					em.merge(moBom);
				}
				
				//�ı��ѷ���������
				List<ManufactureOrderBom> childBoms = getChildMoBom(moBoms, moBom);
				for (ManufactureOrderBom childBom : childBoms) {
					material = em.find(Material.class, childBom.getMaterialRrn());	
//					if (!material.getIsMrp() || !material.getIsLotControl()) {
//						continue;
//					}
					if (!material.getIsLotControl()) {
						continue;
					}
					//ȡ���������ϱ����ѷ�����
//					BigDecimal qtyAllocation = material.getQtyAllocation() == null ? BigDecimal.ZERO : material.getQtyAllocation();
//					material.setQtyAllocation(qtyAllocation.add(childBom.getQtyNeed()));
//					em.merge(material);
				}
			}
			
			Requisition pr = new Requisition();
			pr.setOrgRrn(mo.getOrgRrn());
			pr.setMoRrn(mo.getObjectRrn());
			pr.setMoId(mo.getDocId());
			int i = 0;
			for (DocumentationLine docLine : moLines) {
				if (docLine instanceof ManufactureOrderLine) {
					ManufactureOrderLine moLine = (ManufactureOrderLine)docLine;
					//û�п�ʼ�����ʱ�䣬������MoLine
					if (moLine.getDateStart() == null || moLine.getDateEnd() == null) {
						continue;
					}
					
					Material parentMaterial = em.find(Material.class, mo.getMaterialRrn());
					if("���Ʒ�װ��".equals(parentMaterial.getProcessName())){
						moLine.setParentMaterialId(parentMaterial.getMaterialId());
						moLine.setParentMaterialName(parentMaterial.getName());
					}
					if("�Զ���".equals(parentMaterial.getProcessName())){
						moLine.setParentMaterialId(parentMaterial.getMaterialId());
						moLine.setParentMaterialName(parentMaterial.getName());
					}
					if("����".equals(parentMaterial.getProcessName())){
						moLine.setParentMaterialId(parentMaterial.getMaterialId());
						moLine.setParentMaterialName(parentMaterial.getName());
					}
					
					moLine.setIsActive(true);
					moLine.setCreatedBy(userRrn);
					moLine.setCreated(new Date());
					moLine.setUpdatedBy(userRrn);
					moLine.setMasterMoRrn(mo.getObjectRrn());
					moLine.setMasterMoId(mo.getDocId());
					em.persist(moLine);
					
					//MOLine�����翪ʼʱ����ΪMO��ʼʱ��
					if (moLine.getDateStart() != null) {
						if (moDateStart == null) {
							moDateStart = moLine.getDateStart();
						} else if (moDateStart.compareTo(moLine.getDateStart()) > 0) {
							moDateStart = moLine.getDateStart();
						}
					}
					if (mo.getMaterialRrn().equals(moLine.getMaterialRrn())) {
						moDateEnd = moLine.getDateEnd();
					}
					
					ManufactureOrderBom moBom;
					if (moLine.getMoBomRrn() != null) {
						moBom = getCurrentMoBom(moBoms, moLine.getMoBomRrn().longValue());
					} else {
						moBom = getCurrentMoBom(moBoms, moLine.getPathLevel(), moLine.getMaterialRrn(), moLine.getPath());
					}
					if (moBom != null) {
						moBom.setMoLineRrn(moLine.getObjectRrn());
						em.merge(moBom);
					}
					
				} else if (docLine instanceof RequisitionLine) {
					RequisitionLine prLine = (RequisitionLine)docLine;
					List<RequisitionLine> prLines = new ArrayList<RequisitionLine>();
					prLines.add(prLine);
					if (prLine.getObjectRrn() != null) {
						pr = em.find(Requisition.class, prLine.getRequisitionRrn());
					} else {
						prLine.setLineNo((long)10 + i * 10);
					}
					pr = purManager.savePRLine(pr, prLines, true, userRrn);
					prLine = pr.getPrLines().get(0);
					
					ManufactureOrderBom moBom;
					if (prLine.getMoBomRrn() != null) {
						moBom = getCurrentMoBom(moBoms, prLine.getMoBomRrn().longValue());
					} else {
						moBom = getCurrentMoBom(moBoms, prLine.getPathLevel(), prLine.getMaterialRrn(), prLine.getPath());
					}
					if (moBom != null) {
						moBom.setRequsitionLineRrn(prLine.getObjectRrn());
						em.merge(moBom);
					}
					
					i++;
				}
			}
			if(mo.getIsPrepareMo()){
				if(moDateStart==null || moDateEnd ==null){
					moDateStart = mo.getDateStart();
					moDateEnd = mo.getDateEnd();
				}

			}

			mo.setDateStart(moDateStart);
			mo.setDateEnd(moDateEnd);

			em.merge(mo);
			// �����Ӧ������ʱ���ۼƻ����������ʱ���ۼƻ���isGenerate
			if(mo.getTpsRrn() != null) {
				TpsLine tpsLine = em.find(TpsLine.class, mo.getTpsRrn());
				if(tpsLine != null) {
					tpsLine.setIsGenerate(true);
					em.merge(tpsLine);
				}
			}
			em.flush();
			if(pr.getObjectRrn() != null) {
				mergePrLine(pr);
			}
			
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (ClientException e){ 
			throw e;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return mo;
	}
	
	//��Batch�����д���MO(��ֱ��AddMo����)
	@Override
	public void addMoBatch(ManufactureOrder mo, List<DocumentationLine> moLines, 
			List<ManufactureOrderBom> moBoms, Requisition pr, long userRrn) throws ClientException{
		Map<Long, MaterialSum> sums = new HashMap<Long, MaterialSum>();
		try {
			
			Date moDateStart = null;
			Date moDateEnd = null;

			mo.setIsActive(true);
			mo.setCreatedBy(userRrn);
			mo.setUpdatedBy(userRrn);
			mo.setCreated(new Date());
			mo.setDocStatus(Documentation.STATUS_DRAFTED);
			mo.setMoType(ManufactureOrder.MOTYPE_P);
			mo.setDocType(Documentation.DOCTYPE_PMO);
			mo.setDocId(this.generateMoCode(mo));
			ADUser user = em.find(ADUser.class, userRrn);
			mo.setUserCreated(user.getUserName());
			
			WorkCenter wc = getWorkCenterByMaterial(mo.getOrgRrn(), mo.getMaterialRrn());
			if (wc == null) {
				throw new ClientParameterException("wip.not_workcenter_found", mo.getMaterialId());
			}
			mo.setWorkCenterRrn(wc.getObjectRrn());;
			mo.setWorkCenterId(wc.getName());
			
			Material material = em.find(Material.class, mo.getMaterialRrn());
			mo.setStandTime(material.getStandTime());
			mo.setMaterialName(material.getName());
			em.persist(mo);
			
			for (ManufactureOrderBom moBom : moBoms) {
				moBom.setMoRrn(mo.getObjectRrn());
				if (moBom.getObjectRrn() == null) {
					em.persist(moBom);
				} else {
					em.merge(moBom);
				}
				
				//�ı��ѷ���������
				List<ManufactureOrderBom> childBoms = getChildMoBom(moBoms, moBom);
				for (ManufactureOrderBom childBom : childBoms) {
					material = em.find(Material.class, childBom.getMaterialRrn());	
//					if (!material.getIsMrp() || !material.getIsLotControl()) {
//						continue;
//					}
					if (!material.getIsLotControl()) {
						continue;
					}
					
					//ȡ���������ϱ����ѷ�����
//					BigDecimal qtyAllocation = material.getQtyAllocation() == null ? BigDecimal.ZERO : material.getQtyAllocation();
//					material.setQtyAllocation(qtyAllocation.add(childBom.getQtyNeed()));
//					em.merge(material);
					
					MaterialSum materialSum;
					if (sums.containsKey(childBom.getMaterialRrn())) {
						materialSum = sums.get(childBom.getMaterialRrn());
					} else {
						materialSum = getMaterialSum(mo.getOrgRrn(), childBom.getMaterialRrn(), true, true);
						sums.put(childBom.getMaterialRrn(), materialSum);
					}
					if(materialSum != null){
						materialSum.setQtyAllocation(materialSum.getQtyAllocation().add(childBom.getQtyNeed()));
					}
				}
			}
			
			for (DocumentationLine docLine : moLines) {
				if (docLine instanceof ManufactureOrderLine) {
					ManufactureOrderLine moLine = (ManufactureOrderLine)docLine;
//					//û�п�ʼ�����ʱ�䣬������MoLine
//					if (moLine.getDateStart() == null || moLine.getDateEnd() == null) {
//						continue;
//					}
					moLine.setIsActive(true);
					moLine.setCreatedBy(userRrn);
					moLine.setCreated(new Date());
					moLine.setUpdatedBy(userRrn);
					moLine.setMasterMoRrn(mo.getObjectRrn());
					moLine.setMasterMoId(mo.getDocId());
					em.persist(moLine);
					
					//MOLine�����翪ʼʱ����ΪMO��ʼʱ��
					if (moLine.getDateStart() != null) {
						if (moDateStart == null) {
							moDateStart = moLine.getDateStart();
						} else if (moDateStart.compareTo(moLine.getDateStart()) > 0) {
							moDateStart = moLine.getDateStart();
						}
					}
					
					if (mo.getMaterialRrn().equals(moLine.getMaterialRrn())) {
						moDateEnd = moLine.getDateEnd();
						//���MOLine������MO�Ĳ�Ʒ,�����������ѷ�����,���ⱻ����ʹ��
						MaterialSum materialSum;
						if (sums.containsKey(moLine.getMaterialRrn())) {
							materialSum = sums.get(moLine.getMaterialRrn());
						} else {
							materialSum = getMaterialSum(mo.getOrgRrn(), moLine.getMaterialRrn(), true, true);
							sums.put(moLine.getMaterialRrn(), materialSum);
						}
						materialSum.setQtyAllocation(materialSum.getQtyAllocation().add(moLine.getQty()));
					}
					
					ManufactureOrderBom moBom;
					if (moLine.getMoBomRrn() != null) {
						moBom = getCurrentMoBom(moBoms, moLine.getMoBomRrn().longValue());
					} else {
						moBom = getCurrentMoBom(moBoms, moLine.getPathLevel(), moLine.getMaterialRrn(), moLine.getPath());
					}
					if (moBom != null) {
						moBom.setMoLineRrn(moLine.getObjectRrn());
						em.merge(moBom);
					}
					
					//���¼�������Ʒ��
					MaterialSum materialSum = sums.get(moLine.getMaterialRrn());
//					materialSum.setQtyMoLine(materialSum.getQtyMoLine().add(docLine.getQty()));
					materialSum.setQtyMoLineWip(materialSum.getQtyMoLineWip().add(moLine.getQty()));
					
				} else if (docLine instanceof RequisitionLine) {
					RequisitionLine prLine = (RequisitionLine)docLine;
					List<RequisitionLine> prLines = new ArrayList<RequisitionLine>();
					prLine.setLineNo(10L);
					prLines.add(prLine);
										
					pr = purManager.savePRLine(pr, prLines, true, userRrn);
					prLine = pr.getPrLines().get(0);
					
					ManufactureOrderBom moBom;
					if (prLine.getMoBomRrn() != null) {
						moBom = getCurrentMoBom(moBoms, prLine.getMoBomRrn().longValue());
					} else {
						moBom = getCurrentMoBom(moBoms, prLine.getPathLevel(), prLine.getMaterialRrn(), prLine.getPath());
					}
					if (moBom != null) {
						moBom.setRequsitionLineRrn(prLine.getObjectRrn());
						em.merge(moBom);
					}
						
					//���¼�����;
					MaterialSum materialSum = sums.get(prLine.getMaterialRrn());
					materialSum.setQtyTransit(materialSum.getQtyTransit().add(prLine.getQty()));
				}
			}
			
			for (MaterialSum materialSum : sums.values()) {
				em.merge(materialSum);
			}
			
			mo.setDateStart(moDateStart);
			mo.setDateEnd(moDateEnd);
			em.merge(mo);

		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (ClientException e){ 
			throw e;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//����MO��MOLine,ֻ�޸�ʱ�䲻�޸�����
	public ManufactureOrder updateMo(ManufactureOrder mo, List<DocumentationLine> moLines, 
			List<ManufactureOrderBom> moBoms, long userRrn) throws ClientException{
		try {
			Date moDateStart = null;
			Date moDateEnd = null;
			Set<Long> prSet = new HashSet<Long>();
			
			for (DocumentationLine docLine : moLines) {
				if (docLine instanceof ManufactureOrderLine) {
					ManufactureOrderLine moLine = (ManufactureOrderLine)docLine;
					moLine.setUpdatedBy(userRrn);
					moLine.setDescription(mo.getComments());
					em.merge(moLine);
					
					//MOLine�����翪ʼʱ����ΪMO��ʼʱ��
					if (moLine.getDateStart() != null) {
						if (moDateStart == null) {
							moDateStart = moLine.getDateStart();
						} else if (moDateStart.compareTo(moLine.getDateStart()) > 0) {
							moDateStart = moLine.getDateStart();
						}
					}
					if (mo.getMaterialRrn().equals(moLine.getMaterialRrn())) {
						moDateEnd = moLine.getDateEnd();
					}

				} else if (docLine instanceof RequisitionLine) {
					if (prSet.contains(docLine.getObjectRrn())) {
						continue;
					} else {
						prSet.add(docLine.getObjectRrn());
					}
					RequisitionLine prLine = (RequisitionLine)docLine;
					List<RequisitionLine> prLines = new ArrayList<RequisitionLine>();
					prLines.add(prLine);
					Requisition pr = em.find(Requisition.class, prLine.getRequisitionRrn());
					pr = purManager.savePRLine(pr, prLines, true, userRrn);
				}
			}
			
			if(mo.getIsPrepareMo()){
				//���������ٴθ���û�й����������BUG��ʵ���������û�й����������Ƚ��٣�
				if(moDateStart!=null && moDateEnd!=null){
					mo.setDateStart(moDateStart);
					mo.setDateEnd(moDateEnd);
				}
				List<ManufactureOrderBom> prepareMoLineBoms = adManager.getEntityList(mo.getOrgRrn(), ManufactureOrderBom.class,
						Integer.MAX_VALUE,"isPrepareMoLine = 'Y' and moRrn = "+ mo.getObjectRrn(),null);
				if( prepareMoLineBoms==null || prepareMoLineBoms.size() == 0){
					mo.setDocStatus(Documentation.STATUS_APPROVED);
				}
			}else{
				mo.setDateStart(moDateStart);
				mo.setDateEnd(moDateEnd);
			}
			mo.setUpdatedBy(userRrn);
			em.merge(mo);
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (ClientException e){ 
			throw e;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return mo;
	}
	
	//�ϲ�PR
	@Override
	public Requisition mergePrLine(Requisition pr) throws ClientException {
		StringBuffer sql = new StringBuffer(" SELECT MATERIAL_RRN, DATE_END, SUM(QTY) QTY, SUM(LINE_TOTAL) LINE_TOTAL, " +
				" NVL(SUM(ADVANCE_PAYMENT), 0) ADVANCE_PAYMENT FROM PUR_REQUISITION_LINE ");
		sql.append(" WHERE REQUISITION_RRN = ? "); 
		sql.append(" GROUP BY MATERIAL_RRN, DATE_END "); 
		sql.append(" ORDER BY MATERIAL_RRN, DATE_END "); 
		long totalLines = 0;
		BigDecimal total = BigDecimal.ZERO;
		try {
			pr = em.find(Requisition.class, pr.getObjectRrn());
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, pr.getObjectRrn());
			List<Object[]> result = query.getResultList();
			if (result.size() == 0) {
				em.remove(pr);
				return null;
			}
			String whereClause = " requisitionRrn = '" + pr.getObjectRrn() + "' ";
			List<RequisitionLine> prLines = adManager.getEntityList(pr.getOrgRrn(), RequisitionLine.class, Integer.MAX_VALUE, whereClause, "");

			sql = new StringBuffer(" DELETE FROM RequisitionLine WHERE requisitionRrn = ? ");
			query = em.createQuery(sql.toString());
			query.setParameter(1, pr.getObjectRrn());
			query.executeUpdate();
			
			for (Object[] row : result) {
				long materialRrn = Long.parseLong(String.valueOf(row[0]));
				Date dateEnd = (Date)row[1];
				BigDecimal qtyMPS = (BigDecimal)row[2];
				BigDecimal lineTotal = (BigDecimal)row[3];
				BigDecimal advancePayment = (BigDecimal)row[4];
				
				List<RequisitionLine> mergeLines = getCanMergePrLine(prLines, materialRrn, dateEnd);
				String inString = "";
				for (RequisitionLine prLine : mergeLines) {
					inString = inString + " '" + prLine.getObjectRrn() + "',";
				}
				inString = inString.substring(0, inString.length() - 1);
				RequisitionLine lastLine = this.getLastPrLine(mergeLines);
//				RequisitionLine firstLine = this.getFirstPrLine(mergeLines);
				RequisitionLine prLine = (RequisitionLine)lastLine.clone();
				prLine.setObjectRrn(null);
				
				//��桢�ѷ�����ȡ����PRLine,��;�������¼���
				if (prLine.getQtyTransit() != null) {
					//����;��=���һ����;��+���PR-�ϲ���PR֮��
					prLine.setQtyTransit(prLine.getQtyTransit().add(lastLine.getQty()).subtract(qtyMPS));
				}
//				prLine.setQtyTransit(firstLine.getQtyTransit());
				
				//���������С�����͵���������������������ʵ������
				VendorMaterial vendorMaterial = vdmManager.getPrimaryVendor(prLine.getMaterialRrn());	
				BigDecimal qtyTheory = getQtyTheory(qtyMPS, vendorMaterial.getLeastQuantity(), vendorMaterial.getIncreaseQuantity());
				BigDecimal qty = qtyTheory;
				
				prLine.setVendorRrn(vendorMaterial.getVendorRrn());
				prLine.setQtyEconomicSize(vendorMaterial.getLeastQuantity());
				prLine.setQtyIncreaseSize(vendorMaterial.getIncreaseQuantity());
				prLine.setQtyMPS(qtyMPS);
				prLine.setQtyTheory(qtyTheory);
				prLine.setQty(qty);
				prLine.setLineTotal(qty.multiply(vendorMaterial.getReferencedPrice()));
				total = total.add(prLine.getLineTotal());
				if (vendorMaterial.getAdvanceRatio() != null && vendorMaterial.getAdvanceRatio().compareTo(BigDecimal.ZERO) > 0) {
					prLine.setAdvancePayment(prLine.getLineTotal().multiply(vendorMaterial.getAdvanceRatio()).divide(new BigDecimal(100)));
				}
				prLine.setLineNo((totalLines + 1) * 10);
				
				em.persist(prLine);
				
				sql = new StringBuffer(" UPDATE WIP_MO_BOM SET REQUISITION_LINE_RRN = ? WHERE REQUISITION_LINE_RRN IN(");
				sql.append(inString);
				sql.append(")");
				query = em.createNativeQuery(sql.toString());
				query.setParameter(1, prLine.getObjectRrn());
				query.executeUpdate();
				totalLines++;
			}
			pr.setIsActive(true);
			pr.setTotal(total);
			pr.setTotalLines(totalLines);
			em.merge(pr);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return pr;
	}
	
	private List<RequisitionLine> getCanMergePrLine(List<RequisitionLine> lines, long materialRrn, Date dateEnd) {
		List<RequisitionLine> mergeLines = new ArrayList<RequisitionLine>();
		for (RequisitionLine line : lines) {
			if (line.getMaterialRrn().equals(materialRrn) && dateEnd.compareTo(line.getDateEnd()) == 0) {
				mergeLines.add(line);
			}
		}
		return mergeLines;
	}
	
	private RequisitionLine getLastPrLine(List<RequisitionLine> lines) {
//		RequisitionLine lastPrLine = null;
//		Date lastDate = null;
//		for (RequisitionLine line : lines) {
//			if (lastDate == null) {
//				lastDate = line.getCreated();
//				lastPrLine = line;
//			} else {
//				if (lastDate.compareTo(line.getCreated()) < 0) {
//					lastDate = line.getCreated();
//					lastPrLine = line;
//				} else if (lastDate.compareTo(line.getCreated()) == 0) {
//					if (lastPrLine.getObjectRrn().compareTo(line.getObjectRrn()) < 0) {
//						lastPrLine = line;
//					}
//				}
//			}
//		}
//		return lastPrLine;
		RequisitionLine lastPrLine = null;
		BigDecimal lastTransit = null;
		for (RequisitionLine line : lines) {
			if (lastTransit == null) {
				lastTransit = line.getQtyTransit();
				lastPrLine = line;
			} else {
				if (lastTransit.compareTo(line.getQtyTransit()) < 0) {
					lastTransit = line.getQtyTransit();
					lastPrLine = line;
				} 
			}
		}
		return lastPrLine;
	}
	
	//ɾ����Ӧ��PR
	@Override
	public void deleteMo(ManufactureOrder mo, long userRrn) throws ClientException {
		try{
			if (!Documentation.STATUS_DRAFTED.equals(mo.getDocStatus()) && !Documentation.STATUS_PREPARE.equals(mo.getDocStatus())) {
				throw new ClientException("common.can_not_delete");
			}
			
			boolean refPrFlag = false;
			Requisition moPr = null;
			StringBuffer sql = new StringBuffer(" SELECT Requisition FROM Requisition Requisition WHERE moRrn = ? ");
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, mo.getObjectRrn());
			List<Requisition> requisitions = query.getResultList();
			if (requisitions.size() > 0) {
				refPrFlag = true;
				Requisition pr = requisitions.get(0);
				if (Documentation.STATUS_DRAFTED.equals(pr.getDocStatus())) {
					purManager.deletePR(pr, userRrn);
				}
			} else {
				if(mo.getMpsRrn() != null) {
					sql = new StringBuffer(" SELECT Requisition FROM Requisition Requisition WHERE mpsRrn = ? ");
					query = em.createQuery(sql.toString());
					query.setParameter(1, mo.getMpsRrn());
					requisitions = query.getResultList();
					if (requisitions.size() > 0) {
						moPr = requisitions.get(0);
					}
				}
			}
			
			List<ManufactureOrderBom> moBoms = getMoBomDetailFromDB(mo); 
			for (ManufactureOrderBom moBom : moBoms) {
				//�ı��ѷ���������
				List<ManufactureOrderBom> childBoms = getChildMoBom(moBoms, moBom);
				for (ManufactureOrderBom childBom : childBoms) {
					
					Material material = em.find(Material.class, childBom.getMaterialRrn());	
//					if (!material.getIsMrp() || !material.getIsLotControl()) {
//						continue;
//					}
					if (!material.getIsLotControl()) {
						continue;
					}
					
					//ȡ���������ϱ����ѷ�����
//					BigDecimal qtyAllocation = material.getQtyAllocation() == null ? BigDecimal.ZERO : material.getQtyAllocation();
//					material.setQtyAllocation(qtyAllocation.subtract(childBom.getQtyNeed()));
//					em.merge(material);
					
					if (!material.getIsMrp()) {
						continue;
					}
					
					if (!refPrFlag && moPr != null && material.getIsPurchase()) {
						List<RequisitionLine> prLines = purManager.getPrLineByMaterial(moPr.getOrgRrn(), moPr.getObjectRrn(), material.getObjectRrn());
						BigDecimal qtyNeed = childBom.getQtyNeed();
						for (RequisitionLine prLine : prLines) {
							if (Documentation.STATUS_DRAFTED.equals(prLine.getLineStatus())) {
								if (qtyNeed.compareTo(BigDecimal.ZERO) <= 0) {
									break;
								}
								if(prLine.getQtyMPS() != null){
									if (prLine.getQtyMPS().compareTo(qtyNeed) > 0) {
										prLine.setQtyMPS(prLine.getQtyMPS().subtract(childBom.getQtyNeed()));
										BigDecimal oldQty = prLine.getQty();
										BigDecimal qtyTheory = getQtyTheory(prLine.getQtyMPS(), prLine.getQtyEconomicSize(), prLine.getQtyIncreaseSize());
										BigDecimal qty = qtyTheory;
										prLine.setQtyTheory(qtyTheory);
										prLine.setQty(qty);
										prLine.setLineTotal(qty.multiply(prLine.getUnitPrice()));
										if (prLine.getAdvancePayment() != null && oldQty.compareTo(BigDecimal.ZERO) == 0) {
											prLine.setAdvancePayment(prLine.getAdvancePayment().multiply(prLine.getQty().divide(oldQty)));
										}
										purManager.savePRLine(prLine, userRrn);
									} else {
										purManager.deletePRLine(prLine, userRrn);
										qtyNeed.subtract(prLine.getQtyMPS());
									}
								}
							}
						}
					}
					
				}
				em.remove(moBom);
			}
			
			//Delete ManufactureOrderLine
			sql = new StringBuffer(" DELETE FROM ManufactureOrderLine WHERE masterMoRrn = ? ");
			query = em.createQuery(sql.toString());
			query.setParameter(1, mo.getObjectRrn());
			query.executeUpdate();
			// �����Ӧ������ʱ���ۼƻ����������ʱ���ۼƻ���isGenerate
			if(mo.getTpsRrn() != null) {
				TpsLine tpsLine = em.find(TpsLine.class, mo.getTpsRrn());
				if(tpsLine != null) {
					tpsLine.setIsGenerate(false);
					em.merge(tpsLine);
				}
			}
			
			mo = em.find(ManufactureOrder.class, mo.getObjectRrn());
			em.remove(mo);
			
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	@Override
	public void approveMo(ManufactureOrder mo, long userRrn) throws ClientException {
		try{
			if (!Documentation.STATUS_DRAFTED.equals(mo.getDocStatus())) {
				throw new ClientException("common.can_not_approve");
			}
			String whereClause = " masterMoRrn = '" + mo.getObjectRrn() + "' AND materialRrn = '" + mo.getMaterialRrn() + "'";
			List<ManufactureOrderLine> masterMoLines = adManager.getEntityList(mo.getOrgRrn(), ManufactureOrderLine.class, Integer.MAX_VALUE, whereClause, "");
			if (masterMoLines.size() == 0) {
				throw new ClientException("wip.mo_not_integrity");
			} 
			
			whereClause = " masterMoRrn = '" + mo.getObjectRrn() + "'";
			List<ManufactureOrderLine> moLines = adManager.getEntityList(mo.getOrgRrn(), ManufactureOrderLine.class, Integer.MAX_VALUE, whereClause, "");
			for (ManufactureOrderLine moLine : moLines) {
				if (moLine.getDateStart() == null || moLine.getDateEnd() == null) {
					throw new ClientException("wip.mo_not_integrity");
				}
				moLine.setLineStatus(Documentation.STATUS_APPROVED);
				em.merge(moLine);
			}
			mo.setDocStatus(Documentation.STATUS_APPROVED);
			mo.setUpdatedBy(userRrn);
			ADUser user = em.find(ADUser.class, userRrn);
			mo.setUserApproved(user.getUserName());
			em.merge(mo);
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//��Close��Ӧ��PR
	@Override
	public void closeMo(ManufactureOrder mo, long userRrn) throws ClientException {
		try{
			if (!Documentation.STATUS_APPROVED.equals(mo.getDocStatus())) {
				throw new ClientException("common.can_not_close");
			}
//			if(mo.getQtyReceive() != null
//					&& mo.getQtyReceive().compareTo(mo.getQtyIn()) > 0) {
//				//������δ��ⲻ�ܹر�
//				throw new ClientException("wip.is_not_received_finished");
//			}
			String whereClause = " masterMoRrn = '" + mo.getObjectRrn() + "'";
			List<ManufactureOrderLine> moLines = adManager.getEntityList(mo.getOrgRrn(), ManufactureOrderLine.class, Integer.MAX_VALUE, whereClause, "");
			for (ManufactureOrderLine moLine : moLines) {
				if (ManufactureOrderLine.WORK_STATUS_RUNNING.equals(moLine.getWorkStatus())) {
					throw new ClientParameterException("wip.can_not_close_at_running", moLine.getMaterialId());
				}
			}
			
			List<ManufactureOrderBom> moBoms = getMoBomDetailFromDB(mo); 
			for (ManufactureOrderBom moBom : moBoms) {
				if (moBom.getMoLineRrn() == null) {
					continue;
				}
				ManufactureOrderLine moLine = em.find(ManufactureOrderLine.class, moBom.getMoLineRrn());
				if (Documentation.STATUS_CLOSED.equals(moLine.getLineStatus())) {
					continue;
				}
				moLine.setLineStatus(Documentation.STATUS_CLOSED);
				em.merge(moLine);
				
				BigDecimal qtyReceive = moLine.getQtyReceive() == null ? BigDecimal.ZERO : moLine.getQtyReceive();
				BigDecimal qty = moLine.getQty();
				//�ı��ѷ���������
				List<ManufactureOrderBom> childBoms = getChildMoBom(moBoms, moBom);
				for (ManufactureOrderBom childBom : childBoms) {
					Material material = em.find(Material.class, childBom.getMaterialRrn());	
//					if (!material.getIsMrp() || !material.getIsLotControl()) {
//						continue;
//					}
					if (!material.getIsLotControl()) {
						continue;
					}
					//����MO������м����ϣ�û�м����ѷ��䣬����۳�
//					if (!material.getIsProduct()) {
//						BigDecimal qtyAllocation = material.getQtyAllocation() == null ? BigDecimal.ZERO : material.getQtyAllocation();
//						material.setQtyAllocation(qtyAllocation.subtract(qty.subtract(qtyReceive).multiply(childBom.getUnitQty())));
//					} else if (material.getIsIssueMo()) {
//						BigDecimal qtyAllocation = material.getQtyAllocation() == null ? BigDecimal.ZERO : material.getQtyAllocation();
//						material.setQtyAllocation(qtyAllocation.subtract(qty.subtract(qtyReceive).multiply(childBom.getUnitQty())));
//					}
					
					//ȡ���������ϱ����ѷ�����
//					BigDecimal qtyAllocation = material.getQtyAllocation() == null ? BigDecimal.ZERO : material.getQtyAllocation();
//					material.setQtyAllocation(qtyAllocation.subtract(qty.subtract(qtyReceive).multiply(childBom.getUnitQty())));
//
//					em.merge(material);
				}
			}
			mo.setDocStatus(Documentation.STATUS_CLOSED);
			mo.setUpdated(new Date());
			mo.setUpdatedBy(userRrn);
			em.merge(mo);
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (ClientParameterException e){
			throw e;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//�ֹ�������MO��ֻ������һ������Ϸ��䣬�����ǿ�棬Ҳ���������·ֲ�
	@Override
	public ManufactureOrderLine addMoLine(ManufactureOrderLine moLine, long userRrn) throws ClientException {
		try{
			moLine.setIsActive(true);
			moLine.setCreatedBy(userRrn);
			moLine.setUpdatedBy(userRrn);
			moLine.setCreated(new Date());
			moLine.setLineNo(10L);
			moLine.setLineStatus(DocumentationLine.LINESTATUS_APPROVED);
			em.persist(moLine);
			moLine.setMasterMoRrn(moLine.getObjectRrn());
			em.merge(moLine);
			
			ManufactureOrderBom parentBom = new ManufactureOrderBom();
			parentBom.setMaterialParentRrn(null);
			parentBom.setMaterialRrn(moLine.getMaterialRrn());
			parentBom.setIsActive(true);
			parentBom.setPath(null);
			parentBom.setPathLevel(0L);
			parentBom.setLineNo(10L);
			parentBom.setUnitQty(BigDecimal.ONE);
			parentBom.setQtyBom(BigDecimal.ONE);
			parentBom.setOrgRrn(moLine.getOrgRrn());
			parentBom.setQtyNeed(moLine.getQty());
			parentBom.setQty(moLine.getQty());
			parentBom.setUomId(moLine.getUomId());
			parentBom.setMoLineRrn(moLine.getObjectRrn());
			parentBom.setDateStart(moLine.getDateStart());
			parentBom.setDateEnd(moLine.getDateEnd());
			parentBom.setWorkCenterRrn(moLine.getWorkCenterRrn());
			parentBom.setMoRrn(moLine.getObjectRrn());
			em.persist(parentBom);
			
			List<ManufactureOrderBom> moBoms = getMoChildrenBom(moLine.getOrgRrn(), moLine.getMaterialRrn());
			for (ManufactureOrderBom moBom : moBoms) {
				moBom.setQtyNeed(moLine.getQty().multiply(moBom.getUnitQty()));
				moBom.setQty(BigDecimal.ZERO);
				moBom.setPath(moLine.getMaterialRrn() + "/");
				moBom.setMoRrn(moLine.getObjectRrn());
				em.persist(moBom);
				
				Material material = em.find(Material.class, moBom.getMaterialRrn());	
//				if (!material.getIsMrp() || !material.getIsLotControl()) {
//					continue;
//				}
				if (!material.getIsLotControl()) {
					continue;
				}
//				//����MO������м����ϣ��������ѷ���
//				if (!material.getIsProduct()) {
//					BigDecimal qtyAllocation = material.getQtyAllocation() == null ? new BigDecimal("0") : material.getQtyAllocation();
//					material.setQtyAllocation(qtyAllocation.add(moBom.getQtyNeed()));
//				} else if (material.getIsIssueMo()) {
//					BigDecimal qtyAllocation = material.getQtyAllocation() == null ? new BigDecimal("0") : material.getQtyAllocation();
//					material.setQtyAllocation(qtyAllocation.add(moBom.getQtyNeed()));
//				}
				//ȡ���������ϱ����ѷ�����
//				BigDecimal qtyAllocation = material.getQtyAllocation() == null ? new BigDecimal("0") : material.getQtyAllocation();
//				material.setQtyAllocation(qtyAllocation.add(moBom.getQtyNeed()));

				em.merge(material);
			}
			return moLine;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (ClientParameterException e){
			throw e;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	@Override
	public void closeMoLine(ManufactureOrderLine moLine, long userRrn) throws ClientException {
		try {
			if (ManufactureOrderLine.WORK_STATUS_RUNNING.equals(moLine.getWorkStatus())) {
				throw new ClientParameterException("wip.can_not_close_at_running", moLine.getMaterialId());
			}
			moLine.setLineStatus(Documentation.STATUS_CLOSED);
			moLine.setWorkStatus(ManufactureOrderLine.WORK_STATUS_CLOSED);
			moLine.setUpdated(new Date());
			moLine.setUpdatedBy(userRrn);
			moLine = em.merge(moLine);
			
			long transSeq = basManager.getHisSequence();
			CloseMoLineHis his = new CloseMoLineHis(moLine);
			his.setHisSeq(transSeq);
			his.setUpdatedBy(userRrn);
			em.persist(his);
			
			BigDecimal qtyReceive = moLine.getQtyReceive() == null ? BigDecimal.ZERO : moLine.getQtyReceive();
			List<ManufactureOrderBom> moBoms = getMoLineBom(moLine.getObjectRrn()); 
			for (ManufactureOrderBom moBom : moBoms) {
				Material material = em.find(Material.class, moBom.getMaterialRrn());
				if (!material.getIsLotControl()) {
					continue;
				}
				//ȡ���ѷ��䣬����MO������м����ϣ�û�м����ѷ��䣬����۳�
//				if (!material.getIsProduct()) {
//					BigDecimal qtyAllocation = material.getQtyAllocation() == null ? BigDecimal.ZERO : material.getQtyAllocation();
//					material.setQtyAllocation(qtyAllocation.subtract(moLine.getQty().subtract(qtyReceive).multiply(moBom.getUnitQty())));
//				} else if (material.getIsIssueMo()) {
//					BigDecimal qtyAllocation = material.getQtyAllocation() == null ? BigDecimal.ZERO : material.getQtyAllocation();
//					material.setQtyAllocation(qtyAllocation.subtract(moLine.getQty().subtract(qtyReceive).multiply(moBom.getUnitQty())));
//				}
				//ȡ���������ϱ����ѷ�����
//				BigDecimal qtyAllocation = material.getQtyAllocation() == null ? BigDecimal.ZERO : material.getQtyAllocation();
//				material.setQtyAllocation(qtyAllocation.subtract(moLine.getQty().subtract(qtyReceive).multiply(moBom.getUnitQty())));//�Ѿ����ܵĲ����ڽ���ʱ���Ѿ����ѷ������п۳��ˣ���������Ҫ�۳����ǳ��Ѿ����ܵ�֮���ʣ�ಿ��

				em.merge(material);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (ClientParameterException e){
			throw e;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	@Override
	public ManufactureOrderBom alternateMoBom(ManufactureOrder mo, ManufactureOrderBom moBom, ManufactureOrderBom alternateMoBom, long userRrn) throws ClientException {
		if (!Documentation.STATUS_DRAFTED.equals(mo.getDocStatus())) {
			throw new ClientException("common.can_not_alternate");
		}
		String parentPath = moBom.getPath();
		List<ManufactureOrderBom> moBoms = getMoBom(mo.getOrgRrn(), alternateMoBom.getMaterialRrn());
		for (ManufactureOrderBom currentMoBom : moBoms) {
			if (parentPath.indexOf(currentMoBom.getMaterialRrn().toString()) > -1) {
				throw new ClientException("pdm.bom_nesting");
			}
		}
		
		Material orginMaterial = em.find(Material.class, moBom.getMaterialRrn());
		Material alternateMaterial = em.find(Material.class, alternateMoBom.getMaterialRrn());
		BigDecimal parentQtyBom = moBom.getQtyBom().multiply(alternateMoBom.getUnitQty()).divide(moBom.getUnitQty());
		BigDecimal parentQtyNeed = moBom.getQtyNeed().multiply(alternateMoBom.getUnitQty()).divide(moBom.getUnitQty());
		
		if (orginMaterial.getIsLotControl()) {
			//ȡ���������ϱ����ѷ�����
//			BigDecimal qtyAllocation = orginMaterial.getQtyAllocation() == null ? BigDecimal.ZERO : orginMaterial.getQtyAllocation();
//			orginMaterial.setQtyAllocation(qtyAllocation.subtract(moBom.getQtyNeed()));
//			em.merge(orginMaterial);
		}
		
		if (orginMaterial.getIsLotControl() && orginMaterial.getIsMrp()) {
			if (orginMaterial.getIsPurchase()) {
				moBom = em.find(ManufactureOrderBom.class, moBom.getObjectRrn());
				em.remove(moBom);
				
				if (moBom.getRequsitionLineRrn() != null) {
					BigDecimal qtyNeed = moBom.getQtyNeed();
					RequisitionLine prLine = new RequisitionLine();
					prLine.setObjectRrn(moBom.getRequsitionLineRrn());
					prLine = (RequisitionLine)adManager.getEntity(prLine);
					if (Documentation.STATUS_DRAFTED.equals(prLine.getLineStatus())) {
						if (qtyNeed.compareTo(BigDecimal.ZERO) > 0) {
							if (prLine.getQtyMPS().compareTo(qtyNeed) > 0) {
								prLine.setQtyMPS(prLine.getQtyMPS().subtract(qtyNeed));
								BigDecimal oldQty = prLine.getQty();
								BigDecimal qtyTheory = getQtyTheory(prLine.getQtyMPS(), prLine.getQtyEconomicSize(), prLine.getQtyIncreaseSize());
								BigDecimal qty = qtyTheory;
								prLine.setQtyTheory(qtyTheory);
								prLine.setQty(qty);
								prLine.setLineTotal(qty.multiply(prLine.getUnitPrice()));
								if (prLine.getAdvancePayment() != null && oldQty.compareTo(BigDecimal.ZERO) == 0) {
									prLine.setAdvancePayment(prLine.getAdvancePayment().multiply(prLine.getQty().divide(oldQty)));
								}
								purManager.savePRLine(prLine, userRrn);
							} else {
								purManager.deletePRLine(prLine, userRrn);
								qtyNeed.subtract(prLine.getQtyMPS());
							}
						}
					}
				}
				em.flush();
			} else {
				if (moBom.getMoLineRrn() != null) {
					ManufactureOrderLine moLine = em.find(ManufactureOrderLine.class, moBom.getMoLineRrn());
					deleteMoLine(moLine, userRrn);
				} else {
					StringBuffer sqlDelete = new StringBuffer(" DELETE FROM ManufactureOrderBom ");
					sqlDelete.append(" WHERE moRrn = ? ");
					sqlDelete.append(" AND path LIKE ? ");
					String path = moBom.getPath() == null ? "" : moBom.getPath();
					path = path + moBom.getMaterialRrn() + "/%";
									
					Query queryDelete = em.createQuery(sqlDelete.toString());
					queryDelete.setParameter(1, moBom.getMoRrn());
					queryDelete.setParameter(2, path);
					queryDelete.executeUpdate();
					
					List<ManufactureOrderBom> childMoBoms = getMoBomAllChildBom(moBom.getObjectRrn());
					deleteMoBom(moBom.getMoRrn(), childMoBoms, userRrn);
				}
				em.flush();
			}
		}
		
		if (alternateMaterial.getIsProduct()) {
			//��ǰMoBom����Bom
			for (ManufactureOrderBom currentMoBom : moBoms) {
				if (currentMoBom.getMaterialParentRrn() == null) {
					currentMoBom.setMaterialParentRrn(moBom.getMaterialParentRrn());
					currentMoBom.setUnitQty(alternateMoBom.getUnitQty());
					currentMoBom.setQtyBom(parentQtyBom);
					currentMoBom.setQtyNeed(parentQtyNeed);
					currentMoBom.setPath(moBom.getPath());
					currentMoBom.setPathLevel(moBom.getPathLevel());
				} else {
					currentMoBom.setQtyBom(currentMoBom.getQtyBom().multiply(parentQtyBom));
					currentMoBom.setQtyNeed(BigDecimal.ZERO);
					currentMoBom.setPath(moBom.getPath() + currentMoBom.getPath());
					currentMoBom.setPathLevel(moBom.getPathLevel() + currentMoBom.getPathLevel());
				}
				currentMoBom.setCreatedBy(userRrn);
				currentMoBom.setUpdatedBy(userRrn);
				currentMoBom.setCreated(new Date());
				currentMoBom.setMoRrn(moBom.getMoRrn());
				currentMoBom.setQty(BigDecimal.ZERO);
				em.persist(currentMoBom);
				if(currentMoBom.getMaterialRrn().equals(alternateMoBom.getMaterialRrn()))
					alternateMoBom = currentMoBom;
			}
		} else if(alternateMaterial.getIsPurchase()) {
			//ֻ����ǰMoBom
			alternateMoBom.setOrgRrn(moBom.getOrgRrn());
			alternateMoBom.setIsActive(true);
			alternateMoBom.setCreatedBy(userRrn);
			alternateMoBom.setUpdatedBy(userRrn);
			alternateMoBom.setCreated(new Date());
			alternateMoBom.setLineNo(moBom.getLineNo());
			alternateMoBom.setMaterialParentRrn(moBom.getMaterialParentRrn());
			alternateMoBom.setMoRrn(moBom.getMoRrn());
			alternateMoBom.setPath(moBom.getPath());
			alternateMoBom.setPathLevel(moBom.getPathLevel());
			alternateMoBom.setUomId(alternateMaterial.getInventoryUom());
			alternateMoBom.setQty(BigDecimal.ZERO);
			alternateMoBom.setQtyBom(parentQtyBom);
			alternateMoBom.setQtyNeed(parentQtyNeed);
			em.persist(alternateMoBom);
		}
		
		if (alternateMaterial.getIsLotControl()) {
			//ȡ���������ϱ����ѷ�����
//			BigDecimal qtyAllocation = alternateMaterial.getQtyAllocation() == null ? BigDecimal.ZERO : alternateMaterial.getQtyAllocation();
//			alternateMaterial.setQtyAllocation(qtyAllocation.add(alternateMoBom.getQtyNeed()));
//			em.merge(alternateMaterial);
		}
		
		return alternateMoBom;
	}
	
	//Ϊ���ǰ��ɾ����Ӧ��MoBOM��PR
	public void deleteMoLine(ManufactureOrderLine moLine, long userRrn) throws ClientException {
		try{
			if (!Documentation.STATUS_DRAFTED.equals(moLine.getLineStatus())) {
				throw new ClientException("common.can_not_delete");
			}
			
			//Delete ManufactureOrderLine
			StringBuffer sqlDelete = new StringBuffer(" DELETE FROM ManufactureOrderLine ");
			sqlDelete.append(" WHERE masterMoRrn = ? ");
			sqlDelete.append(" AND path LIKE ? ");
			String path = moLine.getPath() == null ? "" : moLine.getPath();
			path = path + moLine.getMaterialRrn() + "/%";
			
			Query queryDelete = em.createQuery(sqlDelete.toString());
			queryDelete.setParameter(1, moLine.getMasterMoRrn());
			queryDelete.setParameter(2, path);
			queryDelete.executeUpdate();
			
			em.remove(moLine);
			
			List<ManufactureOrderBom> moBoms = getMoLineAllChildBom(moLine.getObjectRrn()); 
			deleteMoBom(moLine.getMasterMoRrn(), moBoms, userRrn);
			
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	private void deleteMoBom(Long moRrn, List<ManufactureOrderBom> moBoms, long userRrn) throws ClientException {
		
		Requisition moPr = null;
		if (moRrn != null) {
			StringBuffer sql = new StringBuffer(" SELECT Requisition FROM Requisition Requisition WHERE moRrn = ? ");
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, moRrn);
			List<Requisition> requisitions = query.getResultList();
			if (requisitions.size() > 0) {
				moPr = requisitions.get(0);
			} else {
				ManufactureOrder mo = em.find(ManufactureOrder.class, moRrn);
				if(mo.getMpsRrn() != null) {
					sql = new StringBuffer(" SELECT Requisition FROM Requisition Requisition WHERE mpsRrn = ? ");
					query = em.createQuery(sql.toString());
					query.setParameter(1, mo.getMpsRrn());
					requisitions = query.getResultList();
					if (requisitions.size() > 0) {
						moPr = requisitions.get(0);
					}					
				}
			}
		}
		
		for (ManufactureOrderBom moBom : moBoms) {
			//�ı��ѷ���������
			List<ManufactureOrderBom> childBoms = getChildMoBom(moBoms, moBom);
			for (ManufactureOrderBom childBom : childBoms) {
				
				Material material = em.find(Material.class, childBom.getMaterialRrn());	
//				if (!material.getIsMrp() || !material.getIsLotControl()) {
//					continue;
//				}
				if (!material.getIsLotControl()) {
					continue;
				}
				//ȡ���������ϱ����ѷ�����
//				BigDecimal qtyAllocation = material.getQtyAllocation() == null ? BigDecimal.ZERO : material.getQtyAllocation();
//				material.setQtyAllocation(qtyAllocation.subtract(childBom.getQtyNeed()));
//				em.merge(material);
				
				if (!material.getIsMrp()) {
					continue;
				}
				if (moPr != null && material.getIsPurchase()) {
					List<RequisitionLine> prLines = purManager.getPrLineByMaterial(moPr.getOrgRrn(), moPr.getObjectRrn(), material.getObjectRrn());
					BigDecimal qtyNeed = childBom.getQtyNeed();
					for (RequisitionLine prLine : prLines) {
						if (Documentation.STATUS_DRAFTED.equals(prLine.getLineStatus())) {
							if (qtyNeed.compareTo(BigDecimal.ZERO) <= 0) {
								break;
							}
							if (prLine.getQtyMPS().compareTo(qtyNeed) > 0) {
								prLine.setQtyMPS(prLine.getQtyMPS().subtract(childBom.getQtyNeed()));
								BigDecimal oldQty = prLine.getQty();
								BigDecimal qtyTheory = getQtyTheory(prLine.getQtyMPS(), prLine.getQtyEconomicSize(), prLine.getQtyIncreaseSize());
								BigDecimal qty = qtyTheory;
								prLine.setQtyTheory(qtyTheory);
								prLine.setQty(qty);
								prLine.setLineTotal(qty.multiply(prLine.getUnitPrice()));
								if (prLine.getAdvancePayment() != null && oldQty.compareTo(BigDecimal.ZERO) == 0) {
									prLine.setAdvancePayment(prLine.getAdvancePayment().multiply(prLine.getQty().divide(oldQty)));
								}
								purManager.savePRLine(prLine, userRrn);
							} else {
								purManager.deletePRLine(prLine, userRrn);
								qtyNeed.subtract(prLine.getQtyMPS());
							}
						}
					}
				}
			}
			em.remove(moBom);
		}
	}
	
	//�ҵ���Ӧ��WorkCenter�����ɳ���ʱ��
	private ManufactureOrderBom generateDuration(ManufactureOrderBom moBom, MaterialSum materialSum) throws ClientException {
		//����ʱ��Ϊ(��׼��ʱ*��������)/������������
		BigDecimal standTime = materialSum.getStandTime() == null ? BigDecimal.ZERO : materialSum.getStandTime(); 
		WorkCenter wc = getWorkCenterByMaterial(moBom.getOrgRrn(), moBom.getMaterialRrn());
		if (wc == null) {
			throw new ClientParameterException("wip.not_workcenter_found", moBom.getMaterialId());
		}
		long duration;
		if (wc.getManpower() != null && wc.getManpower() > 0 ) {
			duration = moBom.getQty().multiply(standTime).longValue()/wc.getManpower();
		} else {
			duration = moBom.getQty().multiply(standTime).longValue();
		}
		moBom.setWorkCenterRrn(wc.getObjectRrn());
		moBom.setDuration(duration);
		if(moBom.getOrgRrn() == 139420L){
			BigDecimal meter = materialSum.getMeter();
			if(meter!=null && meter.compareTo(BigDecimal.ZERO) > 0 ){
				moBom.setDuration(moBom.getQty().multiply(meter).longValue());
			}
		}
		moBom.setWeekType(wc.getWeekType());
		return moBom;
	}
	
	//������Ҫ����������
	private ManufactureOrderBom generateQtyProduct(BusinessCalendar prCalendar, ManufactureOrder mo,
			ManufactureOrderBom bom, List<ManufactureOrderBom> completeBoms, boolean batchFlag) throws ClientException {
		
		BigDecimal qtyProduct = bom.getQtyNeed();
		bom.setQty(qtyProduct);
		bom.setIsProduct(false);
		bom.setIsMaterialNeed(false);
		//ֻ���ڹ�������Ҫʱ�ſ���ʵ�ʵ���������
		if (qtyProduct.compareTo(BigDecimal.ZERO) == 0) {
			return bom;
		}
		
		MaterialSum materialSum = getMaterialSum(bom.getOrgRrn(), bom.getMaterialRrn(), batchFlag, true);
		//��isMrp��isLotControl������������ 
		if (materialSum == null) {
			bom.setQty(BigDecimal.ZERO);
			return bom;
		}
		
		//�ۼ�MO�б���������ʹ�õ�����(����ͬһ�����ϣ�������BOM���ظ�ʹ��)
		//����������
		BigDecimal qtyOtherLineNeed = BigDecimal.ZERO; 
		//�����ж���(����-����)
		BigDecimal qtyOtherLineRemaind = BigDecimal.ZERO; 
		//����������(�ɹ�)
		BigDecimal qtyOtherLineProduct = BigDecimal.ZERO; 
		
		for (ManufactureOrderBom completeBom : completeBoms) {
			if (completeBom.getMaterialRrn().equals(bom.getMaterialRrn())) {
				qtyOtherLineRemaind = qtyOtherLineRemaind.add((completeBom.getQty().subtract(completeBom.getQtyNeed())));
				qtyOtherLineNeed = qtyOtherLineNeed.add(completeBom.getQtyNeed());
				qtyOtherLineProduct = qtyOtherLineProduct.add(completeBom.getQty());
			}
		}
		
		//��������=�������+��;����(PR+PO)+MoLine����Ʒ����-SO����-�ѷ�������-��Ϳ��+�����ж��������
		BigDecimal qtyAvailable = materialSum.getQtyOnHand().add(materialSum.getQtyTransit())
			.add(materialSum.getQtyMoLineWip()).subtract(materialSum.getQtySo())
			.subtract(materialSum.getQtyAllocation()).subtract(materialSum.getQtyMin()).add(qtyOtherLineRemaind);
		//��ǰ��ʹ������=�������-SO����-�ѷ�������(��һ������)-����������ʹ�õ�����
		BigDecimal qtyCanUse = materialSum.getQtyOnHand().subtract(materialSum.getQtySo())
			.subtract(materialSum.getQtyAllocation()).subtract(qtyOtherLineNeed);
		
		if (qtyAvailable.compareTo(BigDecimal.ZERO) < 0) {
			qtyProduct = qtyAvailable.abs().add(qtyProduct);
		} else if (qtyAvailable.compareTo(qtyProduct) < 0 ) {
			qtyProduct = qtyProduct.subtract(qtyAvailable);
		} else {
			qtyProduct = BigDecimal.ZERO;
		}
		StringBuffer sf = new StringBuffer();
		sf.append(qtyOtherLineNeed);
		sf.append(";");
		sf.append(qtyOtherLineRemaind);
		sf.append(";");
		sf.append(qtyOtherLineProduct);
		sf.append(";");
		sf.append(materialSum.getQtyOnHand());
		sf.append(";");
		sf.append(materialSum.getQtyTransit());
		sf.append(";");
		sf.append(materialSum.getQtyMoLineWip());
		sf.append(";");
		sf.append(materialSum.getQtySo());
		sf.append(";");
		sf.append(materialSum.getQtyAllocation());
		sf.append(";");
		sf.append(materialSum.getQtyMin());
		sf.append(";");
		sf.append(qtyProduct);
		sf.append(";");
		bom.setDbaMark(sf.toString());
	
		bom.setQtyMin(materialSum.getQtyMin());
		//�ѷ�����=�ϴ��ѷ�����+��������+����������������
		bom.setQtyAllocation(materialSum.getQtyAllocation().add(qtyOtherLineNeed).add(bom.getQtyNeed()));
		bom.setQtyOnHand(materialSum.getQtyOnHand());
		//��;��=��;��+��������������;
		bom.setQtyTransit(materialSum.getQtyTransit().add(qtyOtherLineProduct));
		bom.setQtyMoLineWip(materialSum.getQtyMoLineWip());
		bom.setQtySo(materialSum.getQtySo());
		bom.setQtyMinProduct(materialSum.getQtyMinProduct());
		bom.setQty(qtyProduct);
		bom.setStandTime(materialSum.getStandTime());
		
		if (qtyCanUse.compareTo(bom.getQtyNeed()) >= 0) {
			bom.setIsCanStart(true);
		}
		
		if (qtyProduct.compareTo(BigDecimal.ZERO) > 0) {
			if (materialSum.getIsProduct()) {
				//����������Ҫ����
//				if (materialSum.getQtyMinProduct().compareTo(BigDecimal.ZERO) > 0
//						&& qtyProduct.compareTo(materialSum.getQtyMinProduct()) < 0) {
//					qtyProduct = materialSum.getQtyMinProduct();
//				}
				qtyProduct = getQtyTheory(qtyProduct, materialSum.getQtyMinProduct(), materialSum.getQtyMinProduct());
				bom.setQty(qtyProduct);
				bom.setIsProduct(true);
				bom.setIsMaterialNeed(false);
				bom = generateDuration(bom, materialSum);
				return bom;
			} else if (materialSum.getIsPurchase()) {
				//�ɹ�������Ҫ�ɹ�
				bom.setIsProduct(false);
				bom.setIsMaterialNeed(true);
				bom.setIqcLeadTime(materialSum.getIqcLeadTime());
//				�ɹ����ϲ�ʹ�ù�������
//				�ӵ�ǰ����һ�����տ�ʼ
//				Calendar now = GregorianCalendar.getInstance();
//				Calendar dateStart = new GregorianCalendar();
//				dateStart.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE), 0, 0, 0);
//				dateStart.add(Calendar.DATE, 1);
//				bom.setDateStart(dateStart.getTime());
//				if (materialSum.getIsJit()) {
//					//��������
//					bom.setDateEnd(dateStart.getTime());
//				} else {
//					//��ǰ����+�ɹ�����
//					VendorMaterial vendorMaterial = vdmManager.getPrimaryVendor(materialSum.getMaterialRrn());
//					if (vendorMaterial == null) {
//						throw new ClientParameterException("pur.material_no_primary_vendor", materialSum.getMaterialId());
//					}
//					
//					int leadTime = vendorMaterial.getLeadTime() == null ? 0 : vendorMaterial.getLeadTime().intValue();
//					dateStart.add(Calendar.DATE, leadTime);
//					bom.setDateEnd(dateStart.getTime());
//				}
//				//�ӵ�ǰ����һ�����տ�ʼ,�㹤����
				Date dateStart = prCalendar.findStartOfNextDay(new Date());
				bom.setDateStart(dateStart);
				if (materialSum.getIsJit()) {
					//��������
					bom.setDateEnd(dateStart);
				} else {
					//��ǰ����+�ɹ�����
					VendorMaterial vendorMaterial = vdmManager.getPrimaryVendor(materialSum.getMaterialRrn());
					if (vendorMaterial == null) {
						throw new ClientParameterException("pur.material_no_primary_vendor", materialSum.getMaterialId());
					}
					
					int leadTime = vendorMaterial.getLeadTime() == null ? 0 : vendorMaterial.getLeadTime().intValue();
					Date dateEnd = prCalendar.addDay(dateStart, leadTime);
					bom.setStandTime(new BigDecimal(leadTime));		//�ɹ�����Ҳ����standTime��
					bom.setDateEnd(dateEnd);
					bom.setVendorMaterial(vendorMaterial);
				}
				if (bom.getDateEnd().compareTo(mo.getDateEnd()) > 0) {
					bom.setIsDateNeed(true);
				}
			}
		}
		
		return bom;
	}
	
	private ManufactureOrderBom generateDateEnd(BusinessCalendar moCalendar, ManufactureOrder mo, 
			ManufactureOrderBom bom, List<ManufactureOrderBom> dateBoms) throws ClientException {
		//ȡ�ø�WorkCenter������δ��ɵ���MO
		StringBuffer sql = new StringBuffer(" SELECT ManufactureOrderLine FROM ManufactureOrderLine ManufactureOrderLine ");
		sql.append(" WHERE ");
		sql.append(" workCenterRrn = ? "); 
		sql.append(" AND (lineStatus = '" + Documentation.STATUS_DRAFTED + "' OR lineStatus = '" +  Documentation.STATUS_APPROVED + "')" );
		sql.append(" ORDER BY dateStart ");
		Query query = em.createQuery(sql.toString());
		query.setParameter(1, bom.getWorkCenterRrn());
		List<ManufactureOrderLine> moLines = query.getResultList();
		
		List<DocumentationLine> lines = new ArrayList<DocumentationLine>();
		lines.addAll(moLines);
		
		//
		List<ManufactureOrderBom> wcDateBoms = new ArrayList<ManufactureOrderBom>();
		for (ManufactureOrderBom dateBom : dateBoms) {
			if (bom.getWorkCenterRrn().equals(dateBom.getWorkCenterRrn())) {
				wcDateBoms.add(dateBom);
			}
		}
		lines.addAll(wcDateBoms);
		//���Ѿ��ڸ�WorkCenter�ϵ���MO�ͱ�MO���ڸ�WorkCenter�ϵ���MO������ʼʱ������
		Collections.sort(lines, new LineStartComparator());
		
		Date dateStart = bom.getDateStart();
		long duration = bom.getDuration();
		//���û����������������MO
		if (lines.size() == 0) {
			Date dateEnd = moCalendar.add(dateStart, new Duration(duration + " " + Duration.BUSINESS_MINUTES));
			bom.setDateEnd(dateEnd);
			return bom;
		}
		
		//Ѱ�ҿ���ʱ��
		for (DocumentationLine docLine : lines) {
			//docLineû��ʱ���򲻲����ų�
			if (docLine.getDateStart() == null || docLine.getDateEnd() == null) {
				continue;
			}
			//�����ʼʱ��С���Ѿ����ڵ���MO�Ŀ�ʼʱ��
			if (dateStart.compareTo(docLine.getDateStart()) < 0) {
				//�������ʱ��
				Date dateEnd = moCalendar.add(dateStart, new Duration(duration + " " + Duration.BUSINESS_MINUTES));
				//�������ʱ�仹С����MO�Ľ���ʱ�䣬���ʾ�ҵ�����ʱ���
				if (dateEnd.compareTo(docLine.getDateStart()) < 0) {
					bom.setDateStart(dateStart);
					bom.setDateEnd(dateEnd);
					return bom;
				} else {
					//���򽫿�ʼʱ�����¶�Ϊ��MO�Ľ���ʱ��
					dateStart = docLine.getDateEnd();
				}
			} else {
				//�����ʼʱ������Ѿ����ڵ���MO�Ŀ�ʼʱ�䣬����С����MO�Ľ���ʱ��
				//����Ҫ����ʼʱ�����¶�Ϊ��MO�Ľ���ʱ��
				if (dateStart.compareTo(docLine.getDateEnd()) < 0) {
					dateStart = docLine.getDateEnd();
				}
			}
			if (dateStart.compareTo(mo.getDateEnd()) >= 0) {
				bom.setIsDateNeed(true);
				return bom;
			}
		}
		
		//�����MO�Ƿ�����������
		if (dateStart.compareTo(mo.getDateEnd()) < 0) {
			Date dateEnd = moCalendar.add(dateStart, new Duration(duration + " " + Duration.BUSINESS_MINUTES));
			if (dateEnd.compareTo(mo.getDateEnd()) < 0) {
				bom.setDateStart(dateStart);
				bom.setDateEnd(dateEnd);
				return bom;
			}
		} 
		bom.setIsDateNeed(true);
		return bom;
	}
	
//	private ManufactureOrderLine getParentMoLine(List<ManufactureOrderLine> moLines, ManufactureOrderLine moLine) {
//		if (moLine.getPath() != null && moLine.getPath().length() > 0) {
//			String[] paths = moLine.getPath().split("/");
//			for (int i = 1; i < paths.length; i++) {
//				String parentRrn = paths[paths.length - i];
//				long parentLevel = moLine.getPathLevel() - i;
//				for (ManufactureOrderLine parentLine : moLines) {
//					if (parentLine.getMaterialRrn().equals(parentRrn) && parentLine.getPathLevel() == parentLevel) {
//						return parentLine;
//					}
//				}
//			}
//		}
//		return null;
//	}
	
	private ManufactureOrderBom getParentMoBom(List<ManufactureOrderBom> moBoms, ManufactureOrderBom moBom) {
		if (moBom.getMaterialParentRrn() == null) {
			return null;
		}
		long parentRrn = moBom.getMaterialParentRrn();
		long parentLevel = moBom.getPathLevel() - 1;
		for (ManufactureOrderBom parentBom : moBoms) {
			if (parentBom.getPath() == null || parentBom.getPath().length() == 0) {
				if (parentBom.getMaterialRrn() == parentRrn 
						&& parentBom.getPathLevel() == parentLevel) {
					return parentBom;
				}
			} else {
				String path = moBom.getPath();
				path = path.substring(0, path.length() - 1);
				path = path.substring(0, path.lastIndexOf("/") + 1);
				if (parentBom.getMaterialRrn() == parentRrn && 
						parentBom.getPath().equals(path) && 
						parentBom.getPathLevel() == parentLevel) {
					return parentBom;
				}
			}
			
		}

		return null;
	} 
	
	private ManufactureOrderBom getCurrentMoBom(List<ManufactureOrderBom> moBoms, long moBomRrn) {
		for (ManufactureOrderBom bom : moBoms) {
			if (bom.getObjectRrn() == moBomRrn) {
				return bom;
			}
		}
		return null;
	}
	
	private ManufactureOrderBom getCurrentMoBom(List<ManufactureOrderBom> moBoms, long pathLevel, long materialRrn, String path) {
		for (ManufactureOrderBom bom : moBoms) {
			if (bom.getPath() == null || bom.getPath().length() == 0) {
				if (bom.getMaterialRrn() == materialRrn &&
						bom.getPathLevel() == pathLevel) {
					return bom;
				}
			} else {
				if (bom.getMaterialRrn() == materialRrn &&
						bom.getPath().equals(path) &&
						bom.getPathLevel() == pathLevel) {
					return bom;
				}
			}
			
		}
		return null;
	} 
	
	private List<ManufactureOrderBom> getChildMoBom(List<ManufactureOrderBom> moBoms, ManufactureOrderBom moBom) {
		List<ManufactureOrderBom> childBoms = new ArrayList<ManufactureOrderBom>();
		long parentRrn = moBom.getMaterialRrn();
		long childLevel = moBom.getPathLevel() + 1;
		for (ManufactureOrderBom childBom : moBoms) {
			if (childBom.getMaterialParentRrn() != null && 
					childBom.getMaterialParentRrn() == parentRrn && 
					childBom.getPath().equals((moBom.getPath() != null ? moBom.getPath() : "") + parentRrn + "/") && 
					childBom.getPathLevel() == childLevel) {
				childBoms.add(childBom);
			}
		}
		return childBoms;
	} 
	
//	private List<ManufactureOrderLine> filterMoLine(List<ManufactureOrderLine> moLines, int level) {
//		List<ManufactureOrderLine> filterMoLines = new ArrayList<ManufactureOrderLine>();
//		for (ManufactureOrderLine moLine : moLines) {
//			if (moLine.getPathLevel() == level) {
//				filterMoLines.add(moLine);
//			}
//		}
//		return filterMoLines;
//	}
	
	private List<ManufactureOrderBom> filterMoBom(List<ManufactureOrderBom> moBoms, int level) {
		List<ManufactureOrderBom> filterMoBoms = new ArrayList<ManufactureOrderBom>();
		for (ManufactureOrderBom moBom : moBoms) {
			if (moBom.getPathLevel() == level) {
				filterMoBoms.add(moBom);
			}
		}
		return filterMoBoms;
	}
	
	public WorkCenter getWorkCenterByMaterial(long orgRrn, long materialRrn) throws ClientException {
		Material material = em.find(Material.class, materialRrn);
		Process pf = new Process();
		pf.setOrgRrn(orgRrn);
		pf.setName(material.getProcessName());
		pf = (Process)prdManager.getActiveProcessDefinition(pf);
		if (pf != null) {
			Long workCenterRrn = pf.getWorkcenterRrn();
			if (workCenterRrn != null) {
				return em.find(WorkCenter.class, workCenterRrn);
			}
		}
		throw new ClientParameterException("wip.not_workcenter_found", material.getMaterialId());
	}
	
	@Override
	public List<ManufactureOrderBom> getMoLineBom(long moLineRrn) throws ClientException {
		StringBuffer sqlLine = new StringBuffer(" SELECT ManufactureOrderBom FROM ManufactureOrderBom ManufactureOrderBom ");
		sqlLine.append(" WHERE ");
		sqlLine.append(" moLineRrn = ? "); 
		StringBuffer sqlMo = new StringBuffer(" SELECT ManufactureOrderBom FROM ManufactureOrderBom ManufactureOrderBom ");
		sqlMo.append(" WHERE ");
		sqlMo.append(" moRrn = ? AND path = ? "); 
		sqlMo.append(" ORDER BY objectRrn "); 
		try{
			Query query = em.createQuery(sqlLine.toString());
			query.setParameter(1, moLineRrn);
			List<ManufactureOrderBom> lineBoms = query.getResultList();
			if (lineBoms.size() > 0) {
				ManufactureOrderBom moLineBom = lineBoms.get(0);
				String path = moLineBom.getPath() == null ? "" : moLineBom.getPath();
				path = path + moLineBom.getMaterialRrn() + "/";
				query = em.createQuery(sqlMo.toString());
				query.setParameter(1, moLineBom.getMoRrn());
				query.setParameter(2, path);
				return query.getResultList();
			}
			return lineBoms;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	@Override
	public List<ManufactureOrderBom> getMoLineAllChildBom(long moLineRrn) throws ClientException {
		StringBuffer sqlLine = new StringBuffer(" SELECT ManufactureOrderBom FROM ManufactureOrderBom ManufactureOrderBom ");
		sqlLine.append(" WHERE ");
		sqlLine.append(" moLineRrn = ? "); 
		StringBuffer sqlMo = new StringBuffer(" SELECT ManufactureOrderBom FROM ManufactureOrderBom ManufactureOrderBom ");
		sqlMo.append(" WHERE ");
		sqlMo.append(" moRrn = ? "); 
		sqlMo.append(" AND path LIKE ? ");
		sqlMo.append(" ORDER BY lineNo "); 
		List<ManufactureOrderBom> boms = new ArrayList<ManufactureOrderBom>();
		try{
			Query query = em.createQuery(sqlLine.toString());
			query.setParameter(1, moLineRrn);
			List<ManufactureOrderBom> lineBoms = query.getResultList();
			if (lineBoms.size() > 0) {
				ManufactureOrderBom moLineBom = lineBoms.get(0);
				//��������
				boms.add(moLineBom);
				
				String path = moLineBom.getPath() == null ? "" : moLineBom.getPath();
				path = path + moLineBom.getMaterialRrn() + "/%";
				query = em.createQuery(sqlMo.toString());
				query.setParameter(1, moLineBom.getMoRrn());
				query.setParameter(2, path);
				boms.addAll(query.getResultList());
				return boms;
			}
			return null;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	@Override
	public List<ManufactureOrderBom> getMoBomAllChildBom(long moBomRrn) throws ClientException {
		StringBuffer sqlLine = new StringBuffer(" SELECT ManufactureOrderBom FROM ManufactureOrderBom ManufactureOrderBom ");
		sqlLine.append(" WHERE ");
		sqlLine.append(" objectRrn = ? "); 
		StringBuffer sqlMo = new StringBuffer(" SELECT ManufactureOrderBom FROM ManufactureOrderBom ManufactureOrderBom ");
		sqlMo.append(" WHERE ");
		sqlMo.append(" moRrn = ? "); 
		sqlMo.append(" AND path LIKE ? ");
		sqlMo.append(" ORDER BY lineNo "); 
		List<ManufactureOrderBom> boms = new ArrayList<ManufactureOrderBom>();
		try{
			Query query = em.createQuery(sqlLine.toString());
			query.setParameter(1, moBomRrn);
			List<ManufactureOrderBom> lineBoms = query.getResultList();
			if (lineBoms.size() > 0) {
				ManufactureOrderBom moLineBom = lineBoms.get(0);
				//��������
				boms.add(moLineBom);
				
				String path = moLineBom.getPath() == null ? "" : moLineBom.getPath();
				path = path + moLineBom.getMaterialRrn() + "/%";
				query = em.createQuery(sqlMo.toString());
				query.setParameter(1, moLineBom.getMoRrn());
				query.setParameter(2, path);
				boms.addAll(query.getResultList());
				return boms;
			}
			return null;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	@Override
	public List<ManufactureOrderBom> getLotBom(Lot lot) throws ClientException {
		LinkedHashMap<Long, ManufactureOrderBom> lotBomMap = new LinkedHashMap<Long, ManufactureOrderBom>(); 
		List<ManufactureOrderBom> lotBoms = new ArrayList<ManufactureOrderBom>(); 
		
		try {
			Material parentMaterial = em.find(Material.class, lot.getMaterialRrn());
			if (!parentMaterial.getIsLotControl()) {
				throw new ClientException("wip.material_must_lot_control");
			}
			String parentLotType = parentMaterial.getLotType();
			BigDecimal qtyLot = lot.getQtyTransaction();
			List<ManufactureOrderBom> moBoms = getMoLineBom(lot.getMoLineRrn());
			for (ManufactureOrderBom bom : moBoms) {
				Material material = em.find(Material.class, bom.getMaterialRrn());
				if (!material.getIsLotControl()) {
					continue;
				}
				ManufactureOrderBom lotBom;
				if (lotBomMap.containsKey(bom.getMaterialRrn())) {
					lotBom = lotBomMap.get(bom.getMaterialRrn());
				} else {
					lotBom = (ManufactureOrderBom)bom.clone();
					lotBom.setUnitQty(BigDecimal.ZERO);
				}
				BigDecimal qty = bom.getUnitQty().multiply(qtyLot);
//				boolean isCarton = pdmManager.isCartonMaterial(bom.getOrgRrn(), bom.getMaterialRrn());
//				if(isCarton){//�˴�����ֽ������ʱ����������ȡ��
//					qty = qty.setScale( 0, BigDecimal.ROUND_HALF_UP );
//				}
				if (InvLot.LOTTYPE_SERIAL.equals(material.getLotType())) {
					if (qty.scale() > 0) {
						qty = qty.round(new MathContext(1, RoundingMode.UP));
						if(qty.compareTo(BigDecimal.ONE) < 0 && qty.compareTo(BigDecimal.ZERO) > 0) {
							qty = BigDecimal.ONE;
						} else {
							qty = qty.setScale(0);					
						}
					}
				}
				lotBom.setUnitQty(lotBom.getUnitQty().add(qty));
				lotBom.setMaterial(material);
				lotBomMap.put(bom.getMaterialRrn(), lotBom);
			}
			for (ManufactureOrderBom bom : lotBomMap.values()) {
				lotBoms.add(bom);
			}
			return lotBoms;
			
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<ManufactureOrderLine> getMoLineByMaterial(long materialRrn) throws ClientException {
		StringBuffer sql = new StringBuffer(" SELECT ManufactureOrderLine FROM ManufactureOrderLine ManufactureOrderLine ");
		sql.append(" WHERE ");
		sql.append(" materialRrn = ? "); 
		sql.append(" AND (lineStatus = '" + Documentation.STATUS_DRAFTED + "' OR lineStatus = '" +  Documentation.STATUS_APPROVED + "')" );
		sql.append(" ORDER BY dateEnd ");
		logger.debug(sql);
		Query query = em.createQuery(sql.toString());
		query.setParameter(1, materialRrn);
		List<ManufactureOrderLine> moLineList = query.getResultList();
		return moLineList;
	}
	
	@Override
	public List<Material> getMaterialByWorkCenter(long orgRrn, long workCenterRrn) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT M.OBJECT_RRN, M.MATERIAL_ID, M.NAME, M.DESCRIPTION FROM  ");
		sql.append(" FROM  (SELECT OBJECT_RRN, MATERIAL_ID, NAME, DESCRIPTION, PROCESS_NAME FROM PDM_MATERIAL WHERE ");
		sql.append(ADBase.SQL_BASE_CONDITION);
		sql.append( " ) M, ");
		sql.append(" (SELECT W.NAME FROM WF_PROCESSDEFINITION W WHERE ");
		sql.append( " W.WORKCENTER_RRN = ? AND W.STATUS = 'Active' ) W ");
		sql.append(" WHERE M.PROCESS_NAME = W.NAME ) M ");
		List<Material> materialList = new ArrayList<Material>();
		try{
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, workCenterRrn);
			List<Object[]> result = query.getResultList();
			for (Object[] row : result) {
				Long materialRrn = Long.parseLong(String.valueOf(row[0]));
				Material material = new Material();
				material.setObjectRrn(materialRrn);
				material.setMaterialId(String.valueOf(row[1]));
				material.setName(String.valueOf(row[2]));
				material.setDescription(String.valueOf(row[3]));
				materialList.add(material);
			}
			return materialList;

		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	@Override
	public RequisitionLine generatePrLineByMin(long orgRrn, long materialRrn) throws ClientException {
		MaterialSum materialSum = getMaterialSum(orgRrn, materialRrn, false, true, true);
		if (materialSum == null) {
			return null;
		}
		
		//��������=�������+��;����(PR+PO)+MoLine����Ʒ����-SO����-�ѷ�������-��Ϳ��
		BigDecimal qtyAvailable = materialSum.getQtyOnHand().add(materialSum.getQtyTransit())
			.add(materialSum.getQtyMoLineWip()).subtract(materialSum.getQtySo())
			.subtract(materialSum.getQtyAllocation()).subtract(materialSum.getQtyMin());

		if (qtyAvailable.compareTo(BigDecimal.ZERO) < 0) {
			BigDecimal qtyProduct = qtyAvailable.abs();
			
			RequisitionLine	prLine = new RequisitionLine();
			prLine.setOrgRrn(orgRrn);
			prLine.setIsActive(true);
			prLine.setMaterialRrn(materialRrn);
			
			//���������С�����͵���������������������ʵ������
			VendorMaterial vendorMaterial = vdmManager.getPrimaryVendor(prLine.getMaterialRrn());	
			if (vendorMaterial == null) {
				Material material = em.find(Material.class, prLine.getMaterialRrn());
				throw new ClientParameterException("pur.material_no_primary_vendor", material.getMaterialId());
			}
			BigDecimal qtyTheory = getQtyTheory(qtyProduct, vendorMaterial.getLeastQuantity(), vendorMaterial.getIncreaseQuantity());
			BigDecimal qty = qtyTheory;
			
			prLine.setVendorRrn(vendorMaterial.getVendorRrn());
			prLine.setQtyEconomicSize(vendorMaterial.getLeastQuantity());
			prLine.setQtyIncreaseSize(vendorMaterial.getIncreaseQuantity());
			prLine.setQtyMPS(qtyProduct);
			prLine.setQtyTheory(qtyProduct);
			prLine.setQty(qty);
			prLine.setLineTotal(prLine.getQty().multiply(vendorMaterial.getReferencedPrice()));
			if (vendorMaterial.getAdvanceRatio() != null && vendorMaterial.getAdvanceRatio().compareTo(BigDecimal.ZERO) > 0) {
				prLine.setAdvancePayment(prLine.getLineTotal().multiply(vendorMaterial.getAdvanceRatio()).divide(new BigDecimal(100)));
			}
			
			prLine.setMaterial(materialSum.getMaterial());
			prLine.setUomId(materialSum.getMaterial().getInventoryUom());
			
			prLine.setQtyHandOn(materialSum.getQtyOnHand());
			prLine.setQtyTransit(materialSum.getQtyTransit());
			prLine.setQtyAllocation(materialSum.getQtyAllocation());
			prLine.setQtyMin(materialSum.getQtyMin());
			prLine.setQtyNeed(qtyProduct);
			
			Warehouse warehouse = invManager.getDefaultWarehouse(orgRrn);
			prLine.setWarehouseRrn(warehouse.getObjectRrn());
			prLine.setLineStatus(Documentation.STATUS_DRAFTED);
			return prLine;
		}
		return null;
	}
	
	@Override
	public MaterialSum getMaterialSum(long orgRrn, long materialRrn, boolean batchFlag, boolean soFlag) throws ClientException {
		return getMaterialSum(orgRrn, materialRrn, batchFlag, soFlag, false);
	}
	
	public MaterialSum getMaterialSum(long orgRrn, long materialRrn, boolean batchFlag, boolean soFlag, boolean calcAllFlag) throws ClientException {
		MaterialSum materialSum = new MaterialSum();
		
		//denny 2013.3.28��,������sqlд��һ��sp��ִ��,Ч�ʸ���
//		//ͳ�ƿ�桢�����桢������(ֻͳ�Ʋ���MRP����Ĳֿ�)
//		StringBuffer sqlOnHand = new StringBuffer();
//		sqlOnHand.append(" SELECT NVL(SUM(QTY_ONHAND)+SUM(QTY_DIFF), 0) QTY_ONHAND, NVL(SUM(QTY_WRITE_OFF), 0) QTY_WRITE_OFF, NVL(SUM(QTY_DIFF), 0) QTY_DIFF ");
//		sqlOnHand.append(" FROM INV_STORAGE S, INV_WAREHOUSE W ");
//		sqlOnHand.append(" WHERE S.WAREHOUSE_RRN = W.OBJECT_RRN ");
//		sqlOnHand.append(" AND W.IS_MRP = 'Y' ");
//		sqlOnHand.append(" AND S.ORG_RRN = ? ");
//		sqlOnHand.append(" AND S.MATERIAL_RRN = ? ");
//		//ͳ��PR��;
//		StringBuffer sqlTranistPr = new StringBuffer();
//		sqlTranistPr.append(" SELECT NVL(SUM(QTY), 0) - NVL(SUM(QTY_ORDERED), 0) FROM PUR_REQUISITION_LINE ");
//		sqlTranistPr.append(" WHERE ORG_RRN = ? ");
//		sqlTranistPr.append(" AND MATERIAL_RRN = ? ");
//		sqlTranistPr.append(" AND LINE_STATUS IN ('DRAFTED', 'APPROVED') ");
//		//ͳ��PO��;
//		StringBuffer sqlTranistPo = new StringBuffer();
//		sqlTranistPo.append(" SELECT NVL(SUM(QTY), 0) - NVL(SUM(QTY_IN), 0) FROM PUR_PO_LINE ");
//		sqlTranistPo.append(" WHERE ORG_RRN = ? ");
//		sqlTranistPo.append(" AND MATERIAL_RRN = ? ");
//		sqlTranistPo.append(" AND LINE_STATUS IN ('DRAFTED', 'APPROVED') ");
//
//		//ͳ����MO����Ʒ
//		StringBuffer sqlMoLineWip = new StringBuffer();
//		sqlMoLineWip.append(" SELECT NVL(SUM(QTY), 0) QTY, NVL(SUM(QTY_RECEIVE), 0) QTY_RECEIVE FROM WIP_MO_LINE ");
//		sqlMoLineWip.append(" WHERE ORG_RRN = ? ");
//		sqlMoLineWip.append(" AND MATERIAL_RRN = ? ");
//		sqlMoLineWip.append(" AND (LINE_STATUS = 'DRAFTED' OR LINE_STATUS = 'APPROVED') ");
//		
//		//ͳ���ѷ���
//		StringBuffer sqlAllocation = new StringBuffer();
////		sqlAllocation.append(" SELECT NVL(SUM(M.QTY * B.QTY_UNIT), 0) SQTY FROM ");
////		sqlAllocation.append(" WIP_MO_BOM B, ");
////		sqlAllocation.append(" (SELECT M.MO_RRN, M.MATERIAL_RRN, M.PATH, T.QTY from WIP_MO_BOM M, ");
////		sqlAllocation.append(" (SELECT OBJECT_RRN, QTY - QTY_RECEIVE QTY FROM WIP_MO_LINE ");
////		sqlAllocation.append(" WHERE ORG_RRN = ? ");
////		sqlAllocation.append(" AND (LINE_STATUS = 'DRAFTED' OR LINE_STATUS = 'APPROVED')) T ");
////		sqlAllocation.append(" WHERE M.MO_LINE_RRN = T.OBJECT_RRN ");
////		sqlAllocation.append(" AND T.QTY <> 0) M ");
////		sqlAllocation.append(" WHERE B.MO_RRN = M.MO_RRN ");
////		sqlAllocation.append("   AND B.MATERIAL_PARENT_RRN = M.MATERIAL_RRN ");
////		sqlAllocation.append("   AND B.PATH = M.PATH || M.MATERIAL_RRN || '/' ");
////		sqlAllocation.append("   AND B.MATERIAL_RRN = ? ");
//		//denny 20120509�޸��ѷ������ļ��㷽��
//		sqlAllocation.append(" SELECT NVL(SUM(CASE WHEN WM.QTY_RECEIVE IS NULL THEN 0 ELSE SQTY END),0) AS SQTY");
//		sqlAllocation.append("  FROM (SELECT C.MO_RRN, B.MATERIAL_RRN, SUM(C.QTY * B.QTY_UNIT) SQTY");
//		sqlAllocation.append("          FROM WIP_MO_BOM B");
//		sqlAllocation.append("         INNER JOIN (SELECT M.MO_RRN, M.MATERIAL_RRN, M.PATH, T.QTY");
//		sqlAllocation.append("                      FROM WIP_MO_BOM M");
//		sqlAllocation.append("                     INNER JOIN (SELECT T.OBJECT_RRN,");
//		sqlAllocation.append("                                       (T.QTY - T.QTY_RECEIVE) QTY");
//		sqlAllocation.append("                                  FROM WIP_MO_LINE T");
//		sqlAllocation.append("                                 WHERE T.LINE_STATUS IN");
//		sqlAllocation.append("                                       ('DRAFTED', 'APPROVED')) T");
//		sqlAllocation.append("                        ON M.MO_LINE_RRN = T.OBJECT_RRN");
//		sqlAllocation.append("                       AND T.QTY <> 0) C");
//		sqlAllocation.append("            ON B.MO_RRN = C.MO_RRN");
//		sqlAllocation.append("           AND B.MATERIAL_PARENT_RRN = C.MATERIAL_RRN");
//		sqlAllocation.append("           AND B.PATH = C.PATH || C.MATERIAL_RRN || '/'");
//		sqlAllocation.append("         GROUP BY C.MO_RRN, B.MATERIAL_RRN) WB");
//		sqlAllocation.append(" INNER JOIN WIP_MO WM");
//		sqlAllocation.append("    ON WB.MO_RRN = WM.OBJECT_RRN");
//		sqlAllocation.append(" INNER JOIN PDM_MATERIAL PM");
//		sqlAllocation.append("    ON WB.MATERIAL_RRN = PM.OBJECT_RRN");
//		sqlAllocation.append("   AND WM.ORG_RRN = ?");
//		sqlAllocation.append(" GROUP BY WB.MATERIAL_RRN");
//		sqlAllocation.append(" HAVING WB.MATERIAL_RRN = ?");
//		
//
//		
//		  
//		//ͳ��SO
//		StringBuffer sqlSo = new StringBuffer();
//		sqlSo.append(" SELECT NVL(SUM(QTY_SO), 0) QTY_SO FROM SAL_SO ");
//		sqlSo.append(" WHERE ORG_RRN = ? ");
//		sqlSo.append(" AND MATERIAL_ID = ? ");
		
		try{
			if (batchFlag) {
				String whereClause = " materialRrn = '" + materialRrn + "' ";
				List<MaterialSum> sums = adManager.getEntityList(orgRrn, MaterialSum.class, 1, whereClause, "");
				if (sums != null && sums.size() > 0) {
					materialSum = sums.get(0);
					return materialSum;
				}
			}
			Material material = em.find(Material.class, materialRrn);
			if (!calcAllFlag) {
				if (!material.getIsMrp() || !material.getIsLotControl()) {
					return null;
				}
			} else {
				if (!material.getIsLotControl()) {
					return null;
				}
			}
			
			materialSum.setMaterial(material);
			materialSum.setMaterialRrn(materialRrn);
			materialSum.setMaterialId(material.getMaterialId());
			materialSum.setMaterialName(material.getName());
			materialSum.setQtyMinProduct(material.getQtyMinProduct() == null ? BigDecimal.ZERO : material.getQtyMinProduct());
			materialSum.setQtyMin(material.getQtyMin() == null ? BigDecimal.ZERO : material.getQtyMin());
			materialSum.setIsPurchase(material.getIsPurchase());
			materialSum.setIsProduct(material.getIsProduct());
			materialSum.setIsJit(material.getIsJit());
			materialSum.setStandTime(material.getStandTime());
			materialSum.setIqcLeadTime(material.getIqcLeadTime() == null ? 0 : material.getIqcLeadTime());
			materialSum.setMeter(material.getMeter()!=null?material.getMeter():BigDecimal.ZERO);
			
//			Query query = em.createNativeQuery(sqlOnHand.toString());
//			query.setParameter(1, orgRrn);
//			query.setParameter(2, materialRrn);
//			Object[] qtyOnHands = (Object[])query.getSingleResult();
//			materialSum.setQtyOnHand((BigDecimal)qtyOnHands[0]);
//			materialSum.setQtyWriteOff((BigDecimal)qtyOnHands[1]);
//			materialSum.setQtyDiff((BigDecimal)qtyOnHands[2]);
//			
//			query = em.createNativeQuery(sqlTranistPr.toString());
//			query.setParameter(1, orgRrn);
//			query.setParameter(2, materialRrn);
//			BigDecimal qtyTranistPr = (BigDecimal)query.getSingleResult();
//			query = em.createNativeQuery(sqlTranistPo.toString());
//			query.setParameter(1, orgRrn);
//			query.setParameter(2, materialRrn);
//			BigDecimal qtyTranistPo = (BigDecimal)query.getSingleResult();
//			materialSum.setQtyTransit(qtyTranistPr.add(qtyTranistPo));
//						
//			query = em.createNativeQuery(sqlMoLineWip.toString());
//			query.setParameter(1, orgRrn);
//			query.setParameter(2, materialRrn);
//			Object[] resultMoLine = (Object[])query.getSingleResult();
//			materialSum.setQtyMoLine((BigDecimal)resultMoLine[0]);
//			materialSum.setQtyMoLineReceive((BigDecimal)resultMoLine[1]);
//			materialSum.setQtyMoLineWip(materialSum.getQtyMoLine().subtract(materialSum.getQtyMoLineReceive()));

			//�����ѷ�����
//			query = em.createNativeQuery(sqlAllocation.toString());
//			query.setParameter(1, orgRrn);
//			query.setParameter(2, materialRrn);
//			List list = query.getResultList();
//			BigDecimal qtyAllocation = BigDecimal.ZERO;
//			if(list != null && list.size() > 0){
//				qtyAllocation = (BigDecimal)list.get(0);
//			}
			
//			query = em.createNativeQuery(sqlSo.toString());
//			query.setParameter(1, orgRrn);
//			query.setParameter(2, material.getMaterialId());
//			BigDecimal qtySo = (BigDecimal)query.getSingleResult();
			
			
			
			Session session = (Session) em.getDelegate();  
	        Connection conn = session.connection();  
            CallableStatement call = conn.prepareCall("{CALL SP_GET_QTYALLOCATION(?,?,?,?,?,?,?,?,?,?,?,?)}");  
            call.setLong(1, orgRrn);  
            call.setLong(2, materialRrn);  
            call.registerOutParameter(3, Types.NUMERIC);  
            call.registerOutParameter(4, Types.NUMERIC);  
            call.registerOutParameter(5, Types.NUMERIC);  
            call.registerOutParameter(6, Types.NUMERIC);  
            call.registerOutParameter(7, Types.NUMERIC);  
            call.registerOutParameter(8, Types.NUMERIC);  
            call.registerOutParameter(9, Types.NUMERIC);  
            call.registerOutParameter(10, Types.NUMERIC);  
            call.registerOutParameter(11, Types.NUMERIC);  
            call.registerOutParameter(12, Types.NUMERIC); 
            
            call.execute();  
            
            BigDecimal qtyOnHand = call.getBigDecimal(3);
            BigDecimal qtyWriteOff = call.getBigDecimal(4);
            BigDecimal qtyDiff = call.getBigDecimal(5);
            BigDecimal qtyTransitPr = call.getBigDecimal(6);
            BigDecimal qtyTransitPo = call.getBigDecimal(7);
            BigDecimal qtyMoLine = call.getBigDecimal(8);
            BigDecimal qtyMoLineReceive = call.getBigDecimal(9);
            BigDecimal qtyMoLineWip = call.getBigDecimal(10);
            BigDecimal qtyAllocation = call.getBigDecimal(11);
            BigDecimal qtySo = call.getBigDecimal(12);
            
			materialSum.setQtyOnHand(qtyOnHand);
			materialSum.setQtyWriteOff(qtyWriteOff);
			materialSum.setQtyDiff(qtyDiff);
			materialSum.setQtyTransit(qtyTransitPr.add(qtyTransitPo));
			materialSum.setQtyMoLine(qtyMoLine);
			materialSum.setQtyMoLineReceive(qtyMoLineReceive);
			materialSum.setQtyMoLineWip(qtyMoLineWip);
			materialSum.setQtyAllocation(qtyAllocation);
			materialSum.setQtySo(qtySo);
			
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return materialSum;
	}
	
	@Override
	public List<MaterialSum> getInvMaterialByWorkCenter(long orgRrn, long workCenterRrn) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT M.OBJECT_RRN, M.PROCESS_NAME FROM ");
		sql.append(" (SELECT OBJECT_RRN, PROCESS_NAME FROM PDM_MATERIAL WHERE ");
		sql.append(ADBase.SQL_BASE_CONDITION);
		sql.append( " ) M, ");
		sql.append(" (SELECT W.NAME FROM WF_PROCESSDEFINITION W WHERE ");
		sql.append(" W.WORKCENTER_RRN = ? AND W.STATUS = 'Active' ) W ");
		sql.append(" WHERE M.PROCESS_NAME = W.NAME ");
		
		List<MaterialSum> materialList = new ArrayList<MaterialSum>();
		try{
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, workCenterRrn);
			List<Object[]> result = query.getResultList();
			for (Object[] row : result) {
				Long materialRrn = Long.parseLong(String.valueOf(row[0]));
				MaterialSum invMaterial = getMaterialSumForWorkCenter(orgRrn, materialRrn);
				materialList.add(invMaterial);
			}

			return materialList;

		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	/*
	 * �˷���δʹ��,�����޸�
	 */
	private MaterialSum getMaterialSumForWorkCenter(long orgRrn, long materialRrn) throws ClientException {
		MaterialSum materialSum = new MaterialSum();
		//ͳ�ƿ��(ֻͳ�����쳵��ֿ�)
		StringBuffer sqlOnHand = new StringBuffer();
		sqlOnHand.append(" SELECT NVL(SUM(QTY_ONHAND), 0) FROM INV_STORAGE S, INV_WAREHOUSE W ");
		sqlOnHand.append(" WHERE  W.IS_WRITE_OFF = 'Y' ");
		sqlOnHand.append(" AND S.WAREHOUSE_RRN = W.OBJECT_RRN ");
		sqlOnHand.append(" AND W.IS_MRP = 'Y' ");
		sqlOnHand.append(" AND S.ORG_RRN = ? ");
		sqlOnHand.append(" AND S.MATERIAL_RRN = ? ");
		
		//ͳ����MO����Ʒ,ֻ����APPROVED��
		StringBuffer sqlMoLineWip = new StringBuffer();
		sqlMoLineWip.append(" SELECT NVL(SUM(QTY), 0) QTY, NVL(SUM(QTY_RECEIVE), 0) QTY_RECEIVE FROM WIP_MO_LINE ");
		sqlMoLineWip.append(" WHERE ORG_RRN = ? ");
		sqlMoLineWip.append(" AND MATERIAL_RRN = ? ");
		sqlMoLineWip.append(" AND LINE_STATUS = 'APPROVED' ");

		try {
			Material material = em.find(Material.class, materialRrn);
			if (!material.getIsMrp() || !material.getIsLotControl()) {
				return null;
			}
			materialSum.setMaterialRrn(materialRrn);
			materialSum.setMaterialId(material.getMaterialId());
			materialSum.setMaterialName(material.getName());
			materialSum.setQtyAllocation(material.getQtyAllocation() == null ? BigDecimal.ZERO : material.getQtyAllocation());
			materialSum.setQtyMinProduct(material.getQtyMinProduct() == null ? BigDecimal.ZERO : material.getQtyMinProduct());
			materialSum.setQtyMin(material.getQtyMin() == null ? BigDecimal.ZERO : material.getQtyMin());
			materialSum.setIsPurchase(material.getIsPurchase());
			materialSum.setIsProduct(material.getIsProduct());
			materialSum.setIsJit(material.getIsJit());
			materialSum.setStandTime(material.getStandTime());
			
			Query query = em.createNativeQuery(sqlOnHand.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, materialRrn);
			BigDecimal qtyOnHand = (BigDecimal)query.getSingleResult();
			materialSum.setQtyOnHand(qtyOnHand);
			
			query = em.createNativeQuery(sqlMoLineWip.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, materialRrn);
			Object[] resultMoLine = (Object[])query.getSingleResult();
			materialSum.setQtyMoLine((BigDecimal)resultMoLine[0]);
			materialSum.setQtyMoLineReceive((BigDecimal)resultMoLine[1]);
			materialSum.setQtyMoLineWip(materialSum.getQtyMoLine().subtract(materialSum.getQtyMoLineReceive()));
			
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return materialSum;
	}
	
	@Override
	public List<ManufactureOrderLine> getMoLineByWorkCenter(long orgRrn, long workCenterRrn, String whereClause) throws ClientException {
		StringBuffer sql = new StringBuffer(" SELECT ManufactureOrderLine FROM ManufactureOrderLine ManufactureOrderLine ");
		sql.append(" WHERE ");
		sql.append(" workCenterRrn = ? "); 
		if (whereClause != null && !"".equals(whereClause.trim())) {
			//sql.append(" WHERE ");
			//sql.append(whereClause);
			sql.append(" AND "+ whereClause);
		} else {
			sql.append(" AND (lineStatus = '" + Documentation.STATUS_APPROVED + "')" );
		}
		sql.append(" ORDER BY dateStart ");
		logger.debug(sql);
		
		StringBuffer sql2 = new StringBuffer();
		sql2.append(" SELECT S.MATERIAL_RRN, NVL(S.QTY_ONHAND + S.QTY_DIFF, 0) QTY_ONHAND FROM INV_STORAGE S, ");
		sql2.append(" (SELECT DISTINCT MATERIAL_RRN FROM WIP_MO_LINE  ");
		sql2.append("   WHERE WORKCENTER_RRN = ?  ");
		sql2.append(" AND (LINE_STATUS = 'DRAFTED' OR LINE_STATUS = 'APPROVED')) W  ");
		sql2.append(" WHERE S.MATERIAL_RRN = W.MATERIAL_RRN  ");
		sql2.append(" AND S.WAREHOUSE_RRN = ?  ");
		logger.debug(sql2);
		
		StringBuffer sql3 = new StringBuffer();
//		sql3.append(" SELECT S.MATERIAL_RRN, NVL(SUM(SQTY), 0) SQTY FROM  ");
//		sql3.append(" (SELECT B.MATERIAL_RRN, M.QTY * B.QTY_UNIT SQTY FROM ");
//		sql3.append(" WIP_MO_BOM B, ");
//		sql3.append(" (SELECT M.MO_RRN, M.MATERIAL_RRN, M.PATH, T.QTY from WIP_MO_BOM M, ");
//		sql3.append(" (SELECT OBJECT_RRN, QTY - QTY_RECEIVE QTY FROM WIP_MO_LINE ");
//		sql3.append(" WHERE ORG_RRN = ? ");
//		sql3.append(" AND (LINE_STATUS = 'DRAFTED' OR LINE_STATUS = 'APPROVED')) T ");
//		sql3.append(" WHERE M.MO_LINE_RRN = T.OBJECT_RRN ");
//		sql3.append("  AND T.QTY <> 0) M ");
//		sql3.append(" WHERE B.MO_RRN = M.MO_RRN ");
//		sql3.append(" AND B.MATERIAL_PARENT_RRN = M.MATERIAL_RRN ");
//		sql3.append("  AND B.PATH = M.PATH || M.MATERIAL_RRN || '/') S, ");
//		sql3.append(" (SELECT DISTINCT MATERIAL_RRN FROM WIP_MO_LINE ");
//		sql3.append(" WHERE WORKCENTER_RRN = ? ");
//		sql3.append(" AND (LINE_STATUS = 'DRAFTED' OR LINE_STATUS = 'APPROVED')) W ");
//		sql3.append(" WHERE S.MATERIAL_RRN = W.MATERIAL_RRN ");
//		sql3.append(" GROUP BY S.MATERIAL_RRN ");
		/*20130608��ע�͵���ԭ�򣺴��ܳ�����������Ӧ���ѷ�������Ӧ�ú� ������ʹ�����Ϲ��ܵĵ�λ����*��������������һ��
		sql3.append(" SELECT WB.MATERIAL_RRN, SUM(CASE WHEN WM.QTY_RECEIVE IS NULL THEN 0 ELSE SQTY END) AS SQTY");
		sql3.append("  FROM (SELECT C.MO_RRN, B.MATERIAL_RRN, SUM(C.QTY * B.QTY_UNIT) SQTY");
		sql3.append("          FROM WIP_MO_BOM B");
		sql3.append("         INNER JOIN (SELECT M.MO_RRN, M.MATERIAL_RRN, M.PATH, T.QTY");
		sql3.append("                     FROM WIP_MO_BOM M");
		sql3.append("                     INNER JOIN (SELECT T.OBJECT_RRN,");
		sql3.append("                                       (T.QTY - T.QTY_RECEIVE) QTY");
		sql3.append("                                  FROM WIP_MO_LINE T");
		sql3.append("                                 WHERE T.LINE_STATUS IN ('DRAFTED', 'APPROVED')) T");
		sql3.append("                        ON M.MO_LINE_RRN = T.OBJECT_RRN");
		sql3.append("                       AND T.QTY <> 0) C");
		sql3.append("            ON B.MO_RRN = C.MO_RRN");
		sql3.append("           AND B.MATERIAL_PARENT_RRN = C.MATERIAL_RRN");
		sql3.append("           AND B.PATH = C.PATH || C.MATERIAL_RRN || '/'");
		sql3.append("         GROUP BY C.MO_RRN, B.MATERIAL_RRN) WB");
		sql3.append(" INNER JOIN WIP_MO WM");
		sql3.append("    ON WB.MO_RRN = WM.OBJECT_RRN");
		sql3.append("	 AND WM.ORG_RRN = ?");
		sql3.append(" INNER JOIN (SELECT DISTINCT MATERIAL_RRN FROM WIP_MO_LINE ");
		sql3.append(" 			  WHERE WORKCENTER_RRN = ? ");
		sql3.append(" 				AND (LINE_STATUS = 'DRAFTED' OR LINE_STATUS = 'APPROVED')) W ");
		sql3.append("    ON WB.MATERIAL_RRN = W.MATERIAL_RRN");
		sql3.append(" GROUP BY WB.MATERIAL_RRN");
		*/
		sql3.append(" SELECT MO.* FROM (SELECT  ");
		sql3.append("  B.MATERIAL_RRN,  ");
		sql3.append("  SUM(B.QTY_UNIT * (M.QTY_PRODUCT - M.QTY_RECEIVE)) QTY_MO_ALLOCATION ");
		sql3.append("  FROM WIP_MO_BOM B, WIP_MO M  ");
		sql3.append("  WHERE  1=1 ");
		sql3.append("  AND B.ORG_RRN = ? ");
		sql3.append("  AND B.MO_RRN = M.OBJECT_RRN  ");
		sql3.append("  AND M.DOC_STATUS IN ('APPROVED', 'DRAFTED') ");
		sql3.append("  GROUP BY B.MATERIAL_RRN ");
		sql3.append(") MO ");
		sql3.append("INNER JOIN (SELECT DISTINCT MATERIAL_RRN FROM WIP_MO_LINE  ");
		sql3.append("  WHERE WORKCENTER_RRN = ? ");
		sql3.append("  AND (LINE_STATUS = 'DRAFTED' OR LINE_STATUS = 'APPROVED')) W  ");
		sql3.append("ON MO.MATERIAL_RRN = W.MATERIAL_RRN ");		
		logger.debug(sql3);
		
		try{
			Warehouse writeOffWarehouse = invManager.getWriteOffWarehouse(orgRrn);
			Map<Long, BigDecimal> qtyOnhandMap = new HashMap<Long, BigDecimal>();
			Map<Long, BigDecimal> qtyAllocationMap = new HashMap<Long, BigDecimal>();
			
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, workCenterRrn);
			List<ManufactureOrderLine> moLineList = query.getResultList();
			
			query = em.createNativeQuery(sql2.toString());
			query.setParameter(1, workCenterRrn);
			query.setParameter(2, writeOffWarehouse.getObjectRrn());
			List<Object[]> result = query.getResultList();
			for (Object[] row : result) {
				qtyOnhandMap.put(Long.parseLong(String.valueOf(row[0])), (BigDecimal)row[1]);
			}
			
			query = em.createNativeQuery(sql3.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, workCenterRrn);
			result = query.getResultList();
			for (Object[] row : result) {
				qtyAllocationMap.put(Long.parseLong(String.valueOf(row[0])), (BigDecimal)row[1]);
			}
			
			for(ManufactureOrderLine moLine : moLineList){
				if (qtyOnhandMap.containsKey(moLine.getMaterialRrn())) {
					moLine.setQtyCurrentOnHand(qtyOnhandMap.get(moLine.getMaterialRrn()));
				} else {
					moLine.setQtyCurrentOnHand(BigDecimal.ZERO);
				}
				if (qtyAllocationMap.containsKey(moLine.getMaterialRrn())) {
					moLine.setQtyCurrentAllocation(qtyAllocationMap.get(moLine.getMaterialRrn()));
				} else {
					moLine.setQtyCurrentAllocation(BigDecimal.ZERO);
				}
			}
			return moLineList;
			
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
//	public List<ManufactureOrderLine> getMoLineByWorkCenter(long orgRrn,String whereClause) throws ClientException {
//		StringBuffer sql = new StringBuffer(" SELECT ManufactureOrderLine FROM ManufactureOrderLine ManufactureOrderLine ");
//		if(whereClause != null && !"".equals(whereClause.trim())) {
//			sql.append(" WHERE ");
//			sql.append(whereClause);
//		} else {
//			throw new ClientException("Where Clause is null");
//		}
//		sql.append(" ORDER BY dateStart ");
//		logger.debug(sql);	
//		
//		StringBuffer sql2 = new StringBuffer();
//		sql2.append("SELECT QTY_ONHAND FROM INV_STORAGE ");
//		sql2.append(" WHERE MATERIAL_RRN = ? AND WAREHOUSE_RRN = ? ");
//		logger.debug(sql2);
//		
//		StringBuffer sql3 = new StringBuffer();
//		sql3.append("SELECT QTY_ALLOCATION FROM PDM_MATERIAL ");
//		sql3.append(" WHERE OBJECT_RRN = ? ");
//		logger.debug(sql3);
//		
//		try{
//			Query query = em.createQuery(sql.toString());
//			List<ManufactureOrderLine> moLineList = query.getResultList();
//			Warehouse writeOffWarehouse = invManager.getWriteOffWarehouse(orgRrn);
//			for(ManufactureOrderLine mol : moLineList){
//				Query query2 = em.createNativeQuery(sql2.toString());
//				query2.setParameter(1, mol.getMaterialRrn());
//				query2.setParameter(2, writeOffWarehouse.getObjectRrn());
//				List qtyOnHands = query2.getResultList();
//				
//				Query query3 = em.createNativeQuery(sql3.toString());
//				query3.setParameter(1, mol.getMaterialRrn());
//				List qtyAllocations = query3.getResultList();
//				
//				if(qtyOnHands != null && qtyOnHands.size() > 0){
//					BigDecimal qtyOnHand = (BigDecimal)qtyOnHands.get(0);
//					mol.setQtyOnHand(qtyOnHand);
//				}else{
//					mol.setQtyOnHand(BigDecimal.ZERO);
//				}
//				
//				if(qtyAllocations != null && qtyAllocations.size() > 0){
//					BigDecimal qtyAllocation = (BigDecimal)qtyAllocations.get(0);
//					mol.setQtyAllocation(qtyAllocation);
//				}else{
//					mol.setQtyAllocation(BigDecimal.ZERO);
//				}
//			}
//			return moLineList;			
//		} catch (Exception e){ 
//			logger.error(e.getMessage(), e);
//			throw new ClientException(e);
//		}
//	}
	
	@Override
	public ManufactureOrderLine runMoLine(ManufactureOrderLine moLine, BigDecimal manpower) throws ClientException {
		try{
			/*List<ManufactureOrderLine> moLines = getMoLineByWorkCenter(moLine.getWorkCenterRrn());
			for (ManufactureOrderLine line : moLines) {
				if (ManufactureOrderLine.WORK_STATUS_RUNNING.equals(line.getWorkStatus())) {
					throw new ClientException("wip.moline_onlyone_run");
				}
			}*/
			moLine.setWorkStatus(ManufactureOrderLine.WORK_STATUS_RUNNING);
			if(moLine.getDateStartActual() == null) {
				moLine.setDateStartActual(new Date());
			}
			moLine = em.merge(moLine);			
			
			long transSeq = basManager.getHisSequence();
			RunMoLineHis his = new RunMoLineHis(moLine);
			his.setHisSeq(transSeq);
			his.setManpower(manpower);
			em.persist(his);
			
			return moLine;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	@Override
	public ManufactureOrderLine suspendMoLine(ManufactureOrderLine moLine) throws ClientException {
		try{
			moLine.setWorkStatus(ManufactureOrderLine.WORK_STATUS_SUSPENED);
			moLine = em.merge(moLine);
			
			long transSeq = basManager.getHisSequence();
			SuspendMoLineHis his = new SuspendMoLineHis(moLine);
			his.setHisSeq(transSeq);
			em.persist(his);

			return moLine;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//��������
	@Override
	public void receiveMultiSerialLot(WorkCenter wc, Lot parentLot, List<String> lotIds, int qty, List<Lot> childLots, long userRrn) throws ClientException {
		try {
			//��� ��ʱ���������
			lotManager.deleteLotTemp(parentLot.getMoLineRrn(), parentLot.getOrgRrn());
			Material parentMaterial = em.find(Material.class, parentLot.getMaterialRrn());
			if (!InvLot.LOTTYPE_SERIAL.equals(parentMaterial.getLotType())) {
				throw new ClientParameterException("wip.lot_must_be_serial");
			}
			BigDecimal qtyLot = new BigDecimal(qty);
			Warehouse writeOffWarehouse = invManager.getWriteOffWarehouse(parentLot.getOrgRrn());
			Date now = new Date();
			
			invManager.updateStorage(parentMaterial.getOrgRrn(), parentLot.getMaterialRrn(), writeOffWarehouse.getObjectRrn(), qtyLot, false, userRrn);
			parentLot.setIsActive(true);
			parentLot.setCreatedBy(userRrn);
			parentLot.setUpdatedBy(userRrn);
			parentLot.setCreated(now);
			parentLot.setPosition(InvLot.POSITION_WIP);
			parentLot.setIsUsed(false);
			parentLot.setDateProduct(now);
			parentLot.setWorkCenterRrn(wc.getObjectRrn());
			parentLot.setWorkCenterId(wc.getName());
			parentLot.setWarehouseRrn(writeOffWarehouse.getObjectRrn());
			parentLot.setWarehouseId(writeOffWarehouse.getWarehouseId());
			parentLot.setQtyInitial(BigDecimal.ONE);
			parentLot.setQtyCurrent(BigDecimal.ONE);
			
			//�޸�MOLine��������
			ManufactureOrderLine moLine = em.find(ManufactureOrderLine.class, parentLot.getMoLineRrn());
			if (moLine.getQty().compareTo(moLine.getQtyReceive().add(qtyLot)) < 0) {
				throw new ClientException("wip.receive_lot_larger_than_qty");
			}
			if (!Documentation.STATUS_APPROVED.equals(moLine.getLineStatus())) {
				throw new ClientException("wip.submo_is_complete_or_close");
			}
			moLine.setQtyReceive(moLine.getQtyReceive().add(qtyLot));
			if (moLine.getQty().equals(moLine.getQtyReceive())) {
				moLine.setLineStatus(Documentation.STATUS_COMPLETED);
				moLine.setWorkStatus(ManufactureOrderLine.WORK_STATUS_COMPLETED);
				moLine.setDateEndActual(now);
				moLine = em.merge(moLine);
				
				long transSeq = basManager.getHisSequence();
				CompleteMoLineHis his = new CompleteMoLineHis(moLine);
				his.setHisSeq(transSeq);
				em.persist(his);
			}
			em.merge(moLine);
			
			//�����ӳ�ԭ��
			if(moLine.getOrgRrn().equals(139420L) && parentLot.getDelayReason()!=null && !"".equals(parentLot.getDelayReason())){
				ManufactureOrderLineDelay lineDelay = new ManufactureOrderLineDelay();
				lineDelay.setOrgRrn(parentLot.getOrgRrn());
				lineDelay.setIsActive(true);
				lineDelay.setCreated(now);
				lineDelay.setCreatedBy(userRrn);
				lineDelay.setUpdated(now);
				lineDelay.setUpdatedBy(userRrn);
				lineDelay.setMasterMoRrn(moLine.getMasterMoRrn());
				lineDelay.setMasterMoId(moLine.getMasterMoId());
				lineDelay.setMaterialName(parentMaterial.getName());
				lineDelay.setMaterialId(parentMaterial.getMaterialId());
				lineDelay.setMaterialRrn(parentMaterial.getObjectRrn());
				lineDelay.setUomId(parentMaterial.getInventoryUom());
				lineDelay.setDateEnd(moLine.getDateEnd());
				lineDelay.setDateEndActual(moLine.getDateEndActual());
				lineDelay.setDateReceive(now);
				lineDelay.setDelayReason(parentLot.getDelayReason());
				lineDelay.setDelayReasonDetail(parentLot.getDelayReasonDetail());
				lineDelay.setQtyReceive(BigDecimal.ONE);
				if(wc!=null){
					lineDelay.setWorkcenterId(wc.getName());
					lineDelay.setWorkcenterRrn(wc.getWarehouseRrn());
				}
				em.persist(lineDelay);
			}

			//�޸�MO��������
			ManufactureOrder mo = null;
			if (parentLot.getMoRrn() != null) {
				mo = em.find(ManufactureOrder.class, parentLot.getMoRrn());
				if (mo != null) {
					if (mo.getMaterialRrn().equals(parentLot.getMaterialRrn())) {
						mo.setQtyReceive(mo.getQtyReceive().add(qtyLot));
						em.merge(mo);
					}
				}
			}
			
			List<Lot> newLots = new ArrayList<Lot>();
			for (int i = 0; i< qty; i++) {
				Lot newLot = (Lot)parentLot.clone();
				String lotId;
				if (lotIds != null && lotIds.size() >= i) {
					lotId = lotIds.get(i);
				} else {
					lotId = invManager.generateNextNumber(parentMaterial.getOrgRrn(), parentMaterial);
				}
				newLot.setLotId(lotId);
				newLot.setQtyTransaction(BigDecimal.ONE);
				if (mo != null) {
					newLot.setMoId(mo.getDocId());
				}
				
				/**
				 * �˶η�����Ϊ������ֹ��������Σ���������������ʱʹ��ָ���������ɵ����ε����
				 */
				boolean flag = true;
				Lot genLot = null;
				try {
					StringBuffer sql = new StringBuffer(" SELECT Lot FROM Lot as Lot ");
					sql.append("WHERE");
					sql.append(ADBase.BASE_CONDITION);
					sql.append("AND lotId = ?");
					Query query = em.createQuery(sql.toString());
					query.setParameter(1, newLot.getOrgRrn());
					query.setParameter(2,  newLot.getLotId());
					List<Lot> lotList = query.getResultList();
					if (lotList != null && lotList.size() > 0) {
						genLot = lotList.get(0);
					}
//					genLot = invManager.getLotByLotId(newLot.getOrgRrn(), newLot.getLotId());
					if(genLot != null && genLot.getObjectRrn() != null && genLot.getPosition().equals(InvLot.POSITION_GEN)){
						genLot.setUpdated(newLot.getUpdated());
						genLot.setUpdatedBy(newLot.getUpdatedBy());
						genLot.setPosition(newLot.getPosition());
						genLot.setIsUsed(newLot.getIsUsed());
						genLot.setDateProduct(newLot.getDateProduct());
						genLot.setWorkCenterRrn(newLot.getWorkCenterRrn());
						genLot.setWorkCenterId(newLot.getWorkCenterId());
						genLot.setWarehouseRrn(newLot.getWarehouseRrn());
						genLot.setWarehouseId(newLot.getWarehouseId());
						genLot.setMoId(newLot.getMoId());
						genLot.setUserQc(newLot.getUserQc());
						genLot.setEquipmentId(newLot.getEquipmentId());
						genLot.setMoldId(newLot.getMoldId());
						genLot.setReverseField1(newLot.getReverseField1());
						genLot.setReverseField2(newLot.getReverseField2());
						genLot.setReverseField3(newLot.getReverseField3());
						genLot.setReverseField4(newLot.getReverseField4());
						genLot.setReverseField5(newLot.getReverseField5());
						genLot.setLotComment(newLot.getLotComment());
						genLot.setQtyCurrent(newLot.getQtyCurrent());
						genLot.setQtyInitial(newLot.getQtyInitial());
						genLot.setQtyCurrent(newLot.getQtyCurrent());
						genLot.setQtyTransaction(newLot.getQtyTransaction());
						genLot.setMoRrn(newLot.getMoRrn());
						genLot.setMoLineRrn(newLot.getMoLineRrn());
						em.merge(genLot);
						newLot = genLot;
					}else{
						flag = false;
					}
				} catch (Exception e) {
					flag = false;
				}
				if(!flag){
					em.persist(newLot);
				}
				newLots.add(newLot);
				
				invManager.updateLotStorage(parentMaterial.getOrgRrn(), newLot.getObjectRrn(), writeOffWarehouse.getObjectRrn(), newLot.getQtyTransaction(), userRrn);
			}
			
			long transSeq = basManager.getHisSequence();
			List<ManufactureOrderBom> boms = this.getLotBom(parentLot);
			//����BOM�ۼ�����
			List<Lot> serialChildLots = new ArrayList<Lot>();
			List<Lot> batchChildLots = new ArrayList<Lot>();
			for (ManufactureOrderBom bom : boms) {
				Material childMaterial = em.find(Material.class, bom.getMaterialRrn());
				bom.setMaterial(childMaterial);
				
				if (InvLot.LOTTYPE_MATERIAL.equals(childMaterial.getLotType())) {
					Lot currentChildLot = invManager.getMaterialLot(childMaterial.getOrgRrn(), childMaterial, userRrn);
					currentChildLot.setQtyTransaction(bom.getUnitQty().multiply(qtyLot));
					
					invManager.updateStorage(currentChildLot.getOrgRrn(), currentChildLot.getMaterialRrn(), writeOffWarehouse.getObjectRrn(), currentChildLot.getQtyTransaction().negate(), false, userRrn);
					invManager.updateLotStorage(currentChildLot.getOrgRrn(), currentChildLot.getObjectRrn(), writeOffWarehouse.getObjectRrn(), currentChildLot.getQtyTransaction().negate(), userRrn);
					
					currentChildLot.setUpdatedBy(userRrn);
					
					em.merge(currentChildLot);
					batchChildLots.add(currentChildLot);
				} else if (InvLot.LOTTYPE_BATCH_A.equals(childMaterial.getLotType()) 
						|| InvLot.LOTTYPE_BATCH.equals(childMaterial.getLotType()) 
						|| InvLot.LOTTYPE_SERIAL.equals(childMaterial.getLotType())) {
					List<Lot> currentChildLots = new ArrayList<Lot>();	
					for (Lot childLot : childLots) {
						if (childLot.getMaterialRrn().equals(bom.getMaterialRrn())) {
							currentChildLots.add(childLot);
						}
					}
						
					if (currentChildLots.size() == 0) {
						throw new ClientParameterException("wip.material_lot_not_found", childMaterial.getMaterialId());
					}
					
					for (Lot currentChildLot : currentChildLots) {
						BigDecimal qtyTrans = currentChildLot.getQtyTransaction();
						currentChildLot = em.find(Lot.class, currentChildLot.getObjectRrn());
						currentChildLot.setQtyTransaction(qtyTrans);
						if (!InvLot.POSITION_INSTOCK.equals(currentChildLot.getPosition()) && 
								!InvLot.POSITION_WIP.equals(currentChildLot.getPosition())) {
							throw new ClientParameterException("wip.lot_not_in_wip_or_stock", currentChildLot.getLotId());
						}
						//�۳������ֿ�����
						long warehouseRrn;
						if (InvLot.POSITION_WIP.equals(currentChildLot.getPosition())) {
							warehouseRrn = writeOffWarehouse.getObjectRrn();
						} else {
							warehouseRrn = writeOffWarehouse.getObjectRrn();
						}
						if (InvLot.LOTTYPE_BATCH_A.equals(childMaterial.getLotType())) {
							LotStorage lotStorage = invManager.getLotStorage(currentChildLot.getOrgRrn(), currentChildLot.getObjectRrn(), warehouseRrn, 1);
							if (lotStorage.getQtyOnhand().compareTo(currentChildLot.getQtyTransaction()) < 0) {
								throw new ClientParameterException("wip.lot_current_less_used", currentChildLot.getLotId());
							}
							if (InvLot.POSITION_WIP.equals(currentChildLot.getPosition())) {
								currentChildLot.setQtyCurrent(currentChildLot.getQtyCurrent().subtract(currentChildLot.getQtyTransaction()));
							}
						} else if (InvLot.LOTTYPE_BATCH.equals(childMaterial.getLotType())) {
							
						} else {
							if (currentChildLot.getIsUsed()) {
								throw new ClientParameterException("wip.lot_is_used", currentChildLot.getLotId());
							}
							currentChildLot.setQtyCurrent(BigDecimal.ZERO);
							currentChildLot.setIsUsed(true);
						}
						invManager.updateStorage(currentChildLot.getOrgRrn(), currentChildLot.getMaterialRrn(), warehouseRrn, currentChildLot.getQtyTransaction().negate(), false, userRrn);
						invManager.updateLotStorage(currentChildLot.getOrgRrn(), currentChildLot.getObjectRrn(), warehouseRrn, currentChildLot.getQtyTransaction().negate(), userRrn);
					
						currentChildLot.setUpdatedBy(userRrn);
						
						em.merge(currentChildLot);
					}
					if (InvLot.LOTTYPE_BATCH_A.equals(childMaterial.getLotType())
							|| InvLot.LOTTYPE_BATCH.equals(childMaterial.getLotType())) {
						batchChildLots.addAll(currentChildLots);
					} else {
						serialChildLots.addAll(currentChildLots);
					}
				}
			}
			
			for (Lot childLot : batchChildLots) {
				int count = 0;
				//��¼������ʷ
				UsedLotHis his = new UsedLotHis(childLot);
				his.setHisSeq(transSeq);
				em.persist(his);
				
				BigDecimal qtyUnit = null;
				for (ManufactureOrderBom bom : boms) {
					if (childLot.getMaterialRrn().equals(bom.getMaterialRrn())) {
						qtyUnit = bom.getUnitQty();
					}
				}
				//��¼LotComponent
				for (Lot newLot : newLots) {
					LotComponent lotComp = new LotComponent();
					lotComp.setOrgRrn(newLot.getOrgRrn());
					lotComp.setIsActive(true);
					lotComp.setCreatedBy(userRrn);
					lotComp.setUpdatedBy(userRrn);
					lotComp.setCreated(now);
					lotComp.setLotParentRrn(newLot.getObjectRrn());
					lotComp.setLotParentId(newLot.getLotId());
					lotComp.setMaterialParentRrn(newLot.getMaterialRrn());
					lotComp.setMaterialParentId(newLot.getMaterialId());
					lotComp.setMaterialParentName(newLot.getMaterialName());
					lotComp.setLotChildRrn(childLot.getObjectRrn());
					lotComp.setLotChildId(childLot.getLotId());
					lotComp.setMaterialChildRrn(childLot.getMaterialRrn());
					lotComp.setMaterialChildId(childLot.getMaterialId());
					lotComp.setMaterialChildName(childLot.getMaterialName());
					if (mo != null) {
						lotComp.setMoRrn(mo.getObjectRrn());
						lotComp.setMoId(mo.getDocId());
					}
					lotComp.setMoLineRrn(moLine.getObjectRrn());
					lotComp.setQtyProduct(newLot.getQtyTransaction());
//					lotComp.setQtyConsume(childLot.getQtyTransaction().divide(qtyLot, Constants.DIVIDE_SCALE, RoundingMode.FLOOR));
					if(qtyUnit.multiply(new BigDecimal(count)).compareTo(childLot.getQtyTransaction()) < 0){
						lotComp.setQtyConsume(qtyUnit);
					}else{
						break;
					}
					em.persist(lotComp);
					count ++;
				}
			}
			
			for (ManufactureOrderBom bom : boms) {
				if (InvLot.LOTTYPE_SERIAL.equals(bom.getMaterial().getLotType())) {
					int qtyBom = bom.getUnitQty().intValue();
					for (Lot newLot : newLots) {
						int j = 0;
						for (Lot childLot : serialChildLots) {
							if (childLot.getMaterialRrn().equals(bom.getMaterialRrn()) 
									&& childLot.getUsedLotRrn() == null) {
								childLot.setUsedLotRrn(newLot.getObjectRrn());
								em.merge(childLot);
								
								LotComponent lotComp = new LotComponent();
								lotComp.setOrgRrn(newLot.getOrgRrn());
								lotComp.setIsActive(true);
								lotComp.setCreatedBy(userRrn);
								lotComp.setUpdatedBy(userRrn);
								lotComp.setCreated(now);
								lotComp.setLotParentRrn(newLot.getObjectRrn());
								lotComp.setLotParentId(newLot.getLotId());
								lotComp.setMaterialParentRrn(newLot.getMaterialRrn());
								lotComp.setMaterialParentId(newLot.getMaterialId());
								lotComp.setMaterialParentName(newLot.getMaterialName());
								lotComp.setLotChildRrn(childLot.getObjectRrn());
								lotComp.setLotChildId(childLot.getLotId());
								lotComp.setMaterialChildRrn(childLot.getMaterialRrn());
								lotComp.setMaterialChildId(childLot.getMaterialId());
								lotComp.setMaterialChildName(childLot.getMaterialName());
								if (mo != null) {
									lotComp.setMoRrn(mo.getObjectRrn());
									lotComp.setMoId(mo.getDocId());
								}
								lotComp.setMoLineRrn(moLine.getObjectRrn());
								lotComp.setQtyProduct(newLot.getQtyTransaction());
								lotComp.setQtyConsume(BigDecimal.ONE);
								em.persist(lotComp);
								
								//��¼������ʷ
								UsedLotHis his = new UsedLotHis(childLot);
								his.setHisSeq(transSeq);
								em.persist(his);
								
								j++;
								if (j >= qtyBom) {
									break;
								}
							}
						}
					}
				}
			}
						
			//�ѷ�����������BOM���з��䣬Ӧ����BOM�����ѷ�������
			for (ManufactureOrderBom moBom : boms) {
				Material material = em.find(Material.class, moBom.getMaterialRrn());
				if (!material.getIsLotControl()) {
					continue;
				}
				//SERIAL��BATCH������getLotBom���Ѿ�����
				//ȡ���������ϱ����ѷ�����
//				material.setQtyAllocation(material.getQtyAllocation().subtract(moBom.getUnitQty().multiply(qtyLot)));
//				em.merge(material);
			}
			
			//��¼������ʷ
			for (Lot newLot : newLots) {
				ReceiveLotHis his = new ReceiveLotHis(newLot);
				his.setActionComment("");
				his.setHisSeq(transSeq);
				em.persist(his);
			}
			
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	
	/* 
	 * ���ֲ�����Σ�ֻ��������쳵�������ֻ�޸Ŀ���������޸ĺ�����
	 * @see com.graly.mes.wip.client.WipManager#partlyDisassembleLot(long, com.graly.mes.wip.model.Lot, java.util.List, long)
	 */
	@Override
	public Lot partlyDisassembleLot(long orgRrn, Lot parentLot, List<Lot> subLots, long userRrn) throws ClientException {
		try {
			if(parentLot == null){
				throw new ClientException("Lot is null!");
			}else if(parentLot.getObjectRrn() == null){
				throw new ClientException("Lot is not exists!");
			}
			Material parentMaterial = em.find(Material.class, parentLot.getMaterialRrn());
			Warehouse writeOffWarehouse = invManager.getWriteOffWarehouse(orgRrn);
			Date now = new Date();

			//����serial���͵���Ҫ���λ��
			if(InvLot.LOTTYPE_SERIAL.equals(parentLot.getLotType())){
				if (!InvLot.POSITION_INSTOCK.equals(parentLot.getPosition()) && 
						!InvLot.POSITION_WIP.equals(parentLot.getPosition())) {
					throw new ClientParameterException("wip.lot_not_in_wip_or_stock", parentLot.getLotId());
				}
			}
				
			//���ֿ��п���Ƿ�
			if (InvLot.POSITION_WIP.equals(parentLot.getPosition())) {
				//WIP��ʾδ��⣬δ���ʱ����Ϊ��ǰ����
				
			} else {
				LotStorage lotStorage = invManager.getLotStorage(orgRrn, parentLot.getObjectRrn(), writeOffWarehouse.getObjectRrn(), 1);
				parentLot.setQtyCurrent(lotStorage.getQtyOnhand());
			}
			
			if(parentLot.getQtyCurrent().compareTo(parentLot.getQtyTransaction()) < 0 ){
				throw new ClientException("��ǰ�����������");
			}
//��һ��		
			//���ٰ��Ʒ�Ŀ��/����������Lot�ϼ�¼��qty_current����
			if (InvLot.LOTTYPE_MATERIAL.equals(parentMaterial.getLotType())) {
				//Material��������
				Lot lot = invManager.getMaterialLot(parentMaterial.getOrgRrn(), parentMaterial, userRrn);
				invManager.updateStorage(parentMaterial.getOrgRrn(), parentLot.getMaterialRrn(), writeOffWarehouse.getObjectRrn(), parentLot.getQtyTransaction().negate(), false, userRrn);//���ӵ�ֵΪ����������෴�������ӿ�����
				invManager.updateLotStorage(parentMaterial.getOrgRrn(), lot.getObjectRrn(), writeOffWarehouse.getObjectRrn(), parentLot.getQtyTransaction().negate(), userRrn);//���ӵ�ֵΪ����������෴�������ӿ�����
				parentLot.setUpdated(now);//��¼�������� ���Ǽ�һ���ֶ������棿
			} else {
				//������������
				parentLot.setUpdated(now);//��¼����ʱ��
				
				invManager.updateStorage(parentMaterial.getOrgRrn(), parentLot.getMaterialRrn(), writeOffWarehouse.getObjectRrn(), parentLot.getQtyTransaction().negate(), false, userRrn);//���ӵ�ֵΪ����������෴�������ӿ�����
				invManager.updateLotStorage(parentMaterial.getOrgRrn(), parentLot.getObjectRrn(), writeOffWarehouse.getObjectRrn(), parentLot.getQtyTransaction().negate(), userRrn);//���ӵ�ֵΪ����������෴�������ӿ�����
			}
			
			Warehouse workWarehouse =null;
			WorkCenter	wc = em.find(WorkCenter.class, parentLot.getWorkCenterRrn());
			Warehouse xnWarehouse = em.find(Warehouse.class, wc.getWarehouseRrn());
			boolean parnentFlag=false;
			if(parentLot.getOrgRrn().equals(139420L) && "�����".equals(xnWarehouse.getWarehouseType()) && xnWarehouse.getDefaultLocatorRrn()!=null){
				workWarehouse = em.find(Warehouse.class, xnWarehouse.getDefaultLocatorRrn());
				List<WorkSchopMaterial> wsMaterials =  adManager.getEntityList(parentLot.getOrgRrn(),
						WorkSchopMaterial.class,Integer.MAX_VALUE, 
						 "materialRrn ="+parentLot.getMaterialRrn(),null);
				if(wsMaterials.size()>0){
					parnentFlag=true;
				}
			}else{
				workWarehouse =null;
			}
			
			if(workWarehouse!=null && workWarehouse.getObjectRrn()!=null&&("ע��".equals(parentMaterial.getProcessName())||parnentFlag) ){
				invManager.updateWorkShopStorage(parentLot.getOrgRrn(), parentLot.getMaterialRrn(), workWarehouse.getObjectRrn(), parentLot.getQtyTransaction().negate(), false, userRrn);
				invManager.updateWorkShopLotStorage(parentLot.getOrgRrn(), parentLot.getObjectRrn(), workWarehouse.getObjectRrn(), parentLot.getQtyTransaction().negate(), userRrn);
			}
			
			parentLot.setQtyCurrent(parentLot.getQtyCurrent().add(parentLot.getQtyTransaction().negate()));//�޸�Lot�ϼ�¼��qty_current��ֵ
			em.merge(parentLot);//����ʱ��
			
//�ڶ���
			//TODO ��Ӧ�Ĺ����������������Ҫ����ȥ,����Ѿ�����ɵĹ����û������,״̬Ҫ�Ļ�Approved
			
			//�޸�MOLine��������
			
			ManufactureOrderLine moLine = em.find(ManufactureOrderLine.class, parentLot.getMoLineRrn());
//			if (moLine.getQtyReceive().compareTo(parentLot.getQtyTransaction()) < 0) {//�ѽ�������С�ڲ����
//				throw new ClientException("������������");
//			}
//		if (!ManufactureOrder.STATUS_APPROVED.equals(moLine.getLineStatus())) {
//			throw new ClientException("wip.submo_is_complete_or_close");
//		}
			moLine.setQtyReceive(moLine.getQtyReceive().subtract(parentLot.getQtyTransaction()));//�ѽ��������������
			if (moLine.getQty().compareTo(moLine.getQtyReceive()) > 0) {//������С��������
//				moLine.setLineStatus(Documentation.STATUS_APPROVED);
				moLine.setLineStatus(Documentation.STATUS_CLOSED);//ͯ���Ҫ��رղ�ֹ�����
				moLine.setWorkStatus(ManufactureOrderLine.WORK_STATUS_SUSPENED);
				moLine.setUpdated(now);
				moLine.setUpdatedBy(userRrn);
				moLine = em.merge(moLine);
				
				long transSeq = basManager.getHisSequence();
				DisassembleMoLineHis his = new DisassembleMoLineHis(moLine);
				his.setHisSeq(transSeq);
				em.persist(his);
			}
			em.merge(moLine);


//			//�޸�MO��������
//			ManufactureOrder mo = null;
//			if (parentLot.getMoRrn() != null) {
//				mo = em.find(ManufactureOrder.class, parentLot.getMoRrn());
//				if (mo != null) {
//					if (mo.getMaterialRrn().equals(parentLot.getMaterialRrn())) {
//						if(mo.getQtyIn() != null && mo.getQtyReceive().subtract(mo.getQtyIn()).compareTo(parentLot.getQtyTransaction()) < 0){
//							throw new ClientException("�Ѿ��������,ʣ��δ������������");
//						}
//						mo.setQtyReceive(mo.getQtyReceive().subtract(parentLot.getQtyTransaction()));//�ӽ������м������β�ֵ���
//						em.merge(mo);
//					}
//					parentLot.setMoId(mo.getDocId());
//					em.merge(parentLot);
//				}
//			}

//������ ����BOM�嵥������Lot������
			long transSeq = basManager.getHisSequence();
			int i = 1;
			List<ManufactureOrderBom> boms = getLotBom(parentLot);
			//����BOM�ۼ�sublot������
			for (ManufactureOrderBom bom : boms) {
				Material childMaterial = em.find(Material.class, bom.getMaterialRrn());
				List<Lot> currentChildLots = new ArrayList<Lot>();
				for (Lot childLot : subLots) {
					if (childLot.getMaterialRrn().equals(bom.getMaterialRrn())) {
						currentChildLots.add(childLot);
					}
				}
				if (InvLot.LOTTYPE_MATERIAL.equals(childMaterial.getLotType())) {
					Lot currentChildLot;
					//���������Lot�����Դ˸�Lot�ϵ�����Ϊ׼
					if (currentChildLots.size() > 0) {
						currentChildLot = em.find(Lot.class, currentChildLots.get(0).getObjectRrn());
					} else {
						currentChildLot = invManager.getMaterialLot(childMaterial.getOrgRrn(), childMaterial, userRrn);
						currentChildLots.add(currentChildLot);
					}
					currentChildLot.setQtyTransaction(bom.getUnitQty());
					//�����������
					invManager.updateStorage(currentChildLot.getOrgRrn(), currentChildLot.getMaterialRrn(), writeOffWarehouse.getObjectRrn(), currentChildLot.getQtyTransaction(), false, userRrn);//���������ϵĿ��
					invManager.updateLotStorage(currentChildLot.getOrgRrn(), currentChildLot.getObjectRrn(), writeOffWarehouse.getObjectRrn(), currentChildLot.getQtyTransaction(), userRrn);//���������ϵĿ��
					
					
					boolean childFlag = false;
					List<WorkSchopMaterial> wsMaterials =  adManager.getEntityList(currentChildLot.getOrgRrn(),
							WorkSchopMaterial.class,Integer.MAX_VALUE, 
							 "materialRrn ="+childMaterial.getObjectRrn(),null);
					if(wsMaterials.size()>0){
						childFlag=true;
					}
					if(workWarehouse!=null && workWarehouse.getObjectRrn()!=null &&("ע��".equals(childMaterial.getProcessName())||childFlag)  ){//��Ϊͯ���Ҫ����ʱֻ��ע�ܼ������Բ��
						invManager.updateWorkShopStorage(currentChildLot.getOrgRrn(), currentChildLot.getMaterialRrn(), workWarehouse.getObjectRrn(), currentChildLot.getQtyTransaction(), false, userRrn);
						invManager.updateWorkShopLotStorage(currentChildLot.getOrgRrn(), currentChildLot.getObjectRrn(), workWarehouse.getObjectRrn(), currentChildLot.getQtyTransaction(), userRrn);
					}
					
					currentChildLot.setUpdatedBy(userRrn);
					currentChildLot.setUsedLotRrn(parentLot.getObjectRrn());
					em.merge(currentChildLot);
				} else if (InvLot.LOTTYPE_BATCH_A.equals(childMaterial.getLotType())
						|| InvLot.LOTTYPE_BATCH.equals(childMaterial.getLotType()) 
						|| InvLot.LOTTYPE_SERIAL.equals(childMaterial.getLotType())) {
					if (currentChildLots.size() == 0) {
						throw new ClientParameterException("wip.material_lot_not_found", childMaterial.getMaterialId());
					}
					for (Lot currentChildLot : currentChildLots) {
						BigDecimal qtyTrans = currentChildLot.getQtyTransaction();
						currentChildLot = em.find(Lot.class, currentChildLot.getObjectRrn());
						currentChildLot.setQtyTransaction(qtyTrans);
						if (!InvLot.POSITION_INSTOCK.equals(currentChildLot.getPosition()) && 
								!InvLot.POSITION_WIP.equals(currentChildLot.getPosition())) {
							throw new ClientParameterException("wip.lot_not_in_wip_or_stock", currentChildLot.getLotId());
						}
						//���������ֿ�����
						long warehouseRrn;
						if (InvLot.POSITION_WIP.equals(currentChildLot.getPosition())) {
//						warehouseRrn = currentChildLot.getWarehouseRrn();
							warehouseRrn = writeOffWarehouse.getObjectRrn();
						} else {
							warehouseRrn = writeOffWarehouse.getObjectRrn();
						}
						
						if (InvLot.LOTTYPE_BATCH_A.equals(childMaterial.getLotType())) {
							LotStorage lotStorage = invManager.getLotStorage(currentChildLot.getOrgRrn(), currentChildLot.getObjectRrn(), warehouseRrn, 1);
//��Ϊ�����ӿ�����Բ�	 					if (lotStorage.getQtyOnhand().compareTo(currentChildLot.getQtyTransaction()) < 0) {
//��Ҫ�ټ�鵱ǰ�����							throw new ClientParameterException("wip.lot_current_less_used", currentChildLot.getLotId());
//�񹻣�ֱ�ӻ���ȥ��ok						}
							if (InvLot.POSITION_WIP.equals(currentChildLot.getPosition())) {
								currentChildLot.setQtyCurrent(currentChildLot.getQtyCurrent().add(currentChildLot.getQtyTransaction()));
							}
						} else if (InvLot.LOTTYPE_BATCH.equals(childMaterial.getLotType())) {
							
						} else {
							if (currentChildLot.getIsUsed()) {
								//���������ʹ���ˣ�����ʹ�����Ĳ��Ǳ��β�ֵĸ��������쳣��ͬһ������������ͬʱʹ�������ָ�����
								if(!currentChildLot.getUsedLotRrn().equals(parentLot.getObjectRrn()))
									throw new ClientParameterException("wip.lot_is_used", currentChildLot.getLotId());
							}
							currentChildLot.setQtyCurrent(currentChildLot.getQtyCurrent().add(currentChildLot.getQtyTransaction()));
							currentChildLot.setIsUsed(false);
						}
						
						//�����������
						invManager.updateStorage(currentChildLot.getOrgRrn(), currentChildLot.getMaterialRrn(), warehouseRrn, currentChildLot.getQtyTransaction(), false, userRrn);
						invManager.updateLotStorage(currentChildLot.getOrgRrn(), currentChildLot.getObjectRrn(), warehouseRrn, currentChildLot.getQtyTransaction(), userRrn);
					
						
						boolean childFlag = false;
						List<WorkSchopMaterial> wsMaterials =  adManager.getEntityList(currentChildLot.getOrgRrn(),
								WorkSchopMaterial.class,Integer.MAX_VALUE, 
								 "materialRrn ="+childMaterial.getObjectRrn(),null);
						if(wsMaterials.size()>0){
							childFlag=true;
						}
						if(workWarehouse!=null && workWarehouse.getObjectRrn()!=null &&("ע��".equals(childMaterial.getProcessName())||childFlag) ){//��Ϊͯ���Ҫ����ʱֻ��ע�ܼ������Բ��
							invManager.updateWorkShopStorage(currentChildLot.getOrgRrn(), currentChildLot.getMaterialRrn(), workWarehouse.getObjectRrn(), currentChildLot.getQtyTransaction(), false, userRrn);
							invManager.updateWorkShopLotStorage(currentChildLot.getOrgRrn(), currentChildLot.getObjectRrn(), workWarehouse.getObjectRrn(), currentChildLot.getQtyTransaction(), userRrn);
						}
						
						currentChildLot.setUpdatedBy(userRrn);
						em.merge(currentChildLot);
					}	
				}
				
				
				
//���Ĳ� ��¼��ʷ
				for (Lot currentChildLot : currentChildLots) {
					
					//��¼���������ʷ
					DisassembleLotHis his = new DisassembleLotHis(currentChildLot);
					his.setActionComment("It's sublot of " + parentLot.getLotId());
					his.setHisSeq(transSeq);
					em.persist(his);
					
					//��¼LotComponent ������������������Ϊ����
					LotComponent lotComp = new LotComponent();
					lotComp.setOrgRrn(parentLot.getOrgRrn());
					lotComp.setIsActive(true);
					lotComp.setCreatedBy(userRrn);
					lotComp.setUpdatedBy(userRrn);
					lotComp.setCreated(now);
					lotComp.setLotParentRrn(parentLot.getObjectRrn());
					lotComp.setLotParentId(parentLot.getLotId());
					lotComp.setMaterialParentRrn(parentLot.getMaterialRrn());
					lotComp.setMaterialParentId(parentLot.getMaterialId());
					lotComp.setMaterialParentName(parentLot.getMaterialName());
					lotComp.setLotChildRrn(currentChildLot.getObjectRrn());
					lotComp.setLotChildId(currentChildLot.getLotId());
					lotComp.setMaterialChildRrn(currentChildLot.getMaterialRrn());
					lotComp.setMaterialChildId(currentChildLot.getMaterialId());
					lotComp.setMaterialChildName(currentChildLot.getMaterialName());
					lotComp.setQtyProduct(parentLot.getQtyTransaction().negate());
					lotComp.setQtyConsume(currentChildLot.getQtyTransaction().negate());
					lotComp.setSeqNo((long)i * 10);
					em.persist(lotComp);
					i++;
				}
			}
			
			//�ѷ�����������BOM���з��䣬Ӧ����BOM�����ѷ�������
			for (ManufactureOrderBom moBom : boms) {
				Material material = em.find(Material.class, moBom.getMaterialRrn());
				if (!material.getIsLotControl()) {
					continue;
				}
				//SERIAL��BATCH������getLotBom���Ѿ�����
				//ȡ���������ϱ����ѷ�����
//				material.setQtyAllocation(material.getQtyAllocation().add(moBom.getUnitQty()));
//				em.merge(material);
			}
			
			//��¼���������ʷ
			DisassembleLotHis his = new DisassembleLotHis(parentLot);
			his.setCreated(now);
			ADUser user = em.find(ADUser.class, userRrn);
			his.setActionComment("Did partly disassemble action at " + now + "by " + user.getUserName());
			his.setHisSeq(transSeq);
			em.persist(his);
			
			return parentLot;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	@Override
	public Lot receiveLot(WorkCenter wc, Lot parentLot, List<Lot> childLots, long userRrn) throws ClientException {
		return receiveLot(wc, parentLot, childLots, null, userRrn);
	}
			
	//��������
	@Override
	public Lot receiveLot(WorkCenter wc, Lot parentLot, List<Lot> childLots, Lot genLot, long userRrn) throws ClientException {
		try {
			//��� ��ʱ���������
			lotManager.deleteLotTemp(parentLot.getMoLineRrn(), parentLot.getOrgRrn());
			Material parentMaterial = em.find(Material.class, parentLot.getMaterialRrn());
			Warehouse writeOffWarehouse = invManager.getWriteOffWarehouse(parentLot.getOrgRrn());
			Date now = new Date();
			//�޸�MOLine��������
			ManufactureOrderLine moLine = em.find(ManufactureOrderLine.class, parentLot.getMoLineRrn());
			//�����ӳ�ԭ��
			if(moLine.getOrgRrn().equals(139420L) && parentLot.getDelayReason()!=null && !"".equals(parentLot.getDelayReason())){
				ManufactureOrderLineDelay lineDelay = new ManufactureOrderLineDelay();
				lineDelay.setOrgRrn(parentLot.getOrgRrn());
				lineDelay.setIsActive(true);
				lineDelay.setCreated(now);
				lineDelay.setCreatedBy(userRrn);
				lineDelay.setUpdated(now);
				lineDelay.setUpdatedBy(userRrn);
				lineDelay.setMasterMoRrn(moLine.getMasterMoRrn());
				lineDelay.setMasterMoId(moLine.getMasterMoId());
				lineDelay.setMaterialName(parentMaterial.getName());
				lineDelay.setMaterialId(parentMaterial.getMaterialId());
				lineDelay.setMaterialRrn(parentMaterial.getObjectRrn());
				lineDelay.setUomId(parentMaterial.getInventoryUom());
				lineDelay.setDateEnd(moLine.getDateEnd());
				lineDelay.setDateEndActual(moLine.getDateEndActual());
				lineDelay.setDateReceive(now);
				lineDelay.setDelayReason(parentLot.getDelayReason());
				lineDelay.setDelayReasonDetail(parentLot.getDelayReasonDetail());
				lineDelay.setQtyReceive(parentLot.getQtyTransaction());
				lineDelay.setDelayDept(parentLot.getDelayDept());
				if(wc!=null){
					lineDelay.setWorkcenterId(wc.getName());
					lineDelay.setWorkcenterRrn(wc.getWarehouseRrn());
				}
				em.persist(lineDelay);
			}
			//---���������,���ƻ�֪ͨCOMPLETED
			boolean subPlanNoticeQty=false;
			if(moLine.getOrgRrn().equals(139420L ) && parentLot.getMpsLineDeliveryRrn()!=null&&parentLot.getMpsLineDeliveryRrn()>0){
				//���û����MpsLineDeliveryRrn��Ϊ0
				subPlanNoticeQty=true;
				MpsLineDelivery mpsLineDelivery = em.find(MpsLineDelivery.class, parentLot.getMpsLineDeliveryRrn());
				mpsLineDelivery.setDocStatus(MpsLineDelivery.DOC_STATUS_COMPLETED);
				if(genLot != null  && genLot.getObjectRrn() != null && genLot.getPosition().equals(InvLot.POSITION_GEN)){
					mpsLineDelivery.setLotId(genLot.getLotId());
				}else{
					mpsLineDelivery.setLotId(parentLot.getLotId());
				}
				mpsLineDelivery.setMoLineRrn(moLine.getObjectRrn());
				mpsLineDelivery.setMoId(moLine.getMasterMoId());
				mpsLineDelivery.setQtyReceived(parentLot.getQtyTransaction());
				moLine.setPlanNoticeQty(moLine.getPlanNoticeQty()-1L);
				em.merge(mpsLineDelivery);
			}
			Warehouse workWarehouse =null;
			Warehouse xnWarehouse = em.find(Warehouse.class, wc.getWarehouseRrn());//
			boolean parnentFlag=false;
			if(moLine.getOrgRrn().equals(139420L) && "�����".equals(xnWarehouse.getWarehouseType()) && xnWarehouse.getDefaultLocatorRrn()!=null){
				workWarehouse = em.find(Warehouse.class, xnWarehouse.getDefaultLocatorRrn());
				List<WorkSchopMaterial> wsMaterials =  adManager.getEntityList(parentLot.getOrgRrn(),
						WorkSchopMaterial.class,Integer.MAX_VALUE,  "materialRrn ="+parentLot.getMaterialRrn(),null);
				if(wsMaterials.size()>0){
					parnentFlag=true;
				}
			}else{
				workWarehouse =null;
			}
			
			//���ӿ��/��������
			if (InvLot.LOTTYPE_MATERIAL.equals(parentMaterial.getLotType())) {
				//Material�����������ӵ������ֿ�
				Lot lot = invManager.getMaterialLot(parentMaterial.getOrgRrn(), parentMaterial, userRrn);
				invManager.updateStorage(parentMaterial.getOrgRrn(), parentLot.getMaterialRrn(), writeOffWarehouse.getObjectRrn(), parentLot.getQtyTransaction(), false, userRrn);
				invManager.updateLotStorage(parentMaterial.getOrgRrn(), lot.getObjectRrn(), writeOffWarehouse.getObjectRrn(), parentLot.getQtyTransaction(), userRrn);
				parentLot.setDateProduct(now);
				parentLot.setWorkCenterRrn(wc.getObjectRrn());
				parentLot.setWorkCenterId(wc.getName());
				
				if(workWarehouse!=null && workWarehouse.getObjectRrn()!=null&&("ע��".equals(parentMaterial.getProcessName())|| parnentFlag) ){
					invManager.updateWorkShopStorage(parentMaterial.getOrgRrn(), parentLot.getMaterialRrn(), workWarehouse.getObjectRrn(), parentLot.getQtyTransaction(), false, userRrn);
					invManager.updateWorkShopLotStorage(parentMaterial.getOrgRrn(), lot.getObjectRrn(), workWarehouse.getObjectRrn(), parentLot.getQtyTransaction(), userRrn);
				}
			} else {
				//���������������ӵ�Ҳ���Ӻ����ֿ�
//				Warehouse wh = em.find(Warehouse.class, wc.getWarehouseRrn());
				
				if(genLot != null  && genLot.getObjectRrn() != null && genLot.getPosition().equals(InvLot.POSITION_GEN)){
					genLot.setIsActive(true);
					genLot.setCreatedBy(userRrn);
					genLot.setUpdatedBy(userRrn);
					genLot.setCreated(now);
					genLot.setPosition(InvLot.POSITION_WIP);
					genLot.setIsUsed(false);
					genLot.setDateProduct(now);
					genLot.setWorkCenterRrn(wc.getObjectRrn());
					genLot.setWorkCenterId(wc.getName());
//					genLot.setWarehouseRrn(wc.getWarehouseRrn());
//					genLot.setWarehouseId(wh.getWarehouseId());
					genLot.setWarehouseRrn(writeOffWarehouse.getObjectRrn());
					genLot.setWarehouseId(writeOffWarehouse.getWarehouseId());
					genLot.setMoId(parentLot.getMoId());
					genLot.setUserQc(parentLot.getUserQc());
					genLot.setEquipmentId(parentLot.getEquipmentId());
					genLot.setMoldId(parentLot.getMoldId());
					genLot.setReverseField1(parentLot.getReverseField1());
					genLot.setReverseField2(parentLot.getReverseField2());
					genLot.setReverseField3(parentLot.getReverseField3());
					genLot.setReverseField4(parentLot.getReverseField4());
					genLot.setReverseField5(parentLot.getReverseField5());
					genLot.setLotComment(parentLot.getLotComment());
					genLot.setQtyInitial(parentLot.getQtyTransaction());
					genLot.setQtyCurrent(parentLot.getQtyTransaction());
					genLot.setQtyTransaction(parentLot.getQtyTransaction());
					genLot.setMoRrn(parentLot.getMoRrn());
					genLot.setMoLineRrn(parentLot.getMoLineRrn());
					em.merge(genLot);
					parentLot = genLot;
				}else{
					parentLot.setIsActive(true);
					parentLot.setCreatedBy(userRrn);
					parentLot.setUpdatedBy(userRrn);
					parentLot.setCreated(now);
					parentLot.setPosition(InvLot.POSITION_WIP);
					parentLot.setIsUsed(false);
					parentLot.setDateProduct(now);
					parentLot.setWorkCenterRrn(wc.getObjectRrn());
					parentLot.setWorkCenterId(wc.getName());
//					parentLot.setWarehouseRrn(wc.getWarehouseRrn());
//					parentLot.setWarehouseId(wh.getWarehouseId());
					parentLot.setWarehouseRrn(writeOffWarehouse.getObjectRrn());
					parentLot.setWarehouseId(writeOffWarehouse.getWarehouseId());
					parentLot.setQtyInitial(parentLot.getQtyTransaction());
					parentLot.setQtyCurrent(parentLot.getQtyTransaction());
					em.persist(parentLot);
				}
//				invManager.updateStorage(parentMaterial.getOrgRrn(), parentLot.getMaterialRrn(), wc.getWarehouseRrn(), parentLot.getQtyTransaction(), true, userRrn);
//				invManager.updateLotStorage(parentMaterial.getOrgRrn(), parentLot.getObjectRrn(), wc.getWarehouseRrn(), parentLot.getQtyTransaction(), userRrn);
				invManager.updateStorage(parentMaterial.getOrgRrn(), parentLot.getMaterialRrn(), writeOffWarehouse.getObjectRrn(), parentLot.getQtyTransaction(), false, userRrn);
				invManager.updateLotStorage(parentMaterial.getOrgRrn(), parentLot.getObjectRrn(), writeOffWarehouse.getObjectRrn(), parentLot.getQtyTransaction(), userRrn);
				
				if(workWarehouse!=null && workWarehouse.getObjectRrn()!=null &&("ע��".equals(parentMaterial.getProcessName())|| parnentFlag) ){
					invManager.updateWorkShopStorage(parentMaterial.getOrgRrn(), parentLot.getMaterialRrn(), workWarehouse.getObjectRrn(), parentLot.getQtyTransaction(), false, userRrn);
					invManager.updateWorkShopLotStorage(parentMaterial.getOrgRrn(), parentLot.getObjectRrn(), workWarehouse.getObjectRrn(), parentLot.getQtyTransaction(), userRrn);
				}
			}
			
			//�޸�MOLine��������
//			ManufactureOrderLine moLine = em.find(ManufactureOrderLine.class, parentLot.getMoLineRrn());
			moLine = em.find(ManufactureOrderLine.class, parentLot.getMoLineRrn());
			if (moLine.getQty().compareTo(moLine.getQtyReceive().add(parentLot.getQtyTransaction())) < 0) {//��������������������
				throw new ClientException("wip.receive_lot_larger_than_qty");
			}
			if (!Documentation.STATUS_APPROVED.equals(moLine.getLineStatus())) {
				throw new ClientException("wip.submo_is_complete_or_close");
			}
			moLine.setQtyReceive(moLine.getQtyReceive().add(parentLot.getQtyTransaction()));
			if (moLine.getQty().equals(moLine.getQtyReceive())) {
				moLine.setLineStatus(Documentation.STATUS_COMPLETED);
				moLine.setWorkStatus(ManufactureOrderLine.WORK_STATUS_COMPLETED);
				moLine.setDateEndActual(now);
				moLine = em.merge(moLine);
				
				long transSeq = basManager.getHisSequence();
				CompleteMoLineHis his = new CompleteMoLineHis(moLine);
				his.setHisSeq(transSeq);
				em.persist(his);
			}
			em.merge(moLine);
			
			//�޸�MO��������
			ManufactureOrder mo = null;
			if (parentLot.getMoRrn() != null) {
				mo = em.find(ManufactureOrder.class, parentLot.getMoRrn());
				if(subPlanNoticeQty){
					mo.setPlanNoticeQty(mo.getPlanNoticeQty()-1L);
				}
				if (mo != null) {
					if (mo.getMaterialRrn().equals(parentLot.getMaterialRrn())) {
						mo.setQtyReceive(mo.getQtyReceive().add(parentLot.getQtyTransaction()));
						em.merge(mo);
					}
					parentLot.setMoId(mo.getDocId());
					em.merge(parentLot);
				}
			}
			
			long transSeq = basManager.getHisSequence();
			int i = 1;
			List<ManufactureOrderBom> boms = this.getLotBom(parentLot);
			//����BOM�ۼ�����
			for (ManufactureOrderBom bom : boms) {
				Material childMaterial = em.find(Material.class, bom.getMaterialRrn());
				
				boolean childFlag = false;
				List<WorkSchopMaterial> wsMaterials =  adManager.getEntityList(parentLot.getOrgRrn(),
						WorkSchopMaterial.class,Integer.MAX_VALUE, 
						 "materialRrn ="+childMaterial.getObjectRrn(),null);
				if(wsMaterials.size()>0){
					childFlag=true;
				}
				
				List<Lot> currentChildLots = new ArrayList<Lot>();
				for (Lot childLot : childLots) {
					if (childLot.getMaterialRrn().equals(bom.getMaterialRrn())) {
						currentChildLots.add(childLot);
					}
				}
				if (InvLot.LOTTYPE_MATERIAL.equals(childMaterial.getLotType())) {
					Lot currentChildLot;
					//���������Lot�����Դ˸�Lot�ϵ�����Ϊ׼
					if (currentChildLots.size() > 0) {
						currentChildLot = em.find(Lot.class, currentChildLots.get(0).getObjectRrn());
					} else {
						currentChildLot = invManager.getMaterialLot(childMaterial.getOrgRrn(), childMaterial, userRrn);
						currentChildLots.add(currentChildLot);
					}
					currentChildLot.setQtyTransaction(bom.getUnitQty());
					invManager.updateStorage(currentChildLot.getOrgRrn(), currentChildLot.getMaterialRrn(), writeOffWarehouse.getObjectRrn(), currentChildLot.getQtyTransaction().negate(), false, userRrn);
					invManager.updateLotStorage(currentChildLot.getOrgRrn(), currentChildLot.getObjectRrn(), writeOffWarehouse.getObjectRrn(), currentChildLot.getQtyTransaction().negate(), userRrn);
					
					//ֻ�۳����Ƽ���ע�ܳ�������
					if(workWarehouse!=null && workWarehouse.getObjectRrn()!=null){
						if("ע��".equals(childMaterial.getProcessName()) ||childFlag  ){
							invManager.updateWorkShopStorage(currentChildLot.getOrgRrn(), currentChildLot.getMaterialRrn(), workWarehouse.getObjectRrn(), currentChildLot.getQtyTransaction().negate(), false, userRrn);
							invManager.updateWorkShopLotStorage(currentChildLot.getOrgRrn(), currentChildLot.getObjectRrn(), workWarehouse.getObjectRrn(), currentChildLot.getQtyTransaction().negate(), userRrn);
							UseWorkShopHis workShopHis = new UseWorkShopHis(currentChildLot);
							workShopHis.setCreated(new Date());
							workShopHis.setCreatedBy(userRrn);
							workShopHis.setUpdated(new Date());
							workShopHis.setUpdatedBy(userRrn);
							if(moLine!=null && moLine.getObjectRrn()!=null){
								workShopHis.setMoId(moLine.getMasterMoId());
								workShopHis.setMoRrn(moLine.getMasterMoRrn());
								workShopHis.setMoLineRrn(moLine.getObjectRrn());
							}
							workShopHis.setWorkCenterRrn(wc.getObjectRrn());
							workShopHis.setWorkCenterId(wc.getName());
							workShopHis.setWarehouseRrn(workWarehouse.getObjectRrn());
							workShopHis.setWarehouseId(workWarehouse.getWarehouseId());
							em.persist(workShopHis);
						}
					}
					
					currentChildLot.setUpdatedBy(userRrn);
					currentChildLot.setUsedLotRrn(parentLot.getObjectRrn());
					em.merge(currentChildLot);
				} else if (InvLot.LOTTYPE_BATCH_A.equals(childMaterial.getLotType())
						|| InvLot.LOTTYPE_BATCH.equals(childMaterial.getLotType()) 
						|| InvLot.LOTTYPE_SERIAL.equals(childMaterial.getLotType())) {
					if (currentChildLots.size() == 0) {
						throw new ClientParameterException("wip.material_lot_not_found", childMaterial.getMaterialId());
					}
					for (Lot currentChildLot : currentChildLots) {
						BigDecimal qtyTrans = currentChildLot.getQtyTransaction();
						currentChildLot = em.find(Lot.class, currentChildLot.getObjectRrn());
						currentChildLot.setQtyTransaction(qtyTrans);
						if (!InvLot.POSITION_INSTOCK.equals(currentChildLot.getPosition()) && 
								!InvLot.POSITION_WIP.equals(currentChildLot.getPosition())) {
							throw new ClientParameterException("wip.lot_not_in_wip_or_stock", currentChildLot.getLotId());
						}
						//�۳������ֿ�����
						long warehouseRrn;
						if (InvLot.POSITION_WIP.equals(currentChildLot.getPosition())) {
//							warehouseRrn = currentChildLot.getWarehouseRrn();
							warehouseRrn = writeOffWarehouse.getObjectRrn();
						} else {
							warehouseRrn = writeOffWarehouse.getObjectRrn();
						}
						if (InvLot.LOTTYPE_BATCH_A.equals(childMaterial.getLotType())) {
							LotStorage lotStorage = invManager.getLotStorage(currentChildLot.getOrgRrn(), currentChildLot.getObjectRrn(), warehouseRrn, 1);
							if (lotStorage.getQtyOnhand().compareTo(currentChildLot.getQtyTransaction()) < 0) {
								throw new ClientParameterException("wip.lot_current_less_used", currentChildLot.getLotId());
							}
							if (InvLot.POSITION_WIP.equals(currentChildLot.getPosition())) {
								currentChildLot.setQtyCurrent(currentChildLot.getQtyCurrent().subtract(currentChildLot.getQtyTransaction()));
							}
						} else if (InvLot.LOTTYPE_BATCH.equals(childMaterial.getLotType())) {
							
						} else {
							if (currentChildLot.getIsUsed()) {
								throw new ClientParameterException("wip.lot_is_used", currentChildLot.getLotId());
							}
							currentChildLot.setQtyCurrent(BigDecimal.ZERO);
							currentChildLot.setIsUsed(true);
						}
						invManager.updateStorage(currentChildLot.getOrgRrn(), currentChildLot.getMaterialRrn(), warehouseRrn, currentChildLot.getQtyTransaction().negate(), false, userRrn);
						invManager.updateLotStorage(currentChildLot.getOrgRrn(), currentChildLot.getObjectRrn(), warehouseRrn, currentChildLot.getQtyTransaction().negate(), userRrn);
					
						currentChildLot.setUpdatedBy(userRrn);
						currentChildLot.setUsedLotRrn(parentLot.getObjectRrn());
						em.merge(currentChildLot);
						
						//ֻ�����Ƶ������ϲŻ�۳����
						if(workWarehouse!=null && workWarehouse.getObjectRrn()!=null){
							if("ע��".equals(childMaterial.getProcessName()) ||childFlag ){
								invManager.updateWorkShopStorage(currentChildLot.getOrgRrn(), currentChildLot.getMaterialRrn(), workWarehouse.getObjectRrn(), currentChildLot.getQtyTransaction().negate(), false, userRrn);
								invManager.updateWorkShopLotStorage(currentChildLot.getOrgRrn(), currentChildLot.getObjectRrn(), workWarehouse.getObjectRrn(), currentChildLot.getQtyTransaction().negate(), userRrn);
								UseWorkShopHis workShopHis = new UseWorkShopHis(currentChildLot);
								workShopHis.setCreated(new Date());
								workShopHis.setCreatedBy(userRrn);
								workShopHis.setUpdated(new Date());
								workShopHis.setUpdatedBy(userRrn);
								if(moLine!=null && moLine.getObjectRrn()!=null){
									workShopHis.setMoId(moLine.getMasterMoId());
									workShopHis.setMoRrn(moLine.getMasterMoRrn());
									workShopHis.setMoLineRrn(moLine.getObjectRrn());
								}
								workShopHis.setWorkCenterRrn(wc.getObjectRrn());
								workShopHis.setWorkCenterId(wc.getName());
								workShopHis.setWarehouseRrn(workWarehouse.getObjectRrn());
								workShopHis.setWarehouseId(workWarehouse.getWarehouseId());
								em.persist(workShopHis);
							}
						}
					}	
				}
				
//				LinkedHashMap<Long, LotConsume> materialConsumes = new LinkedHashMap<Long, LotConsume>();
				for (Lot currentChildLot : currentChildLots) {
					
					//��¼������ʷ
					UsedLotHis his = new UsedLotHis(currentChildLot);
					if(currentChildLot.getMoRrn() != null){
						ManufactureOrder parentMo = em.find(ManufactureOrder.class, currentChildLot.getMoRrn());
						if(parentMo != null){
							his.setMoId(parentMo.getDocId());
						}
					}
					his.setHisSeq(transSeq);
					em.persist(his);
					
					//��¼LotComponent
					LotComponent lotComp = new LotComponent();
					lotComp.setOrgRrn(parentLot.getOrgRrn());
					lotComp.setIsActive(true);
					lotComp.setCreatedBy(userRrn);
					lotComp.setUpdatedBy(userRrn);
					lotComp.setCreated(now);
					lotComp.setLotParentRrn(parentLot.getObjectRrn());
					lotComp.setLotParentId(parentLot.getLotId());
					lotComp.setMaterialParentRrn(parentLot.getMaterialRrn());
					lotComp.setMaterialParentId(parentLot.getMaterialId());
					lotComp.setMaterialParentName(parentLot.getMaterialName());
					lotComp.setLotChildRrn(currentChildLot.getObjectRrn());
					lotComp.setLotChildId(currentChildLot.getLotId());
					lotComp.setMaterialChildRrn(currentChildLot.getMaterialRrn());
					lotComp.setMaterialChildId(currentChildLot.getMaterialId());
					lotComp.setMaterialChildName(currentChildLot.getMaterialName());
					if (mo != null) {
						lotComp.setMoRrn(mo.getObjectRrn());
						lotComp.setMoId(mo.getDocId());
					}
					lotComp.setMoLineRrn(moLine.getObjectRrn());
					lotComp.setQtyProduct(parentLot.getQtyTransaction());
					lotComp.setQtyConsume(currentChildLot.getQtyTransaction());
					lotComp.setSeqNo((long)i * 10);
					em.persist(lotComp);
					i++;
					
					/*
					 * ȡ����ʵ��������������Ϊ���ϵ�����
					 * 
					if (Lot.LOTTYPE_MATERIAL.equals(parentMaterial.getLotType())) {
						//����Material���͵�ԭ���ϲ���¼ʵ������
					} else {
						//��������ԭ����
						List<LotConsume> childConsumes;
						if (Lot.LOTTYPE_MATERIAL.equals(childMaterial.getLotType())) {
							childConsumes = invManager.getMaterialBomConsume(parentLot.getMoRrn(), bom.getPath());
						} else {
							childConsumes = invManager.getMaterialConsume(currentChildLot.getObjectRrn());
						}
						if (childConsumes.size() > 0) {
							for (LotConsume childConsume : childConsumes) {
								if (materialConsumes.containsKey(childConsume.getMaterialRrn())) {
									LotConsume lotConsume = materialConsumes.get(childConsume.getMaterialRrn());
									lotConsume.setQtyConsume(lotConsume.getQtyConsume().add(currentChildLot.getQtyTransaction().multiply(childConsume.getUnitConsume())));
								} else {
									LotConsume lotConsume = new LotConsume();
									lotConsume.setOrgRrn(parentLot.getOrgRrn());
									lotConsume.setIsActive(true);
									lotConsume.setCreatedBy(userRrn);
									lotConsume.setUpdatedBy(userRrn);
									lotConsume.setCreated(new Date());
									lotConsume.setLotRrn(parentLot.getObjectRrn());
									lotConsume.setLotId(parentLot.getLotId());
									lotConsume.setMaterialRrn(childConsume.getMaterialRrn());
									lotConsume.setMaterialId(childConsume.getMaterialId());
									lotConsume.setMaterialName(childConsume.getMaterialName());
									lotConsume.setQtyProduct(parentLot.getQtyCurrent());
									lotConsume.setQtyConsume(currentChildLot.getQtyTransaction().multiply(childConsume.getUnitConsume()));
									lotConsume.setIsWin(false);
							
									materialConsumes.put(childConsume.getMaterialRrn(), lotConsume);
								}
							}
						} else {
							if (materialConsumes.containsKey(currentChildLot.getMaterialRrn())) {
								LotConsume lotConsume = materialConsumes.get(currentChildLot.getMaterialRrn());
								lotConsume.setQtyConsume(lotConsume.getQtyConsume().add(currentChildLot.getQtyTransaction()));
							} else {
								LotConsume lotConsume = new LotConsume();
								lotConsume.setOrgRrn(parentLot.getOrgRrn());
								lotConsume.setIsActive(true);
								lotConsume.setCreatedBy(userRrn);
								lotConsume.setUpdatedBy(userRrn);
								lotConsume.setCreated(new Date());
								lotConsume.setLotRrn(parentLot.getObjectRrn());
								lotConsume.setLotId(parentLot.getLotId());
								lotConsume.setMaterialRrn(currentChildLot.getMaterialRrn());
								lotConsume.setMaterialId(currentChildLot.getMaterialId());
								lotConsume.setMaterialName(currentChildLot.getMaterialName());
								lotConsume.setQtyProduct(parentLot.getQtyTransaction());
								lotConsume.setQtyConsume(currentChildLot.getQtyTransaction());
								lotConsume.setIsWin(false);
								materialConsumes.put(currentChildLot.getMaterialRrn(), lotConsume);
							}
						}
					}*/
					
				}
			}
				
			//�ѷ�����������BOM���з��䣬Ӧ����BOM�����ѷ�������
			for (ManufactureOrderBom moBom : boms) {
				Material material = em.find(Material.class, moBom.getMaterialRrn());
				if (!material.getIsLotControl()) {
					continue;
				}
				//SERIAL��BATCH������getLotBom���Ѿ������˴�moBom.getUnitQty�Ѿ��Ǿ��������(��λ����*��������)
				//ȡ���������ϱ����ѷ�����
//				material.setQtyAllocation(material.getQtyAllocation().subtract(moBom.getUnitQty()));
//				em.merge(material);
			}
			if(workWarehouse!=null && workWarehouse.getObjectRrn()!=null){
				RecWorkShopHis recWorkHis =  new RecWorkShopHis(parentLot);
				recWorkHis.setCreated(new Date());
				recWorkHis.setCreatedBy(userRrn);
				recWorkHis.setUpdated(new Date());
				recWorkHis.setUpdatedBy(userRrn);
				recWorkHis.setWorkCenterRrn(wc.getObjectRrn());
				recWorkHis.setWorkCenterId(wc.getName());
				recWorkHis.setWarehouseRrn(workWarehouse.getObjectRrn());
				recWorkHis.setWarehouseId(workWarehouse.getWarehouseId());
				if (mo != null) {//���mo���ǿվͼ�¼moId
					recWorkHis.setMoId(mo.getDocId());
				}
				em.persist(recWorkHis);
			}
			
			//��¼������ʷ
			ReceiveLotHis his = new ReceiveLotHis(parentLot);
			if (mo != null) {//���mo���ǿվͼ�¼moId
				his.setMoId(mo.getDocId());
			}
			his.setCreated(now);
			his.setActionComment("");
			his.setHisSeq(transSeq);
			em.persist(his);
			
			return parentLot;
			
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	

	
//	public List<Lot> getNextChildLot(Lot parentLot, List<Lot> childLots) throws ClientException {
//		try {
//			List<Lot> lots = new ArrayList<Lot>();
//			List<ManufactureOrderBom> moBoms = getLotBom(parentLot);
//			for (ManufactureOrderBom moBom : moBoms) {
//				Material material = em.find(Material.class, moBom.getMaterialRrn());
//				if (!material.getIsLotControl()) {
//					continue;
//				}
//				if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
//					continue;
//				}
//				for (Lot childLot : childLots) {
//					if (material.getObjectRrn().equals(childLot.getMaterialRrn())) {
//						Lot lot = (Lot)adManager.getEntity(childLot);
//						if (lot.getQtyCurrent().compareTo(BigDecimal.ZERO) == 0) {
//							break;
//						}
//						lots.add(lot);
//						break;
//					}
//				}
//			}
//			return lots;
//		} catch (Exception e){ 
//			logger.error(e.getMessage(), e);
//			throw new ClientException(e);
//		}
//	}
	
	
	//�������
	@Override
	public void disassembleLot(long parentLotRrn, long userRrn) throws ClientException {
		try{
			Date now = new Date();
			Lot parentLot = em.find(Lot.class, parentLotRrn);
			
			if(parentLot == null || !parentLot.getIsActive()){
				throw new ClientException("inv.lotnotexist");
			}
			
			if (InvLot.LOTTYPE_MATERIAL.equals(parentLot.getLotType())) {
				throw new ClientParameterException("wip.material_must_be_serial_or_batch", parentLot.getMaterialId());
			}
//			if (!Lot.POSITION_WIP.equals(parentLot.getPosition())) {
//				throw new ClientParameterException("wip.lot_not_in_wip", parentLot.getLotId());
//			}
			if (parentLot.getIsUsed() || parentLot.getQtyInitial().compareTo(parentLot.getQtyCurrent()) != 0) {
				throw new ClientParameterException("wip.lot_is_used", parentLot.getLotId());
			}
			
			List<LotComponent> lotComponents = getLotComponent(parentLot.getObjectRrn());
			
			if (lotComponents.size() == 0) {
				throw new ClientParameterException("wip.lot_no_child", parentLot.getLotId());
			}
			
			Warehouse writeOffWarehouse = invManager.getWriteOffWarehouse(parentLot.getOrgRrn());
			
			Warehouse workWarehouse =null;
			WorkCenter	wc = em.find(WorkCenter.class, parentLot.getWorkCenterRrn());
			Warehouse  xnWarehouse = em.find(Warehouse.class, wc.getWarehouseRrn());
			if(parentLot.getOrgRrn().equals(139420L) && "�����".equals(xnWarehouse.getWarehouseType()) && xnWarehouse.getDefaultLocatorRrn()!=null){
				workWarehouse = em.find(Warehouse.class, xnWarehouse.getDefaultLocatorRrn());
			}else{
				workWarehouse =null;
			}
			for (LotComponent lotComponent : lotComponents) {
				Lot childLot = em.find(Lot.class, lotComponent.getLotChildRrn());
				if(childLot == null){
//					throw new ClientParameterException("wip.lotchild_not_exists", lotComponent.getLotChildId());
					//BUG�޸�:�����������Ҳ������½�һ������.
					String sql = " insert into wip_lot (OBJECT_RRN, ORG_RRN, IS_ACTIVE, CREATED, CREATED_BY, UPDATED, " +
					"UPDATED_BY, LOCK_VERSION, LOT_ID, LOT_TYPE, MATERIAL_RRN, MATERIAL_ID, WAREHOUSE_RRN," +
					" WAREHOUSE_ID, LOCATOR_ID, RECEIPT_RRN, RECEIPT_ID, IQC_RRN, IQC_ID, PO_RRN," +
					" PO_ID, IN_RRN, IN_ID, OUT_RRN, OUT_ID, QTY_INITIAL, QTY_CURRENT, IS_USED, " +
					"USER_QC, LOCATOR_RRN, IQC_LINE_RRN, IN_LINE_RRN, OUT_LINE_RRN, PO_LINE_RRN," +
					" DATE_IN, DATE_OUT, DESCRIPTION, PART_RRN, PART_NAME, PART_VERSION, PART_TYPE," +
					" MAIN_QTY, SUB_QTY, LOCATION, ENGINEER, CUSTOMER_NAME, CUSTOMER_ORDER, CUSTOMER_PART_ID," +
					" CUSTOMER_LOT_ID, PRIORITY, REQUITED_DATE, DUE_DATE, PLAN_START_DATE, CREATE_TIME, " +
					"LOT_COMMENT, CURRENT_SEQ, START_MAIN_QTY, START_SUB_QTY, END_MAIN_QTY, END_SUB_QTY, " +
					"START_TIME, END_TIME, TRACK_IN_TIME, TRACK_OUT_TIME, EQUIPMENT_RRN, EQUIPMENT_ID, " +
					"PARENT_LOT_RRN, COM_CLASS, STATE, SUB_STATE, STATE_ENTRY_TIME, PRE_TRANS_TYPE, " +
					"PRE_COM_CLASS, PRE_STATE, PRE_SUB_STATE, PRE_STATE_ENTRY_TIME, PROCESS_INSTANCE_RRN, " +
					"PROCEDURE_RRN, PROCEDURE_NAME, PROCEDURE_VERSION, STEP_RRN, STEP_NAME, STEP_VERSION," +
					" OPERATOR_RRN, OPERATOR_NAME, PARENT_UNIT_RRN, SUB_UNIT_TYPE, MO_LINE_RRN, WORKCENTER_RRN," +
					" DATE_PRODUCT, MO_RRN, USED_LOT_RRN, POSITION, TRANSFER_LINE_RRN, MO_ID, MATERIAL_NAME, " +
					"WORKCENTER_ID, REVERSE_FIELD1, REVERSE_FIELD2, REVERSE_FIELD3, REVERSE_FIELD4, REVERSE_FIELD5," +
					" REVERSE_FIELD6, REVERSE_FIELD7, REVERSE_FIELD8, REVERSE_FIELD9, REVERSE_FIELD10, QTY_WAITINGIN," +
					" MOLD_ID, MOLD_RRN, IS_PACKAGE) "
					+ " values (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8, ?9," +
							" ?10, ?11, ?12, ?13, ?14, '', null, '', null, '', null, ''," +
							" null, '', null, '', 0.000000, 0.000000, 'N', '', null, null, null, null, null, null, null," +
							" ?15, null, '', null, '', null, null, '', null, '', '', '', '', null, null, null, null, null, ''," +
							" null, null, null, null, null, null, null, null, null, null, '', null, '', '', '', null, ''," +
							" '', '', '', null, null, null, '', null, null, '', null, null, '', null, '', null, null, null, " +
							"?16, null, 'INSTOCK', null, ?17, ?18, '', '', '', '', '', '', '', '', '', '', " +
							"'', null, '', null, 'N')"; 

					Query query = em.createNativeQuery(sql);
					query.setParameter(1, lotComponent.getLotChildRrn());
					query.setParameter(2, lotComponent.getOrgRrn());
					query.setParameter(3, "Y");
					query.setParameter(4, new Date());
					query.setParameter(5, userRrn);
					query.setParameter(6, new Date());
					query.setParameter(7, userRrn);
					query.setParameter(8, 1);
					Material childMaterial = em.find(Material.class, lotComponent.getMaterialChildRrn());
					String lotId = invManager.generateNextNumber(childMaterial.getOrgRrn(), childMaterial);
					query.setParameter(9, lotId);//lot_id
					query.setParameter(10, childMaterial.getLotType());//lot_type
					query.setParameter(11, childMaterial.getObjectRrn());//material_rrn
					query.setParameter(12, childMaterial.getMaterialId());//material_id
					query.setParameter(13, parentLot.getWarehouseRrn());//warehouserrn
					query.setParameter(14, parentLot.getWarehouseId());//warehouse_id
					String description ="ԭ����:"+lotComponent.getLotChildId()+".�����ţ�"+lotId+"ĸ��:"+parentLot.getLotId();
					query.setParameter(15, description);//description
					query.setParameter(16, lotComponent.getMoRrn());//moRrn
					query.setParameter(17, lotComponent.getMoId());//moid
					query.setParameter(18, childMaterial.getName());//material_name
					query.executeUpdate();
					childLot = em.find(Lot.class, lotComponent.getLotChildRrn());
				}
				childLot.setIsUsed(false);
				childLot.setQtyCurrent(childLot.getQtyCurrent().add(lotComponent.getQtyConsume()));
				childLot.setUpdatedBy(userRrn);
				childLot.setUsedLotRrn(null);
				em.merge(childLot);
				
//				em.remove(lotComponent);
				LotComponent lotComp = (LotComponent)lotComponent.clone();
				lotComp.setObjectRrn(null);
				lotComp.setCreated(now);
				lotComp.setCreatedBy(userRrn);
				lotComp.setUpdatedBy(userRrn);
				lotComp.setQtyProduct(lotComp.getQtyProduct().negate());
				lotComp.setQtyConsume(lotComp.getQtyConsume().negate());
				em.persist(lotComp);
				
				//���ӿ������
				long warehouseRrn;
				if (InvLot.POSITION_WIP.equals(childLot.getPosition())) {
//					warehouseRrn = childLot.getWarehouseRrn();
					warehouseRrn = writeOffWarehouse.getObjectRrn();
				} else {
					warehouseRrn = writeOffWarehouse.getObjectRrn();
				}
				invManager.updateStorage(childLot.getOrgRrn(), childLot.getMaterialRrn(), warehouseRrn, lotComponent.getQtyConsume(), false, userRrn);
				invManager.updateLotStorage(childLot.getOrgRrn(), childLot.getObjectRrn(), warehouseRrn, lotComponent.getQtyConsume(), userRrn);
			
				boolean childFlag = false;
				List<WorkSchopMaterial> wsMaterials =  adManager.getEntityList(childLot.getOrgRrn(),
						WorkSchopMaterial.class,Integer.MAX_VALUE, 
						 "materialRrn ="+childLot.getMaterialRrn(),null);
				if(wsMaterials.size()>0){
					childFlag=true;
				}
				
				Material childMaterial = em.find(Material.class, childLot.getMaterialRrn());
				if(workWarehouse!=null && workWarehouse.getObjectRrn()!=null &&("ע��".equals(childMaterial.getProcessName())|| childFlag) ){//��Ϊͯ���Ҫ����ʱֻ��ע�ܼ������Բ��
					invManager.updateWorkShopStorage(childLot.getOrgRrn(), childLot.getMaterialRrn(), workWarehouse.getObjectRrn(), lotComponent.getQtyConsume(), false, userRrn);
					invManager.updateWorkShopLotStorage(childLot.getOrgRrn(), childLot.getObjectRrn(), workWarehouse.getObjectRrn(), lotComponent.getQtyConsume(), userRrn);
				}
			}
			
			//ɾ��LotConsume
//			StringBuffer sql = new StringBuffer(" DELETE FROM LotConsume ");
//			sql.append(" WHERE lotRrn = ? ");
//			Query query = em.createQuery(sql.toString());
//			query.setParameter(1, parentLotRrn);
//			query.executeUpdate();
			
			/*
			List<Lot> childLots = getAssembleLot(parentLot.getObjectRrn());
			if (childLots.size() == 0) {
				throw new ClientParameterException("wip.lot_no_child", parentLot.getLotId());
			}
			for (Lot childLot : childLots) {
				BigDecimal qtyUnit = BigDecimal.ONE;
				Material material = em.find(Material.class, childLot.getMaterialRrn());
				if (Lot.LOTTYPE_SERIAL.equals(material.getLotType())) {
					childLot.setIsUsed(false);
				} else if (Lot.LOTTYPE_BATCH.equals(material.getLotType())) {
					List<ManufactureOrderBom> boms = getMoLineBom(parentLot.getMoLineRrn());
					
					for (ManufactureOrderBom bom : boms) {
						if (bom.getMaterialRrn().equals(childLot.getMaterialRrn())) {
							qtyUnit = bom.getUnitQty();
							break;
						}
					}
					childLot.setQtyCurrent(childLot.getQtyCurrent().add(qtyUnit));
					childLot.setIsUsed(false);
				}
				childLot.setUpdatedBy(userRrn);
				childLot.setUsedLotRrn(null);
				em.merge(childLot);
				//���ӿ������
				invManager.updateStorage(childLot.getOrgRrn(), childLot.getMaterialRrn(), childLot.getWarehouseRrn(), qtyUnit, userRrn);				
			}
			*/
			//���ٿ������
			invManager.updateStorage(parentLot.getOrgRrn(), parentLot.getMaterialRrn(), writeOffWarehouse.getObjectRrn(), parentLot.getQtyCurrent().negate(), false, userRrn);
			invManager.updateLotStorage(parentLot.getOrgRrn(), parentLot.getObjectRrn(), writeOffWarehouse.getObjectRrn(), parentLot.getQtyCurrent().negate(), userRrn);
			
			Material parentMaterial = em.find(Material.class, parentLot.getMaterialRrn());
			boolean parnentFlag=false;
			List<WorkSchopMaterial> wsMaterials =  adManager.getEntityList(parentLot.getOrgRrn(),
					WorkSchopMaterial.class,Integer.MAX_VALUE, 
					 "materialRrn ="+parentMaterial.getObjectRrn(),null);
			if(wsMaterials.size()>0){
				parnentFlag=true;
			}
			if(workWarehouse!=null && workWarehouse.getObjectRrn()!=null &&("ע��".equals(parentMaterial.getProcessName())||parnentFlag) ){//ע�����ϲŹ���
				invManager.updateWorkShopStorage(parentLot.getOrgRrn(), parentLot.getMaterialRrn(), workWarehouse.getObjectRrn(), parentLot.getQtyCurrent().negate(), false, userRrn);
				invManager.updateWorkShopLotStorage(parentLot.getOrgRrn(), parentLot.getObjectRrn(), workWarehouse.getObjectRrn(), parentLot.getQtyCurrent().negate(), userRrn);
				
			}
			
			//��¼�����ʷ
			long transSeq = basManager.getHisSequence();
			DisassembleLotHis his = new DisassembleLotHis(parentLot);
			his.setCreated(now);
			his.setQtyTransaction(parentLot.getQtyCurrent());
			his.setActionComment("");
			his.setHisSeq(transSeq);
			em.persist(his);
			//ɾ��������
//			em.remove(parentLot);
			parentLot.setIsActive(false);
			em.merge(parentLot);
			
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (ClientParameterException e){
			logger.error(e.getMessage(), e);
			throw e;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	@Override
	public void disassembleMoLine(long moLineRrn, long userRrn)
			throws ClientException {
		try {
			ManufactureOrderLine moLine = em.find(ManufactureOrderLine.class, moLineRrn);
			if(DocumentationLine.LINESTATUS_CLOSED.equalsIgnoreCase(moLine.getLineStatus())){
				throw new ClientException("wip.subMo_has_closed");
			}
			Material parentMaterial = moLine.getMaterial();
			
			if(!InvLot.LOTTYPE_MATERIAL.equals(parentMaterial.getLotType())){
				throw new ClientParameterException("wip_material_isnot_materialtype",parentMaterial.getMaterialId());
			}
			
			Warehouse writeOffWarehouse = invManager.getWriteOffWarehouse(parentMaterial.getOrgRrn());//�����ֿ�
			Date now = new Date();

			//�������Ϻ��ҵ�Lot(Material���͵����ϵ����κž������ϱ��)
			Lot parentLot = invManager.getLotByLotId(parentMaterial.getOrgRrn(), parentMaterial.getMaterialId());
			
			List<LotComponent> lotComponents = getLotComponent(parentLot.getObjectRrn(), moLineRrn);
			
			if (lotComponents.size() == 0) {
				throw new ClientParameterException("wip.lot_no_child", parentLot.getLotId());
			}
			
			for(LotComponent lotComp : lotComponents){
				Lot childLot = em.find(Lot.class, lotComp.getLotChildRrn());
				childLot.setIsUsed(false);
				childLot.setQtyCurrent(childLot.getQtyCurrent().add(lotComp.getQtyConsume()));
				childLot.setUpdatedBy(userRrn);
				childLot.setUsedLotRrn(null);
				em.merge(childLot);				
				LotComponent lotComp2 = (LotComponent)lotComp.clone();
				lotComp2.setObjectRrn(null);
				lotComp2.setCreated(now);
				lotComp2.setCreatedBy(userRrn);
				lotComp2.setUpdatedBy(userRrn);
				lotComp2.setQtyProduct(lotComp2.getQtyProduct().negate());
				lotComp2.setQtyConsume(lotComp2.getQtyConsume().negate());
				em.persist(lotComp2);
				
				//���ӿ������
				long warehouseRrn;
				if (InvLot.POSITION_WIP.equals(childLot.getPosition())) {
					warehouseRrn = writeOffWarehouse.getObjectRrn();
				} else {
					warehouseRrn = writeOffWarehouse.getObjectRrn();
				}
				invManager.updateStorage(childLot.getOrgRrn(), childLot.getMaterialRrn(), warehouseRrn, lotComp.getQtyConsume(), false, userRrn);
				invManager.updateLotStorage(childLot.getOrgRrn(), childLot.getObjectRrn(), warehouseRrn, lotComp.getQtyConsume(), userRrn);
			}
				
			//���ٿ������
			invManager.updateStorage(parentMaterial.getOrgRrn(), parentMaterial.getObjectRrn(), writeOffWarehouse.getObjectRrn(), moLine.getQtyReceive().negate(), false, userRrn);
			invManager.updateLotStorage(parentMaterial.getOrgRrn(), parentLot.getObjectRrn(), writeOffWarehouse.getObjectRrn(), moLine.getQtyReceive().negate(), userRrn);
			
			//�޸�MOLine��������
//			moLine.setUpdated(now);//moLine��updated��¼���ǲ�ֵ�ʱ��
//			moLine.setQtyReceive(BigDecimal.ZERO);//moLine����������Ϊ0
			
			//��MoLine״̬��Ϊclose
			moLine.setLineStatus(DocumentationLine.LINESTATUS_CLOSED);
			em.merge(moLine);
			
			//�޸�MO��������
//			ManufactureOrder mo = null;
//			if (parentLot.getMoRrn() != null) {
//				mo = em.find(ManufactureOrder.class, parentLot.getMoRrn());
//				if (mo != null) {
//					if (mo.getMaterialRrn().equals(parentLot.getMaterialRrn())) {
//						mo.setQtyReceive(mo.getQtyReceive().subtract(parentLot.getQtyTransaction()));
//						em.merge(mo);
//					}
//					parentLot.setMoId(mo.getDocId());
//					em.merge(parentLot);
//				}
//			}
			
			//��¼�����ʷ
			long transSeq = basManager.getHisSequence();
			DisassembleLotHis his = new DisassembleLotHis(parentLot);
			his.setCreated(now);
			his.setQtyTransaction(moLine.getQtyReceive());
			his.setActionComment("MoLine Disassemble");
			his.setHisSeq(transSeq);
			em.persist(his);
			//ɾ��������
//			em.remove(em.getReference(parentLot.getClass(), parentLot.getObjectRrn()));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
//	public List<Lot> getAssembleLot(long parentLotRrn) throws ClientException {
//		StringBuffer sql = new StringBuffer(" SELECT Lot FROM Lot Lot ");
//		sql.append(" WHERE ");
//		sql.append(" usedLotRrn = ? "); 
//		logger.debug(sql);
//		try {
//			Query query = em.createQuery(sql.toString());
//			query.setParameter(1, parentLotRrn);
//			List<Lot> childLots = query.getResultList();
//			return childLots;
//		} catch (Exception e) { 
//			logger.error(e.getMessage(), e);
//			throw new ClientException(e);
//		}
//	}
	
	@Override
	public List<LotComponent> getLotComponent(long parentLotRrn) throws ClientException {
		StringBuffer sql = new StringBuffer(" SELECT LotComponent FROM LotComponent LotComponent ");
		sql.append(" WHERE ");
		sql.append(" lotParentRrn = ? "); 
		logger.debug(sql);
		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, parentLotRrn);
			List<LotComponent> lotComponents = query.getResultList();
			return lotComponents;
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
		
	@Override
	public List<LotComponent> getLotComponent(long parentLotRrn, long moLineRrn) throws ClientException {
		StringBuffer sql = new StringBuffer(" SELECT LotComponent FROM LotComponent LotComponent ");
		sql.append(" WHERE ");
		sql.append(" lotParentRrn = ? and moLineRrn = ? "); 
		logger.debug(sql);
		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, parentLotRrn);
			query.setParameter(2, moLineRrn);
			List<LotComponent> lotComponents = query.getResultList();
			return lotComponents;
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	@Override
	public List<LotComponent> getLotUsages(long childLotRrn) throws ClientException {
		StringBuffer sql = new StringBuffer(" SELECT LotComponent FROM LotComponent LotComponent ");
		sql.append(" WHERE ");
		sql.append(" lotChildRrn = ? "); 
		logger.debug(sql);
		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, childLotRrn);
			List<LotComponent> lotComponents = query.getResultList();
			return lotComponents;
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	@Override
	public List<Lot> getAvailableLot4In(long moRrn) throws ClientException {
		StringBuffer sql = new StringBuffer(" SELECT Lot, LotStorage.qtyOnhand FROM Lot Lot, LotStorage LotStorage ");
		sql.append(" WHERE ");
		sql.append(" Lot.objectRrn = LotStorage.lotRrn ");
		sql.append(" AND Lot.moRrn = ? AND Lot.materialRrn = ?"); 
		sql.append(" AND Lot.isUsed = 'N' ");
		sql.append(" AND LotStorage.warehouseRrn = ? ");
		sql.append(" AND LotStorage.qtyOnhand <> 0 ");
		sql.append(" ORDER BY Lot.dateProduct ");
		logger.debug(sql);
		
		try {
			ManufactureOrder mo = em.find(ManufactureOrder.class, moRrn);
			Warehouse wh = invManager.getWriteOffWarehouse(mo.getOrgRrn());
			
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, moRrn);
			query.setParameter(2, mo.getMaterialRrn());
			query.setParameter(3, wh.getObjectRrn());
			List<Lot> lotList = new ArrayList<Lot>();
			List<Object[]> objs = query.getResultList();
			Lot lot = null;
			BigDecimal qtyOnand;
			for(Object[] obj : objs) {
				lot = (Lot)obj[0];
				qtyOnand = (BigDecimal)obj[1];
				lot.setQtyCurrent(qtyOnand);				
				lotList.add(lot);
			}
			return lotList;
			
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
//	public List<Lot> getCompanentLots(long orgRrn, Lot lot) throws ClientException {
//		try{
//			StringBuffer sql = new StringBuffer(" SELECT Lot FROM Lot as Lot ");
//			sql.append("WHERE");
//			sql.append(ADBase.BASE_CONDITION);
//			sql.append("AND usedLotRrn = ?");
//			logger.debug(sql);
//			Query query = em.createQuery(sql.toString());
//			query.setParameter(1, orgRrn);
//			query.setParameter(2, lot.getObjectRrn());
//			List<Lot> lotList = query.getResultList();
//			
//			return lotList;
//		} catch (Exception e){ 
//			logger.error(e.getMessage(), e);
//			throw new ClientException(e);
//		}
//	}
	
	@Override
	public Lot getWipLotByLotId(String lotId, Long orgRrn) throws ClientException {
		Lot lot = null;
		try {
			StringBuffer sql = new StringBuffer("SELECT Lot FROM Lot as lot WHERE ");
			sql.append(ADBase.BASE_CONDITION);
			sql.append(" AND lot.lotId = ? ");
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, lotId);
			List list = query.getResultList();
			if(list != null && list.size() > 0) {
				lot = (Lot)list.get(0);
			} else
				return null;
			if(lot.getIsUsed()) {
				throw new ClientParameterException("inv.lot_already_used", lot.getLotId());
			}
			if (!InvLot.POSITION_INSTOCK.equals(lot.getPosition()) && 
					!InvLot.POSITION_WIP.equals(lot.getPosition())) {
				throw new ClientParameterException("wip.lot_not_in_wip_or_stock", lot.getLotId());
			}
			if (InvLot.POSITION_WIP.equals(lot.getPosition())) {
				//WIP��ʾδ��⣬δ���ʱ����Ϊ��ǰ����
				lot.setQtyTransaction(lot.getQtyCurrent());
				return lot;
			} else {
				Warehouse house = invManager.getWriteOffWarehouse(orgRrn);
				LotStorage lotStorage = invManager.getLotStorage(orgRrn, lot.getObjectRrn(), house.getObjectRrn(), 1);
				lot.setQtyCurrent(lotStorage.getQtyOnhand());
				lot.setQtyTransaction(lotStorage.getQtyOnhand());
				return lot;
			}
			
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	private String generateMoCode(ManufactureOrder mo) {
		StringBuffer moCode = new StringBuffer("");
		moCode.append(basManager.generateCodePrefix(mo.getOrgRrn(), mo.getDocType()));
//		Material material = em.find(Material.class, mo.getMaterialRrn());
//		moCode.append(material.getMaterialType());
		moCode.append(basManager.generateCodeSuffix(mo.getOrgRrn(), mo.getDocType(), mo.getDateStart()));
		return moCode.toString();
	}
	
	private BigDecimal getQtyTheory(BigDecimal qtyMPS, BigDecimal qtyLeast, BigDecimal qtyIncrease) {
		BigDecimal qtyTheory = qtyMPS;
		if (qtyLeast != null) {
			if (qtyMPS.compareTo(qtyLeast) <= 0) {
				qtyTheory = qtyLeast;
			} else if (qtyIncrease != null && qtyIncrease.compareTo(BigDecimal.ZERO) > 0) {
				int i = 1;
				while (true) {
					if (qtyLeast.add((qtyIncrease.multiply(new BigDecimal(i)))).compareTo(qtyMPS) >= 0) {
						qtyTheory = qtyLeast.add((qtyIncrease.multiply(new BigDecimal(i))));
						break;
					}
					i++;
				}
			}
		}
		return qtyTheory;
	}
	
	/**
	 * ������ʹ������
	 */
	@Override
	public List<MaterialUsed> getMoMaterialUsed(long orgRrn, long materialRrn, String status, String whereClause) throws ClientException {
		//����sql��Ҫ����һ�£�������Ҫ����һ��where��λ��
		List<MaterialUsed> useds = new ArrayList<MaterialUsed>();
		String where = " 1=1 ";
		if (whereClause != null && whereClause.trim().length() > 0) {
			where = where + "  " + whereClause;
		}
		StringBuffer sql = new StringBuffer();
		sql.append("  SELECT  M.MO_RRN, ");
		sql.append("     M.DOC_ID MO_ID, ");
		sql.append("     M.DOC_STATUS MO_STATUS, ");
		sql.append("     M.MATERIAL_RRN, ");
		sql.append("     P.MATERIAL_ID, ");
		sql.append("     P.NAME, ");
		sql.append("     M.UOM_ID, ");
		sql.append("     M.QTY_NEED, ");
		sql.append("     M.QTY_MO_PRODUCT, ");
		sql.append("     M.QTY_MO_RECEIVE, ");
		sql.append("     M.DATE_END ");
		sql.append("  FROM   ( SELECT M.OBJECT_RRN MO_RRN, ");
		sql.append("        M.DOC_ID, ");
		sql.append("        M.DOC_STATUS, ");
		sql.append("        M.MATERIAL_RRN, ");
		sql.append("        M.UOM_ID, ");
		sql.append("        SUM(M.QTY_NEED) QTY_NEED, ");
		sql.append("        SUM(M.QTY_MO_PRODUCT) QTY_MO_PRODUCT, ");
		sql.append("        SUM(M.QTY_MO_RECEIVE) QTY_MO_RECEIVE, ");
		sql.append("        M.DATE_END ");
		sql.append("     FROM  ( SELECT M.OBJECT_RRN, ");
		sql.append("           M.DOC_ID, ");
		sql.append("           M.DOC_STATUS, ");
		sql.append("           M.MATERIAL_RRN, ");
		sql.append("           B.UOM_ID, ");
		sql.append("           B.QTY_NEED, ");
		sql.append("           B.QTY_UNIT * M.QTY_PRODUCT QTY_MO_PRODUCT, ");
		sql.append("           B.QTY_UNIT * M.QTY_RECEIVE QTY_MO_RECEIVE, ");
		sql.append("           M.DATE_END ");
		sql.append("          FROM WIP_MO_BOM B, WIP_MO M ");
		sql.append("          WHERE   B.MO_RRN = M.OBJECT_RRN ");
		sql.append("           AND B.MATERIAL_RRN = ? ");
		sql.append("           AND M.DOC_STATUS IN ");

		if(status != null && !"".equals(status.trim())) {
			sql.append(" ('" + status + "') ");
		} else {
			//Add 'COMPLETED' status By BruceYou 2012-3-5
			sql.append(" ('APPROVED', 'DRAFTED', 'CLOSED', 'COMPLETED') ");			
		}
		sql.append("	 AND " + where );
		sql.append("	 ) M ");
		sql.append("     GROUP BY M.OBJECT_RRN, M.DOC_ID, M.MATERIAL_RRN, M.DOC_STATUS, M.UOM_ID, M.DATE_END) M, ");
		sql.append("     PDM_MATERIAL P ");
		sql.append("  WHERE  M.MATERIAL_RRN = P.OBJECT_RRN ");
		sql.append("  ORDER BY MO_ID ");


		Query query = em.createNativeQuery(sql.toString());
		query.setParameter(1, materialRrn);
		List<Object[]> result = query.getResultList();				
		for (Object[] row : result) {
			MaterialUsed used = new MaterialUsed();
			used.setMoRrn(((BigDecimal)row[0]).longValue());
			used.setMoId((String)row[1]);
			used.setMoStatus((String)row[2]);
			used.setMaterialRrn(((BigDecimal)row[3]).longValue());
			used.setMaterialId((String)row[4]);
			used.setMaterialName((String)row[5]);
			used.setBomUomId((String)row[6]);
			used.setQtyUsed((BigDecimal)row[7]);
			used.setQtyMoProduct((BigDecimal)row[8]);
			used.setQtyMoReceive((BigDecimal)row[9]);
			used.setDateEnd((Date) row[10]);
			useds.add(used);
		}
		return useds;
	}
	
	@Override
	public List<ManufactureOrderLine> getMoLineChildrenForMo(long orgRrn, long moRrn) throws ClientException {
		try {
			List<ManufactureOrderLine> children = new ArrayList<ManufactureOrderLine>();
			StringBuffer sql = new StringBuffer("");
			sql.append("SELECT ManufactureOrderLine FROM ManufactureOrderLine ManufactureOrderLine ");
			sql.append(" WHERE ");
			sql.append(ADBase.BASE_CONDITION);
			sql.append(" AND ManufactureOrderLine.masterMoRrn = ? ");
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, moRrn);
			children = query.getResultList();
			return children;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new OptimisticLockException(e);
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	// ���Batch��Material�������������вֿ��µĿ���б�
	@Override
	public List<LotStorage> getLotAllStorage(long orgRrn, long lotRrn) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT LS.LOT_RRN, LS.WAREHOUSE_RRN, LS.QTY_ONHAND, L.LOT_ID, W.WAREHOUSE_ID ");
		sql.append(" FROM INV_LOT_STORAGE LS, WIP_LOT L, INV_WAREHOUSE W ");
		sql.append(" WHERE LS.ORG_RRN = ? AND LS.LOT_RRN = ? AND LS.QTY_ONHAND <> 0 ");
		sql.append(" AND LS.LOT_RRN = L.OBJECT_RRN ");
		sql.append(" AND LS.WAREHOUSE_RRN = W.OBJECT_RRN");
		try {
			List<LotStorage> lss = new ArrayList<LotStorage>();
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, lotRrn);
			List<Object[]> result = query.getResultList();
			LotStorage ls = null;
			for(Object[] obj : result) {
				ls = new LotStorage();
				ls.setLotRrn(((BigDecimal)obj[0]).longValue());
				ls.setWarehouseRrn(((BigDecimal)obj[1]).longValue());
				ls.setQtyOnhand((BigDecimal)obj[2]);
				ls.setLotId((String)obj[3]);
				ls.setWarehouseId((String)obj[4]);
				lss.add(ls);
			}
			return lss;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	@Override
	public void deleteMoByMps(ManufactureOrder mo, long userRrn) throws ClientException {
		if(mo == null || mo.getMpsRrn() == null)
			return;
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ManufactureOrder FROM ManufactureOrder ManufactureOrder ");
		sql.append(" WHERE ManufactureOrder.mpsRrn = ? ");
		sql.append(" AND ManufactureOrder.docStatus = '" + Documentation.STATUS_DRAFTED + "' ");
		
		try {
			List<ManufactureOrder> mos;
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, mo.getMpsRrn());
			mos = query.getResultList();
			for(ManufactureOrder delMo : mos) {
				deleteMo(delMo, userRrn);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	
	@Override
	public void closeMoByMps(ManufactureOrder mo, long userRrn) throws ClientException {
		if(mo == null || mo.getMpsRrn() == null)
			return;
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ManufactureOrder FROM ManufactureOrder ManufactureOrder ");
		sql.append(" WHERE ManufactureOrder.mpsRrn = ? ");
		sql.append(" AND ManufactureOrder.docStatus = '" + Documentation.STATUS_APPROVED + "' ");
		
		try {
			List<ManufactureOrder> mos;
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, mo.getMpsRrn());
			mos = query.getResultList();
			for(ManufactureOrder closeMo : mos) {
				try {
					closeMo(closeMo, userRrn);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					continue;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	@Override
	public ManufactureOrder getMoById(long orgRrn, String moId) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ManufactureOrder FROM ManufactureOrder ManufactureOrder ");
		sql.append(" WHERE ManufactureOrder.orgRrn = ? AND ManufactureOrder.docId = ? ");
		
		try {
			List<ManufactureOrder> mos;
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, moId);
			mos = query.getResultList();
			if (mos.size() > 0) {
				return mos.get(0);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return null;
	}
	
	@Override
	public void changeWorkCenter(ManufactureOrderLine moLine, long userRrn)
			throws ClientException {
		long moRrn = moLine.getMasterMoRrn();
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT ManufactureOrder FROM ManufactureOrder ManufactureOrder ");
		sql.append(" WHERE ManufactureOrder.objectRrn = ? and ManufactureOrder.orgRrn = ? ");
		List moList = null;
		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, moRrn);
			query.setParameter(2, moLine.getOrgRrn());
			moList = query.getResultList();
			if(moList != null && moList.size() > 0){//���moLine��mo����
				ManufactureOrder mo = (ManufactureOrder) moList.get(0);
				if(mo.getMaterialRrn().compareTo(moLine.getMaterialRrn()) == 0){//���moLine�����Ϻ�mo��������ͬ
					WorkCenter wc = em.find(WorkCenter.class, moLine.getWorkCenterRrn());
					mo.setWorkCenterRrn(wc.getObjectRrn());
					mo.setWorkCenterId(wc.getName());
					mo.setUpdated(new Date());
					mo.setUpdatedBy(userRrn);
					em.merge(mo);
				}
			}
			moLine.setUpdated(new Date());
			moLine.setUpdatedBy(userRrn);
			em.merge(moLine);
			em.flush();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	@Override
	public List<DailyMoMaterial> getDailyMoMaterials(long orgRrn,
			String whereClause) throws ClientException {
		return getDailyMoMaterials(orgRrn, null, null, null, whereClause);
	}

	@Override
	public List<DailyMoMaterial> getDailyMoMaterials(long orgRrn,
			String materialId, String fromDate, String endDate, String whereClause)
			throws ClientException {
		try{
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT DailyMoMaterial FROM DailyMoMaterial DailyMoMaterial ");
			sql.append(" WHERE DailyMoMaterial.orgRrn = :orgrrn ");
			if(materialId != null && materialId.trim().length() > 0){
				sql.append(" AND DailyMoMaterial.materialId = :materialId '");
			}
			
			if(fromDate != null && fromDate.trim().length() > 0){
				sql.append(" AND DailyMoMaterial.currDate >= :fromdate ");
			}
			
			if(endDate != null && endDate.trim().length() > 0){
				sql.append(" AND DailyMoMaterial.currDate <= :todate ");
			}
			
			if(whereClause != null && whereClause.trim().length() > 0){
				sql.append(" AND " + whereClause);
			}
			sql.append(" ORDER BY DailyMoMaterial.currDate ");
			Query query = em.createQuery(sql.toString());
			query.setParameter("orgrrn", orgRrn);
			if(materialId != null && materialId.trim().length() > 0){
				query.setParameter("materialId", materialId);
			}
			
			if(fromDate != null && fromDate.trim().length() > 0){
				SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
				Date d = sdf.parse(fromDate);
				query.setParameter("fromdate", d, TemporalType.DATE);
			}
			
			if(endDate != null && endDate.trim().length() > 0){
				SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
				Date d = sdf.parse(endDate);
				query.setParameter("todate", d, TemporalType.DATE);
			}
			List<DailyMoMaterial> result = query.getResultList();
			
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	@Override
	public List<LotComponent> receiveDetail(long orgRrn, long moLineRrn, String parentLotId) throws ClientException {
		try{
			StringBuffer sql = new StringBuffer();
//			sql.append(" SELECT  LC.mo_id,LC.mo_line_rrn,LC.Material_Parent_Id,LC.Material_Parent_Name,LC.material_child_id,LC.Material_Child_Name,LC.lot_child_id,LC.qty_product,LC.qty_consume,LC.LOT_PARENT_ID FROM wip_lot_component LC ");
//			sql.append(" WHERE LC.mo_line_rrn = ? ");
//			sql.append(" AND LC.LOT_PARENT_ID = ? ");
			sql.append(" FROM LotComponent LotComponent ");
			sql.append(" WHERE LotComponent.moLineRrn = :moLineRrn ");
			sql.append(" AND (LotComponent.lotParentId = :parentLotId or LotComponent.lotParentId like :parentLotId||'$%') ");
			
//			Query query = em.createNativeQuery(sql.toString());
			Query query = em.createQuery(sql.toString());
			query.setParameter("moLineRrn", moLineRrn);
			query.setParameter("parentLotId", parentLotId);
//			List<Object[]> rslt = query.getResultList();
//			List<LotComponent> lcs = new ArrayList<LotComponent>();
//			for(Object[] o : rslt){
//				LotComponent lc = new LotComponent();
//				lc.setMoId((String) (o[0] == null ? "" : o[0]));
//				lc.setMoLineRrn(((BigDecimal)o[1]).longValue());
//				lc.setMaterialParentId((String) o[2]);
//				lc.setMaterialParentName((String) o[3]);
//				lc.setMaterialChildId((String) o[4]);
//				lc.setMaterialChildName((String) o[5]);
//				lc.setLotChildId((String) o[6]);
//				lc.setQtyProduct((BigDecimal)o[7]);
//				lc.setQtyConsume((BigDecimal)o[8]);
//				lc.setLotParentId((String)o[9]);
//				lcs.add(lc);
//			}
//			return lcs;
			return query.getResultList();
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	@Override
	public List<LotComponent> receiveDetail(long orgRrn, long moLineRrn) throws ClientException {
		try{
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT  LC.mo_id,LC.mo_line_rrn,LC.Material_Parent_Id,LC.Material_Parent_Name,LC.material_child_id,LC.Material_Child_Name,LC.lot_child_id,avg(LC.qty_product),sum(LC.qty_consume) FROM wip_lot_component LC ");
			sql.append(" WHERE LC.mo_line_rrn = ? ");
			sql.append(" GROUP BY LC.mo_id,LC.mo_line_rrn,LC.Material_Parent_Id,LC.Material_Parent_Name,LC.material_child_id,LC.Material_Child_Name,LC.lot_child_id ");
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, moLineRrn);
			List<Object[]> rslt = query.getResultList();
			List<LotComponent> lcs = new ArrayList<LotComponent>();
			for(Object[] o : rslt){
				LotComponent lc = new LotComponent();
				lc.setMoId((String) (o[0] == null ? "" : o[0]));
				lc.setMoLineRrn(((BigDecimal)o[1]).longValue());
				lc.setMaterialParentId((String) o[2]);
				lc.setMaterialParentName((String) o[3]);
				lc.setMaterialChildId((String) o[4]);
				lc.setMaterialChildName((String) o[5]);
				lc.setLotChildId((String) o[6]);
				lc.setQtyProduct((BigDecimal)o[7]);
				lc.setQtyConsume((BigDecimal)o[8]);
				lcs.add(lc);
			}
			return lcs;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
//	@Override
//	public List<DailyMoMaterial> getDailyMoMaterials(long orgRrn,
//			String materialId, String fromDate, String endDate, String whereClause)
//			throws ClientException {
//		try{
//			List<DailyMoMaterial> ldmm = new ArrayList<DailyMoMaterial>();
//			StringBuffer sql = new StringBuffer();
//			sql.append(" SELECT D.OBEJCT_RRN, D.ORG_RRN, D.MO_LINE_RRN, D.MO_RRN,D.WORKCENTER_RRN," +
//					" D.MATERIAL_RRN, D.QTY, D.QTY_RECEIVE, D.TOTAL_TIME, D.AVAILABLE_DAY, D.DAY_NO," +
//			" D.CURR_DATE, D.MANPOWER, D.CURR_DAY_POWER FROM DAILY_MO_MATERIAL D ");
//			sql.append(" WHERE D.ORG_RRN = ");
//			sql.append(orgRrn);
//			sql.append(" AND 1=1 ");
//			if(materialId != null && materialId.trim().length() > 0){
//				sql.append(" AND D.MATERIAL_ID LIKE '");
//				sql.append(materialId);
//				sql.append("%'");
//			}
//			
//			String datePattern = "'YYYY-MM-DD'";
//			if(fromDate != null && fromDate.trim().length() > 0){
//				sql.append(" AND TRUNC(D.CURR_DATE) >= TO_DATE('" + fromDate + "', "
//						+ datePattern + ") ");
//			}
//			
//			if(endDate != null && endDate.trim().length() > 0){
//				sql.append(" AND TRUNC(D.CURR_DATE) <= TO_DATE('" + endDate + "', "
//						+ datePattern + ") ");
//			}
//			
//			if(whereClause != null && whereClause.trim().length() > 0){
//				sql.append(" AND " + whereClause);
//			}
//			sql.append(" ORDER BY D.CURR_DATE ");
//			Query query = em.createNativeQuery(sql.toString());
//			List<Object[]> result = (List<Object[]>)query.getResultList();
//			for (Object[] row : result) {
//				DailyMoMaterial dmm = new DailyMoMaterial();
//				dmm.setObjectRrn(Long.valueOf(String.valueOf(row[0])));
//				dmm.setOrgRrn(Long.valueOf(String.valueOf(row[1])));
//				dmm.setMoLineRrn(Long.valueOf(String.valueOf(row[2])));
//				dmm.setMoRrn(Long.valueOf(String.valueOf(row[3])));
//				dmm.setWorkcenterRrn(Long.valueOf(String.valueOf(row[4])));
//				dmm.setMaterialRrn(Long.valueOf(String.valueOf(row[5])));
//				dmm.setQty((BigDecimal)row[6]);
//				dmm.setQtyReceive((BigDecimal)row[7]);
//				dmm.setTotalTime((BigDecimal)row[8]);
//				dmm.setAvailableDay(Long.valueOf(String.valueOf(row[9])));
//				dmm.setCurrDate((Date)row[11]);
//				dmm.setCurrDayPower((BigDecimal)row[13]);
//				ldmm.add(dmm);
//			}
//			return ldmm;
//		} catch (Exception e) {
//			logger.error(e.getMessage(), e);
//			throw new ClientException(e);
//		}
//	}
	
	//�󻷱���Ʒ������쳵���ʵ�ʿ����
	private BigDecimal getWipQtyOnHand(long orgRrn, long materialRrn, long userRrn) throws ClientException{
		Warehouse house1 = invManager.getWriteOffWarehouse(orgRrn);//���쳵����Ʒ��
		Storage s1 = invManager.getMaterialStorage(orgRrn, materialRrn, house1.getObjectRrn(), userRrn);
		
		Warehouse house2 = invManager.getDefaultWarehouse(orgRrn);//������Ʒ��
		Storage s2 = invManager.getMaterialStorage(orgRrn, materialRrn, house2.getObjectRrn(), userRrn);
		return s1.getQtyOnhand().add(s1.getQtyDiff()).add(s2.getQtyOnhand().add(s2.getQtyDiff()));
	}

	@Override
	public List<LotHis> getWipHisByUsedLot(long orgRrn,
			String lotId, Long materialChildRrn, String otherWhereClause) throws ClientException {
		try{
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT h.object_rrn, ");
			sql.append("		h.mo_id, ");
			sql.append("        h.mo_rrn, ");
			sql.append("        h.material_id, ");
			sql.append("        h.material_rrn, ");
			sql.append("        h.material_name, ");
			sql.append("        h.mo_line_rrn, ");
			sql.append("        h.workcenter_id, ");
			sql.append("        h.qty_current, ");
			sql.append("        h.equipment_id, ");
			sql.append("        h.user_qc, ");
			sql.append("        h.date_product, ");
			sql.append("        h.lot_id, ");
			sql.append("        h.lot_rrn, ");
			sql.append("        h.lot_comment ");
			sql.append("  FROM wiphis_lot h ");
			sql.append("  WHERE h.mo_line_rrn IN (SELECT c.mo_line_rrn ");
			sql.append("                            FROM wip_lot_component c ");
			sql.append(" 							WHERE ");
			if(lotId != null && lotId.trim().length() > 0){
				sql.append(" c.lot_child_id = ? AND c.org_rrn = ? ");
				if(materialChildRrn != null){
					sql.append(" AND c.material_child_rrn = ? ");
				}
			}else{
				if(materialChildRrn != null){
					sql.append(" c.material_child_rrn = ? ");
				}else{
					sql.append(" 1<>1 ");
				}
			}
			sql.append(" )        AND ");
			sql.append(ADBase.SQL_BASE_CONDITION);
			sql.append(" 		AND h.trans_type = 'RECEIVE' ");
			
			if(otherWhereClause != null && otherWhereClause.trim().length()>0){
				sql.append(otherWhereClause);
			}
			
			Query query = em.createNativeQuery(sql.toString());
			if(lotId != null && lotId.trim().length() > 0){
				query.setParameter(1, lotId);
				query.setParameter(2, orgRrn);
				if(materialChildRrn != null){
					query.setParameter(3, materialChildRrn);
					query.setParameter(4, orgRrn);
				}else{
					query.setParameter(3, orgRrn);
				}
			}else{
				if(materialChildRrn != null){
					query.setParameter(1, materialChildRrn);
					query.setParameter(2, orgRrn);
				}else{
					query.setParameter(1, orgRrn);
				}
			}
			List<Object[]> rs = query.getResultList();
			List<LotHis> hisLots = new ArrayList<LotHis>();
			for(Object[] objs : rs){
				LotHis his = new LotHis();
				his.setObjectRrn(((BigDecimal)objs[0]).longValue());
				his.setMoId((String)objs[1]);
				his.setMoRrn(((BigDecimal)objs[2]).longValue());
				his.setMaterialId((String)objs[3]);
				his.setMaterialRrn(((BigDecimal)objs[4]).longValue());
				his.setMaterialName((String)objs[5]);
				his.setMoLineRrn(((BigDecimal)objs[6]).longValue());
				his.setWorkCenterId((String)objs[7]);
				his.setQtyCurrent((BigDecimal)objs[8]);
				his.setEquipmentId((String)objs[9]);
				his.setUserQc((String)objs[10]);
				his.setDateProduct((Date)objs[11]);
				his.setLotId((String)objs[12]);
				his.setLotRrn(((BigDecimal)objs[13]).longValue());
				his.setLotComment((String)objs[14]);
				hisLots.add(his);
			}
			return hisLots;
		}catch(Exception e){
			throw new ClientException(e);
		}
	}
	
	@Override
	public List<Lot> getGenLotIds(Lot lot) throws ClientException{
		try{
			StringBuffer hql = new StringBuffer();
			hql.append(" from Lot lot ");
			hql.append(" where lot.position = 'GEN' ");
			hql.append(" and lot.materialRrn = " + lot.getMaterialRrn());
			
			Query query = em.createQuery(hql.toString());
			return query.getResultList();
		}catch(Exception e){
			throw new ClientException(e);
		}
	}
	
	@Override
	public List<Lot> getOutStorageLot(Lot lot) throws ClientException{
		try{
			StringBuffer hql = new StringBuffer();
			hql.append(" from Lot lot ");
			hql.append(" where lot.position = 'OUT' ");
			hql.append(" and lot.lotId = " + lot.getLotId());
			hql.append(" and lot.orgRrn = " + lot.getOrgRrn());
			
			Query query = em.createQuery(hql.toString());
			return query.getResultList();
		}catch(Exception e){
			throw new ClientException(e);
		}
	}

	@Override
	public List<ManufactureOrderLine> getCanMergeMoLines(ManufactureOrderLine moLine) throws ClientException {
		try{
			if(moLine == null || moLine.getObjectRrn() == null){
				return new ArrayList<ManufactureOrderLine>();
			}
			
			Long moRrn = moLine.getMasterMoRrn();
			//�������ж��ӹ��������Ϻ��������������Ƿ�һ�������һ����������
			//��������Ƚ����⣬��Ϊ�ֶ��������ӹ�����û�������������moLine.getMasterMoRrn()���Ǵ���������objectRrn
			if(moRrn != null && !moRrn.equals(moLine.getObjectRrn())){
				//��������������Ҫ�ж��ӹ�����������Ƿ������������������ͬ������ͬ����Ϊ����Ʒ�����ɺϲ������ɳ�����������ǣ���ɳ������ϲ���
				ManufactureOrder mo = new ManufactureOrder();
				mo.setObjectRrn(moRrn);
				mo = (ManufactureOrder) adManager.getEntity(mo);
				if(moLine.getMaterialRrn().equals(mo.getMaterialRrn())){
					//�ӹ���������������������������ͬ,����Ʒ�����ɺϲ�
					return new ArrayList<ManufactureOrderLine>();
				}
			}
			//���û���������˵�����ֹ��������ӹ�����ɺϲ����ɳ�����������ͬ���ϵ��ӹ������״̬��approved�ģ����ɺϲ�
			Long workcenterRrn = moLine.getWorkCenterRrn();
			Long materialRrn = moLine.getMaterialRrn();
			
			StringBuffer hql = new StringBuffer();
			hql.append(" from ManufactureOrderLine ml ");
			hql.append(" where " + ADBase.BASE_CONDITION);
			hql.append(" and ml. workCenterRrn =? ");
			hql.append(" and ml.materialRrn = ? ");
			hql.append(" and ml.lineStatus = ? ");
			hql.append(" and ml.objectRrn <> ? ");
			hql.append(" and not exists ( ");
			hql.append(" from ManufactureOrder mo ");
			hql.append(" where mo.objectRrn = ml.masterMoRrn ");
			hql.append(" and mo.materialRrn = ml.materialRrn ");
			hql.append(") ");
			
			
			
			Query query = em.createQuery(hql.toString());
			query.setParameter(1, moLine.getOrgRrn());
			query.setParameter(2, workcenterRrn);
			query.setParameter(3, materialRrn);
			query.setParameter(4, DocumentationLine.LINESTATUS_APPROVED);
			query.setParameter(5, moLine.getObjectRrn());
			
			List list = query.getResultList();
			
			List<ManufactureOrderLine> returnList = new ArrayList<ManufactureOrderLine>();
			// �˴��ж�һ���ӹ������Bom�ṹ�Ƿ�һ�� һ�µĲſɺϲ�
			List<ManufactureOrderBom> boms1 = getMoLineBom(moLine.getObjectRrn());
			if(list != null && list.size() > 0){
				for(Object o : list){
					if(o instanceof  ManufactureOrderLine){
						ManufactureOrderLine mol = (ManufactureOrderLine)o;
						List<ManufactureOrderBom> boms2 = getMoLineBom(mol.getObjectRrn());
						//���ѭ���Ƚ�BOM�ṹ
						int i = 0;
						boolean flag = true;
						for(ManufactureOrderBom bom1 : boms1){
							long materialRrn1 = bom1.getMaterialRrn();
							BigDecimal qtyUnit1 = bom1.getUnitQty();
							ManufactureOrderBom bom2 = boms2.get(i++);
							long materialRrn2 = bom2.getMaterialRrn();
							BigDecimal qtyUnit2 = bom2.getUnitQty();
							if(materialRrn1 != materialRrn2){
								flag = false;
								break;
							}else if(qtyUnit1.compareTo(qtyUnit2) != 0){
								flag = false;
								break;
							}
						}
						
						if(flag){
							returnList.add(mol);
						}
					}else{
						continue;
					}
				}
			}
			
			return returnList;
		}catch (Exception e) {
			throw new ClientException(e);
		}
	}

	@Override
	public void mergeMoLines(ManufactureOrderLine srcLine, List<ManufactureOrderLine> dstLines, Long userRrn) throws ClientException {
		try {
			BigDecimal newQty = BigDecimal.ZERO;//�µ��ӹ�������������
			Date now = new Date();
			
			ManufactureOrderLine newMoLine = new ManufactureOrderLine();
			newMoLine.setIsActive(true);
			newMoLine.setOrgRrn(srcLine.getOrgRrn());
			newMoLine.setCreated(now);
			newMoLine.setCreatedBy(userRrn);
			newMoLine.setUpdatedBy(userRrn);
			newMoLine.setUpdated(now);
			newMoLine.setDateMerge(now);
			newMoLine.setLineNo(10L);
			newMoLine.setMaterialRrn(srcLine.getMaterialRrn());
			newMoLine.setMaterialName(srcLine.getMaterialName());
			newMoLine.setUomId(srcLine.getUomId());
			newMoLine.setDescription(srcLine.getDescription());//������Ϣ�û����ܻ���Ҫ�޸ģ��ŵ�srcLine����ʱ������
			newMoLine.setWorkCenterRrn(srcLine.getWorkCenterRrn());
			newMoLine.setLineStatus(DocumentationLine.LINESTATUS_APPROVED);
			newMoLine.setDateStart(srcLine.getDateStart());//�û����ܻ���Ҫ�޸���ʼ���ڣ��ŵ�srcLine����ʱ������
			newMoLine.setDateEnd(srcLine.getDateEnd());//�û����ܻ���Ҫ�޸���ʼ���ڣ��ŵ�srcLine����ʱ������
			em.persist(newMoLine);
			
			dstLines.add(srcLine);
			
			for(ManufactureOrderLine moLine : dstLines){
				if (ManufactureOrderLine.WORK_STATUS_RUNNING.equals(moLine.getWorkStatus())) {
					throw new ClientParameterException("wip.can_not_close_at_running", moLine.getMaterialId());
				}
				
				if(moLine.getObjectRrn() == null){
					addMoLine(moLine, userRrn);
				}
				
				moLine.setLineStatus(Documentation.STATUS_MERGED);
				moLine.setWorkStatus(ManufactureOrderLine.WORK_STATUS_MERGED);
				moLine.setUpdated(now);
				moLine.setUpdatedBy(userRrn);
				moLine.setMergeBy(userRrn);
				moLine.setDateMerge(now);
				moLine.setMergeNewRrn(newMoLine.getObjectRrn());
				moLine = em.merge(moLine);
				
				
				long transSeq = basManager.getHisSequence();
				MergeMoLineHis his = new MergeMoLineHis(moLine);
				his.setHisSeq(transSeq);
				his.setUpdatedBy(userRrn);
				em.persist(his);
				
				BigDecimal qtyReceive = moLine.getQtyReceive() == null ? BigDecimal.ZERO : moLine.getQtyReceive();
				newQty = newQty.add(moLine.getQty().subtract(qtyReceive));
				List<ManufactureOrderBom> moBoms = getMoLineBom(moLine.getObjectRrn()); 
				for (ManufactureOrderBom moBom : moBoms) {
					Material material = em.find(Material.class, moBom.getMaterialRrn());
					if (!material.getIsLotControl()) {
						continue;
					}
					//ȡ���������ϱ����ѷ�����
//					BigDecimal qtyAllocation = material.getQtyAllocation() == null ? BigDecimal.ZERO : material.getQtyAllocation();
//					material.setQtyAllocation(qtyAllocation.subtract(moLine.getQty().subtract(qtyReceive).multiply(moBom.getUnitQty())));//�Ѿ����ܵĲ����ڽ���ʱ���Ѿ����ѷ������п۳��ˣ���������Ҫ�۳����ǳ��Ѿ����ܵ�֮���ʣ�ಿ��
//��					em.merge(material);
			   }
			}
			
			newMoLine.setMasterMoRrn(newMoLine.getObjectRrn());//�ϲ����ɵ����ӹ�������û����������ģ���˽��Լ���objectRrn�������������λ��
			newMoLine.setQty(newQty);
			em.merge(newMoLine);
			
			ManufactureOrderBom parentBom = new ManufactureOrderBom();
			parentBom.setMaterialParentRrn(null);
			parentBom.setMaterialRrn(newMoLine.getMaterialRrn());
			parentBom.setIsActive(true);
			parentBom.setPath(null);
			parentBom.setPathLevel(0L);
			parentBom.setLineNo(10L);
			parentBom.setUnitQty(BigDecimal.ONE);
			parentBom.setQtyBom(BigDecimal.ONE);
			parentBom.setOrgRrn(newMoLine.getOrgRrn());
			parentBom.setQtyNeed(newMoLine.getQty());
			parentBom.setQty(newMoLine.getQty());
			parentBom.setUomId(newMoLine.getUomId());
			parentBom.setMoLineRrn(newMoLine.getObjectRrn());
			parentBom.setDateStart(newMoLine.getDateStart());
			parentBom.setDateEnd(newMoLine.getDateEnd());
			parentBom.setWorkCenterRrn(newMoLine.getWorkCenterRrn());
			parentBom.setMoRrn(newMoLine.getObjectRrn());
			em.persist(parentBom);
			                                                                          
			List<ManufactureOrderBom> moBoms = getMoChildrenBom(newMoLine.getOrgRrn(), newMoLine.getMaterialRrn());
			for (ManufactureOrderBom moBom : moBoms) {
				moBom.setQtyNeed(newMoLine.getQty().multiply(moBom.getUnitQty()));
				moBom.setQty(BigDecimal.ZERO);
				moBom.setPath(newMoLine.getMaterialRrn() + "/");
				moBom.setMoRrn(newMoLine.getObjectRrn());
				em.persist(moBom);
				
				Material material = em.find(Material.class, moBom.getMaterialRrn());	
				if (!material.getIsLotControl()) {
					continue;
				}
				//ȡ���������ϱ����ѷ�����
//				BigDecimal qtyAllocation = material.getQtyAllocation() == null ? BigDecimal.ZERO : material.getQtyAllocation();
//				material.setQtyAllocation(qtyAllocation.add(moBom.getQtyNeed()));
//
//				em.merge(material);
			}
		} catch (ClientParameterException e) {
			throw e;
		}
	}
	
	@Override
	public List<ManufactureOrderLine> findMoLinesByChildMaterial(long orgRrn, long materialRrn) throws ClientException {
		try{
			StringBuffer sql = new StringBuffer();
			sql.append(" select path from wip_mo_bom ");
			sql.append(" where " + ADBase.SQL_BASE_CONDITION);
			sql.append(" and material_rrn = ? ");
			sql.append(" and path is not null ");
			
			Query query  = em.createNativeQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, materialRrn);
			
			List objList = query.getResultList();
			Set<String> rootPathList =  new HashSet<String>();
			for(Object o : objList){
				if(o instanceof String){
					String path = (String)o;
					String[] splitStrs = path.split("/");
					rootPathList.add(splitStrs[0]);
				}
			}
			
			String materialRrnIns = null;
			int i=0;
			for(String str : rootPathList){
				if(materialRrnIns == null){
					materialRrnIns = "" + str;
				}else{
					materialRrnIns += ", " + str;
				}
			}
			
			StringBuffer sql2 = new StringBuffer("");
			sql2.append(" select ml.* from wip_mo_line ml ");
			sql2.append(" where ml.object_rrn in ( ");
			sql2.append(" select b.mo_line_rrn from wip_mo_bom b ");
			sql2.append(" where " + ADBase.SQL_BASE_CONDITION);
			sql2.append(" and b.material_rrn in (" + materialRrnIns + ") ");
			sql2.append(" and b.mo_line_rrn is not null ");
			sql2.append(" ) ");
			sql2.append(" and ml.line_status in ('DRAFTED','APPROVED') ");
			
			Query query2 = em.createNativeQuery(sql2.toString(), ManufactureOrderLine.class);
			query2.setParameter(1, orgRrn);
			
			List returnList = query2.getResultList();
			return returnList;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public WCTMovement saveWCTMovementLine(WCTMovement wctMovement, WCTMovementLine wctMovementLine, long orgRrn, long userRrn) throws ClientException{
		try {
			List<WCTMovementLine> lines = new ArrayList<WCTMovementLine>();
			lines.add(wctMovementLine);
			return saveWCTMovementLine(wctMovement, lines, orgRrn, userRrn);
		} catch (ClientException e) {
			throw e;
		}
	}
	
	@Override
	public WCTMovement saveWCTMovementLine(WCTMovement wctMovement,List<WCTMovementLine> wctMovementLines, long orgRrn,long userRrn) throws ClientException{
		try{
			Date now = new Date();
			
			if(wctMovement == null){
				throw new ClientException("WCTMovement is null");
			}
			
			long srcWorkCenterRrn = wctMovement.getSrcWorkcenterRrn();
			long desWorkCenterRrn = wctMovement.getDesWorkcenterRrn();
			
			WorkCenter srcWc = em.find(WorkCenter.class, srcWorkCenterRrn);
			WorkCenter desWc = em.find(WorkCenter.class, desWorkCenterRrn);
			
			if(srcWc == null){
				throw new ClientException("Invalid source workcenter");
			}
			
			if(desWc == null){
				throw new ClientException("Invalid destination workcenter");
			}
			
			wctMovement.setSrcWorkcenterName(srcWc.getName());
			wctMovement.setDesWorkcenterName(desWc.getName());
			
			if(wctMovement.getObjectRrn() == null){
				wctMovement.setOrgRrn(orgRrn);
				wctMovement.setIsActive(true);
				wctMovement.setCreated(now);
				wctMovement.setCreatedBy(userRrn);
				wctMovement.setUpdated(now);
				wctMovement.setUpdatedBy(userRrn);
				wctMovement.setDateMovement(now);
				
				wctMovement.setDocStatus(WCTMovement.DOC_STATUS_DRAFTED);
				wctMovement.setDocType(WCTMovement.DOC_TYPE_WCTM);
				String docId = generateWCTMCode(wctMovement);
				wctMovement.setDocId(docId);
				
				em.persist(wctMovement);
			}
			
			for(WCTMovementLine line : wctMovementLines){
				if(line==null){
					throw new ClientException("WCTMovementLine is null");
				}
				
				line.setIsActive(true);
				line.setOrgRrn(orgRrn);
				line.setCreated(now);
				line.setCreatedBy(userRrn);
				line.setUpdated(now);
				line.setUpdatedBy(userRrn);
				line.setMovementRrn(wctMovement.getObjectRrn());
				long materialRrn = line.getMaterialRrn();
				Material material = em.find(Material.class, materialRrn);
				
				if(material == null){
					throw new ClientException("Invalid material");
				}
				
				line.setMaterialId(material.getMaterialId());
				line.setMaterialName(material.getName());
				
				if(line.getObjectRrn() == null){
					em.persist(line);
					wctMovement.setUpdated(now);
					wctMovement.setUpdatedBy(userRrn);
					wctMovement.setTotalLines(wctMovement.getTotalLines()+1);
					em.merge(wctMovement);
				}else{
					em.merge(line);
				}
			}
			return wctMovement;
		}catch(Exception e){
			throw new ClientException(e);
		}
	}
	
	@Override
	public void deleteWCTMovement(WCTMovement wctMovement, long userRrn)throws ClientException {
		try{
			if(wctMovement != null && wctMovement.getObjectRrn() != null) {
				wctMovement = em.find(WCTMovement.class, wctMovement.getObjectRrn());
				List<WCTMovementLine> WCTMovementLines = wctMovement.getWCTMovementLines();
				int size = WCTMovementLines.size();
				for(int i = 0; i < size; i++) {
					WCTMovementLine WCTMovementLine = WCTMovementLines.get(i);
					deleteWCTMovementLine(WCTMovementLine, userRrn);
				}
				em.remove(wctMovement);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void deleteWCTMovementLine(WCTMovementLine wctMovementLine, long userRrn)throws ClientException {
		try {
			if(wctMovementLine != null && wctMovementLine.getObjectRrn() != null) {
				wctMovementLine = em.find(WCTMovementLine.class, wctMovementLine.getObjectRrn());
				em.remove(wctMovementLine);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	@Override
	public WCTMovementLine newMovementLine(WCTMovement wctmovement) throws ClientException {
		WCTMovementLine WCTMovementLine = new WCTMovementLine();
		try{
			if (wctmovement != null && wctmovement.getObjectRrn() != null) {
				wctmovement = em.find(WCTMovement.class, wctmovement.getObjectRrn());
				long maxLineNo = 1;
				for (WCTMovementLine line : wctmovement.getWCTMovementLines()) {
					maxLineNo = maxLineNo < line.getLineNo() ? line.getLineNo() : maxLineNo;
				}
				WCTMovementLine.setLineNo((long)Math.ceil(maxLineNo / 10) * 10 + 10);
			} else {
				WCTMovementLine.setLineNo(10L);
			}
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return WCTMovementLine;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List getEquipmentByWorkCenter(long orgRrn, long objecrRrn) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT e.object_rrn,e.equipment_name FROM wip_workcenter_equipment w,wip_equipment e");
		sql.append(" WHERE " + ADBase.SQL_BASE_CONDITION);
		sql.append(" AND e.object_rrn = w.equipment_rrn AND w.workcenter_rrn = ? "); 
		logger.debug(sql);
		try {
				Query query = em.createNativeQuery(sql.toString());
				query.setParameter(1, orgRrn);
				query.setParameter(2, objecrRrn);
				List<Object[]> eqp_Rrn = query.getResultList();
				return eqp_Rrn;
		} catch (NoResultException e){
			return null;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new JbpmException(e);
		}
	}
	
	@Override
	public void saveWCTMovementLineLots(List<WCTMovementLine> wctMovementLines, List<WCTMovementLineLot> lineLots, Long userRrn) throws ClientException {
		try {
			Date now = new Date();
			for(WCTMovementLine line : wctMovementLines){
				List<WCTMovementLineLot> currentLots = new ArrayList<WCTMovementLineLot>();
				BigDecimal totalLotQty = BigDecimal.ZERO;
				for(WCTMovementLineLot lineLot : lineLots){
					if(lineLot.getMaterialRrn().equals(line.getMaterialRrn())){
						lineLot.setOrgRrn(line.getOrgRrn());
						lineLot.setIsActive(true);
						lineLot.setMovementRrn(line.getMovementRrn());
						lineLot.setMovementLineRrn(line.getObjectRrn());
						totalLotQty = totalLotQty.add(lineLot.getQtyMovement());
						currentLots.add(lineLot);
					}
				}
				
				if(totalLotQty.compareTo(line.getQtyMovement()) != 0){
					throw new ClientException("��������");
				}
				
				for(WCTMovementLineLot lineLot : currentLots){
					if(lineLot != null && lineLot.getObjectRrn() != null){
						lineLot.setUpdated(now);
						lineLot.setUpdatedBy(userRrn);
						lineLot = em.merge(lineLot);
					}else{
						lineLot.setCreated(now);
						lineLot.setCreatedBy(userRrn);
						lineLot.setUpdated(now);
						lineLot.setUpdatedBy(userRrn);
						em.persist(lineLot);
					}
				}
			}
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}
	
	@Override
	public void saveWCTMovementLineLots(WCTMovement wctMovement, List<WCTMovementLineLot> lineLots, Long userRrn) throws ClientException {
		wctMovement = (WCTMovement) adManager.getEntity(wctMovement);
		List<WCTMovementLine> lines = wctMovement.getWCTMovementLines();
		this.saveWCTMovementLineLots(lines, lineLots, userRrn);
	}

	@Override
	public void saveWCTMovementLineLots(WCTMovementLine wctMovementLine, List<WCTMovementLineLot> lineLots, Long userRrn) throws ClientException {
		List<WCTMovementLine> wctMovementLines = new ArrayList<WCTMovementLine>();
		wctMovementLines.add(wctMovementLine);
		this.saveWCTMovementLineLots(wctMovementLines, lineLots, userRrn);
	}
	
	private WCTLotStorage getWCTLotStorage(WCTMovementLineLot lot, Long workcenterRrn) throws ClientException{
		try {
			Date now = new Date();
			StringBuffer hql = new StringBuffer();
			hql.append(" FROM WCTLotStorage storage ");
			hql.append(" WHERE " + ADBase.BASE_CONDITION);
			hql.append(" and storage.lotRrn = ? ");
			hql.append(" and storage.workCenterRrn = ? ");
			
			Query query = em.createQuery(hql.toString());
			query.setParameter(1, lot.getOrgRrn());
			query.setParameter(2, lot.getLotRrn());
			query.setParameter(3, workcenterRrn);
			
			List<?> rslt = query.getResultList();
			if(rslt != null && rslt.size() > 0){
				return (WCTLotStorage) rslt.get(0);
			}else{
				WorkCenter wc = new WorkCenter();
				wc.setObjectRrn(workcenterRrn);
				wc = (WorkCenter) adManager.getEntity(wc);
				
				WCTLotStorage lotStorage = new WCTLotStorage();
				lotStorage.setOrgRrn(lot.getOrgRrn());
				lotStorage.setIsActive(true);
				lotStorage.setCreated(now);
				lotStorage.setLotRrn(lot.getLotRrn());
				lotStorage.setLotId(lot.getLotId());
				lotStorage.setMaterialRrn(lot.getMaterialRrn());
				lotStorage.setMaterialId(lot.getMaterialId());
				lotStorage.setMaterialName(lot.getMaterialName());
				lotStorage.setUpdated(now);
				lotStorage.setWorkCenterRrn(wc.getObjectRrn());
				lotStorage.setWorkCenterId(wc.getName());
				lotStorage.setQtyInitial(BigDecimal.ZERO);
				lotStorage.setQtyCurrent(BigDecimal.ZERO);
				
				em.persist(lotStorage);
				return lotStorage;
			}
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}
	
	private WCTLotStorage updateWCTLotStorage(WCTMovementLineLot lot, Long workcenterRrn, BigDecimal qty) throws ClientException{
		try {
			Date now = new Date();
			WCTLotStorage lotStorage = getWCTLotStorage(lot, workcenterRrn);
			lotStorage.setQtyCurrent(lotStorage.getQtyCurrent().add(qty));
			lotStorage.setUpdated(now);
			em.merge(lotStorage);
			
			return lotStorage;
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}
	
	@Override
	public WCTMaterialStorage getWCTMaterialStorage(Material material, long workcenterRrn) throws ClientException{
		try {
			Date now = new Date();
			StringBuffer hql = new StringBuffer();
			hql.append(" FROM WCTMaterialStorage storage ");
			hql.append(" WHERE " + ADBase.BASE_CONDITION);
			hql.append(" AND storage.workcenterRrn = ? ");
			hql.append(" AND storage.materialRrn = ? ");
			
			Query query = em.createQuery(hql.toString());
			query.setParameter(1, material.getOrgRrn());
			query.setParameter(2, workcenterRrn);
			query.setParameter(3, material.getObjectRrn());
			
			List<?> rslt = query.getResultList();
			if(rslt != null && rslt.size() > 0){
				return (WCTMaterialStorage) rslt.get(0);
			}else{
				material = (Material) adManager.getEntity(material);
				
				WorkCenter wc = new WorkCenter();
				wc.setObjectRrn(workcenterRrn);
				wc = (WorkCenter) adManager.getEntity(wc);
				
				WCTMaterialStorage storage = new WCTMaterialStorage();
				storage.setOrgRrn(material.getOrgRrn());
				storage.setIsActive(true);
				storage.setCreated(now);
				storage.setMaterialRrn(material.getObjectRrn());
				storage.setMaterialId(material.getMaterialId());
				storage.setMaterialName(material.getName());
				storage.setUpdated(now);
				storage.setWorkcenterRrn(wc.getObjectRrn());
				storage.setWorkcenterName(wc.getName());
				storage.setQty(BigDecimal.ZERO);
				
				em.persist(storage);
				
				return storage;
			}
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}
	
	@Override
	public WCTMaterialStorage updateWCTMaterialStorage(Material material, long workcenterRrn, BigDecimal qty) throws ClientException{
		try {
			Date now = new Date();
			material = (Material) adManager.getEntity(material);
			WCTMaterialStorage storage = getWCTMaterialStorage(material, workcenterRrn);
			
			storage.setQty(storage.getQty().add(qty));
			storage.setUpdated(now);
			
			em.merge(storage);
			return storage;
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}

	@Override
	public void approveWCTMovement(WCTMovement movement, long userRrn) throws ClientException {
		try {
			Date now = new Date();
			movement.setDocStatus(WCTMovement.DOC_STATUS_APPROVED);
			movement.setDateApprove(now);
			movement.setApproveBy(userRrn);
			em.merge(movement);
			
			List<WCTMovementLine> lines = movement.getWCTMovementLines();
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT WCTMovementLineLot FROM WCTMovementLineLot WCTMovementLineLot ");
			sql.append(" WHERE ");
			sql.append(" WCTMovementLineLot.movementLineRrn =?");
			for(WCTMovementLine line : lines){
				Query query = em.createQuery(sql.toString());
				query.setParameter(1, line.getObjectRrn());
				List<WCTMovementLineLot> wctMovementLineLots =query.getResultList();
				
				Material material = new Material();
				material.setObjectRrn(line.getMaterialRrn());
				material = (Material) adManager.getEntity(material);
				
				//Դ�ֿ����Ͽ�����
				updateWCTMaterialStorage(material, movement.getSrcWorkcenterRrn(), line.getQtyMovement().negate());
				//Ŀ��ֿ����Ͽ������
				updateWCTMaterialStorage(material, movement.getDesWorkcenterRrn(), line.getQtyMovement());
				
				for(WCTMovementLineLot lot : wctMovementLineLots){
					//Դ�ֿ���������
					updateWCTLotStorage(lot, movement.getSrcWorkcenterRrn(), lot.getQtyMovement().negate());
					//Ŀ��ֿ����������
					updateWCTLotStorage(lot, movement.getDesWorkcenterRrn(), lot.getQtyMovement());
				}
			}
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}
	
	private String generateWCTMCode(WCTMovement movement) {
		StringBuffer moCode = new StringBuffer("");
		moCode.append(basManager.generateCodePrefix(movement.getOrgRrn(), movement.getDocType()));
		moCode.append(basManager.generateCodeSuffix(movement.getOrgRrn(), movement.getDocType(), movement.getCreated()));
		return moCode.toString();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ManufactureOrderLine> getCanDissolveMoLines(ManufactureOrderLine moLine) throws ClientException {
		try{
			if(moLine == null || moLine.getObjectRrn() == null){
				return new ArrayList<ManufactureOrderLine>();
			}
			
			Long workcenterRrn = moLine.getWorkCenterRrn();
			Long materialRrn = moLine.getMaterialRrn();
			
			StringBuffer hql = new StringBuffer();
			hql.append(" from ManufactureOrderLine ml ");
			hql.append(" where ml.workCenterRrn =? ");
			hql.append(" and ml.materialRrn = ? ");
			hql.append(" and ml.lineStatus = ? ");
			hql.append(" and ml.mergeNewRrn = ? ");
			
			
			Query query = em.createQuery(hql.toString());
			query.setParameter(1, workcenterRrn);
			query.setParameter(2, materialRrn);
			query.setParameter(3, ManufactureOrderLine.WORK_STATUS_MERGED);
			query.setParameter(4, moLine.getObjectRrn());
			
			List list = query.getResultList();
			List<ManufactureOrderLine> returnList = new ArrayList<ManufactureOrderLine>();
			if(list != null && list.size() > 0){
				for(Object o : list){
					if(o instanceof  ManufactureOrderLine){
						ManufactureOrderLine mol = (ManufactureOrderLine)o;
						returnList.add(mol);
					}else{
						continue;
					}
				}
			}
			
			return returnList;
		}catch (Exception e) {
			throw new ClientException(e);
		}
	}

	@Override
	public void dissolveMoLines(ManufactureOrderLine srcLine, List<ManufactureOrderLine> dstLines, Long userRrn) throws ClientException {
		try {
			Date now = new Date();
			srcLine.setLineStatus(Documentation.STATUS_CLOSED);
			srcLine.setUpdated(now);
			srcLine.setUpdatedBy(userRrn);
			srcLine.setDateUnMerge(now);
			srcLine.setUnMergeBy(userRrn);
			em.merge(srcLine);		
			
			for(ManufactureOrderLine moLine : dstLines){
				if (ManufactureOrderLine.WORK_STATUS_RUNNING.equals(moLine.getWorkStatus())) {
					throw new ClientParameterException("wip.can_not_close_at_running", moLine.getMaterialId());
				}
				moLine.setLineStatus(Documentation.STATUS_APPROVED);
				moLine.setWorkStatus(null); 
				moLine.setUpdated(now);
				moLine.setUpdatedBy(userRrn);
				moLine.setDateUnMerge(now);
				moLine.setUnMergeBy(userRrn);
				moLine = em.merge(moLine);
				
				long transSeq = basManager.getHisSequence();
				UnMergeMoLineHis his = new UnMergeMoLineHis(moLine);
				his.setHisSeq(transSeq);
				his.setUpdatedBy(userRrn);
				his.setDateUnMerge(now);
				em.persist(his);
			}
		} catch (ClientParameterException e) {
			throw e;
		}
	}
	
	@Override
	public Boolean validateDelete(Lot lot, long orgRrn, long userRrn) throws ClientException {
        StringBuffer hql = new StringBuffer();
        hql.append(" select * from LARGE_WIP_LOT t ");
        hql.append(" where t.lot_rrn = ? ");
        
		Query query = em.createNativeQuery(hql.toString());
		query.setParameter(1, lot.getObjectRrn());
		
		List list = query.getResultList();
		if(list.size() != 0){
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public void packageLots(LargeLot largerlot, List<LargeWipLot> lwLots, long orgRrn, long userRrn) throws ClientException {
		try {
			Date now = new Date();
			largerlot.setOrgRrn(orgRrn);
            largerlot.setIsActive( true);
            largerlot.setCreatedBy(userRrn);
            largerlot.setUpdatedBy(userRrn);
            largerlot.setCreated(now);
            largerlot.setUpdated(now);
            
            Long materialRrn = largerlot.getMaterialRrn();
            Material material = em.find(Material.class, materialRrn);
            
            largerlot.setMaterialId(material.getMaterialId());
            String lotId = generateLargeLotId(orgRrn, largerlot.getMaterialId());
            largerlot.setLotId(lotId);

            if(largerlot.getObjectRrn() != null){
            	em.merge(largerlot);
            }else{
            	em.persist(largerlot);
            }
             
            List<LargeWipLot> filtList = new ArrayList<LargeWipLot>();
            
            Map<Long,LargeWipLot> maps = new HashMap<Long,LargeWipLot>();
            for (LargeWipLot bean : lwLots) {
            	Long key = bean.getLotRrn();
            	if(maps.containsKey(key)){
            		LargeWipLot val = maps.get(key);
            		val.setQty(val.getQty().add(bean.getQty()));
            	}else{
            		maps.put(key, bean);
            	}
            }  
            
            filtList.addAll(maps.values());
            
            StringBuffer inClause = new StringBuffer();
			for(LargeWipLot largerwiplot : filtList){
				largerwiplot.setOrgRrn(orgRrn);
				largerwiplot.setIsActive( true);
				largerwiplot.setCreatedBy(userRrn);
				largerwiplot.setUpdatedBy(userRrn);
				largerwiplot.setCreated(now);
				largerwiplot.setUpdated(now);
				largerwiplot.setLargeLotRrn(largerlot.getObjectRrn());
				Lot lot = largerwiplot.getLot();
				lot.setIsPackage(true);
				em.merge(lot);
				
				if(largerwiplot.getObjectRrn() != null){
					em.merge(largerwiplot);
				}else{
					em.persist(largerwiplot);
				}
								
				inClause.append(largerwiplot.getObjectRrn()+",");
			}
			
			 //ɾ��
			StringBuffer lotRrnClause = new StringBuffer();
            StringBuffer hql = new StringBuffer();
            hql.append(" FROM LargeWipLot LargeWipLot ");
            hql.append(" WHERE LargeWipLot.largeLotRrn = ? ");
            if(inClause.toString().trim().length() > 0){
            	hql.append(" and LargeWipLot.objectRrn not in ( "+ inClause.substring(0,inClause.length()-1) +")");
            }
            
			Query query = em.createQuery(hql.toString());
			query.setParameter(1, largerlot.getObjectRrn());
			
			List lstToDel = query.getResultList();
			for(Object obj : lstToDel){
				LargeWipLot lwl = (LargeWipLot)obj;
				lotRrnClause.append(lwl.getLotRrn()+",");
			}
			query = em.createQuery("DELETE " + hql.toString());
			query.setParameter(1, largerlot.getObjectRrn());
			query.executeUpdate();
			
			
			//��ԭwip_lot
			if(lotRrnClause.toString().trim().length() > 0){
				StringBuffer hql2 = new StringBuffer();
				hql2.append(" update wip_lot lot set lot.is_package = 'N' ");
				hql2.append(" where lot.object_rrn in ( ");
				hql2.append(" select l.object_rrn from wip_lot l ");
				hql2.append(" where l.object_rrn in ( "+ lotRrnClause.substring(0,lotRrnClause.length()-1) +") ");
				hql2.append(" and not exists (select 1 from large_wip_lot lwl where lwl.lot_rrn = l.object_rrn)) ");
				
				query = em.createNativeQuery(hql2.toString());
				query.executeUpdate();
			}
			
			Boolean lldel = validateLLDelete( largerlot, orgRrn, userRrn );
			if( lldel == false ){
				StringBuffer hql3 = new StringBuffer();
				hql3.append(" DELETE FROM LargeLot LargeLot WHERE LargeLot.objectRrn = ? ");
				Query query2 = em.createQuery(hql3.toString());
				query2.setParameter(1, largerlot.getObjectRrn());
				query2.executeUpdate();
			}
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}
	
	private String generateLargeLotId(Long orgRrn, String materialId) throws ClientException{
		try {
			String dateSuffix = basManager.getCurrentDateCode(orgRrn);
			
			long seq = adManager.getNextSequence(orgRrn, materialId + dateSuffix);
			String seqSuffix = String.format("%04d", seq);
			return "LL"+materialId + dateSuffix + seqSuffix;
		} catch (Exception e) {
			throw new ClientException(e);
		}
	}
	
	@Override
	public Boolean validateLLDelete(LargeLot ll, long orgRrn, long userRrn) throws ClientException {
        StringBuffer hql = new StringBuffer();
        hql.append(" select * from LARGE_WIP_LOT t ");
        hql.append(" where t.large_lot_rrn = ? ");
        
		Query query = em.createNativeQuery(hql.toString());
		query.setParameter(1, ll.getObjectRrn());
		
		List list = query.getResultList();
		if(list.size() != 0){
			return true;
		}else{
			return false;
		}
	}
	
//	@Override
//	public List<TempManufactureOrderBom> generateTmpMoBomDetail(
//			TempManufactureOrder tmpMo, List<TempManufactureOrderBom> tmpMoBoms, boolean batchFlag) throws ClientException {
//		try {
//			Map<String, BusinessCalendar> moCalendarMap = new HashMap<String, BusinessCalendar>();
//			BusinessCalendar prCalendar = basManager.getCalendarByDay(tmpMo.getOrgRrn(), BusinessCalendar.CALENDAR_PURCHASE);
//			List<String> weekTypes = basManager.getWeekType(tmpMo.getOrgRrn());
//			for (String weekType : weekTypes) {
//				BusinessCalendar moCalendar = basManager.getCalendarByDayHours(tmpMo.getOrgRrn(), BusinessCalendar.CALENDAR_MANUFACTURE, weekType);
//				moCalendarMap.put(weekType, moCalendar);
//			}
//			
//			int level = 0;
//			List<TempManufactureOrderBom> completeBoms = new ArrayList<TempManufactureOrderBom>();
//			//��һ��ѭ��,��BOM��߲㿪ʼ������������
//			while (true) {
//				List<TempManufactureOrderBom> filterMoBoms = filterTmpMoBom(tmpMoBoms, level);
//				if (filterMoBoms == null || filterMoBoms.size() == 0) {
//					break;
//				}
//				for (TempManufactureOrderBom moBom : filterMoBoms) {
//					TempManufactureOrderBom parentBom = getParentTmpMoBom(tmpMoBoms, moBom);
//					//������������������������
//					//����������ָҪ����һ�������ĸ���������Ҫ��ָ��������(��������=����������*��λ����)
//					//������������ָҪ����������������Ҫ�������������������������������桢��;�����Ƶ������Ľ��
//					if (parentBom != null) {
//						//����������ǲɹ�����Ҫ������
//						if (parentBom.getIsMaterialNeed()) {
//							moBom.setQtyNeed(BigDecimal.ZERO);
//						} else {
//							moBom.setQtyNeed(parentBom.getQty().multiply(moBom.getUnitQty()));
//						}
//						moBom = generateTmpQtyProduct(prCalendar, tmpMo, moBom, completeBoms, batchFlag);
//					} else {
//						//parentBomΪnull��ʾΪMO������������
//						moBom.setIsProduct(true);
//						moBom.setIsMaterialNeed(false);
//						moBom.setQtyNeed(tmpMo.getQtyProduct());
//						moBom.setQty(tmpMo.getQtyProduct());
//						MaterialSum materialSum = getMaterialSum(moBom.getOrgRrn(), moBom.getMaterialRrn(), batchFlag, true);
//						if(materialSum == null) {
//							throw new ClientParameterException("wip.material_is_not_lot_control_or_mrp", moBom.getMaterialId());
//						}
//						moBom = generateTmpDuration(moBom, materialSum);
//						moBom.setIqcLeadTime(materialSum.getIqcLeadTime());
//						moBom.setStandTime(materialSum.getStandTime());
//					}
//					completeBoms.add(moBom);
//				}
//				level++;
//			}
//			
//			//�ڶ���ѭ��,����ײ㿪ʼ������ù�ʱ
//			level--;
//			completeBoms = new ArrayList<TempManufactureOrderBom>();
//			while (level >= 0) {
//				List<TempManufactureOrderBom> filterMoBoms = filterTmpMoBom(tmpMoBoms, level);
//				if (filterMoBoms == null || filterMoBoms.size() == 0) {
//					continue;
//				}
//				for (TempManufactureOrderBom moBom : filterMoBoms) {
//					if (moBom.getIsProduct()) {
//						BusinessCalendar moCalendar = getBusinessCalendar(moCalendarMap, moBom.getWeekType());
//						List<TempManufactureOrderBom> childBoms = getChildTmpMoBom(tmpMoBoms, moBom);
//						
//						Date dateStart = moCalendar.add(tmpMo.getDateStart(), new Duration(0 + " " + Duration.BUSINESS_MINUTES));
//						//������MO�Ľ���ʱ�䣬ȷ����MO�Ŀ�ʼʱ��
//						for (TempManufactureOrderBom childBom : childBoms) {
//							//�����MO�Ѿ���ʱ����MO�϶���ʱ
//							if (childBom.getIsDateNeed() && !childBom.getIsCanStart()) {
//								moBom.setIsDateNeed(true);
//								break;
//							}
//							//MO��ʼʱ�������ڴ�MO����MO����ʱ��(�Է���Ϊ��λ)
//							if (childBom.getIsCanStart()) {
//								dateStart = moCalendar.add(dateStart, new Duration(0 + " " + Duration.BUSINESS_MINUTES));
//							} else if (childBom.getDateEnd() == null) {
//								dateStart = moCalendar.add(dateStart, new Duration(0 + " " + Duration.BUSINESS_MINUTES));
//							} else {
//								Date dateEnd = moCalendar.add(childBom.getDateEnd(), new Duration(0 + " " + Duration.BUSINESS_MINUTES));
//								//�����PR����PR�Ľ���ʱ��+��������Ϊ��һ�������յĿ�ʼ
//								if (childBom.getIsMaterialNeed()) {
//									int iqcLeadTime = childBom.getIqcLeadTime() != null ? childBom.getIqcLeadTime().intValue() : 0;
//									dateEnd = moCalendar.addDay(dateEnd, iqcLeadTime);
//									dateEnd = moCalendar.addDay(dateEnd, 1);
//									dateEnd = moCalendar.add(dateEnd, new Duration(0 + " " + Duration.BUSINESS_MINUTES));
//								}
//								if (dateEnd.compareTo(tmpMo.getDateEnd()) > 0) {
//									moBom.setIsDateNeed(true);
//									break;
//								}
//								if (dateStart.compareTo(dateEnd) < 0) {
//									dateStart = dateEnd;
//								}
//							}
//						}
//						
//						if (!moBom.getIsDateNeed()) {
//							moBom.setDateStart(dateStart);
//							//���ҿ��ù�ʱ
//							generateTmpDateEnd(moCalendar, tmpMo, moBom, completeBoms);
//							if (!moBom.getIsDateNeed()) {
//								completeBoms.add(moBom);
//							}
//						}
//					}
//				}
//				level--;
//			}
//			
//			for (TempManufactureOrderBom moBom : tmpMoBoms) {
//				em.persist(moBom);
//			}
//			
//		} catch (ClientParameterException e){ 
//			throw e;
//		} catch (ClientException e){ 
//			throw e;
//		}  catch (Exception e){ 
//			logger.error(e.getMessage(), e);
//			throw new ClientException(e);
//		}
//		return tmpMoBoms;
//	}
//	
//	private List<TempManufactureOrderBom> filterTmpMoBom(List<TempManufactureOrderBom> moBoms, int level) {
//		List<TempManufactureOrderBom> filterMoBoms = new ArrayList<TempManufactureOrderBom>();
//		for (TempManufactureOrderBom moBom : moBoms) {
//			if (moBom.getPathLevel() == level) {
//				filterMoBoms.add(moBom);
//			}
//		}
//		return filterMoBoms;
//	}
//	
//	private List<TempManufactureOrderBom> getChildTmpMoBom(List<TempManufactureOrderBom> moBoms, TempManufactureOrderBom moBom) {
//		List<TempManufactureOrderBom> childBoms = new ArrayList<TempManufactureOrderBom>();
//		long parentRrn = moBom.getMaterialRrn();
//		long childLevel = moBom.getPathLevel() + 1;
//		for (TempManufactureOrderBom childBom : moBoms) {
//			if (childBom.getMaterialParentRrn() != null && 
//					childBom.getMaterialParentRrn() == parentRrn && 
//					childBom.getPath().equals((moBom.getPath() != null ? moBom.getPath() : "") + parentRrn + "/") && 
//					childBom.getPathLevel() == childLevel) {
//				childBoms.add(childBom);
//			}
//		}
//		return childBoms;
//	}
//	
//	public List<DocumentationLine> generateTmpMoLine(TempManufactureOrder mo, List<TempManufactureOrderBom> moBoms, boolean batchFlag, Long userRrn) throws ClientException {
//		List<DocumentationLine> moLines = new ArrayList<DocumentationLine>();
//		try {
//			Warehouse warehouse = invManager.getDefaultWarehouse(mo.getOrgRrn());
//			for (TempManufactureOrderBom moBom : moBoms) {
//				if (moBom.getIsProduct() && moBom.getQty().compareTo(BigDecimal.ZERO) > 0) {
//					TempManufactureOrderLine moLine = new TempManufactureOrderLine();
//					moLine.setOrgRrn(mo.getOrgRrn());
//					moLine.setIsActive(true);
//					moLine.setMaterialRrn(moBom.getMaterialRrn());
//					if (!batchFlag) {
//						Material material = moBom.getMaterial();
//						if (material == null) {
//							material = em.find(Material.class, moBom.getMaterialRrn());
//						}
//						moLine.setMaterial(material);
//						// Add by BruceYou 2012-03-13
//						moLine.setMaterialName(material.getName());
//						moLine.setUomId(material.getInventoryUom());
//					}
//					moLine.setUomId(moBom.getUomId());
//					moLine.setQty(moBom.getQty());
//					moLine.setQtyNeed(moBom.getQtyNeed());
//					moLine.setQtyAllocation(moBom.getQtyAllocation());
//					moLine.setQtyOnHand(moBom.getQtyOnHand());
//					moLine.setWorkCenterRrn(moBom.getWorkCenterRrn());
//					moLine.setDateStart(moBom.getDateStart());
//					moLine.setDateEnd(moBom.getDateEnd());
//					moLine.setPath(moBom.getPath());
//					moLine.setPathLevel(moBom.getPathLevel());
//					moLine.setLineStatus(Documentation.STATUS_DRAFTED);
//					moLine.setMoBomRrn(moBom.getObjectRrn());
//					moLine.setDescription(mo.getComments());//�����������д��뱸ע��Ϣ
//					moLine.setOrderId(mo.getOrderId());//�����������д��붩�������Ϣ
//					moLine.setSalePlanType(mo.getSalePlanType());//�����������д�������������Ϣ
//					moLine.setCustomerName(mo.getCustomerName());//�����������д���ͻ�����Ϣ
//					moLine.setSaler(mo.getSaler());//�����������д���ҵ��Ա��Ϣ
//					moLines.add(moLine);
//				} else if (moBom.getIsMaterialNeed()){
//					TempRequisitionLine	prLine = new TempRequisitionLine();
//					prLine.setOrgRrn(mo.getOrgRrn());
//					prLine.setIsActive(true);
//					prLine.setMaterialRrn(moBom.getMaterialRrn());
//					VendorMaterial vendorMaterial = moBom.getVendorMaterial();
//					if (vendorMaterial == null) {
//						vendorMaterial = vdmManager.getPrimaryVendor(prLine.getMaterialRrn());
//					}
//					if (vendorMaterial == null) {
//						Material material = em.find(Material.class, moBom.getMaterialRrn());
//						throw new ClientParameterException("pur.material_no_primary_vendor", material.getMaterialId());
//					}
////					if (!batchFlag) {
////						//���������С�����͵���������������������ʵ������
////						BigDecimal qtyMPS = moBom.getQty();
////						BigDecimal qtyTheory = getQtyTheory(qtyMPS, vendorMaterial.getLeastQuantity(), vendorMaterial.getIncreaseQuantity());
////						BigDecimal qty = qtyTheory;
////						prLine.setVendorRrn(vendorMaterial.getVendorRrn());
////						prLine.setQtyEconomicSize(vendorMaterial.getLeastQuantity());
////						prLine.setQtyIncreaseSize(vendorMaterial.getIncreaseQuantity());
////						prLine.setQtyMPS(qtyMPS);
////						prLine.setQtyTheory(qtyTheory);
////						prLine.setQty(qty);
////						prLine.setLineTotal(qty.multiply(vendorMaterial.getReferencedPrice()));
////					} else {
////						//�����Batch�������MergerPrLineʱ���¼���
////						prLine.setQty(moBom.getQty());
////						prLine.setLineTotal(moBom.getQty().multiply(vendorMaterial.getReferencedPrice()));
////					}
//
//					prLine.setQty(moBom.getQty());
//					prLine.setLineTotal(moBom.getQty().multiply(vendorMaterial.getReferencedPrice()));
//					prLine.setLeadTime(vendorMaterial.getLeadTime());
//
//					if (vendorMaterial.getAdvanceRatio() != null && vendorMaterial.getAdvanceRatio().compareTo(BigDecimal.ZERO) > 0) {
//						prLine.setAdvancePayment(prLine.getLineTotal().multiply(vendorMaterial.getAdvanceRatio()).divide(new BigDecimal(100)));
//					}
//					Material material = moBom.getMaterial();
//					if (material == null) {
//						material = em.find(Material.class, moBom.getMaterialRrn());
//					}
//					prLine.setMaterial(material);
//					prLine.setUomId(material.getInventoryUom());
//					
//					prLine.setQtyHandOn(moBom.getQtyOnHand());
//					BigDecimal qtyHandOn2 = getWipQtyOnHand(prLine.getOrgRrn(), prLine.getMaterialRrn(), userRrn);
//					prLine.setQtyHandOn2(qtyHandOn2);//ֻͳ���˻�����Ʒ������쳵����Ʒ��Ŀ��
//					prLine.setQtyTransit(moBom.getQtyTransit());
//					prLine.setQtyAllocation(moBom.getQtyAllocation());
//					prLine.setQtyMin(moBom.getQtyMin());
//					prLine.setQtyNeed(moBom.getQtyNeed());
//					prLine.setUnitPrice(vendorMaterial.getLastPrice());
//					prLine.setPurchaser(vendorMaterial.getPurchaser());
//					prLine.setDateStart(moBom.getDateStart());
//					prLine.setDateEnd(moBom.getDateEnd());
//					prLine.setPath(moBom.getPath());
//					prLine.setPathLevel(moBom.getPathLevel());
//					prLine.setWarehouseRrn(warehouse.getObjectRrn());
//					prLine.setLineStatus(Documentation.STATUS_DRAFTED);
//					prLine.setMoBomRrn(moBom.getObjectRrn());
//					moLines.add(prLine);
//				}
//			}
//		} catch (ClientException e){ 
//			throw e;
//		} catch (Exception e){ 
//			logger.error(e.getMessage(), e);
//			throw new ClientException(e);
//		}
//		return moLines;
//	}
//	
//	public void addTmpMoBatch(TempManufactureOrder mo, List<DocumentationLine> moLines, 
//			List<TempManufactureOrderBom> moBoms, TempRequisition pr, long userRrn) throws ClientException{
//		Map<Long, MaterialSum> sums = new HashMap<Long, MaterialSum>();
//		try {
//			
//			Date moDateStart = null;
//			Date moDateEnd = null;
//
//			mo.setIsActive(true);
//			mo.setCreatedBy(userRrn);
//			mo.setUpdatedBy(userRrn);
//			mo.setCreated(new Date());
//			mo.setDocStatus(Documentation.STATUS_DRAFTED);
//			mo.setMoType(ManufactureOrder.MOTYPE_P);
//			mo.setDocType(Documentation.DOCTYPE_PMO);
//			mo.setDocId(this.generateMoCode(mo));
//			ADUser user = em.find(ADUser.class, userRrn);
//			mo.setUserCreated(user.getUserName());
//			
//			WorkCenter wc = getWorkCenterByMaterial(mo.getOrgRrn(), mo.getMaterialRrn());
//			if (wc == null) {
//				throw new ClientParameterException("wip.not_workcenter_found", mo.getMaterialId());
//			}
//			mo.setWorkCenterRrn(wc.getObjectRrn());;
//			mo.setWorkCenterId(wc.getName());
//			
//			Material material = em.find(Material.class, mo.getMaterialRrn());
//			mo.setStandTime(material.getStandTime());
//			mo.setMaterialName(material.getName());
//			em.persist(mo);
//			
//			for (TempManufactureOrderBom moBom : moBoms) {
//				moBom.setMoRrn(mo.getObjectRrn());
//				if (moBom.getObjectRrn() == null) {
//					em.persist(moBom);
//				} else {
//					em.merge(moBom);
//				}
//				
//				//�ı��ѷ���������
//				List<TempManufactureOrderBom> childBoms = getChildTmpMoBom(moBoms, moBom);
//				for (TempManufactureOrderBom childBom : childBoms) {
//					material = em.find(Material.class, childBom.getMaterialRrn());	
////					if (!material.getIsMrp() || !material.getIsLotControl()) {
////						continue;
////					}
//					if (!material.getIsLotControl()) {
//						continue;
//					}
//					
//					//ȡ���������ϱ����ѷ�����
////					BigDecimal qtyAllocation = material.getQtyAllocation() == null ? BigDecimal.ZERO : material.getQtyAllocation();
////					material.setQtyAllocation(qtyAllocation.add(childBom.getQtyNeed()));
////					em.merge(material);
//					
//					MaterialSum materialSum;
//					if (sums.containsKey(childBom.getMaterialRrn())) {
//						materialSum = sums.get(childBom.getMaterialRrn());
//					} else {
//						materialSum = getMaterialSum(mo.getOrgRrn(), childBom.getMaterialRrn(), true, true);
//						sums.put(childBom.getMaterialRrn(), materialSum);
//					}
//					if(materialSum != null){
//						materialSum.setQtyAllocation(materialSum.getQtyAllocation().add(childBom.getQtyNeed()));
//					}
//				}
//			}
//			
//			for (DocumentationLine docLine : moLines) {
//				if (docLine instanceof TempManufactureOrderLine) {
//					TempManufactureOrderLine moLine = (TempManufactureOrderLine)docLine;
////					//û�п�ʼ�����ʱ�䣬������MoLine
////					if (moLine.getDateStart() == null || moLine.getDateEnd() == null) {
////						continue;
////					}
//					moLine.setIsActive(true);
//					moLine.setCreatedBy(userRrn);
//					moLine.setCreated(new Date());
//					moLine.setUpdatedBy(userRrn);
//					moLine.setMasterMoRrn(mo.getObjectRrn());
//					moLine.setMasterMoId(mo.getDocId());
//					em.persist(moLine);
//					
//					//MOLine�����翪ʼʱ����ΪMO��ʼʱ��
//					if (moLine.getDateStart() != null) {
//						if (moDateStart == null) {
//							moDateStart = moLine.getDateStart();
//						} else if (moDateStart.compareTo(moLine.getDateStart()) > 0) {
//							moDateStart = moLine.getDateStart();
//						}
//					}
//					
//					if (mo.getMaterialRrn().equals(moLine.getMaterialRrn())) {
//						moDateEnd = moLine.getDateEnd();
//						//���MOLine������MO�Ĳ�Ʒ,�����������ѷ�����,���ⱻ����ʹ��
//						MaterialSum materialSum;
//						if (sums.containsKey(moLine.getMaterialRrn())) {
//							materialSum = sums.get(moLine.getMaterialRrn());
//						} else {
//							materialSum = getMaterialSum(mo.getOrgRrn(), moLine.getMaterialRrn(), true, true);
//							sums.put(moLine.getMaterialRrn(), materialSum);
//						}
//						materialSum.setQtyAllocation(materialSum.getQtyAllocation().add(moLine.getQty()));
//					}
//					
//					ManufactureOrderBom moBom;
//					if (moLine.getMoBomRrn() != null) {
//						moBom = getCurrentTmpMoBom(moBoms, moLine.getMoBomRrn().longValue());
//					} else {
//						moBom = getCurrentTmpMoBom(moBoms, moLine.getPathLevel(), moLine.getMaterialRrn(), moLine.getPath());
//					}
//					if (moBom != null) {
//						moBom.setMoLineRrn(moLine.getObjectRrn());
//						em.merge(moBom);
//					}
//					
//					//���¼�������Ʒ��
//					MaterialSum materialSum = sums.get(moLine.getMaterialRrn());
////					materialSum.setQtyMoLine(materialSum.getQtyMoLine().add(docLine.getQty()));
//					materialSum.setQtyMoLineWip(materialSum.getQtyMoLineWip().add(moLine.getQty()));
//					
//				} else if (docLine instanceof TempRequisitionLine) {
//					TempRequisitionLine prLine = (TempRequisitionLine)docLine;
//					List<TempRequisitionLine> prLines = new ArrayList<TempRequisitionLine>();
//					prLine.setLineNo(10L);
//					prLines.add(prLine);
//										
//					pr = purManager.saveTmpPRLine(pr, prLines, true, userRrn);
//					prLine = (TempRequisitionLine) pr.getPrLines().get(0);
//					
//					TempManufactureOrderBom moBom;
//					if (prLine.getMoBomRrn() != null) {
//						moBom = getCurrentTmpMoBom(moBoms, prLine.getMoBomRrn().longValue());
//					} else {
//						moBom = getCurrentTmpMoBom(moBoms, prLine.getPathLevel(), prLine.getMaterialRrn(), prLine.getPath());
//					}
//					if (moBom != null) {
//						moBom.setRequsitionLineRrn(prLine.getObjectRrn());
//						em.merge(moBom);
//					}
//						
//					//���¼�����;
//					MaterialSum materialSum = sums.get(prLine.getMaterialRrn());
//					materialSum.setQtyTransit(materialSum.getQtyTransit().add(prLine.getQty()));
//				}
//			}
//			
//			for (MaterialSum materialSum : sums.values()) {
//				em.merge(materialSum);
//			}
//			
//			mo.setDateStart(moDateStart);
//			mo.setDateEnd(moDateEnd);
//			em.merge(mo);
//
//		} catch (OptimisticLockException e){
//			logger.error(e.getMessage(), e);
//			throw new ClientException("error.optimistic_lock");
//		} catch (ClientException e){ 
//			throw e;
//		} catch (Exception e){ 
//			logger.error(e.getMessage(), e);
//			throw new ClientException(e);
//		}
//	}
//	
//	private TempManufactureOrderBom getCurrentTmpMoBom(List<TempManufactureOrderBom> moBoms, long moBomRrn) {
//		for (TempManufactureOrderBom bom : moBoms) {
//			if (bom.getObjectRrn() == moBomRrn) {
//				return bom;
//			}
//		}
//		return null;
//	}
//	
//	private TempManufactureOrderBom getCurrentTmpMoBom(List<TempManufactureOrderBom> moBoms, long pathLevel, long materialRrn, String path) {
//		for (TempManufactureOrderBom bom : moBoms) {
//			if (bom.getPath() == null || bom.getPath().length() == 0) {
//				if (bom.getMaterialRrn() == materialRrn &&
//						bom.getPathLevel() == pathLevel) {
//					return bom;
//				}
//			} else {
//				if (bom.getMaterialRrn() == materialRrn &&
//						bom.getPath().equals(path) &&
//						bom.getPathLevel() == pathLevel) {
//					return bom;
//				}
//			}
//			
//		}
//		return null;
//	}
//	
//	private TempManufactureOrderBom getParentTmpMoBom(List<TempManufactureOrderBom> moBoms, TempManufactureOrderBom moBom) {
//		if (moBom.getMaterialParentRrn() == null) {
//			return null;
//		}
//		long parentRrn = moBom.getMaterialParentRrn();
//		long parentLevel = moBom.getPathLevel() - 1;
//		for (TempManufactureOrderBom parentBom : moBoms) {
//			if (parentBom.getPath() == null || parentBom.getPath().length() == 0) {
//				if (parentBom.getMaterialRrn() == parentRrn 
//						&& parentBom.getPathLevel() == parentLevel) {
//					return parentBom;
//				}
//			} else {
//				String path = moBom.getPath();
//				path = path.substring(0, path.length() - 1);
//				path = path.substring(0, path.lastIndexOf("/") + 1);
//				if (parentBom.getMaterialRrn() == parentRrn && 
//						parentBom.getPath().equals(path) && 
//						parentBom.getPathLevel() == parentLevel) {
//					return parentBom;
//				}
//			}
//			
//		}
//
//		return null;
//	}
//	
//	private TempManufactureOrderBom generateTmpQtyProduct(BusinessCalendar prCalendar, TempManufactureOrder mo,
//			TempManufactureOrderBom bom, List<TempManufactureOrderBom> completeBoms, boolean batchFlag) throws ClientException {
//		
//		BigDecimal qtyProduct = bom.getQtyNeed();
//		bom.setQty(qtyProduct);
//		bom.setIsProduct(false);
//		bom.setIsMaterialNeed(false);
//		//ֻ���ڹ�������Ҫʱ�ſ���ʵ�ʵ���������
//		if (qtyProduct.compareTo(BigDecimal.ZERO) == 0) {
//			return bom;
//		}
//		
//		MaterialSum materialSum = getMaterialSum(bom.getOrgRrn(), bom.getMaterialRrn(), batchFlag, true);
//		//��isMrp��isLotControl������������ 
//		if (materialSum == null) {
//			bom.setQty(BigDecimal.ZERO);
//			return bom;
//		}
//		
//		//�ۼ�MO�б���������ʹ�õ�����(����ͬһ�����ϣ�������BOM���ظ�ʹ��)
//		//����������
//		BigDecimal qtyOtherLineNeed = BigDecimal.ZERO; 
//		//�����ж���(����-����)
//		BigDecimal qtyOtherLineRemaind = BigDecimal.ZERO; 
//		//����������(�ɹ�)
//		BigDecimal qtyOtherLineProduct = BigDecimal.ZERO; 
//		
//		for (TempManufactureOrderBom completeBom : completeBoms) {
//			if (completeBom.getMaterialRrn().equals(bom.getMaterialRrn())) {
//				qtyOtherLineRemaind = qtyOtherLineRemaind.add((completeBom.getQty().subtract(completeBom.getQtyNeed())));
//				qtyOtherLineNeed = qtyOtherLineNeed.add(completeBom.getQtyNeed());
//				qtyOtherLineProduct = qtyOtherLineProduct.add(completeBom.getQty());
//			}
//		}
//		
//		//��������=�������+��;����(PR+PO)+MoLine����Ʒ����-SO����-�ѷ�������-��Ϳ��+�����ж��������
//		BigDecimal qtyAvailable = materialSum.getQtyOnHand().add(materialSum.getQtyTransit())
//			.add(materialSum.getQtyMoLineWip()).subtract(materialSum.getQtySo())
//			.subtract(materialSum.getQtyAllocation()).subtract(materialSum.getQtyMin()).add(qtyOtherLineRemaind);
//		//��ǰ��ʹ������=�������-SO����-�ѷ�������(��һ������)-����������ʹ�õ�����
//		BigDecimal qtyCanUse = materialSum.getQtyOnHand().subtract(materialSum.getQtySo())
//			.subtract(materialSum.getQtyAllocation()).subtract(qtyOtherLineNeed);
//		
//		if (qtyAvailable.compareTo(BigDecimal.ZERO) < 0) {
//			qtyProduct = qtyAvailable.abs().add(qtyProduct);
//		} else if (qtyAvailable.compareTo(qtyProduct) < 0 ) {
//			qtyProduct = qtyProduct.subtract(qtyAvailable);
//		} else {
//			qtyProduct = BigDecimal.ZERO;
//		}
//		
//		bom.setQtyMin(materialSum.getQtyMin());
//		//�ѷ�����=�ϴ��ѷ�����+��������+����������������
//		bom.setQtyAllocation(materialSum.getQtyAllocation().add(qtyOtherLineNeed).add(bom.getQtyNeed()));
//		bom.setQtyOnHand(materialSum.getQtyOnHand());
//		//��;��=��;��+��������������;
//		bom.setQtyTransit(materialSum.getQtyTransit().add(qtyOtherLineProduct));
//		bom.setQtyMoLineWip(materialSum.getQtyMoLineWip());
//		bom.setQtySo(materialSum.getQtySo());
//		bom.setQtyMinProduct(materialSum.getQtyMinProduct());
//		bom.setQty(qtyProduct);
//		bom.setStandTime(materialSum.getStandTime());
//		
//		if (qtyCanUse.compareTo(bom.getQtyNeed()) >= 0) {
//			bom.setIsCanStart(true);
//		}
//		
//		if (qtyProduct.compareTo(BigDecimal.ZERO) > 0) {
//			if (materialSum.getIsProduct()) {
//				//����������Ҫ����
////				if (materialSum.getQtyMinProduct().compareTo(BigDecimal.ZERO) > 0
////						&& qtyProduct.compareTo(materialSum.getQtyMinProduct()) < 0) {
////					qtyProduct = materialSum.getQtyMinProduct();
////				}
//				qtyProduct = getQtyTheory(qtyProduct, materialSum.getQtyMinProduct(), materialSum.getQtyMinProduct());
//				bom.setQty(qtyProduct);
//				bom.setIsProduct(true);
//				bom.setIsMaterialNeed(false);
//				bom = generateTmpDuration(bom, materialSum);
//				return bom;
//			} else if (materialSum.getIsPurchase()) {
//				//�ɹ�������Ҫ�ɹ�
//				bom.setIsProduct(false);
//				bom.setIsMaterialNeed(true);
//				bom.setIqcLeadTime(materialSum.getIqcLeadTime());
////				�ɹ����ϲ�ʹ�ù�������
////				�ӵ�ǰ����һ�����տ�ʼ
////				Calendar now = GregorianCalendar.getInstance();
////				Calendar dateStart = new GregorianCalendar();
////				dateStart.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE), 0, 0, 0);
////				dateStart.add(Calendar.DATE, 1);
////				bom.setDateStart(dateStart.getTime());
////				if (materialSum.getIsJit()) {
////					//��������
////					bom.setDateEnd(dateStart.getTime());
////				} else {
////					//��ǰ����+�ɹ�����
////					VendorMaterial vendorMaterial = vdmManager.getPrimaryVendor(materialSum.getMaterialRrn());
////					if (vendorMaterial == null) {
////						throw new ClientParameterException("pur.material_no_primary_vendor", materialSum.getMaterialId());
////					}
////					
////					int leadTime = vendorMaterial.getLeadTime() == null ? 0 : vendorMaterial.getLeadTime().intValue();
////					dateStart.add(Calendar.DATE, leadTime);
////					bom.setDateEnd(dateStart.getTime());
////				}
////				//�ӵ�ǰ����һ�����տ�ʼ,�㹤����
//				Date dateStart = prCalendar.findStartOfNextDay(new Date());
//				bom.setDateStart(dateStart);
//				if (materialSum.getIsJit()) {
//					//��������
//					bom.setDateEnd(dateStart);
//				} else {
//					//��ǰ����+�ɹ�����
//					VendorMaterial vendorMaterial = vdmManager.getPrimaryVendor(materialSum.getMaterialRrn());
//					if (vendorMaterial == null) {
//						throw new ClientParameterException("pur.material_no_primary_vendor", materialSum.getMaterialId());
//					}
//					
//					int leadTime = vendorMaterial.getLeadTime() == null ? 0 : vendorMaterial.getLeadTime().intValue();
//					Date dateEnd = prCalendar.addDay(dateStart, leadTime);
//					bom.setStandTime(new BigDecimal(leadTime));		//�ɹ�����Ҳ����standTime��
//					bom.setDateEnd(dateEnd);
//					bom.setVendorMaterial(vendorMaterial);
//				}
//				if (bom.getDateEnd().compareTo(mo.getDateEnd()) > 0) {
//					bom.setIsDateNeed(true);
//				}
//			}
//		}
//		
//		return bom;
//	}
//	
//	private TempManufactureOrderBom generateTmpDuration(TempManufactureOrderBom moBom, MaterialSum materialSum) throws ClientException {
//		//����ʱ��Ϊ(��׼��ʱ*��������)/������������
//		BigDecimal standTime = materialSum.getStandTime() == null ? BigDecimal.ZERO : materialSum.getStandTime(); 
//		WorkCenter wc = getWorkCenterByMaterial(moBom.getOrgRrn(), moBom.getMaterialRrn());
//		if (wc == null) {
//			throw new ClientParameterException("wip.not_workcenter_found", moBom.getMaterialId());
//		}
//		long duration;
//		if (wc.getManpower() != null && wc.getManpower() > 0 ) {
//			duration = moBom.getQty().multiply(standTime).longValue()/wc.getManpower();
//		} else {
//			duration = moBom.getQty().multiply(standTime).longValue();
//		}
//		moBom.setWorkCenterRrn(wc.getObjectRrn());
//		moBom.setDuration(duration);
//		moBom.setWeekType(wc.getWeekType());
//		return moBom;
//	}
//	
//	private TempManufactureOrderBom generateTmpDateEnd(BusinessCalendar moCalendar, TempManufactureOrder mo, 
//			TempManufactureOrderBom bom, List<TempManufactureOrderBom> dateBoms) throws ClientException {
//		//ȡ�ø�WorkCenter������δ��ɵ���MO
//		StringBuffer sql = new StringBuffer(" SELECT TempManufactureOrderLine FROM TempManufactureOrderLine TempManufactureOrderLine ");
//		sql.append(" WHERE ");
//		sql.append(" workCenterRrn = ? "); 
//		sql.append(" AND (lineStatus = '" + Documentation.STATUS_DRAFTED + "' OR lineStatus = '" +  Documentation.STATUS_APPROVED + "')" );
//		sql.append(" ORDER BY dateStart ");
//		Query query = em.createQuery(sql.toString());
//		query.setParameter(1, bom.getWorkCenterRrn());
//		List<TempManufactureOrderLine> moLines = query.getResultList();
//		
//		List<DocumentationLine> lines = new ArrayList<DocumentationLine>();
//		lines.addAll(moLines);
//		
//		//
//		List<TempManufactureOrderBom> wcDateBoms = new ArrayList<TempManufactureOrderBom>();
//		for (TempManufactureOrderBom dateBom : dateBoms) {
//			if (bom.getWorkCenterRrn().equals(dateBom.getWorkCenterRrn())) {
//				wcDateBoms.add(dateBom);
//			}
//		}
//		lines.addAll(wcDateBoms);
//		//���Ѿ��ڸ�WorkCenter�ϵ���MO�ͱ�MO���ڸ�WorkCenter�ϵ���MO������ʼʱ������
//		Collections.sort(lines, new LineStartComparator());
//		
//		Date dateStart = bom.getDateStart();
//		long duration = bom.getDuration();
//		//���û����������������MO
//		if (lines.size() == 0) {
//			Date dateEnd = moCalendar.add(dateStart, new Duration(duration + " " + Duration.BUSINESS_MINUTES));
//			bom.setDateEnd(dateEnd);
//			return bom;
//		}
//		
//		//Ѱ�ҿ���ʱ��
//		for (DocumentationLine docLine : lines) {
//			//docLineû��ʱ���򲻲����ų�
//			if (docLine.getDateStart() == null || docLine.getDateEnd() == null) {
//				continue;
//			}
//			//�����ʼʱ��С���Ѿ����ڵ���MO�Ŀ�ʼʱ��
//			if (dateStart.compareTo(docLine.getDateStart()) < 0) {
//				//�������ʱ��
//				Date dateEnd = moCalendar.add(dateStart, new Duration(duration + " " + Duration.BUSINESS_MINUTES));
//				//�������ʱ�仹С����MO�Ľ���ʱ�䣬���ʾ�ҵ�����ʱ���
//				if (dateEnd.compareTo(docLine.getDateStart()) < 0) {
//					bom.setDateStart(dateStart);
//					bom.setDateEnd(dateEnd);
//					return bom;
//				} else {
//					//���򽫿�ʼʱ�����¶�Ϊ��MO�Ľ���ʱ��
//					dateStart = docLine.getDateEnd();
//				}
//			} else {
//				//�����ʼʱ������Ѿ����ڵ���MO�Ŀ�ʼʱ�䣬����С����MO�Ľ���ʱ��
//				//����Ҫ����ʼʱ�����¶�Ϊ��MO�Ľ���ʱ��
//				if (dateStart.compareTo(docLine.getDateEnd()) < 0) {
//					dateStart = docLine.getDateEnd();
//				}
//			}
//			if (dateStart.compareTo(mo.getDateEnd()) >= 0) {
//				bom.setIsDateNeed(true);
//				return bom;
//			}
//		}
//		
//		//�����MO�Ƿ�����������
//		if (dateStart.compareTo(mo.getDateEnd()) < 0) {
//			Date dateEnd = moCalendar.add(dateStart, new Duration(duration + " " + Duration.BUSINESS_MINUTES));
//			if (dateEnd.compareTo(mo.getDateEnd()) < 0) {
//				bom.setDateStart(dateStart);
//				bom.setDateEnd(dateEnd);
//				return bom;
//			}
//		} 
//		bom.setIsDateNeed(true);
//		return bom;
//	}
@Override
	public List<Object[]> getMouldsByMaterial(long orgRrn, long objecrRrn) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT m.object_rrn,m.mould_id, m.mould_name FROM PDM_MATERIAL w, WIP_MOULD m");
		sql.append(" WHERE w.org_rrn = ? ");
		sql.append(" AND m.object_rrn = w.mould_rrn AND w.object_rrn = ? "); 
		Query query = em.createNativeQuery(sql.toString());
		query.setParameter(1, orgRrn);
		query.setParameter(2, objecrRrn);
		List<Object[]> eqp_Rrn = query.getResultList();
		return eqp_Rrn;
	}	

	/**
	 * 1.�����������ͳ�Ʋ�ѯBUG��MERGE��������ѷ�����û�м���ȥ
	 * 2.ֻ�޸�SP_GET_QTYALLOCATION���merge״̬
	 * */
	public MaterialSum getMaterialSum2(long orgRrn, long materialRrn, boolean batchFlag, boolean soFlag, boolean calcAllFlag) throws ClientException {
		MaterialSum materialSum = new MaterialSum();
		try{
			if (batchFlag) {
				String whereClause = " materialRrn = '" + materialRrn + "' ";
				List<MaterialSum> sums = adManager.getEntityList(orgRrn, MaterialSum.class, 1, whereClause, "");
				if (sums != null && sums.size() > 0) {
					materialSum = sums.get(0);
					return materialSum;
				}
			}
			Material material = em.find(Material.class, materialRrn);
			if (!calcAllFlag) {
				if (!material.getIsMrp() || !material.getIsLotControl()) {
					return null;
				}
			} else {
				if (!material.getIsLotControl()) {
					return null;
				}
			}
			
			materialSum.setMaterial(material);
			materialSum.setMaterialRrn(materialRrn);
			materialSum.setMaterialId(material.getMaterialId());
			materialSum.setMaterialName(material.getName());
			materialSum.setQtyMinProduct(material.getQtyMinProduct() == null ? BigDecimal.ZERO : material.getQtyMinProduct());
			materialSum.setQtyMin(material.getQtyMin() == null ? BigDecimal.ZERO : material.getQtyMin());
			materialSum.setIsPurchase(material.getIsPurchase());
			materialSum.setIsProduct(material.getIsProduct());
			materialSum.setIsJit(material.getIsJit());
			materialSum.setStandTime(material.getStandTime());
			materialSum.setIqcLeadTime(material.getIqcLeadTime() == null ? 0 : material.getIqcLeadTime());
			
			Session session = (Session) em.getDelegate();  
	        Connection conn = session.connection();  
            CallableStatement call = conn.prepareCall("{CALL SP_GET_QTYALLOCATION2(?,?,?,?,?,?,?,?,?,?,?,?)}");  
            call.setLong(1, orgRrn);  
            call.setLong(2, materialRrn);  
            call.registerOutParameter(3, Types.NUMERIC);  
            call.registerOutParameter(4, Types.NUMERIC);  
            call.registerOutParameter(5, Types.NUMERIC);  
            call.registerOutParameter(6, Types.NUMERIC);  
            call.registerOutParameter(7, Types.NUMERIC);  
            call.registerOutParameter(8, Types.NUMERIC);  
            call.registerOutParameter(9, Types.NUMERIC);  
            call.registerOutParameter(10, Types.NUMERIC);  
            call.registerOutParameter(11, Types.NUMERIC);  
            call.registerOutParameter(12, Types.NUMERIC); 
            
            call.execute();  
            
            BigDecimal qtyOnHand = call.getBigDecimal(3);
            BigDecimal qtyWriteOff = call.getBigDecimal(4);
            BigDecimal qtyDiff = call.getBigDecimal(5);
            BigDecimal qtyTransitPr = call.getBigDecimal(6);
            BigDecimal qtyTransitPo = call.getBigDecimal(7);
            BigDecimal qtyMoLine = call.getBigDecimal(8);
            BigDecimal qtyMoLineReceive = call.getBigDecimal(9);
            BigDecimal qtyMoLineWip = call.getBigDecimal(10);
            BigDecimal qtyAllocation = call.getBigDecimal(11);
            BigDecimal qtySo = call.getBigDecimal(12);
            
			materialSum.setQtyOnHand(qtyOnHand);
			materialSum.setQtyWriteOff(qtyWriteOff);
			materialSum.setQtyDiff(qtyDiff);
			materialSum.setQtyTransit(qtyTransitPr.add(qtyTransitPo));
			materialSum.setQtyMoLine(qtyMoLine);
			materialSum.setQtyMoLineReceive(qtyMoLineReceive);
			materialSum.setQtyMoLineWip(qtyMoLineWip);
			materialSum.setQtyAllocation(qtyAllocation);
			materialSum.setQtySo(qtySo);
			
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return materialSum;
	}
	
	
	/**
	 * Ӫ�˲��Ž����˹�����󣬵�������Ա����༭��������ø÷���
	 * (����ͳ��ÿ�����ϵ�������)
	 * */
	@Override
	public List<ManufactureOrderBom> generateFirstPrepareMoBomDetail(ManufactureOrder mo, List<ManufactureOrderBom> moBoms) throws ClientException {
//		if(mo.getHasFirstCountBOM()==null){
		if(!mo.getHasFirstCountBOM() || mo.getHasFirstCountBOM()==null){
			moBoms = generatePrepareMoBomDetail(mo, moBoms, false);
			mo.setHasFirstCountBOM(true);
//			em.merge(mo);
		}
		return moBoms;
	}
	
	@Override
	public List<ManufactureOrderBom> generatePrepareMoBomDetail(ManufactureOrder mo, List<ManufactureOrderBom> moBoms) throws ClientException {
		return generatePrepareMoBomDetail(mo, moBoms, false);
	}
	/**
	 * 1.����BOM�ṹ(ȡ��������Ĺ�����)
	 * */
	@Override
	public List<ManufactureOrderBom> generatePrepareMoBomDetail(
			ManufactureOrder mo, List<ManufactureOrderBom> moBoms, boolean batchFlag) throws ClientException {
		try {
			Map<String, BusinessCalendar> moCalendarMap = new HashMap<String, BusinessCalendar>();
			BusinessCalendar prCalendar = basManager.getCalendarByDay(mo.getOrgRrn(), BusinessCalendar.CALENDAR_PURCHASE);
			List<String> weekTypes = basManager.getWeekType(mo.getOrgRrn());
			for (String weekType : weekTypes) {
				BusinessCalendar moCalendar = basManager.getCalendarByDayHours(mo.getOrgRrn(), BusinessCalendar.CALENDAR_MANUFACTURE, weekType);
				moCalendarMap.put(weekType, moCalendar);
			}
			
			int level = 0;
			List<ManufactureOrderBom> completeBoms = new ArrayList<ManufactureOrderBom>();
			//��һ��ѭ��,��BOM��߲㿪ʼ������������
			while (true) {
				List<ManufactureOrderBom> filterMoBoms = filterMoBom(moBoms, level);
				if (filterMoBoms == null || filterMoBoms.size() == 0) {
					break;
				}
				for (ManufactureOrderBom moBom : filterMoBoms) {
					ManufactureOrderBom parentBom = getParentMoBom(moBoms, moBom);
					//������������������������
					//����������ָҪ����һ�������ĸ���������Ҫ��ָ��������(��������=����������*��λ����)
					//������������ָҪ����������������Ҫ�������������������������������桢��;�����Ƶ������Ľ��
					if (parentBom != null) {
						//����������ǲɹ�����Ҫ������
						if (parentBom.getIsMaterialNeed()) {
							moBom.setQtyNeed(BigDecimal.ZERO);
						} else {
							moBom.setQtyNeed(parentBom.getQty().multiply(moBom.getUnitQty()));
						}
						moBom = generateQtyProduct(prCalendar, mo, moBom, completeBoms, batchFlag);
					} else {
						//parentBomΪnull��ʾΪMO������������
						moBom.setIsProduct(true);
						moBom.setIsMaterialNeed(false);
						moBom.setQtyNeed(mo.getQtyProduct());
						moBom.setQty(mo.getQtyProduct());
						MaterialSum materialSum = getMaterialSum(moBom.getOrgRrn(), moBom.getMaterialRrn(), batchFlag, true);
						if(materialSum == null) {
							throw new ClientParameterException("wip.material_is_not_lot_control_or_mrp", moBom.getMaterialId());
						}
						moBom = generateDuration(moBom, materialSum);
						moBom.setIqcLeadTime(materialSum.getIqcLeadTime());
						moBom.setStandTime(materialSum.getStandTime());
					}
					completeBoms.add(moBom);
				}
				level++;
			}
			
			//�ڶ���ѭ��,����ײ㿪ʼ������ù�ʱ
			level--;
			completeBoms = new ArrayList<ManufactureOrderBom>();
			while (level >= 0) {
				List<ManufactureOrderBom> filterMoBoms = filterMoBom(moBoms, level);
				if (filterMoBoms == null || filterMoBoms.size() == 0) {
					continue;
				}
				for (ManufactureOrderBom moBom : filterMoBoms) {
					if (moBom.getIsProduct()) {
						BusinessCalendar moCalendar = getBusinessCalendar(moCalendarMap, moBom.getWeekType());
						List<ManufactureOrderBom> childBoms = getChildMoBom(moBoms, moBom);
						
						Date dateStart = moCalendar.add(mo.getDateStart(), new Duration(0 + " " + Duration.BUSINESS_MINUTES));
						//������MO�Ľ���ʱ�䣬ȷ����MO�Ŀ�ʼʱ��
						for (ManufactureOrderBom childBom : childBoms) {
							//�����MO�Ѿ���ʱ����MO�϶���ʱ
							if (childBom.getIsDateNeed() && !childBom.getIsCanStart()) {
								moBom.setIsDateNeed(true);
								break;
							}
							//MO��ʼʱ�������ڴ�MO����MO����ʱ��(�Է���Ϊ��λ)
							if (childBom.getIsCanStart()) {
								dateStart = moCalendar.add(dateStart, new Duration(0 + " " + Duration.BUSINESS_MINUTES));
							} else if (childBom.getDateEnd() == null) {
								dateStart = moCalendar.add(dateStart, new Duration(0 + " " + Duration.BUSINESS_MINUTES));
							} else {
								Date dateEnd = moCalendar.add(childBom.getDateEnd(), new Duration(0 + " " + Duration.BUSINESS_MINUTES));
								//�����PR����PR�Ľ���ʱ��+��������Ϊ��һ�������յĿ�ʼ
								if (childBom.getIsMaterialNeed()) {
									int iqcLeadTime = childBom.getIqcLeadTime() != null ? childBom.getIqcLeadTime().intValue() : 0;
									dateEnd = moCalendar.addDay(dateEnd, iqcLeadTime);
									dateEnd = moCalendar.addDay(dateEnd, 1);
									dateEnd = moCalendar.add(dateEnd, new Duration(0 + " " + Duration.BUSINESS_MINUTES));
								}
								if (dateEnd.compareTo(mo.getDateEnd()) > 0) {
									moBom.setIsDateNeed(true);
									break;
								}
								if (dateStart.compareTo(dateEnd) < 0) {
									dateStart = dateEnd;
								}
							}
						}
						
						if (!moBom.getIsDateNeed()) {
							moBom.setDateStart(dateStart);
							//���ҿ��ù�ʱ
							generateDateEnd(moCalendar, mo, moBom, completeBoms);
							if (!moBom.getIsDateNeed()) {
								completeBoms.add(moBom);
							}
						}
					}
				}
				level--;
			}
			
			for (ManufactureOrderBom moBom : moBoms) {
//				if("Y".equals(mo.getIsPrepareMo()) && "Y".equals(moBom.getIsPrepareMoLine())){
//					moBom.setDateStart(null);
//					moBom.setDuration(null);
//					moBom.setWorkCenterRrn(null);
//					moBom.setStandTime(null);
//				}
				em.merge(moBom);
			}
			
		} catch (ClientParameterException e){ 
			throw e;
		} catch (ClientException e){ 
			throw e;
		}  catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return moBoms;
	}
	
	/**�����ݿ��л�ȡMoBom(���е�BOM��Ϣ�Ѿ��Ǽ������BOM)
	 * 1.����ȡ��������Ĺ�����BOM�ṹ
	 * 2.��ѯ���й�����
	 * */
	@Override
	public List<ManufactureOrderBom> getPerpareMoBomDetailFromDB(ManufactureOrder mo,List<ManufactureOrderBom> boms) throws ClientException {
		//�����ɹ������BOM��ԭ��Ϊ������BOM,����Ϊȡ��������
//		List<ManufactureOrderBom> updateBoms = adManager.getEntityList(mo.getOrgRrn(),ManufactureOrderBom.class ,Integer.MAX_VALUE,
//				" moRrn = "+mo.getObjectRrn() + " and againGenMoLine = 'Y' and isPrepareMoLine is null and pathLevel <> 0 ",null);
		List<ManufactureOrderBom> updateBoms = adManager.getEntityList(mo.getOrgRrn(),ManufactureOrderBom.class ,Integer.MAX_VALUE,
		" moRrn = "+mo.getObjectRrn() + " and againGenMoLine = 'Y' and (isPrepareMoLine <> 'Y' or isPrepareMoLine is null ) and pathLevel <> 0 ",null);		
		//������
		List<ManufactureOrderBom> rootBom = adManager.getEntityList(mo.getOrgRrn(),ManufactureOrderBom.class ,Integer.MAX_VALUE,
				" moRrn = "+mo.getObjectRrn() + " and pathLevel = 0 ",null);//rootBom���ڴ�������ĳһ������Ҫ���ɹ������û�дӵ�һ�ȼ���ʼ�����������������
		//�õ�����Ҫ�Ѿ����ɹ������BOM�ṹ
		List<ManufactureOrderBom> moBoms = getMoBomDetailFromDBCana(mo);
		//���������ɹ������BOM�ṹ,��BOM������Ʒ�ȵ�
		if(updateBoms!=null && updateBoms.size()>0){
			updateBoms.addAll(rootBom);
			generatePrepareMoBomDetail(mo, updateBoms);
			moBoms.addAll(updateBoms);
		}else{
			moBoms.addAll(rootBom);
		}
		return moBoms;
	}
	/**
	 * �õ����ܳ�Ԥ�������˵�BOM�ṹ�������������÷�������Ϊ��Ҫ�������ɹ���������Ҫ���¼���BOM�ṹ������BOM�Ƿ���Ҫ�ɹ��ȵ������Ϣ��
	 * ���Ԥ���������BOM���Ƕ�ȡ���ݣ�����������
	 */
	public List<ManufactureOrderBom> getMoBomDetailFromDBCana(ManufactureOrder mo) throws ClientException {
		StringBuffer sql = new StringBuffer(" SELECT ManufactureOrderBom FROM ManufactureOrderBom ManufactureOrderBom ");
		sql.append(" WHERE ");
		sql.append(ADBase.BASE_CONDITION);
		sql.append(" AND moRrn = ? ");
//		sql.append(" AND againGenMoLine is null "); 
		sql.append(" AND (againGenMoLine <> 'Y' or  againGenMoLine is null) "); 
		sql.append(" AND pathLevel <> 0 "); 
		sql.append(" ORDER BY pathLevel, lineNo ");
		logger.debug(sql);
		Query query = em.createQuery(sql.toString());
		query.setParameter(1, mo.getOrgRrn());
		query.setParameter(2, mo.getObjectRrn());
		List<ManufactureOrderBom> moBoms = query.getResultList();
		return moBoms;
	}
	
	/**�����ݿ��л�ȡMoLine
	 * 1.���ɴ���������
	 * 2.��ѯ���й�����
	 * */
//	@Override
//	public List<DocumentationLine> getPrepareMoLine(ManufactureOrder mo, List<ManufactureOrderBom> moBoms, Long userRrn)  throws ClientException {
//		List<ManufactureOrderBom> prepareBoms = new ArrayList<ManufactureOrderBom>();//����������
//		for(ManufactureOrderBom bom : moBoms){
//			if("Y".equals(bom)){
//				
//			}
//			prepareBoms.add(e);
//		}
//		generateMoLine(mo, prepareBoms, false, userRrn);
//		if (mo.getObjectRrn() == null) {
//			return generateMoLine(mo, moBoms, false, userRrn);
//		} else {
//			return getMoLineFromDB(mo, moBoms);
//		}
 
	
	/**
	 * ���ܻ�ȡMOBOM
	 * 1.�½���ֱ�Ӵ����ݿ��л�ȡ
	 * 2.�༭�����ȴ����ݿ��л�ȡ��������MO�Ǵ�����������ȡWIP_MO_BOM����BOM�Ƿ��Ǵ�������������
	 * */
	@Override
	public List<ManufactureOrderBom> updateMoBomCana(long orgRrn, List<ManufactureOrderBom> updateBoms,ManufactureOrder mo) throws ClientException {
		List<ManufactureOrderBom> moBoms = new ArrayList<ManufactureOrderBom>();
		try {
			if(mo!=null){
				mo = em.find(ManufactureOrder.class, mo.getObjectRrn());
			}
			int i = Integer.parseInt(mo.getCountPrepareBom());//
			List<ManufactureOrderBom> countPrepareBom = new ArrayList<ManufactureOrderBom>();
			for(ManufactureOrderBom bom : updateBoms){
				if(bom.getAgainGenMoLine()){
					mo.setHasPrepareMoLine(true);
					i--;
				}
				if(bom.getIsPrepareMoLine()){
					countPrepareBom.add(bom);
				}
				em.merge(bom);
			}
			mo.setCountPrepareBom(i+"");
			em.merge(mo);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return moBoms;
	}
	
	/**
	 * ����������ɵĹ�������д���
	 */
	public ManufactureOrder addMoCana(ManufactureOrder mo, List<DocumentationLine> moLines, 
			List<ManufactureOrderBom> moBoms, long userRrn) throws ClientException{
		try {
			Date moDateStart = null;
			Date moDateEnd = null;

			//�������ɹ������BOM�Ѿ�������
			for (ManufactureOrderBom moBom : moBoms) {
				if(moBom.getAgainGenMoLine()){
					moBom = em.find(ManufactureOrderBom.class, moBom.getObjectRrn());
//					moBom.setAgainGenMoLine(null);
					moBom.setAgainGenMoLine(false);
					em.merge(moBom);
				}
			}
			em.flush();
			
			List<Requisition> moRequisitions = adManager.getEntityList(mo.getOrgRrn(), Requisition.class, 
					Integer.MAX_VALUE,"orgRrn ="+mo.getOrgRrn()+" and moRrn = "+mo.getObjectRrn(),null);
			
			Requisition pr = null;
			
			
			
			if(moRequisitions!=null && moRequisitions.size() >0){
				pr = moRequisitions.get(0);
			}else{
				pr = new Requisition();
				pr.setOrgRrn(mo.getOrgRrn());
				pr.setMoRrn(mo.getObjectRrn());
				pr.setMoId(mo.getDocId());
			}
			int i = 0;
			for (DocumentationLine docLine : moLines) {
				//objectRrn��Ϊ�ղ��账��ֻ����ʱ�䣬
				if (docLine instanceof ManufactureOrderLine) {
					ManufactureOrderLine moLine = (ManufactureOrderLine)docLine;
					//û�п�ʼ�����ʱ�䣬������MoLine
					if (moLine.getDateStart() == null || moLine.getDateEnd() == null) {
						continue;
					}
//					//MOLine�����翪ʼʱ����ΪMO��ʼʱ��
//					if (moLine.getDateStart() != null) {
//						if (moDateStart == null) {
//							moDateStart = moLine.getDateStart();
//						} else if (moDateStart.compareTo(moLine.getDateStart()) > 0) {
//							moDateStart = moLine.getDateStart();
//						}
//					}
//					if (mo.getMaterialRrn().equals(moLine.getMaterialRrn())) {
//						moDateEnd = moLine.getDateEnd();
//					}
					if(moLine.getObjectRrn()!=null){
						moLine.setUpdated(new Date());
						moLine.setUpdatedBy(userRrn);
						em.merge(moLine);
						
//						ManufactureOrderBom moBom;
//						if (moLine.getMoBomRrn() != null) {
//							moBom = getCurrentMoBom(moBoms, moLine.getMoBomRrn().longValue());
//						} else {
//							moBom = getCurrentMoBom(moBoms, moLine.getPathLevel(), moLine.getMaterialRrn(), moLine.getPath());
//						}
//						if (moBom != null) {
//							moBom.setMoLineRrn(moLine.getObjectRrn());
//							em.merge(moBom);
//						}
						continue;
					}
					moLine.setIsActive(true);
					moLine.setCreatedBy(userRrn);
					moLine.setCreated(new Date());
					moLine.setUpdatedBy(userRrn);
					moLine.setMasterMoRrn(mo.getObjectRrn());
					moLine.setMasterMoId(mo.getDocId());
					em.persist(moLine);
					
					ManufactureOrderBom moBom;
					if (moLine.getMoBomRrn() != null) {
						moBom = getCurrentMoBom(moBoms, moLine.getMoBomRrn().longValue());
					} else {
						moBom = getCurrentMoBom(moBoms, moLine.getPathLevel(), moLine.getMaterialRrn(), moLine.getPath());
					}
					if (moBom != null) {
						moBom = em.find(ManufactureOrderBom.class, moBom.getObjectRrn());
						moBom.setMoLineRrn(moLine.getObjectRrn());
//						moBom.setHasMoBom("Y");
						em.merge(moBom);
					}
					
				} else if (docLine instanceof RequisitionLine) {
					if(docLine.getObjectRrn()!=null){
						continue;
					}
					//�����е�prline�����õ�ͬһ��pr���� 
					RequisitionLine prLine = (RequisitionLine)docLine;
					List<RequisitionLine> prLines = new ArrayList<RequisitionLine>();
					prLines.add(prLine);
					if (prLine.getObjectRrn() != null) {
						pr = em.find(Requisition.class, prLine.getRequisitionRrn());
					} else {
						prLine.setLineNo((long)10 + i * 10);
					}
					pr = purManager.savePRLine(pr, prLines, true, userRrn);
					prLine = pr.getPrLines().get(0);
					
					ManufactureOrderBom moBom;
					if (prLine.getMoBomRrn() != null) {
						moBom = getCurrentMoBom(moBoms, prLine.getMoBomRrn().longValue());
					} else {
						moBom = getCurrentMoBom(moBoms, prLine.getPathLevel(), prLine.getMaterialRrn(), prLine.getPath());
					}
					if (moBom != null) {
						moBom = em.find(ManufactureOrderBom.class, moBom.getObjectRrn());
						moBom.setRequsitionLineRrn(prLine.getObjectRrn());
//						moBom.setHasMoBom("Y");
						em.merge(moBom);
					}
					
					i++;
				}
			}
//			mo.setDateStart(moDateStart);
//			mo.setDateEnd(moDateEnd);
//			em.merge(mo);
			em.flush();
			if(pr.getObjectRrn() != null) {
				mergePrLine(pr);
			}
			
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (ClientException e){ 
			throw e;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return mo;
	}
	
	
	/**
	 * 
	 * ��һ�α༭Ԥ��������������ɹ�����
	 * ֻ��Ԥ�����BOM��������moline
	 */
	
	public List<DocumentationLine> generateMoLineFirstPrepare(ManufactureOrder mo, List<ManufactureOrderBom> moBoms, boolean batchFlag, Long userRrn) throws ClientException {
		List<DocumentationLine> moLines = new ArrayList<DocumentationLine>();
		try {
			Warehouse warehouse = invManager.getDefaultWarehouse(mo.getOrgRrn());
			for (ManufactureOrderBom moBom : moBoms) {
				//parpare�����������:Ԥ�������ϲ����ɹ�����
				if(moBom.getIsPrepareMoLine()){
					continue;
				}
				if (moBom.getIsProduct() && moBom.getQty().compareTo(BigDecimal.ZERO) > 0) {
					ManufactureOrderLine moLine = new ManufactureOrderLine();
					moLine.setOrgRrn(mo.getOrgRrn());
					moLine.setIsActive(true);
					moLine.setMaterialRrn(moBom.getMaterialRrn());
					if (!batchFlag) {
						Material material = moBom.getMaterial();
						if (material == null) {
							material = em.find(Material.class, moBom.getMaterialRrn());
						}
						moLine.setMaterial(material);
						// Add by BruceYou 2012-03-13
						moLine.setMaterialName(material.getName());
						moLine.setUomId(material.getInventoryUom());
					}
					moLine.setUomId(moBom.getUomId());
					moLine.setQty(moBom.getQty());
					moLine.setQtyNeed(moBom.getQtyNeed());
					moLine.setQtyAllocation(moBom.getQtyAllocation());
					moLine.setQtyOnHand(moBom.getQtyOnHand());
					moLine.setWorkCenterRrn(moBom.getWorkCenterRrn());
					moLine.setDateStart(moBom.getDateStart());
					moLine.setDateEnd(moBom.getDateEnd());
					moLine.setPath(moBom.getPath());
					moLine.setPathLevel(moBom.getPathLevel());
					//parpare�����������:Ԥ�������Ĺ�������ֱ�����
					if(mo.getIsPrepareMo()){
						moLine.setLineStatus(Documentation.STATUS_APPROVED);
					}else{
						moLine.setLineStatus(Documentation.STATUS_DRAFTED);
					}
					moLine.setMoBomRrn(moBom.getObjectRrn());
					moLine.setDescription(mo.getComments());//�����������д��뱸ע��Ϣ
					moLine.setOrderId(mo.getOrderId());//�����������д��붩�������Ϣ
					moLine.setSalePlanType(mo.getSalePlanType());//�����������д�������������Ϣ
					moLine.setCustomerName(mo.getCustomerName());//�����������д���ͻ�����Ϣ
					moLine.setSaler(mo.getSaler());//�����������д���ҵ��Ա��Ϣ
					moLines.add(moLine);
				} else if (moBom.getIsMaterialNeed()){
					RequisitionLine	prLine = new RequisitionLine();
					prLine.setOrgRrn(mo.getOrgRrn());
					prLine.setIsActive(true);
					prLine.setMaterialRrn(moBom.getMaterialRrn());
					VendorMaterial vendorMaterial = moBom.getVendorMaterial();
					if (vendorMaterial == null) {
						vendorMaterial = vdmManager.getPrimaryVendor(prLine.getMaterialRrn());
					}
					if (vendorMaterial == null) {
						Material material = em.find(Material.class, moBom.getMaterialRrn());
						throw new ClientParameterException("pur.material_no_primary_vendor", material.getMaterialId());
					}
//					if (!batchFlag) {
//						//���������С�����͵���������������������ʵ������
//						BigDecimal qtyMPS = moBom.getQty();
//						BigDecimal qtyTheory = getQtyTheory(qtyMPS, vendorMaterial.getLeastQuantity(), vendorMaterial.getIncreaseQuantity());
//						BigDecimal qty = qtyTheory;
//						prLine.setVendorRrn(vendorMaterial.getVendorRrn());
//						prLine.setQtyEconomicSize(vendorMaterial.getLeastQuantity());
//						prLine.setQtyIncreaseSize(vendorMaterial.getIncreaseQuantity());
//						prLine.setQtyMPS(qtyMPS);
//						prLine.setQtyTheory(qtyTheory);
//						prLine.setQty(qty);
//						prLine.setLineTotal(qty.multiply(vendorMaterial.getReferencedPrice()));
//					} else {
//						//�����Batch�������MergerPrLineʱ���¼���
//						prLine.setQty(moBom.getQty());
//						prLine.setLineTotal(moBom.getQty().multiply(vendorMaterial.getReferencedPrice()));
//					}

					prLine.setQty(moBom.getQty());
					prLine.setLineTotal(moBom.getQty().multiply(vendorMaterial.getReferencedPrice()));
					prLine.setLeadTime(vendorMaterial.getLeadTime());

					if (vendorMaterial.getAdvanceRatio() != null && vendorMaterial.getAdvanceRatio().compareTo(BigDecimal.ZERO) > 0) {
						prLine.setAdvancePayment(prLine.getLineTotal().multiply(vendorMaterial.getAdvanceRatio()).divide(new BigDecimal(100)));
					}
					Material material = moBom.getMaterial();
					if (material == null) {
						material = em.find(Material.class, moBom.getMaterialRrn());
					}
					prLine.setMaterial(material);
					prLine.setUomId(material.getInventoryUom());
					prLine.setPackageSpec(material.getPackageSpec());//�Զ�������װ���
					
					prLine.setQtyHandOn(moBom.getQtyOnHand());
					BigDecimal qtyHandOn2 = getWipQtyOnHand(prLine.getOrgRrn(), prLine.getMaterialRrn(), userRrn);
					prLine.setQtyHandOn2(qtyHandOn2);//ֻͳ���˻�����Ʒ������쳵����Ʒ��Ŀ��
					prLine.setQtyTransit(moBom.getQtyTransit());
					prLine.setQtyAllocation(moBom.getQtyAllocation());
					prLine.setQtyMin(moBom.getQtyMin());
					prLine.setQtyNeed(moBom.getQtyNeed());
					prLine.setUnitPrice(vendorMaterial.getLastPrice());
					prLine.setPurchaser(vendorMaterial.getPurchaser());
					prLine.setDateStart(moBom.getDateStart());
					prLine.setDateEnd(moBom.getDateEnd());
					prLine.setPath(moBom.getPath());
					prLine.setPathLevel(moBom.getPathLevel());
					prLine.setWarehouseRrn(warehouse.getObjectRrn());
					prLine.setLineStatus(Documentation.STATUS_DRAFTED);
					prLine.setMoBomRrn(moBom.getObjectRrn());
					moLines.add(prLine);
				}
			}
		} catch (ClientException e){ 
			throw e;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return moLines;
	}
	
	
	/**
	 * �����Ҫ���ɹ������BOM�������ɹ�����
	 * */
	
	public List<DocumentationLine> generateMoLineCana(ManufactureOrder mo, List<ManufactureOrderBom> moBoms, boolean batchFlag, Long userRrn) throws ClientException {
		List<DocumentationLine> moLines = new ArrayList<DocumentationLine>();
		try {
			Warehouse warehouse = invManager.getDefaultWarehouse(mo.getOrgRrn());
			for (ManufactureOrderBom moBom : moBoms) {
				if(!moBom.getAgainGenMoLine()){
					continue;
				}
				//parpare�����������
				if(moBom.getIsPrepareMoLine()){
					continue;
				}
				if (moBom.getIsProduct() && moBom.getQty().compareTo(BigDecimal.ZERO) > 0) {
					ManufactureOrderLine moLine = new ManufactureOrderLine();
					moLine.setOrgRrn(mo.getOrgRrn());
					moLine.setIsActive(true);
					moLine.setMaterialRrn(moBom.getMaterialRrn());
					if (!batchFlag) {
						Material material = moBom.getMaterial();
						if (material == null) {
							material = em.find(Material.class, moBom.getMaterialRrn());
						}
						moLine.setMaterial(material);
						// Add by BruceYou 2012-03-13
						moLine.setMaterialName(material.getName());
						moLine.setUomId(material.getInventoryUom());
					}
					moLine.setUomId(moBom.getUomId());
					moLine.setQty(moBom.getQty());
					moLine.setQtyNeed(moBom.getQtyNeed());
					moLine.setQtyAllocation(moBom.getQtyAllocation());
					moLine.setQtyOnHand(moBom.getQtyOnHand());
					moLine.setWorkCenterRrn(moBom.getWorkCenterRrn());
					moLine.setDateStart(moBom.getDateStart());
					moLine.setDateEnd(moBom.getDateEnd());
					moLine.setPath(moBom.getPath());
					moLine.setPathLevel(moBom.getPathLevel());
					//����parpare�����������
					if(mo.getIsPrepareMo()){
						moLine.setLineStatus(Documentation.STATUS_APPROVED);
					}else{
						moLine.setLineStatus(Documentation.STATUS_DRAFTED);
					}
					moLine.setMoBomRrn(moBom.getObjectRrn());
					moLine.setDescription(mo.getComments());//�����������д��뱸ע��Ϣ
					moLine.setOrderId(mo.getOrderId());//�����������д��붩�������Ϣ
					moLine.setSalePlanType(mo.getSalePlanType());//�����������д�������������Ϣ
					moLine.setCustomerName(mo.getCustomerName());//�����������д���ͻ�����Ϣ
					moLine.setSaler(mo.getSaler());//�����������д���ҵ��Ա��Ϣ
					moLines.add(moLine);
				} else if (moBom.getIsMaterialNeed()){
					RequisitionLine	prLine = new RequisitionLine();
					prLine.setOrgRrn(mo.getOrgRrn());
					prLine.setIsActive(true);
					prLine.setMaterialRrn(moBom.getMaterialRrn());
					VendorMaterial vendorMaterial = moBom.getVendorMaterial();
					if (vendorMaterial == null) {
						vendorMaterial = vdmManager.getPrimaryVendor(prLine.getMaterialRrn());
					}
					if (vendorMaterial == null) {
						Material material = em.find(Material.class, moBom.getMaterialRrn());
						throw new ClientParameterException("pur.material_no_primary_vendor", material.getMaterialId());
					}
					prLine.setQty(moBom.getQty());
					prLine.setLineTotal(moBom.getQty().multiply(vendorMaterial.getReferencedPrice()));
					prLine.setLeadTime(vendorMaterial.getLeadTime());

					if (vendorMaterial.getAdvanceRatio() != null && vendorMaterial.getAdvanceRatio().compareTo(BigDecimal.ZERO) > 0) {
						prLine.setAdvancePayment(prLine.getLineTotal().multiply(vendorMaterial.getAdvanceRatio()).divide(new BigDecimal(100)));
					}
					Material material = moBom.getMaterial();
					if (material == null) {
						material = em.find(Material.class, moBom.getMaterialRrn());
					}
					prLine.setMaterial(material);
					prLine.setUomId(material.getInventoryUom());
					prLine.setPackageSpec(material.getPackageSpec());//�Զ�������װ���
					
					prLine.setQtyHandOn(moBom.getQtyOnHand());
					BigDecimal qtyHandOn2 = getWipQtyOnHand(prLine.getOrgRrn(), prLine.getMaterialRrn(), userRrn);
					prLine.setQtyHandOn2(qtyHandOn2);//ֻͳ���˻�����Ʒ������쳵����Ʒ��Ŀ��
					prLine.setQtyTransit(moBom.getQtyTransit());
					prLine.setQtyAllocation(moBom.getQtyAllocation());
					prLine.setQtyMin(moBom.getQtyMin());
					prLine.setQtyNeed(moBom.getQtyNeed());
					prLine.setUnitPrice(vendorMaterial.getLastPrice());
					prLine.setPurchaser(vendorMaterial.getPurchaser());
					prLine.setDateStart(moBom.getDateStart());
					prLine.setDateEnd(moBom.getDateEnd());
					prLine.setPath(moBom.getPath());
					prLine.setPathLevel(moBom.getPathLevel());
					prLine.setWarehouseRrn(warehouse.getObjectRrn());
					prLine.setLineStatus(Documentation.STATUS_DRAFTED);
					prLine.setMoBomRrn(moBom.getObjectRrn());
					moLines.add(prLine);
				}
			}
		} catch (ClientException e){ 
			throw e;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return moLines;
	}
	
	/**
	 * ������2������ɺ�����Ԥ��������
	 * */
	public ManufactureOrder addMoPrepare(ManufactureOrder mo,
			List<ManufactureOrderBom> moBoms, long userRrn) throws ClientException{
		try {
//			Date moDateStart = null;
//			Date moDateEnd = null;
			mo.setIsActive(true);
			mo.setCreatedBy(userRrn);
			mo.setUpdatedBy(userRrn);
			mo.setCreated(new Date());
			//����parpare�����������
			if(mo.getIsPrepareMo()){
				mo.setDocStatus(Documentation.STATUS_PREPARE);
			}else{
				mo.setDocStatus(Documentation.STATUS_DRAFTED);
			}
			
			if (ManufactureOrder.MOTYPE_P.equals(mo.getMoType())) {
				mo.setDocType(Documentation.DOCTYPE_PMO);
			}else if (ManufactureOrder.MOTYPE_B.equals(mo.getMoType())) {
				mo.setDocType(Documentation.DOCTYPE_BMO);
			} else {
				mo.setDocType(Documentation.DOCTYPE_AMO);
			}
			mo.setDocId(this.generateMoCode(mo));
			ADUser user = em.find(ADUser.class, userRrn);
			mo.setUserCreated(user.getUserName());
			
			WorkCenter wc = getWorkCenterByMaterial(mo.getOrgRrn(), mo.getMaterialRrn());
			if (wc == null) {
				throw new ClientParameterException("wip.not_workcenter_found", mo.getMaterialId());
			}
			mo.setWorkCenterRrn(wc.getObjectRrn());;
			mo.setWorkCenterId(wc.getName());
			
			Material material = em.find(Material.class, mo.getMaterialRrn());
			mo.setStandTime(material.getStandTime());
			mo.setMaterialName(material.getName());
			if(mo.getTpsRrn() != null){//�����ͨ����ʱ�ƻ���������Ҫ���һ��ͬʱ�ǲ����Ѿ��������˴�������ͬ�Ĺ�����
				StringBuffer sql = new StringBuffer();
				sql.append(" SELECT ManufactureOrder FROM ManufactureOrder ManufactureOrder WHERE ManufactureOrder.tpsRrn = ? ");
				sql.append(" AND ManufactureOrder.docStatus <> 'CLOSED'");
				Query query = em.createQuery(sql.toString());
				query.setParameter(1, mo.getTpsRrn());
				List rslt = query.getResultList();
				if(rslt != null && rslt.size() > 0){
					throw new ClientException("wip.tps_has_mo_yet");
				}
			}
			List<ManufactureOrderBom> prepareBoms = new ArrayList<ManufactureOrderBom>();//����������
			for (ManufactureOrderBom moBom : moBoms) {
				if(moBom.getIsPrepareMoLine()){
					prepareBoms.add(moBom);
				}
				if(moBom.getAgainGenMoLine()){
					mo.setHasPrepareMoLine(true);
//					break;
				}
			}
			mo.setCountPrepareBom(prepareBoms.size()+"");
			em.persist(mo);
			
			for (ManufactureOrderBom moBom : moBoms) {
				moBom.setMoRrn(mo.getObjectRrn());
				if (moBom.getObjectRrn() == null) {
					em.persist(moBom);
				} else {
					em.merge(moBom);
				}
			}
			// �����Ӧ������ʱ���ۼƻ����������ʱ���ۼƻ���isGenerate
			if(mo.getTpsRrn() != null) {
				TpsLine tpsLine = em.find(TpsLine.class, mo.getTpsRrn());
				if(tpsLine != null) {
					tpsLine.setIsGenerate(true);
					em.merge(tpsLine);
				}
			}
			
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (ClientException e){ 
			throw e;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return mo;
	}
	
	/**
	 * ��ѯ�д��������ϵ����й���������������������ϰ�ť��
	 * */
	public List<ManufactureOrderBom> getPrepareBoms(long orgRrn,String whereClause) throws ClientException{
		try{
			StringBuffer hql = new StringBuffer();
			hql.append("SELECT BOM,MO  FROM ManufactureOrder MO , ManufactureOrderBom BOM    ");
			hql.append("  where MO.objectRrn =  BOM.moRrn ");
			hql.append(" AND BOM.orgRrn =");
			hql.append(orgRrn);
			hql.append(" AND BOM.isPrepareMoLine = 'Y' ");
			if(whereClause!=null && whereClause.length() > 0){
				hql.append(whereClause);
			}
			
			Query query = em.createQuery(hql.toString());
			List<Object[]> results = query.getResultList();
			List<ManufactureOrderBom> boms = new ArrayList<ManufactureOrderBom>();
			for(Object[] object: results){
				ManufactureOrderBom orderBom = (ManufactureOrderBom) object[0];
				ManufactureOrder mo = (ManufactureOrder) object[1];
				orderBom.setMoMoId(mo.getDocId());
				orderBom.setMoMaterialName(mo.getMaterialName());
				orderBom.setMoMaterialId(mo.getMaterialId());
				
				Material material = orderBom.getMaterial();
				if(orderBom.getMaterial()==null){
					material = em.find(Material.class, orderBom.getMaterialRrn());
				}
				if(material.getIsPurchase()){
					orderBom.setPrepareType("�⹺");
				}else if("����".equals(material.getMaterialCategory2())){
					orderBom.setPrepareType(material.getMaterialCategory2());
				}
				boms.add(orderBom);
			}
			return boms;
			
		}catch(Exception e ){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	/**
	 * У��TPS�Ƿ�����Ԥ��������TPS
	 * 1.���Ƽ��¼�����Ϊ��
	 * 2.�⹺���ڵ����ʱ������
	 * return  true:��Ԥ��������
	 * return  false:û��Ԥ��������
	 * ҵ����1.��ʱ�ƻ�Ԥ��������ΪNULL,������д��ڸüƻ�������ɾ��
	 * 2.����üƻ���Ԥ����,��������������Ϣ����Ԥ������ʱ�ƻ�����������ʱֱ�Ӷ�ȡ������Ϣ�����ٴζ�ȡ����
	 * */
	public boolean verifyPrepareTps(long orgRrn, long userRrn, TpsLine tpsLine) throws ClientException{
		boolean flag = false;//����Ƿ�����Ԥ��������
		try{
			tpsLine.setPrepareTps(false);//Ĭ�����ò�Ϊ
			if(tpsLine.getMaterialRrn()!=null){
				//�����Ƿ����⹺��Ԥ��������,û�вſ�ʼƥ��
				List<BomDetail> bomDetails = pdmManager.getActualLastBomDetails(tpsLine.getMaterialRrn());
				for(BomDetail bomDetail : bomDetails){
					Material childMaterial = bomDetail.getChildMaterial();
					//EXCEL�����������ƻ����⹺
					if((childMaterial.getIsPurchase()
							|| "����".equals(childMaterial.getMaterialCategory2()))
							&& bomDetail.getIsPrepareBomPurchase()){
						flag = true;
						break;
					}
//					ȡ�����Ƽ���Ԥ����
//					if("����".equals(childMaterial.getMaterialCategory2())){//û����BOM 
//						List<ManufactureOrderBom> childboms =getMoChildrenBom(orgRrn,childMaterial.getObjectRrn());
//						if(childboms==null || childboms.size() ==0){
//							bomDetail.setIsPrepareBomPurchase(true);
//							flag = true;
//						}
//						//û������,�������Ƽ���û�����͵���û�г�����¼
//						List<MovementLine> movementLines = adManager.getEntityList(orgRrn,MovementLine.class,1,
//						"materialRrn = "+ childMaterial.getObjectRrn(),"");
//						List<LotComponent> lotComponents = adManager.getEntityList(orgRrn,LotComponent.class,1,
//								"materialParentRrn = "+ childMaterial.getObjectRrn() ,"");
//						if((movementLines ==null || movementLines.size() == 0) && 
//							(lotComponents == null || lotComponents.size()==0)){
//								flag = true;
//								break;
////								return flag;
//						}
//					}
//					if(childMaterial.getIsPurchase() && bomDetail.getIsPrepareBomPurchase()){
//						flag = true;
//					}
					if(childMaterial.getIsPurchase()){
						//�⹺��û�г���� ���� û������ҵ��ΪԤ����
						List<MovementLine> movementLines = adManager.getEntityList(orgRrn,MovementLine.class,1,
								"materialRrn = "+ childMaterial.getObjectRrn() ,"");
						List<LotConsume> lotComponents = adManager.getEntityList(orgRrn,LotConsume.class,1,
								"materialRrn = "+ childMaterial.getObjectRrn() ,""); 
						if((movementLines ==null || movementLines.size() == 0) && 
							(lotComponents == null || lotComponents.size()==0)){
								flag = true;
								break;
//								return flag;
						}
					}
				}
				//�����Ԥ����־û�������
				if(flag){
					List<BomDetailPrepare> oldBDPrepares = adManager.getEntityList(tpsLine.getOrgRrn(),
							BomDetailPrepare.class,Integer.MAX_VALUE,"tpsLineRrn = "+tpsLine.getObjectRrn(),null);
					for(BomDetailPrepare bdPrepare : oldBDPrepares){
						bdPrepare = em.find(BomDetailPrepare.class, bdPrepare.getObjectRrn());
						em.remove(bdPrepare);//ɾ�����о�����
					}
					TpsLine updateTpsLine = em.find(TpsLine.class, tpsLine.getObjectRrn());
					updateTpsLine.setPrepareTps(true);
					em.merge(updateTpsLine);//�����Ƿ�ΪԤ������ʱ�ƻ�
					//��������BOM�ṹ������������
					for(BomDetail bomDetail : bomDetails){
						BomDetailPrepare bomDetailPrepare = new BomDetailPrepare(bomDetail);
						bomDetailPrepare.setOrgRrn(tpsLine.getOrgRrn());
						bomDetailPrepare.setIsActive(true);
						bomDetailPrepare.setCreated(new Date());
						bomDetailPrepare.setCreatedBy(userRrn);
						bomDetailPrepare.setUpdated(new Date());
						bomDetailPrepare.setUpdatedBy(userRrn);
						bomDetailPrepare.setTpsLineId(tpsLine.getTpsId());
						bomDetailPrepare.setTpsLineRrn(tpsLine.getObjectRrn());
						em.persist(bomDetailPrepare);
					}
				}
			}
			return flag;
		}catch(Exception e ){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	/**
	 * ��PDM_BOM_DETAIL_PREPARE����ȡ���ƻ������嵥ת����ManufactOrderBom
	 * ���ã�У��ƻ�ʱ�洢BOM�ṹ���Ӽƻ������������������BOM�ṹ
	 * */
	public List<ManufactureOrderBom> getMoBomFromPrepareTpsLine(long orgRrn,long userRrn,TpsLine tpsLine) throws ClientException{
		List<ManufactureOrderBom> moBoms = new ArrayList<ManufactureOrderBom>();
		try{
			if(tpsLine.getMaterialRrn()!=null){
					List<BomDetailPrepare> oldBDPrepares = adManager.getEntityList(tpsLine.getOrgRrn(),
							BomDetailPrepare.class,Integer.MAX_VALUE,"tpsLineRrn = "+tpsLine.getObjectRrn(),null);
					
					ManufactureOrderBom rootMoBom = new ManufactureOrderBom();
					rootMoBom.setMaterialParentRrn(null);
					rootMoBom.setMaterialRrn(tpsLine.getMaterialRrn());
					rootMoBom.setIsActive(true);
					rootMoBom.setPath(null);
					rootMoBom.setPathLevel(0L);
					rootMoBom.setLineNo(10L);
					rootMoBom.setUnitQty(BigDecimal.ONE);
					rootMoBom.setQtyBom(BigDecimal.ONE);
					Material material = em.find(Material.class, tpsLine.getMaterialRrn());
					rootMoBom.setOrgRrn(orgRrn);
					rootMoBom.setMaterial(material);
					rootMoBom.setUomId(material.getInventoryUom());
					moBoms.add(rootMoBom);
					for(BomDetailPrepare bdPrepare : oldBDPrepares){
						ManufactureOrderBom moBom = new ManufactureOrderBom();
						moBom.setOrgRrn(orgRrn);
						moBom.setIsActive(true);
						moBom.setMaterialParentRrn(bdPrepare.getParentRrn());
						moBom.setMaterialRrn(bdPrepare.getChildRrn());
						moBom.setPath(bdPrepare.getPath());
						moBom.setRealPath(bdPrepare.getRealPath());//��¼�����������ϵ�path
						moBom.setPathLevel(bdPrepare.getPathLevel());
						moBom.setRealPathLevel(bdPrepare.getRealPathLevel());
						moBom.setLineNo(bdPrepare.getSeqNo());
						moBom.setUnitQty(bdPrepare.getUnitQty());
						moBom.setQtyBom(bdPrepare.getQtyBom());
						moBom.setMaterial(bdPrepare.getChildMaterial());
						if (bdPrepare.getChildMaterial() != null) {
							moBom.setUomId(bdPrepare.getChildMaterial().getInventoryUom());
						}
						moBom.setDescription(bdPrepare.getDescription());
						moBom.setIsSelfControl(bdPrepare.getIsPrepareBom());
						moBoms.add(moBom);
					}
			}
		}catch(Exception e ){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return moBoms;
	}
	
	/**
	 * ��Batch�����д���MO(��ֱ��AddMo����)
	 * ͯ���Ҫ�����ƻ��������������ƻ��Ĺ�����Ŀ�ʼʱ�������������ƻ���ʱ��
	 * ���ʱ��͵��ڽ���ʱ��
	 * ����Ա:����
	 * */
	@Override
	public void addMoBatchTime(ManufactureOrder mo, List<DocumentationLine> moLines, 
			List<ManufactureOrderBom> moBoms, Requisition pr, long userRrn,Date startTime,Date endTime) throws ClientException{
		Map<Long, MaterialSum> sums = new HashMap<Long, MaterialSum>();
		try {
			
			Date moDateStart = null;
			Date moDateEnd = null;

			mo.setIsActive(true);
			mo.setCreatedBy(userRrn);
			mo.setUpdatedBy(userRrn);
			mo.setCreated(new Date());
			mo.setDocStatus(Documentation.STATUS_DRAFTED);
			mo.setMoType(ManufactureOrder.MOTYPE_P);
			mo.setDocType(Documentation.DOCTYPE_PMO);
			mo.setDocId(this.generateMoCode(mo));
			ADUser user = em.find(ADUser.class, userRrn);
			mo.setUserCreated(user.getUserName());
			
			WorkCenter wc = getWorkCenterByMaterial(mo.getOrgRrn(), mo.getMaterialRrn());
			if (wc == null) {
				throw new ClientParameterException("wip.not_workcenter_found", mo.getMaterialId());
			}
			mo.setWorkCenterRrn(wc.getObjectRrn());;
			mo.setWorkCenterId(wc.getName());
			
			Material material = em.find(Material.class, mo.getMaterialRrn());
			mo.setStandTime(material.getStandTime());
			mo.setMaterialName(material.getName());
			em.persist(mo);
			
			for (ManufactureOrderBom moBom : moBoms) {
				moBom.setMoRrn(mo.getObjectRrn());
				if (moBom.getObjectRrn() == null) {
					em.persist(moBom);
				} else {
					em.merge(moBom);
				}
				
				//�ı��ѷ���������
				List<ManufactureOrderBom> childBoms = getChildMoBom(moBoms, moBom);
				for (ManufactureOrderBom childBom : childBoms) {
					material = em.find(Material.class, childBom.getMaterialRrn());	
//					if (!material.getIsMrp() || !material.getIsLotControl()) {
//						continue;
//					}
					if (!material.getIsLotControl()) {
						continue;
					}
					
					//ȡ���������ϱ����ѷ�����
//					BigDecimal qtyAllocation = material.getQtyAllocation() == null ? BigDecimal.ZERO : material.getQtyAllocation();
//					material.setQtyAllocation(qtyAllocation.add(childBom.getQtyNeed()));
//					em.merge(material);
					
					MaterialSum materialSum;
					if (sums.containsKey(childBom.getMaterialRrn())) {
						materialSum = sums.get(childBom.getMaterialRrn());
					} else {
						materialSum = getMaterialSum(mo.getOrgRrn(), childBom.getMaterialRrn(), true, true);
						sums.put(childBom.getMaterialRrn(), materialSum);
					}
					if(materialSum != null){
						materialSum.setQtyAllocation(materialSum.getQtyAllocation().add(childBom.getQtyNeed()));
					}
				}
			}
			
			for (DocumentationLine docLine : moLines) {
				if (docLine instanceof ManufactureOrderLine) {
					ManufactureOrderLine moLine = (ManufactureOrderLine)docLine;
//					//û�п�ʼ�����ʱ�䣬������MoLine
//					if (moLine.getDateStart() == null || moLine.getDateEnd() == null) {
//						continue;
//					}
					moLine.setIsActive(true);
					moLine.setCreatedBy(userRrn);
					moLine.setCreated(new Date());
					moLine.setUpdatedBy(userRrn);
					moLine.setMasterMoRrn(mo.getObjectRrn());
					moLine.setMasterMoId(mo.getDocId());
					em.persist(moLine);
					
					//MOLine�����翪ʼʱ����ΪMO��ʼʱ��
					if (moLine.getDateStart() != null) {
						if (moDateStart == null) {
							moDateStart = moLine.getDateStart();
						} else if (moDateStart.compareTo(moLine.getDateStart()) > 0) {
							moDateStart = moLine.getDateStart();
						}
					}
					
					if (mo.getMaterialRrn().equals(moLine.getMaterialRrn())) {
						moDateEnd = moLine.getDateEnd();
						//���MOLine������MO�Ĳ�Ʒ,�����������ѷ�����,���ⱻ����ʹ��
						MaterialSum materialSum;
						if (sums.containsKey(moLine.getMaterialRrn())) {
							materialSum = sums.get(moLine.getMaterialRrn());
						} else {
							materialSum = getMaterialSum(mo.getOrgRrn(), moLine.getMaterialRrn(), true, true);
							sums.put(moLine.getMaterialRrn(), materialSum);
						}
						materialSum.setQtyAllocation(materialSum.getQtyAllocation().add(moLine.getQty()));
					}
					
					ManufactureOrderBom moBom;
					if (moLine.getMoBomRrn() != null) {
						moBom = getCurrentMoBom(moBoms, moLine.getMoBomRrn().longValue());
					} else {
						moBom = getCurrentMoBom(moBoms, moLine.getPathLevel(), moLine.getMaterialRrn(), moLine.getPath());
					}
					if (moBom != null) {
						moBom.setMoLineRrn(moLine.getObjectRrn());
						em.merge(moBom);
					}
					
					//���¼�������Ʒ��
					MaterialSum materialSum = sums.get(moLine.getMaterialRrn());
//					materialSum.setQtyMoLine(materialSum.getQtyMoLine().add(docLine.getQty()));
					materialSum.setQtyMoLineWip(materialSum.getQtyMoLineWip().add(moLine.getQty()));
					
				} else if (docLine instanceof RequisitionLine) {
					RequisitionLine prLine = (RequisitionLine)docLine;
					List<RequisitionLine> prLines = new ArrayList<RequisitionLine>();
					prLine.setLineNo(10L);
					prLines.add(prLine);
										
					pr = purManager.savePRLine(pr, prLines, true, userRrn);
					prLine = pr.getPrLines().get(0);
					
					ManufactureOrderBom moBom;
					if (prLine.getMoBomRrn() != null) {
						moBom = getCurrentMoBom(moBoms, prLine.getMoBomRrn().longValue());
					} else {
						moBom = getCurrentMoBom(moBoms, prLine.getPathLevel(), prLine.getMaterialRrn(), prLine.getPath());
					}
					if (moBom != null) {
						moBom.setRequsitionLineRrn(prLine.getObjectRrn());
						em.merge(moBom);
					}
						
					//���¼�����;
					MaterialSum materialSum = sums.get(prLine.getMaterialRrn());
					materialSum.setQtyTransit(materialSum.getQtyTransit().add(prLine.getQty()));
				}
			}
			
			for (MaterialSum materialSum : sums.values()) {
				em.merge(materialSum);
			}
			
//			mo.setDateStart(moDateStart);
//			mo.setDateEnd(moDateEnd);
			mo.setDateStart(startTime);
			mo.setDateEnd(endTime);
			em.merge(mo);

		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (ClientException e){ 
			throw e;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	//�����ע���£����������ӹ�����ı�ע
	public ManufactureOrder updateMoComments(ManufactureOrder mo ,long userRrn) throws ClientException{
		try {
		 List<ManufactureOrderLine> molines =  adManager.getEntityList(mo.getOrgRrn(),
				 ManufactureOrderLine.class,Integer.MAX_VALUE, 
					 "masterMoRrn ="+mo.getObjectRrn(),null);
		 if(molines!=null && molines.size() >0){
			 for(ManufactureOrderLine moline : molines){
				 moline.setDescription(mo.getComments());
				 em.merge(moline);
			 }
		 }
		 em.merge(mo);
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return mo;
	}
	
	/**
	 * ���¹����������¼
	 * 
	 * */
	public void updateMoAndLinePlanAlarm(long orgRrn,long userRrn,String whereClause) throws ClientException{
		try{
			List<ManufactureOrder> mos= adManager.getEntityList(orgRrn, ManufactureOrder.class,Integer.MAX_VALUE,  whereClause.toString(), null);
			if(mos!=null && mos.size() >0 ){
				ManufactureOrder mo = mos.get(0);
//				mo = em.find(ManufactureOrder.class, mo.getObjectRrn());
				mo.setPlanNoticeQty(mo.getPlanNoticeQty()+1L);
				em.merge(mo);
				StringBuffer whereClause2 = new StringBuffer();
				whereClause2.append("masterMoRrn =");
				whereClause2.append(mo.getObjectRrn());
				whereClause2.append(" and materialRrn =");
				whereClause2.append(mo.getMaterialRrn());
 
				List<ManufactureOrderLine> moLines= adManager.getEntityList(orgRrn, ManufactureOrderLine.class,Integer.MAX_VALUE,  whereClause2.toString(), null);
				if(moLines!=null && moLines.size() > 0 ){
					ManufactureOrderLine moline = moLines.get(0);
					moline = em.find(ManufactureOrderLine.class, moline.getObjectRrn());
					moline.setPlanNoticeQty(moline.getPlanNoticeQty()+1);
					em.merge(moline);
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void runSchedulePurchase(long orgRrn,long userRrn) throws ClientException{
		try{
			StringBuffer sqlSP = new StringBuffer();
			sqlSP.append( " {call SP_SCHEDULE()} " );
			Query query = em.createNativeQuery(sqlSP.toString());
			query.executeUpdate();
			
//			StringBuffer sql = new StringBuffer(" FROM RepScheNoMoBom RepScheNoMoBom " );
//			query = em.createQuery(sql.toString());
//			List<RepScheNoMoBom> noMoBoms = query.getResultList();
//			for(RepScheNoMoBom noMoBom : noMoBoms){
//				BigDecimal qtyUnit = BigDecimal.ONE;
//				String[] qtyUnits = noMoBom.getQtyUnitTotal().split("\\*");
//				for(int i=1;i<qtyUnits.length;i++){
//					qtyUnit = qtyUnit.multiply(new BigDecimal(qtyUnits[i]));
//				}
//				noMoBom.setQtyUnit(qtyUnit);
//				em.merge(noMoBom);
//			}
//				
//			sql = new StringBuffer(" FROM RepScheMoBom RepScheMoBom " );
//			query = em.createQuery(sql.toString());
//			List<RepScheMoBom> moBoms = query.getResultList();
//			for(RepScheMoBom moBom : moBoms){
//				BigDecimal qtyUnit = BigDecimal.ONE;
//				String[] qtyUnits = moBom.getQtyUnitTotal().split("\\*");
//				for(int i=1;i<qtyUnits.length;i++){
//					qtyUnit = qtyUnit.multiply(new BigDecimal(qtyUnits[i]));
//				}
//				moBom.setQtyUnit(qtyUnit);
//				em.merge(moBom);
//			}
			
			
			
			StringBuffer sql = new StringBuffer("  SELECT   MATERIAL_PARENT_RRN,'test' FROM REP_SCHE_MO_BOM3 BOM3 ");
			sql.append(" UNION ");
			sql.append(" SELECT  MATERIAL_PARENT_RRN,'test' FROM REP_SCHE_BOM2 ");
 
			Query querysql = em.createNativeQuery(sql.toString());
			List<Object[]> result = querysql.getResultList();
			for(Object[] row : result){
				long parentRrn2 = Long.parseLong(String.valueOf(row[0]));
				scheAddMoLine(parentRrn2);
			}
			
			em.flush();//bug ����merge(moBom)��û���� ������SP_SCHEDULE2
			sqlSP = new StringBuffer();
			sqlSP.append( " {call SP_SCHEDULE2()} " );
			query = em.createNativeQuery(sqlSP.toString());
			query.executeUpdate();
//			
			PasErrorLog pasLog = new PasErrorLog();
			pasLog.setOrgRrn(orgRrn);
			pasLog.setPasType("SCHEDULE");
			pasLog.setIsActive(true);
			pasLog.setErrDate(new Date());
			em.persist(pasLog);
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//�ֹ�������MO��ֻ������һ������Ϸ��䣬�����ǿ�棬Ҳ���������·ֲ�
	private void scheAddMoLine(long parentRrn) throws ClientException {
		try{
			RepScheBomLine parentBom = new RepScheBomLine();
			parentBom.setMaterialParentRrn(null);
			parentBom.setMaterialRrn(parentRrn);
			parentBom.setPath(null);
			parentBom.setPathLevel(0L);
			parentBom.setLineNo(10L);
			parentBom.setUnitQty(BigDecimal.ONE);
			parentBom.setQtyBom(BigDecimal.ONE);
			parentBom.setQtyNeed(BigDecimal.ONE);
			parentBom.setQty(BigDecimal.ONE);
			em.persist(parentBom);
			
			List<ManufactureOrderBom> moBoms = getMoChildrenBom(139420, parentBom.getMaterialRrn());
			for (ManufactureOrderBom moBom : moBoms) {
				RepScheBomLine childtBom = new RepScheBomLine(moBom);
				childtBom.setQtyNeed(parentBom.getQty().multiply(moBom.getUnitQty()));
				childtBom.setQty(BigDecimal.ZERO);
				childtBom.setPath(parentBom.getMaterialRrn() + "/");
				em.persist(childtBom);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (ClientParameterException e){
			throw e;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//����
	@Override
	public List<ManufactureOrderLine> getMoLineByWorkCenter2(long orgRrn, long workCenterRrn, String whereClause) throws ClientException {
		StringBuffer sql = new StringBuffer(" SELECT ManufactureOrderLine FROM ManufactureOrderLine ManufactureOrderLine ");
		sql.append(" WHERE ");
		sql.append(" workCenterRrn = ? "); 
		if (whereClause != null && !"".equals(whereClause.trim())) {
			//sql.append(" WHERE ");
			//sql.append(whereClause);
			sql.append(" AND "+ whereClause);
		} else {
			sql.append(" AND (lineStatus = '" + Documentation.STATUS_APPROVED + "')" );
		}
		sql.append(" ORDER BY dateStart ");
		logger.debug(sql);
		
//		StringBuffer sql2 = new StringBuffer();
//		sql2.append(" SELECT S.MATERIAL_RRN, NVL(S.QTY_ONHAND + S.QTY_DIFF, 0) QTY_ONHAND FROM INV_STORAGE S, ");
//		sql2.append(" (SELECT DISTINCT MATERIAL_RRN FROM WIP_MO_LINE  ");
//		sql2.append("   WHERE WORKCENTER_RRN = ?  ");
//		sql2.append(" AND (LINE_STATUS = 'DRAFTED' OR LINE_STATUS = 'APPROVED')) W  ");
//		sql2.append(" WHERE S.MATERIAL_RRN = W.MATERIAL_RRN  ");
//		sql2.append(" AND S.WAREHOUSE_RRN = ?  ");
//		logger.debug(sql2);
//		
//		StringBuffer sql3 = new StringBuffer();
//		sql3.append(" SELECT MO.* FROM (SELECT  ");
//		sql3.append("  B.MATERIAL_RRN,  ");
//		sql3.append("  SUM(B.QTY_UNIT * (M.QTY_PRODUCT - M.QTY_RECEIVE)) QTY_MO_ALLOCATION ");
//		sql3.append("  FROM WIP_MO_BOM B, WIP_MO M  ");
//		sql3.append("  WHERE  1=1 ");
//		sql3.append("  AND B.ORG_RRN = ? ");
//		sql3.append("  AND B.MO_RRN = M.OBJECT_RRN  ");
//		sql3.append("  AND M.DOC_STATUS IN ('APPROVED', 'DRAFTED') ");
//		sql3.append("  GROUP BY B.MATERIAL_RRN ");
//		sql3.append(") MO ");
//		sql3.append("INNER JOIN (SELECT DISTINCT MATERIAL_RRN FROM WIP_MO_LINE  ");
//		sql3.append("  WHERE WORKCENTER_RRN = ? ");
//		sql3.append("  AND (LINE_STATUS = 'DRAFTED' OR LINE_STATUS = 'APPROVED')) W  ");
//		sql3.append("ON MO.MATERIAL_RRN = W.MATERIAL_RRN ");		
//		logger.debug(sql3);
		
		try{
			Warehouse writeOffWarehouse = invManager.getWriteOffWarehouse(orgRrn);
			Map<Long, BigDecimal> qtyOnhandMap = new HashMap<Long, BigDecimal>();
			Map<Long, BigDecimal> qtyAllocationMap = new HashMap<Long, BigDecimal>();
			
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, workCenterRrn);
			List<ManufactureOrderLine> moLineList = query.getResultList();
			
//			query = em.createNativeQuery(sql2.toString());
//			query.setParameter(1, workCenterRrn);
//			query.setParameter(2, writeOffWarehouse.getObjectRrn());
//			List<Object[]> result = query.getResultList();
//			for (Object[] row : result) {
//				qtyOnhandMap.put(Long.parseLong(String.valueOf(row[0])), (BigDecimal)row[1]);
//			}
			
//			query = em.createNativeQuery(sql3.toString());
//			query.setParameter(1, orgRrn);
//			query.setParameter(2, workCenterRrn);
//			result = query.getResultList();
//			for (Object[] row : result) {
//				qtyAllocationMap.put(Long.parseLong(String.valueOf(row[0])), (BigDecimal)row[1]);
//			}
			
//			for(ManufactureOrderLine moLine : moLineList){
//				if (qtyOnhandMap.containsKey(moLine.getMaterialRrn())) {
//					moLine.setQtyCurrentOnHand(qtyOnhandMap.get(moLine.getMaterialRrn()));
//				} else {
//					moLine.setQtyCurrentOnHand(BigDecimal.ZERO);
//				}
//				if (qtyAllocationMap.containsKey(moLine.getMaterialRrn())) {
//					moLine.setQtyCurrentAllocation(qtyAllocationMap.get(moLine.getMaterialRrn()));
//				} else {
//					moLine.setQtyCurrentAllocation(BigDecimal.ZERO);
//				}
//			}
			return moLineList;
			
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//����
	@Override
	public ManufactureOrderLine getMoLineByWorkCenter2Qty(long orgRrn, long workCenterRrn, String whereClause,ManufactureOrderLine moLine) throws ClientException {
 
		
		StringBuffer sql2 = new StringBuffer();
		sql2.append(" SELECT S.MATERIAL_RRN, NVL(S.QTY_ONHAND + S.QTY_DIFF, 0) QTY_ONHAND FROM INV_STORAGE S, ");
		sql2.append(" (SELECT DISTINCT MATERIAL_RRN FROM WIP_MO_LINE  ");
		sql2.append("   WHERE WORKCENTER_RRN = ?  ");
		sql2.append(" AND (LINE_STATUS = 'DRAFTED' OR LINE_STATUS = 'APPROVED')) W  ");
		sql2.append(" WHERE S.MATERIAL_RRN = W.MATERIAL_RRN  ");
		sql2.append(" and  S.MATERIAL_RRN =");
		sql2.append(moLine.getMaterialRrn());
		sql2.append(" AND S.WAREHOUSE_RRN = ?  ");
		logger.debug(sql2);
		
		StringBuffer sql3 = new StringBuffer();
		sql3.append(" SELECT MO.* FROM (SELECT  ");
		sql3.append("  B.MATERIAL_RRN,  ");
		sql3.append("  SUM(B.QTY_UNIT * (M.QTY_PRODUCT - M.QTY_RECEIVE)) QTY_MO_ALLOCATION ");
		sql3.append("  FROM WIP_MO_BOM B, WIP_MO M  ");
		sql3.append("  WHERE  1=1 ");
		sql3.append("  AND B.ORG_RRN = ? ");
		sql3.append("  AND B.MATERIAL_RRN =");
		sql3.append(moLine.getMaterialRrn());
		sql3.append("  AND B.MO_RRN = M.OBJECT_RRN  ");
		sql3.append("  AND M.DOC_STATUS IN ('APPROVED', 'DRAFTED') ");
		sql3.append("  GROUP BY B.MATERIAL_RRN ");
		sql3.append(") MO ");
		sql3.append("INNER JOIN (SELECT DISTINCT MATERIAL_RRN FROM WIP_MO_LINE  ");
		sql3.append("  WHERE WORKCENTER_RRN = ? ");
		sql3.append("  AND (LINE_STATUS = 'DRAFTED' OR LINE_STATUS = 'APPROVED')) W  ");
		sql3.append("ON MO.MATERIAL_RRN = W.MATERIAL_RRN ");		
		logger.debug(sql3);
		
		try{
			Warehouse writeOffWarehouse = invManager.getWriteOffWarehouse(orgRrn);
			Map<Long, BigDecimal> qtyOnhandMap = new HashMap<Long, BigDecimal>();
			Map<Long, BigDecimal> qtyAllocationMap = new HashMap<Long, BigDecimal>();
			
			Query query  = em.createNativeQuery(sql2.toString());
			query.setParameter(1, workCenterRrn);
			query.setParameter(2, writeOffWarehouse.getObjectRrn());
			List<Object[]> result = query.getResultList();
			for (Object[] row : result) {
				qtyOnhandMap.put(Long.parseLong(String.valueOf(row[0])), (BigDecimal)row[1]);
			}
			
			query = em.createNativeQuery(sql3.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, workCenterRrn);
			result = query.getResultList();
			for (Object[] row : result) {
				qtyAllocationMap.put(Long.parseLong(String.valueOf(row[0])), (BigDecimal)row[1]);
			}
			
			if (qtyOnhandMap.containsKey(moLine.getMaterialRrn())) {
				moLine.setQtyCurrentOnHand(qtyOnhandMap.get(moLine.getMaterialRrn()));
			} else {
				moLine.setQtyCurrentOnHand(BigDecimal.ZERO);
			}
			if (qtyAllocationMap.containsKey(moLine.getMaterialRrn())) {
				moLine.setQtyCurrentAllocation(qtyAllocationMap.get(moLine.getMaterialRrn()));
			} else {
				moLine.setQtyCurrentAllocation(BigDecimal.ZERO);
			}
			
			return moLine;
			
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void runSchedulePurchase2(long orgRrn,long userRrn) throws ClientException{
		try{
			StringBuffer sqlSP = new StringBuffer();
			sqlSP.append( " {call SP_SCHEDULE_MATERIAL()} " );
			Query query = em.createNativeQuery(sqlSP.toString());
			query.executeUpdate();
			
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void runSchedulePurchase3(long orgRrn,long userRrn) throws ClientException{
		try{
			StringBuffer sqlSP = new StringBuffer();
			sqlSP.append( " {call sp_schedule_hjd()} " );
			Query query = em.createNativeQuery(sqlSP.toString());
			query.executeUpdate();
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public void runSchedulePurchase4(long orgRrn,long userRrn) throws ClientException{
		try{
			StringBuffer sqlSP = new StringBuffer();
			sqlSP.append( " {call sp_schedule_hjd2()} " );
			Query query = em.createNativeQuery(sqlSP.toString());
			query.executeUpdate();
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
}

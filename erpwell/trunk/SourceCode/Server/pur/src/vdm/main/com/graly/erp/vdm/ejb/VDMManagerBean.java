package com.graly.erp.vdm.ejb;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.graly.erp.base.client.BASManager;
import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Material;
import com.graly.erp.vdm.client.VDMManager;
import com.graly.erp.vdm.model.MaterialAssessment;
import com.graly.erp.vdm.model.Vendor;
import com.graly.erp.vdm.model.VendorAssessment;
import com.graly.erp.vdm.model.VendorMaterial;
import com.graly.erp.vdm.model.VendorYearTarget;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.core.exception.ClientParameterException;
import com.graly.framework.security.model.ADUser;

@Stateless
@Remote(VDMManager.class)
@Local(VDMManager.class)
public class VDMManagerBean implements VDMManager{
	private static final Logger logger = Logger.getLogger(VDMManagerBean.class);
	
	@PersistenceContext
	private EntityManager em;
	
	@EJB
	private ADManager adManager;
	
	@EJB
	private BASManager basManager;
	
	public List<MaterialAssessment> getMaterialAssessment(String whereClause)throws ClientException {
		StringBuffer sql = new StringBuffer();
		boolean flag=false;
		if(!"".equalsIgnoreCase(whereClause)&&whereClause!=null){
			sql.append("SELECT MaterialAssessment FROM MaterialAssessment as MaterialAssessment Where ");
			sql.append(whereClause);
			sql.append(" order by created desc");
			flag=true;
		}else{
			sql.append("select * from (select * from vdm_material_assessment t  order by t.created desc) where rownum<=10");
		}
		logger.debug(sql);
		List<MaterialAssessment> materialAssessments = new ArrayList<MaterialAssessment>();
		try{
			Query query;
			if(flag){
				query = em.createQuery(sql.toString());
			}else{
				query = em.createNativeQuery(sql.toString(),MaterialAssessment.class);
			}
			materialAssessments = (List<MaterialAssessment>)query.getResultList();
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return materialAssessments;
	}

	public void saveMaterialAssessment(MaterialAssessment materialAssessment, long userRrn)throws ClientException {
		try{
			materialAssessment.setIsActive(true);
			materialAssessment.setUpdatedBy(userRrn);
			materialAssessment.setUpdated(new Date());
			//set userid
			ADUser user=em.find(ADUser.class,userRrn);
			materialAssessment.setAssessmentUserId(user.getUserName());
			//set materId
			Material material= em.find(Material.class, materialAssessment.getMaterialRrn());
			materialAssessment.setMaterialId(material.getMaterialId());
			//set vendor
			Vendor vendor=em.find(Vendor.class, materialAssessment.getVendorRrn());
			materialAssessment.setVendorId(vendor.getVendorId());
			
			if (materialAssessment.getObjectRrn() == null) {
				materialAssessment.setCreatedBy(userRrn);
				materialAssessment.setCreated(new Date());
				em.persist(materialAssessment);
			} else {
				em.merge(materialAssessment);
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	private Date getSwitchDate(String startDate) throws ClientException {
		Date dt = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if(!"".equals(startDate)){
				dt = dateFormat.parse(startDate);
			}else{
				Calendar calender = Calendar.getInstance();
				calender.add(Calendar.DATE,-1);
				Date now=calender.getTime();
				dt=dateFormat.parse(dateFormat.format(now));
			}
			
			return dt;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}

	}
	
	public VendorMaterial saveVendorMaterial(VendorMaterial vendorMaterial, long userRrn) throws ClientException {
		try{
			String whereClause = " materialRrn = '" + vendorMaterial.getMaterialRrn() + "' AND  isPrimary = 'Y'";
			//查询出这个物料的主供应商，返回一个list
			List<VendorMaterial> vendorMaterials = adManager.
				getEntityList(vendorMaterial.getOrgRrn(), VendorMaterial.class, 2, whereClause, "");
			
			if(vendorMaterial.getIsPrimary()){
				//如果页面上传来的数据，也是主供应商。那么将刚才查询出来的数据全部设为非主供应商
				for (VendorMaterial vm : vendorMaterials) {
					vm.setIsPrimary(false);
					em.merge(vm);
				}
			}else{
				//如果页面上传来的数据，不是主供应商
				if(vendorMaterials.size() == 0){
					//如果数据库中不存在这个物料的主供应商
					throw new ClientException("error.no_primary_vendor");
				}else if(vendorMaterials.size() == 1){
					//
					VendorMaterial vm = vendorMaterials.get(0);
					if(vm.getObjectRrn() != null && vm.getObjectRrn().equals(vendorMaterial.getObjectRrn())) {
						throw new ClientException("error.no_primary_vendor");						
					}
				}
			}
			
			vendorMaterial.setIsActive(true);
			vendorMaterial.setUpdatedBy(userRrn);
			vendorMaterial.setUpdated(new Date());
			if (vendorMaterial.getObjectRrn() == null) {
				VendorMaterial vm = getVendorMaterial(vendorMaterial.getVendorRrn(), vendorMaterial.getMaterialRrn());
				if (vm != null){
					throw new ClientParameterException("pur.vendor_material_duplicate", vm.getVendorId());
				} else {
					vendorMaterial.setCreatedBy(userRrn);
					vendorMaterial.setCreated(new Date());
					em.persist(vendorMaterial);
				}
			} else {
				vendorMaterial = em.merge(vendorMaterial);
			}
		}catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return vendorMaterial;
	}
	
	public VendorMaterial getVendorMaterial(long vendorRrn, long materialRrn)  throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT VendorMaterial FROM VendorMaterial as VendorMaterial ");
		sql.append(" WHERE vendorRrn=? ");
		sql.append(" AND materialRrn=? ");
		VendorMaterial vendorMaterial = null;
		try{
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, vendorRrn);
			query.setParameter(2, materialRrn);
			List<VendorMaterial> vendorMaterials = (List<VendorMaterial>)query.getResultList();
			if (vendorMaterials.size() > 0) {
				vendorMaterial = vendorMaterials.get(0);
			}
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return vendorMaterial;
	}
	
	public VendorMaterial getPrimaryVendor(Long materialRrn) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT VendorMaterial FROM VendorMaterial as VendorMaterial ");
		sql.append(" WHERE materialRrn = ? ");
		sql.append(" AND isPrimary = 'Y' ");
		logger.debug(sql);
		List<VendorMaterial> vendors = new ArrayList<VendorMaterial>();
		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, materialRrn);
			vendors = query.getResultList();
			if (vendors.size() > 0) {
				return vendors.get(0);
			}
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return null;
	}
	
	public String generateVendorCode(long orgRrn, String docType) throws ClientException {
		StringBuffer verodrCode = new StringBuffer("");
		verodrCode.append(basManager.getDocCode(orgRrn, docType));
		verodrCode.append(generateNextNumber(orgRrn, docType));
		return verodrCode.toString();
	}
	
	public String generateNextNumber(long orgRrn, String docType) throws ClientException {
		try{
			long verodrCode = adManager.getNextSequence(orgRrn, docType);
			String seqSuffix = String.format("%06d", verodrCode);
			return seqSuffix;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public VendorAssessment generateVendorAssessment(long orgRrn, 
			long materialRrn, long vendorRrn, String purchaser, Date dateStart, Date dateEnd) throws ClientException {
		StringBuffer sqlPass = new StringBuffer();
		sqlPass.append(" SELECT SUM (L.QTY_IQC) QTY_IQC, ");
		sqlPass.append("        SUM ( ");
		sqlPass.append("           CASE L.ERCEIVE_CONDITION ");
		sqlPass.append("              WHEN 'NORMAL' THEN L.QTY_QUALIFIED ");
		sqlPass.append("              WHEN 'CONCESSION' THEN 0 ");
		sqlPass.append("              ELSE L.QTY_QUALIFIED ");
		sqlPass.append("           END) ");
		sqlPass.append("           QTY_QUALIFIED ");
		sqlPass.append(" FROM (SELECT * FROM INV_IQC I WHERE  ");
		sqlPass.append(ADBase.SQL_BASE_CONDITION);
		sqlPass.append(" ) I, INV_IQC_LINE L, PUR_PO P");
		sqlPass.append(" WHERE I.OBJECT_RRN = L.IQC_RRN ");
		sqlPass.append(" AND I.PO_RRN = P.OBJECT_RRN ");
		sqlPass.append(" AND L.MATERIAL_RRN = ? ");
		sqlPass.append(" AND P.VENDOR_RRN = ? ");
		if (purchaser != null && purchaser.trim().length() > 0) {
			sqlPass.append(" AND P.PURCHASER = '");
			sqlPass.append(purchaser);
			sqlPass.append("'");
		}
		sqlPass.append(" AND TRUNC(I.DATE_APPROVED) >= TRUNC(?) AND TRUNC(I.DATE_APPROVED) <= TRUNC(?) ");

		StringBuffer sqlInTime = new StringBuffer();
		sqlInTime.append(" SELECT SUM(L.QTY_RECEIPT) QTY_RECEIPT ");
		sqlInTime.append(" FROM (SELECT * FROM INV_RECEIPT I WHERE  ");
		sqlInTime.append(ADBase.SQL_BASE_CONDITION);
		sqlInTime.append(" ) I, INV_RECEIPT_LINE L, PUR_PO P, PUR_PO_LINE O");
		sqlInTime.append(" WHERE I.OBJECT_RRN = L.RECEIPT_RRN ");
		sqlInTime.append(" AND I.PO_RRN = P.OBJECT_RRN ");
		sqlInTime.append(" AND L.PO_LINE_RRN = O.OBJECT_RRN ");
		sqlInTime.append(" AND TRUNC(I.DATE_CREATED) <= TRUNC(O.DATE_END) ");
		sqlInTime.append(" AND L.MATERIAL_RRN = ? ");
		sqlInTime.append(" AND P.VENDOR_RRN = ? ");
		if (purchaser != null && purchaser.trim().length() > 0) {
			sqlInTime.append(" AND P.PURCHASER = '");
			sqlInTime.append(purchaser);
			sqlInTime.append("'");
		}
		sqlInTime.append(" AND TRUNC(I.DATE_APPROVED) >= TRUNC(?) AND TRUNC(I.DATE_APPROVED) <= TRUNC(?) ");
		
		
		StringBuffer sqlOutTime = new StringBuffer();
		sqlOutTime.append(" SELECT SUM(L.QTY_RECEIPT) QTY_RECEIPT ");
		sqlOutTime.append(" FROM (SELECT * FROM INV_RECEIPT I WHERE  ");
		sqlOutTime.append(ADBase.SQL_BASE_CONDITION);
		sqlOutTime.append(" ) I, INV_RECEIPT_LINE L, PUR_PO P, PUR_PO_LINE O");
		sqlOutTime.append(" WHERE I.OBJECT_RRN = L.RECEIPT_RRN ");
		sqlOutTime.append(" AND I.PO_RRN = P.OBJECT_RRN ");
		sqlOutTime.append(" AND L.PO_LINE_RRN = O.OBJECT_RRN ");
		sqlOutTime.append(" AND TRUNC(I.DATE_CREATED) > TRUNC(O.DATE_END) ");
		sqlOutTime.append(" AND L.MATERIAL_RRN = ? ");
		sqlOutTime.append(" AND P.VENDOR_RRN = ? ");
		if (purchaser != null && purchaser.trim().length() > 0) {
			sqlOutTime.append(" AND P.PURCHASER = '");
			sqlOutTime.append(purchaser);
			sqlOutTime.append("'");
		}
		sqlOutTime.append(" AND TRUNC(I.DATE_APPROVED) >= TRUNC(?) AND TRUNC(I.DATE_APPROVED) <= TRUNC(?) ");
		
		StringBuffer sqlIn = new StringBuffer();
		sqlIn.append(" SELECT SUM(L.QTY_MOVEMENT) QTY_MOVEMENT ");
		sqlIn.append(" FROM (SELECT * FROM INV_MOVEMENT I WHERE  ");
		sqlIn.append(ADBase.SQL_BASE_CONDITION);
		sqlIn.append(" ) I, INV_MOVEMENT_LINE L, PUR_PO P ");
		sqlIn.append(" WHERE I.OBJECT_RRN = L.MOVEMENT_RRN ");
		sqlIn.append(" AND I.PO_RRN = P.OBJECT_RRN ");
		sqlIn.append(" AND L.MATERIAL_RRN = ? ");
		sqlIn.append(" AND I.VENDOR_RRN = ? ");
		if (purchaser != null && purchaser.trim().length() > 0) {
			sqlIn.append(" AND P.PURCHASER = '");
			sqlIn.append(purchaser);
			sqlIn.append("'");
		}
		sqlIn.append(" AND TRUNC(I.DATE_APPROVED) BETWEEN TRUNC(?) AND TRUNC(?) ");
			
		
		VendorAssessment va = new VendorAssessment();
		try {
			Material material = em.find(Material.class, materialRrn);
			va.setMaterialRrn(materialRrn);
			va.setMaterialId(material.getMaterialId());
			va.setMaterialName(material.getName());
			Vendor vendor = em.find(Vendor.class, vendorRrn);
			va.setVendorRrn(vendorRrn);
			va.setVerndorId(vendor.getVendorId());
			va.setVendorName(vendor.getCompanyName());
			
			Query query = em.createNativeQuery(sqlPass.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, materialRrn);
			query.setParameter(3, vendorRrn);
			query.setParameter(4, dateStart);
			query.setParameter(5, dateEnd);
			Object[] resultPass = (Object[])query.getSingleResult();
			BigDecimal qtyIqc = (BigDecimal)resultPass[0];//检验数
			BigDecimal qtyPass = (BigDecimal)resultPass[1];//合格数

			if (qtyIqc != null && qtyIqc.compareTo(BigDecimal.ZERO) > 0) {
				//合格率 = 合格数 / 检验数
				va.setPassPercent(qtyPass.multiply(new BigDecimal(100)).divide(qtyIqc, 2, RoundingMode.HALF_UP));
			}
			
			query = em.createNativeQuery(sqlInTime.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, materialRrn);
			query.setParameter(3, vendorRrn);
			query.setParameter(4, dateStart);
			query.setParameter(5, dateEnd);
			BigDecimal qtyInTime = (BigDecimal)query.getSingleResult();//及时入库数
			
			query = em.createNativeQuery(sqlOutTime.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, materialRrn);
			query.setParameter(3, vendorRrn);
			query.setParameter(4, dateStart);
			query.setParameter(5, dateEnd);
			BigDecimal qtyOutTime = (BigDecimal)query.getSingleResult();//超时入库数
			if (qtyInTime == null && qtyOutTime == null) {
			} else if (qtyInTime == null) {
				qtyInTime = BigDecimal.ZERO;
			} else if (qtyOutTime == null) {
				qtyOutTime = BigDecimal.ZERO;
			}
			if (qtyInTime != null && qtyInTime.add(qtyOutTime).compareTo(BigDecimal.ZERO) > 0) {
				//及时率 = 及时收货数 / 采购总数
				va.setInTimePercent(qtyInTime.multiply(new BigDecimal(100)).divide(qtyInTime.add(qtyOutTime), 2, RoundingMode.HALF_UP));
			}
			
			query = em.createNativeQuery(sqlIn.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, materialRrn);
			query.setParameter(3, vendorRrn);
			query.setParameter(4, dateStart);
			query.setParameter(5, dateEnd);
			BigDecimal qtyIn = (BigDecimal)query.getSingleResult();
			va.setQtyIn(qtyIn);
			
			va.setPurchaser(purchaser);
			
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return va;
	}
	
	public List<VendorMaterial> getOnlyMainVendorMaterialList(long orgRrn, int maxResult) throws ClientException {
		try {
			List<VendorMaterial> vms = new ArrayList<VendorMaterial>();
			StringBuffer sql = new StringBuffer(" SELECT VM.VENDOR_RRN, V.VENDOR_ID, V.COMPANY_NAME, " +
					" VM.MATERIAL_RRN, M.MATERIAL_ID, M.NAME, " +
					" VM.REFERENCED_PRICE, VM.LAST_PRICE, VM.LEAD_TIME, VM.LEAST_QUANTITY, VM.PURCHASER, " +
					" VM.ADVANCE_RATIO, VM.IS_PRIMARY");
			
			sql.append(" FROM VDM_VENDOR_MATERIAL VM, PDM_MATERIAL M, VDM_VENDOR V ");
			sql.append(" WHERE VM.MATERIAL_RRN IN ");
			sql.append(" (");
				sql.append(" SELECT DISTINCT MATERIAL_RRN FROM VDM_VENDOR_MATERIAL ");
				sql.append(" WHERE ");
				sql.append(ADBase.SQL_BASE_CONDITION);
				sql.append(" AND IS_PRIMARY = 'Y' ");
				
				sql.append(" MINUS ");
				
				sql.append(" SELECT DISTINCT MATERIAL_RRN FROM VDM_VENDOR_MATERIAL ");
				sql.append(" WHERE ");
				sql.append(ADBase.SQL_BASE_CONDITION);
				sql.append(" AND IS_PRIMARY = 'N' ");
			sql.append(" ) ");
			sql.append(" AND VM.MATERIAL_RRN = M.OBJECT_RRN AND VM.VENDOR_RRN = V.OBJECT_RRN");

			Query query = em.createNativeQuery(sql.toString());
			query.setMaxResults(maxResult);
			query.setParameter(1, orgRrn);
			query.setParameter(2, orgRrn);
			List<Object[]> objs = (List<Object[]>)query.getResultList();
			VendorMaterial vm = null;
			Vendor v = null;
			Material m = null;
			for(Object[] obj : objs) {
				vm = new VendorMaterial();
				v = new Vendor();
				m = new Material();
				
				vm.setVendorRrn(((BigDecimal)obj[0]).longValue());
				vm.setVendorId((String)obj[1]);
				v.setVendorId((String)obj[1]);
				v.setCompanyName((String)obj[2]);
				vm.setVendor(v);
				
				vm.setMaterialRrn(((BigDecimal)obj[3]).longValue());
				vm.setMaterialId((String)obj[4]);
				m.setMaterialId((String)obj[4]);
				m.setName((String)obj[5]);
				vm.setMaterial(m);
				
				vm.setReferencedPrice(((BigDecimal)obj[6]));
				vm.setLastPrice(((BigDecimal)obj[7]));
				vm.setLeadTime(((BigDecimal)obj[8]).longValue());
				vm.setLeastQuantity(((BigDecimal)obj[9]));
				vm.setPurchaser((String)obj[10]);
				vm.setAdvanceRatio(((BigDecimal)obj[11]));
				vm.setIsPrimary("Y".equals((String)obj[12]) ? true : false);				
				vms.add(vm);
			}
			return vms;
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	// 获得最近一段时间有进行出入库记录的物料供应商列表
	public List<VendorMaterial> getVendorMaterialList(long orgRrn, Date dateStart, Date dateEnd, String whereClause) throws ClientException {
		try {
			List<VendorMaterial> vms = new ArrayList<VendorMaterial>();
			StringBuffer sql = new StringBuffer("");
			sql.append(" SELECT V.MATERIAL_RRN, V.VENDOR_RRN, V.PURCHASER FROM ");
			sql.append(" (");
				sql.append(" SELECT L.MATERIAL_RRN, P.VENDOR_RRN, P.PURCHASER ");
				sql.append(" FROM INV_RECEIPT I, INV_RECEIPT_LINE L, PUR_PO P ");
				sql.append(" WHERE I.OBJECT_RRN = L.RECEIPT_RRN ");
				sql.append(" AND I.PO_RRN = P.OBJECT_RRN ");
				sql.append(" AND P.ORG_RRN = ? ");
				sql.append(" AND TRUNC(I.DATE_APPROVED) BETWEEN TRUNC(?) AND TRUNC(?) ");
				
				sql.append(" UNION ");
				
				sql.append(" SELECT L.MATERIAL_RRN, P.VENDOR_RRN, P.PURCHASER ");
				sql.append(" FROM INV_IQC I, INV_IQC_LINE L, PUR_PO P ");
				sql.append(" WHERE I.OBJECT_RRN = L.IQC_RRN ");
				sql.append(" AND I.PO_RRN = P.OBJECT_RRN ");
				sql.append(" AND P.ORG_RRN = ? ");
				sql.append(" AND TRUNC(I.DATE_APPROVED) BETWEEN TRUNC(?) AND TRUNC(?) ");
				
				sql.append(" UNION ");
				
				sql.append(" SELECT L.MATERIAL_RRN, P.VENDOR_RRN, P.PURCHASER ");
				sql.append(" FROM INV_MOVEMENT I, INV_MOVEMENT_LINE L, PUR_PO P ");
				sql.append(" WHERE I.OBJECT_RRN = L.MOVEMENT_RRN ");
				sql.append(" AND I.PO_RRN = P.OBJECT_RRN ");
				sql.append(" AND P.ORG_RRN = ? ");
				sql.append(" AND I.DOC_TYPE = 'PIN' ");
				sql.append(" AND TRUNC(I.DATE_APPROVED) BETWEEN TRUNC(?) AND TRUNC(?) ");
			sql.append(") V");
			sql.append(" WHERE ");
			if(whereClause != null && !"".equals(whereClause.trim())) {
				sql.append(whereClause);
			}

			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, dateStart);
			query.setParameter(3, dateEnd);
			query.setParameter(4, orgRrn);
			query.setParameter(5, dateStart);
			query.setParameter(6, dateEnd);
			query.setParameter(7, orgRrn);
			query.setParameter(8, dateStart);
			query.setParameter(9, dateEnd);
			List<Object[]> objs = (List<Object[]>)query.getResultList();
			VendorMaterial vm = null;
			for(Object[] obj : objs) {
				vm = new VendorMaterial();				
				vm.setMaterialRrn(((BigDecimal)obj[0]).longValue());
				vm.setVendorRrn(((BigDecimal)obj[1]).longValue());
				vm.setPurchaser((String)obj[2]);				
				vms.add(vm);
			}
			return vms;
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	
	// 获得最近一段时间有进行出入库记录的物料供应商列表
	public List<VendorMaterial> getVendorMaterialList2(long orgRrn, Date dateStart, Date dateEnd, String whereClause) throws ClientException {
		try {
			List<VendorMaterial> vms = new ArrayList<VendorMaterial>();
			StringBuffer sql = new StringBuffer("");
			sql.append(" SELECT V.MATERIAL_RRN, V.VENDOR_RRN, V.PURCHASER FROM ");
			sql.append(" (");
				sql.append(" SELECT L.MATERIAL_RRN, P.VENDOR_RRN, P.PURCHASER ");
				sql.append(" FROM INV_RECEIPT I, INV_RECEIPT_LINE L, PUR_PO P ");
				sql.append(" WHERE I.OBJECT_RRN = L.RECEIPT_RRN ");
				sql.append(" AND I.PO_RRN = P.OBJECT_RRN ");
				sql.append(" AND P.ORG_RRN = ? ");
				sql.append(" AND TRUNC(I.DATE_APPROVED) >= TRUNC(?) AND TRUNC(I.DATE_APPROVED) <= TRUNC(?) ");
				
				sql.append(" UNION ");
				
				sql.append(" SELECT L.MATERIAL_RRN, P.VENDOR_RRN, P.PURCHASER ");
				sql.append(" FROM INV_IQC I, INV_IQC_LINE L, PUR_PO P ");
				sql.append(" WHERE I.OBJECT_RRN = L.IQC_RRN ");
				sql.append(" AND I.PO_RRN = P.OBJECT_RRN ");
				sql.append(" AND P.ORG_RRN = ? ");
				sql.append(" AND TRUNC(I.DATE_APPROVED) >= TRUNC(?) AND TRUNC(I.DATE_APPROVED) <= TRUNC(?) ");
				
				sql.append(" UNION ");
				
				sql.append(" SELECT L.MATERIAL_RRN, P.VENDOR_RRN, P.PURCHASER ");
				sql.append(" FROM INV_MOVEMENT I, INV_MOVEMENT_LINE L, PUR_PO P ");
				sql.append(" WHERE I.OBJECT_RRN = L.MOVEMENT_RRN ");
				sql.append(" AND I.PO_RRN = P.OBJECT_RRN ");
				sql.append(" AND P.ORG_RRN = ? ");
				sql.append(" AND I.DOC_TYPE = 'PIN' ");
				sql.append(" AND TRUNC(I.DATE_APPROVED) >= TRUNC(?) AND TRUNC(I.DATE_APPROVED) <= TRUNC(?) ");
			sql.append(") V");
			sql.append(" WHERE ");
			if(whereClause != null && !"".equals(whereClause.trim())) {
				sql.append(whereClause);
			}

			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, dateStart);
			query.setParameter(3, dateEnd);
			query.setParameter(4, orgRrn);
			query.setParameter(5, dateStart);
			query.setParameter(6, dateEnd);
			query.setParameter(7, orgRrn);
			query.setParameter(8, dateStart);
			query.setParameter(9, dateEnd);
			List<Object[]> objs = (List<Object[]>)query.getResultList();
			VendorMaterial vm = null;
			for(Object[] obj : objs) {
				vm = new VendorMaterial();				
				vm.setMaterialRrn(((BigDecimal)obj[0]).longValue());
				vm.setVendorRrn(((BigDecimal)obj[1]).longValue());
				vm.setPurchaser((String)obj[2]);				
				vms.add(vm);
			}
			return vms;
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	@Override
	public List<Vendor> getVendorByVendorId(long orgRrn, String vendorId) throws ClientException {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT Vendor FROM Vendor Vendor ");
			sql.append(" WHERE ");
			sql.append(ADBase.BASE_CONDITION);
			sql.append(" AND vendorId = ? ");
			
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, orgRrn);
			query.setParameter(2, vendorId);
			return query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	@Override
	public List<VendorMaterial> getVendorMaterials(long vendorRrn, long materialRrn)
			throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT VendorMaterial FROM VendorMaterial as VendorMaterial ");
		sql.append(" WHERE vendorRrn = ? ");
		sql.append(" and materialRrn = ? ");
		try{
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, vendorRrn);
			query.setParameter(2, materialRrn);
			List<VendorMaterial> vendorMaterials = (List<VendorMaterial>)query.getResultList();
			return vendorMaterials;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	@Override
	public List<VendorMaterial> getVendorMaterialsByMaterial(long materialRrn)
			throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT VendorMaterial FROM VendorMaterial as VendorMaterial ");
		sql.append(" WHERE materialRrn = ? ");
		try{
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, materialRrn);
			List<VendorMaterial> vendorMaterials = (List<VendorMaterial>)query.getResultList();
			return vendorMaterials;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	@Override
	public List<VendorYearTarget> getVendorYearTarget(long vendorRrn, String targetYear)
			throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT VendorYearTarget FROM VendorYearTarget as VendorYearTarget ");
		sql.append(" WHERE vendorRrn = ? ");
		sql.append(" and targetYear = ? ");
		try{
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, vendorRrn);
			query.setParameter(2, targetYear);
			List<VendorYearTarget> VendorYearTargets = (List<VendorYearTarget>)query.getResultList();
			return VendorYearTargets;
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
}
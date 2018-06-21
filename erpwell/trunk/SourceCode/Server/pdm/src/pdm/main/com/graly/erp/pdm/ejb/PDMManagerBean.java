package com.graly.erp.pdm.ejb;


import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
import org.hibernate.Session;
import org.hibernate.lob.SerializableBlob;

import com.graly.erp.base.model.Documentation;
import com.graly.erp.base.model.DocumentationLine;
import com.graly.erp.base.model.Material;
import com.graly.erp.base.model.MaterialHis;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.pdm.model.Bom;
import com.graly.erp.pdm.model.BomDetail;
import com.graly.erp.pdm.model.MaterialActual;
import com.graly.erp.pdm.model.MaterialOptional;
import com.graly.erp.pdm.model.MaterialUnSelected;
import com.graly.erp.pdm.model.VPdmBom;
import com.graly.erp.pdm.model.WmsMaterial;
import com.graly.erp.ppm.model.TpsLinePrepare;
import com.graly.erp.product.client.CANAManager;
import com.graly.erp.product.model.CanaProduct;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.core.exception.ClientParameterException;

@Stateless
@Remote(PDMManager.class)
@Local(PDMManager.class)
public class PDMManagerBean implements PDMManager {
	private static final Logger logger = Logger.getLogger(PDMManagerBean.class);
	
	@PersistenceContext
	private EntityManager em;
	
	@EJB
	private ADManager adManager;
	
	@EJB
	private CANAManager canaManager;
	
	public Material saveMaterial(long tableRrn, Material material, long userRrn) throws ClientException {
		try{
			Date now = new Date();
			//共享物料Org=0
			if (material.getIsShare()) {
				material.setOrgRrn(0L);
			}
			if (material.getInventoryUom() != null) {
				material.setPurchaseUom(material.getInventoryUom());
			}
			material = (Material)adManager.saveEntity(tableRrn, material, userRrn);
			if(material.getOrgRrn()==139420L){//WMS物料
				WmsMaterial wmsMaterial = new WmsMaterial();
				wmsMaterial.setOrgRrn(material.getOrgRrn());
				wmsMaterial.setIsActive(true);
				wmsMaterial.setMaterialCode(material.getMaterialId());
				wmsMaterial.setMaterialName(material.getName());
				wmsMaterial.setSpec(material.getPackageSpec());
				wmsMaterial.setUnit(material.getInventoryUom());
				wmsMaterial.setUpperLimit(material.getQtyMax());
				wmsMaterial.setLowerLimit(material.getQtyMin());
				wmsMaterial.setSafeStock(material.getQtyMin());
				wmsMaterial.setErpWrite(BigDecimal.ONE);
				wmsMaterial.setErpWriteTime(new Date());
				wmsMaterial.setWmsRead(BigDecimal.ZERO);
				//wmsMaterial.setWmsReadTime(new Date());
				wmsMaterial.setMaterialCategory1(material.getMaterialCategory1());
				wmsMaterial.setMaterialCategory2(material.getMaterialCategory2());
				wmsMaterial.setMaterialCategory3(material.getMaterialCategory3());
				wmsMaterial.setMaterialCategory4(material.getMaterialCategory4());
				wmsMaterial.setMaterialCategory5(material.getMaterialCategory5());
				wmsMaterial.setMaterialCategory6(material.getMaterialCategory6());
				em.persist(wmsMaterial);
			}
			
			//记历史
			MaterialHis his = new MaterialHis(material);
			his.setCreated(now);
			his.setUpdated(now);
			his.setCreatedBy(userRrn);
			his.setUpdatedBy(userRrn);
			em.persist(his);
			
			return material;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public byte[] getMaterialPhoto(Material material) throws ClientException {
		try {
			if(material == null || material.getObjectRrn() == null) {
				return new byte[0];
			}
			StringBuffer sql = new StringBuffer(" SELECT t.PHOTO FROM PDM_MATERIAL t WHERE t.OBJECT_RRN = ? ");
			logger.debug(sql);
			
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, material.getObjectRrn());
			SerializableBlob result = (SerializableBlob)query.getSingleResult();
			
			if(result != null) {
				InputStream is = result.getBinaryStream();
				byte[] bytes = new byte[1024 * 1024];
				is.read(bytes);
				int length = bytes.length;
				return bytes;
			} else {
				return null;
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public boolean saveMaterialPhoto(Material material, byte[] bytes) {
		if(material == null || material.getObjectRrn() == null) {
			return false;
		}
		if(bytes == null || bytes.length == 0) {
			return false;
		}
		try {
			StringBuffer sql = new StringBuffer(" UPDATE PDM_MATERIAL t SET t.PHOTO = ? WHERE t.OBJECT_RRN = ? ");
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, bytes);
			query.setParameter(2, material.getObjectRrn());
			int result = query.executeUpdate();
			return true;
		} catch(Exception e) {
			logger.error(e.getMessage());
		}
		return false;
	}
	
	public Bom newBom(Material material) throws ClientException {
		List<Bom> boms=new ArrayList<Bom>();
		Bom newBom = new Bom();
		try{
			newBom.setOrgRrn(material.getOrgRrn());
			if (material != null && material.getObjectRrn() != null) {
				newBom.setParentRrn(material.getObjectRrn());
				boms = getChildrenBoms(material.getObjectRrn(), BigDecimal.ONE);
				long maxLineNo = 1;
				for (Bom childerBom : boms) {
					if(childerBom.getSeqNo() == null)
						continue;
					maxLineNo = maxLineNo < childerBom.getSeqNo() ? childerBom.getSeqNo() : maxLineNo;
				}
				newBom.setSeqNo((long)Math.ceil(maxLineNo / 10) * 10 + 10);
			} else {
				newBom.setSeqNo(10L);
			}
			
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return newBom;
	}
	
	//获得父物料最新的BOM
	public List<Bom> getChildrenBoms(long parentRrn, BigDecimal parentQty) throws ClientException {
		long version = getLastVersion(parentRrn);
		return getChildrenBoms(parentRrn, version, parentQty);
	}
	
	//获得下一级的BOM，如果有大BOM则显示，否则显示小BOM
	public List<Bom> getChildrenBoms(long parentRrn, long version, BigDecimal parentQty) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT Bom FROM Bom as Bom ");
		sql.append(" WHERE parentRrn = ? ");
		sql.append(" AND parentVersion = ? ");
//		sql.append(" ORDER BY seqNo, created ");    
		sql.append(" ORDER BY childMaterial.materialId ");//生产部要求按物料编号由小到大排列
		logger.debug(sql);
		List<Bom> boms = new ArrayList<Bom>();
		try{
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, parentRrn);
			query.setParameter(2, version);
			boms = query.getResultList();
			if (boms.size() == 0) {
				//使用小BOM
				Material material = em.find(Material.class, parentRrn);
				if (material != null) {
					Long bomRrn = material.getBomRrn();
					if (bomRrn != null && bomRrn != 0) {
						boms = getActualChildrenBoms(parentRrn, bomRrn, true);
					}
				}
			} 
			for (Bom bom : boms) {
				bom.setQtyBom(bom.getUnitQty().multiply(parentQty));
			}
			
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return boms;
	}
	
	//根据BOM大类获得BOM，并处理实际选料与未选料
	public List<Bom> getActualChildrenBoms(long actualParentRrn, long parentRrn, boolean selectedFlag) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT Bom FROM Bom as Bom ");
		sql.append(" WHERE parentRrn = ? ");
		sql.append(" AND parentVersion = ? ");
		sql.append(" ORDER BY seqNo, created ");
		logger.debug(sql);
		List<Bom> actualBoms = new ArrayList<Bom>();
		List<Bom> unSelectedBoms = new ArrayList<Bom>();
		try {
			long version = getLastVersion(parentRrn);
			
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, parentRrn);
			query.setParameter(2, version);
			List<Bom> boms = query.getResultList();
			if (boms.size() == 0) {
				Material material = em.find(Material.class, parentRrn);
				if (material != null) {
					Long bomRrn = material.getBomRrn();
					if (bomRrn != null && bomRrn != 0) {
						return getActualChildrenBoms(parentRrn, bomRrn, selectedFlag);
					}
				}
			} else {
				for (Bom bom : boms) {
					bom.setCategory(Bom.CATEGORY_BOM);
					Bom actualBom = getActualBom(actualParentRrn, bom);
					if (actualBom != null) {
						actualBom.setBomTypeChildRrn(bom.getChildRrn());
						bom = actualBom;
					}
					Bom clone = (Bom) bom.clone();
					actualBoms.add(clone);
				}
			}
			//设置BOM真实的Parent
			for (Bom actualBom : actualBoms) {
				actualBom.setParentRrn(actualParentRrn);
			}
			
			//设置未选料
			if (selectedFlag) {
				sql = new StringBuffer();
				sql.append(" SELECT MaterialUnSelected FROM MaterialUnSelected as MaterialUnSelected ");
				sql.append(" WHERE materialRrn = ? ");
				query = em.createQuery(sql.toString());
				query.setParameter(1, actualParentRrn);
				List<MaterialUnSelected> unSelecteds = query.getResultList();
				for (MaterialUnSelected unSelected : unSelecteds) {
					for (Bom actualBom : actualBoms) {
						if (unSelected.getUnSelectedRrn().equals(actualBom.getChildRrn())) {
							unSelectedBoms.add(actualBom);
						}
					}
				}
				actualBoms.removeAll(unSelectedBoms);
			}
			
		} catch (Exception e){
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return actualBoms;
	}
	
	
	
	//获得实际物料(对可选料)
	public Bom getActualBom(long actualParentRrn, Bom bom) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT MaterialActual FROM MaterialActual as MaterialActual ");
		sql.append(" WHERE materialRrn = ? ");
		sql.append(" AND childRrn = ? "); 
		logger.debug(sql);
		
		try{
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, actualParentRrn);
			query.setParameter(2, bom.getChildRrn());
			List<MaterialActual> actuals = (List<MaterialActual>)query.getResultList();
			if (actuals.size() > 0) {
				MaterialActual actual = actuals.get(0);
				Bom actualBom = new Bom();
				actualBom.setChildMaterial(em.find(Material.class, actual.getActualRrn()));
				actualBom.setChildRrn(actual.getActualRrn());
				actualBom.setParentRrn(bom.getParentRrn());
				actualBom.setParentVersion(bom.getParentVersion());
				actualBom.setSeqNo(bom.getSeqNo());
				actualBom.setUnitQty(actual.getUnitQty());
				actualBom.setDescription(actual.getDescription());
				actualBom.setCategory(Bom.CATEGORY_OPTIONAL);
				return actualBom;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return null;
	}
	

	//保存单个BOM记录
	public Bom saveBom(Bom bom, long userRrn) throws ClientException {
		try{
			Date now = new Date();
			if (bom.getParentVersion() == null) {
				bom.setParentVersion(getLastVersion(bom.getParentRrn()));
			}
			List<Bom> childrenBom = getChildrenBoms(bom.getParentRrn(), bom.getParentVersion(), BigDecimal.ONE);
			for (Bom childBom : childrenBom) {
				if (!childBom.getObjectRrn().equals(bom.getObjectRrn()) && childBom.getChildRrn().equals(bom.getChildRrn())) {
					throw new ClientException("pdm.bom_material_existed");
				}
			}
			bom = updateVersion(bom, userRrn, now);
			
			bom.setIsActive(true);
			
			if (bom.getObjectRrn() == null) {
				if (bom.getCreated() == null) {
					bom.setCreatedBy(userRrn);
					bom.setCreated(new Date());
				}
				em.persist(bom);
			} else {
				bom = em.merge(bom);
			}

			getBomDetails(bom.getParentRrn(), bom.getParentVersion());
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return bom;
	}
	
	//删除单个BOM记录
	public void deleteBom(Bom bom, long userRrn) throws ClientException {
		Date now = new Date();
		
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT Bom FROM Bom as Bom ");
		sql.append(" WHERE parentRrn = ? ");
		sql.append(" AND parentVersion = ? ");
		try{
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, bom.getParentRrn());
			query.setParameter(2, bom.getParentVersion());
			List<Bom> boms = query.getResultList();
			if (boms.size() <= 1) {
				//删除所有BOM
				StringBuffer sqlDelete = new StringBuffer();
				sqlDelete.append("DELETE FROM Bom ");
				sqlDelete.append(" WHERE parentRrn = ? ");
				query = em.createQuery(sqlDelete.toString());
				query.setParameter(1, bom.getParentRrn());
				query.executeUpdate();
			} else {
				bom = updateVersion(bom, userRrn, now);
				if (bom.getObjectRrn() != null) {
					em.remove(bom);
				}
			}
		}catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
//	public void saveBomType(Material material, Material bomMaterial) throws ClientException {
//		StringBuffer sql = new StringBuffer();
//		sql.append("SELECT Bom FROM Bom as Bom ");
//		sql.append(" WHERE parentRrn = ? ");
//		try{
//			Query query = em.createQuery(sql.toString());
//			query.setParameter(1, material.getObjectRrn());
//			List<Bom> boms = query.getResultList();
//			if (boms.size() > 0) {
//				throw new ClientException("pdm.bom_existed");
//			}
//			if (material.getBomRrn() != bomMaterial.getObjectRrn()) {
//				material.setBomRrn(bomMaterial.getObjectRrn());
//				material.setBomId(bomMaterial.getMaterialId());
//				em.merge(material);
//			}
//			getLastBomDetails(material.getObjectRrn());
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
	
	//一次保存全部BOM及可选料
	public void saveCategoryBom(List<Bom> boms, List<MaterialOptional> optionalBoms, long userRrn) throws ClientException {
		try{
			Set<Long> set = new HashSet<Long>();
			for (Bom bom : boms) {
				if (set.contains(bom.getChildRrn())) {
					throw new ClientException("pdm.bom_material_existed");
				}
				set.add(bom.getChildRrn());
			}
			if (boms.size() == 0) {
				return;
			}
			Bom fBom = boms.get(0);
			if (fBom.getParentVersion() == null) {
				fBom.setParentVersion(getLastVersion(fBom.getParentRrn()));
			}
			
			StringBuffer sql = new StringBuffer();
			sql.append(" DELETE FROM Bom ");
			sql.append(" WHERE parentRrn = ? ");
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, fBom.getParentRrn());
			query.executeUpdate();

			int i = 1;
			for (Bom bom : boms) {
				bom.setIsActive(true);
				bom.setUpdatedBy(userRrn);
				bom.setParentVersion(fBom.getParentVersion());
				bom.setSeqNo(new Long(i * 10));
				if (bom.getObjectRrn() == null) {
					if (bom.getCreated() == null) {
						bom.setCreatedBy(userRrn);
						bom.setCreated(new Date());
					}
					em.persist(bom);
				}
				
				i++;
			}
			
			sql = new StringBuffer();
			sql.append(" DELETE FROM MaterialOptional ");
			sql.append(" WHERE materialRrn = ? ");
			query = em.createQuery(sql.toString());
			query.setParameter(1, fBom.getParentRrn());
			query.executeUpdate();
			for (MaterialOptional optionalBom : optionalBoms) {
				optionalBom.setIsActive(true);
				optionalBom.setUpdatedBy(userRrn);
				optionalBom.setCreatedBy(userRrn);
				optionalBom.setCreated(new Date());
				em.persist(optionalBom);
			}
			
			resetBOM(fBom.getParentRrn());
			
			getBomDetails(fBom.getParentRrn(), fBom.getParentVersion());
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//保存小BOM
	public void saveMaterialBom(Material material, Material categoryBom, List<MaterialActual> actualBoms, List<MaterialUnSelected> unSelectedBoms) throws ClientException {
		try{
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT Bom FROM Bom as Bom ");
			sql.append(" WHERE parentRrn = ? ");
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, material.getObjectRrn());
			List<Bom> boms = query.getResultList();
			if (boms.size() > 0) {
				throw new ClientException("pdm.bom_existed");
			}
			
			if (categoryBom != null && !categoryBom.getObjectRrn().equals(material.getBomRrn())) {
				material.setBomRrn(categoryBom.getObjectRrn());
				material.setBomId(categoryBom.getMaterialId());
				em.merge(material);
			}
			
			sql = new StringBuffer();
			sql.append(" DELETE FROM MaterialActual ");
			sql.append(" WHERE materialRrn = ? ");
			query = em.createQuery(sql.toString());
			query.setParameter(1, material.getObjectRrn());
			query.executeUpdate();
			for (MaterialActual actualBom : actualBoms) {
				em.persist(actualBom);
			}
			
			sql = new StringBuffer();
			sql.append(" DELETE FROM MaterialUnSelected ");
			sql.append(" WHERE materialRrn = ? ");
			query = em.createQuery(sql.toString());
			query.setParameter(1, material.getObjectRrn());
			query.executeUpdate();
			
			for (MaterialUnSelected unSelectedBom : unSelectedBoms) {
				em.persist(unSelectedBom);
			}
			
			getLastBomDetails(material.getObjectRrn());
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
	
	public Bom updateVersion(Bom bom, long userRrn, Date updated) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT Bom FROM Bom as Bom ");
		sql.append(" WHERE parentRrn = ? ");
		sql.append(" AND parentVersion = ? ");
		try{
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, bom.getParentRrn());
			query.setParameter(2, bom.getParentVersion());
			List<Bom> boms = query.getResultList();
			for (Bom childBom : boms) {
				Long childRrn = childBom.getObjectRrn();
				Bom clone = (Bom)childBom.clone();
				clone.setUpdated(updated);
				clone.setUpdatedBy(userRrn);
				clone.setParentVersion(clone.getParentVersion() + 1);
				if (!childRrn.equals(bom.getObjectRrn())) {
					em.persist(clone);
				}
			}
			bom.setObjectRrn(null);
			bom.setParentVersion(bom.getParentVersion() + 1);
			bom.setUpdated(updated);
			bom.setUpdatedBy(userRrn);
			return bom;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	//校验最新的BOM
	public void verifyBOM(long materialRrn) throws ClientException {
		List<BomDetail> bomDetails = getLastBomDetails(materialRrn);
		verifyBOM(materialRrn, bomDetails);
	}
	
	//校验BOM:1,BOM必须存在;2,如果物料是Product则必须有BOM
	public void verifyBOM(long materialRrn, List<BomDetail> bomDetails) throws ClientException {
		if (bomDetails == null || bomDetails.size() == 0) {
			Material material = em.find(Material.class, materialRrn);
			throw new ClientParameterException("pdm.matreial_no_bom", material.getMaterialId());
		}
		List<String> failedMaterial = new ArrayList<String>();
		for (BomDetail bomDetail : bomDetails) {
			Material material = bomDetail.getChildMaterial();
			if (material == null) {
				material = em.find(Material.class, bomDetail.getChildRrn());
			}
			if (material.getIsLotControl() && material.getIsProduct()) {
				List<BomDetail> childDetails = getChildBomDetail(bomDetails, bomDetail); 
				if (childDetails == null || childDetails.size() == 0) {
					failedMaterial.add(bomDetail.getChildMaterial().getMaterialId());
				}
			}
			
//			if (material.getIsPurchase() && !material.getIsProduct()) {
//				List<BomDetail> childDetails = getChildBomDetail(bomDetails, bomDetail); 
//				if (childDetails != null && childDetails.size() > 0) {
//					throw new ClientParameterException("pdm.matreial_cannot_have_bom", bomDetail.getChildMaterial().getMaterialId());
//				}
//			}
		}
		if(failedMaterial != null && failedMaterial.size() > 0){
			StringBuffer message = new StringBuffer();
			for(String m : failedMaterial){
				message.append(m);
				message.append(",");
			}
			throw new ClientParameterException("pdm.matreial_no_bom", message.toString());
		}
	}
	
	//获得物料最新的BOM
	public List<BomDetail> getLastBomDetails(long materialRrn) throws ClientException {
		long version = getLastVersion(materialRrn);
		List<BomDetail> bomDetails = new ArrayList<BomDetail>();
		String path = materialRrn + "/";
		List<Bom> children = getChildrenBoms(materialRrn, version, BigDecimal.ONE);
		for (Bom bom : children) {
			BomDetail bomDetail = new BomDetail();
			bomDetail.setMaterialRrn(materialRrn);
			bomDetail.setMaterialVersion(version);
			bomDetail.setPath(path);
			bomDetail.setRealPath(path);
			bomDetail.setPathLevel(1L);
			bomDetail.setRealPathLevel(1L);
			bomDetail.setParentRrn(bom.getParentRrn());
			bomDetail.setChildRrn(bom.getChildRrn());
			bomDetail.setSeqNo(bom.getSeqNo());
			bomDetail.setUnitQty(bom.getUnitQty());
			bomDetail.setQtyBom(bom.getQtyBom());
			bomDetail.setDescription(bom.getDescription());
			bomDetail.setChildMaterial(bom.getChildMaterial());
			internalGetBomDetail(bomDetail, bomDetails);
		}
		return bomDetails;
	}
	
	
	//获得实际BOM,没有虚拟料
	public List<BomDetail> getActualLastBomDetails(long materialRrn) throws ClientException {
		long version = getLastVersion(materialRrn);
		List<BomDetail> bomDetails = new ArrayList<BomDetail>();
		String path = materialRrn + "/";
		List<Bom> children = getChildrenBoms(materialRrn, version, BigDecimal.ONE);
		for (Bom bom : children) {
			BomDetail bomDetail = new BomDetail();
			bomDetail.setMaterialRrn(materialRrn);
			bomDetail.setMaterialVersion(version);
			bomDetail.setPath(path);
			bomDetail.setRealPath(path);
			bomDetail.setPathLevel(1L);
			bomDetail.setRealPathLevel(1L);
			bomDetail.setParentRrn(bom.getParentRrn());
			bomDetail.setChildRrn(bom.getChildRrn());
			bomDetail.setSeqNo(bom.getSeqNo());
			bomDetail.setUnitQty(bom.getUnitQty());
			bomDetail.setQtyBom(bom.getQtyBom());
			bomDetail.setDescription(bom.getDescription());
			bomDetail.setChildMaterial(bom.getChildMaterial());
			bomDetail.setIsPrepareBomPurchase(bom.getIsPrepareBomPurchase());
			bomDetail.setParentMaterial(bom.getParentMaterial());
			internalGetBomDetail(bomDetail, bomDetails, true);
		}
		return bomDetails;
	}
	
	//获得物料指定版本的BOM
	public List<BomDetail> getBomDetails(long materialRrn, long version) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT BomDetail FROM BomDetail as BomDetail ");
		sql.append(" WHERE materialRrn = ? ");
		sql.append(" AND materialVersion = ? ");
		sql.append(" ORDER BY pathLevel, seqNo ");
		logger.debug(sql);
		List<BomDetail> bomDetails = new ArrayList<BomDetail>();
		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, materialRrn);
			query.setParameter(2, version);
			bomDetails = (List<BomDetail>)query.getResultList();
			if (bomDetails.size() == 0) {
				String path = materialRrn + "/";
				List<Bom> children = getChildrenBoms(materialRrn, BigDecimal.ONE);
				for (Bom bom : children) {
					BomDetail bomDetail = new BomDetail();
					bomDetail.setMaterialRrn(materialRrn);
					bomDetail.setMaterialVersion(version);
					bomDetail.setPath(path);
					bomDetail.setRealPath(path);
					bomDetail.setPathLevel(1L);
					bomDetail.setRealPathLevel(1L);
					bomDetail.setParentRrn(bom.getParentRrn());
					bomDetail.setChildRrn(bom.getChildRrn());
					bomDetail.setSeqNo(bom.getSeqNo());
					bomDetail.setUnitQty(bom.getUnitQty());
					bomDetail.setQtyBom(bom.getQtyBom());
					bomDetail.setDescription(bom.getDescription());
					bomDetail.setChildMaterial(bom.getChildMaterial());
					internalGetBomDetail(bomDetail, bomDetails);
				}
				for (BomDetail bomDetail : bomDetails) {
					em.persist(bomDetail);
				}
			}
		} catch (ClientException e){ 
			throw e;
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
		return bomDetails;
	}
	
	protected void resetBOM(long materialRrn) throws ClientException {
		try {
			Material material = em.find(Material.class, materialRrn);
			material.setBomRrn(null);
			em.persist(material);
			
			StringBuffer sql = new StringBuffer();
			sql.append("DELETE FROM MaterialActual ");
			sql.append(" WHERE materialRrn = ? ");
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, materialRrn);
			query.executeUpdate();
			
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	protected void internalGetBomDetail(BomDetail parent, List<BomDetail> bomDetails) throws ClientException {
		internalGetBomDetail(parent, bomDetails, false);
	}

	//递归获得全部BOM，ignoreVirtual表示是否剔除虚拟料
	protected void internalGetBomDetail(BomDetail parent, List<BomDetail> bomDetails, boolean ignoreVirtual) throws ClientException {
		String path = new String(parent.getPath());
		String realPath = null;//记录真实的path，包括虚拟父物料的path
		if(parent.getRealPath() == null || parent.getRealPath().trim().length() == 0){
			realPath = new String(parent.getPath());
		}else{
			realPath = new String(parent.getRealPath());
		}
		long level = 0;
		long realLevel = 0;//记录真实的level，包括虚拟父物料
		if(parent.getRealPathLevel() != null){
			realLevel = parent.getRealPathLevel();
		}
		//检查嵌套
		String[] paths =  path.split("/");//BUG修复，下面那句有问题父：315 字 31就有问题
		for(String materialRrn :paths){
			if(materialRrn.equals(parent.getChildRrn().toString())){
				throw new ClientException("pdm.bom_nesting");
			}
		}
//		if (path.indexOf(parent.getChildRrn().toString()) > -1) {
//			throw new ClientException("pdm.bom_nesting");
//		}
		if (ignoreVirtual && parent.getChildMaterial().getIsVirtual()) {
			level = parent.getPathLevel();
			realPath += parent.getChildRrn() + "/";
			realLevel = realLevel + 1;
		} else {
			bomDetails.add(parent);
			path += parent.getChildRrn() + "/";
			level = parent.getPathLevel() + 1;
			realPath += parent.getChildRrn() + "/";
			realLevel = realLevel + 1;
		}
		
		List<Bom> children = getChildrenBoms(parent.getChildRrn(), parent.getUnitQty());
		for (Bom bom : children) {
			BomDetail bomDetail = new BomDetail();
			if (ignoreVirtual && parent.getChildMaterial().getIsVirtual()) {
				bomDetail.setParentRrn(parent.getParentRrn());
				bomDetail.setUnitQty(parent.getUnitQty().multiply(bom.getUnitQty()));
			} else {
				bomDetail.setParentRrn(bom.getParentRrn());
				bomDetail.setUnitQty(bom.getUnitQty());
			}
			bomDetail.setMaterialRrn(parent.getMaterialRrn());
			bomDetail.setMaterialVersion(parent.getMaterialVersion());
			bomDetail.setPath(path);
			bomDetail.setRealPath(realPath);//记录真实的path包括被过滤掉的虚拟父料
			bomDetail.setPathLevel(level);
			bomDetail.setRealPathLevel(realLevel);
			bomDetail.setChildRrn(bom.getChildRrn());
			bomDetail.setSeqNo(bom.getSeqNo());
			bomDetail.setQtyBom(bom.getQtyBom());
			bomDetail.setDescription(bom.getDescription());
			bomDetail.setChildMaterial(bom.getChildMaterial());
			bomDetail.setIsPrepareBomPurchase(bom.getIsPrepareBomPurchase());
			bomDetail.setParentMaterial(bom.getParentMaterial());
			internalGetBomDetail(bomDetail, bomDetails, ignoreVirtual);
		}
	}
	
	private long getLastVersion(long parentRrn) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT NVL(MAX(parentVersion), 0) FROM Bom WHERE parentRrn = ?");
		logger.debug(sql);
		
		try {
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, parentRrn);
			return ((Long)query.getSingleResult()).longValue();
		} catch (Exception e) {
			logger.error(e);
		}
		return 0;
	}
	
	private List<BomDetail> getChildBomDetail(List<BomDetail> bomDetails, BomDetail bomDetail) {
		List<BomDetail> childBoms = new ArrayList<BomDetail>();
		long childRrn = bomDetail.getChildRrn();
		long parentLevel = bomDetail.getPathLevel() + 1;
		for (BomDetail parentBom : bomDetails) {
			if (parentBom.getParentRrn() != null && 
					parentBom.getParentRrn() == childRrn && 
					parentBom.getPath().equals((bomDetail.getPath() != null ? bomDetail.getPath() : "") + childRrn + "/") && 
					parentBom.getPathLevel() == parentLevel) {
				childBoms.add(parentBom);
			}
		}
		return childBoms;
	}

	@Override
	public List<VPdmBom> getFullParentBomTree(long materialRrn) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT MATERIAL_PARENT_RRN, MATERIAL_PARENT_ID, MATERIAL_PARENT_NAME, MATERIAL_CHILD_RRN," +
					" MATERIAL_CHILD_ID, MATERIAL_CHILD_NAME, SEQ_NO, QTY_UNIT, DESCRIPTION, STATUS, QTY_ONHAND1, QTY_ONHAND2");
		sql.append(" FROM V_PDM_BOM T");
		sql.append(" CONNECT BY T.MATERIAL_CHILD_RRN = PRIOR T.MATERIAL_PARENT_RRN");
		sql.append(" START WITH MATERIAL_CHILD_RRN = ?");
		sql.append(" ORDER BY MATERIAL_PARENT_ID");
		
		logger.debug(sql);
		
		try {
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, materialRrn);
			List<Object[]> results = query.getResultList();
			List<VPdmBom> boms = new ArrayList<VPdmBom>();
			VPdmBom bom = null;
			for(Object[] objs : results){
				bom = new VPdmBom();
				bom.setMaterialParentRrn(((BigDecimal)objs[0]).longValue());
				bom.setMaterialParentId((String)objs[1]);
				bom.setMaterialParentName((String)objs[2]);
				bom.setMaterialChildRrn(((BigDecimal)objs[3]).longValue());
				bom.setMaterialChildId((String)objs[4]);
				bom.setMaterialChildName((String)objs[5]);
				bom.setSeqNo(objs[6] == null ? null : ((BigDecimal) objs[6]).longValue());
				bom.setQtyUnit(objs[7] == null ? null : (BigDecimal)objs[7]);
				bom.setDescription((String)objs[8]);
				bom.setStatus((String)objs[9]);
				bom.setQtyOnhand1((BigDecimal)objs[10]);
				bom.setQtyOnhand2((BigDecimal)objs[11]);
				boms.add(bom);
			}
			return boms;
		} catch (Exception e) {
			logger.error(e);
			throw new ClientException(e);
		}
	} 
	
	@Override
	public List<VPdmBom> getAllChildBoms(long materialRrn) throws ClientException {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT MATERIAL_PARENT_RRN, MATERIAL_PARENT_ID, MATERIAL_PARENT_NAME, MATERIAL_CHILD_RRN," +
					" MATERIAL_CHILD_ID, MATERIAL_CHILD_NAME, SEQ_NO, QTY_UNIT, DESCRIPTION, STATUS");
		sql.append(" FROM V_PDM_BOM T");
		sql.append(" CONNECT BY PRIOR T.MATERIAL_CHILD_RRN = T.MATERIAL_PARENT_RRN");
		sql.append(" START WITH MATERIAL_PARENT_RRN = ?");
		sql.append(" ORDER SIBLINGS BY SEQ_NO, MATERIAL_CHILD_ID ");
		
		logger.debug(sql);
		
		try {
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, materialRrn);
			List<Object[]> results = query.getResultList();
			List<VPdmBom> boms = new ArrayList<VPdmBom>();
			VPdmBom bom = null;
			for(Object[] objs : results){
				bom = new VPdmBom();
				bom.setMaterialParentRrn(((BigDecimal)objs[0]).longValue());
				bom.setMaterialParentId((String)objs[1]);
				bom.setMaterialParentName((String)objs[2]);
				bom.setMaterialChildRrn(((BigDecimal)objs[3]).longValue());
				bom.setMaterialChildId((String)objs[4]);
				bom.setMaterialChildName((String)objs[5]);
				bom.setSeqNo(objs[6] == null ? null : ((BigDecimal) objs[6]).longValue());
				bom.setQtyUnit(objs[7] == null ? null : (BigDecimal)objs[7]);
				bom.setDescription((String)objs[8]);
				bom.setStatus((String)objs[9]);
				boms.add(bom);
			}
			return boms;
		} catch (Exception e) {
			logger.error(e);
			throw new ClientException(e);
		}
	} 
	
	// 撤销为物料设置的Bom大类，materialRrn为需要撤销Bom大类设置的物料的objectRrn
	public Material revokeBomTypeSetup(long orgRrn, long materialRrn, long userRrn) throws ClientException {
		try{
			Material material = em.find(Material.class, materialRrn);
			material.setBomRrn(null);
			material.setBomId(null);
			material.setUpdated(new Date());
			material.setUpdatedBy(userRrn);
			em.merge(material);
			
			StringBuffer sqlDelete = new StringBuffer(" DELETE FROM MaterialActual MaterialActual ");
			sqlDelete.append(" WHERE MaterialActual.materialRrn = ? ");
			Query query = em.createQuery(sqlDelete.toString());
			query.setParameter(1, materialRrn);
			query.executeUpdate();
			return material;
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
	
	public List<MaterialUnSelected> getUnSelectMaterialList(long orgRrn, long materialRrn) throws ClientException {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT MaterialUnSelected FROM MaterialUnSelected as MaterialUnSelected ");
			sql.append(" WHERE materialRrn = ? ");
			logger.debug(sql);
			Query query = em.createQuery(sql.toString());
			query = em.createQuery(sql.toString());
			query.setParameter(1, materialRrn);
			return query.getResultList();
		} catch (Exception e) {
			logger.error(e);
			throw new ClientException(e);
		}
	}
	
	// bomType为1表示删除编制Bom；bomType为2表示删除选用Bom
	public void deleteAllBOM(Material material, int bomType, long userRrn) throws ClientException {
		try{
			Query query = null;
			switch(bomType) {
			case 1 : {
				//删除所有BOM
				StringBuffer sqlDelete = new StringBuffer();
				sqlDelete.append(" DELETE FROM Bom ");
				sqlDelete.append(" WHERE parentRrn = ? ");
				query = em.createQuery(sqlDelete.toString());
				query.setParameter(1, material.getObjectRrn());
				query.executeUpdate();
				
				//删除所有可选料
				sqlDelete = new StringBuffer();
				sqlDelete.append(" DELETE FROM MaterialOptional ");
				sqlDelete.append(" WHERE materialRrn = ? ");
				query = em.createQuery(sqlDelete.toString());
				query.setParameter(1, material.getObjectRrn());
				query.executeUpdate();
				
				//删除所有可替代料
				sqlDelete = new StringBuffer();
				sqlDelete.append(" DELETE FROM MaterialAlternate ");
				sqlDelete.append(" WHERE materialRrn = ? ");
				query = em.createQuery(sqlDelete.toString());
				query.setParameter(1, material.getObjectRrn());
				query.executeUpdate();
				break;
			}
			case 2 : {
				//取消BOM大类、删除所有实际选料
				revokeBomTypeSetup(material.getOrgRrn(), material.getObjectRrn(), userRrn);
				
				//删除所有未选择料
				StringBuffer sqlDelete = new StringBuffer();
				sqlDelete.append(" DELETE FROM MaterialUnSelected ");
				sqlDelete.append(" WHERE materialRrn = ? ");
				query = em.createQuery(sqlDelete.toString());
				query.setParameter(1, material.getObjectRrn());
				query.executeUpdate();
				break;
			}
			}
		} catch (OptimisticLockException e){
			logger.error(e.getMessage(), e);
			throw new ClientException("error.optimistic_lock");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	@Override
	public List<Material> getMaterialById(String materialId, long orgRrn)  throws ClientException {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT Material FROM Material as Material");
			sql.append(" WHERE materialId = ? ");
			sql.append(" AND orgRrn = ?");
			logger.debug(sql);
			Query query = em.createQuery(sql.toString());
			query.setParameter(1, materialId);
			query.setParameter(2, orgRrn);
			List<Material> resultList = query.getResultList();
			return resultList;
		} catch (Exception e) {
			logger.error(e);
			throw new ClientException(e);
		}
	}

	@Override
	public List<Bom> getParentBomsOnlyUsefulInfos(long materialRrn) throws ClientException {
		try {
			StringBuffer sql = new StringBuffer();
//			sql.append(" SELECT B.OBJECT_RRN, B.MATERIAL_PARENT_RRN, M.NAME, M.MATERIAL_ID, B.QTY_UNIT ");
//			sql.append(" FROM   PDM_BOM B, PDM_MATERIAL M ");
//			sql.append(" WHERE  M.OBJECT_RRN = B.MATERIAL_PARENT_RRN ");
//			sql.append(" AND B.MATERIAL_CHILD_RRN = ? ");
			
			sql.append(" SELECT BOM.OBJECT_RRN, ");
			sql.append("        BOM.MATERIAL_PARENT_RRN, ");
			sql.append("        M.NAME, ");
			sql.append("        M.MATERIAL_ID, ");
			sql.append("        BOM.QTY_UNIT ");
			sql.append(" FROM   PDM_BOM BOM ");
			sql.append(" INNER JOIN  (SELECT B.MATERIAL_PARENT_RRN, ");
			sql.append("                MAX(B.MATERIAL_PARENT_VERSION) LATEST_VERSION ");
			sql.append("         	FROM   PDM_BOM B ");
			sql.append("         	GROUP  BY B.MATERIAL_PARENT_RRN) RS ");
			sql.append(" 		ON BOM.MATERIAL_PARENT_RRN = RS.MATERIAL_PARENT_RRN ");
			sql.append("        AND BOM.MATERIAL_PARENT_VERSION = RS.LATEST_VERSION ");//找最新版本的BOM
			
			sql.append(" INNER JOIN PDM_MATERIAL M ");
			sql.append("        ON M.OBJECT_RRN = RS.MATERIAL_PARENT_RRN ");
			sql.append(" WHERE BOM.MATERIAL_CHILD_RRN = ? ");
			
			logger.debug(sql);
			
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, materialRrn);
			
			List<Object[]> rslt = query.getResultList();
			List<Bom> boms = new ArrayList<Bom>();
			Bom bom = null;
			for(Object[] obj : rslt){
				bom = new Bom();
				bom.setObjectRrn(((BigDecimal)obj[0]).longValue());
				bom.setParentRrn(((BigDecimal)obj[1]).longValue());
				Material m1 = new Material();
				m1.setObjectRrn(((BigDecimal)obj[1]).longValue());
				m1.setName((String)obj[2]);
				m1.setMaterialId((String)obj[3]);
				bom.setParentMaterial(m1);
				bom.setUnitQty((BigDecimal)obj[4]);
				boms.add(bom);
			}
			return boms;
		} catch (Exception e) {
			logger.error(e);
			throw new ClientException(e);
		}
	}

	@Override
	public void batchRemoveBom(List<Bom> parentBoms, Material childMaterial)
			throws ClientException {
		try {
			for(Bom bom : parentBoms){
				em.remove(em.find(Bom.class, bom.getObjectRrn()));
			}
		} catch (Exception e) {
			logger.error(e);
			throw new ClientException(e);
		}
	}

	@Override
	public List<Bom> batchReplaceBom(List<Bom> parentBoms,
			Material oldMaterial, Material newMaterial) throws ClientException {
		return null;
	}

	@Override
	public void batchUpdateBomUnitQty(List<Bom> boms, BigDecimal unitQty) throws ClientException {
		try {
			for(Bom bom : boms){
				StringBuffer sql = new StringBuffer();
				sql.append(" UPDATE PDM_BOM B ");
				sql.append(" SET B.QTY_UNIT = ? ");
				sql.append(" WHERE B.OBJECT_RRN = ? ");
				Query query = em.createNativeQuery(sql.toString());
				query.setParameter(1, unitQty);
				query.setParameter(2, bom.getObjectRrn());
				query.executeUpdate();
//				bom = em.getReference(Bom.class, bom.getObjectRrn());
//				bom.setUnitQty(unitQty);
//				newBoms.add(em.merge(bom));
			}
//			em.flush();
		} catch (Exception e) {
			logger.error(e);
			throw new ClientException(e);
		}
	}

	@Override
	public boolean testBOM(Material parentMaterial) throws ClientException {
		
		return false;
	}

	@Override
	public boolean isCartonMaterial(long orgRrn, long materialRrn) throws ClientException {
		StringBuffer sqlSP = new StringBuffer();
		sqlSP.append( " {call SP_CATCH_CARTON_MATERIAL(?)} " );
		Query query = em.createNativeQuery(sqlSP.toString());
		query.setParameter(1, orgRrn);
		query.executeUpdate();
		
		StringBuffer sqlQuery = new StringBuffer();
		sqlQuery.append( " SELECT PC.* FROM PDM_CARTON PC " );
		sqlQuery.append( " WHERE PC.OBJECT_RRN = ? " );
		Query query2 = em.createNativeQuery(sqlQuery.toString());
		query2.setParameter(1, materialRrn);
		List rzlt = query2.getResultList();
		
		return !( rzlt == null || rzlt.size() == 0 );
	}

	@Override
	public Material calculateVolumeByBOM(Material material)
			throws ClientException {
		BigDecimal maxVolume = BigDecimal.ZERO;
		BigDecimal length = BigDecimal.ZERO;
		BigDecimal width = BigDecimal.ZERO;
		BigDecimal height = BigDecimal.ZERO;
		BigDecimal unitQty = BigDecimal.ONE;
		List<Bom> boms = getDeepChildBOMs(material);
		for(Bom bom : boms){
			Material childMaterial = bom.getChildMaterial();
			if(childMaterial.getIsVolumeBasis()){
				if(childMaterial.getVolume() != null){
					if(maxVolume.compareTo(childMaterial.getVolume()) < 0){
						length = childMaterial.getLength();
						width = childMaterial.getWidth();
						height = childMaterial.getHeight();
						maxVolume = childMaterial.getVolume();
						unitQty = bom.getUnitQty();
					}
				}
			}
		}
		if(maxVolume.compareTo(BigDecimal.ZERO) > 0){
			if(unitQty.compareTo(BigDecimal.ONE) == 0){
				material.setLength(length);
				material.setWidth(width);
				material.setHeight(height);
				material.setVolume(maxVolume);
			}else{//如果单耗不是1，只记体积不记长宽高
				material.setLength(BigDecimal.ZERO);
				material.setWidth(BigDecimal.ZERO);
				material.setHeight(BigDecimal.ZERO);
				material.setVolume(maxVolume.multiply(unitQty));
			}
		}
		
		return em.merge(material);
	}
	
	
	/**
	 * 此方法查找以某物料为根的完整的BOM结构
	 * @param material 父物料
	 * @return 以父物料为根的完整的BOM结构
	 * @throws ClientException
	 */
	private List<Bom> getDeepChildBOMs(Material material) throws ClientException{
		try {
			List<Bom> boms = null;
			List<Bom> tempBoms = new ArrayList<Bom>();
			boms = getChildrenBoms(material.getObjectRrn(), BigDecimal.ONE);
			tempBoms.addAll(boms);
			for(Bom bom : tempBoms){
				List<Bom> boms1 = getDeepChildBOMs(bom.getChildMaterial());
				boms.addAll(boms1);
			}
			return boms;
		} catch (Exception e) {
			logger.error(e);
			throw new ClientException(e);
		}
	}
	
	
	/**
	 * 根据物料找到所有最新版本的父BOM
	 * @param material 子物料
	 * @return 所有最新版本的父BOM
	 * @throws ClientException
	 */
	private List<Bom> getParentBOMs(Material material) throws ClientException{
		try{
			StringBuffer sql = new StringBuffer();
			sql.append( " SELECT T.OBJECT_RRN, T.ORG_RRN ");
			sql.append( " FROM ( SELECT * FROM PDM_BOM WHERE MATERIAL_CHILD_RRN = ? ) T, ");
			sql.append( "        ( SELECT B.MATERIAL_PARENT_RRN, ");
			sql.append( "                NVL(MAX(B.MATERIAL_PARENT_VERSION), 0) LATEST_VERSION ");
			sql.append( "         FROM   PDM_BOM B ");
			sql.append( "         WHERE  B.MATERIAL_CHILD_RRN = ? ");
			sql.append( "         GROUP  BY B.MATERIAL_PARENT_RRN ) V ");
			sql.append( " WHERE  V.MATERIAL_PARENT_RRN = T.MATERIAL_PARENT_RRN ");
			sql.append( "        AND T.MATERIAL_PARENT_VERSION = V.LATEST_VERSION ");
			logger.debug(sql);
			
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, material.getObjectRrn());
			query.setParameter(2, material.getObjectRrn());
			
			List<Bom> boms = new ArrayList<Bom>();
			List<Object[]> rs = query.getResultList();
			for(Object[] objs : rs){
				Bom bom = em.find(Bom.class, ((BigDecimal)objs[0]).longValue());
				boms.add(bom);
			}

			return boms;
		} catch (Exception e){
			logger.error(e);
			throw new ClientException(e);
		}
	}
	
	public List<Bom> getDeepParentBOMs(Material material) throws ClientException{
		try{
			List<Bom> boms = getParentBOMs(material);
			List<Bom> tempBoms = new ArrayList<Bom>();
			tempBoms.addAll(boms);
			for(Bom bom : tempBoms){
				List<Bom> b = getDeepParentBOMs(bom.getParentMaterial());
				boms.addAll(b);
			}
			return boms;
		} catch (Exception e){
			logger.error(e);
			throw new ClientException(e);
		}
	}

	@Override
	public List<ADBase> validateBeforeDeleteMaterial(Material material)
			throws ClientException {
		try{
			List<ADBase> objects = new ArrayList<ADBase>();
			List<Long> bomRrns = new ArrayList<Long>();
			bomRrns.add(material.getObjectRrn());
			//第一步  所有使用该物料做为子物料的BOM
			List<VPdmBom> boms = getFullParentBomTree(material.getObjectRrn());
			objects.addAll(boms);
			for(VPdmBom bom : boms){
				bomRrns.add(bom.getMaterialParentRrn());
			}
			
			//第二步 所有使用了子物料中包含该物料的BOM的工作令
			StringBuffer sqlMo = new StringBuffer();
			sqlMo.append( " SELECT ManufactureOrder FROM ManufactureOrder ManufactureOrder WHERE ManufactureOrder.materialRrn IN (:rrns) " );
			sqlMo.append( " AND ManufactureOrder.docStatus <> " );
			sqlMo.append( "'" + Documentation.STATUS_CLOSED + "' " );
			sqlMo.append( " AND ManufactureOrder.docStatus <> " );
			sqlMo.append( "'" + Documentation.STATUS_COMPLETED + "' " );
			logger.debug(sqlMo);
			
			Query moQuery = em.createQuery(sqlMo.toString());
			moQuery.setParameter("rrns", bomRrns);
			List moList = moQuery.getResultList();
			
			objects.addAll(moList);
			
			//第三步 所有使用了子物料中包含该物料的BOM的子工作令
			StringBuffer sqlMoLine = new StringBuffer();
			sqlMoLine.append( " SELECT ManufactureOrderLine FROM ManufactureOrderLine ManufactureOrderLine WHERE ManufactureOrderLine.materialRrn IN (:rrns) " );
			sqlMoLine.append(" AND ManufactureOrderLine.lineStatus <> ");
			sqlMoLine.append( " '" + DocumentationLine.LINESTATUS_CLOSED + "' " );
			sqlMoLine.append( " AND ManufactureOrderLine.lineStatus <> " );
			sqlMoLine.append( " '"+ DocumentationLine.LINESTATUS_COMPLETED + "' "  );
			logger.debug(sqlMoLine);
			
			Query moLineQuery = em.createQuery(sqlMoLine.toString());
			moLineQuery.setParameter("rrns", bomRrns);
			List moLineList = moLineQuery.getResultList();
			objects.addAll(moLineList);
			
			return objects;
		}catch (Exception e){
			logger.error(e);
			throw new ClientException(e);
		}
	}

	@Override
	public BigDecimal caculateCostFormula(Material material)
			throws ClientException {
		try{
			List<VPdmBom> l = filterCostFormulaBOMs(material, BigDecimal.ONE);
			BigDecimal b = BigDecimal.ZERO;
			for(VPdmBom v : l){
				CanaProduct product = canaManager.getCanaProduct(v.getMaterialChildId());
				BigDecimal unitPrice = product.getPrice2Low();
				b = b.add(v.getQtyUnit().multiply(unitPrice));
			}
			return b;
		}catch (Exception e){
			logger.error(e);
			throw new ClientException(e);
		}
	}
	
	private List<VPdmBom> filterCostFormulaBOMs(Material material, BigDecimal qty) throws ClientException {
		try {
			List<VPdmBom> results = new LinkedList<VPdmBom>();
			StringBuffer sql = new StringBuffer();
			sql.append( " SELECT  " );
			sql.append( "      LEVEL, " );
			sql.append( "      T.MATERIAL_PARENT_RRN, " );
			sql.append( "      T.MATERIAL_CHILD_RRN, " );
			sql.append( "      T.SEQ_NO, " );
			sql.append( "      T.UOM_ID, " );
			sql.append( "      T.QTY_UNIT " );
			sql.append( " FROM   V_PDM_BOM T " );
			sql.append( " START  WITH T.MATERIAL_PARENT_RRN = ? " );
			sql.append( " CONNECT BY PRIOR T.MATERIAL_CHILD_RRN = T.MATERIAL_PARENT_RRN " );
			sql.append( " ORDER  SIBLINGS BY T.SEQ_NO " );
			
			Query query = em.createNativeQuery(sql.toString());
			query.setParameter(1, material.getObjectRrn());
			
			List<Object[]> queryResult = query.getResultList();
			if(queryResult != null && queryResult.size() > 0){
				for(Object[] objs : queryResult){
					VPdmBom vpb = new VPdmBom();
					vpb.setLevel((BigDecimal)objs[0]);
					vpb.setMaterialParentRrn(((BigDecimal)objs[1]).longValue());
					vpb.setMaterialChildRrn(((BigDecimal)objs[2]).longValue());
					vpb.setSeqNo(((BigDecimal)objs[3]).longValue());
					vpb.setQtyUnit((BigDecimal)objs[5]);
					if(vpb.getLevel().compareTo(BigDecimal.ONE) == 0){
						vpb.setQtyUnit(vpb.getQtyUnit().multiply(qty));
						Material m = new Material();
						m.setObjectRrn(vpb.getMaterialChildRrn());
						m = (Material) adManager.getEntity(m);
						results.addAll(filterCostFormulaBOMs(m, vpb.getQtyUnit()));
					}
				}
			}else{
				VPdmBom vpb = new VPdmBom();
				vpb.setMaterialChildRrn(material.getObjectRrn());
				vpb.setMaterialChildId(material.getMaterialId());
				vpb.setQtyUnit(qty);
				results.add(vpb);
			}
			return results;
		} catch (Exception e){
			logger.error(e);
			throw new ClientException(e);
		}
	}

	public boolean testBOM(long parentRrn) throws ClientException {
		// TODO Auto-generated method stub
		return false;
	}
	
	//调用此方法判断是否显示加号
	public List<Bom> getChildrenBomsFirst(long parentRrn, BigDecimal parentQty) throws ClientException {
		long version = getLastVersion(parentRrn);
		return getChildrenBomsFirst(parentRrn, version, parentQty);
	}
	
	public List<Bom> getChildrenBomsFirst(long parentRrn, long version, BigDecimal parentQty) throws ClientException {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT Bom FROM Bom as Bom ");
			sql.append(" WHERE parentRrn = ? ");
			sql.append(" AND parentVersion = ? ");
//		sql.append(" ORDER BY seqNo, created ");    
			sql.append(" ORDER BY childMaterial.materialId ");//生产部要求按物料编号由小到大排列
			logger.debug(sql);
			List<Bom> boms = new ArrayList<Bom>();
			
				Query query = em.createQuery(sql.toString());
				query.setParameter(1, parentRrn);
				query.setParameter(2, version);
				return boms = query.getResultList();
		} catch (RuntimeException e) {
			logger.error(e);
			throw new ClientException(e);
		}
	}
	
	public List<Object[]> getBomVersion(Long parentRrn) throws ClientException{
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from (select distinct t.material_parent_version as iversion ,to_char(t.updated,'yyyy-mm-dd') as itime,t.updated_by,u.user_name");
			sql.append(" from pdm_bom t,ad_user u where u.object_rrn(+)=t.updated_by and t.material_parent_rrn=?) a");		
			sql.append(" order by a.iversion");
			logger.debug(sql);
			List<Object[]> vboms=new ArrayList();
				Query query = em.createNativeQuery(sql.toString());
					query.setParameter(1, parentRrn);
					 vboms = query.getResultList();
					 return vboms;
		} catch (RuntimeException e) {
			logger.error(e);
			throw new ClientException(e);
		}
		
		
	}
	
	public List<Object[]> getBomContrastVersion(Long parentRrn,Long version) throws ClientException{
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select * from (select distinct t.material_parent_version as version ,to_char(t.updated,'yyyy-mm-dd') as itime,t.updated_by,u.user_name");
			sql.append(" from pdm_bom t,ad_user u where u.object_rrn(+)=t.updated_by and t.material_parent_rrn=?) a");		
			sql.append(" where a.version not in ?");
			sql.append(" order by a.version");
			logger.debug(sql);
			List<Object[]> vboms=new ArrayList();
				Query query = em.createNativeQuery(sql.toString());
					query.setParameter(1, parentRrn);
					query.setParameter(2, version);
					 vboms = query.getResultList();
					 return vboms;
		} catch (RuntimeException e) {
			logger.error(e);
			throw new ClientException(e);
		}
		
		
	}
	
	public boolean getMaterialRrn(long materialrrn) throws ClientException {
		try{
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT COUNT(*) FROM inv_movement_line T");
		sql.append(" WHERE T.material_rrn = ? ");
		Query query = em.createNativeQuery(sql.toString());
		query.setParameter(1, materialrrn);
		Object ob = query.getSingleResult();
	    if(ob == null){
	    	return true;//keyishanchu
	    }else if(((BigDecimal)ob).compareTo(BigDecimal.ZERO) == 0){
	    	return true;//keyishanchu
	    }
	    else{
	    	return false;
	    }
		} catch (Exception e){ 
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}

	/**
	 * 物料查询的专用方法，可查询新增物料，以及新增物料是否有bom
	 * @param orgRrn, maxResult, whereClause
	 * @return
	 * @throws ClientException
	 */
	public List<Material> queryMaterial(long orgRrn, int maxResult, String whereClause) throws ClientException{
		try {
			StringBuffer hql = new StringBuffer();
			hql.append(" FROM Material Material ");
			hql.append(" WHERE " + ADBase.BASE_CONDITION);
			hql.append(" AND " + whereClause);

			Query query = em.createQuery(hql.toString());
			query.setParameter(1, orgRrn);
			query.setMaxResults(maxResult);
			
			List<Material> matrls = query.getResultList();
			
//			if(matrls != null){
//				for(Material ma : matrls){
//					StringBuffer sql2 = new StringBuffer();
//
//					sql2.append(" SELECT COUNT(*) FROM PDM_BOM B ");
//					sql2.append(" WHERE " + ADBase.SQL_BASE_CONDITION);
//					sql2.append(" AND B.MATERIAL_PARENT_RRN = ?");
//
//					
//					Query query2 = em.createNativeQuery(sql2.toString());
//					query2.setParameter(1, orgRrn);
//					query2.setParameter(2, ma.getObjectRrn());
//					
//					BigDecimal count = (BigDecimal) query2.getSingleResult();
//					if(count == null || count.longValue() == 0){
//						ma.setHasBom(false);
//					}else{
//						ma.setHasBom(true);
//					}
//				}
//			}
			return matrls;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	public EntityManager getEntityManager(){
		return em;
	}
	/**
	 * 得到预处理工作令消除重复
	 * */
	public List<TpsLinePrepare> getTpsLinePrepare(long orgRrn,String whereClause) throws ClientException{
		try{
			StringBuffer hql = new StringBuffer();
			hql.append("SELECT DISTINCT P.ORG_RRN,P.tps_id  FROM PPM_TPS_LINE_PREPARE P ");
			hql.append(" WHERE  is_active = 'Y' AND (org_rrn = ? OR org_rrn = 0) " );
			if(whereClause!=null && whereClause.length()>0){
				hql.append(" AND " + whereClause);
			}
			Query query = em.createNativeQuery(hql.toString());
			query.setParameter(1, orgRrn);
			List<TpsLinePrepare> tpsLinePrepares = new ArrayList<TpsLinePrepare>();
			List<Object[]> results = (List<Object[]>)query.getResultList();
			long i=1;
			for (Object[] objs : results) {
				TpsLinePrepare tpsLinePrepare = new TpsLinePrepare();
				tpsLinePrepare.setObjectRrn(i);
				tpsLinePrepare.setOrgRrn(((BigDecimal)objs[0]).longValue());
				tpsLinePrepare.setTpsId(objs[1]!=null?objs[1].toString():"");
//				tpsLinePrepare.setTpsStatus((String) objs[1]);
				tpsLinePrepares.add(tpsLinePrepare);
				i++;
			}
			return tpsLinePrepares;
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		return null;
	}
	
	
	public void runTempFirstBom() throws ClientException {
		try{
		Session session = (Session) em.getDelegate();  
        Connection conn = session.connection();  
        CallableStatement call = conn.prepareCall("{CALL SP_FIRSTBOM()}");  
        call.execute();  
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ClientException(e);
		}
	}
}


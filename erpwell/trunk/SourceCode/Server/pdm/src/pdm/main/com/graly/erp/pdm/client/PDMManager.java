/**
 * 
 */
package com.graly.erp.pdm.client;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;

import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.model.Bom;
import com.graly.erp.pdm.model.BomDetail;
import com.graly.erp.pdm.model.MaterialActual;
import com.graly.erp.pdm.model.MaterialOptional;
import com.graly.erp.pdm.model.MaterialUnSelected;
import com.graly.erp.pdm.model.VPdmBom;
import com.graly.erp.ppm.model.TpsLinePrepare;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.core.exception.ClientException;


public interface PDMManager {
	Bom newBom(Material material) throws ClientException ;
	Material saveMaterial(long tableRrn, Material material, long userRrn) throws ClientException;
	byte[] getMaterialPhoto(Material material) throws ClientException;
	boolean saveMaterialPhoto(Material material, byte[] bytes) throws ClientException;
	
	List<Bom> getChildrenBoms(long parentRrn, BigDecimal parentQty) throws ClientException;
	List<Bom> getChildrenBoms(long parentRrn, long version, BigDecimal parentQty) throws ClientException;
	
	List<Object[]> getBomContrastVersion(Long parentRrn,Long version) throws ClientException;
	List<Object[]> getBomVersion(Long parentRrn) throws ClassCastException, ClientException;
	List<Bom> getChildrenBomsFirst(long parentRrn, BigDecimal parentQty) throws ClientException;
	List<Bom> getChildrenBomsFirst(long parentRrn, long version, BigDecimal parentQty) throws ClientException;
	Bom saveBom(Bom bom, long userRrn) throws ClientException;
	
	void deleteBom(Bom bom, long userRrn) throws ClientException;
	List<BomDetail> getLastBomDetails(long materialRrn) throws ClientException;
	List<BomDetail> getActualLastBomDetails(long materialRrn) throws ClientException;
	List<BomDetail> getBomDetails(long materialRrn, long version) throws ClientException;
	
//	void saveBomType(Material material, Material bomMaterial) throws ClientException;
	void verifyBOM(long materialRrn) throws ClientException;
	void verifyBOM(long materialRrn, List<BomDetail> bomDetails) throws ClientException;
	
	void saveCategoryBom(List<Bom> boms, List<MaterialOptional> optionalBoms, long userRrn) throws ClientException;
	void saveMaterialBom(Material material, Material categoryBom, List<MaterialActual> actualBoms, List<MaterialUnSelected> unSelectedBoms) throws ClientException;
	
	List<Material> getMaterialById(String materialId, long orgRrn) throws ClientException;
	List<Bom> getParentBomsOnlyUsefulInfos(long materialRrn) throws ClientException;
	List<VPdmBom> getFullParentBomTree(long materialRrn) throws ClientException;
	List<VPdmBom> getAllChildBoms(long materialRrn) throws ClientException;
	
	void batchRemoveBom(List<Bom> parentBoms, Material childMaterial) throws ClientException;
	List<Bom> batchReplaceBom(List<Bom> parentBoms, Material oldMaterial, Material newMaterial) throws ClientException;
	void batchUpdateBomUnitQty(List<Bom> boms, BigDecimal unitQty) throws ClientException;
	
//	BigDecimal getConvertOfUom(long materialRrn, String convertType) throws ClientException;
	Material revokeBomTypeSetup(long orgRrn, long materialRrn, long userRrn) throws ClientException;
	List<MaterialUnSelected> getUnSelectMaterialList(long orgRrn, long materialRrn) throws ClientException;
	List<Bom> getActualChildrenBoms(long actualParentRrn, long parentRrn, boolean selectedFlag) throws ClientException;
	void deleteAllBOM(Material material, int bomType, long userRrn) throws ClientException;
	
	boolean testBOM(long parentRrn) throws ClientException;
	
	boolean isCartonMaterial(long orgRrn, long materialRrn) throws ClientException;
	
	/**
	 * 从BOM中获取体积最大的纸箱的体积信息
	 * @param material
	 * @return 长 宽 高 体积
	 * @throws ClientException
	 */
	Material calculateVolumeByBOM(Material material) throws ClientException;
	/**
	 * @param material 要删除的物料
	 * @return 该物料被使用的地方，包括 BOM 工作令 子工作令
	 * @throws ClientException
	 */
	List<ADBase> validateBeforeDeleteMaterial(Material material) throws ClientException;
	List<Bom> getDeepParentBOMs(Material material) throws ClientException;
	
	BigDecimal caculateCostFormula(Material material) throws ClientException;
	boolean testBOM(Material parentMaterial) throws ClientException;
	boolean getMaterialRrn(long materialrrn) throws ClientException;
	List<Material> queryMaterial(long orgRrn, int maxResult, String whereClause) throws ClientException;
	
	public EntityManager getEntityManager();
	public List<TpsLinePrepare> getTpsLinePrepare(long orgRrn,String whereClause) throws ClientException;
	void runTempFirstBom() throws ClientException;
}

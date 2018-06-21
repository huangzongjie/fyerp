package com.graly.erp.vdm.client;

import java.util.Date;
import java.util.List;

import com.graly.erp.vdm.model.MaterialAssessment;
import com.graly.erp.vdm.model.Vendor;
import com.graly.erp.vdm.model.VendorAssessment;
import com.graly.erp.vdm.model.VendorMaterial;
import com.graly.erp.vdm.model.VendorYearTarget;
import com.graly.framework.core.exception.ClientException;

public interface VDMManager {
	List<MaterialAssessment> getMaterialAssessment(String whereClause) throws ClientException;
	void saveMaterialAssessment(MaterialAssessment materialAssessment,long userRrn) throws ClientException;
	VendorMaterial saveVendorMaterial(VendorMaterial vendorMaterial,long userRrn)throws ClientException;
	VendorMaterial getVendorMaterial(long vendorRrn, long materialRrn)  throws ClientException;
	VendorMaterial getPrimaryVendor(Long materialRrn) throws ClientException;
	String generateVendorCode(long orgRrn, String docType) throws ClientException;
	List<VendorMaterial> getOnlyMainVendorMaterialList(long orgRrn, int maxResult) throws ClientException;
	List<VendorMaterial> getVendorMaterialList(long orgRrn, Date dateStart, Date dateEnd, String whereClause) throws ClientException;
	List<VendorMaterial> getVendorMaterialList2(long orgRrn, Date dateStart, Date dateEnd, String whereClause) throws ClientException;
	VendorAssessment generateVendorAssessment(long orgRrn, 
			long materialRrn, long vendorRrn, String purchaser, Date dateStart, Date dateEnd) throws ClientException;
	List<Vendor> getVendorByVendorId(long orgRrn, String vendorId) throws ClientException;
	List<VendorMaterial> getVendorMaterials(long vendorRrn, long materialRrn) throws ClientException;
	List<VendorMaterial> getVendorMaterialsByMaterial(long materialRrn) throws ClientException;
    List<VendorYearTarget> getVendorYearTarget(long vendorRrn, String targetYear) throws ClientException;
}

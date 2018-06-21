package com.graly.erp.vdm.vendormaterial;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.vdm.client.VDMManager;
import com.graly.erp.vdm.model.Vendor;
import com.graly.erp.vdm.model.VendorMaterial;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.base.entitymanager.forms.EntityProperties;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;

public class VendorMaterialProperties extends EntityProperties {
	private static final Logger logger = Logger.getLogger(VendorMaterialProperties.class);
			
//	public final static Long orgRrn = 139420L + 12644730L;//奔泰的orgrrn
	public final static Map<Long,long[]> syncOrgMaps = new HashMap<Long,long[]>();
	{
		syncOrgMaps.put(139420L, new long[]{12644730,41673024});
		syncOrgMaps.put(12644730L, new long[]{139420});
		syncOrgMaps.put(41673024L, new long[]{139420});
	}
	
	@Override
	protected void saveAdapter() {
		try {
			form.getMessageManager().setAutoUpdate(false);
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				ADBase oldBase = getAdObject();
				boolean saveFlag = true;
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
					}
				}
				if (saveFlag) {
					for (Form detailForm : getDetailForms()) {
						PropertyUtil.copyProperties(getAdObject(), detailForm.getObject(), detailForm.getFields());
					}
					
					//save vendormaterial in this area and synchronize it in another area
					//1.save this vendormaterial
					VDMManager vdmManager = Framework.getService(VDMManager.class);
					VendorMaterial vendorMaterial = vdmManager.saveVendorMaterial((VendorMaterial) getAdObject(), Env.getUserRrn());
					ADManager adManager = Framework.getService(ADManager.class);
					setAdObject(adManager.getEntity(vendorMaterial));
					
					/**取消同步的功能 2012/9/6
					//2.find vendormaterial by the vendor,if it does not exists add it and set primary vendor true,otherwise filter it by
					//material if someone exits update it exclude primary vendor ,otherwise add it and set primary vendor true
					
					//2.1 find vendormaterial by vendor
					VendorMaterial thisVm = (VendorMaterial) getAdObject();
					VendorMaterial anotherVm = null;
					Vendor thisVendor = thisVm.getVendor();
					List<Vendor> anotherVendors = vdmManager.getVendorByVendorId(orgRrn - thisVm.getOrgRrn(), thisVendor.getVendorId());
					Vendor anotherVendor = null;
					PDMManager pdmManager = Framework.getService(PDMManager.class);
					if(anotherVendors == null || anotherVendors.size() == 0){
						//if there is no vendor at all,add it
						anotherVendor = copyVender(thisVendor, anotherVendor);
						anotherVendor = (Vendor) adManager.saveEntity(anotherVendor, Env.getUserRrn());
					}else{
						anotherVendor = anotherVendors.get(0);
					}
					
					Material thisMaterial = thisVm.getMaterial();
					List<Material> anotherMaterials = pdmManager.getMaterialById(thisMaterial.getMaterialId(), orgRrn - thisMaterial.getOrgRrn());
					Material anotherMaterial = null;
					//if there is no material, add it
					if(anotherMaterials == null || anotherMaterials.size() == 0){
						anotherMaterial = copyMaterial(thisMaterial, anotherMaterial);
						anotherMaterial = pdmManager.saveMaterial(0L, anotherMaterial, Env.getUserRrn());
					}else{
						anotherMaterial = anotherMaterials.get(0);
					}
					
					List<VendorMaterial> vdms = vdmManager.getVendorMaterials(anotherVendor.getObjectRrn(), anotherMaterial.getObjectRrn());
					if(vdms == null || vdms.size() == 0){
					}else{
						anotherVm = vdms.get(0);
					}
					//copy vendormaterial
					anotherVm = copyVendorMaterial(thisVm, anotherVm);
					anotherVm.setMaterialRrn(anotherMaterial.getObjectRrn());
					anotherVm.setVendorRrn(anotherVendor.getObjectRrn());
					
					List<VendorMaterial> vdms2 = vdmManager.getVendorMaterialsByMaterial(anotherMaterial.getObjectRrn());
					if(vdms2 == null || vdms2.size() == 0){
						anotherVm.setIsPrimary(true);
					}
					
					vdmManager.saveVendorMaterial(anotherVm, Env.getUserRrn());
					***/
					UI.showInfo(Message.getString("common.save_successed"));// 弹出提示框
					refresh();
				}
				ADBase newBase = getAdObject();
				if (oldBase.getObjectRrn() == null) {
					getMasterParent().refreshAdd(newBase);
				} else {
					getMasterParent().refreshUpdate(newBase);
				}
//				getMasterParent().refresh();
			}
			form.getMessageManager().setAutoUpdate(true);
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}
	
	private Vendor copyVender(Vendor source, Vendor target, long targetOrgRrn){
		if(target == null){
			try {//如果是新建,则同步
				target = (Vendor) source.clone();
				target.setOrgRrn(targetOrgRrn);
			} catch (CloneNotSupportedException e) {
				logger.error("VendorProperties:copyVendor()",e);
			}
		}else{//如果是修改则不同步
//			target.setVendorType(source.getVendorType());
//			target.setShipmentCode(source.getShipmentCode());
//			target.setStatus(source.getStatus());
//			target.setCompanyName(source.getCompanyName());
//			target.setDescription(source.getDescription());
//			target.setContact(source.getContact());
//			target.setPhone1(source.getPhone1());
//			target.setPhone2(source.getPhone2());
//			target.setAddress(source.getAddress());
//			target.setUrl(source.getUrl());
//			target.setFax(source.getFax());
//			target.setZipCode(source.getZipCode());
//			target.setCountry(source.getCountry());
//			target.setArea(source.getArea());
//			target.setTermsCode(source.getTermsCode());
//			target.setContractLife(source.getContractLife());
//			target.setBankName(source.getBankName());
//			target.setAccountId(source.getAccountId());
//			target.setComments(source.getComments());
//			target.setContractDoc(source.getContractDoc());
//			target.setContractStart(source.getContractStart());
//			target.setContractEnd(source.getContractEnd());
//			target.setIsIssueInvoice(source.getIsIssueInvoice());
//			target.setInvoiceType(source.getInvoiceType());
//			target.setVatRate(source.getVatRate());
		}
		return target;
	}
	
	private VendorMaterial copyVendorMaterial(VendorMaterial source, VendorMaterial target, long targetOrgRrn){
		if(target == null){
			try {//如果是新建,则同步
				target = (VendorMaterial) source.clone();
				target.setOrgRrn(targetOrgRrn);
			} catch (CloneNotSupportedException e) {
				logger.error("VendorMaterialProperties:copyVendorMaterial()", e);
			}
		}else{//如果是修改则不同步
//			target.setVendorRrn(source.getVendorRrn());
//			target.setFeedType(source.getFeedType());
//			target.setAssessmentMark(source.getAssessmentMark());
//			target.setReferencedPrice(source.getReferencedPrice());
//			target.setHighestPrice(source.getHighestPrice());
//			target.setLastPrice(source.getLastPrice());
//			target.setLeadTime(source.getLeadTime());
//			target.setFeedMaterial(source.getFeedMaterial());
//			target.setLeastQuantity(source.getLeastQuantity());
//			target.setIncreaseQuantity(source.getIncreaseQuantity());
//			target.setComments(source.getComments());
//			target.setIsPrimary(source.getIsPrimary());
//			target.setVendorId(source.getVendorId());
//			target.setMaterialId(source.getMaterialId());
//			target.setPurchaser(source.getPurchaser());
//			target.setAdvanceRatio(source.getAdvanceRatio());
//			target.setAveragePrice(source.getAveragePrice());
//			target.setLowestPrice(source.getLowestPrice());
		}
		return target;
	}
	
	private Material copyMaterial(Material source, Material target, long targetOrgRrn){
		if(target == null){
			try {//如果是新建,则同步
				target = (Material) source.clone();
				target.setOrgRrn(targetOrgRrn);
			} catch (CloneNotSupportedException e) {
				logger.error(e);
			}
		}else{
			//如果是修改则不同步
//			target.setMaterialId(source.getMaterialId());
//			target.setName(source.getName());
//			target.setDescription(source.getDescription());
//			target.setComments(source.getComments());
//			target.setMaterialCategory(source.getMaterialCategory1());
//			target.setMaterialType(source.getMaterialType());
//			target.setRefernectPrice(source.getRefernectPrice());
//			target.setSpecification(source.getSpecification());
//			target.setModel(source.getModel());
//			target.setWeight(source.getWeight());
//			target.setVolume(source.getVolume());
//			target.setInventoryUom(source.getInventoryUom());
//			target.setPurchaseUom(source.getPurchaseUom());
//			target.setPurInvConversion(source.getPurInvConversion());
//			target.setInventoryProperty(source.getInventoryProperty());
//			target.setAbcCategory(source.getAbcCategory());
//			target.setIsJit(source.getIsJit());
//			target.setIsVirtual(source.getIsVirtual());
//			target.setIsPurchase(source.getIsPurchase());
//			target.setIsSale(source.getIsSale());
//			target.setIsProduct(source.getIsProduct());
//			target.setIsMrp(source.getIsMrp());
//			target.setStockCapability(source.getStockCapability());
//			target.setQtyMin(source.getQtyMin());
//			target.setQtyMax(source.getQtyMax());
//			target.setQtyInitial(source.getQtyInitial());
//			target.setQtyIn(source.getQtyIn());
//			target.setQtyOut(source.getQtyOut());
//			target.setQtyDifference(source.getQtyDifference());
//			target.setQtyTransit(source.getQtyTransit());
//			target.setQtyAllocation(source.getQtyAllocation());
//			target.setReferenceDoc1(source.getReferenceDoc1());
//			target.setReferenceDoc2(source.getReferenceDoc2());
//			target.setReferenceDoc3(source.getReferenceDoc3());
//			target.setReferenceDoc4(source.getReferenceDoc4());
//			target.setReferenceDoc5(source.getReferenceDoc5());
//			target.setProductionCycle(source.getProductionCycle());
//			target.setWorkCenterRrn(source.getWorkCenterRrn());
//			target.setPhoto(source.getPhoto());
//			target.setBuyerId(source.getBuyerId());
//			target.setPlannerId(source.getPlannerId());
//			target.setKeeperId(source.getKeeperId());
//			target.setStandCost(source.getStandCost());
//			target.setActrualCost(source.getActrualCost());
//			target.setLotType(source.getLotType());
//			target.setState(source.getState());
//			target.setStandTime(source.getStandTime());
//			target.setQtyMinProduct(source.getQtyMinProduct());
//			target.setIsShare(source.getIsShare());
//			target.setMaterialCategory2(source.getMaterialCategory2());
//			target.setMaterialCategory3(source.getMaterialCategory3());
//			target.setMaterialCategory4(source.getMaterialCategory4());
//			target.setMaterialCategory1(source.getMaterialCategory1());
//			target.setIqcLeadTime(source.getIqcLeadTime());
//			target.setBomPrice(source.getBomPrice());
//			target.setApplicant(source.getApplicant());
//			target.setDateApply(source.getDateApply());
//			target.setMaterialCategory6(source.getMaterialCategory6());
//			target.setMaterialCategory5(source.getMaterialCategory5());
//			target.setLength(source.getLength());
//			target.setWidth(source.getWidth());
//			target.setHeight(source.getHeight());
//			target.setCostFormula(source.getCostFormula());
//			target.setIsLotControl(source.getIsLotControl());
//			target.setIsInspectionFree(source.getIsInspectionFree());
//			target.setIsVolumeBasis(source.getIsVolumeBasis());
//			target.setProcessName(source.getProcessName());
		}
		return target;
	}
}

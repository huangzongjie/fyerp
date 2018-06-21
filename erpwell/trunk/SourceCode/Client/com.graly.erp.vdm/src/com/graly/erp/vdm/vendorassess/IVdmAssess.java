package com.graly.erp.vdm.vendorassess;

import java.util.Date;

import com.graly.framework.base.entitymanager.IRefresh;

public interface IVdmAssess extends IRefresh {
	void setVendorRrn(Long vendorRrn);
	void setMaterialRrn(Long materialRrn);
	void setPurchaser(String purchaser);
	void setStartDate(Date dateStart);
	void setEndDate(Date dateEnd);
}

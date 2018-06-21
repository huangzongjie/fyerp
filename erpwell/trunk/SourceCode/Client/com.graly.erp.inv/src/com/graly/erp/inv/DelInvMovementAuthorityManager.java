package com.graly.erp.inv;

import java.util.List;

import com.graly.erp.inv.model.VUserWarehouse;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;

/**
 * @author Jim 
 * 删除出入库单的权限管理
 */
public class DelInvMovementAuthorityManager {
	private static ADManager adManager;
	private static List<VUserWarehouse> warehouseList;
//	private static long UserRrn = -1L;
	
	public static List<VUserWarehouse> getAuthorityWarehouseList(long userRrn, long warehouseRrn) throws Exception {
//		if(userRrn == UserRrn) {
//			if(warehouseList != null)
//				return warehouseList;
//		} else {
//			UserRrn = userRrn;
//		}
		if(adManager == null)
			adManager = Framework.getService(ADManager.class);
		String whereClause = " userRrn = " + userRrn + " ";
		warehouseList = adManager.getEntityList(Env.getOrgRrn(), VUserWarehouse.class,
				Env.getMaxResult(), whereClause, null);
		
		return warehouseList;
	}
	
	public static boolean hasDeleteAuthority(long userRrn, long warehouseRrn, String warehosueId) throws Exception {
		for(VUserWarehouse vwh : getAuthorityWarehouseList(userRrn, warehouseRrn)) {
			if(warehouseRrn == vwh.getObjectRrn().longValue())
				return true;
		}
		UI.showError(String.format(Message.getString("inv.has_not_del_authority"), warehosueId));
		return false;
	}
}

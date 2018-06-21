package com.graly.erp.inv.transfer;

import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.views.ItemAdapterFactory;
import com.graly.framework.base.ui.views.ListItemAdapter;

public class TrsLotTableManager extends TableListManager {
	public String LotId = "lotId";
	public String MaterialId = "materialId";
	public String MaterialName = "materialName";
	public String CurrentQty = "qtyCurrent";
	public String InDate = "dateIn";
	public String LocatorId = "locatorId";

	public String Cloumn_LotId = Message.getString("inv.lotid");
	public String Cloumn_MaterialID = Message.getString("pdm.material_id");
	public String Cloumn_MaterialName = Message.getString("pdm.material_name");
	public String Cloumn_CurrentQty = Message.getString("inv.lot_qty");
	public String Cloumn_InDate = Message.getString("inv.in_date");
	public String Cloumn_LocatorId = Message.getString("inv.locator_id");
	
	private GainableSplitLot gainable;
	
	public TrsLotTableManager(GainableSplitLot gainable) {
		super(null);
		this.gainable = gainable;
	}
	
//	@Override
//    protected ItemAdapterFactory createAdapterFactory() {
//        ItemAdapterFactory factory = new ItemAdapterFactory();
//        try{
//	        factory.registerAdapter(Object.class, new TrsLotItemAdapter(gainable));
//        } catch (Exception e){
//        	e.printStackTrace();
//        }
//        return factory;
//    }
	
    @Override
    protected String[] getColumns() {
    	return new String[]{LotId, MaterialId, MaterialName, CurrentQty, InDate, LocatorId};
    }
    
    @Override
    protected String[] getColumnsHeader() {
    	return new String[]{Cloumn_LotId, Cloumn_MaterialID, Cloumn_MaterialName,
    			Cloumn_CurrentQty, Cloumn_InDate, Cloumn_LocatorId};
    }
    
    protected Integer[] getColumnSize() {
    	return new Integer[]{15, 15, 25, 10, 15, 15};
    }
}

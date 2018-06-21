package com.graly.erp.wip.disassemblelot;

import java.util.List;

import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.views.ItemAdapterFactory;
import com.graly.framework.base.ui.views.ListItemAdapter;
import com.graly.mes.wip.model.LotComponent;

public class LotComponentListManager extends TableListManager {

	public LotComponentListManager(ADTable adTable, int style) {
		super(adTable, style);
	}

	public LotComponentListManager(ADTable adTable) {
		super(adTable);
	}
	
	@Override
    protected ItemAdapterFactory createAdapterFactory() {
        ItemAdapterFactory factory = new ItemAdapterFactory();
        try{
	        factory.registerAdapter(LotComponent.class, new LotComponentItemAdapter<LotComponent>());
	        factory.registerAdapter(Object.class, new ListItemAdapter<LotComponent>());
        } catch (Exception e){
        	e.printStackTrace();
        }
        return factory;
    }
}

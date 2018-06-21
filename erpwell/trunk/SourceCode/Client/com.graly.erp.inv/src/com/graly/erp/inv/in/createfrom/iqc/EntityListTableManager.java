package com.graly.erp.inv.in.createfrom.iqc;

import java.util.List;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.adapter.EntityItemAdapter;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.views.ItemAdapterFactory;
import com.graly.framework.base.ui.views.ListItemAdapter;

public class EntityListTableManager extends EntityTableManager {

	public EntityListTableManager(ADTable adTable) {
		super(adTable);
	}
	
	@Override
    protected ItemAdapterFactory createAdapterFactory() {
        ItemAdapterFactory factory = new ItemAdapterFactory();
        try{
	        factory.registerAdapter(Object.class, new EntityItemAdapter());
	        factory.registerAdapter(List.class, new ListItemAdapter());
        } catch (Exception e){
        	e.printStackTrace();
        }
        return factory;
    }

}

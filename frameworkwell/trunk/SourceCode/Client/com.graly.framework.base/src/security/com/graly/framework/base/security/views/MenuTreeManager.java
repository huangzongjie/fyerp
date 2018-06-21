package com.graly.framework.base.security.views;

import java.util.List;

import com.graly.framework.security.model.ADAuthority;
import com.graly.framework.base.entitymanager.adapter.EntityItemAdapter;
import com.graly.framework.base.ui.views.ItemAdapterFactory;
import com.graly.framework.base.ui.views.TreeViewerManager;
import com.graly.framework.base.security.adapter.MenuItemAdapter;
import com.graly.framework.base.ui.views.ItemAdapter;

public class MenuTreeManager extends TreeViewerManager {
	
 	@Override
    protected ItemAdapterFactory createAdapterFactory() {
        ItemAdapterFactory factory = new ItemAdapterFactory();
        try{
        	ItemAdapter adapter = new MenuItemAdapter();
	        factory.registerAdapter(List.class, adapter);
	        factory.registerAdapter(ADAuthority.class, adapter);
        } catch (Exception e){
        	e.printStackTrace();
        }
        return factory;
    }
}

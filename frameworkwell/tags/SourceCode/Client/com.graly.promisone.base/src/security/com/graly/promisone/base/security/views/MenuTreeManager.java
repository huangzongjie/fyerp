package com.graly.promisone.base.security.views;

import java.util.List;

import com.graly.promisone.security.model.ADMenu;
import com.graly.promisone.base.entitymanager.adapter.EntityItemAdapter;
import com.graly.promisone.base.ui.views.ItemAdapterFactory;
import com.graly.promisone.base.ui.views.TreeViewerManager;
import com.graly.promisone.base.security.adapter.MenuItemAdapter;
import com.graly.promisone.base.ui.views.ItemAdapter;

public class MenuTreeManager extends TreeViewerManager {
	
 	@Override
    protected ItemAdapterFactory createAdapterFactory() {
        ItemAdapterFactory factory = new ItemAdapterFactory();
        try{
        	ItemAdapter adapter = new MenuItemAdapter();
	        factory.registerAdapter(List.class, adapter);
	        factory.registerAdapter(ADMenu.class, adapter);
        } catch (Exception e){
        	e.printStackTrace();
        }
        return factory;
    }
}

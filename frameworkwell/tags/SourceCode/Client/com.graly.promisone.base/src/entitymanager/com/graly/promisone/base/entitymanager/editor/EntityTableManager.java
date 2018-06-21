package com.graly.promisone.base.entitymanager.editor;

import org.eclipse.ui.IEditorInput;

import com.graly.promisone.base.entitymanager.adapter.EntityItemAdapter;
import com.graly.promisone.base.entitymanager.views.TableViewerManager;
import com.graly.promisone.base.ui.views.ItemAdapterFactory;
import com.graly.promisone.activeentity.model.ADBase;
import com.graly.promisone.activeentity.model.ADTable;

public class EntityTableManager extends TableViewerManager {
	
	public EntityTableManager(ADTable adTable){
		super(adTable);
	}
	
	@Override
    protected ItemAdapterFactory createAdapterFactory() {
        ItemAdapterFactory factory = new ItemAdapterFactory();
        try{
	        factory.registerAdapter(Object.class, new EntityItemAdapter());
        } catch (Exception e){
        	e.printStackTrace();
        }
        return factory;
    }
	
}

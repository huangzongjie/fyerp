package com.graly.framework.base.entitymanager.editor;

import org.eclipse.ui.IEditorInput;

import com.graly.framework.base.entitymanager.adapter.EntityItemAdapter;
import com.graly.framework.base.entitymanager.views.TableViewerManager;
import com.graly.framework.base.ui.views.ItemAdapterFactory;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;

public class EntityTableManager extends TableViewerManager {
	
	public EntityTableManager(ADTable adTable){
		super(adTable);
	}
	
	public EntityTableManager(ADTable adTable,int style){
		super(adTable); 
		this.style = style;
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
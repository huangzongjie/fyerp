package com.graly.framework.base.entitymanager.views;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.eclipse.swt.SWT;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.base.ui.views.ItemAdapterFactory;
import com.graly.framework.base.ui.views.ListItemAdapter;

public class TableListManager extends TableViewerManager {	
	
	public TableListManager(ADTable adTable) {
		super(adTable);
		super.addStyle( SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
	}
    
	public TableListManager(ADTable adTable, int style) {
		this(adTable);
		super.addStyle(style);
	}
	
	@Override
    protected ItemAdapterFactory createAdapterFactory() {
        ItemAdapterFactory factory = new ItemAdapterFactory();
        try{
	        factory.registerAdapter(Object.class, new ListItemAdapter<ADBase>());
        } catch (Exception e){
        	e.printStackTrace();
        }
        return factory;
    }
}

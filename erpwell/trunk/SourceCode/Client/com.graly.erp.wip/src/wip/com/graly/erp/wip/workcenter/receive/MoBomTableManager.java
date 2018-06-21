package com.graly.erp.wip.workcenter.receive;

import org.eclipse.swt.SWT;

import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.views.ItemAdapterFactory;

public class MoBomTableManager extends TableListManager {
	GainableMoBoms gainable;
	
	@Override
    protected ItemAdapterFactory createAdapterFactory() {
        ItemAdapterFactory factory = new ItemAdapterFactory();
        try{
	        factory.registerAdapter(Object.class, new MoBomItemAdapter(gainable));
        } catch (Exception e){
        	e.printStackTrace();
        }
        return factory;
    }

	public MoBomTableManager(GainableMoBoms gainable) {
		super(null);
		super.addStyle( SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		this.gainable = gainable;
	}
	
    @Override
    protected String[] getColumns() {
    	return new String[]{TextProvider.FieldName_MaterialId,
    			TextProvider.FieldName_MaterialName,
    			TextProvider.FieldName_LotType,
    			TextProvider.FieldName_UnitQty};
    }
    
    @Override
    protected String[] getColumnsHeader() {
    	return new String[]{TextProvider.MaterialId, TextProvider.MaterialName,
    			TextProvider.LotType, TextProvider.TotalQty};
    }
    
    protected Integer[] getColumnSize() {
    	return new Integer[]{32, 64, 32, 32};
    }
}

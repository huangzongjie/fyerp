package com.graly.erp.wip.workcenter.bom;

import java.util.List;

import org.eclipse.swt.SWT;

import com.graly.erp.pur.model.RequisitionLine;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.erp.wip.workcenter.receive.TextProvider;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.views.AbstractItemAdapter;
import com.graly.framework.base.ui.views.ItemAdapterFactory;

public class WorkOrderBomManager extends TableListManager {
	
	public WorkOrderBomManager() {
		super(null);
		super.addStyle( SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
	}

	public WorkOrderBomManager(ADTable adTable) {
		super(adTable);
		super.addStyle(style);
	}
	
	@Override
    protected ItemAdapterFactory createAdapterFactory() {
        ItemAdapterFactory factory = new ItemAdapterFactory();
        try {
        	AbstractItemAdapter itemAdapter = new MoLineItemAdapter();
        	factory.registerAdapter(ManufactureOrderLine.class, itemAdapter);
	        factory.registerAdapter(ManufactureOrderBom.class, itemAdapter);
	        factory.registerAdapter(RequisitionLine.class, itemAdapter);
	        factory.registerAdapter(List.class, itemAdapter);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return factory;
    }
	
    @Override
    protected String[] getColumns() {
    	return new String[]{TextProvider.FieldName_MaterialId,
    			TextProvider.FieldName_MaterialName,
    			TextProvider.FieldName_ProductQty,
    			TextProvider.FieldName_FinishedQty,
    			TextProvider.FieldName_NeedQty,
    			TextProvider.FieldName_StorageQty,
    			TextProvider.FieldName_TransactionQty,
    			TextProvider.FieldName_LineWipQty,
    			TextProvider.FieldName_AllocationQty,
    			TextProvider.FieldName_DateStart,
    			TextProvider.FieldName_DateEnd};
    }

    @Override
    protected String[] getColumnsHeader() {
    	return new String[]{TextProvider.MaterialId,
    			TextProvider.MaterialName,
    			TextProvider.ProductQty,
    			TextProvider.FinishedQty,
    			TextProvider.NeedQty,
    			TextProvider.StorageQty,
    			TextProvider.TransationQty,
    			TextProvider.LineWipQty,
    			TextProvider.AllocationQty,
    			TextProvider.DateStart,
    			TextProvider.DateEnd};
    }
    
    protected Integer[] getColumnSize() {
    	return new Integer[]{26, 40, 20, 20, 20, 20, 20, 20, 20, 26, 26};
    }
}

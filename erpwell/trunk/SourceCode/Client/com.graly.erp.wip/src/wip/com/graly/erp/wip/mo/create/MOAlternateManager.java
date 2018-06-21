package com.graly.erp.wip.mo.create;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TreeViewer;

import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.views.AbstractItemAdapter;
import com.graly.framework.base.ui.views.ItemAdapterFactory;

public class MOAlternateManager extends MOTreeManager {
	private static final Logger logger = Logger.getLogger(MOAlternateManager.class);
	private int[] columnWidths = new int[]{300, 300, 64, 128, 300,64};

	public MOAlternateManager(ADTable adTable) {
		super(adTable);
	}

	@Override
	protected ItemAdapterFactory createAdapterFactory() {
		ItemAdapterFactory factory = new ItemAdapterFactory();
		try {
        	AbstractItemAdapter itemAdapter = new MOBomItemAdapter();
	        factory.registerAdapter(List.class, itemAdapter);
	        factory.registerAdapter(ManufactureOrderBom.class, itemAdapter);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return factory;
	}
	
	protected void setCellEditor(TreeViewer treeViewer) {}

	@Override
	protected String[] getColumns() {
		return MOGenerateContext.Columns;
	}
	
	@Override
    protected String[] getColumnsHeader() {
		return MOGenerateContext.ColumnHeaders;
	}
	
	public int[] getColumnWidths() {
		return columnWidths;
	}
}

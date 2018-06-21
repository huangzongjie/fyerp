package com.graly.erp.wip.mo.create;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.views.AbstractItemAdapter;
import com.graly.framework.base.ui.views.ItemAdapterFactory;

public class MOBomTreeManager extends MOTreeManager {
	protected MOBomListSection parentSection;
	private int[] columnWidths = new int[]{150, 120, 80, 80, 80, 80, 120, 80, 100, 100, 100};
	
	public MOBomTreeManager(ADTable adTable) {
		super(adTable);
	}
	
	public MOBomTreeManager(ADTable adTable, MOBomListSection parentSection) {
		this(adTable);
		this.parentSection = parentSection;
		this.canEdit = parentSection.isCanEdit();
	}
	
	@Override
    protected ItemAdapterFactory createAdapterFactory() {
        ItemAdapterFactory factory = new ItemAdapterFactory();
        try {
        	AbstractItemAdapter itemAdapter = new MOBomItemAdapter();
	        factory.registerAdapter(List.class, itemAdapter);
	        factory.registerAdapter(ManufactureOrderBom.class, itemAdapter);
        } catch (Exception e){
        	e.printStackTrace();
        }
        return factory;
    }
	
	@Override
	protected StructuredViewer newViewer(Composite parent, FormToolkit toolkit) {		
		Tree tree = toolkit.createTree(parent, style); 
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
    	GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
    	tree.setLayoutData(gd);    	
    	Rectangle listRect = tree.getBounds ();
    	gd.heightHint = tree.getItemHeight () * 18;
    	tree.setBounds(listRect);
    	tree.setLayoutData(gd);
    	
	    toolkit.paintBordersFor(parent);  
	    treeViewer = new TreeViewer(tree);
    	fillColumns(tree);

		return treeViewer;
	}
	
	@Override
    protected String[] getColumns() {
    	if (getADTable() instanceof ADTable){
        	List<String> columnsList = new ArrayList<String>();
        	ADTable table = getADTable();
        	for (ADField field : table.getFields()){
        		if (field.getIsMain()){
        			columnsList.add(field.getName());
        		}
        	}
        	columnsList.add(MOGenerateContext.Comments);
        	return columnsList.toArray(new String[]{});
        }
    	return new String[]{};
    }
    
    @Override
    protected String[] getColumnsHeader() {
    	if (getADTable() instanceof ADTable){
        	List<String> columnsHeaderList = new ArrayList<String>();
        	ADTable table = getADTable();
        	for (ADField field : table.getFields()){
        		if (field.getIsMain()){
        			columnsHeaderList.add(I18nUtil.getI18nMessage(field, "label"));
        		}
        	}
        	columnsHeaderList.add(MOGenerateContext.COMMENTS);
        	return columnsHeaderList.toArray(new String[]{});
        }
    	return new String[]{};
    }
    
	public int[] getColumnWidths() {
		return columnWidths;
	}
}

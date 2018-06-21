package com.graly.erp.pur.request.refmo;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.base.model.DocumentationLine;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.views.AbstractItemAdapter;
import com.graly.framework.base.ui.views.ItemAdapterFactory;
import com.graly.framework.base.ui.views.TreeViewerManager;

public class MoTreeManager extends TreeViewerManager {
	private int[] columnWidths = new int[]{240, 380, 72, 70, 72, 70, 63, 63, 63, 80};
	
	protected SubMoLineSection parentSection;
	protected TreeViewer treeViewer;
	protected ADTable table;
	protected boolean canEdit = true;
	
	public MoTreeManager(ADTable adTable) {
		super();
		this.table = adTable;
	}
	
	public MoTreeManager(ADTable adTable, SubMoLineSection parentSection) {
		this(adTable);
		this.parentSection = parentSection;
		this.canEdit = parentSection.isCanEdit();
	}
	
	@Override
    protected ItemAdapterFactory createAdapterFactory() {
        ItemAdapterFactory factory = new ItemAdapterFactory();
        try{
        	AbstractItemAdapter itemAdapter = new MoLineItemAdapter<Object>();
	        factory.registerAdapter(List.class, itemAdapter);
	        factory.registerAdapter(DocumentationLine.class, itemAdapter);
        } catch (Exception e){
        	e.printStackTrace();
        }
        return factory;
    }
	
	@Override
	protected StructuredViewer newViewer(Composite parent, FormToolkit toolkit) {
		Tree tree;
		if (parent instanceof Tree) {
			tree = (Tree) parent;
		} else {
			tree = toolkit.createTree(parent, style);
		}
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		Rectangle listRect = tree.getBounds();
		gd.heightHint = tree.getItemHeight () * 18;
		tree.setBounds(listRect);
		tree.setLayoutData(gd);
		GridLayout gl = new GridLayout(3, false);
		//new WeightedTableLayout(columnWidths, columnWidths)
		tree.setLayout(gl);
		treeViewer = new TreeViewer(tree);
		fillColumns(tree);
		
		return treeViewer;
	}
	
	protected void fillColumns(Tree tree) {
        String[] columns = getColumns();
        String[] columnsHeader = getColumnsHeader();
        int[] widths = this.getColumnWidths();
        if (columns != null) {
			tree.setLinesVisible(true);
			tree.setHeaderVisible(true);

			for (int i = 0; i < columns.length; i++) {
				TreeColumn column;
				column = new TreeColumn(tree, SWT.BORDER);
				if(i < widths.length) {
					column.setWidth(widths[i]);
				} else {
					column.setWidth(150);					
				}
				if (columnsHeader != null) {
					column.setText(columnsHeader[i]);
				} else {
					column.setText(columns[i]);
				}
				column.setData(columns[i]);
				column.setResizable(true);
			}
		}
		tree.pack();
    }
	
    @Override
    protected String[] getColumns() {
    	if (getADTable() instanceof ADTable){
        	List<String> columnsList = new ArrayList<String>();
        	ADTable table = getADTable();
        	for (ADField field : table.getFields()){
        		if (field.getIsMain() && field.getIsDisplay()){
        			columnsList.add(field.getName());
        		}
        	}
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
        		if (field.getIsMain() && field.getIsDisplay()){
        			columnsHeaderList.add(I18nUtil.getI18nMessage(field, "label"));
        		}
        	}
        	return columnsHeaderList.toArray(new String[]{});
        }
    	return new String[]{};
    }
    
	public ADTable getADTable() {
		return table;
	}

	public void getADTable(ADTable table) {
		this.table = table;
	}

	public SubMoLineSection getParentSection() {
		return parentSection;
	}

	public void setParentSection(SubMoLineSection parentSection) {
		this.parentSection = parentSection;
	}

	public int[] getColumnWidths() {
		return columnWidths;
	}

	public void setColumnWidths(int[] columnWidths) {
		this.columnWidths = columnWidths;
	}
}

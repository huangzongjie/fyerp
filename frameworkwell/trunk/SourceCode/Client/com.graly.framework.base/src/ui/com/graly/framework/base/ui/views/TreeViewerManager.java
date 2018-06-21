package com.graly.framework.base.ui.views;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.base.ui.layout.WeightedTableLayout;
import com.graly.framework.base.ui.util.SWTResourceCache;
public class TreeViewerManager extends StructuredViewerManager {
	
	protected int style;

    public TreeViewerManager() {
        style = SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION;
    }
 
    public TreeViewerManager(int style) {
        this.style = style;
    }
    
    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public void addStyle(int style) {
        this.style = this.style | style;
    }
	    
	@Override
	protected ItemAdapterFactory createAdapterFactory() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected StructuredViewer newViewer(Composite parent, FormToolkit toolkit) {
		Tree tree;
		if(parent instanceof Tree){
			tree = (Tree)parent;
		}else{
			tree = toolkit.createTree(parent, SWT.LEFT);
		}
    	GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		tree.setLayoutData(gd);
        TreeViewer tv = new TreeViewer(tree);
        fillColumns(tree);
        return tv;
	}
	
	@Override
	protected StructuredViewer newViewer(Composite parent, FormToolkit toolkit, int heightHint) {
		return newViewer(parent, toolkit);
	}
	
	@Override
	protected StructuredViewer newViewer(Composite parent, FormToolkit toolkit,
			String[] columns, String[] columnsHeaders, int[] columnsSize) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	protected String[] getColumns() {
        return new String[]{"Column 1"};
    }
	
	protected void fillColumns(Tree tree) {
        String[] columns = getColumns();
        String[] columnsHeader = getColumnsHeader();
        if (columns != null) {
        	tree.setFont(SWTResourceCache.getFont("Verdana"));
            tree.setLinesVisible(false);
            tree.setHeaderVisible(false);

            for (int i = 0; i < columns.length; i++) {
                TreeColumn column;
                column = new TreeColumn(tree, SWT.NONE);
                column.setData(COLUMN_ID, columns[i]);
                if (columnsHeader != null) {
                    column.setText(columnsHeader[i]);
                } else {
                    column.setText(columns[i]);
                }
                column.setResizable(true);
            }
            int[] columnLayout = new int[1];
            columnLayout[0] = 100;
            tree.setLayout(new WeightedTableLayout(columnLayout));
        }
        tree.pack();
    }		
}

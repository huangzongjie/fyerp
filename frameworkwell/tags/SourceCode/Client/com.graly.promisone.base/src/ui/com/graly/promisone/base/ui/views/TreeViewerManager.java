package com.graly.promisone.base.ui.views;

import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.promisone.base.ui.layout.WeightedTableLayout;
import com.graly.promisone.base.ui.util.SWTResourceCache;
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
                column.setData("id", columns[i]);
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

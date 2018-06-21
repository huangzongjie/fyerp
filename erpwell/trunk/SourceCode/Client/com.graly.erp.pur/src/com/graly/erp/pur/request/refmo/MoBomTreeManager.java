package com.graly.erp.pur.request.refmo;

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
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.views.AbstractItemAdapter;
import com.graly.framework.base.ui.views.ItemAdapterFactory;

public class MoBomTreeManager extends MoTreeManager {
	private int[] columnWidths = new int[]{200, 300, 150, 150, 180, 180};
	protected MoBomListSection parentSection;
	
	public MoBomTreeManager(ADTable adTable) {
		super(adTable);
	}
	
	public MoBomTreeManager(ADTable adTable, MoBomListSection parentSection) {
		this(adTable);
		this.parentSection = parentSection;
		this.canEdit = parentSection.isCanEdit();
	}
	
	@Override
    protected ItemAdapterFactory createAdapterFactory() {
        ItemAdapterFactory factory = new ItemAdapterFactory();
        try{
        	AbstractItemAdapter itemAdapter = new MoBomItemAdapter();
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
    
	public int[] getColumnWidths() {
		return columnWidths;
	}
}

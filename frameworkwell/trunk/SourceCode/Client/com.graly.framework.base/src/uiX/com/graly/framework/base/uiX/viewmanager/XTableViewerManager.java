package com.graly.framework.base.uiX.viewmanager;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.layout.WeightedTableLayout;

public class XTableViewerManager extends AbstractXViewerManager {
	/*
	 * adTable提供列表中显示的栏位相关的信息,即adfield,从adapter中来
	 */
	protected ADTable adTable;
	public XTableViewerManager(IXViewerAdapter adapter) {
		super(adapter);
		this.adTable = ((AbstractXViewerAdapter)adapter).getAdTable();
	}

	@Override
	protected StructuredViewer createViewer(Composite parent) {
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());
		Table table = toolkit.createTable(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.VIRTUAL); 
    	table.setHeaderVisible(true);
    	table.setLinesVisible(true);
    	GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd.heightHint = 80;
		gd.widthHint = 100;
    	table.setLayoutData(gd);
	    toolkit.paintBordersFor(table); 
    	
		TableViewer tv = new TableViewer(table);
		return tv;
	}
	
	@Override
	protected void adapterViewer() {
		super.adapterViewer();
		
		Assert.isTrue(viewer instanceof TableViewer);
		
		Assert.isTrue(adTable != null);
		
		if(adTable == null){
			return;
		}
		
		ColumnData[] cDatas = adapter.getColumnDatas();
		
		Table table = ((TableViewer)viewer).getTable();
		int[] columnLayout = new int[cDatas.length];
		int totalWidth = 0;
		for(int i=0; i< cDatas.length; i++){
			ColumnData cData = cDatas[i];
			String columnLabel = cData.getColumnLabel();
			
			TableColumn tC = new TableColumn(table, SWT.NULL);
			tC.setText(columnLabel);
			tC.setData(cData);
			
			tC.setWidth(cData.getColumnWidth());
			columnLayout[i] = cData.getColumnWidth();
			tC.setWidth(1);
            tC.setResizable(true);
			totalWidth+=cData.getColumnWidth();
		}
		
    	table.setLayout(new WeightedTableLayout(columnLayout, columnLayout));
	}
}

package com.graly.erp.wip.mo.material_standtime;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.model.Bom;
import com.graly.framework.base.ui.layout.WeightedTableLayout;
import com.graly.framework.base.ui.views.ItemAdapter;
import com.graly.framework.base.ui.views.ItemAdapterFactory;
import com.graly.framework.base.ui.views.TreeViewerManager;

public class MaterialTreeManager extends TreeViewerManager {
	private static final Logger logger = Logger.getLogger(MaterialTreeManager.class);

	protected EnableExpendAll eeall;
	private int[] columnWidths = new int[]{64, 64, 16, 16, 16, 16,16,16};

	public MaterialTreeManager(int style, EnableExpendAll eeall) {
		super(style);
		this.eeall = eeall;
	}

	@Override
	protected ItemAdapterFactory createAdapterFactory() {
		ItemAdapterFactory factory = new ItemAdapterFactory();
		try {
			ItemAdapter materialAdapter = new MaterialItemAdapter();
			factory.registerAdapter(List.class, materialAdapter);
			factory.registerAdapter(Material.class, materialAdapter);
			factory.registerAdapter(Bom.class, new BomItemAdapter(eeall));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return factory;
	}

	@Override
	protected StructuredViewer newViewer(Composite parent, FormToolkit toolkit) {
		Tree tree;
		if (parent instanceof Tree) {
			tree = (Tree) parent;
		} else {
			tree = toolkit.createTree(parent, getStyle());
		}
		GridData gd = new GridData(GridData.FILL_BOTH);
		Rectangle listRect = tree.getBounds();
		gd.heightHint = tree.getItemHeight () * 27;
		tree.setBounds(listRect);
		tree.setLayoutData(gd);
		tree.setLayout(new WeightedTableLayout(columnWidths, columnWidths, true));
		tree.addListener(SWT.MeasureItem, new Listener() {
    		public void handleEvent (Event event) {
    			event.height = event.gc.getFontMetrics().getHeight() * 3/2;
    		}
    	});
		TreeViewer tv = null;
		if ((style & SWT.CHECK) != 0) {
	    	tv = new CheckboxTreeViewer(tree);
	    } else {
	    	tv = new TreeViewer(tree);
	    }
		fillColumns(tree, BomConstant.ColumnHeaders, BomConstant.ColumnHeaders, columnWidths);
		
//		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(tv) {
//			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
//				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
//						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
//						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
//			}
//		};
//		TreeViewerEditor.create(tv, actSupport,
//				ColumnViewerEditor.TABBING_HORIZONTAL
//						| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
//						| ColumnViewerEditor.TABBING_VERTICAL
//						| ColumnViewerEditor.KEYBOARD_ACTIVATION);
		return tv;
	}

	protected void fillColumns(Tree tree, String[] columns,
			String[] columnsHeader, int[] columnsWidths) {
		if (columns != null) {
			tree.setLinesVisible(true);
			tree.setHeaderVisible(true);

			int totleSize = 0;
			for (int i = 0; i < columns.length; i++) {
				TreeColumn column;
				column = new TreeColumn(tree, SWT.BORDER);
				if (columnsHeader != null) {
					column.setText(columnsHeader[i]);
				} else {
					column.setText(columns[i]);
				}
				totleSize += columnsWidths[i];
				column.setData(columns[i], columns[i]);
				column.setResizable(true);
			}
		}
		tree.pack();
	}

	@Override
	protected String[] getColumns() {
		return BomConstant.ColumnHeaders;
	}
}

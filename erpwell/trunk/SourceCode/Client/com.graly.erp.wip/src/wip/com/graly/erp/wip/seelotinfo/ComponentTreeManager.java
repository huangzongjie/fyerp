package com.graly.erp.wip.seelotinfo;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
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

import com.graly.erp.wip.disassemblelot.LotComponentItemAdapter;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.layout.WeightedTableLayout;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.views.ItemAdapter;
import com.graly.framework.base.ui.views.ItemAdapterFactory;
import com.graly.framework.base.ui.views.TreeViewerManager;
import com.graly.mes.wip.model.Lot;
import com.graly.mes.wip.model.LotComponent;

public class ComponentTreeManager extends TreeViewerManager {
	private static final Logger logger = Logger.getLogger(ComponentTreeManager.class);
	private ADTable adTable;

	public ComponentTreeManager(int style, ADTable adTable) {
		super(style);
		this.adTable = adTable;
	}

	@Override
	protected ItemAdapterFactory createAdapterFactory() {
		ItemAdapterFactory factory = new ItemAdapterFactory();
		try {
			ItemAdapter lotAdapter = new LotItemAdapter();
			factory.registerAdapter(List.class, lotAdapter);
			factory.registerAdapter(Lot.class, lotAdapter);
			factory.registerAdapter(LotComponent.class, new LotComponentItemAdapter<LotComponent>());
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
			tree = toolkit.createTree(parent, SWT.LEFT);
		}
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		Rectangle listRect = tree.getBounds();
		gd.heightHint = tree.getItemHeight() * 18;
		tree.setBounds(listRect);
		tree.setLayoutData(gd);
		tree.setLayout(new WeightedTableLayout(getColumnSize(), getColumnSize(), true));
		tree.addListener(SWT.MeasureItem, new Listener() {
			public void handleEvent(Event event) {
				event.height = event.gc.getFontMetrics().getHeight() * 3 / 2;
			}
		});
		TreeViewer tv = new TreeViewer(tree);
		fillColumns(tree, getColumnsHeader(), getColumnsHeader(), getColumnSize());

		ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(tv) {
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
						|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};
		TreeViewerEditor.create(tv, actSupport, ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
				| ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION);

		return tv;
	}

	protected void fillColumns(Tree tree, String[] columns, String[] columnsHeader, int[] columnsWidths) {
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
		if (getADTable() instanceof ADTable) {
			List<String> columnsList = new ArrayList<String>();
			ADTable table = getADTable();
			for (ADField field : table.getFields()) {
				if (field.getIsDisplay()) {
					columnsList.add(field.getName().toString());
				}
			}
			return columnsList.toArray(new String[] {});
		}
		return new String[] {};
	}

	protected String[] getColumnsHeader() {
		if (getADTable() instanceof ADTable) {
			List<String> columnsHeaderList = new ArrayList<String>();
			ADTable table = getADTable();
			for (ADField field : table.getFields()) {
				if (field.getIsDisplay()) {
					columnsHeaderList.add(I18nUtil.getI18nMessage(field, "label"));
				}
			}
			return columnsHeaderList.toArray(new String[] {});
		}
		return new String[] {};
	}

	protected int[] getColumnSize() {
		if (getADTable() instanceof ADTable) {
			ADTable table = getADTable();
			List<ADField> tableField = (List<ADField>) table.getFields();
			int[] size = new int[tableField.size()];
			int i = 0;
			for (ADField field : table.getFields()) {
				int csize = (field.getDisplayLength() == null ? 32 : field.getDisplayLength().intValue());
				size[i++] = csize;
			}
			return size;
		}
		return null;
	}

	public ADTable getADTable() {
		return adTable;
	}
}

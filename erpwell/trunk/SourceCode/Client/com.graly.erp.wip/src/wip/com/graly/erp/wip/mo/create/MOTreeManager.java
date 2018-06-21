package com.graly.erp.wip.mo.create;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.erp.base.model.DocumentationLine;
import com.graly.erp.pur.model.RequisitionLine;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.ManufactureOrderBom;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.views.AbstractItemAdapter;
import com.graly.framework.base.ui.views.ItemAdapterFactory;
import com.graly.framework.base.ui.views.TreeViewerManager;

public class MOTreeManager extends TreeViewerManager {
	private static final Logger logger = Logger.getLogger(SubMOLineSection.class);
	private  int[] columnWidths = new int[]{100, 150, 68, 68, 72, 70, 72, 70, 63, 63, 63, 52};
	private final String ColumnName_StartDate = "dateStart";
	private final String ColumnName_EndDate = "dateEnd";
	private final String ColumnName_StartTime = "timeStart";
	private final String ColumnName_EndTime = "timeEnd";
	
	protected SubMOLineSection parentSection;
	protected TreeViewer treeViewer;
	protected ADTable table;
	private CellEditor[] cellEditor;
	protected boolean canEdit = true;
	
	public MOTreeManager(ADTable adTable) {
		super();
		this.table = adTable;
	}
	
	public MOTreeManager(ADTable adTable, SubMOLineSection parentSection) {
		this(adTable);
		this.parentSection = parentSection;
		this.canEdit = parentSection.isCanEdit();
	}
	
	@Override
    protected ItemAdapterFactory createAdapterFactory() {
        ItemAdapterFactory factory = new ItemAdapterFactory();
        try{
        	AbstractItemAdapter itemAdapter = new MOLineItemAdapter<Object>();
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
		GridLayout gl = new GridLayout(3, false); //new WeightedTableLayout(columnWidths, columnWidths)
		tree.setLayout(gl);
		treeViewer = new TreeViewer(tree);
		fillColumns(tree);
		
		setCellEditor(treeViewer);		
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
        		if (field.getIsMain()){
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
        		if (field.getIsMain()){
        			columnsHeaderList.add(I18nUtil.getI18nMessage(field, "label"));
        		}
        	}
        	return columnsHeaderList.toArray(new String[]{});
        }
    	return new String[]{};
    }
    
	protected void setCellEditor(TreeViewer treeViewer) {
		int size = this.getColumns().length;
		cellEditor = new CellEditor[size];
		String[] properties = new String[size];
		Tree tree = treeViewer.getTree();
		for(int i = 0; i < size; i++) {
			String column = (String)tree.getColumn(i).getData();
			if(ColumnName_StartDate.equals(column) || ColumnName_EndDate.equals(column)) {
				cellEditor[i] = new CalendarCellEditor(treeViewer);
			} else if(ColumnName_StartTime.equals(column) || ColumnName_EndTime.equals(column)) {
				cellEditor[i] = new TimeCellEditor(treeViewer);
			} else {
				cellEditor[i] = null;
			}
			properties[i] = column;
		}

		treeViewer.setColumnProperties(properties);
		treeViewer.setCellEditors(cellEditor);
		CalendarCellModifier ccm = new CalendarCellModifier(treeViewer);
		treeViewer.setCellModifier(ccm);
	}
    
    private class CalendarCellModifier implements ICellModifier {
		public CalendarCellModifier(TreeViewer treeViewer) {
		}

		@Override
		public boolean canModify(Object element, String property) {
			if(canEdit && element instanceof DocumentationLine) {
				if(element instanceof ManufactureOrderBom) {
					ManufactureOrderBom moBom = (ManufactureOrderBom)element;
					if(moBom.getIsDateNeed()) 
						return true;
				} else {
					DocumentationLine docLine = (DocumentationLine)element;
					if(ManufactureOrder.STATUS_DRAFTED.equals(docLine.getLineStatus()) || ManufactureOrder.STATUS_APPROVED.equals(docLine.getLineStatus())) {
						if(docLine instanceof RequisitionLine) {
							if(ColumnName_StartTime.equals(property) || ColumnName_EndTime.equals(property)) {
								// 为PR时，开始日期和结束日期不能设置
								return false;
							}
						}
						// 如果开始日期或完成日期为null,则对应的开始时间和完成时间不可编辑
						if(ColumnName_StartTime.equals(property) && docLine.getDateStart() == null)
							return false;
						else if(ColumnName_EndTime.equals(property) && docLine.getDateEnd() == null)
							return false;
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public Object getValue(Object element, String property) {
			if(element instanceof DocumentationLine) {
				DocumentationLine doLine = (DocumentationLine)element;				
				Date now = Env.getSysDate();
				if(ColumnName_StartDate.equals(property) || ColumnName_StartTime.equals(property)) {
					if(doLine.getDateStart() == null) return now;
					return doLine.getDateStart();
				} else if(ColumnName_EndDate.equals(property) || ColumnName_EndTime.equals(property)) {
					if(doLine.getDateEnd() == null) return now;
					return doLine.getDateEnd();
				}
			}
			return null;
		}

		@Override
		public void modify(Object element, String property, Object value) {
			try {
				DocumentationLine doline = (DocumentationLine)((TreeItem)element).getData();
				if(value != null) {
					Date date = (Date)value;
					if(ColumnName_StartDate.equals(property)) {
						Date start = doline.getDateStart();
						if(start != null) {
							date.setHours(start.getHours());
							date.setMinutes(start.getMinutes());
							date.setSeconds(start.getSeconds());
						}
						if(doline.getDateEnd() != null) {
							if(date.after(doline.getDateEnd())) {
								return;
							}
						}
						doline.setDateStart(date);
						parentSection.updateGanttEventBy(doline);
					} else if(ColumnName_EndDate.equals(property)) {
						Date end = doline.getDateEnd();
						if(end != null) {
							date.setHours(end.getHours());
							date.setMinutes(end.getMinutes());
							date.setSeconds(end.getSeconds());
						} else {
							date.setHours(17);
							date.setMinutes(30);
							date.setSeconds(0);
						}
						if(doline.getDateStart() != null) {
							if(date.before(doline.getDateStart())) {
								return;
							}							
						}
						doline.setDateEnd(date);
						parentSection.updateGanttEventBy(doline);
					} else if(ColumnName_StartTime.equals(property)) {
						if(isSameDate(doline, date)) {
							// 当开始日期和完成日期相同时，验证开始时间应早于完成时间
							if(!valid(date, doline.getDateEnd())) {
								return;
							}
						}
						if(doline.getDateStart() != null) {
							doline.getDateStart().setHours(date.getHours());
							doline.getDateStart().setMinutes(date.getMinutes());							
						}
					} else if(ColumnName_EndTime.equals(property)) {
						if(isSameDate(doline, date)) {
							if(!valid(doline.getDateStart(), date)) {
								return;
							}
						}												
						if(doline.getDateEnd() != null) {
							doline.getDateEnd().setHours(date.getHours());
							doline.getDateEnd().setMinutes(date.getMinutes());
						}
					}
				} else {
					if(ColumnName_StartDate.equals(property)) {
						doline.setDateStart(null);
					} else if(ColumnName_EndDate.equals(property)) {
						doline.setDateEnd(null);
					}
					parentSection.updateGanttEventBy(doline);
				}
				treeViewer.refresh();
			} catch(Exception e) {
				logger.error("Error at MOTreeManager : CalendarCellModifier.modify() ", e);
			}
		}
		
		public boolean isSameDate(DocumentationLine doline, Date date) {
			if(doline.getDateStart() == null || doline.getDateEnd() == null) return false;
			if(doline.getDateStart().getDate() == doline.getDateEnd().getDate()) {
				return true;
			}
			return false;
		}
		
		public boolean valid(Date start, Date end) {
			// 若start或end为空,则返回true
			if(start == null || end == null) return true;
			if(start.getHours() > end.getHours()) {
				return false;
			} else if(start.getHours() == end.getHours()) {
				if(start.getMinutes() >= end.getMinutes()) {
					return false;					
				}
			}
			return true;
		}
	}
	
	public ADTable getADTable() {
		return table;
	}

	public void getADTable(ADTable table) {
		this.table = table;
	}

	public SubMOLineSection getParentSection() {
		return parentSection;
	}

	public void setParentSection(SubMOLineSection parentSection) {
		this.parentSection = parentSection;
	}

	public int[] getColumnWidths() {
		return columnWidths;
	}

	public void setColumnWidths(int[] columnWidths) {
		this.columnWidths = columnWidths;
	}

}

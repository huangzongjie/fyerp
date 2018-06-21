package com.graly.erp.wip.mo.wms;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;

import com.graly.framework.base.ui.util.PropertyUtil;

public class EntityPropertyCellModify implements ICellModifier {
	private Logger logger = Logger.getLogger(EntityPropertyCellModify.class);

	private TableViewer tableViewer;
	private IEditable editable;

	public EntityPropertyCellModify(TableViewer tableViewer, IEditable editable) {
		this.tableViewer = tableViewer;
		this.editable = editable;
		if(this.editable == null) {
			this.editable = new EntityPropertyEdit(null);
		}
	}

	@Override
	public boolean canModify(Object element, String property) {
		if(editable.isCanEdit(property)) {
			return true;
		}
		return false;
	}

	@Override
	public Object getValue(Object element, String property) {
		if(element != null && property != null) {
			try{
				Object obj = PropertyUtil.getPropertyForString(element, property);
				if(obj == null)
					return "";
				return (String)obj;
			} catch (Exception e){
				logger.error("Error: ", e);
			}
		}
		return null;
	}

	@Override
	public void modify(Object element, String property, Object value) {
		tableViewer.refresh();
	}
}

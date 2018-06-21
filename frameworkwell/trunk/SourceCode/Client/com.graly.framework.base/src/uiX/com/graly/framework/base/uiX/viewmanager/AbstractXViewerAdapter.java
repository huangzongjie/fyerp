package com.graly.framework.base.uiX.viewmanager;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.I18nUtil;

public abstract class AbstractXViewerAdapter implements IXViewerAdapter {
	protected ADTable adTable;
	
	public AbstractXViewerAdapter(ADTable adTable) {
		super();
		this.adTable = adTable;
	}

	@Override
	public ADTable getAdTable() {
		return adTable;
	}

	public void setAdTable(ADTable adTable) {
		this.adTable = adTable;
	}
	
	@Override
	public ColumnData[] getColumnDatas() {
		Assert.isTrue(adTable != null);
		
		if(adTable == null) return new ColumnData[]{};
		
		Assert.isTrue(adTable.getFields() != null);
		if(adTable.getFields() == null) return new ColumnData[]{};
    	List<ColumnData> colDatas = new ArrayList<ColumnData>(20);
    	for (ADField field : adTable.getFields()){
			if (field.getIsMain() && field.getIsDisplay()){
				ColumnData colData = new ColumnData();
				colData.setColumnId(field.getName());
				colData.setColumnLabel(I18nUtil.getI18nMessage(field, "label"));
				colData.setColumnWidth((field.getDisplayLength() == null ? 32 : field.getDisplayLength().intValue()));
				
				colDatas.add(colData);
			}
    	}
		return colDatas.toArray(new ColumnData[]{});
	}
}

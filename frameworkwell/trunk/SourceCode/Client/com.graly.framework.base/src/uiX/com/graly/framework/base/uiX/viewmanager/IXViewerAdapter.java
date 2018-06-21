package com.graly.framework.base.uiX.viewmanager;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;

import com.graly.framework.activeentity.model.ADTable;

public interface IXViewerAdapter extends IStructuredContentProvider,
		ITreeContentProvider, ITableLabelProvider, ITableFontProvider,
		ITableColorProvider, ILabelProvider {
	public ColumnData[] getColumnDatas();
	public ADTable getAdTable();
}

package com.graly.framework.base.uiX.viewmanager;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import com.graly.framework.activeentity.model.ADTable;

public class XTableViewerAdapter extends AbstractXViewerAdapter {
	public XTableViewerAdapter(ADTable adTable) {
		super(adTable);
	}

	@Override
	public Object[] getElements(Object inputElement) {
		Assert.isTrue(inputElement instanceof String);
		
		if(inputElement instanceof String){
			String[] strArray = ((String)inputElement).split("&");
			return strArray;
		}
		return null;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getParent(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		ColumnData[] cDatas = getColumnDatas();
		if(element instanceof String){
			ColumnData cData = cDatas[columnIndex];
			String strElem = ((String)element);
			int start = strElem.indexOf(cData.getColumnId()+"=");
			if(start < 0){
				return "";
			}
			start+=(cData.getColumnId()+"=").length();
			String subStr = strElem.substring(start);
			String colTxt = subStr;
			if(subStr.contains(",")){
				colTxt = subStr.substring(0, subStr.indexOf(',', 0));
			}
			
			return colTxt==null?"":colTxt;
		}
		return "...";
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Font getFont(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getBackground(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getForeground(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Image getImage(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getText(Object element) {
		// TODO Auto-generated method stub
		return null;
	}
}

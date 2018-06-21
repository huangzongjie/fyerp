package com.graly.promisone.base.ui.layout;

import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

public class WeightedTableLayout extends TableLayout {
	private int[] weights;
	private int[] fixedWidths;

	public WeightedTableLayout(int[] weights){
		this(weights, null);
	}
	
	public WeightedTableLayout(int[] weights, int[] fixedWidths) {
		if (weights != null) 
			this.weights = weights;
		else
			this.weights = new int[0];

		if (fixedWidths != null)
			this.fixedWidths = fixedWidths;
		else
			this.fixedWidths = new int[0];

		// check the widths for consistence
		for (int i = 0; i < this.weights.length; i++) {
			int weight = this.weights[i];
			if (weight < 0) {
				if (getFixedWidth(i) < 0)
					throw new IllegalArgumentException("weight and fixedWidth for columnIndex="+i+" are both < 0!!!"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		for (int i = this.weights.length; i < this.fixedWidths.length; i++) {
			int fixedWidth = this.fixedWidths[i];
			if (fixedWidth < 0 && this.weights.length > i) {
				if (this.weights[i] < 0)
					throw new IllegalArgumentException("weight and fixedWidth for columnIndex="+i+" are both < 0!!!"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
	}
	
	protected boolean isFixedWidth(int columnIndex){
		return getFixedWidth(columnIndex) >= 0;
	}

	protected int getFixedWidth(int columnIndex){
		if (columnIndex >= fixedWidths.length)
			return -1;

		return fixedWidths[columnIndex];
	}

	protected int getWeight(int columnIndex){
		if (columnIndex >= weights.length) {
			if (!isFixedWidth(columnIndex))
				return 0;

			return -1;
		}

		return weights[columnIndex];
	}

//	private boolean firstTime = true;	
	@Override
	public void layout(Composite c, boolean flush){
		try{
			int columnCount;
			if (c instanceof Table)
				columnCount = ((Table)c).getColumnCount();
			else if (c instanceof Tree)
				columnCount = ((Tree)c).getColumnCount();
			else
				throw new IllegalArgumentException("Composite c is neither a " + Table.class.getName() + " nor a " + Tree.class.getName()); //$NON-NLS-1$ //$NON-NLS-2$
	
			int width = c.getBounds().width;
			ScrollBar sb = c.getVerticalBar();
			if(sb.isEnabled() && sb.isVisible()) 
				width -= (sb.getSize().x + 2);
			
			int totalWeight = 0;
			int totalFixedWidth = 0;
			for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
				if (isFixedWidth(columnIndex))
					totalFixedWidth += getFixedWidth(columnIndex);
				else
					totalWeight += getWeight(columnIndex);
			}
			if (totalWeight == 0)
				totalWeight = 1; // prevent division by 0
	
			int totalDynamicWidth = width - totalFixedWidth;
			if (totalDynamicWidth < 16)
				totalDynamicWidth = 16;
	
			for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
				int columnWidth;
				if (isFixedWidth(columnIndex))
					columnWidth = getFixedWidth(columnIndex);
				else
					columnWidth = (totalDynamicWidth * getWeight(columnIndex) / totalWeight) - (int)(totalDynamicWidth * 0.01);
	
				if (c instanceof Table) {
					if (columnWidth != ((Table)c).getColumn(columnIndex).getWidth()) {
						((Table)c).getColumn(columnIndex).setWidth(columnWidth);
					}
				} else if (c instanceof Tree) {
					//columnWidth -= 10;
					((Tree)c).getColumn(columnIndex).setWidth(columnWidth);
				}
				else
					throw new IllegalArgumentException("Composite c is neither a " + Table.class.getName() + " nor a " + Tree.class.getName()); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

package com.graly.mes.prd.designer.common.util;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Text;


public class CellEditorLocator implements org.eclipse.gef.tools.CellEditorLocator {
	
	private Label label;
	
	public CellEditorLocator(Label label) {
		this.label = label;
	}
	
	private Viewport getViewPort() {
		Viewport result = null;
		IFigure parent = label.getParent();
		while (parent != null && !(parent instanceof Viewport)) {
			parent = parent.getParent();
		}
		if (parent != null) {
			result = (Viewport)parent;
		}
		return result;
	}
	
	public Point getViewportOrigin() {
		Point result = new Point(0, 0);
		Viewport viewport = getViewPort();
		if (viewport != null) {
			result = new Point(viewport.getLocation());
		}
		return result;
	}

	public void relocate(CellEditor celleditor) {
		Text text = (Text) celleditor.getControl();
		Point origin = getViewportOrigin();
		Rectangle rect = label.getTextBounds().getCopy().expand(5, 0).translate(origin);
		text.setBounds(rect.x, rect.y, rect.width, rect.height);
	}

}

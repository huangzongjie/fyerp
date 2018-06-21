package org.eclipse.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class QueryButton extends Button {
	public QueryButton(Composite parent, int style) {
		super(parent, style);
	}
	
	public Point computeSize (int wHint, int hHint, boolean changed) {
		checkWidget ();
		int width = 0, height = 0, border = getBorderWidth ();
		
		if (getImage () != null) {
			Rectangle rect = getImage ().getBounds ();
			width = rect.width;
			height = rect.height;
		} else {
			return super.computeSize(wHint, hHint, changed);
		}
		if (wHint != SWT.DEFAULT) width = wHint;
		if (hHint != SWT.DEFAULT) height = hHint;
		width += border * 2; 
		height += border * 2;
		width += 7;  height += 5;
		return new Point (width, height);
	}
}

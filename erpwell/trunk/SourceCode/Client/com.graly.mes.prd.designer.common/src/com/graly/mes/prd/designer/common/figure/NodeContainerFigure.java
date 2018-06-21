package com.graly.mes.prd.designer.common.figure;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.StackLayout;

public class NodeContainerFigure extends AbstractNodeFigure {

	private FixedAnchor selfReferencingAnchor;
	private IFigure pane;

	public NodeContainerFigure() {
		setBorder(new NodeContainerBorder());
		connectionAnchor = new ChopboxAnchor(this);
		selfReferencingAnchor = new FixedAnchor(this);
		ScrollPane scrollpane = new ScrollPane();
		pane = new FreeformLayer();
		pane.setLayoutManager(new FreeformLayout());
		setLayoutManager(new StackLayout());
		add(scrollpane);
		scrollpane.setViewport(new FreeformViewport());
		scrollpane.setContents(pane);
	}

	public ConnectionAnchor getSelfReferencingAnchor() {
		return selfReferencingAnchor;
	}

	public IFigure getContentPane() {
		return pane;
	}

}

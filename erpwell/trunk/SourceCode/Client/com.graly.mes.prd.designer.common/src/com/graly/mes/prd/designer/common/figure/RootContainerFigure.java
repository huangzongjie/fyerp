package com.graly.mes.prd.designer.common.figure;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.LineBorder;

public class RootContainerFigure extends FreeformLayer {
	
	public RootContainerFigure() {
		setLayoutManager(new FreeformLayout());
		setBorder(new LineBorder(1));		
	}

}

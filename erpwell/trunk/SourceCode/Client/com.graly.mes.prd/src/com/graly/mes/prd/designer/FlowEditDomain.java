package com.graly.mes.prd.designer;

import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.swt.events.MouseEvent;

public class FlowEditDomain extends EditDomain {
	
	@Override
	public void mouseUp(MouseEvent mouseEvent, EditPartViewer viewer) {
		super.mouseUp(mouseEvent, viewer);
		if(mouseEvent.button == 3){//用户点击鼠标右键时选中选择工具
			loadDefaultTool();
		}
	}
}

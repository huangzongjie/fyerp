package com.graly.mes.prd.designer;

import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.swt.events.MouseEvent;

public class FlowEditDomain extends EditDomain {
	
	@Override
	public void mouseUp(MouseEvent mouseEvent, EditPartViewer viewer) {
		super.mouseUp(mouseEvent, viewer);
		if(mouseEvent.button == 3){//�û��������Ҽ�ʱѡ��ѡ�񹤾�
			loadDefaultTool();
		}
	}
}

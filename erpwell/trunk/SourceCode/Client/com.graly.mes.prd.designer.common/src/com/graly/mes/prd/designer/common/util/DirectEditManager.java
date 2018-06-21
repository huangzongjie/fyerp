package com.graly.mes.prd.designer.common.util;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.swt.widgets.Text;
import com.graly.mes.prd.designer.common.notation.Label;
import com.graly.mes.prd.designer.common.part.LabelGraphicalEditPart;


public class DirectEditManager extends org.eclipse.gef.tools.DirectEditManager {
	
	public DirectEditManager(GraphicalEditPart source, Class editorType, CellEditorLocator locator) {
		super(source, editorType, locator);
	}

	protected void initCellEditor() {
		Text text = (Text) getCellEditor().getControl();
		String value = ((Label)((LabelGraphicalEditPart)getEditPart()).getNotationElement()).getText();
		getCellEditor().setValue(value);
		text.selectAll();
	}

}

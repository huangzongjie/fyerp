package com.graly.mes.prd.designer.common.editor;

import org.eclipse.ui.IEditorInput;
import com.graly.mes.prd.designer.common.notation.RootContainer;

public interface ContentProvider {
	
	boolean saveToInput(IEditorInput input, RootContainer rootContainer);
	void addNotationInfo(RootContainer rootContainer, IEditorInput input);
	
}

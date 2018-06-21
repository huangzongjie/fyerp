package com.graly.mes.prd.designer;

import org.eclipse.ui.IEditorInput;
import org.w3c.dom.Document;

import com.graly.mes.prd.designer.common.notation.RootContainer;
import com.graly.mes.prd.designer.common.xml.XmlAdapter;

public interface DocumentProvider {
	boolean saveToInput(IEditorInput input, XmlAdapter xmlAdapter);
	Document getDocument(IEditorInput input);
}

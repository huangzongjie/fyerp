package com.graly.mes.prd.designer;

import java.io.BufferedReader;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.w3c.dom.Document;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.core.internal.provisional.document.IEncodedDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.document.DOMModelImpl;
import org.eclipse.wst.xml.core.internal.document.DocumentImpl;
import org.eclipse.wst.xml.core.internal.encoding.XMLDocumentLoader;
import org.eclipse.wst.xml.core.internal.modelhandler.ModelHandlerForXML;

import com.graly.mes.prd.designer.common.Logger;
import com.graly.mes.prd.designer.common.notation.RootContainer;
import com.graly.mes.prd.designer.common.xml.XmlAdapter;

public class DefaultDocumentProvider implements DocumentProvider {
	
	protected static final int DEFAULT_FILE_SIZE= 15 * 1024;
	private static String documentFile = "processdefinition.xml";
	
	@Override
	public boolean saveToInput(IEditorInput element, XmlAdapter xmlAdapter){
		boolean result = true;
		try {
			FlowEditorInput input = (FlowEditorInput)element;
			DocumentImpl doc = (DocumentImpl)xmlAdapter.getNode().getOwnerDocument();
			if (doc != null) {
				input.setFlowDocument(doc.getModel().getStructuredDocument().getText());
			} else {
				throw new RuntimeException("Document can not be found!");
			}
		} catch (Exception e) {
			result = false; 
			Logger.logError("Problem while saving the input.", e);
			throw new RuntimeException(e);
		}
		return result;
	}
	
	@Override
	public Document getDocument(IEditorInput element){
		try{
			FlowEditorInput input = (FlowEditorInput)element;
			InputStream inputStream = new ByteArrayInputStream(input.getFlowDocument().getBytes());
			if(inputStream.available() == 0) inputStream = null;
			if (inputStream == null) {
				inputStream = createInitialProcessDefinition(input.getName());
			}

			DOMModelImpl domModel = new DOMModelImpl();
			domModel.setId(documentFile);
			domModel.setContentTypeIdentifier("org.eclipse.core.runtime.xml");
			
			XMLDocumentLoader loader = new XMLDocumentLoader();
			IEncodedDocument document = loader.createNewStructuredDocument();
			loader.reload(document, new java.io.InputStreamReader(inputStream));
			ModelHandlerForXML modelHandler = new ModelHandlerForXML();
			domModel.setModelHandler(modelHandler);
			domModel.setStructuredDocument((IStructuredDocument)document);
    	    domModel.getFactoryRegistry().addFactory(new org.eclipse.wst.xml.core.internal.modelquery.ModelQueryAdapterFactoryForXML());
    	    domModel.getFactoryRegistry().addFactory(new org.eclipse.wst.xml.core.internal.propagate.PropagatingAdapterFactoryImpl());

			return domModel.getDocument();
		} catch (Exception e){
			Logger.logError("Problem adding create document", e);
			throw new RuntimeException(e);
		}
	}
	
	private InputStream createInitialProcessDefinition(String processName) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append("<process-definition name=\"" + processName + "\" xmlns=\"urn:jbpm.org:jpdl-3.2\"></process-definition>");	
		return new ByteArrayInputStream(buffer.toString().getBytes());
	}
}

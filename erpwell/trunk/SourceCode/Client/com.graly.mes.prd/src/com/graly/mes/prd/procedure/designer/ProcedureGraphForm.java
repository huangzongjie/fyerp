package com.graly.mes.prd.procedure.designer;

import java.util.List;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.mes.prd.designer.common.editor.ContentProvider;
import com.graly.mes.prd.designer.common.model.SemanticElement;
import com.graly.mes.prd.designer.common.notation.Node;
import com.graly.mes.prd.designer.model.EndState;
import com.graly.mes.prd.designer.model.StartState;
import com.graly.mes.prd.designer.model.StepState;
import com.graly.mes.prd.designer.part.JpdlGraphicalEditPartFactory;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.UI;
import com.graly.mes.prd.designer.AbstractGraphForm;
import com.graly.mes.prd.designer.AbstractGraphicalViewer;
import com.graly.mes.prd.designer.DefaultContentProvider;
import com.graly.mes.prd.designer.DefaultDocumentProvider;
import com.graly.mes.prd.designer.DocumentProvider;

public class ProcedureGraphForm extends AbstractGraphForm {

	private static String contributorId = "com.graly.mes.prd.designer.procedure";
	private ProcedureDialog dialog;

	
	public ProcedureGraphForm(ProcedureDialog dialog, FormToolkit toolkit, Composite parent, IEditorInput input) {
		super(toolkit, parent, input);
		this.dialog = dialog;
	}
	
	@Override
	protected String createTitle() {
		return "";
	}
	
	@Override
	protected ContentProvider createContentProvider() {
		return new DefaultContentProvider();
	}

	@Override
	protected DocumentProvider createDocumentProvider() {
		return new DefaultDocumentProvider();
	}

	@Override
	protected AbstractGraphicalViewer createGraphicalViewer() {
		return new AbstractGraphicalViewer(this) {
			protected void initEditPartFactory() {
				setEditPartFactory(new JpdlGraphicalEditPartFactory());
			}			
		};
	}

	@Override
	protected SemanticElement createMainElement() {
		return getSemanticElementFactory().createById("ProcessDefinition");
	}
	
	@Override
	protected String getContributorId() {
		return contributorId;
	}
	
	@Override
	protected SelectionListener getSaveListener() {
		SelectionListener saveListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event){
				if(validate()){
					doSave();
				} else {
					UI.showError(Message.getString("error.flow_save_error"));
				}
			}
		};
		return saveListener;
	}

	@Override
	protected SelectionListener getCancelListener() {
		SelectionListener cancelListener = new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				dialog.close();//µ÷ÓÃProcessDialogµÄclose()
			}				
		};
		return cancelListener;
	}
	
	@Override
	public boolean doSave() {
		if (super.doSave()){
			dialog.doSave(getEditorInput());
			return true;
		} 
		return false;
	}
	
	private boolean validate(){
		List<Node> nodes = getRootContainer().getNodes();
		boolean validateFlag = true;
		boolean flag1 = false, flag2 = false, flag3 = false;
		
		for(Node element : nodes){
			SemanticElement node = element.getSemanticElement();
			if(node instanceof StartState) {
				if(element.getLeavingEdges().size() != 0){
					flag1 = true;
				} else {
					flag1 = false;
				}
			}
			if(node instanceof EndState) {
				if(element.getArrivingEdges().size() != 0){
					flag2 = true;
				} else {
					flag2 = false;
				}
			}
			if(node instanceof StepState) {
				if(element.getArrivingEdges().size()!= 0 && element.getLeavingEdges().size() != 0){
					flag3 = true;
				} else {
					flag3 = false;
				}
			}
			validateFlag = flag1 && flag2 && flag3;			
		}
		
		return validateFlag;
	}

}

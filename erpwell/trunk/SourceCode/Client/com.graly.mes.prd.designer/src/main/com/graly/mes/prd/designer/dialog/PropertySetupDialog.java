package com.graly.mes.prd.designer.dialog;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADRefTable;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.custom.XCombo;
import com.graly.framework.base.ui.forms.DialogForm;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.RefTableField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.mes.prd.designer.command.NodeCreateCommand;
import com.graly.mes.prd.designer.common.notation.Node;
import com.graly.mes.prd.designer.model.AbstractNode;
import com.graly.mes.prd.designer.model.ProcedureState;
import com.graly.mes.prd.designer.model.ProcessDefinition;
import com.graly.mes.prd.designer.model.StepState;
import com.graly.framework.runtime.Framework;

public class PropertySetupDialog extends InClosableTitleAreaDialog{
	private static final Logger logger = Logger.getLogger(PropertySetupDialog.class);
    private String value = "";//$NON-NLS-1$
    private AvailableFlowForm form;

    private NodeCreateCommand command;
	private String dialogTitle;
	private String dialogMessage;
	private Node model = null;
	
	private final String ADFIELD_NAME = "StepNode";
	private final String DISPLAY_TYPE = "reftable";
	private final String ADFIELD_LABEL = Message.getString("wip.step");
	
	public PropertySetupDialog(Shell parentShell, String dialogTitle, String dialogMessage, String initialValue,
			IInputValidator validator,Node model){
		super(parentShell);
		this.dialogTitle = dialogTitle;
        this.dialogMessage = dialogMessage;
        this.model = model;
        if (initialValue == null) {
			value = "";//$NON-NLS-1$
		} else {
			value = initialValue;
		}
	}
    public PropertySetupDialog(Shell parentShell, String dialogTitle,
            String dialogMessage, String initialValue,
            IInputValidator validator,NodeCreateCommand command) {
        super(parentShell);
        this.command = command;       
        this.dialogTitle = dialogTitle;
        this.dialogMessage = dialogMessage;
        
        if (initialValue == null) {
			value = "";//$NON-NLS-1$
		} else {
			value = initialValue;
		}

    }

    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
        	
            int runner = 0;
            
            value = form.getValue();
            if(value == null || "".equals(value.trim())) {
            	return;
            }
    		String prefix = value; 

			while(runner < 1000) {
    			String candidate = runner == 0 ? prefix : prefix + "_" + runner;
    			AbstractNode findNode =(AbstractNode) ((ProcessDefinition)model.getContainer().getSemanticElement()).getNodeElementByName(candidate);
    			if (findNode == null || findNode.equals( model.getSemanticElement()/*当前双击的节点*/)) {
    				if(model.getSemanticElement() instanceof ProcedureState){
	    				ProcedureState procedureState = (ProcedureState)model.getSemanticElement();
	    				procedureState.setName(candidate);
	    				procedureState.getProcedure().setName(prefix);
    				} else if(model.getSemanticElement() instanceof StepState){
    					StepState stepState = (StepState)model.getSemanticElement();
    					stepState.setName(candidate);
    					stepState.getStep().setName(prefix);
    				}
    				break;
    			}
    			runner ++;
    		}
        } else {
            value = null;
        }
        super.buttonPressed(buttonId);
    }

	protected Control createDialogArea(Composite parent) {
        // create composite
        parent.getShell().setActive();
        Composite dlgArea = (Composite) super.createDialogArea(parent);

        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        gd.horizontalSpan = ((GridLayout) dlgArea.getLayout()).numColumns;

        getShell().setText(Message.getString("common.createnode"));
        setTitle(dialogTitle);
        setMessage(dialogMessage);

        // create the form.
        Composite panel = new Composite(dlgArea, SWT.NONE);
        panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        panel.setLayout(new FillLayout());

		form = createFlowForm(panel);
       
        applyDialogFont(panel);
        return dlgArea;
    }
	
	private AvailableFlowForm createFlowForm(Composite parent){
		ADField adField = new ADField();
		
		adField.setName(ADFIELD_NAME);
		adField.setDisplayType(DISPLAY_TYPE);
		adField.setIsMandatory(true);
		return new AvailableFlowForm(parent, SWT.NONE, "", adField);		
	}
	
	private class AvailableFlowForm extends DialogForm{
		private ADField adField;
		private final String WHERE_CLAUSE = "STATUS = 'Active'";
		
		public AvailableFlowForm(Composite parent, int style, Object object, ADField adField) {
			super(parent, style, object);
			this.adField = adField;			
			createForm();
		}

		@Override
		public void createForm() {
	        addFields();
	        if (object != null) {
	            loadFromObject();
	        }
	        setDefaultSelected(model);
	        createContent();
		}

		@Override
		public void addFields() {
			IField field = getField(adField);
    		if (field == null) {
				return;
			}
			field.setADField(adField);			
		}
		
		public IField getField(ADField adField){
			IField field = null;
			if(ADFIELD_NAME.equals(adField.getName())
					&& DISPLAY_TYPE.equals(adField.getDisplayType())) {
				ADTable adTable = null;
				ADRefTable refTable = new ADRefTable();
				refTable.setKeyField("name");
				refTable.setValueField("name");
				
				try {
					ADManager adManager = Framework.getService(ADManager.class);			
					if(model.getSemanticElement() instanceof ProcedureState){
						adTable = adManager.getADTable(0L, "Procedure");
					} else if(model.getSemanticElement() instanceof StepState){
						adTable = adManager.getADTable(0L, "Step");
					}
					TableListManager tableManager = new TableListManager(adTable);
					FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
					TableViewer viewer = (TableViewer)tableManager.createViewer(getShell(),
							toolkit);
					List<ADBase> list = adManager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(),
							Env.getMaxResult(), WHERE_CLAUSE, "");
					viewer.setInput(list);
					
					int mStyle = SWT.READ_ONLY;
					field = new RefTableField("availableform", viewer, refTable, mStyle);
					field.setLabel(ADFIELD_LABEL);
					addField(ADFIELD_NAME, field);
					return field;
				} catch(Exception en) {
					logger.error("Error at ProertySetupDialog : createFlowForm() " + en.getMessage());
				}
			}
			return field;
		}

		@Override
		public boolean saveToObject() {
    		return true;
		}
		
		public String getValue(){
			RefTableField f = (RefTableField) fields.get(adField.getName());
			XCombo combo = f.getComboControl();
			String value = combo.getText();
			return value;
		}
		
		public void setDefaultSelected(Node model){
			RefTableField f = (RefTableField) fields.get(adField.getName());
			if(model != null){
				String key = "";
				if(model.getSemanticElement() instanceof ProcedureState){					
					key = ((ProcedureState)model.getSemanticElement()).getProcedure().getName();
				} else if(model.getSemanticElement() instanceof StepState){
					key = ((StepState)model.getSemanticElement()).getStep().getName();
				}
				f.setValue(key);
			}
		}
	}
	
}

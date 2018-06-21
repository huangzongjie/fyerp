package com.graly.framework.base.entitymanager.dialog;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;

public class MasterSectionDialog extends InClosableTitleAreaDialog {
	
	private static final Logger logger = Logger.getLogger(MasterSectionDialog.class);
	private static int MIN_DIALOG_WIDTH = 700;
	private static int MIN_DIALOG_HEIGHT = 500;
	
	protected ADTable table;
	protected MasterSection section;
	protected ManagedForm managedForm;
	
	
	public MasterSectionDialog(Shell parent) {
        super(parent);
    }
	
	public MasterSectionDialog(Shell parent, ADTable table){
		this(parent);
		this.table = table;
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
        setTitleImage(SWTResourceCache.getImage("entity-dialog"));
        try{
			String dialogTitle = String.format(Message.getString("common.editor"), I18nUtil.getI18nMessage(table, "label"));
			setTitle(dialogTitle);
		} catch (Exception e){
		}
        Composite composite = (Composite) super.createDialogArea(parent);
        
        createFormContent(composite);
		fireSelectionChanged(managedForm);
        return composite;
    }
	
	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginTop = 0;
		layout.marginBottom = 0;
		body.setLayout(layout);
		body.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	protected void createFormContent(Composite composite) {
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		managedForm = new ManagedForm(toolkit, sForm);
		
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		
		createSection(table);
		section.createContents(managedForm, body);    
	}
	
	protected void createSection(ADTable adTable) {
		section = new MasterSection(new EntityTableManager(adTable));
	}
	
	protected void fireSelectionChanged(IManagedForm managedForm){
		try{
			EntityTableManager tableManager = section.getTableManager();
			ADTable table = tableManager.getADTable();
			Object obj = Class.forName(table.getModelClass()).newInstance();
			
			// 实现选中列表中第一行
			if(((TableViewer)section.getViewer()).getTable().getItemCount() > 0) {
				TableItem it = ((TableViewer)section.getViewer()).getTable().getItem(0);
				if(it.getData() instanceof ADBase) {
					obj = it.getData();
				}
			}
			if (obj instanceof ADBase) {
				((ADBase)obj).setOrgRrn(Env.getOrgRrn());
			}
			for(IFormPart part : managedForm.getParts()){
				if (part instanceof SectionPart) {
					managedForm.fireSelectionChanged(part, new StructuredSelection(new Object[] {obj}));
				}
			}
		} catch (Exception e){
			logger.error("fireSelectionChanged error: ", e);
		}
	}
	
	@Override
    protected void okPressed() {
		super.okPressed();
    }
	
	@Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.CANCEL_ID,
               Message.getString("common.exit"), false);
    }
	
	protected String validate() {
		return null;
	}
	
	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
						shellSize.y));
	}
	
	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}
}

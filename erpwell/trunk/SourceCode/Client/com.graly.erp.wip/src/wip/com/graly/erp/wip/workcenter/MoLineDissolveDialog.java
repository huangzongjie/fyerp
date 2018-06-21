package com.graly.erp.wip.workcenter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.ppm.model.Mps;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;


public class MoLineDissolveDialog extends InClosableTitleAreaDialog {

	protected ADTable adTable;
	public static final String AD_TABLE_NAME_MOLINE = "WIPManufactureOrderLine";
	private static int MIN_DIALOG_WIDTH = 640;
	private static int MIN_DIALOG_HEIGHT = 400;
	protected EntityTableManager tableManager;
	protected TableViewer viewer;
	protected List<ManufactureOrderLine> input;
	private ManufactureOrderLine moLine;
	protected String whereClause;
	protected Mps mps;
	ADManager adManager;
	protected String DESCRIPTION="description";
	protected String DATESTAR="dateStart";
	protected String DATEEND="dateEnd";
	MoLineCombineBaseInfoForm baseForm;
	protected ADTable formAdTable;
	protected Object[] elements;
	
	public MoLineDissolveDialog(Shell parentShell,ManufactureOrderLine moLine,List<ManufactureOrderLine> list) {
		super(parentShell);
		this.moLine=moLine;
		this.input=list;
	}

	@Override
    protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("bomtitle"));
		getADTableOfMoLine();
        setTitle(String.format(Message.getString("common.editor"),
        		I18nUtil.getI18nMessage(adTable, "label")));

        Composite comp = (Composite)super.createDialogArea(parent);
        FormToolkit toolkit = new FormToolkit(comp.getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(comp);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		createBasicInfoFrom(body,toolkit);
		createTableViewer(body, toolkit);
		//getMoLineCombine();
		return body;
	}
	
	
	/*
	 * 取到动态对象
	 */
	protected ADTable getADTableOfMoLine() {
		try {
			if(adTable == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTable = entityManager.getADTable(0L, AD_TABLE_NAME_MOLINE);
			}
			return adTable;
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return null;
	}
	
	protected void createBasicInfoFrom(Composite body, FormToolkit toolkit) {
		Composite com = toolkit.createComposite(body, SWT.NONE);
		com.setLayout(new GridLayout());
		com.setLayoutData(new GridData(GridData.FILL_BOTH));
		 baseForm = new MoLineCombineBaseInfoForm(com, SWT.NONE,moLine);
		}
	
	protected void createTableViewer(Composite client, FormToolkit toolkit) {
		
		tableManager = new EntityTableManager(adTable,SWT.NONE);
		viewer = (TableViewer) tableManager.createViewer(client, toolkit);
		viewer.setInput(input);
		tableManager.updateView(viewer);
	}
	
	protected void refresh() {
		tableManager.setInput(getInput());
		tableManager.updateView(viewer);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createDissolveButton(parent);
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.exit"), false);
		}
	
	protected void createDissolveButton(Composite parent){
		
		((GridLayout) parent.getLayout()).numColumns++;
		Button button = new Button(parent, SWT.PUSH);
		button.setText("撤销合并");
		button.setFont(JFaceResources.getDialogFont());
		button.setData(new Integer(IDialogConstants.CANCEL_ID));
		button.setEnabled(false);

		if(viewer != null && viewer.getElementAt(0) != null)
			button.setEnabled(true);
		else return;
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					if(viewer != null){
						Object elements= viewer.getInput();
						if(elements != null && elements instanceof ArrayList){
							ArrayList<ManufactureOrderLine> mos = (ArrayList<ManufactureOrderLine>)elements;
							if(mos.size()>0){
								List<ManufactureOrderLine> orderLines=new ArrayList<ManufactureOrderLine>();
								for(Object obj:mos){
								ManufactureOrderLine line=(ManufactureOrderLine)obj;
								orderLines.add(line);
								}
								SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
								moLine.setDescription(getIField(DESCRIPTION).getValue().toString());
								//moLine.setDateEnd(dateFormat.parse(getIField(DATEEND).getValue().toString()));
								//moLine.setDateStart(dateFormat.parse(getIField(DATESTAR).getValue().toString()));
								
								WipManager wipManager=Framework.getService(WipManager.class);
								wipManager.dissolveMoLines(moLine, orderLines, Env.getUserRrn());
								UI.showInfo("撤销合并成功！");
								close();
							}else{
								UI.showError("无合并项，不能乱撤！！！");
								return;
							}
						}
					}
				} catch (ClientException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
    
	protected IField getIField(String fieldId){
		formAdTable=baseForm.getFormAdTable();
		for(ADField adField:formAdTable.getFields()){
			IField field=baseForm.getFields().get(fieldId);
			if(field!=null){
				return field;
			}
		}
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

	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout(1, true);
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
	
	public List<ManufactureOrderLine> getInput() {
		return input;
	}
	
	public void setInput(List<ManufactureOrderLine> input) {
		this.input = input;
	}
}

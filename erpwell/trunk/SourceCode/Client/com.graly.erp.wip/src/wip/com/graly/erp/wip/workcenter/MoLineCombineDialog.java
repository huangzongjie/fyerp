package com.graly.erp.wip.workcenter;

import java.awt.TextArea;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.birt.core.i18n.ResourceConstants;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.ppm.model.Mps;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.erp.wip.model.MaterialUsed;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.ExtendDialog;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.editor.EntityTableManager;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.TextField;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;
import com.ibm.icu.text.SimpleDateFormat;

public class MoLineCombineDialog extends InClosableTitleAreaDialog{

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
	protected String qty;
	protected BigDecimal addQty;
	private ManufactureOrderLine addMoLine;
	protected String text;
	protected Text text1;
	protected AddQtyDialog dialog;
	protected Label label;
	protected Material material;
	protected BigDecimal sum = BigDecimal.ZERO;
	protected BigDecimal qtyCheck = BigDecimal.ZERO;
	protected BigDecimal qtyViewer = BigDecimal.ZERO;
	protected String combineInfoFormat = "当前可合并生产数 :%1$s个，已选择合并:%2$s个，合并后生产总数:%3$s个。";
	protected String combineInfo =null;
	protected Button btnSelectAll;
	protected Button btnInvertAll;
	protected CheckboxTableViewer tViewer;
	
	public MoLineCombineDialog(Shell parentShell,ManufactureOrderLine moLine,List<ManufactureOrderLine> list) {
		super(parentShell);
		this.moLine=moLine;
		this.input=list;
		this.material= moLine.getMaterial();
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
		
		Composite top = toolkit.createComposite(body);
		GridLayout topLayout = new GridLayout();
		topLayout.numColumns = 1;
		top.setLayout(topLayout);
		top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createBasicInfoFrom(top,toolkit);
		
		Composite mid = toolkit.createComposite(body);
		GridLayout midLayout = new GridLayout();
		midLayout.marginRight = 10;
		midLayout.numColumns = 2;
		mid.setLayout(midLayout);
		mid.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		BigDecimal sum = (moLine.getQty().subtract(moLine.getQtyReceive())).add(qtyCheck);
		label = toolkit.createLabel(mid, "");
		createAddQtyButton(mid);
		Composite tableTopButtonBar = toolkit.createComposite(body);
		GridLayout tableTopBarLayout = new GridLayout();
		tableTopBarLayout.numColumns = 2;
		tableTopButtonBar.setLayout(midLayout);
		tableTopButtonBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createSelectButton(tableTopButtonBar);
		createTableViewer(body, toolkit);
		combineInfo = String.format(combineInfoFormat, qtyViewer.toString(), qtyCheck.toString(), sum.toString());
		label.setText(combineInfo);
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
		
		tableManager = new EntityTableManager(adTable,SWT.CHECK);
		viewer = (TableViewer) tableManager.createViewer(client, toolkit);
		createViewerAction((CheckboxTableViewer)viewer);
		viewer.setInput(input);
		Table table = viewer.getTable();
		
		for (int i = 0; i < table.getItemCount(); i++) {
			TableItem item = table.getItem(i);
			Object obj = item.getData();
			if (obj instanceof ManufactureOrderLine) {
				ManufactureOrderLine o = (ManufactureOrderLine)obj;
				if (o.getQty() != null) {
					qtyViewer = qtyViewer.add(o.getQty().subtract(o.getQtyReceive()));
				}}
		}
		tableManager.updateView(viewer);
	}
	
	protected void refresh() {
		tableManager.setInput(getInput());
		tableManager.updateView(viewer);
	}
	
	private void createViewerAction(final CheckboxTableViewer viewer) {
		viewer.addCheckStateListener(new ICheckStateListener(){
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				qtyCheck = BigDecimal.ZERO;
				Object[] objs = viewer.getCheckedElements();
				for(int i=0; i< objs.length; i++){
					Object obj = objs[i];
					if(obj instanceof ManufactureOrderLine){
						ManufactureOrderLine b = (ManufactureOrderLine)obj;
						if (b.getQty() != null) {
							qtyCheck = qtyCheck.add(b.getQty().subtract(b.getQtyReceive()));
						}
					}
				}
				BigDecimal sum = (moLine.getQty().subtract(moLine.getQtyReceive())).add(qtyCheck);
				combineInfo = String.format(combineInfoFormat, qtyViewer.toString(), qtyCheck.toString(), sum.toString());
				label.setText(combineInfo);
				label.getParent().layout();
			}
		});
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createCombineButton(parent);
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.exit"), false);
		}
	
	protected void createSelectButton(Composite parent ){
		Button btnSelectAll = new Button(parent, SWT.PUSH);
		btnSelectAll.setText("全选");
		btnSelectAll.setFont(JFaceResources.getDialogFont());
		btnSelectAll.setData(new Integer(IDialogConstants.CANCEL_ID));
		btnSelectAll.setEnabled(true);
	    btnSelectAll.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (viewer instanceof CheckboxTableViewer) {
					tViewer = (CheckboxTableViewer) viewer;
					tViewer.setAllChecked(true);
					qtyCheck = BigDecimal.ZERO;
					Object[] objs = tViewer.getCheckedElements();
					for(int i=0; i< objs.length; i++){
						Object obj = objs[i];
						if(obj instanceof ManufactureOrderLine){
							ManufactureOrderLine b = (ManufactureOrderLine)obj;
							if (b.getQty() != null) {
								qtyCheck = qtyCheck.add(b.getQty().subtract(b.getQtyReceive()));
							}
						}
					}
					BigDecimal sum = (moLine.getQty().subtract(moLine.getQtyReceive())).add(qtyCheck);
					combineInfo = String.format(combineInfoFormat, qtyViewer.toString(), qtyCheck.toString(), sum.toString());
					label.setText(combineInfo);
					label.getParent().layout();
				}
			}
			
		});

		Button btnInvertAll = new Button(parent, SWT.PUSH);
		btnInvertAll.setText("反选");
		btnInvertAll.setFont(JFaceResources.getDialogFont());
		btnInvertAll.setData(new Integer(IDialogConstants.CANCEL_ID));
		btnInvertAll.setEnabled(true);
	    btnInvertAll.addSelectionListener(new SelectionListener(){
	    	
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (viewer instanceof CheckboxTableViewer) {
				tViewer = (CheckboxTableViewer) viewer;
				Object[] checkedObjects = tViewer.getCheckedElements();
				tViewer.setAllChecked(true);
				for(Object o : checkedObjects){
					tViewer.setChecked(o, false);
				}
				qtyCheck = BigDecimal.ZERO;
				Object[] objs = tViewer.getCheckedElements();
				for(int i=0; i< objs.length; i++){
					Object obj = objs[i];
					if(obj instanceof ManufactureOrderLine){
						ManufactureOrderLine b = (ManufactureOrderLine)obj;
						if (b.getQty() != null) {
							qtyCheck = qtyCheck.add(b.getQty().subtract(b.getQtyReceive()));
						}
					}
				}
				BigDecimal sum = (moLine.getQty().subtract(moLine.getQtyReceive())).add(qtyCheck);
				combineInfo = String.format(combineInfoFormat, qtyViewer.toString(), qtyCheck.toString(), sum.toString());
				label.setText(combineInfo);
				label.getParent().layout();
			}
		}
		
	});
}
		
	protected void createAddQtyButton(Composite parent){
		Button button = new Button(parent, SWT.PUSH);
		button.setText("添加新工作令");
		button.setFont(JFaceResources.getDialogFont());
		button.setData(new Integer(IDialogConstants.CANCEL_ID));
		button.setEnabled(true);
		button.addSelectionListener(new SelectionAdapter() {
	    public void widgetSelected(SelectionEvent event) {
		AddQtyDialog dialog = new AddQtyDialog();
		dialog.open();
		}
		});
	}
	
	protected void createCombineButton(Composite parent){
		((GridLayout) parent.getLayout()).numColumns++;
		Button button = new Button(parent, SWT.PUSH);
		button.setText(Message.getString("wip.mocombine"));
		button.setFont(JFaceResources.getDialogFont());
		button.setData(new Integer(IDialogConstants.CANCEL_ID));
		button.setEnabled(false);
		
		if(viewer != null && viewer instanceof CheckboxTableViewer){
			CheckboxTableViewer checkboxTableViewer = (CheckboxTableViewer)viewer;
			Object[] elements= checkboxTableViewer.getCheckedElements();
			if(elements!=null){
				button.setEnabled(true);
			}
		}else{
			return;
		}
		
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					if(viewer != null && viewer instanceof CheckboxTableViewer){
					CheckboxTableViewer checkboxTableViewer = (CheckboxTableViewer)viewer;
					Object[] elements = checkboxTableViewer.getCheckedElements();
					if(elements.length>0){
						List<ManufactureOrderLine> orderLines=new ArrayList<ManufactureOrderLine>();
						for(Object obj:elements){
						ManufactureOrderLine line=(ManufactureOrderLine)obj;
						orderLines.add(line);
						}
						SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy",Locale.US);
						moLine.setDescription(getIField(DESCRIPTION).getValue().toString());
						Date end=dateFormat.parse(getIField(DATEEND).getValue().toString());
						moLine.setDateEnd(end);
						Date start=dateFormat.parse(getIField(DATESTAR).getValue().toString());
						moLine.setDateStart(start);
						WipManager wipManager=Framework.getService(WipManager.class);
						wipManager.mergeMoLines(moLine, orderLines, Env.getUserRrn());
						UI.showInfo(Message.getString("wip_merge_successed"));
						close();
						
					}else{
						UI.showError(Message.getString("wip_keli"));
						return;
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
	
	class AddQtyDialog extends ExtendDialog{
		private EntityTableManager tableManager;
		protected EntityForm entityForm;
		
		public AddQtyDialog() {
			super();
		}
		

		@Override
		protected Control createDialogArea(Composite parent) {
			getADTableOfMoLine();
	        setTitleImage(SWTResourceCache.getImage("search-dialog"));
	        setTitle("增加合并工作令");
	        setMessage("请在输入框中填写需要增加的生产数量");
	        Composite comp = (Composite)super.createDialogArea(parent);
	        FormToolkit toolkit = new FormToolkit(comp.getDisplay());
			ScrolledForm sForm = toolkit.createScrolledForm(comp);
			sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
			Composite body = sForm.getForm().getBody();
			configureBody(body);
			Label titleBarSeparator = new Label(body, SWT.HORIZONTAL
					| SWT.SEPARATOR);
			Composite mid = toolkit.createComposite(body);
			GridLayout midLayout = new GridLayout();
			midLayout.numColumns = 2;
			mid.setLayout(midLayout);
			mid.setLayoutData(new GridData(GridData.FILL_BOTH));
			titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			toolkit.createLabel(mid,"增加的生产数量：");
			text1 =toolkit.createText(mid,qty);
			text1.setSize(10, 10);
	        return body;
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
		
		public EntityTableManager getTableManager() {
			return tableManager;
		}

		public void setTableManager(EntityTableManager tableManager) {
			this.tableManager = tableManager;
		}
		
		@Override
		protected Control createButtonBar(Composite parent) {
			Composite bar = new Composite(parent, SWT.BORDER);
			GridLayout gl1 = new GridLayout(2, false);
			gl1.marginHeight = 0;
			gl1.marginWidth = 0;
			bar.setLayout(gl1);
			bar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			Composite temp = new Composite(bar, SWT.NONE);
			GridLayout gl2 = new GridLayout(1, false);
			gl2.marginHeight = 0;
			gl2.marginWidth = 0;
			temp.setLayout(gl2);
			
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.heightHint = 0;
			gd.horizontalSpan = 2;
			temp.setLayoutData(gd);
				
			Composite aqComp = new Composite(bar, SWT.NONE);
			GridLayout layout = new GridLayout(0, false);
			layout.makeColumnsEqualWidth = true;
			aqComp.setLayout(layout);
			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING
					| GridData.VERTICAL_ALIGN_CENTER);
			aqComp.setLayoutData(data);
			aqComp.setFont(parent.getFont());
//			createComponentButtonForButtonBar(aqComp);

			Composite composite = new Composite(bar, SWT.NONE);
			GridLayout l = new GridLayout(0, true);
			l.makeColumnsEqualWidth = true;
			composite.setLayout(l);
			GridData data2 = new GridData(GridData.HORIZONTAL_ALIGN_END);
			data2.horizontalAlignment = GridData.END;
			composite.setLayoutData(data2);
			composite.setFont(parent.getFont());
			createButtonsForButtonBar(composite);
			return bar;
		}
		
		@Override
		protected void createButtonsForButtonBar(Composite parent) {
			createOkButton(parent);			
			createButton(parent, IDialogConstants.CANCEL_ID,
					Message.getString("common.cancel"), false);
		}
		
		protected void createOkButton(Composite parent){
			((GridLayout) parent.getLayout()).numColumns++;
			Button button = new Button(parent, SWT.PUSH);
			button.setText("确认");
			button.setFont(JFaceResources.getDialogFont());
			button.setData(new Integer(IDialogConstants.OK_ID));
			button.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					qty = text1.getText();
					Long a =Long.parseLong(qty);
					addQty = BigDecimal.valueOf(a);
					if(addQty!= null){
						Date now = new Date();
						addMoLine = new ManufactureOrderLine();
						input.add(addMoLine);
						addMoLine.setIsActive(true);
						addMoLine.setCreated(now);
						addMoLine.setMaterial(material);
						addMoLine.setMaterialRrn(moLine.getMaterialRrn());
						addMoLine.setDescription(moLine.getDescription());
						addMoLine.setDateStart(moLine.getDateStart());
						addMoLine.setDateEnd(moLine.getDateEnd());
						addMoLine.setQty(addQty);
						addMoLine.setIsActive(true);
						addMoLine.setLineNo(10L);
						addMoLine.setCreatedBy(Env.getUserRrn());
						addMoLine.setUpdatedBy(Env.getUserRrn());
						addMoLine.setOrgRrn(moLine.getOrgRrn());
						addMoLine.setUpdated(now);
						addMoLine.setDateMerge(now);
						addMoLine.setMaterialName(moLine.getMaterialName());
						addMoLine.setUomId(moLine.getUomId());
						addMoLine.setWorkCenterRrn(moLine.getWorkCenterRrn());
						addMoLine.setLineStatus(ManufactureOrderLine.LINESTATUS_APPROVED);
						}
						qtyViewer = qtyViewer.add(addMoLine.getQty().subtract(addMoLine.getQtyReceive()));
						combineInfo = String.format(combineInfoFormat, qtyViewer.toString(), qtyCheck.toString(), sum.toString());
						label.setText(combineInfo);
					 close();
					 refresh();
				}});
			}
	
	}}



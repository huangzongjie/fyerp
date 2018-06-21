package com.graly.erp.pdm.bomhistory;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import sun.management.counter.Counter;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Material;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.pdm.model.Bom;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.sun.jmx.snmp.Timestamp;

public class BomHisVersionDialog extends InClosableTitleAreaDialog{
	private Material material;
	private TableListManager tableManager; 
	private TableViewer viewer;
	protected ADTable adTable;
	protected List<Bom> input;
	protected Bom bom;
	protected Bom selectBom;
	protected Section section;
	public static final String AD_TABLE_NAME_MOLINE = "PDMBomVersion";
	protected ToolItem itemNow;
	protected ToolItem itemHistory;
	protected IManagedForm form;
	protected Long version;
	PDMManager manager;

	public BomHisVersionDialog(Shell parentShell,Material material,IManagedForm form ) {
		super(parentShell);
		this.material=material;
		this.form=form;
		// TODO Auto-generated constructor stub
	}
	
	@Override
    protected Control createDialogArea(Composite parent) {
		setTitleImage(SWTResourceCache.getImage("bomtitle"));
        setTitle(String.format(Message.getString("common.editor"),
        		I18nUtil.getI18nMessage(adTable, "label")));

        Composite comp = (Composite)super.createDialogArea(parent);
        FormToolkit toolkit = new FormToolkit(comp.getDisplay());
//		ScrolledForm sForm = toolkit.createScrolledForm(comp);
//		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
//		Composite body = sForm.getForm().getBody();
//		configureBody(body);
//		getADTableOfBomHis();
//		createTableViewer(body,toolkit);
        Composite content = toolkit.createComposite(parent, SWT.NULL);
        content.setLayoutData(new GridData(GridData.FILL_BOTH));
        content.setLayout(new GridLayout(1, false));

		section = toolkit.createSection(content, Section.TITLE_BAR);
		section.setText("BOM历史版本");
		section.marginWidth = 2;
		section.marginHeight = 2;
		toolkit.createCompositeSeparator(section);
		
		createToolBar(section);
		
		TableWrapLayout layout = new TableWrapLayout();
		layout.topMargin = 2;
		layout.leftMargin = 2;
		layout.rightMargin = 2;
		layout.bottomMargin = 2;
		content.setLayout(layout);

		section.setLayout(layout);
		TableWrapData td = new TableWrapData(TableWrapData.FILL,
				TableWrapData.FILL);
		td.grabHorizontal = true;
		td.grabVertical = true;
		section.setLayoutData(td);

		Composite client = toolkit.createComposite(section, SWT.NULL);
		GridLayout gridLayout = new GridLayout(1, true);
		gridLayout.numColumns = 1;
		client.setLayout(gridLayout);
		GridData g = new GridData(GridData.FILL_BOTH);
		client.setLayoutData(g);
		
		toolkit.paintBordersFor(section);
		section.setClient(client);
		
		
		
		getADTableOfBomHis();
		createTableViewer(client,toolkit);
		return comp;
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemSelectNow(tBar);
		createToolItemSelectHistory(tBar);
		
		section.setTextClient(tBar);
		
		
	}
	
	protected void createToolItemSelectNow(ToolBar tBar) {
		itemNow = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_EDITBOM_PREVIEW);
		itemNow.setText("查看");
		itemNow.setImage(SWTResourceCache.getImage("preview"));
		//itemNow.setEnabled(false);
		itemNow.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				viewAdapter();
			}
		});
	}
	
	protected void createToolItemSelectHistory(ToolBar tBar) {
		itemHistory = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_EDITBOM_PREVIEW);
		itemHistory.setText("对比");
		itemHistory.setImage(SWTResourceCache.getImage("preview"));
		//itemHistory.setEnabled(false);
		itemHistory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				contrastAdapter();
				
			}
		});
	}
	
	protected void viewAdapter() {
		if(selectBom!=null){
			version=selectBom.getParentVersion();
			BomHisTreeDialog btd = new BomHisTreeDialog(UI.getActiveShell(), form, material, false,version);
			if(btd.open() == Dialog.CANCEL) {
				
			}
		}
	}
	
	protected void contrastAdapter(){
		version=selectBom.getParentVersion();
		BomContrastDialog dtd=new BomContrastDialog(UI.getActiveShell(), material, form,false,version);
		dtd.open();
	}
	
	
	protected ADTable getADTableOfBomHis() {
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
	
	protected void createTableViewer(Composite client, FormToolkit toolkit) {
			tableManager = new  TableListManager(adTable);
			viewer = (TableViewer) tableManager.createViewer(client, toolkit);
			tableManager.setInput(getBomHisVersion(material));
			tableManager.updateView(viewer);
			viewer.addSelectionChangedListener(new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					StructuredSelection ss = (StructuredSelection) event.getSelection();
						selectBom=(Bom)ss.getFirstElement();
						if(selectBom!=null){
							//itemHistory.setEnabled(true);
							//itemNow.setEnabled(true);
						}
				}
			});
	}
	
	
	


	
	protected void refresh() {
		tableManager.setInput(input);
		tableManager.updateView(viewer);
	}
	

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID,
				Message.getString("common.exit"), false);
		}
	
	//获得历史版本
	protected List<Bom> getBomHisVersion(Object object){
		try {
			if(object instanceof Material){
				List<Bom> bomList=new ArrayList<Bom>();
				Material material = (Material)object;
				manager=Framework.getService(PDMManager.class);
				List<Object[]> objList=manager.getBomVersion(material.getObjectRrn());
				for(Object[] objs:objList){
					Bom vBom=new Bom();
					BigDecimal version= (BigDecimal)objs[0];
					vBom.setParentVersion(version.longValue());
					String string= objs[3] == null?"": (String)objs[3];
					vBom.setUserName(string);
					if(objs[1]!=null){
						String updatedStr=objs[1].toString();
						SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
						Date udate=sdf.parse(updatedStr);
						vBom.setUpdated(udate);
					}
					bomList.add(vBom);
				}
				return input=bomList;
			}
		} catch (ClassCastException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(); 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
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

	public List<Bom> getInput() {
		return input;
	}

	public void setInput(List<Bom> input) {
		this.input = input;
	}
	
	
}

	


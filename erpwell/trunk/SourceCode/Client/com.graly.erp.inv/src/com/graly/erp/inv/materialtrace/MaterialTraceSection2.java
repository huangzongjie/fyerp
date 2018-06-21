package com.graly.erp.inv.materialtrace;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.graly.erp.base.model.Material;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MaterialTrace;
import com.graly.erp.inv.model.MaterialTraceDetail;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.MasterSection;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;

public class MaterialTraceSection2 extends MasterSection {
	private Logger logger = Logger.getLogger(MaterialTraceSection2.class);
			
	private final String TRACE_DETAIL_TABLENAME = "MaterialTraceDetail2";
	private IManagedForm	form;
	private MaterialTrace mt;
	private Material material;
	private Text	text;
	private static final String PROPERTY_NAME = "properyName";
	
	private List<Control> children = new ArrayList<Control>();
	private String[] labels1 = new String[]{"采购入库","生产入库","退库","其他入库","销售出库","其他出库","财务入库调整","财务出库调整","手工核销","财务入库调整","财务出库调整"};
	private String[] labels1Data = new String[]{"qtyPin","qtyWin","qtyRin","qtyOin","qtySou","qtyOou","qtyAdIn","qtyAdOu","qtyMwo","qtyAouIn","qtyAouOu"};
	private String[] labels2 = new String[]{"生产接受","生产消耗","营运入库调整","营运出库调整","拆分入(子物料还原)","拆分(父物料拆分)"};
	private String[] labels2Data = new String[]{"qtyMo","qtyConsume","qtyAdIn","qtyAdOu","qtyDisassembleIn","qtyDisassembleOu"};
	private String[] labels3 = new String[]{};
	private String[] labels3Data = new String[]{};

	private Label	footerLabel;

	@Override
	public void createContents(IManagedForm form, Composite parent) {
		this.form = form;
		final FormToolkit toolkit = form.getToolkit();
		section = toolkit.createSection(parent, Section.TITLE_BAR);
		section.setText("物料追踪");
		section.marginWidth = 3;
		section.marginHeight = 4;
		toolkit.createCompositeSeparator(section);

		createToolBar(section);

		TableWrapLayout layout = new TableWrapLayout();
		layout.topMargin = 0;
		layout.leftMargin = 5;
		layout.rightMargin = 2;
		layout.bottomMargin = 0;
		parent.setLayout(layout);

		section.setLayout(layout);
		TableWrapData td = new TableWrapData(TableWrapData.FILL,
				TableWrapData.FILL);
		td.grabHorizontal = true;
		td.grabVertical = true;
		section.setLayoutData(td);

		Composite client = toolkit.createComposite(section);	 

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		client.setLayout(gridLayout);
		client.setLayoutData(new GridData(GridData.FILL_BOTH));

		createSectionTitle(client);
		createSectionContent(client);

		toolkit.paintBordersFor(section);
		section.setClient(client);

	}
	
	@Override
	public void createToolBar(Section section) {
	}

	private void createSectionContent(Composite client) {
		createSearchArea(client);
		FormToolkit toolkit = form.getToolkit();
		CTabFolder tabFolder = new CTabFolder(client, SWT.NONE | SWT.FLAT | SWT.TOP);
		tabFolder.marginHeight = 10;
		tabFolder.marginWidth = 5;
		GridData gd = new GridData(GridData.FILL_BOTH);
		tabFolder.setLayoutData(gd);
		toolkit.adapt(tabFolder, true, true);
		tabFolder.setSelectionBackground(new Color(null,new RGB(122,168,243)));
		tabFolder.setSelectionForeground(new Color(null,new RGB(220,232,252)));
		toolkit.paintBordersFor(tabFolder);
		CTabItem ti = new CTabItem(tabFolder, SWT.BORDER);
		ti.setText("消耗统计");
		Composite grp = new Composite(tabFolder, SWT.NONE);
		ti.setControl(grp);
		grp.setLayout(new GridLayout(1, true));
		grp.setLayoutData(new GridData(GridData.FILL_BOTH));
		createTopArea(grp);
		createDetailArea(grp);
		createFooterArea(grp);
		
		if (tabFolder.getTabList().length > 0) {
			tabFolder.setSelection(0);
		}
	}

	private void createTopArea(Composite grp) {
		Composite topBar = new Composite(grp, SWT.NONE);
		GridLayout layout = new GridLayout(6, false);
		layout.marginTop = 5;
		topBar.setLayout(layout);
		topBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		for(int i = 0; i < labels1.length; i++){
			Label lb = new Label(topBar, SWT.NONE);
			lb.setText(labels1[i]);
			lb.setData(PROPERTY_NAME, labels1Data[i]);
			
			Text txt = new Text(topBar, SWT.BORDER | SWT.READ_ONLY);
			txt.setData(PROPERTY_NAME, labels1Data[i]);
			GridData gd = new GridData(GridData.GRAB_HORIZONTAL | GridData.FILL_HORIZONTAL);
			txt.setLayoutData(gd);
			children.add(txt);
			
			Button detailButton = new Button(topBar, SWT.PUSH);
			detailButton.setText("详细");
			detailButton.addSelectionListener(createDetailButtonListener());
			detailButton.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL));
			detailButton.setData(PROPERTY_NAME, labels1Data[i]);
		}
		
	}

	private void createFooterArea(Composite grp) {
		Composite footerBar = new Composite(grp, SWT.NONE);
		GridLayout layout = new GridLayout(2, true);
		layout.marginTop = 20;
		footerBar.setLayout(layout);
		footerBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		footerLabel = new Label(footerBar, SWT.NONE);
		footerLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		refreshFooterArea();
	}

	private void refreshFooterArea() {
		BigDecimal yy = new BigDecimal(0);
		BigDecimal hx = new BigDecimal(0);

		for(String propName : labels1Data){
			Text txt = getText(propName);
			if(txt != null){
				if(txt.getText() != null && txt.getText().trim().length() > 0 && !txt.getText().trim().equals("-") ){
					BigDecimal val = new BigDecimal(txt.getText());
					if(propName.endsWith("In")){
						yy = yy.add(val);
						hx = hx.add(val);
					}else if(propName.endsWith("Ou")){
						yy = yy.subtract(val);
						hx = hx.subtract(val);
					}
				}
			}
		}
		
		for(String propName : labels2Data){
			Text txt = getText(propName);
			if(txt != null){
				if(txt.getText() != null && txt.getText().trim().length() > 0 && !txt.getText().trim().equals("-")){
					BigDecimal val = new BigDecimal(txt.getText());
					if(propName.endsWith("In")||("qtyMo").equals(propName)){
						yy = yy.add(val);
					}else if(propName.endsWith("Ou")||("qtyConsume").equals(propName)){
						yy = yy.subtract(val);
					}
				}
			}
		}
		
		footerLabel.setText("总计  营运总计：" + yy.toString() + "			核销总计: " + hx.toString());
	}

	private void createSearchArea(Composite client) {
		final FormToolkit toolkit = form.getToolkit();
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = SWT.TOP;
		Composite top = toolkit.createComposite(client);
		top.setLayout(new GridLayout(3, false));
		top.setLayoutData(gd);
		Label label = toolkit.createLabel(top, "物料编号");
		label.setForeground(SWTResourceCache.getColor("Folder"));
		text = toolkit.createText(top, "", SWT.BORDER);
		GridData gLabel = new GridData();
		gLabel.horizontalAlignment = GridData.FILL;
		gLabel.grabExcessHorizontalSpace = true;

		GridData gText = new GridData();
		gText.widthHint = 200;
		text.setLayoutData(gText);
		text.setTextLimit(32);
		text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				Text tMaterialId = ((Text) event.widget);
				tMaterialId.setForeground(SWTResourceCache.getColor("Black"));
				switch (event.keyCode) {
				case SWT.CR:
					String materialId = tMaterialId.getText();
					material = searchMaterial(materialId);
					tMaterialId.selectAll();
					if (material == null) {
						tMaterialId.setForeground(SWTResourceCache.getColor("Red"));
					} else {
						mt = getMaterialTrace(material.getObjectRrn());
					}
					refresh();
					break;
				}
			}

			public Material searchMaterial(String materialId) {
				try {
					PDMManager pdmManager = Framework.getService(PDMManager.class);
					List<Material> ls = pdmManager.getMaterialById(materialId, Env.getOrgRrn());
					if(ls != null && ls.size() > 0){
						return ls.get(0);
					}
					return null;
				} catch (Exception e) {
					ExceptionHandlerManager.asyncHandleException(e);
					return null;
				}
			}
		});

		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				Text tLotId = ((Text) e.widget);
				tLotId.setText(tLotId.getText());
				tLotId.selectAll();
			}
		});
	}
	
	protected MaterialTrace getMaterialTrace(Long materialRrn) {
		try {
			INVManager invManager = Framework.getService(INVManager.class);
			MaterialTrace mt = invManager.traceMaterial(materialRrn, null, null);
			return mt;
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return null;
		}
	}

	private void createDetailArea(Composite client) {
		final FormToolkit toolkit = form.getToolkit();
		GridData gd = new GridData(GridData.FILL_BOTH);
		Composite cnt = toolkit.createComposite(client);
		cnt.setLayout(new GridLayout(2, false));
		cnt.setLayoutData(gd);
		
		Group grp1 = new Group(cnt, SWT.NONE);
		grp1.setText("营运销耗");		
		GridLayout layout = new GridLayout();
	    layout.numColumns = 3;
	    grp1.setLayout(layout);
	    GridData data = new GridData(GridData.FILL_BOTH);
	    grp1.setLayoutData(data);
	    createGroup1ChildWidgets(grp1);
		
//		Group grp2 = new Group(cnt, SWT.NONE);
//		grp2.setText("财务销耗");
//		GridLayout layout2 = new GridLayout();
//	    layout2.numColumns = 3;
//	    grp2.setLayout(layout2);
//	    GridData data2 = new GridData(GridData.FILL_BOTH);
//		grp2.setLayoutData(data2);
//		createGroup2ChildWidgets(grp2);
	}

	private void createGroup1ChildWidgets(Group grp1) {
		for(int i = 0; i < labels2.length; i++){
			Label lb = new Label(grp1, SWT.NONE);
			lb.setText(labels2[i]);
			lb.setData(PROPERTY_NAME, labels2Data[i]);
			
			Text txt = new Text(grp1, SWT.BORDER | SWT.READ_ONLY);
			txt.setData(PROPERTY_NAME, labels2Data[i]);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			txt.setLayoutData(gd);
			children.add(txt);
			
			Button detailButton = new Button(grp1, SWT.PUSH);
			detailButton.setText("详细");
			detailButton.addSelectionListener(createDetailButtonListener());
			detailButton.setData(PROPERTY_NAME, labels2Data[i]);
		}
	}
	
	private void createGroup2ChildWidgets(Group grp2) {
		for(int i = 0; i < labels3.length; i++){
			Label lb = new Label(grp2, SWT.NONE);
			lb.setText(labels3[i]);
			lb.setData(PROPERTY_NAME, labels3Data[i]);
			
			Text txt = new Text(grp2, SWT.BORDER | SWT.READ_ONLY);
			txt.setData(PROPERTY_NAME, labels3Data[i]);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			txt.setLayoutData(gd);
			children.add(txt);
			
			Button detailButton = new Button(grp2, SWT.PUSH);
			detailButton.setText("详细");
			detailButton.addSelectionListener(createDetailButtonListener());
			detailButton.setData(PROPERTY_NAME, labels3Data[i]);
			detailButton.addSelectionListener(createDetailButtonListener());
		}
	}
	
	private SelectionListener createDetailButtonListener() {
		return new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				Button btn = (Button) e.widget;
				String property = (String) btn.getData(PROPERTY_NAME);
				ADTable adTable = getADTableByName(TRACE_DETAIL_TABLENAME);
				
				if(property == null || property.trim().length() == 0){
					return;
				}else if(property.equals("qtyPin")){
					MaterialTraceDetailDialog mtd = new MaterialTraceDetailDialog(UI.getActiveShell(), "PIN" , new TableListManager(adTable), material.getObjectRrn());
					mtd.open();
				}else if(property.equals("qtyWin")){
					MaterialTraceDetailDialog mtd = new MaterialTraceDetailDialog(UI.getActiveShell(), "WIN" , new TableListManager(adTable), material.getObjectRrn());
					mtd.open();
				}else if(property.equals("qtyRin")){
					MaterialTraceDetailDialog mtd = new MaterialTraceDetailDialog(UI.getActiveShell(), "RIN" , new TableListManager(adTable), material.getObjectRrn());
					mtd.open();
				}else if(property.equals("qtyOin")){
					MaterialTraceDetailDialog mtd = new MaterialTraceDetailDialog(UI.getActiveShell(), "OIN" , new TableListManager(adTable), material.getObjectRrn());
					mtd.open();
				}else if(property.equals("qtySou")){
					MaterialTraceDetailDialog mtd = new MaterialTraceDetailDialog(UI.getActiveShell(), "SOU" , new TableListManager(adTable), material.getObjectRrn());
					mtd.open();
				}else if(property.equals("qtyOou")){
					MaterialTraceDetailDialog mtd = new MaterialTraceDetailDialog(UI.getActiveShell(), "OOU" , new TableListManager(adTable), material.getObjectRrn());
					mtd.open();
				}else if(property.equals("qtyAdIn")){
					MaterialTraceDetailDialog mtd = new MaterialTraceDetailDialog(UI.getActiveShell(), "ADIN" , new TableListManager(adTable), material.getObjectRrn());
					mtd.open();
				}else if(property.equals("qtyAdOu")){
					MaterialTraceDetailDialog mtd = new MaterialTraceDetailDialog(UI.getActiveShell(), "ADOU" , new TableListManager(adTable), material.getObjectRrn());
					mtd.open();
				}else if(property.equals("qtyMwo")){
					MaterialTraceDetailDialog mtd = new MaterialTraceDetailDialog(UI.getActiveShell(), "MWO" , new TableListManager(adTable), material.getObjectRrn());
					mtd.open();
				}else if(property.equals("qtyAouIn")){
					MaterialTraceDetailDialog mtd = new MaterialTraceDetailDialog(UI.getActiveShell(), "AOUIN" , new TableListManager(adTable), material.getObjectRrn());
					mtd.open();
				}else if(property.equals("qtyAouOu")){
					MaterialTraceDetailDialog mtd = new MaterialTraceDetailDialog(UI.getActiveShell(), "AOUOU" , new TableListManager(adTable), material.getObjectRrn());
					mtd.open();
				}
			}
			
		};
	}

	@Override
	public void refresh() {
		for(Control ctl : children){
			if(ctl instanceof Text){
				Text txt = (Text)ctl;
				String property = (String) txt.getData(PROPERTY_NAME);
				Object value = PropertyUtil.getPropertyForIField(mt, property);
				txt.setText(value == null ? "-" : value.toString());
			}
		}
		
		refreshFooterArea();
	}
	
	private Text getText(String key){
		for(Control ctl : children){
			if(ctl instanceof Text){
				Text txt = (Text)ctl;
				if(key.equals(txt.getData(PROPERTY_NAME))){
					return txt;
				}
			}
		}
		return null;
	}
	
	protected ADTable getADTableByName(String tableName) {
		ADTable adTable = null;
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			adTable = entityManager.getADTable(0L, tableName);
			adTable = entityManager.getADTableDeep(adTable.getObjectRrn());
			return adTable;
		} catch (Exception e) {
			logger.error("MaterialTraceSection : getADTableByName()", e);
		}
		return null;
	}
}


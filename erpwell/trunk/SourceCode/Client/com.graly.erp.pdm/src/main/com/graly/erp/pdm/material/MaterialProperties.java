package com.graly.erp.pdm.material;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.AuthorityMenuItem;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.BrowserDialog;
import com.graly.erp.base.QueryTimeDialog;
import com.graly.erp.base.model.Constants;
import com.graly.erp.base.model.Material;
import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.VStorageMaterial;
import com.graly.erp.pdm.bomedit.BomTreeDialog;
import com.graly.erp.pdm.bomhistory.BomHisVersionDialog;
import com.graly.erp.pdm.bomselect.BomSelectTreeDialog;
import com.graly.erp.pdm.client.PDMManager;
import com.graly.erp.pdm.model.Bom;
import com.graly.erp.pdm.model.VPdmBom;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.erp.wip.model.ManufactureOrderLine;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADField;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.entitymanager.forms.EntityForm;
import com.graly.framework.base.entitymanager.forms.EntityProperties;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.forms.field.IField;
import com.graly.framework.base.ui.forms.field.listener.IValueChangeListener;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.PropertyUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.core.exception.ClientException;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
public class MaterialProperties extends EntityProperties {
	private static final Logger logger = Logger.getLogger(MaterialProperties.class);
	private final String TABLE_BOM = "BOM_Using_Material";//BOM使用的表
	private final String TABLE_AFFECTEDBOMS = "Affected_BOMS";//修改体积时提醒哪些BOM会受影响的表
	private static final String FIELD_NAME_MATERIALID = "materialId";
	private static final String FIELD_NAME_LENGTH = "costFormula";
	private static final String FIELD_NAME_WIDTH = "width";
	private static final String FIELD_NAME_HEIGHT = "height";
	private static final String FIELD_NAME_VOLUME = "volume";
	private static final String FIELD_NAME_PROCESSNAME = "processName";
	private static final String FIELD_NAME_ISPRODUCT = "isProduct";
	private static final String FIELD_NAME_ISLOTCONTROL = "isLotControl";
	private static final String FIELD_NAME_LOTTYPE= "lotType";
	private static final String FIELD_NAME_ISTHROUGH = "isThrough";
	private static final String FIELD_NAME_MATERIALCATEGORY7 = "materialCategory7";
	private static final String FIELD_NAME_MATERIALCATEGORY8 = "materialCategory8";
	private static final String FIELD_NAME_MATERIALCATEGORY9 = "materialCategory9";
	private static final String FIELD_NAME_MATERIALCATEGORY10 = "materialCategory10";
	private static final String FIELD_NAME_MATERIALCATEGORY11 = "materialCategory11";
	private static final String FIELD_NAME_RO1 = "ro1";
	private static final String FIELD_NAME_RO2 = "ro2";
	private String IMAGEFORM_TITLE = "Photo";
	protected ImageForm imageForm;
	protected ToolItem costFormula;	// 修改体积
	protected ToolItem editVolume;	// 修改体积
	protected ToolItem editBom;		// 编制Bom
	protected ToolItem selectBom;	// 选用Bom
	protected ToolItem bomHis;
	protected ToolItem reportBom;	
	protected ToolItem financialCost;//财务成本统计
	protected ToolItem testBOM; //测试BOM结构是否与现有BOM中的某个相同 OBJECT_RRN为99999999
	protected ToolItem bomQuery; //Query boms using this material
	
	protected ToolItem itemViewChart;
	protected static String URL = "http://192.168.0.235:81/caxa.jsp?serial_number=";
	
	private ADTable vPdmBomTable;
	private UsageInfoDialog usageDialog;
	private String label;
	private ADManager adManager;
	private boolean isVolumeChanged = false;
	private ADBase oldAdObject = null;
	private MovementLine movementLine;

	public final static Map<Long,long[]> syncOrgMaps = new HashMap<Long,long[]>();
	{
		syncOrgMaps.put(139420L, new long[]{12644730,41673024});
		syncOrgMaps.put(49204677L, new long[]{139420});
		syncOrgMaps.put(12644730L, new long[]{139420});
		syncOrgMaps.put(41673024L, new long[]{139420});
	}
	protected Menu menu;
	private VStorageMaterial selectedLine;
	
	public MaterialProperties() {
		super();
	}

	public MaterialProperties(EntityBlock masterParent, ADTable table) {
		super(masterParent, table);
	}

	@Override
	protected void createSectionContent(Composite client) {
		final FormToolkit toolkit = form.getToolkit();
		final IMessageManager mmng = form.getMessageManager();
		setTabs(new CTabFolder(client, SWT.FLAT | SWT.TOP));
		getTabs().marginHeight = 10;
		getTabs().marginWidth = 5;
		toolkit.adapt(getTabs(), true, true);
		GridData gd = new GridData(GridData.FILL_BOTH);
		getTabs().setLayoutData(gd);
		Color selectedColor = toolkit.getColors().getColor(FormColors.SEPARATOR);
		getTabs().setSelectionBackground(new Color[] { selectedColor, toolkit.getColors().getBackground() }, new int[] { 50 });
		toolkit.paintBordersFor(getTabs());

		for (ADTab tab : getTable().getTabs()) {
			CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
			item.setText(I18nUtil.getI18nMessage(tab, "label"));
			Form itemForm = null;
			if (IMAGEFORM_TITLE.equals(tab.getName())) {
				imageForm = new ImageForm(getTabs(), SWT.NULL, tab, mmng);
				item.setControl(imageForm);
			} else {
				itemForm = new EntityForm(getTabs(), SWT.NONE, tab, mmng);
				getDetailForms().add(itemForm);
				item.setControl(itemForm);
			}
		}
		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}

		addIsProductChanageListener();
	}

	private void addIsProductChanageListener() {
		IField isProduct = getIField(FIELD_NAME_ISPRODUCT);
		IField isLotControl = getIField(FIELD_NAME_ISLOTCONTROL);
		IField fLength = getIField(FIELD_NAME_LENGTH);
		IField fWidth = getIField(FIELD_NAME_WIDTH);
		IField fHeight = getIField(FIELD_NAME_HEIGHT);
		IField isThrough = getIField(FIELD_NAME_ISTHROUGH);
		if (isProduct != null)
			isProduct.addValueChangeListener(getIsProductChangedListener());
		if (isLotControl != null)
			isLotControl.addValueChangeListener(getIsLotControlChangedListener());
		if(Env.getOrgRrn()==139420L && isThrough!=null)
			isThrough.addValueChangeListener(getIsThroughChangedListener());
//		if (fLength != null)
//			fLength.addValueChangeListener(getLWHChangeListener());
//		if (fWidth != null)
//			fWidth.addValueChangeListener(getLWHChangeListener());
//		if (fHeight != null)
//			fHeight.addValueChangeListener(getLWHChangeListener());
	}

	private IValueChangeListener getIsLotControlChangedListener() {
		return new IValueChangeListener() {
			@Override
			public void valueChanged(Object sender, Object newValue) {
				refreshLotTypeName(newValue);
			}
		};
	}

	private IValueChangeListener getIsProductChangedListener() {
		return new IValueChangeListener() {
			@Override
			public void valueChanged(Object sender, Object newValue) {
				refreshProcessName(newValue);
			}
		};
	};
	
	private IValueChangeListener getIsThroughChangedListener() {
		return new IValueChangeListener() {
			@Override
			public void valueChanged(Object sender, Object newValue) {
				refreshMaterialCategory(newValue);
			}
		};
	}
	
//	private IValueChangeListener getLWHChangeListener(){
//		return new IValueChangeListener() {
//			public void valueChanged(Object sender, Object newValue) {
//				refreshVolume(newValue);
//			}
//		};
//	}

//	private void refreshVolume(Object newValue) {
//		IField fLength = getIField(FIELD_NAME_LENGTH);
//		IField fWidth = getIField(FIELD_NAME_WIDTH);
//		IField fHeight = getIField(FIELD_NAME_HEIGHT);
//		IField fVolume = getIField(FIELD_NAME_VOLUME);
//		if(fLength instanceof TextField && fWidth instanceof TextField && fHeight instanceof TextField && fVolume instanceof TextField){
//			BigDecimal costFormula = null;
//			BigDecimal width = null;
//			BigDecimal height = null;
//			BigDecimal volume = null;
//			BigDecimal oldVolume = null;
//			if(fLength.getValue() != null && ((String)fLength.getValue()).trim().length() > 0){
//				costFormula = new BigDecimal((String)fLength.getValue());
//			}
//			if(fWidth.getValue() != null && ((String)fWidth.getValue()).trim().length() > 0){
//				width = new BigDecimal((String)fWidth.getValue());
//			}
//			if(fHeight.getValue() != null && ((String)fHeight.getValue()).trim().length() > 0){
//				height = new BigDecimal((String)fHeight.getValue());
//			}
//			if(fVolume.getValue() != null && ((String)fVolume.getValue()).trim().length() > 0){
//				oldVolume = new BigDecimal((String)fVolume.getValue());
//			}
//			if(costFormula != null && width != null && height != null){
//				volume = costFormula.multiply(width).multiply(height);
//				fVolume.setValue(volume.toString());
//				fVolume.refresh();
//			}else{
//				fVolume.setValue(null);
//				fVolume.refresh();
//			}
//			if(oldVolume != volume && oldAdObject.equals(getAdObject())){
//				isVolumeChanged = true;
//			}else{
//				isVolumeChanged = false;
//			}
//		}
//	}

	private void refreshLotTypeName(Object newValue) {
		IField lotType = getIField(FIELD_NAME_LOTTYPE);
		ADField adFieldLotType = (ADField) lotType.getADField();
		label = lotType.getLabel();
		Label lotTypeLabel=(Label) lotType.getControls()[0];
		lotTypeLabel.setRedraw(true);
		if ("true".equals(newValue.toString())) {
			lotTypeLabel.setText(Message.getString("pdm.material_lottype"));
			adFieldLotType.setIsMandatory(true);
		} else {
			lotTypeLabel.setText(label);
			adFieldLotType.setIsMandatory(false);
		}
	}
	
	private void refreshProcessName(Object newValue) {
		IField processName = getIField(FIELD_NAME_PROCESSNAME);
		
		ADField adFieldProcess = (ADField) processName.getADField();
		label = processName.getLabel();
		Label processNameLabel=(Label) processName.getControls()[0];
		processNameLabel.setRedraw(true);
		if ("true".equals(newValue.toString())) {
			processNameLabel.setText(Message.getString("pdm.material_processname"));
			adFieldProcess.setIsMandatory(true);
		} else {
			adFieldProcess.setIsMandatory(false);
			processNameLabel.setText(label);
		}
	}

	private IField getIField(String fieldId) {
		for (Form form : getDetailForms()) {
			IField f = form.getFields().get(fieldId);
			if (f != null) {
				return f;
			}
		}
		return null;
	}

	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		// createToolItemBomReport(tBar);
		createToolItemNew(tBar);
//		createToolItemTestBOM(tBar);
		createToolItemSave(tBar);
		createToolItemEditVolume(tBar);
		createToolItemCostFormula(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemBomQuery(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createMenuBOM(tBar);
		createToolItemEditBom(tBar);
		createToolItemSelectBom(tBar);
	//	createToolItemSelectBomHis(tBar);//查看BOM历史
		createToolItemFinancialCost(tBar);
		
		createToolitemViewChart(tBar);
		
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
		setInitAuthority(true);
	}
	
//	protected void createToolItemSelectBomHis(ToolBar tBar) {
//		bomHis = new AuthorityToolItem(tBar, SWT.PUSH, KEY_BOMHIS);
//		bomHis.setText("查看BOM历史");
//		bomHis.setImage(SWTResourceCache.getImage("bomtree"));
//		bomHis.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent event) {
//				selectBomHisAdapter(event);
//			}
//		});
//	}
	
	protected void createToolItemCostFormula(ToolBar tBar) {
		costFormula = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_COSTFORMULA);
		costFormula.setText(Message.getString("pdm.costformula"));
		costFormula.setImage(SWTResourceCache.getImage("formula"));
		costFormula.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				costFormulaAdapter();
			}
		});
	}
	
	protected void costFormulaAdapter() {
		Material material = (Material) getAdObject();
		if(material != null && material.getObjectRrn() != null){
			CostFormulaDialog cfd = new CostFormulaDialog(UI.getActiveShell(), this, form, (Material) getAdObject());
			cfd.open();
		}
	}

	protected void createToolItemEditVolume(ToolBar tBar) {
		editVolume = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_EDITVOLUME);
		editVolume.setText(Message.getString("pdm.edit_volume"));
		editVolume.setImage(SWTResourceCache.getImage("volume"));
		editVolume.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				editVolumeAdapter();
			}
		});
	}

	protected void editVolumeAdapter() {
		Material material = (Material) getAdObject();
		if(material != null && material.getObjectRrn() != null){
			EditVolumeDialog evd = new EditVolumeDialog(UI.getActiveShell(),this,form,material);
			evd.open();
		}
	}

	protected void createToolItemFinancialCost(ToolBar tBar) {
		financialCost = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_FINANCIALCOST);
		financialCost.setText(Message.getString("pdm.financial_cost"));
		financialCost.setImage(SWTResourceCache.getImage("report"));
		financialCost.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				financialCostAdapter();
			}
		});
	}
	
	protected void financialCostAdapter() {
		try {
			String report = "financialCost_report.rptdesign";

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();
			Material material = (Material) getAdObject();
			Long materialRrn = (material != null ? material.getObjectRrn() : null);
			
			if(materialRrn != null){
				userParams.put("MATERIAL_RRN", String.valueOf(materialRrn));
			}else{
				userParams.put("MATERIAL_RRN", null);
			}
			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
			dialog.open();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	protected void createToolItemTestBOM(ToolBar tBar) {
		testBOM = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_TESTBOM);
		testBOM.setText(Message.getString("pdm.test_bom"));
		testBOM.setImage(SWTResourceCache.getImage("report"));
		testBOM.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				testBOMAdapter();
			}
		});
	}

	protected void testBOMAdapter() {
		
	}

	protected void createToolItemBomQuery(ToolBar tBar) {
		bomQuery = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_BOMUSAGE);
		bomQuery.setText(Message.getString("pdm.querybom_title"));
		bomQuery.setImage(SWTResourceCache.getImage("report"));
		bomQuery.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				queryAdapter();
			}
		});
	}
	
	protected void queryAdapter() {
		if(getAdObject() != null && getAdObject().getObjectRrn() != null) {
			usageDialog = new UsageInfoDialog(UI.getActiveShell(), (Material) getAdObject(), getVPdmBomTable());
			usageDialog.open();
		}
	}
	
	@Override
	protected void createToolItemNew(ToolBar tBar) {
		itemNew = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_NEW);
		itemNew.setText(Message.getString("common.new"));
		itemNew.setImage(SWTResourceCache.getImage("new"));
		itemNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				newAdapter();
			}
		});
	}

	@Override
	protected void createToolItemSave(ToolBar tBar) {
		itemSave = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_SAVE);
		itemSave.setText(Message.getString("common.save"));
		itemSave.setImage(SWTResourceCache.getImage("save"));
		itemSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				saveAdapter();
			}
		});
	}

	@Override
	protected void createToolItemDelete(ToolBar tBar) {
		itemDelete =new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_DELETE);
		itemDelete.setText(Message.getString("common.delete"));
		itemDelete.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_DELETE));
		itemDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteAdapter();
			}
		});
	}
	
	@Override
	protected void deleteAdapter() {
		if(getAdObject().getObjectRrn() != null)
		{
		try{
			PDMManager pdmManager = Framework.getService(PDMManager.class);
			INVManager invManager = Framework.getService(INVManager.class);			
			List childBOM = pdmManager.getAllChildBoms(getAdObject().getObjectRrn());
			List lotStorage = invManager.getLotStorage(getAdObject().getObjectRrn());
			if(!pdmManager.getMaterialRrn(getAdObject().getObjectRrn())){
				UI.showInfo("该物料有出入库记录，不能删除");
				return ;
			};

			if(childBOM.size() > 0){
				UI.showInfo("该物料有BOM，不能删除");
				return ;
			}
			
			if(lotStorage != null && lotStorage.size() > 0){
				UI.showInfo("该物料有库存，不能删除");
				return ;
			}
			List<ADBase> objects = pdmManager.validateBeforeDeleteMaterial((Material) getAdObject());
//			if(materialRrn = null){
//				
//			}
			if(objects != null && objects.size() > 0){
				StringBuffer maSb = new StringBuffer();
				StringBuffer moSb = new StringBuffer();
				StringBuffer moLineSb = new StringBuffer();
				for(ADBase object : objects){
					if(object instanceof VPdmBom){
						maSb.append(" '" + ((VPdmBom)object).getMaterialParentId() + "' ");
					}else if(object instanceof ManufactureOrder){
						moSb.append(" '" + ((ManufactureOrder)object).getDocId() + "' ");
					}else if(object instanceof ManufactureOrderLine){
						moLineSb.append(" '" + ((ManufactureOrderLine)object).getMaterialId() + "' ");
					}
				}
				StringBuffer info = new StringBuffer();
				info.append( "共有" + objects.size() + "个对象使用了该物料,其中\n" );
				info.append( " 物料:" + maSb +"\n" );
				info.append( " 工作令:" + moSb + "\n" );
				info.append( " 子工作令:" + moLineSb );
				
				UI.showInfo(info.toString());
				return;
			}
			
			
			super.deleteAdapter();
		}
		catch (Exception e){
			ExceptionHandlerManager.asyncHandleException(e);
		}
		}else
		{
			UI.showInfo("请选择物料");
		}
	}
	
	private void createToolitemViewChart(ToolBar tBar) {
		itemViewChart = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_VIEW_CHART);	
		itemViewChart.setText("查看图纸");
		itemViewChart.setImage(SWTResourceCache.getImage("bomtree"));
		itemViewChart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				itemViewChartAdapter();
			}
		});
	}

	protected void itemViewChartAdapter() {
		Material m = (Material)getAdObject();
		BrowserDialog bd = new BrowserDialog(UI.getActiveShell(), URL + m.getMaterialId()+"&type=0");
		bd.open();
	}


	protected void createToolItemBomReport(ToolBar tBar) {
		reportBom = new AuthorityToolItem(tBar, SWT.PUSH, "");
		reportBom.setText("BomReport");
		reportBom.setImage(SWTResourceCache.getImage("report"));
		reportBom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				bomReportAdapter(event);
			}
		});
	}

	protected void createMenuBOM(ToolBar tBar){
		menu = new Menu(UI.getActiveShell(), SWT.POP_UP);
		MenuItem miViewBOM = new MenuItem(menu, SWT.PUSH);
		miViewBOM.setText("查看");
		MenuItem miEditBOM = new MenuItem(menu, SWT.PUSH);
		miEditBOM.setText("编辑");
		
		new MenuItem(menu, SWT.SEPARATOR);
		AuthorityMenuItem miHisBOM = new AuthorityMenuItem(menu, SWT.PUSH, Constants.KEY_MATERIAL_BOMHIS);
		miHisBOM.setText("BOM历史");
		AuthorityMenuItem amiBomDoc = new AuthorityMenuItem(menu, SWT.PUSH, Constants.KEY_MATERIAL_BOMDOC);
		amiBomDoc.setText("BOM流转单");
		
		AuthorityMenuItem amiBomQuery = new AuthorityMenuItem(menu, SWT.PUSH, Constants.KEY_MATERIAL_BOMQUERY);
		amiBomQuery.setText("BOM查询");
		
		if(miHisBOM.isCreated() || amiBomDoc.isCreated() || amiBomQuery.isCreated()){
			new MenuItem(menu, SWT.SEPARATOR);
		}
		
		MenuItem miCancel = new MenuItem(menu, SWT.PUSH);
		miCancel.setText("取消");
		
		miViewBOM.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				viewBomAdapter();
			}
			
		});
		
		miEditBOM.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				editBomAdapter();
			}
			
		});
		
		miHisBOM.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				selectBomHisAdapter();
			}
			
		});
		
		amiBomDoc.addSelectionListener(new SelectionListener(){

			@Override
			public void widgetSelected(SelectionEvent e) {
				selectBomDocAdapter();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		});
		
		amiBomQuery.addSelectionListener(new SelectionListener(){
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectBomQueryAdapter();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
			
		});
	}
	
	protected void selectBomQueryAdapter() {
		String urlfmt = Message.getString("url.bomquery");
		String url = String.format(urlfmt);
		BrowserDialog bd = new BrowserDialog(UI.getActiveShell(), url);
		bd.open();
	}

	protected void selectBomDocAdapter() {
		String urlfmt = Message.getString("url.bomdoc");
		String url = String.format(urlfmt);
		BrowserDialog bd = new BrowserDialog(UI.getActiveShell(), url);
		bd.open();
	}

	protected void createToolItemEditBom(final ToolBar tBar) {
		editBom = new AuthorityToolItem(tBar, SWT.DROP_DOWN, Constants.KEY_MATERIAL_EDITBOM);
		editBom.setText(Message.getString("pdm.bom"));
		editBom.setImage(SWTResourceCache.getImage("bomtree"));
		editBom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if(event.detail == SWT.ARROW){
					Rectangle bounds = editBom.getBounds();
					Point point = tBar.toDisplay(bounds.x, bounds.y + bounds.height);
					menu.setLocation(point);
					menu.setVisible(true);
				}
			}
		});
	}
	
	protected void createToolItemSelectBom(ToolBar tBar) {
		selectBom = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_MATERIAL_SELECTBOM);
		selectBom.setText(Message.getString("pdm.select_bom"));
		selectBom.setImage(SWTResourceCache.getImage("bomtree"));
		selectBom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				selectBomAdapter(event);
			}
		});
	}

	protected void bomReportAdapter(SelectionEvent event) {
		try {
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				Material material = (Material) getAdObject();
				PDMManager manager = Framework.getService(PDMManager.class);
				manager.getBomDetails(material.getObjectRrn(), 1);
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}

	protected void viewBomAdapter() {
		form.getMessageManager().removeAllMessages();
		try {
			if (getAdObject() != null && getAdObject().getObjectRrn() != null) {
				if(adManager == null) 
					adManager = Framework.getService(ADManager.class);
				setAdObject(adManager.getEntity(getAdObject()));
				
				if(((Material)getAdObject()).getBomRrn() == null) {
					BomTreeDialog btd = new BomTreeDialog(UI.getActiveShell(), form, (Material) getAdObject(), false);
					if(btd.open() == Window.CANCEL) {
						
					}
				}
				else {
					UI.showError(String.format(Message.getString("pdm.has_bom_type_can_not_edit"),
							((Material)getAdObject()).getMaterialId()));
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("MaterialProperties : viewBomAdapter()", e);
		}
	}
	
	protected void editBomAdapter() {
		form.getMessageManager().removeAllMessages();
		try {
			if (getAdObject() != null && getAdObject().getObjectRrn() != null) {
				if(adManager == null) 
					adManager = Framework.getService(ADManager.class);
				setAdObject(adManager.getEntity(getAdObject()));
				
				if(((Material)getAdObject()).getBomRrn() == null) {
					BomTreeDialog btd = new BomTreeDialog(UI.getActiveShell(), form, (Material) getAdObject(), true);
					if(btd.open() == Window.CANCEL) {
						PDMManager pdmManager = Framework.getService(PDMManager.class);
						Material material = (Material)adManager.getEntity(this.getAdObject());
						material = pdmManager.calculateVolumeByBOM(material);
						setAdObject(material);
						refresh();
						getMasterParent().refreshUpdate(material);
						if(btd.isChanged()){
							if(material != null && material.getObjectRrn() != null && material.getIsVolumeBasis()){
								List<Bom> parentBOMs = pdmManager.getDeepParentBOMs(material);
								List<Material> materials = new ArrayList<Material>();
								for(Bom bom : parentBOMs){
									Material m = bom.getParentMaterial();
									if(!materials.contains(m)){
										materials.add(m);
									}
								}
								CalculateVolumeProgressDialog progressDialog = new CalculateVolumeProgressDialog(UI.getActiveShell());
								CalculateVolumeProgress progress = new CalculateVolumeProgress(materials);
								progressDialog.run(true, true, progress);
								if (progress.isFinished()) {
									UI.showInfo("使用该物料的BOM体积已成功更新！");
								}
							}
						}
					}
				}
				else {
					UI.showError(String.format(Message.getString("pdm.has_bom_type_can_not_edit"),
							((Material)getAdObject()).getMaterialId()));
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("MaterialProperties : editBomAdapter()", e);
		}
	}
	
	protected void selectBomHisAdapter() {
		try {
			if (getAdObject() != null && getAdObject().getObjectRrn() != null) {
				if(adManager == null) 
					adManager = Framework.getService(ADManager.class);
				setAdObject(adManager.getEntity(getAdObject()));
				
				if(((Material)getAdObject()).getBomRrn() == null) {
					BomHisVersionDialog bomHisVersionDialog=new BomHisVersionDialog(UI.getActiveShell(),(Material) getAdObject(),form);
					bomHisVersionDialog.open();
				}
				else {
					UI.showError(String.format(Message.getString("pdm.has_bom_type_can_not_edit"),
							((Material)getAdObject()).getMaterialId()));
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
	
	protected void selectBomAdapter(SelectionEvent event) {
		form.getMessageManager().removeAllMessages();
		try {
			if (getAdObject() != null && getAdObject().getObjectRrn() != null) {
				if(adManager == null) 
					adManager = Framework.getService(ADManager.class);
				setAdObject(adManager.getEntity(getAdObject()));
				
				if(!hasActualBom()) {
					BomSelectTreeDialog btd = new BomSelectTreeDialog(event.widget.getDisplay().getActiveShell(), form, (Material) getAdObject());
					if(btd.open() == Window.CANCEL) {
						Material material = (Material)adManager.getEntity(this.getAdObject());
						setAdObject(material);
						refresh();
						getMasterParent().refreshUpdate(material);
					}					
				}
				else {
					UI.showError(String.format(Message.getString("pdm.has_bom_can_not_select"),
							((Material)getAdObject()).getMaterialId()));
				}
			}
		} catch(Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("MaterialProperties : selectBomAdapter()", e);
		}
	}
	
	protected boolean hasActualBom() throws Exception {
		if (getAdObject() != null && getAdObject().getObjectRrn() != null) {
			if(adManager == null) 
				adManager = Framework.getService(ADManager.class);
			Long parentRrn = ((Material)getAdObject()).getObjectRrn();
			List<Bom> boms = adManager.getEntityList(Env.getOrgRrn(), Bom.class, 2,
					" parentRrn = " + parentRrn + " ", null);
			if(boms != null && boms.size() > 0)
				return true;
			else
				return false;
		}
		throw new Exception("Can not Get Bom for Material: "
				+ ((Material)getAdObject()).getMaterialId());
	}

	@Override
	protected void saveAdapter() {
		try {
			PDMManager pdmManager = Framework.getService(PDMManager.class);
			Material mat = (Material) getAdObject();
			List<Bom> parentBOMs = null;
			List<Material> materials = null;
			boolean isCalculate = false;
			if(isVolumeChanged){
				if(mat != null && mat.getObjectRrn() != null && mat.getIsVolumeBasis()){
					parentBOMs = pdmManager.getDeepParentBOMs(mat);
					materials = new ArrayList<Material>();
					for(Bom bom : parentBOMs){
						Material m = bom.getParentMaterial();
						if(!materials.contains(m)){
							materials.add(m);
						}
					}
					
					AffectedBomInfoDialog abid = new AffectedBomInfoDialog(UI.getActiveShell(), materials, getADTableByName(TABLE_AFFECTEDBOMS));
					if(abid.open() == Window.OK){
						isCalculate = true;
					}else{
						return;
					}
					
				}
			}
			
			form.getMessageManager().removeAllMessages();
			if (getAdObject() != null) {
				boolean saveFlag = true;
				for (Form detailForm : getDetailForms()) {
					if (!detailForm.saveToObject()) {
						saveFlag = false;
					}
				}
				if(Env.getOrgRrn()==139420L){//--童庆飞要求
					Material knMaterial = (Material) getAdObject();
					if("商品".equals(knMaterial.getMaterialCategory1())){
						if(knMaterial.getMaterialCategory7()==null || "".equals(knMaterial.getMaterialCategory7())){
							UI.showError("计划信息中的外壳不能为空");
							return;
						}
						if(knMaterial.getMaterialCategory8()==null || "".equals(knMaterial.getMaterialCategory8())){
							UI.showError("计划信息中的玻璃钢桶不能为空");
							return;
						}
						if(knMaterial.getMaterialCategory9()==null || "".equals(knMaterial.getMaterialCategory9())){
							UI.showError("计划信息中的控制阀型号不能为空");
							return;
						}
						if(knMaterial.getMaterialCategory10()==null || "".equals(knMaterial.getMaterialCategory10())){
							UI.showError("计划信息中的旁通阀及其他联接不能为空");
							return;
						}
						if(knMaterial.getMaterialCategory11()==null || "".equals(knMaterial.getMaterialCategory11())){
							UI.showError("计划信息中的盐阀不能为空");
							return;
						}
						if(knMaterial.getRo1()==null || "".equals(knMaterial.getRo1())){
							UI.showError("RO机不能为空");
							return;
						}
						if(knMaterial.getRo2()==null || "".equals(knMaterial.getRo2())){
							UI.showError("RO膜不能为空");
							return;
						}
					}
				}
				if (saveFlag) {
					ADBase oldBase = getAdObject();
					for (Form detailForm : getDetailForms()) {
						PropertyUtil.copyProperties(getAdObject(), detailForm.getObject(), detailForm.getFields());
					}
					
					//save material and synchronize the one in another area
					//1.save material this area
					Material material = pdmManager.saveMaterial(getTable().getObjectRrn(), (Material) getAdObject(), Env.getUserRrn());
					ADManager entityManager = Framework.getService(ADManager.class);
					setAdObject(entityManager.getEntity(material));
					
					//2.get material with the same id in another area
					long[] syncOrgs = syncOrgMaps.get(Env.getOrgRrn());
					if(syncOrgs == null ){
						syncOrgs = new long[]{};
					}
					for(long orgRrn : syncOrgs){
						if(Env.getOrgRrn() ==49204677L &&  oldBase.getObjectRrn()!=null  ){
							break;
						}
						List<Material> materialList = pdmManager.getMaterialById(material.getMaterialId(), orgRrn);
						Material sMaterial = null;
						if(materialList == null || materialList.size() == 0){
						}else{
							sMaterial = materialList.get(0);
							if(Env.getOrgRrn()==49204677L){
								break;
							}
						}
						sMaterial = copyMaterial(material, sMaterial, orgRrn);
						
						pdmManager.saveMaterial(getTable().getObjectRrn(), sMaterial, Env.getUserRrn());
					}
					
					// 保存Material Photo
					imageForm.setObject(getAdObject());
					imageForm.saveToObject();
					if(isCalculate){
						CalculateVolumeProgressDialog progressDialog = new CalculateVolumeProgressDialog(UI.getActiveShell());
						CalculateVolumeProgress progress = new CalculateVolumeProgress(materials);
						progressDialog.run(true, true, progress);
						if (progress.isFinished()) {
							UI.showInfo("使用该物料的BOM体积已成功更新！");
						}
					}
					UI.showInfo(Message.getString("common.save_successed"));// 弹出提示框
					refresh();
					ADBase newBase = getAdObject();
					if (oldBase.getObjectRrn() == null) {
						getMasterParent().refreshAdd(newBase);
					} else {
						getMasterParent().refreshUpdate(newBase);
					}
//					getMasterParent().refresh();
				}
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	@Override
	public void refresh() {
		isVolumeChanged = false;
		for (Form detailForm : getDetailForms()) {
			detailForm.setObject(getAdObject());
			detailForm.loadFromObject();
		}
		// 刷新 Material Photo
		imageForm.setObject(getAdObject());
		imageForm.loadFromObject();
		form.getMessageManager().removeAllMessages();
	}
	
	protected void setInitAuthority(boolean enabled) {
		itemNew.setEnabled(enabled);
		itemSave.setEnabled(enabled);
		itemDelete.setEnabled(enabled);
		editBom.setEnabled(enabled);
		selectBom.setEnabled(enabled);
//		reportBom.setEnabled(enabled);
	}

	@Override
	public void dispose() {
		if (imageForm != null && !imageForm.isDisposed()) {
			imageForm.dispose();
		}
		super.dispose();
	}

	private ADTable getVPdmBomTable() {
		try {
			if (vPdmBomTable != null) {
				return vPdmBomTable;
			} else {
				vPdmBomTable = getADTableByName(TABLE_BOM);
				return vPdmBomTable;
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return null;
	}

	private ADTable getADTableByName(String tableName) {
		try {
			ADManager entityManager = Framework.getService(ADManager.class);
			ADTable adTablee = entityManager.getADTable(0L, tableName);
			return adTablee;
		} catch (Exception e) {
			logger.error(e);
			ExceptionHandlerManager.asyncHandleException(e);
		}
		return null;
	}
	
	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		super.selectionChanged(part, selection);
        oldAdObject = getAdObject();
	}
	
	public void saveVolume(){
		try {
			if(isVolumeChanged){//只有体积改变时才保存，体积没变时不保存
				boolean isCalculate = false;
				if(isVolumeChanged){
					PDMManager pdmManager = Framework.getService(PDMManager.class);
					Material mat = (Material) getAdObject();
					List<Bom> parentBOMs = null;
					List<Material> materials = null;
					if(mat != null && mat.getObjectRrn() != null && mat.getIsVolumeBasis()){
						parentBOMs = pdmManager.getDeepParentBOMs(mat);
						materials = new ArrayList<Material>();
						for(Bom bom : parentBOMs){
							Material m = bom.getParentMaterial();
							if(!materials.contains(m)){
								materials.add(m);
							}
						}
						
						AffectedBomInfoDialog abid = new AffectedBomInfoDialog(UI.getActiveShell(), materials, getADTableByName(TABLE_AFFECTEDBOMS));
						if(abid.open() == Window.OK){
							isCalculate = true;
						}else{
							return;
						}
						
					}
					if (getAdObject() != null) {
						Material material = pdmManager.saveMaterial(getTable().getObjectRrn(), (Material) getAdObject(), Env.getUserRrn());
						ADManager entityManager = Framework.getService(ADManager.class);
						setAdObject(entityManager.getEntity(material));
						if(isCalculate){
							CalculateVolumeProgressDialog progressDialog = new CalculateVolumeProgressDialog(UI.getActiveShell());
							CalculateVolumeProgress progress = new CalculateVolumeProgress(materials);
							progressDialog.run(true, true, progress);
							if (progress.isFinished()) {
								UI.showInfo("使用该物料的BOM体积已成功更新！");
							}
						}
						UI.showInfo(Message.getString("common.save_successed"));// 弹出提示框
						refresh();
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public boolean isVolumeChanged() {
		return isVolumeChanged;
	}

	public void setVolumeChanged(boolean isVolumeChanged) {
		this.isVolumeChanged = isVolumeChanged;
	}
	
	private Material copyMaterial(Material source, Material target, long targetOrgRrn){
		if(target == null){
			//如果是新建,则同步
			try {
				target = (Material) source.clone();
				target.setOrgRrn(targetOrgRrn);
			} catch (CloneNotSupportedException e) {
				logger.error(e);
			}
		}else{
//			/*2013-5-6如果创建人和更新人是同一个人,则同步更新其它区域该物料的信息*/ 16-03-21修改不同步 小谢提出
			if(source.getCreatedBy().equals(Env.getUserRrn())){
//				target.setMaterialId(source.getMaterialId());
//				target.setName(source.getName());
//				target.setDescription(source.getDescription());
//				target.setComments(source.getComments());
//				target.setMaterialCategory(source.getMaterialCategory1());
//				target.setMaterialType(source.getMaterialType());
//				target.setRefernectPrice(source.getRefernectPrice());
//				target.setSpecification(source.getSpecification());
//				target.setModel(source.getModel());
//				target.setWeight(source.getWeight());
//				target.setVolume(source.getVolume());
//				target.setInventoryUom(source.getInventoryUom());
//				target.setPurchaseUom(source.getPurchaseUom());
//				target.setPurInvConversion(source.getPurInvConversion());
//				target.setInventoryProperty(source.getInventoryProperty());
//				target.setAbcCategory(source.getAbcCategory());
//				target.setIsJit(source.getIsJit());
//				target.setIsVirtual(source.getIsVirtual());
//				target.setIsPurchase(source.getIsPurchase());
//				target.setIsSale(source.getIsSale());
//				target.setIsProduct(source.getIsProduct());
//				target.setIsMrp(source.getIsMrp());
//				target.setStockCapability(source.getStockCapability());
//				//祁亦要求其他区域的最小库存更新不影响到开能的最小库存
//				if(targetOrgRrn!=139420L){
//					target.setQtyMin(source.getQtyMin());
//				}
//				target.setQtyMax(source.getQtyMax());
//				target.setQtyInitial(source.getQtyInitial());
//				target.setQtyIn(source.getQtyIn());
//				target.setQtyOut(source.getQtyOut());
//				target.setQtyDifference(source.getQtyDifference());
//				target.setQtyTransit(source.getQtyTransit());
//				target.setQtyAllocation(source.getQtyAllocation());
//				target.setReferenceDoc1(source.getReferenceDoc1());
//				target.setReferenceDoc2(source.getReferenceDoc2());
//				target.setReferenceDoc3(source.getReferenceDoc3());
//				target.setReferenceDoc4(source.getReferenceDoc4());
//				target.setReferenceDoc5(source.getReferenceDoc5());
//				target.setProductionCycle(source.getProductionCycle());
//				target.setWorkCenterRrn(source.getWorkCenterRrn());
//				target.setPhoto(source.getPhoto());
//				target.setBuyerId(source.getBuyerId());
//				target.setPlannerId(source.getPlannerId());
//				target.setKeeperId(source.getKeeperId());
//				target.setStandCost(source.getStandCost());
//				target.setActrualCost(source.getActrualCost());
//				target.setLotType(source.getLotType());
//				target.setState(source.getState());
//				target.setStandTime(source.getStandTime());
//				target.setQtyMinProduct(source.getQtyMinProduct());
//				target.setIsShare(source.getIsShare());
//				target.setMaterialCategory2(source.getMaterialCategory2());
//				target.setMaterialCategory3(source.getMaterialCategory3());
//				target.setMaterialCategory4(source.getMaterialCategory4());
//				target.setMaterialCategory1(source.getMaterialCategory1());
//				target.setIqcLeadTime(source.getIqcLeadTime());
//				target.setBomPrice(source.getBomPrice());
//				target.setApplicant(source.getApplicant());
//				target.setDateApply(source.getDateApply());
//				target.setMaterialCategory6(source.getMaterialCategory6());
//				target.setMaterialCategory5(source.getMaterialCategory5());
//				target.setLength(source.getLength());
//				target.setWidth(source.getWidth());
//				target.setHeight(source.getHeight());
//				target.setCostFormula(source.getCostFormula());
//				target.setIsLotControl(source.getIsLotControl());
//				target.setIsInspectionFree(source.getIsInspectionFree());
//				target.setIsVolumeBasis(source.getIsVolumeBasis());
//				target.setProcessName(source.getProcessName());
			}
			//如果是修改则不同步
//			target.setMaterialId(source.getMaterialId());
//			target.setName(source.getName());
//			target.setDescription(source.getDescription());
//			target.setComments(source.getComments());
//			target.setMaterialCategory(source.getMaterialCategory1());
//			target.setMaterialType(source.getMaterialType());
//			target.setRefernectPrice(source.getRefernectPrice());
//			target.setSpecification(source.getSpecification());
//			target.setModel(source.getModel());
//			target.setWeight(source.getWeight());
//			target.setVolume(source.getVolume());
//			target.setInventoryUom(source.getInventoryUom());
//			target.setPurchaseUom(source.getPurchaseUom());
//			target.setPurInvConversion(source.getPurInvConversion());
//			target.setInventoryProperty(source.getInventoryProperty());
//			target.setAbcCategory(source.getAbcCategory());
//			target.setIsJit(source.getIsJit());
//			target.setIsVirtual(source.getIsVirtual());
//			target.setIsPurchase(source.getIsPurchase());
//			target.setIsSale(source.getIsSale());
//			target.setIsProduct(source.getIsProduct());
//			target.setIsMrp(source.getIsMrp());
//			target.setStockCapability(source.getStockCapability());
//			target.setQtyMin(source.getQtyMin());
//			target.setQtyMax(source.getQtyMax());
//			target.setQtyInitial(source.getQtyInitial());
//			target.setQtyIn(source.getQtyIn());
//			target.setQtyOut(source.getQtyOut());
//			target.setQtyDifference(source.getQtyDifference());
//			target.setQtyTransit(source.getQtyTransit());
//			target.setQtyAllocation(source.getQtyAllocation());
//			target.setReferenceDoc1(source.getReferenceDoc1());
//			target.setReferenceDoc2(source.getReferenceDoc2());
//			target.setReferenceDoc3(source.getReferenceDoc3());
//			target.setReferenceDoc4(source.getReferenceDoc4());
//			target.setReferenceDoc5(source.getReferenceDoc5());
//			target.setProductionCycle(source.getProductionCycle());
//			target.setWorkCenterRrn(source.getWorkCenterRrn());
//			target.setPhoto(source.getPhoto());
//			target.setBuyerId(source.getBuyerId());
//			target.setPlannerId(source.getPlannerId());
//			target.setKeeperId(source.getKeeperId());
//			target.setStandCost(source.getStandCost());
//			target.setActrualCost(source.getActrualCost());
//			target.setLotType(source.getLotType());
//			target.setState(source.getState());
//			target.setStandTime(source.getStandTime());
//			target.setQtyMinProduct(source.getQtyMinProduct());
//			target.setIsShare(source.getIsShare());
//			target.setMaterialCategory2(source.getMaterialCategory2());
//			target.setMaterialCategory3(source.getMaterialCategory3());
//			target.setMaterialCategory4(source.getMaterialCategory4());
//			target.setMaterialCategory1(source.getMaterialCategory1());
//			target.setIqcLeadTime(source.getIqcLeadTime());
//			target.setBomPrice(source.getBomPrice());
//			target.setApplicant(source.getApplicant());
//			target.setDateApply(source.getDateApply());
//			target.setMaterialCategory6(source.getMaterialCategory6());
//			target.setMaterialCategory5(source.getMaterialCategory5());
//			target.setLength(source.getLength());
//			target.setWidth(source.getWidth());
//			target.setHeight(source.getHeight());
//			target.setCostFormula(source.getCostFormula());
//			target.setIsLotControl(source.getIsLotControl());
//			target.setIsInspectionFree(source.getIsInspectionFree());
//			target.setIsVolumeBasis(source.getIsVolumeBasis());
//			target.setProcessName(source.getProcessName());
		}
		return target;
	}
	
	private void refreshMaterialCategory(Object newValue) {
//		IField isThrough = getIField(FIELD_NAME_ISTHROUGH);
		IField materialCategory7 = getIField(FIELD_NAME_MATERIALCATEGORY7);
		IField materialCategory8 = getIField(FIELD_NAME_MATERIALCATEGORY8);
		IField materialCategory9 = getIField(FIELD_NAME_MATERIALCATEGORY9);
		IField materialCategory10 = getIField(FIELD_NAME_MATERIALCATEGORY10);
		IField materialCategory11 = getIField(FIELD_NAME_MATERIALCATEGORY11);
		IField ro1 = getIField(FIELD_NAME_RO1);
		IField ro2 = getIField(FIELD_NAME_RO2);
		Boolean flag = (Boolean) newValue;
		if(newValue!=null&&flag){
			if(materialCategory7!=null){
				if(materialCategory7.getValue()==null || "".equals(materialCategory7.getValue())){
					materialCategory7.setValue("空白");
					materialCategory7.refresh();
				}
			}
			if(materialCategory8!=null){
				if(materialCategory8.getValue()==null || "".equals(materialCategory8.getValue())){
					materialCategory8.setValue("空白");
					materialCategory8.refresh();
				}
			}
			if(materialCategory9!=null){
				if(materialCategory9.getValue()==null || "".equals(materialCategory9.getValue())){
					materialCategory9.setValue("空白");
					materialCategory9.refresh();
				}
			}
			if(materialCategory10!=null){
				if(materialCategory10.getValue()==null || "".equals(materialCategory10.getValue())){
					materialCategory10.setValue("空白");
					materialCategory10.refresh();
				}
			}
			if(materialCategory11!=null){
				if(materialCategory11.getValue()==null || "".equals(materialCategory11.getValue())){
					materialCategory11.setValue("空白");
					materialCategory11.refresh();
				}
			}
			if(ro1!=null){
				if(ro1.getValue()==null || "".equals(ro1.getValue())){
					ro1.setValue("空白");
					ro1.refresh();
				}
			}
			if(ro2!=null){
				if(ro2.getValue()==null || "".equals(ro2.getValue())){
					ro2.setValue("空白");
					ro2.refresh();
				}
			}
		}else{
			if (materialCategory7 != null) {
				if (materialCategory7.getValue() == null
						|| "".equals(materialCategory7.getValue())) {
					materialCategory7.setValue(null);
					materialCategory7.refresh();
				}
			}
			if (materialCategory8 != null) {
				if (materialCategory8.getValue() == null
						|| "".equals(materialCategory8.getValue())) {
					materialCategory8.setValue(null);
					materialCategory8.refresh();
				}
			}
			if (materialCategory9 != null) {
				if (materialCategory9.getValue() == null
						|| "".equals(materialCategory9.getValue())) {
					materialCategory9.setValue(null);
					materialCategory9.refresh();
				}
			}
			if (materialCategory10 != null) {
				if (materialCategory10.getValue() == null
						|| "".equals(materialCategory10.getValue())) {
					materialCategory10.setValue(null);
					materialCategory10.refresh();
				}
			}
			if (materialCategory11 != null) {
				if (materialCategory11.getValue() == null
						|| "".equals(materialCategory11.getValue())) {
					materialCategory11.setValue(null);
					materialCategory11.refresh();
				}
				if (ro1 != null) {
					if (ro1.getValue() == null || "".equals(ro1.getValue())) {
						ro1.setValue(null);
						ro1.refresh();
					}
				}
				if (ro2 != null) {
					if (ro2.getValue() == null || "".equals(ro2.getValue())) {
						ro2.setValue(null);
						ro2.refresh();
					}
				}
			}
		}
	}
}

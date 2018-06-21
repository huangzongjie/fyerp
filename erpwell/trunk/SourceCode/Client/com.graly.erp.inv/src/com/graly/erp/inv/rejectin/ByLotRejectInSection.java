package com.graly.erp.inv.rejectin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.barcode.LotDialog;
import com.graly.erp.inv.barcode.LotMasterSection;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.MovementIn.InType;
import com.graly.erp.inv.otherin.ByLotInSection;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.dialog.InClosableTitleAreaDialog;
import com.graly.framework.base.entitymanager.views.TableListManager;
import com.graly.framework.base.entitymanager.views.TableViewerManager;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.model.Lot;
/**
 * @author Administrator
 */
public class ByLotRejectInSection extends ByLotInSection {
	private static final Logger logger = Logger.getLogger(ByLotRejectInSection.class);
	protected ToolItem fromOutLot;
	
	public ByLotRejectInSection(ADTable adTable, LotDialog parentDialog,
			InType inType) {
		super(adTable, parentDialog, inType);
	}
	
	@Override
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemFromOutLot(tBar);
		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemDelete(tBar);
		setEnabled(true);
		section.setTextClient(tBar);
	}
	
	protected void createToolItemFromOutLot(ToolBar tBar) {
		fromOutLot = new ToolItem(tBar, SWT.PUSH);
		fromOutLot.setText("查询已出库");
		fromOutLot.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_TOOL_REDO));
		fromOutLot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				fromOutLotAdapter();
			}
		});
	}

	protected void fromOutLotAdapter() {
		OutLotQueryDialog olqd = new OutLotQueryDialog(UI.getActiveShell());
		if(olqd.open()==Window.OK){
			List<String> lotIds = olqd.getLotIds();
			for(String lotId : lotIds){
				addLot(lotId);
			}
		}
	}

	@Override
	protected void addLot(){
		String lotId = txtLotId.getText();
		addLot(lotId);
	}
	protected void addLot(String lotId) {
		try {
			if(lotId != null && !"".equals(lotId)) {				
				INVManager invManager = Framework.getService(INVManager.class);
				lot = invManager.getLotByLotId(Env.getOrgRrn(), lotId);
				if(lot == null || lot.getMaterialRrn() == null) {
					// 如果退货入库中有位Material类型的物料,默认其对应的批次已经在Lot表中存在
					//(因为只有先入库才能出库，然后再退货),所以如果lot为null,即使为Material类型,
					//表示其还未入库或还未审核，也会提示该批次不存在
					txtLotId.setForeground(SWTResourceCache.getColor("Red"));
					UI.showError(Message.getString("inv.lotnotexist"));
					return;
				}
				else if(validLot(lot)) {
					// 如果l不为null，表示lot所对应的物料在lines中或与inLine对应的物料一致
					MovementLine l = this.isContainsLot(lot);
					if(l == null) {
						return;
					}
					// Batch或Material类型需要设置调拨数量
					MovementLineLot lineLot = null;
					if(Lot.LOTTYPE_BATCH.equals(lot.getLotType())
							|| Lot.LOTTYPE_MATERIAL.equals(lot.getLotType())) {
						RejectInQtySetupDialog resQtyDialog = new RejectInQtySetupDialog(UI.getActiveShell(),
								null, lot, null);
						int openId = resQtyDialog.open();
						if(openId == Dialog.OK) {
							lineLot = pareseMovementLineLot(l, resQtyDialog.getInputQty(), lot);
						} else if(openId == Dialog.CANCEL) {
							return;
						}
					} else if(Lot.LOTTYPE_SERIAL.equals(lot.getLotType())) {
						// 此时Lot.QtyCurrent可能为零(已使用或已出库) 所以传入的值应为BigDecimal.ONE
						lineLot = pareseMovementLineLot(l, BigDecimal.ONE, lot);
					}
					addLineLotToTable(lineLot);
				}
			}
		} catch(Exception e) {
			txtLotId.setForeground(SWTResourceCache.getColor("Red"));
			logger.error("Error at LotMasterSection ：addLot() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		} finally {
			txtLotId.selectAll();
		}
	}

	@Override
	protected boolean validLot(Lot lot) {
		if(isContainsInLineLots(lot)) {
			UI.showError(String.format(Message.getString("wip.lot_list_contains_lot"), lot.getLotId()));
			return false;
		}
		if (Lot.LOTTYPE_SERIAL.equals(lot.getLotType()) && !Lot.POSITION_OUT.equals(lot.getPosition())) {
			UI.showError(String.format(Message.getString("inv.lot_not_out"), lot.getLotId()));
			return false;
		} else {
			if (lot.getIsUsed()) {
				UI.showError(String.format(Message.getString("wip.lot_is_used"), lot.getLotId()));
				return false;
			}
		}
		return true;
	}
}

class OutLotQuerySection{
	private static final Logger logger = Logger.getLogger(OutLotQuerySection.class);
	public static final String ORDERBY_LOT_ID = " lotId ";
	protected IManagedForm form;
	protected Section section;
	protected IFormPart spart;
	protected Text txtMaterialId;
	
	protected TableViewerManager lotManager;
	protected TableViewer viewer;
	protected CheckboxTableViewer checkViewer;
	
	protected ADTable adTable;
	protected List<Lot> lots;
	protected String whereClause = " 1 != 1 ";
	protected Lot lot = null;
	private List<String> lotIds;
	
	public OutLotQuerySection() {
	}
	
	public OutLotQuerySection(ADTable adTable) {
		super();
		this.adTable = adTable;
	}

	public void createContents(IManagedForm form, Composite parent){
		createContents(form, parent, Section.DESCRIPTION | Section.TITLE_BAR);
	}
	
	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		this.form = form;
		final FormToolkit toolkit = form.getToolkit();
		
		section = toolkit.createSection(parent, sectionStyle);
		setSectionTitle();
		section.marginWidth = 3;
	    section.marginHeight = 4;
	    toolkit.createCompositeSeparator(section);

//	    createToolBar(section);
		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		section.setLayout(layout);
		section.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Composite client = toolkit.createComposite(section);    
	    GridLayout gridLayout = new GridLayout();    
	    layout.numColumns = 1;    
	    client.setLayout(gridLayout);
	    
	    spart = new SectionPart(section);    
	    form.addPart(spart);
	    
	    createLotInfoComposite(client, toolkit);
	    createTableContent(client, toolkit);
	    section.setClient(client);
	}
	
	protected void createLotInfoComposite(Composite client, FormToolkit toolkit) {
		Composite comp = toolkit.createComposite(client, SWT.BORDER);
		comp.setLayout(new GridLayout(2, false));
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label label = toolkit.createLabel(comp, Message.getString("pdm.material_id"));
		label.setForeground(SWTResourceCache.getColor("Folder"));
		label.setFont(SWTResourceCache.getFont("Verdana"));
		txtMaterialId = toolkit.createText(comp, "", SWT.BORDER);
		txtMaterialId.setTextLimit(48);
		GridData gd = new GridData();//GridData.FILL_HORIZONTAL
		gd.heightHint = 13;
		gd.widthHint = 340;
		txtMaterialId.setLayoutData(gd);
		txtMaterialId.addKeyListener(getKeyListener());
		txtMaterialId.setFocus();
	}
	
	protected KeyListener getKeyListener() {
		try {
			return new KeyAdapter() {
				public void keyPressed(KeyEvent e) {
					txtMaterialId.setForeground(SWTResourceCache.getColor("Black"));
					switch (e.keyCode) {
					case SWT.CR :
						addLot();
						break;
					case SWT.TRAVERSE_RETURN :
						addLot();
						break;
					}
				}
			};
		} catch(Exception e) {
			logger.error("Error at ByLotRejectInSection.OutLotQuerySection ：getKeyListener() ", e);
		}
		return null;
	}
	
	protected void addLot() {
		String materialId = txtMaterialId.getText();
		try {
			if(materialId != null && !"".equals(materialId)) {
				INVManager invManager = Framework.getService(INVManager.class);
				lotIds = invManager.getOutedLotsByMaterialId(materialId, Env.getOrgRrn());
				for(String lotId : lotIds){
					lot = new Lot();
					lot.setLotId(lotId);
					getLots().add(lot);					
				}
				if(lotIds==null || lotIds.size()==0) {
					txtMaterialId.setForeground(SWTResourceCache.getColor("Red"));
					UI.showError(Message.getString("inv.lotnotexist"));
					return;
				}
				refresh();
			}
		} catch(Exception e) {
			txtMaterialId.setForeground(SWTResourceCache.getColor("Red"));
			logger.error("Error at ByLotRejectInSection.OutLotQuerySection ：addLot() ", e);
			ExceptionHandlerManager.asyncHandleException(e);
		} finally {
			txtMaterialId.selectAll();
		}
	}
	
	protected void setSectionTitle() {
		section.setText("已出库批次");
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		new ToolItem(tBar, SWT.SEPARATOR);
		section.setTextClient(tBar);
	}
	
	protected void createTableContent(Composite client, FormToolkit toolkit) {
		createTableViewer(client, toolkit);
		if(viewer instanceof CheckboxTableViewer) {
			checkViewer = (CheckboxTableViewer)viewer;
		}
		initTableContent();
	}
	
	protected void createTableViewer(Composite client, FormToolkit toolkit) {
		lotManager = new TableListManager(adTable, SWT.CHECK);
		viewer = (TableViewer)lotManager.createViewer(client, toolkit);
		viewer.getTable().addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem ti = (TableItem) e.item;
			}
		});
		lotManager.updateView(viewer);
	}
	
	/* 根据whereClause得到需要的Lot列表,
	 * 子类可重载getWhereClause()得到需要的Lots
	 */
	protected void initTableContent() {
		List<ADBase> list = null;
		try {
        	ADManager manager = Framework.getService(ADManager.class);
            list = manager.getEntityList(Env.getOrgRrn(), adTable.getObjectRrn(), 
            		Env.getMaxResult(), getWhereClause(), getOrderByClause());
            List<Lot> l = new ArrayList<Lot>();
            for(ADBase ab : list) {
            	Lot lot = (Lot)ab;
            	l.add(lot);
            }
            setLots(l);
            refresh();
        } catch (Exception e) {
        	ExceptionHandlerManager.asyncHandleException(e);
        	logger.error(e.getMessage(), e);
        }
	}
	
	public void refresh() {
		if(lotManager != null && viewer != null) {
			lotManager.setInput(getInput());
			lotManager.updateView(viewer);
			createSectionDesc(section);
		}
	}
	
	protected void createSectionDesc(Section section){
		String text = Message.getString("common.totalshow");
		int count = viewer.getTable().getItemCount();
		if (count > Env.getMaxResult()) {
			text = String.format(text, String.valueOf(count), String.valueOf(Env.getMaxResult()));
		} else {
			text = String.format(text, String.valueOf(count), String.valueOf(count));
		}
		section.setDescription("  " + text);
	}
	
	protected List<?> getInput() {
		return getLots();
	}

	protected List<Lot> getLots() {
		if(lots == null) {
			lots = new ArrayList<Lot>();
			return lots;
		}
		return lots;
	}
	
	protected String getOrderByClause() {
		return ORDERBY_LOT_ID;
	}

	public void setLots(List<Lot> lots) {
		this.lots = lots;
	}

	public String getWhereClause() {
		return whereClause;
	}

	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}
	
	public List<String> getLotIds(){
		List<String> ids = new ArrayList<String>();
		Object[] objs = checkViewer.getCheckedElements();
		for(Object obj : objs){
			if(obj instanceof Lot){
				Lot lt = (Lot)obj;
				ids.add(lt.getLotId());
			}
		}
		return ids;
	}
}

class OutLotQueryDialog extends InClosableTitleAreaDialog{
	private static final Logger logger = Logger.getLogger(OutLotQueryDialog.class);
	private static int MIN_DIALOG_WIDTH = 600;
	private static int MIN_DIALOG_HEIGHT = 450;
	protected ADTable table;
	protected ManagedForm managedForm;
	protected OutLotQuerySection section;
	private String tableName = "INVLot";
	private List<String> lotIds;

	protected OutLotQueryDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected Control createContents(Composite parent) {
		return super.createContents(parent);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
        setTitleImage(SWTResourceCache.getImage("entity-dialog"));
        setTitleMessage();
        Composite composite = (Composite) super.createDialogArea(parent);
		FormToolkit toolkit = new FormToolkit(getShell().getDisplay());
		ScrolledForm sForm = toolkit.createScrolledForm(composite);
		sForm.setLayoutData(new GridData(GridData.FILL_BOTH));
		managedForm = new ManagedForm(toolkit, sForm);
		Composite body = sForm.getForm().getBody();
		configureBody(body);
		
        // 创建LotSection
		createSection(body);
        return composite;
    }
	
	protected void setTitleMessage() {
		setTitle("已出库批次查询");
	}
	
	protected void configureBody(Composite body) {
		GridLayout layout = new GridLayout(1, false);
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
	
	protected void createSection(Composite composite) {
		section = new OutLotQuerySection(getADTableOfInvLot());
		section.createContents(managedForm, composite);
	}
	
	@Override
    protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID,
				Message.getString("common.ok"), false);
    	createButton(parent, IDialogConstants.CANCEL_ID,
    			Message.getString("common.exit"), false);
    }
	
	protected ADTable getADTableOfInvLot() {
		try {
			if(table == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				table = entityManager.getADTable(0L, getADTableName());
			}
			return table;
		} catch(Exception e) {
			logger.error("RequisitionLineDialog : initAdTableOfBom()", e);
		}
		return null;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String getADTableName() {
		return this.tableName;
	}
	
	
	public List<String> getLotIds() {
		return lotIds;
	}

	public void setLotIds(List<String> lotIds) {
		this.lotIds = lotIds;
	}

	@Override
	protected void okPressed() {
		lotIds = section.getLotIds();
		super.okPressed();
	}
	
	@Override
	protected Point getInitialSize() {
		Point shellSize = super.getInitialSize();
		return new Point(Math.max(
				convertHorizontalDLUsToPixels(MIN_DIALOG_WIDTH), shellSize.x),
				Math.max(convertVerticalDLUsToPixels(MIN_DIALOG_HEIGHT),
						shellSize.y));
	}
}
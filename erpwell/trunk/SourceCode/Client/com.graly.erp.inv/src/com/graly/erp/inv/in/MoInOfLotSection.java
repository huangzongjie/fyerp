package com.graly.erp.inv.in;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.base.model.Constants;
import com.graly.erp.base.report.PreviewDialog;
import com.graly.erp.base.report.ReportUtil;
import com.graly.erp.inv.barcode.LotMasterSection;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.Movement;
import com.graly.erp.inv.model.MovementIn;
import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.forms.Form;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.Message;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.util.UI;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wip.model.Lot;
public class MoInOfLotSection extends LotMasterSection {
	private static final Logger logger = Logger.getLogger(MoInOfLotSection.class);
	protected ManufactureOrder mo;
	protected MovementIn moIn;
	protected Section section;
	protected IFormPart spart;
	protected CTabFolder tabs;
	private ToolItem itemApprove;
	private ToolItem itemPreview;
	protected List<Form> detailForms = new ArrayList<Form>();
	protected int mStyle = SWT.FULL_SELECTION | SWT.BORDER | SWT.CHECK;
	private static final String TABLE_NAME_WIPMOIN = "WIPMovementIn";
	protected List<Lot> selectedItems;
	protected MoInOfLotDialog dialog;
	private ADTable adTableMoIn;

	public MoInOfLotSection(ADTable adTable, ManufactureOrder mo, MovementIn moIn , MoInOfLotDialog dialog) {
		super(adTable);
		this.mo = mo;
		this.moIn = moIn;
		this.dialog  = dialog;
	}

	protected void createParentContent(Composite client, final FormToolkit toolkit) {
		final IMessageManager mmng = form.getMessageManager();
		setTabs(new CTabFolder(client, SWT.FLAT | SWT.TOP));
		getTabs().marginHeight = 5;
		getTabs().marginWidth = 5;
		toolkit.adapt(getTabs(), true, true);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
		getTabs().setLayoutData(gd);
		Color selectedColor = toolkit.getColors().getColor(FormColors.SEPARATOR);
		getTabs().setSelectionBackground(new Color[] { selectedColor, toolkit.getColors().getBackground() }, new int[] { 50 });
		toolkit.paintBordersFor(getTabs());

		adTableMoIn = getADTableOfAction();
		for (ADTab tab : adTableMoIn.getTabs()) {
			CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
			item.setText(I18nUtil.getI18nMessage(tab, "label"));
			WarehouseEntityForm itemForm = new WarehouseEntityForm(getTabs(), SWT.NONE, moIn, tab, mmng);
			detailForms.add(itemForm);
			item.setControl(itemForm);
		}

		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}

		if (moIn.getObjectRrn() == null || moIn.getDocStatus().equals("APPROVED")) {
			itemApprove.setEnabled(false);
		}
	}

	// 默认带出所有Lot, 因此验证lot是验证存在返回true，不存在则返回false
	protected boolean validLot(Lot lot) {
		if (getLots().contains(lot)) {
			return true;
		} else {
			return false;
		}
	}

	protected void createTableViewer(Composite client, FormToolkit toolkit) {
		lotManager = new LotCheckTableManager(adTable);
		lotManager.setStyle(mStyle);
		viewer = (TableViewer) lotManager.createViewer(client, toolkit);
	}

	public void createToolBar(Section section) {
		final ToolBar toolBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemApprove(toolBar);
		new ToolItem(toolBar, SWT.SEPARATOR);
		createToolItemSave(toolBar);
		createToolItemPreview(toolBar);
		section.setTextClient(toolBar);
		this.setStatusChanged();
	}

	protected void createToolItemApprove(ToolBar tBar) {
		itemApprove = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_WIN_APPROVED);
		itemApprove.setText(Message.getString("common.approve"));
		itemApprove.setImage(SWTResourceCache.getImage("approve"));
		itemApprove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				approveAdapter();
			}
		});
	}
	
	protected void createToolItemPreview(ToolBar tBar) {
		itemPreview = new ToolItem(tBar, SWT.PUSH);
		itemPreview.setText(Message.getString("common.print"));
		itemPreview.setImage(SWTResourceCache.getImage("print"));
		itemPreview.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				previewAdapter();
			}
		});
	}
	
	protected void previewAdapter() {
		try {
			form.getMessageManager().removeAllMessages();
			String report = "win_report.rptdesign";

			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put(ReportUtil.SERVLET_NAME_KEY, ReportUtil.VIEWER_FRAMESET);
			HashMap<String, String> userParams = new HashMap<String, String>();

			if(!(Movement.STATUS_APPROVED.equals(moIn.getDocStatus()) || Movement.STATUS_COMPLETED.equals(moIn.getDocStatus()))){
				UI.showWarning(Message.getString("common.is_not_approved")+","+Message.getString("common.can_not_print"));
				return;
			}
			if(moIn == null){
				UI.showWarning(Message.getString("common.choose_one_record"));
				return;
			}
			
			Long objectRrn = moIn.getObjectRrn();
			userParams.put("INV_OBJECT_RRN", String.valueOf(objectRrn));

			PreviewDialog dialog = new PreviewDialog(UI.getActiveShell(), report, params, userParams);
			dialog.open();
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			return;
		}
	}

	protected void approveAdapter() {
		try {
			boolean confirm = UI.showConfirm(Message.getString("common.approve_confirm"), Message.getString("common.title_confirm"));
			if(!confirm) return;
			ADManager adManager = Framework.getService(ADManager.class);
			moIn = (MovementIn)adManager.getEntity(moIn);
			INVManager invManager = Framework.getService(INVManager.class);
			// 不能将approveMovementIn()返回的最新的MoIn赋给onIn,因为这样在多个用户操作时可以多次审核
			invManager.approveMovementIn(moIn, MovementIn.InType.WIN, Env.getUserRrn());
			UI.showInfo(Message.getString("common.approve_successed"));
			this.refresh();
			this.setStatusChanged();
			this.itemApprove.setEnabled(false);
			this.itemSave.setEnabled(false);
			setIsSaved(true);
		} catch (Exception e) {
			e.printStackTrace();
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	protected void saveAdapter() {
		try {
			boolean saveFlag = true;
			for (Form detailForm : getDetailForms()) {
				if (!detailForm.saveToObject()) {
					saveFlag = false;
					return;
				}
			}
			if (saveFlag) {
				List<Lot> selectedLots = getSelectionList();
				if (selectedLots == null || selectedLots.size() == 0) {
					UI.showWarning(Message.getString("wip_moin_isnull"));
					return;
				}
				List<MovementLine> inLines = new ArrayList<MovementLine>();
				moIn = (MovementIn) getDetailForms().get(0).getObject();
				if(moIn.getObjectRrn() == null){
					moIn.setMoRrn(mo.getObjectRrn());
					moIn.setMoId(mo.getDocId());
					inLines = this.generateMovementLine(selectedLots);
				}else{
					ADManager adManager = Framework.getService(ADManager.class);
					moIn = (MovementIn)adManager.getEntity(moIn);
					inLines = moIn.getMovementLines();
				}
				List<MovementLineLot> lineLots = getMovementLineLot();
				List<MovementLineLot> newLineLots = new ArrayList<MovementLineLot>();
				BigDecimal total = BigDecimal.ZERO;
				BigDecimal current = null;
				for (Lot lot : selectedLots) {
					current = lot.getQtyCurrent() == null ? BigDecimal.ZERO : lot.getQtyCurrent();
					total = total.add(current);
					MovementLineLot inLineLot = getReferenceLineLot(lineLots, lot);
					if(inLineLot == null) {
						Date now = Env.getSysDate();
						inLineLot = new MovementLineLot();
						inLineLot.setOrgRrn(Env.getOrgRrn());
						inLineLot.setIsActive(true);
						inLineLot.setCreated(now);
						inLineLot.setCreatedBy(Env.getUserRrn());
						inLineLot.setUpdated(now);
						inLineLot.setUpdatedBy(Env.getUserRrn());
						
						inLineLot.setMovementRrn(moIn.getObjectRrn());
						inLineLot.setMovementId(moIn.getDocId());
						MovementLine line = inLines.get(0);
						if(line != null && line.getObjectRrn() != null) {
							inLineLot.setMovementLineRrn(inLines.get(0).getObjectRrn());
						}
						inLineLot.setLotRrn(lot.getObjectRrn());
						inLineLot.setLotId(lot.getLotId());
						inLineLot.setMaterialRrn(lot.getMaterialRrn());
						inLineLot.setMaterialId(lot.getMaterialId());
						inLineLot.setMaterialName(lot.getMaterialName());
						inLineLot.setQtyMovement(lot.getQtyCurrent());
					}
					newLineLots.add(inLineLot);
				}
				inLines.get(0).setQtyMovement(total);
				inLines.get(0).setMovementLots(newLineLots);
				//保存LocatorRrn
				inLines.get(0).setLocatorRrn(dialog.getLocationRrn());		
				INVManager invManager = Framework.getService(INVManager.class);
				moIn = invManager.saveMovementInLine(moIn, inLines, MovementIn.InType.WIN, Env.getUserRrn());
				UI.showInfo(Message.getString("common.save_successed"));
				this.refresh();
				this.setStatusChanged();
				this.setIsSaved(true);
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at MoInOfLotSection : buttonPressed()", e);
			return;
		}
	}
	
	private MovementLineLot getReferenceLineLot(List<MovementLineLot> lineLots, Lot lot) {
		MovementLineLot lineLot = null;
		for(MovementLineLot ll : lineLots) {
			if(lot.getObjectRrn().equals(ll.getLotRrn()))
				return ll;
		}
		return lineLot;
	}
	
	protected List<MovementLineLot> getMovementLineLot() throws Exception{
		List<MovementLineLot> lineLots = new ArrayList<MovementLineLot>();
		if(moIn == null || moIn.getObjectRrn() == null)
			return lineLots;
		String whereClause = " movementRrn = '" + moIn.getObjectRrn() + "' ";
		ADManager adManager = Framework.getService(ADManager.class);
		lineLots = adManager.getEntityList(Env.getOrgRrn(), MovementLineLot.class,
				Integer.MAX_VALUE, whereClause, null);
		return lineLots;
	}
	
	public void refresh() {
		for(Form detailForm : getDetailForms()) {
			detailForm.setObject(moIn);
			detailForm.loadFromObject();
		}
		super.refresh();
	}
	
	private List<MovementLine> generateMovementLine(List<Lot> selectedLots) {
		List<MovementLine> list = new ArrayList<MovementLine>();
		MovementLine inLine = new MovementLine();
		inLine.setOrgRrn(Env.getOrgRrn());
		inLine.setMaterialRrn(mo.getMaterialRrn());
		inLine.setUomId(mo.getMaterial().getInventoryUom());
		inLine.setQtyMovement(BigDecimal.ZERO);
		list.add(inLine);
		return list;
	}

	protected void initTableContent() {
		try {
			if(mo != null && mo.getObjectRrn() != null) {
				WipManager wipManager = Framework.getService(WipManager.class);
				List<Lot> lotList = wipManager.getAvailableLot4In(mo.getObjectRrn());
				List<Lot> lots = new ArrayList<Lot>();
				if (moIn.getObjectRrn() != null) {
					String where = " inRrn = '" + moIn.getObjectRrn() + "' ";
					ADManager adManager = Framework.getService(ADManager.class);
					lots = adManager.getEntityList(Env.getOrgRrn(), Lot.class,
							Integer.MAX_VALUE, where, getOrderByClause());
					for (Lot lot : lots) {
						lotList.add(lot);
					}
				}
				Collections.sort(lotList, new LotIdComparator());
				// 如果入库单已审核，则只显示lots，否则显示lotList(包含所有lots)
				if(MovementIn.STATUS_APPROVED.equals(moIn.getDocStatus())) {
					this.setLots(lots);
				} else {
					setLots(lotList); 				
				}
				refresh();
				for (Lot lot : lotList) {
					if (lot.getInRrn() != null && (lot.getInRrn()).equals(moIn.getObjectRrn())) {
						checkViewer.setChecked(lot, true);
					}
				}
				lotManager.updateView(checkViewer);				
			}
		} catch (Exception e) {
			e.printStackTrace();
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}

	protected ADTable getADTableOfAction() {
		try {
			if (adTableMoIn == null) {
				ADManager entityManager = Framework.getService(ADManager.class);
				adTableMoIn = entityManager.getADTable(0L, TABLE_NAME_WIPMOIN);
				adTableMoIn = entityManager.getADTableDeep(adTableMoIn.getObjectRrn());
			}
			return adTableMoIn;
		} catch (Exception e) {
			logger.error("MoInOfLotDialog : getADTableOfAction()", e);
		}
		return null;
	}

	public List<Lot> getSelectionList() {
		Object[] os = checkViewer.getCheckedElements();
		if (os.length != 0) {
			selectedItems = new ArrayList<Lot>();
			for (Object o : os) {
				Lot lot = (Lot) o;
				selectedItems.add(lot);
			}
		}
		return this.selectedItems;
	}

	protected void setStatusChanged() {
		if (moIn != null) {
			String status = moIn.getDocStatus();
			if (MovementIn.STATUS_DRAFTED.equals(status)) {
				itemApprove.setEnabled(true);
				itemSave.setEnabled(true);
			} else {
				itemApprove.setEnabled(false);
				itemSave.setEnabled(false);
			}
		} else {
			itemApprove.setEnabled(false);
			itemSave.setEnabled(false);
		}
	}

	public CTabFolder getTabs() {
		return tabs;
	}

	public void setTabs(CTabFolder tabs) {
		this.tabs = tabs;
	}

	public List<Form> getDetailForms() {
		return detailForms;
	}

	public void setDetailForms(List<Form> detailForms) {
		this.detailForms = detailForms;
	}
	
	class LotIdComparator implements Comparator<Lot> {
		public int compare(Lot obj1, Lot obj2) {
			if(obj1 != null && obj2 != null) {
				if(obj1.getLotId() != null && obj2.getLotId() != null);
				return obj1.getLotId().compareTo(obj2.getLotId());
			}
			return 0;
		}
	}
}

package com.graly.erp.wip.disassemblelot;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.AuthorityToolItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.graly.erp.base.model.Constants;
import com.graly.erp.inv.client.INVManager;
import com.graly.erp.inv.model.LotStorage;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADBase;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.EntitySection;
import com.graly.framework.base.entitymanager.views.TableListManager;
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
import com.graly.mes.wip.model.LotComponent;

public class DisAssembleLotSection extends EntitySection{
	private static final Logger logger = Logger.getLogger(DisAssembleLotSection.class);
	
	protected LotComponentListManager tableManager;
	protected StructuredViewer viewer;
	protected Section section;
	protected IFormPart spart;
	protected ToolItem itemRefresh;
	private ToolItem itemDisAssembleLot;
	protected Lot lot;
	protected List<LotComponent> lots = new ArrayList<LotComponent>();
	
	public DisAssembleLotSection(ADTable table) {
		super(table);
		this.table = table;
		this.tableManager = new LotComponentListManager(table);
	}
	
	public void createContents(IManagedForm form, Composite parent) {
		createContents(form, parent, Section.DESCRIPTION | Section.TITLE_BAR);
	}
	
	public void createContents(final IManagedForm form, Composite parent, int sectionStyle) {
		super.form = form;
		final FormToolkit toolkit = form.getToolkit();
		section = toolkit.createSection(parent, Section.TITLE_BAR);
		section.setText(String.format(Message.getString("common.detail"),
				I18nUtil.getI18nMessage(getTable(), "label")));
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
		td.grabVertical = false;
		section.setLayoutData(td);

		Composite client = toolkit.createComposite(section);	 

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		client.setLayout(gridLayout);

		createSectionTitle(client);
		createSectionContent(client);
		createTableViewer(client,toolkit);
		
		toolkit.paintBordersFor(section);
		section.setClient(client);
		
		refresh();
	}
	
	protected void createTableViewer(Composite client, FormToolkit toolkit) {
		viewer = tableManager.createViewer(client, toolkit);
	}
	
	protected void createSectionTitle(Composite client) {
		final FormToolkit toolkit = form.getToolkit();
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = SWT.TOP;
		Composite top = toolkit.createComposite(client);
		top.setLayout(new GridLayout(3, false));
		top.setLayoutData(gd);
		Label label = toolkit.createLabel(top, Message.getString("inv.lotid"));
		label.setForeground(SWTResourceCache.getColor("Folder"));
		Text text = toolkit.createText(top, "", SWT.BORDER);
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
				Text tLotId = ((Text) event.widget);
				tLotId.setForeground(SWTResourceCache.getColor("Black"));
				switch (event.keyCode) {
				case SWT.CR:
					String lotId = tLotId.getText();
					lot = searchLot(lotId);
					tLotId.selectAll();
					if (lot == null || lot.getObjectRrn() == null) {
						tLotId.setForeground(SWTResourceCache.getColor("Red"));
						lot = new Lot();
						lot.setOrgRrn(Env.getOrgRrn());
					}	
					if(lot != null && Env.getOrgRrn() == 70000000L ){
						setLotCurrent(lot);
					}
					setAdObject(lot);
					refresh();
					
					lots = getAssembleLot(lot) == null ? new ArrayList<LotComponent>() : getAssembleLot(lot);
					viewer.setInput(lots);
					tableManager.updateView(viewer);
					break;
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
	
	public Lot searchLot(String lotId) {
		try {
			INVManager invManager = Framework.getService(INVManager.class);
			return invManager.getLotByLotId(Env.getOrgRrn(), lotId);
		} catch (Exception e) {
			logger.error("SearchByLotSection searchLot(): Lot isn' t exsited!");
			ExceptionHandlerManager.asyncHandleException(e);
			return null;
		}
	}
	public void setLotCurrent(Lot lot) {
		try {
			INVManager invManager = Framework.getService(INVManager.class);
			LotStorage lotStorage = invManager.getLotStorage(lot.getOrgRrn(), lot.getObjectRrn(), 9L, 1);
			lot.setQtyCurrent(lotStorage.getQtyOnhand());
		} catch (Exception e) {
			logger.error("SearchByLotSection searchLot(): Lot isn' t exsited!");
			ExceptionHandlerManager.asyncHandleException(e);
		}
	}
	
	public List<LotComponent> getAssembleLot(Lot lot) {
		try {
			if(lot == null || lot.getObjectRrn() == null){
				return null;
			}
			WipManager wipManager = Framework.getService(WipManager.class);
			return wipManager.getLotComponent(lot.getObjectRrn());
		} catch (Exception e) {
			logger.error("SearchByLotSection searchLot(): Lot isn' t exsited!");
			ExceptionHandlerManager.asyncHandleException(e);
			return null;
		}
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
		createToolItemDisAssembleLot(tBar);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}

	private void createToolItemDisAssembleLot(ToolBar tBar) {
		itemDisAssembleLot = new AuthorityToolItem(tBar, SWT.PUSH, Constants.KEY_DISASSEMBLELOT_DISASSEMBLE);
		itemDisAssembleLot.setText(Message.getString("wip.disassemblelot"));
		itemDisAssembleLot.setImage(SWTResourceCache.getImage("split"));
		itemDisAssembleLot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				disAssembleLotAdapter();
			}
		});
	}
	
	protected void refreshAdapter() {
		viewer.setInput(new ArrayList<Lot>());
	}
	
	private void disAssembleLotAdapter() {
		try {
			if(lot == null || lot.getObjectRrn() == null){
				return;
			}
			if(lot.getIsUsed()){
				UI.showError(String.format(Message.getString("wip.lot_is_used"),lot.getLotId()));
				return;
			}
			if(!Lot.POSITION_INSTOCK.equals(lot.getPosition()) && !Lot.POSITION_WIP.equals(lot.getPosition())){
				UI.showError(String.format(Message.getString("wip.lot_not_in_wip_or_stock"),lot.getLotId()));
				return;
			}
			WipManager wipManager = Framework.getService(WipManager.class);
			wipManager.disassembleLot(lot.getObjectRrn(), Env.getUserRrn());
			UI.showInfo(Message.getString("wip.disassemblelot_successful"));
			refreshAdapter();
		} catch (Exception e1) {
			ExceptionHandlerManager.asyncHandleException(e1);
			return;
		}
	}
}

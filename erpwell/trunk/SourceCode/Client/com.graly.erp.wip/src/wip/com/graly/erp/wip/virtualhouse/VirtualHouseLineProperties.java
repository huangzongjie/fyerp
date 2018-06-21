package com.graly.erp.wip.virtualhouse;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.model.MovementLine;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.erp.inv.model.MovementOut;
import com.graly.framework.activeentity.client.ADManager;
import com.graly.framework.activeentity.model.ADTab;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.entitymanager.forms.ChildEntityForm;
import com.graly.framework.base.entitymanager.forms.ChildEntityProperties;
import com.graly.framework.base.entitymanager.forms.EntityBlock;
import com.graly.framework.base.ui.util.Env;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.runtime.Framework;

public class VirtualHouseLineProperties extends ChildEntityProperties {
	private static final Logger logger = Logger.getLogger(VirtualHouseLineProperties.class);
	private String PREFIX = " movementRrn = '";
	protected boolean flag;
	public VirtualHouseLineProperties() {
		super();
    }
	
	public VirtualHouseLineProperties(EntityBlock masterParent, ADTable table, Object parentObject,boolean flag) {
		super(masterParent, table, parentObject);
		this.flag = flag;
	}

	// 重载实现创建MovementChildForm(使各个控件保存后为只读)
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
		getTabs().setSelectionBackground(
						new Color[] { selectedColor,
								toolkit.getColors().getBackground() },
						new int[] { 50 });
		toolkit.paintBordersFor(getTabs());

		for (ADTab tab : getTable().getTabs()) {
			CTabItem item = new CTabItem(getTabs(), SWT.BORDER);
			item.setText(I18nUtil.getI18nMessage(tab, "label"));
			ChildEntityForm itemForm = new ChildEntityForm(getTabs(), SWT.NONE, null, tab, mmng, parentObject);
			getDetailForms().add(itemForm);
			item.setControl(itemForm);
		}

		if (getTabs().getTabList().length > 0) {
			getTabs().setSelection(0);
		}
		if (parentObject != null) {
			loadFromParent();
		}
	}
	
	public void createToolBar(Section section) {
		ToolBar tBar = new ToolBar(section, SWT.FLAT | SWT.HORIZONTAL);
//		createToolItemNew(tBar);
//		createToolItemSave(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
//		createToolItemDelete(tBar);
		new ToolItem(tBar, SWT.SEPARATOR);
		createToolItemRefresh(tBar);
		section.setTextClient(tBar);
	}
	
	public void refresh() {
		super.refresh();
		VirtualHouseLineEntryBlock block = (VirtualHouseLineEntryBlock)getMasterParent();
//		block.setParenObjectStatusChanged();
	}

	  
	
	protected List<MovementLineLot> getLineLotList(MovementOut out, MovementLine line) throws Exception {
		List<MovementLineLot> linelots = null;
		if(out.getObjectRrn() == null || line.getObjectRrn() == null)
			return linelots;
		String whereClause = " movementRrn = " + out.getObjectRrn()
							+ " AND movementLineRrn = " + line.getObjectRrn() + " ";
    	ADManager manager = Framework.getService(ADManager.class);
    	linelots = manager.getEntityList(Env.getOrgRrn(), MovementLineLot.class,
        		Integer.MAX_VALUE, whereClause, null);
		return linelots;
	}
 
}

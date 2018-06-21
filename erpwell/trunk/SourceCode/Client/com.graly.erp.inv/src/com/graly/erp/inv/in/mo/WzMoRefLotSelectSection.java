package com.graly.erp.inv.in.mo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.graly.erp.inv.barcode.LotMasterSection;
import com.graly.erp.inv.in.LotCheckTableManager;
import com.graly.erp.wip.model.ManufactureOrder;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.runtime.Framework;
import com.graly.framework.runtime.exceptionhandler.ExceptionHandlerManager;
import com.graly.mes.wip.client.WipManager;
import com.graly.mes.wip.model.Lot;

public class WzMoRefLotSelectSection extends LotMasterSection {
	private static final Logger logger = Logger.getLogger(WzMoRefLotSelectSection.class);
	
	protected int mStyle = SWT.FULL_SELECTION | SWT.BORDER | SWT.CHECK;
	protected WzMoRefLotSelectPage parentSection;
	protected List<Lot> selectedItems;
	protected ManufactureOrder mo;

	public WzMoRefLotSelectSection(ADTable adTable, ManufactureOrder mo) {
		super(adTable);
		this.mo = mo;
	}
	
	protected void createLotInfoComposite(Composite client, FormToolkit toolkit) {
	}

	@Override
	public void createToolBar(Section section) {
	}

	protected void createTableViewer(Composite client, FormToolkit toolkit) {
		lotManager = new LotCheckTableManager(adTable);
		lotManager.setStyle(mStyle);
		viewer = (TableViewer) lotManager.createViewer(client, toolkit);
	}

	protected void initTableContent() {
		try {
			if(mo != null && mo.getObjectRrn() != null) {
				WipManager wipManager = Framework.getService(WipManager.class);
				List<Lot> lotList = wipManager.getAvailableLot4In(mo.getObjectRrn());
				
				Collections.sort(lotList, new LotIdComparator());
				this.setLots(lotList);
				refresh();
				lotManager.updateView(checkViewer);				
			}
		} catch (Exception e) {
			ExceptionHandlerManager.asyncHandleException(e);
			logger.error("Error at WzMoRefLotSelectSection : initTableContent() ", e);
		}
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

package com.graly.erp.inv.adjust.in;

import java.util.ArrayList;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;

import com.graly.erp.inv.barcode.LotTableManager;
import com.graly.erp.inv.model.MovementLineLot;
import com.graly.framework.activeentity.model.ADTable;
import com.graly.framework.base.ui.util.Message;
import com.graly.mes.wip.model.Lot;

public class AdjustInLotTableManager extends LotTableManager {
	public String LotId = "lotId";
	public String MaterialId = "materialId";
	public String MaterialName = "materialName";
	public String QtyMovement = "qtyMovement";

	public String Cloumn_LotId = Message.getString("inv.lotid");
	public String Cloumn_MaterialID = Message.getString("pdm.material_id");
	public String Cloumn_MaterialName = Message.getString("pdm.material_name");
	public String Cloumn_CurrentQty = Message.getString("wip.lot_qty");
	
	public AdjustInLotTableManager(ADTable adTable) {
		super(adTable, Lot.LOTTYPE_SERIAL);
	}
	
	protected void initDisplayList() {
		displayList = new ArrayList<String>();
		if(Lot.LOTTYPE_BATCH.equals(lotType)) {
			displayList.add(DisplayName_LotQty);
		}
	}
	
	@Override
    protected String[] getColumns() {
//		headerLabels = new LinkedHashMap<String, String>();
    	return new String[]{LotId, MaterialId, MaterialName, QtyMovement};
    }
    
    @Override
    protected String[] getColumnsHeader() {
    	return new String[]{Cloumn_LotId, Cloumn_MaterialID, Cloumn_MaterialName,
    			Cloumn_CurrentQty};
    }
    
    protected Integer[] getColumnSize() {
    	return new Integer[]{32, 32, 64, 32};
    }

    protected void setCellEditor(TableViewer tableViewer) {
    	int length = getColumns().length;
		cellEditor = new CellEditor[length];
		String[] properties = new String[length];
		for (int i = 0; i < length; i++) {
			String column = (String)tableViewer.getTable().getColumn(i).getData(COLUMN_ID);
			properties[i] = column;
			if(DisplayName_LotQty.equals(column) || DisplayName_LotId.equals(column)) {
				cellEditor[i] = new ModifyTextCellEditor(tableViewer, column);
			} else {
				cellEditor[i] = null;
			}
		}
		tableViewer.setColumnProperties(properties);
		tableViewer.setCellEditors(cellEditor);
		TextCellModifier tcm = new TextCellModifier(tableViewer, "");
		tableViewer.setCellModifier(tcm);
	}
    
    protected class TextCellModifier implements ICellModifier {
		private TableViewer tableViewer;
		private MovementLineLot linelot;

		public TextCellModifier(TableViewer tableViewer, String defValueInt) {
			this.tableViewer = tableViewer;
		}

		@Override
		public boolean canModify(Object element, String property) {
			if(isCanEdit()) {
				if(DisplayName_LotId.equals(property))
					return true;
				linelot = (MovementLineLot)element;
				if(DisplayName_LotQty.equals(property)
						&& linelot.getMaterial() != null
						&& Lot.LOTTYPE_BATCH.equals(linelot.getMaterial().getLotType()))
					return true;
			}
			return false;
		}

		@Override
		public Object getValue(Object element, String property) {
			linelot = (MovementLineLot)element;
			if(DisplayName_LotId.equals(property)) {
				return linelot.getLotId();
			}
			if(DisplayName_LotQty.equals(property)) {
				return linelot.getQtyMovement().toString();
			}
			return null;
		}

		@Override
		public void modify(Object element, String property, Object value) {
			tableViewer.refresh();
		}
	}
}

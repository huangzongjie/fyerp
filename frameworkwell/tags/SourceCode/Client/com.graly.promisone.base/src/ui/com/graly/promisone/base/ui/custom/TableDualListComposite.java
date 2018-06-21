package com.graly.promisone.base.ui.custom;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.jface.viewers.TableViewer;

import com.graly.promisone.activeentity.model.ADTable;
import com.graly.promisone.base.entitymanager.views.TableListManager;

public class TableDualListComposite<T extends Object> extends DualListComposite<T> {
	
	ADTable adTable;
	List<T> avaliableList;
	List<T> chosenList;
	public TableDualListComposite(Composite parent, int style, ADTable adTable, List<T> avaliableList, List<T> chosenList) {
		super(parent, style);
		this.adTable = adTable;
		this.avaliableList = avaliableList;
		this.chosenList = chosenList;
		createControl();
	}
	
	@Override
    protected final TableViewer createAvailableTableViewer(Composite parent) {
		TableListManager listManager = new TableListManager(adTable);
		TableViewer viewer = (TableViewer)listManager.createViewer(parent, new FormToolkit(parent.getDisplay())); 
		viewer.setInput(avaliableList);
		return viewer;
    }
	
	@Override
    protected final TableViewer createChosenTableViewer(Composite parent) {
		TableListManager listManager = new TableListManager(adTable);
		TableViewer viewer = (TableViewer)listManager.createViewer(parent, new FormToolkit(parent.getDisplay())); 
		viewer.setInput(chosenList);
		return viewer;
    }
	
	public void refresh(List<T> avaliableList, List<T> chosenList){
		this.avaliableList = avaliableList;
		this.chosenList = chosenList;
		this.availableViewer.getTable().clearAll();
		this.chosenViewer.getTable().clearAll();
		this.availableViewer.setInput(avaliableList);
		this.chosenViewer.setInput(chosenList);
		refreshTableViewers();
	}
}

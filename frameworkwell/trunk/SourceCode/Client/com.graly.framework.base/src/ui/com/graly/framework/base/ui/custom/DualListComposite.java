package com.graly.framework.base.ui.custom;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

import com.graly.framework.base.ui.views.ListItemAdapter;

public class DualListComposite<T extends Object> extends Composite {
	
    private Table availableTable;
    private Table chosenTable;
    
    private Label lblAvailable;
    private Label lblBlank;
    private Label lblChosen;
    protected TableViewer availableViewer;
    protected TableViewer chosenViewer;
    
    private Composite centerComposite;
    private Button btnAdd;
    private Button btnAddAll;
    private Button btnRemove;
    private Button btnRemoveAll;
    
    private final java.util.List<ListContentChangedListener<T>> chosenListChangedListeners = new ArrayList<ListContentChangedListener<T>>();
    private final java.util.List<ListContentChangedListener<T>> availableListChangedListeners = new ArrayList<ListContentChangedListener<T>>();
    
    public DualListComposite(Composite parent, int style) {
        super(parent, style);
        //createControl();
    }
    
    protected void createControl() {
        this.setLayout(new GridLayout(3,false));
        this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        
        //Available TableViewer
        availableViewer = createAvailableTableViewer(this);
        if (availableViewer == null){
        	availableViewer = new TableViewer(this);
        	availableTable = availableViewer.getTable();
            final GridData viewerGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
            viewerGridData.heightHint = 100;
            viewerGridData.widthHint = 200;
            availableTable.setLayoutData(viewerGridData);
        } else {
        	availableTable = availableViewer.getTable();
        }
        //availableViewer.setUseHashlookup(true);
        
        //Buttons between two TableViewers
        centerComposite = new Composite(this, SWT.NONE);
        final GridLayout buttonLayout = new GridLayout();
        buttonLayout.marginWidth = 2;
        buttonLayout.marginHeight = 2;
        centerComposite.setLayout(buttonLayout);
        centerComposite.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, true));
        
        //Add button
        btnAdd = new Button(centerComposite, SWT.PUSH);
        btnAdd.setText("&Add");
        btnAdd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        
        //Remove button
        btnRemove = new Button(centerComposite, SWT.PUSH);
        btnRemove.setText("&Remove");
        btnRemove.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        
        //Add All button
        btnAddAll = new Button(centerComposite, SWT.PUSH);
        btnAddAll.setText("A&dd All");
        btnAddAll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        
        //Remove All button
        btnRemoveAll = new Button(centerComposite, SWT.PUSH);
        btnRemoveAll.setText("Re&move All");
        btnRemoveAll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        
        //Chosen TableViewer
        chosenViewer = createChosenTableViewer(this);
        if (chosenViewer == null) {
            chosenViewer = new TableViewer(this);
            chosenTable = chosenViewer.getTable();
            final GridData viewerGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
            viewerGridData.heightHint = 100;
            viewerGridData.widthHint = 200;
            chosenTable.setLayoutData(viewerGridData);
        } else {
        	chosenTable = chosenViewer.getTable();
        }
        //chosenViewer.setUseHashlookup(true);
       
        addListeners();
    }
    
    protected TableViewer createAvailableTableViewer(Composite parent) {
        return null;
    }
    
    protected TableViewer createChosenTableViewer(Composite parent) {
        return null;
    }
    
    private void addListeners() {
        btnAdd.addSelectionListener(new SelectionAdapter() {
            @SuppressWarnings({ "unchecked" })
            @Override
            public void widgetSelected(SelectionEvent e) {
                final IStructuredSelection selection = (IStructuredSelection) availableViewer.getSelection();
                final Iterator it = selection.iterator();
                while (it.hasNext()) {
                    final Object obj = it.next();
                    ((List<T>)availableViewer.getInput()).remove((T) obj);
                    ((List<T>)chosenViewer.getInput()).add((T) obj);
                }
                refreshTableViewers();
                fireChosenListContentChangedEvent((List<T>)chosenViewer.getInput());
                fireAvailableListContentChangedEvent((List<T>)availableViewer.getInput());
            }
        });
        
        btnRemove.addSelectionListener(new SelectionAdapter() {
            @SuppressWarnings("unchecked")
            @Override
            public void widgetSelected(SelectionEvent e) {
                final IStructuredSelection selection = (IStructuredSelection) chosenViewer.getSelection();
                final Iterator it = selection.iterator();
                while (it.hasNext()) {
                    final Object obj = it.next();
                    ((List<T>)availableViewer.getInput()).add((T) obj);
                    ((List<T>)chosenViewer.getInput()).remove((T) obj);
                }
                refreshTableViewers();
                fireChosenListContentChangedEvent((List<T>)chosenViewer.getInput());
                fireAvailableListContentChangedEvent((List<T>)availableViewer.getInput());
            }
        });
        
        btnAddAll.addSelectionListener(new SelectionAdapter() {
            @SuppressWarnings("unchecked")
            @Override
            public void widgetSelected(SelectionEvent e) {
                final TableItem[] items = availableTable.getItems();
                for (int i = 0; i < items.length; i++) {
                    if (items[i] == null) continue;
                    final Object obj = items[i].getData();
                    ((List<T>)availableViewer.getInput()).remove((T) obj);
                    ((List<T>)chosenViewer.getInput()).add((T) obj);
                }
                refreshTableViewers();
                fireChosenListContentChangedEvent((List<T>)chosenViewer.getInput());
                fireAvailableListContentChangedEvent((List<T>)availableViewer.getInput());
            }
        });
        
        btnRemoveAll.addSelectionListener(new SelectionAdapter() {
            @SuppressWarnings("unchecked")
            @Override
            public void widgetSelected(SelectionEvent e) {
                final TableItem[] items = chosenTable.getItems();
                for (int i = 0; i < items.length; i++) {
                    if (items[i] == null) continue;
                    final Object obj = items[i].getData();
                    ((List<T>)availableViewer.getInput()).add((T) obj);
                    ((List<T>)chosenViewer.getInput()).remove((T) obj);
                }
                refreshTableViewers();
                fireChosenListContentChangedEvent((List<T>)chosenViewer.getInput());
                fireAvailableListContentChangedEvent((List<T>)availableViewer.getInput());
            }
        });
        
        availableTable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                //centerComposite.setEnabledOfChild(btnAdd, availableTable.getSelectionCount() > 0);
            }
        });
        
        availableTable.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                //centerComposite.setEnabledOfChild(btnAdd, availableTable.getSelectionCount() > 0);
                //centerComposite.setEnabledOfChild(btnAddAll, availableTable.getItemCount() > 0);
            }
        });

        chosenTable.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                //centerComposite.setEnabledOfChild(btnRemove, chosenTable.getSelectionCount() > 0);
            }
        });
        chosenTable.addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                //centerComposite.setEnabledOfChild(btnRemove, chosenTable.getSelectionCount() > 0);
                //centerComposite.setEnabledOfChild(btnRemoveAll, chosenTable.getItemCount() > 0);
            }
        });
    }
    
    public void refreshTableViewers(){
        BusyIndicator.showWhile(Display.getCurrent(),new Runnable() {
            public void run() {
                availableViewer.refresh();
                chosenViewer.refresh();
            }
        });
    }
    
    public void refreshChosenViewer() {
        BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
            public void run() {
                chosenViewer.refresh();
            }
        });
    }
    
    public void refreshAvailableViewer() {
        BusyIndicator.showWhile(Display.getCurrent(), new Runnable() {
            public void run() {
                availableViewer.refresh();
            }
        });
    }
    
    public void setViewerLabels(String availableLabel, String chosenLabel) {
        if (((availableLabel==null) || availableLabel.trim().equals("")) && 
                ((chosenLabel==null) || chosenLabel.trim().equals(""))) return;
        
        if (lblAvailable == null) {
            //create label widgets
            lblAvailable = new Label(this, SWT.NONE);
            lblAvailable.setText(availableLabel);
            lblAvailable.moveAbove(availableTable);
            lblBlank = new Label(this,SWT.NONE);
            lblBlank.moveBelow(lblAvailable);
            lblChosen = new Label(this, SWT.NONE);
            lblChosen.setText(chosenLabel);
            lblChosen.moveBelow(lblBlank);
        }
        lblAvailable.setText(availableLabel==null ? "" : availableLabel);
        lblChosen.setText(chosenLabel==null ? "" : chosenLabel);
    }
    
    public Table getAvailableTable() {
        return this.availableTable;
    }
    
    public Table getChosenTable() {
        return this.chosenTable;
    }
    
    public void addChosenListChangedSelectionListener(ListContentChangedListener<T> listener) {
        if (listener != null)
            chosenListChangedListeners.add(listener);
    }
    
    public void removeChosenListChangedSelectionListener(ListContentChangedListener<T> listener) {
        if (listener != null)
            chosenListChangedListeners.remove(listener);
    }
    
    public void addAvailableListChangedSelectionListener(ListContentChangedListener<T> listener) {
        if (listener != null)
            availableListChangedListeners.add(listener);
    }
    
    public void removeAvailableListChangedSelectionListener(ListContentChangedListener<T> listener) {
        if (listener != null)
            availableListChangedListeners.remove(listener);
    }

    private void fireChosenListContentChangedEvent(List<T> list) {
        final Iterator<ListContentChangedListener<T>> it = chosenListChangedListeners.iterator();
        while (it.hasNext()) {
            (it.next()).listContentChanged(list);
        }
    }
    
    private void fireAvailableListContentChangedEvent(List<T> list) {
        final Iterator<ListContentChangedListener<T>> it = availableListChangedListeners.iterator();
        while (it.hasNext()) {
            (it.next()).listContentChanged(list);
        }
    }
    
    public interface ListContentChangedListener<T extends Object> {
        /**
         * Method that is called every time the content of the <code>TableViewer</code> has changed.
         * @param contentProvider <code>IRemovableContentProvider</code>  of the <code>TableViewer</code>.  <code>getElements</code> can be called
         * on the contentProvider to retrieve the new items.
         */
        public void listContentChanged(List<T> list);
    }

    public static boolean isEqual(Object obj1, Object obj2) {
        if ((obj1 == null) ^ (obj2 == null)) return false;
        if ((obj1 == null) && (obj2 == null)) return true;
        return obj1.equals(obj2);
    }

}

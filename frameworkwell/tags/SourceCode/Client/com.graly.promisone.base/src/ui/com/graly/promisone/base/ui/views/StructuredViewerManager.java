package com.graly.promisone.base.ui.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.forms.widgets.FormToolkit;

public abstract class StructuredViewerManager {
	public static final String DATA_KEY_MANAGER = "manager";
	
	public ItemAdapterFactory adapterFactory;
	protected StructuredViewer viewer;
	protected ViewerContentProvider provider;
	protected ViewerComparator comparator;
	
	protected abstract StructuredViewer newViewer(Composite parent, FormToolkit toolkit);
	protected abstract StructuredViewer newViewer(Composite parent, FormToolkit toolkit, String[] columns, String[] columnsHeaders, int[] columnsSize);
	protected abstract ItemAdapterFactory createAdapterFactory();
	/**
     * This method should be overrides if columns are needed. string specified
     * are the columns id. Labels are displayed with getColumnsHeader()
     *
     * @return
     */
    protected String[] getColumns() {
        return null;
    }

    /**
     * When column are specify, provides the labels to display them.
     *
     * @return
     */
    protected String[] getColumnsHeader() {
        return null;
    }

    /**
     * Default column size are 150 pixel. Override this methode to change the
     * size of a column according to its index.
     *
     * @param columnIndex
     * @return
     */
//    protected int getColumnSize(int columnIndex) {
//        return 150;
//    }
    
    public StructuredViewer createViewer(Composite parent, FormToolkit toolkit) {
    	return createViewer(parent, toolkit, null, null, null);
    }
    
    public StructuredViewer createViewer(Composite parent, FormToolkit toolkit,String[] columns, String[] columnsHeaders, int[] columnsSize) {
    	if(columns != null && columnsHeaders!= null && columnsSize != null && 
    			columns.length == columnsHeaders.length && columnsHeaders.length== columnsSize.length){
    		viewer = newViewer(parent, toolkit,columns, columnsHeaders, columnsSize);
    	} else{
    		viewer = newViewer(parent, toolkit);
    	}
    	viewer.setData(DATA_KEY_MANAGER, this);
    	adapterFactory = createAdapterFactory();
    	provider = new ViewerContentProvider(this);
    	viewer.setLabelProvider(provider);
        viewer.setContentProvider(provider);
        
        setComparator(null);
        
    	return viewer;
    }
    
    public void setInput(Object input) {
        viewer.setInput(input);
    }
    
    public Object getInput() {
        return viewer.getInput();
    }
    
    public void refresh() {
        viewer.refresh();
    }
    
    public ViewerComparator createComparator(final String columnName) {
    	
        return new ViewerComparator() {

            @Override
            public int category(Object element) {
                return isContainer(element) ? 0 : 1;
            }

            @Override
            @SuppressWarnings("unchecked")
            public int compare(Viewer viewer, Object e1, Object e2) {
                int cat1 = category(e1);
                int cat2 = category(e2);

                if (cat1 != cat2) {
                    return cat1 - cat2;
                }

                String name1 = provider.getText(e1, columnName);
                String name2 = provider.getText(e2, columnName);

                if (name1 == null) {
                    name1 = "";
                }
                if (name2 == null) {
                    name2 = "";
                }
                // use the comparator to compare the strings
                return getComparator().compare(name1, name2);
            }
        };
    }

    public void setComparator(ViewerComparator sorter) {
        if (comparator != sorter) {
            comparator = sorter;
            viewer.setComparator(sorter);
        }
    }

    public ViewerComparator getComparator() {
        return comparator;
    }
    
    public boolean isContainer(Object item) {
        ItemAdapter adapter = adapterFactory.getAdapter(item.getClass());
        assert adapter != null;
        if (adapter == null) {
            return false;
        }
        return adapter.isContainer(item);
    }
    
    public void dispose() {
        if (viewer != null) {
            viewer = null;
        }
        if (provider != null) {
            provider.dispose();
            provider = null;
        }
        if (adapterFactory != null) {
            adapterFactory.dispose();
            adapterFactory = null;
        }
        comparator = null;
    }
}

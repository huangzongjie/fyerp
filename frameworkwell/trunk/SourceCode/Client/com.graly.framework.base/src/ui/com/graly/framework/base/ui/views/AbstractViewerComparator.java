package com.graly.framework.base.ui.views;

import java.util.Comparator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import java.util.Hashtable;

public abstract class AbstractViewerComparator extends ViewerComparator {
	public static final int ASCENDING = 0;

    public static final int DESCENDING = 1;
    
    public enum CompareType {CHARACTER, NUMBERIC};

    // the column id ; comparator will use this field to obtain
    // object that will be compared
    protected String sortingColumn;

    public int direction;

    protected Hashtable<String, Comparator<Object>> map;
    //比较类型，数值型(Double, BigDecimal, Long, Integer)、字符型
    protected CompareType compType = CompareType.CHARACTER;

    public AbstractViewerComparator(Hashtable<String, Comparator<Object>> map) {
        this.map = map;
    }

    public void doSort(String columnId) {
        if (sortingColumn != null && sortingColumn.equals(columnId)) {
            direction = 1 - direction;
        } else {
            sortingColumn = columnId;
            direction = ASCENDING;
        }
    }
    
    public void doSort(String columnId, CompareType compType) {
    	this.compType = compType;
    	this.doSort(columnId);
    }
    

    public void doSort(String columnId, int direction) {
        doSort(columnId);
        this.direction = direction;
    }

    protected abstract Object getObjectToCompare(Viewer viewer, Object o);

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        int rc = 0;
        if (sortingColumn == null) {
            return rc;
        }
        Object o1 = getObjectToCompare(viewer, e1);
        Object o2 = getObjectToCompare(viewer, e2);
        
        Comparator<Object> comparator = null;
        if (map != null){
        	comparator = map.get(sortingColumn);
        }
        if (comparator != null) {
            rc = comparator.compare(o1, o2);
        } else {
            rc = getComparator().compare(o1.toString(), o2.toString());
        }
        if (direction == DESCENDING) {
            rc = -rc;
        }
        return rc;
    }
}

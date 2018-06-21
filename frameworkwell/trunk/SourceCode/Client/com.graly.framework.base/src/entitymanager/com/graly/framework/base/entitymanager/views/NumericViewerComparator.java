package com.graly.framework.base.entitymanager.views;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.Viewer;

import com.graly.framework.base.ui.views.AbstractViewerComparator;

public class NumericViewerComparator extends AbstractViewerComparator {
	private static final Logger logger = Logger.getLogger(TableViewerManager.class);
	public static final String QTY_PREFIX = "qty";
	public static final String QTY_SUFFIX = "Qty";
	public static final String NUMERIC_DOUBLE = "double";
	public static final String NUMERIC_INTEGER = "integer";
	
	protected HashSet<String> sortCols;
	
	public NumericViewerComparator(Hashtable<String, Comparator<Object>> map) {
        super(map);
        sortCols = new LinkedHashSet<String>();
    }

    @Override
    protected Object getObjectToCompare(Viewer viewer, Object o) {
    	switch(this.compType) {
    	case NUMBERIC : 
    		try {
    			Object property = PropertyUtils.getSimpleProperty(o, sortingColumn);
    			return property;	            	
    		} catch(Exception e) {
    			logger.error("Error at NumericViewerComparator getObjectToCompare() : ", e);
    		}
    		break;
    	}
        return null;
    }
    
    protected boolean containsColumns(String col) {
    	if(sortCols != null && sortCols.contains(col))
    		return true;
    	return false;
    }
    
    public void putComparatorMap(String columnName) {
    	if(map == null) {
    		map = new Hashtable<String, Comparator<Object>>();
    	}
    	if(map.get(columnName) == null) {
    		map.put(columnName, new NumericComparator());
    		sortCols.add(columnName);
    	}
    }
}

package com.graly.erp.base.model;

import java.util.Comparator;

public class LineEndComparator implements Comparator {
	
	public int compare(Object obj1, Object obj2) {
		if (!(obj1 instanceof DocumentationLine) || !(obj2 instanceof DocumentationLine)) {
			return 0;
		}
		DocumentationLine line1 = (DocumentationLine)obj1;
		DocumentationLine line2 = (DocumentationLine)obj2;
		if (line1.getDateEnd() == null) {
			return -1;
		} 
		if (line2.getDateEnd() == null) {
			return 1;
		}
		return line1.getDateEnd().compareTo(line2.getDateEnd());
	}
}

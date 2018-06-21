package com.graly.framework.base.ui.views;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

public interface ItemAdapter {
	String getText(Object object);

    String getText(Object object, String id);

    ImageDescriptor getImageDescriptor(Object object);

    ImageDescriptor getImageDescriptor(Object object, String id);

    Font getFont(Object object);

    Color getColor(Object object);

    Object[] getChildren(Object object);
    
    Object[] getElements(Object object);
    
    boolean hasChildren(Object object);

    boolean isContainer(Object object);

    Object getParent(Object object);

    Font getFont(Object object, String id);

    Color getBackground(Object element, String id);

    Color getForeground(Object element, String id);
    
    Object getManager();
}

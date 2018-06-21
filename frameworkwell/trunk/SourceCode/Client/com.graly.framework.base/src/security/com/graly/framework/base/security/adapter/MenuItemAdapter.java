package com.graly.framework.base.security.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import com.graly.framework.base.application.Activator;
import com.graly.framework.base.ui.util.I18nUtil;
import com.graly.framework.base.ui.util.SWTResourceCache;
import com.graly.framework.base.ui.views.AbstractItemAdapter;
import com.graly.framework.security.model.ADAuthority;

public class MenuItemAdapter extends AbstractItemAdapter {
	
	private static final Logger logger = Logger.getLogger(MenuItemAdapter.class);
	List<ADAuthority> list;
	
	public MenuItemAdapter(){
	}
	
	@Override
	public Object[] getChildren(Object object) {
		ADAuthority parent = (ADAuthority)object;
		List<ADAuthority> childrenList = new ArrayList<ADAuthority>();
		for(ADAuthority menu : list){
			if (menu.getParentRrn() == null){
				continue;
			}
			if(menu.getParentRrn().equals(parent.getObjectRrn())){
				childrenList.add(menu);
			}
		}
		return childrenList.toArray();
	}
	
	@Override
	public Object[] getElements(Object object) {
		list = (List<ADAuthority>)object;
		List<ADAuthority> root = new ArrayList<ADAuthority>();
		for(ADAuthority menu : list){
			if(menu.getLevel() == 1){
				root.add(menu);
			}
		}
		return root.toArray();
	}
	
	@Override
	public Object getParent(Object element) {
		ADAuthority menu = (ADAuthority)element;
		if (menu.getParentRrn() == null) {
			return null;
		} else {
			for(ADAuthority parent : list){
				if(menu.getParentRrn().equals(parent.getObjectRrn()))
					return parent;
			}
		}
		return null;
	}
	
	@Override
	public boolean hasChildren(Object element) {
		ADAuthority menu = (ADAuthority)element;
		for(ADAuthority child : list){
			if (child.getParentRrn() == null){
				continue;
			} else if(child.getParentRrn().equals(menu.getObjectRrn()))
				return true;
		}
		return false;
	}
	
	@Override
	public String getText(Object element) {
		ADAuthority menu = (ADAuthority)element;
		String lable = (String)I18nUtil.getI18nMessage(menu, "label");
		
		return lable;
	}
	
	@Override
	public String getText(Object object, String id) {
		ADAuthority menu = (ADAuthority)object;
		return I18nUtil.getI18nMessage(menu, "label");
	}
	
	@Override
	public ImageDescriptor getImageDescriptor(Object object, String id) {
		ADAuthority ADAuthority = (ADAuthority)object;
		if(ADAuthority.AUTHORITY_TYPE_MENU.equals(ADAuthority.getAuthorityType())) {
			return Activator.getImageDescriptor("open-folder");
		} else if(ADAuthority.AUTHORITY_TYPE_FUNCTION.equals(ADAuthority.getAuthorityType())) {
			return Activator.getImageDescriptor("function");
		} else if(ADAuthority.AUTHORITY_TYPE_FEATURE.equals(ADAuthority.getAuthorityType())) {
			return Activator.getImageDescriptor("feature");
		}
		return null;
	}
	
	@Override
	public Color getForeground(Object element, String id) {
		ADAuthority parent = (ADAuthority)element;
		for(ADAuthority child : list) {
			if(child.getParentRrn() == null) {
				continue;
			}
			if(child.getParentRrn().equals(parent.getObjectRrn())) {
				try {
					return SWTResourceCache.getColor("Folder");
				} catch(Exception ex) {
					logger.error("Exception MenuItemAdapter, not found color Folder", ex);
				}
			}
		}
		try {
			return SWTResourceCache.getColor("Function");
		} catch(Exception ex) {
			logger.error("Exception MenuItemAdapter, not found color Function", ex);
		}
		return null;
	}
	
	public Color getColor(Object object) {
		return new Color(Display.getCurrent(), 0, 67, 255);
	}
	
	@Override
	public Font getFont(Object object, String id) {
		try {
			return SWTResourceCache.getFont("Verdana");
		} catch(Exception ex) {
			logger.error("Exception MenuItemAdapter, not found font Verdana", ex);
		}
		return null;
	}
}